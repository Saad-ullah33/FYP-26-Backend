package com.propsightai.Service;

import com.propsightai.Model.Bid;
import java.util.List;
import java.math.BigDecimal;

public interface BidService {

    Bid placeBid(int auctionId, int userId, BigDecimal amount);

    List<Bid> getAllBids();

    List<Bid> getBidsByAuction(int auctionId);

    List<Bid> getBidsByUser(int userId);

    Bid getHighestBid(int auctionId);

    void deleteBid(int bidId);
}