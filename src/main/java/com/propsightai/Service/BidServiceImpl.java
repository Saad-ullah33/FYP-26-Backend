package com.propsightai.Service;

import com.propsightai.AuditEventType;
import com.propsightai.Dto.BidResponseDto;
import com.propsightai.Model.*;
import com.propsightai.Repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Service
public class BidServiceImpl implements BidService {

    private static final Logger logger = LoggerFactory.getLogger(BidServiceImpl.class);

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuctionValidator auctionValidator;

    @Autowired
    private com.propsightai.AuditService auditService;

    private static final int MAX_RETRIES = 3;

    // ================= PLACE BID =================
    @Override
    @Transactional
    public BidResponseDto placeBid(
            int auctionId,
            BigDecimal amount,
            Principal principal
    ) {
        if (principal == null) {
            throw new RuntimeException(
                    "User authentication required"
            );
        }
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {

            try {


                Auction auction =
                        auctionRepository.findById(auctionId)
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "Auction not found"
                                        )
                                );


                String email = principal.getName();


                User user =
                        userRepository.findByEmail(email)
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "User not found"
                                        )
                                );



                auctionValidator.validateBidAllowed(
                        auction,
                        amount
                );



                Bid highestBid =
                        bidRepository
                                .findTopByAuctionIdOrderByAmountDesc(
                                        auctionId
                                );



                if (highestBid != null &&
                        amount.compareTo(
                                highestBid.getAmount()
                        ) <= 0) {

                    throw new RuntimeException(
                            "Bid must be higher than current bid"
                    );
                }



                Bid bid = new Bid();

                bid.setAuction(auction);

                bid.setBidder(user);

                bid.setAmount(amount);

                auction.setCurrentHighestBid(amount);

                auctionRepository.save(auction);

                Bid saved =
                        bidRepository.save(bid);
                try {

                    auditService.record(
                            AuditEventType.BID_PLACED,
                            user.getId(),
                            "Bid placed: " + saved.getAmount()
                    );

                } catch (Exception ignored) {

                    logger.warn(
                            "Audit failed for bid {}",
                            saved.getId()
                    );

                }
                return new BidResponseDto(saved);
            } catch (OptimisticLockingFailureException ex) {
                logger.warn(
                        "Concurrent bid attempt {} for auction {}",
                        attempt,
                        auctionId
                );


                if (attempt == MAX_RETRIES) {

                    throw new RuntimeException(
                            "Auction is busy. Please try again."
                    );
                }
            }
        }


        throw new RuntimeException(
                "Unable to place bid"
        );
    }

    // ================= GET ALL =================
    @Override
    public List<Bid> getAllBids() {
        return bidRepository.findAll();
    }

    // ================= BY AUCTION =================
    @Override
    public List<Bid> getBidsByAuction(int auctionId) {
        return bidRepository.findByAuctionId(auctionId);
    }

    // ================= BY USER =================
    @Override
    public List<Bid> getBidsByUser(int userId) {
        return bidRepository.findByBidderId(userId);
    }

    // ================= HIGHEST BID =================
    @Override
    public Bid getHighestBid(int auctionId) {
        return bidRepository.findTopByAuctionIdOrderByAmountDesc(auctionId);
    }

    // ================= DELETE =================
    @Override
    public void deleteBid(int bidId) {
        bidRepository.deleteById(bidId);
    }
}