package gtu.cse.cse396.sdbelt.scan.infra.mapper;

import org.springframework.stereotype.Component;
import gtu.cse.cse396.sdbelt.scan.domain.model.Scan;
import gtu.cse.cse396.sdbelt.scan.infra.model.ScanEntity;

@Component
public class ScanMapper {

    private ScanMapper() {
        // Private constructor to prevent instantiation
    }
    
    public static Scan toDomain(ScanEntity entity) {
        return new Scan(
                entity.getProductId(),
                entity.getTimestamp(),
                entity.getIsSuccess(),
                entity.getErrorMessage()
        );
    }

    public static ScanEntity toEntity(Scan scan) {
        return new ScanEntity(
                scan.productId(),
                scan.timestamp(),
                scan.isSuccess(),
                scan.errorMessage()
        );
    }
  
} 
