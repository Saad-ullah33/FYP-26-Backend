package com.propsightai.Service;

import com.propsightai.Model.Auction;
import com.propsightai.Repository.AuctionRepository;
import com.propsightai.Role.AuctionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionScheduler {

    private final AuctionRepository auctionRepository;
    private final AuctionService auctionService;

    /**
     * Periodically sweeps for live auctions that have passed their expiration deadline
     * and triggers the automated settlement/finalization engine.
     * Runs every 30 minutes (1800000 milliseconds).
     */
    @Scheduled(fixedRate = 1800000)
    public void checkExpiredAuctions() {
        LocalDateTime scanTime = LocalDateTime.now();
        log.info("Starting automated auction expiration sweep at {}", scanTime);

        // Uses the modernized, soft-delete aware query interface
        List<Auction> expiredAuctions = auctionRepository
                .findByStatusAndEndTimeBeforeAndIsDeletedFalse(AuctionStatus.ACTIVE, scanTime);

        if (expiredAuctions.isEmpty()) {
            log.info("Sweep complete: No expired auctions found.");
            return;
        }

        log.info("Found {} expired auction(s) requiring finalization.", expiredAuctions.size());

        for (Auction auction : expiredAuctions) {
            try {
                // Process each record within an isolated execution boundary
                log.debug("Processing automated finalization for auction ID: {}", auction.getId());
                auctionService.finalizeAuction(auction.getId());
                log.info("Successfully finalized auction ID: {}", auction.getId());
            } catch (Exception e) {
                // Safeguard: A failure processing one bad record must never halt the entire batch
                log.error("Fatal exception encountered while finalizing auction ID: {}. Skipping to next record.",
                        auction.getId(), e);
            }
        }

        log.info("Automated auction expiration sweep completed successfully.");
    }
}