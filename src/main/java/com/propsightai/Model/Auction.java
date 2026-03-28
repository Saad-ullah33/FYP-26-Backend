package com.propsightai.Model;

import com.propsightai.Role.AuctionStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Auctions")
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "AuctionID"
    )
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PropertyID", nullable = false)
    private Property property;

    @Column(
            name = "StartingPrice"
    )
    private Double startingPrice;

    @Column(
            name = "ReservePrice"
    )
    private Double reservePrice; // optional minimum

    @Column(
            name = "StartTime"
    )
    private LocalDateTime startTime;

    @Column(
            name = "EndTime"
    )
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "Status"
    )
    private AuctionStatus status = AuctionStatus.CLOSED;

    @OneToMany(
            mappedBy = "auction",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Bid> bids = new ArrayList<>();

    public void addBid(Bid bid) {
        bids.add(bid);
        bid.setAuction(this);
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

    public Double getStartingPrice() {
        return startingPrice;
    }

    public Double getReservePrice() {
        return reservePrice;
    }

    public LocalDateTime getStartTime() {
        return startTime;
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


    public void setProperty(Property property) {
        this.property = property;
    }

    public void setStartingPrice(Double startingPrice) {
        this.startingPrice = startingPrice;
    }

    public void setReservePrice(Double reservePrice) {
        this.reservePrice = reservePrice;
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
}

