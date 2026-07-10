package com.propsightai.Repository;

import com.propsightai.Model.Auction;
import com.propsightai.Role.AuctionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuctionRepository extends JpaRepository<Auction,Integer> {
    boolean existsByPropertyIdAndStatusIn(Integer propertyId, List<AuctionStatus> statuses);
    List<Auction> findByStatusIn(List<AuctionStatus> statuses);

    List<Auction> findByStatusAndStatusIn(AuctionStatus status, List<AuctionStatus> allowedStatuses);

    List<Auction> findByStatus(AuctionStatus status);
    List<Auction> findByStatusAndEndTimeBefore(AuctionStatus status, LocalDateTime time);

    long countByStatus(AuctionStatus status);

    // Analytics queries
    default long countByStatusActive() {
        return countByStatus(AuctionStatus.ACTIVE);
    }
    int countByProperty_Owner_Id(Integer ownerId);

    int countByProperty_Owner_IdAndStatus(Integer ownerId, AuctionStatus status);

    List<Auction> findByProperty_Owner_Id(Integer ownerId);

    List<Auction> findByProperty_Owner_IdAndStatus(Integer ownerId, AuctionStatus status);
}

