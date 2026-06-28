package com.propsightai.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.propsightai.Role.AuctionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Auctions")
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column( name = "AuctionID" )
    private Integer id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PropertyID", nullable = false)
    private Property property;

    @Column( name = "StartingPrice",nullable = false )
    private BigDecimal startingPrice;

    @Column(  name = "ReservePrice")
    private BigDecimal reservePrice; // optional minimum

    @Column( name = "StartTime",nullable = false)
    private LocalDateTime startTime;

    @Column( name = "EndTime",nullable = false )
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column( name = "Status" )
    private AuctionStatus status = AuctionStatus.DRAFT;

    @JsonIgnore
    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<Bid> bids = new ArrayList<>();


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WinnerUserID")
    private User winner;

    @Column(name = "CurrentHighestBid")
    private BigDecimal currentHighestBid;

    @Column(name = "CreatedAt", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;



    @Version
    private Long version;

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;



    public void addBid(Bid bid) {

        if (bid == null || bid.getAmount() == null) {
            throw new IllegalArgumentException("Invalid bid");
        }

        if (status != AuctionStatus.ACTIVE) {
            throw new IllegalStateException("Auction is not active");
        }

        LocalDateTime now = LocalDateTime.now();

        if (startTime != null && now.isBefore(startTime)) {
            throw new IllegalStateException("Auction not started yet");
        }

        if (endTime != null && now.isAfter(endTime)) {
            throw new IllegalStateException("Auction already ended");
        }

        if (currentHighestBid != null &&
                bid.getAmount().compareTo(currentHighestBid) <= 0) {
            throw new IllegalArgumentException("Bid must be higher than current highest bid");
        }

        bids.add(bid);
        bid.setAuction(this);

        currentHighestBid = bid.getAmount();
    }

    public void removeBid(Bid bid) {
        bids.remove(bid);
        bid.setAuction(null);
    }


        //getter/setters


    public Integer getId() {
        return id;
    }

    public Property getProperty() {
        return property;
    }



    public LocalDateTime getStartTime() {
        return startTime;
    }


    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public AuctionStatus getStatus() {
        return status;
    }

    public List<Bid> getBids() {
        return bids;
    }

    public User getWinner() {
        return winner;
    }

    public void setWinner(User winner) {
        this.winner = winner;
    }

    public BigDecimal getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(BigDecimal startingPrice) {
        this.startingPrice = startingPrice;
    }

    public BigDecimal getReservePrice() {
        return reservePrice;
    }
    public Long getVersion() {
        return version;
    }
    public void setReservePrice(BigDecimal reservePrice) {
        this.reservePrice = reservePrice;
    }

    public void setBids(List<Bid> bids) {
        this.bids = (bids != null) ? bids : new ArrayList<>();
    }

    public BigDecimal getCurrentHighestBid() {
        return currentHighestBid;
    }

    public void setCurrentHighestBid(BigDecimal currentHighestBid) {
        this.currentHighestBid = currentHighestBid;
    }

    public void setProperty(Property property) {
        this.property = property;
    }



    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setStatus(AuctionStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}


