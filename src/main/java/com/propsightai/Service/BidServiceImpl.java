package com.propsightai.Service;

import com.propsightai.Model.*;
import com.propsightai.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BidServiceImpl implements BidService {

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private UserRepository userRepository;

    // ================= PLACE BID =================
    @Override
    public Bid placeBid(int auctionId, int userId, BigDecimal amount) {

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Bid highestBid = bidRepository.findTopByAuctionIdOrderByAmountDesc(auctionId);

        if (highestBid != null &&
                amount.compareTo(highestBid.getAmount()) <= 0) {
            throw new RuntimeException("Bid must be higher than current highest bid");
        }

        Bid bid = new Bid();
        bid.setAuction(auction);
        bid.setBidder(user);
        bid.setAmount(amount);

        // 🔥 IMPORTANT FIX
        auction.setCurrentHighestBid(amount);
        auctionRepository.save(auction);

        return bidRepository.save(bid);
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