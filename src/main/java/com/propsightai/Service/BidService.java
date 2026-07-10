package com.propsightai.Service;

import com.propsightai.Dto.BidResponseDto;
import com.propsightai.Model.Bid;

import java.security.Principal;
import java.util.List;
import java.math.BigDecimal;

public interface BidService {

    BidResponseDto placeBid(
            int auctionId,
            BigDecimal amount,
            Principal principal
    );

    List<Bid> getAllBids();

    List<Bid> getBidsByAuction(int auctionId);

    List<Bid> getBidsByUser(int userId);

    Bid getHighestBid(int auctionId);

    void deleteBid(int bidId);
}