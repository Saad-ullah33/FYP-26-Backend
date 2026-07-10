package com.propsightai.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.propsightai.Role.PropertyType;
import com.propsightai.Role.PurposeType;
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
    @Column(  name = "Price",nullable = false )
    private Double price;

    @Enumerated(EnumType.STRING)
    @Column( name = "Purpose",nullable = false)
    private PurposeType purpose; // BUY / RENT


    @Column(  name = "Property_Type",nullable = false)
    @Enumerated(EnumType.STRING)
    private PropertyType propertyType; // HOUSE, PLOT, APARTMENT

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    private List<Image> images = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;
    @Column(
            name = "Area"
    )
    private String area;
    @Column(
            name = "location"
            ,nullable = false
    )
    private String location;
    @Column(name = "bathrooms", nullable = false)
    private Integer bathrooms;

    @Column(name = "bedrooms", nullable = false)
    private Integer bedrooms;
    @Column(
            name = "Address"
            ,nullable = false
    )
    private String address;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User owner;

    @Column(
            name = "IsAvailable"

    )
    private Boolean isAvailable;

    @JsonIgnore
    @OneToOne(mappedBy = "property", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Auction auctions;

    @Column
    private boolean trending;

    @CreationTimestamp
    private LocalDateTime createdAt;


    private Boolean featured = false;

    private Integer priorityRank = 0;

    private Boolean auctionEnabled = false;

    private Integer viewsCount = 0;

    private Boolean approved = false;

    private Boolean sold = false;




//getters /setters


    public void setId(Integer id) {
        this.id = id;
    }

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

    public PurposeType getPurpose() {
        return purpose;
    }

    public PropertyType getPropertyType() {
        return propertyType;
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

    public City getCity() {
        return city;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(Integer bathrooms) {
        this.bathrooms = bathrooms;
    }

    public Integer getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(Integer bedrooms) {
        this.bedrooms = bedrooms;
    }

    public Auction getAuctions() {
        return auctions;
    }

    public Integer getViewsCount() {
        return viewsCount;
    }

    public Boolean getApproved() {
        return approved;
    }

    public Boolean getSold() {
        return sold;
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

    public void setPurpose(PurposeType purpose) {
        this.purpose = purpose;
    }

    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
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

    public void setCity(City city) {
        this.city = city;
    }

    public void addImage(Image image) {
        images.add(image);
        image.setProperty(this);
    }


    public void removeImage(Image image) {
        images.remove(image);
        image.setProperty(this);
    }

    public Boolean getAuctionEnabled() {
        return auctionEnabled;
    }

    public void setAuctionEnabled(Boolean auctionEnabled) {
        this.auctionEnabled = auctionEnabled;
    }

    public Integer getPriorityRank() {
        return priorityRank;
    }

    public void setPriorityRank(Integer priorityRank) {
        this.priorityRank = priorityRank;
    }

    public Boolean getFeatured() {
        return featured;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    public void setAuctions(Auction auctions) {
        this.auctions = auctions;
    }
    public void setSold(Boolean sold) {
        this.sold = sold;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public void setViewsCount(Integer viewsCount) {
        this.viewsCount = viewsCount;
    }
}
