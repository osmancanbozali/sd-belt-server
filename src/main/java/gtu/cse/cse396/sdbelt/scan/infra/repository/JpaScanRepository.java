package gtu.cse.cse396.sdbelt.scan.infra.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import gtu.cse.cse396.sdbelt.scan.infra.model.ScanEntity;

public interface JpaScanRepository extends JpaRepository<ScanEntity, Long>, JpaSpecificationExecutor<ScanEntity> {

    // Custom query methods can be defined here if needed
    // For example, to find scans by productId or timestamp range
    List<ScanEntity> findByProductIdAndTimestampBetween(String productId, LocalDateTime startTime,
            LocalDateTime endTime);

    List<ScanEntity> findByProductId(String productId);

    List<ScanEntity> findByTimestampBetween(LocalDateTime startTime, LocalDateTime endTime);
}
