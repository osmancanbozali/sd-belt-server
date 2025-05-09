package gtu.cse.cse396.sdbelt.scan.infra.adapter;

import gtu.cse.cse396.sdbelt.scan.domain.service.ScanService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class ScanDataInitializer implements ApplicationRunner {

    private final ScanService scanService;

    private static final int SCAN_COUNT = 1000;

    @Override
    public void run(ApplicationArguments args) {
        if (scanService.list().size() >= SCAN_COUNT) {
            System.out.println("Scan data already initialized.");
            return;
        }

        System.out.println("Initializing scan data...");

        Random random = new Random();

        for (int i = 0; i < SCAN_COUNT; i++) {
            Long productId = (long) (1 + random.nextInt(5)); // productId between 1 and 5
            boolean isSuccess = random.nextBoolean();
            String errorMessage = isSuccess ? null : "Scan failed due to random error";

            // Generate random timestamp between 2 years ago and now
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime twoYearsAgo = now.minusYears(2);

            long startEpoch = twoYearsAgo.toEpochSecond(ZoneOffset.UTC);
            long endEpoch = now.toEpochSecond(ZoneOffset.UTC);

            long randomEpoch = ThreadLocalRandom.current().nextLong(startEpoch, endEpoch);
            /* LocalDateTime randomTimestamp = LocalDateTime.ofInstant(Instant.ofEpochSecond(randomEpoch), ZoneOffset.UTC); */

            // Save scan
            scanService.create(productId, isSuccess, randomEpoch, errorMessage);

            // Optionally print progress every 100
            if (i % 100 == 0) {
                System.out.println("Inserted " + i + " scans...");
            }
        }

        System.out.println("Scan data initialization complete.");
    }
}
