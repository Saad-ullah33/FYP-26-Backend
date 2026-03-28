package com.propsightai.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Entity
@Table(name = "properties")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "PropertyID"
    )
    private Integer id;
    @Column(
            name = "Title"
            ,nullable = false
    )
    private String title;

    @Column(
            name = "Description"
    )
    private String description;
    @Column(
            name = "Price"
            ,nullable = false
    )
    private Double price;
    @Column(
            name = "Purpose"
            ,nullable = false
    )
    private String purpose; // BUY / RENT
    @Column(
            name = "Property_Type"
            ,nullable = false
    )
    private String propertyType; // HOUSE, PLOT, APARTMENT

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Image> images = new ArrayList<>();
    @Column(
            name = "City"
            ,nullable = false
    )
    private String city;
    @Column(
            name = "Area"
            ,nullable = false
    )
    private String area;
    @Column(
            name = "Address"
            ,nullable = false
    )
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User owner;

    @Column(
            name = "IsAvailable"

    )
    private Boolean isAvailable;
    @Column(name = "IsAuction")
    private Boolean isAuction = false;

    @OneToMany(mappedBy = "property", fetch = FetchType.LAZY)
    private List<Auction> auctions = new ArrayList<>();


    @CreationTimestamp
    private LocalDateTime createdAt;

    //getters /setters


    public Boolean getAvailable() {
        return isAvailable;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getPropertyType() {
        return propertyType;
    }


    public String getCity() {
        return city;
    }

    public String getArea() {
        return area;
    }

    public String getAddress() {
        return address;
    }

    public User getOwner() {
        return owner;
    }

    public Boolean getAuction() {
        return isAuction;
    }

    public List<Auction> getAuctions() {
        return auctions;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }

    public void setAuction(Boolean auction) {
        isAuction = auction;
    }


    public void addAuction(Auction auction) {
        auctions.add(auction);
        auction.setProperty(this);
    }

    public void removeAuction(Auction auction) {
        auctions.remove(auction);
        auction.setProperty(null);
    }

    public void addImage(Image image) {
        images.add(image);
        image.setProperty(this); // ✅ should work now
    }


    public void removeImage(Image image) {
        images.remove(image);
        image.setProperty(this);
    }


}
