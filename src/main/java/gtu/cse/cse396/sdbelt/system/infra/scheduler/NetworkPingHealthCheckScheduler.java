package gtu.cse.cse396.sdbelt.system.infra.scheduler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import gtu.cse.cse396.sdbelt.system.domain.model.SystemStatus;
import gtu.cse.cse396.sdbelt.system.domain.service.SystemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class NetworkPingHealthCheckScheduler {
    @Value("${system.server.host}")
    private String host;

    private int pingTimeout = 3000;

    private final SystemService systemService;

    @Scheduled(fixedDelay = 6000) // every 6 seconds
    public void checkServerHealth() {
        try {
            InetAddress address = InetAddress.getByName(host);
            boolean isReachable = address.isReachable(pingTimeout);

            if (isReachable) {
                log.info("Ping to {} successful - server is healthy", host);
                systemService.update(SystemStatus.ACTIVE);
            } else {
                log.warn("Ping to {} failed - server appears unreachable", host);
                systemService.update(SystemStatus.INACTIVE, 0.0, 0.0, 0.0);
            }

        } catch (UnknownHostException e) {
            log.error("Unknown host during ping to {}: {}", host, e.getMessage());
            systemService.update(SystemStatus.INACTIVE, 0.0, 0.0, 0.0);
        } catch (IOException e) {
            log.error("IO exception during ping to {}: {}", host, e.getMessage());
            systemService.update(SystemStatus.INACTIVE, 0.0, 0.0, 0.0);
        } catch (Exception e) {
            log.error("Unexpected exception during ping to {}: {}", host, e.getMessage());
            systemService.update(SystemStatus.INACTIVE, 0.0, 0.0, 0.0);
        }
    }
}