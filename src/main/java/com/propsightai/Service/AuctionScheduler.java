package com.propsightai.Service;

import com.propsightai.Model.Auction;
import com.propsightai.Repository.AuctionRepository;
import com.propsightai.Role.AuctionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuctionScheduler {

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private AuctionService auctionService;

    // runs every 30 minute
    @Scheduled(fixedRate = 1800000)
    public void checkExpiredAuctions() {

        List<Auction> expiredAuctions =
                auctionRepository.findByStatusAndEndTimeBefore(
                        AuctionStatus.ACTIVE,
                        LocalDateTime.now()
                );

        for (Auction auction : expiredAuctions) {
            auctionService.finalizeAuction(auction.getId());
        }
    }
}