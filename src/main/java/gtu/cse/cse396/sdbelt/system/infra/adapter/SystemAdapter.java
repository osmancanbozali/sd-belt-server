package gtu.cse.cse396.sdbelt.system.infra.adapter;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import gtu.cse.cse396.sdbelt.system.domain.config.DefaultSystemConfig;
import gtu.cse.cse396.sdbelt.system.domain.model.BeltDirection;
import gtu.cse.cse396.sdbelt.system.domain.model.System;
import gtu.cse.cse396.sdbelt.system.domain.model.SystemStatus;
import gtu.cse.cse396.sdbelt.system.domain.service.SystemService;
import gtu.cse.cse396.sdbelt.system.infra.mapper.SystemMapper;
import gtu.cse.cse396.sdbelt.system.infra.repository.JpaSystemRepository;
import gtu.cse.cse396.sdbelt.ws.domain.model.RawMessage;
import gtu.cse.cse396.sdbelt.ws.domain.model.RestartSystemCommand;
import gtu.cse.cse396.sdbelt.ws.domain.model.StartSystemCommand;
import gtu.cse.cse396.sdbelt.ws.domain.model.StopSystemCommand;
import gtu.cse.cse396.sdbelt.ws.domain.service.WebSocketService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional
public class SystemAdapter implements SystemService {

    private final DefaultSystemConfig config;
    private final WebSocketService webSocketService;
    private final JpaSystemRepository jpaSystemRepository;

    @PostConstruct
    public void init() {
        try {
            get();
        } catch (Exception e) {
            System system = new System(config.getId(), config.getName(), config.getDescription(), LocalDateTime.now(),
                    LocalDateTime.now(), SystemStatus.INACTIVE, config.getAccuracy(), config.getSpeed(),
                    BeltDirection.FORWARD, 0, 0, 0);
            jpaSystemRepository.save(SystemMapper.toEntity(system));
        }
    }

    @Override
    public System get() {
        return SystemMapper.toDomain(jpaSystemRepository.findById(config.getId())
                .orElseThrow(() -> new RuntimeException("System not found")));
    }

    @Override
    public void start() {
        StartSystemCommand command = new StartSystemCommand();
        RawMessage message = RawMessage.ofCommand(command);
        webSocketService.send(message);
        System system = get();
        System updatedSystem = system.copyWith(SystemStatus.ACTIVE);
        jpaSystemRepository.save(SystemMapper.toEntity(updatedSystem));
    }

    @Override
    public void shutdown() {
        StartSystemCommand command = new StartSystemCommand();
        RawMessage message = RawMessage.ofCommand(command);
        webSocketService.send(message);
        System system = get();
        System updatedSystem = system.copyWith(SystemStatus.INACTIVE);
        jpaSystemRepository.save(SystemMapper.toEntity(updatedSystem));
    }

    @Override
    public void stop() {
        StopSystemCommand command = new StopSystemCommand();
        RawMessage message = RawMessage.ofCommand(command);
        webSocketService.send(message);
        System system = get();
        System updatedSystem = system.copyWith(SystemStatus.STOPPED);
        jpaSystemRepository.save(SystemMapper.toEntity(updatedSystem));
    }

    @Override
    public void restart() {
        RestartSystemCommand command = new RestartSystemCommand();
        RawMessage message = RawMessage.ofCommand(command);
        webSocketService.send(message);
    }

    @Override
    public void update(String name, String description, Integer speed, Integer accuracy,
            BeltDirection beltDirection, Integer cpuUsage, Integer cpuUtilization, Integer memoryUsage) {
        System system = get();
        System updatedSystem = new System(system.id(), name, description, system.createdAt(), system.runAt(),
                system.status(), accuracy, speed, beltDirection, cpuUsage, cpuUtilization, memoryUsage);
        jpaSystemRepository.save(SystemMapper.toEntity(updatedSystem));
    }
}
