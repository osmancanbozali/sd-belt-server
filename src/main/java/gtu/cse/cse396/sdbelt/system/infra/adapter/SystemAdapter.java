package gtu.cse.cse396.sdbelt.system.infra.adapter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Component;

import gtu.cse.cse396.sdbelt.system.domain.config.DefaultSystemConfig;
import gtu.cse.cse396.sdbelt.system.domain.model.BeltDirection;
import gtu.cse.cse396.sdbelt.system.domain.model.System;
import gtu.cse.cse396.sdbelt.system.domain.model.SystemLatestInfo;
import gtu.cse.cse396.sdbelt.system.domain.model.SystemStatus;
import gtu.cse.cse396.sdbelt.system.domain.service.SystemService;
import gtu.cse.cse396.sdbelt.system.infra.mapper.SystemMapper;
import gtu.cse.cse396.sdbelt.system.infra.model.SystemStatusEntity;
import gtu.cse.cse396.sdbelt.system.infra.repository.JpaSystemLogRepository;
import gtu.cse.cse396.sdbelt.system.infra.repository.JpaSystemRepository;
import gtu.cse.cse396.sdbelt.ws.domain.service.WebSocketService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SystemAdapter implements SystemService {

    private final DefaultSystemConfig config;
    private final WebSocketService webSocketService;
    private final JpaSystemRepository jpaSystemRepository;
    private final JpaSystemLogRepository jpaSystemLogRepository;
    private final SystemCommandSender systemCommandSender;

    @Override
    public void saveStatus(SystemLatestInfo info) {
        SystemStatusEntity entity = new SystemStatusEntity();
        Instant instant = Instant.parse(info.timestamp());
        // Or convert to LocalDateTime in UTC
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        // add 3 hours to match the system's timezone if needed
        LocalDateTime now = localDateTime.plusHours(3);
        entity.setId(null);
        entity.setTimestamp(now);
        entity.setLevel(info.level());
        entity.setMessage(info.message());
        jpaSystemLogRepository.save(entity);
    }

    @Override
    public List<SystemLatestInfo> getLogs() {
        List<SystemStatusEntity> entities = jpaSystemLogRepository.findAll();
        return entities.stream()
                .map(entity -> {
                    String year = String.format("%04d", entity.getTimestamp().getYear());
                    // Convert month to string in Turkish
                    String monthNum = String.format("%02d", entity.getTimestamp().getMonthValue());
                    String month = getMonthAsString(entity.getTimestamp().getMonthValue());
                    String day = String.format("%02d", entity.getTimestamp().getDayOfMonth());
                    String hour = String.format("%02d", entity.getTimestamp().getHour());
                    String minute = String.format("%02d", entity.getTimestamp().getMinute());
                    String second = String.format("%02d", entity.getTimestamp().getSecond());
                    String timestamp = String.format("%s %s %s %s:%s:%s", day, month, year, hour, minute, second);
                    return new SystemLatestInfo(timestamp, entity.getLevel(), entity.getMessage());
                })
                .toList();
    }

    private String getMonthAsString(int month) {
        switch (month) {
            case 1:
                return "Ocak";
            case 2:
                return "Şubat";
            case 3:
                return "Mart";
            case 4:
                return "Nisan";
            case 5:
                return "Mayıs";
            case 6:
                return "Haziran";
            case 7:
                return "Temmuz";
            case 8:
                return "Ağustos";
            case 9:
                return "Eylül";
            case 10:
                return "Ekim";
            case 11:
                return "Kasım";
            case 12:
                return "Aralık";
            default:
                throw new IllegalArgumentException("Invalid month: " + month);
        }
    }

    @PostConstruct
    public void init() {
        try {
            get();
        } catch (Exception e) {
            System system = new System(config.getId(), config.getName(), config.getDescription(), LocalDateTime.now(),
                    LocalDateTime.now(), SystemStatus.INACTIVE, 70.0, config.getSpeed(),
                    BeltDirection.FORWARD, 0.0, 0.0, 0.0, LocalDateTime.now());
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

        systemCommandSender.sendSpeedCommand(40);
        System system = get();
        System updatedSystem = system.copyWith(SystemStatus.ACTIVE);
        jpaSystemRepository.save(SystemMapper.toEntity(updatedSystem));
    }

    @Override
    public void shutdown() {
        /*
         * StartSystemCommand command = new StartSystemCommand();
         * RawMessage message = RawMessage.ofCommand(command);
         * webSocketService.send(message);
         */
        systemCommandSender.sendShutdownCommand();
        System system = get();
        System updatedSystem = system.copyWith(SystemStatus.INACTIVE);
        jpaSystemRepository.save(SystemMapper.toEntity(updatedSystem));
    }

    @Override
    public void stop() {
        /*
         * StopSystemCommand command = new StopSystemCommand();
         * RawMessage message = RawMessage.ofCommand(command);
         * webSocketService.send(message);
         */
        systemCommandSender.sendStopCommand();
        System system = get();
        System updatedSystem = system.copyWith(SystemStatus.STOPPED);
        jpaSystemRepository.save(SystemMapper.toEntity(updatedSystem));
    }

    @Override
    public void restart() {
        /*
         * RestartSystemCommand command = new RestartSystemCommand();
         * RawMessage message = RawMessage.ofCommand(command);
         * webSocketService.send(message);
         */
        systemCommandSender.sendRestartCommand();
    }

    @Override
    public void update(String name, String description, Integer speed, Double threshold,
            BeltDirection beltDirection) {
        System system = get();
        System updatedSystem = new System(system.id(), name, description, system.createdAt(), system.runAt(),
                system.status(), threshold, speed, beltDirection, system.cpuUsage(), system.cpuTemperature(),
                system.memoryUsage(), LocalDateTime.now());
        jpaSystemRepository.save(SystemMapper.toEntity(updatedSystem));
    }

    @Override
    public void updateInfo(SystemStatusInfo info) {
        java.lang.System.out.println("Updating system info: " + info);
        System system = get();
        Integer m1 = Integer.decode(info.memoryUsage().split("/")[0]);
        Integer m2 = Integer.decode(info.memoryUsage().split("/")[1].split(" ")[0]);
        log.info("Memory usage: {}/{}", m1, m2);
        double memoryUsage = (100.0 * m1) / m2;

        System updatedSystem = new System(system.id(), system.name(), system.description(), system.createdAt(),
                system.runAt(),
                system.status(), system.threshold(), system.speed(), system.beltDirection(), info.cpuUsage(),
                info.cpuDegree(), memoryUsage,
                LocalDateTime.now());
        jpaSystemRepository.save(SystemMapper.toEntity(updatedSystem));
    }

    public void reverse() {
        systemCommandSender.sendReverseCommand();
        System system = get();
        BeltDirection newDirection = system.beltDirection() == BeltDirection.FORWARD ? BeltDirection.REVERSE
                : BeltDirection.FORWARD;
        System updatedSystem = system.copyWith(newDirection);
        jpaSystemRepository.save(SystemMapper.toEntity(updatedSystem));
    }

    @Override
    public void updateThreshold(Double threshold) {
        systemCommandSender.sendThresholdCommand(threshold);
        System system = get();
        System updatedSystem = system.copyWith(system.speed(), threshold);
        jpaSystemRepository.save(SystemMapper.toEntity(updatedSystem));
    }

    @Override
    public void updateSpeed(Integer speed) {
        systemCommandSender.sendSpeedCommand(speed);
        System system = get();
        System updatedSystem = system.copyWith(speed, system.threshold());
        jpaSystemRepository.save(SystemMapper.toEntity(updatedSystem));
    }

    @Override
    public void update(SystemStatus status, Double cpuUsage, Double cpuTemperature, Double memoryUsage) {
        System system = get();
        System updatedSystem = system.copyWith(status, cpuUsage, cpuTemperature, memoryUsage);
        jpaSystemRepository.save(SystemMapper.toEntity(updatedSystem));
    }

    @Override
    public void update(SystemStatus status) {
        System system = get();
        System updatedSystem = system.copyWith(status);
        jpaSystemRepository.save(SystemMapper.toEntity(updatedSystem));
    }
}
