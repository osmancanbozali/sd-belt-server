package gtu.cse.cse396.sdbelt.system.infra.adapter;

import java.time.LocalDateTime;

public record SystemStatusInfo(LocalDateTime timestamp, Double cpuDegree, Double cpuUsage, String memoryUsage) {
}