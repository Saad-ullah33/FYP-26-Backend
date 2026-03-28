package com.propsightai.Model;

import com.propsightai.Role.Role;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.security.AuthProvider;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "UserID"
    )
    private Integer id;

    @Column(
            name = "name"
    )
    private String name;

    @Column(
            name = "Email",
            nullable = false, unique = true)
    private String email;

    @Column(
            name = "Password"
    )
    private String password; // null for Google users

    @Column(
            name = "PhoneNumber"
    )
    private String phone;

    @Column(
            name = "UserType"
    )
    private String userType;
    @Column(
            name = "Googleid"
    )
    private String googleid;
    @Column(
            name = "Auth"
    )
    private String auth;
    @Column(
            name = "Profileimg"
    )
    private String profile; // USER, AGENT, ADMIN

        private String providerId;
    private String image;// Google sub

    @Column(
            name = "isVerified"
    )
    private Boolean isVerified = false;
    @Column(
            name = "Address"
    )
    private String address;

    @Column(
            name = "IsActive"
    )
    private Boolean isActive = true;

    @CreationTimestamp
    private LocalDate createdAt;


    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getGoogleid() {
        return googleid;
    }

    public void setGoogleid(String googleid) {
        this.googleid = googleid;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Boolean getVerified() {
        return isVerified;
    }

    public void setVerified(Boolean verified) {
        isVerified = verified;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }
}
