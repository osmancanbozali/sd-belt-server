package gtu.cse.cse396.sdbelt.scan.infra.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import gtu.cse.cse396.sdbelt.scan.infra.model.ScanEntity;

public interface JpaScanRepository extends JpaRepository<ScanEntity, UUID>, JpaSpecificationExecutor<ScanEntity> {
}
