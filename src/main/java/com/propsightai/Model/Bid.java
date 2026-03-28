package com.propsightai.Model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bids")
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "BidID"
    )
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AuctionID", nullable = false)
    private Auction auction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID", nullable = false)
    private User bidder;


@Column(name = "BidAmount" , nullable = false)
    private Double amount;

@Column(name = "BidTime",nullable = false)
    private LocalDateTime bidTime ;

@PrePersist
void prePersist() {
    bidTime = LocalDateTime.now();
}

//getter/setters


    public Integer getId() {
        return id;
    }


    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }

    public User getBidder() {
        return bidder;
    }

    public void setBidder(User bidder) {
        this.bidder = bidder;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDateTime getBidTime() {
        return bidTime;
    }


}

