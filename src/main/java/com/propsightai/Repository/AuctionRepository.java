package com.propsightai.Repository;

import com.propsightai.Model.Auction;
import com.propsightai.Role.AuctionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Integer> {

    // ---------------- ACTIVE/LIVE CHECK CONSTRAINTS ----------------

    boolean existsByPropertyIdAndIsDeletedFalseAndStatusIn(Integer propertyId, Collection<AuctionStatus> statuses);

    // ---------------- GLOBAL FETCHES (EXCLUDING SOFT-DELETED) ----------------

    List<Auction> findAllByIsDeletedFalse();

    List<Auction> findByStatusAndIsDeletedFalse(AuctionStatus status);

    List<Auction> findByStatusInAndIsDeletedFalse(Collection<AuctionStatus> statuses);

    // ---------------- USER DASHBOARD LOOKUPS ----------------

    List<Auction> findByProperty_Owner_IdAndIsDeletedFalse(Integer userId);

    List<Auction> findByProperty_Owner_IdAndStatusAndIsDeletedFalse(Integer userId, AuctionStatus status);

    int countByProperty_Owner_IdAndIsDeletedFalse(Integer userId);

    int countByProperty_Owner_IdAndStatusAndIsDeletedFalse(Integer userId, AuctionStatus status);

    // ---------------- CRON / BATCH PROCESSING ----------------

    // Used by background workers to look up auctions that hit their expiration time but haven't been finalized
    List<Auction> findByStatusAndEndTimeBeforeAndIsDeletedFalse(AuctionStatus status, LocalDateTime time);

    // ---------------- ANALYTICS & METRICS ----------------

    long countByStatusAndIsDeletedFalse(AuctionStatus status);

    default long countByStatusActive() {
        return countByStatusAndIsDeletedFalse(AuctionStatus.ACTIVE);
    }

    long countByStatus(AuctionStatus auctionStatus);
}