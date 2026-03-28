package com.propsightai.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "Images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(
            name = "cloudinary_src"
    )
    private String cloudinary_src;
    @Column(
            name = "cloud_id"
    )
    private String cloud_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propertyId")
    @JsonBackReference
    private Property property;

    // No-args constructor
    public Image() {}

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCloudinary_src() { return cloudinary_src; }
    public void setCloudinary_src(String cloudinary_src) { this.cloudinary_src = cloudinary_src; }

    public String getCloud_id() { return cloud_id; }
    public void setCloud_id(String cloud_id) { this.cloud_id = cloud_id; }

    public Property getProperty() { return property; }
    public void setProperty(Property property) { this.property = property; }
}
