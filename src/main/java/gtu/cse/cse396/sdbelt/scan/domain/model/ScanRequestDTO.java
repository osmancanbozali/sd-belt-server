package gtu.cse.cse396.sdbelt.scan.domain.model;

import lombok.Builder;
import lombok.Getter;

/**
 * Represents the result of a product scan performed during the conveyor belt
 * process.
 * <p>
 * Each scan captures the time of scanning, whether the operation was
 * successful,
 * and an optional error message if the scan failed.
 */
@Builder
public record ScanRequestDTO(

        /**
         * The unique identifier of the product that was scanned.
         * <p>
         * This field links the scan result back to the product it belongs to.
         */
        String productResult,

        Double confidence,

        Double x,
        Double y) {
}
