package com.propsightai.Repository;

import com.propsightai.Model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid,Integer> {
    List<Bid> findByAuctionId(int auctionId);

    List<Bid> findByBidderId(int userId);

    Bid findTopByAuctionIdOrderByAmountDesc(int auctionId);

    // Analytics queries
    long countByAuctionId(Integer auctionId);

    Integer countByBidder_Id(Integer bidderId);

    long countByAuctionPropertyId(Integer propertyId);

    @Query("SELECT COUNT(DISTINCT b.bidder.id) FROM Bid b WHERE b.auction.id = :auctionId")
    Long countDistinctBiddersByAuctionId(@Param("auctionId") Integer auctionId);
}

