package com.propsightai.Repository;

import com.propsightai.Model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid,Integer> {
    List<Bid> findByAuctionId(int auctionId);

    List<Bid> findByBidderId(int userId);

    Bid findTopByAuctionIdOrderByAmountDesc(int auctionId);
}
