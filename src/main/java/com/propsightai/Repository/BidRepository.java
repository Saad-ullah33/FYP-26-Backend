package com.propsightai.Repository;

import com.propsightai.Model.Bid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid, Integer> {

    // ---------------- BASE CRUDS & QUERIES ----------------

    List<Bid> findByAuctionId(Integer auctionId);

    List<Bid> findByBidderId(Integer userId);

    /**
     * Pulls the absolute highest bid for an auction. Used inside the core engine
     * to determine historical outbids and run the cron finalization routines.
     */
    Bid findTopByAuctionIdOrderByAmountDesc(Integer auctionId);


    // ---------------- GRANULAR COUNT METRICS ----------------

    long countByAuctionId(Integer auctionId);

    Integer countByBidderId(Integer bidderId);

    int countByAuctionPropertyId(Integer propertyId);

    @Query("SELECT COUNT(DISTINCT b.bidder.id) FROM Bid b WHERE b.auction.id = :auctionId")
    Long countDistinctBiddersByAuctionId(@Param("auctionId") Integer auctionId);


    // ---------------- HIGH-PERFORMANCE ANALYTICS AGGREGATIONS ----------------

    /**
     * Aggregates complete historic financial turnover across the platform safely in the DB.
     */
    @Query("SELECT SUM(b.amount) FROM Bid b WHERE b.amount IS NOT NULL")
    Double sumAllBidAmounts();

    /**
     * Ranks auctions based on engagement using high-speed indexed groupings.
     */
    @Query("SELECT b.auction.id, COUNT(b.id) FROM Bid b GROUP BY b.auction.id ORDER BY COUNT(b.id) DESC")
    List<Object[]> findAuctionsOrderedByBidCount(Pageable pageable);

    /**
     * Tracks power users/bidders on the platform for leaderboards or dashboard features.
     */
    @Query("SELECT b.bidder.id, COUNT(b.id) FROM Bid b GROUP BY b.bidder.id ORDER BY COUNT(b.id) DESC")
    List<Object[]> findTopBiddersByBidCount(Pageable pageable);

    /**
     * Counts how many auctions a user successfully won by checking if they are flagged as the final winner.
     */
    @Query("SELECT COUNT(b.id) FROM Bid b WHERE b.bidder.id = :userId AND b.auction.winner.id = :userId")
    int countSuccessfulBidsByBidder(@Param("userId") Integer userId);

    /**
     * Calculates complete individual investor/bidder capital expenditure.
     */
    @Query("SELECT COALESCE(SUM(b.amount), 0.0) FROM Bid b WHERE b.bidder.id = :userId")
    Double sumTotalAmountSpentByBidder(@Param("userId") Integer userId);
}