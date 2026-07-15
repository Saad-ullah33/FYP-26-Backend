package com.propsightai.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.propsightai.Role.AuthProvider;
import com.propsightai.Role.Role;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userid") // ✅ Fixed casing to match DB schema
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "email", nullable = false, unique = true) // ✅ Lowercase match
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "phone_number", nullable = false, unique = true) // ✅ Matches schema name
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type") // ✅ Lowercase underscore match
    private Role userType;

    @Column(name = "auth") // ✅ Lowercase match
    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    @Column(name = "provider_id") // ✅ Lowercase match
    private String providerId;

    @Column(name = "image")
    private String image;

    @Column(name = "address")
    private String address;

    @Column(name = "is_active") // ✅ Lowercase match
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at") // ✅ Lowercase match
    private LocalDate createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status") // ✅ Lowercase underscore match
    private com.propsightai.Role.UserStatus status = com.propsightai.Role.UserStatus.PENDING_VERIFICATION;

    @JsonIgnore
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Property> properties = new ArrayList<>();

    @Column(name = "total_listings") // ✅ Lowercase match
    private Integer totalListings = 0;

    @Column(name = "refresh_token") // ✅ Lowercase match
    private String refreshToken;

    @Column(name = "reset_token") // ✅ Lowercase match
    private String resetToken;

    @Column(name = "reset_token_expiry") // ✅ Lowercase match
    private LocalDateTime resetTokenExpiry;

    @Column(name = "verification_token") // ✅ Lowercase match
    private String verificationToken;

    @Column(name = "verification_token_expiry") // ✅ Lowercase match
    private LocalDateTime verificationTokenExpiry;

    @Column(name = "is_email_verified") // ✅ Fixed naming casing mapping
    private Boolean isEmailVerified = false;

    @Column(name = "last_login") // ✅ Lowercase match
    private LocalDateTime lastLogin;

    // ================= GETTERS & SETTERS =================

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Integer getTotalListings() {
        return totalListings;
    }

    public void setTotalListings(Integer totalListings) {
        this.totalListings = totalListings;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public LocalDateTime getResetTokenExpiry() {
        return resetTokenExpiry;
    }

    public void setResetTokenExpiry(LocalDateTime resetTokenExpiry) {
        this.resetTokenExpiry = resetTokenExpiry;
    }

    public Boolean getEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.isEmailVerified = emailVerified; // ✅ Ensured safe assignment pointer
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public Role getUserType() {
        return userType;
    }

    public void setUserType(Role userType) {
        this.userType = userType;
    }

    public AuthProvider getAuthProvider() {
        return authProvider;
    }

    public void setAuthProvider(AuthProvider authProvider) {
        this.authProvider = authProvider;
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
        this.isActive = active; // ✅ Verified clean field scope binding pointer
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public com.propsightai.Role.UserStatus getStatus() {
        return status;
    }

    public void setStatus(com.propsightai.Role.UserStatus status) {
        this.status = status;
    }

    public LocalDateTime getVerificationTokenExpiry() {
        return verificationTokenExpiry;
    }

    public void setVerificationTokenExpiry(LocalDateTime verificationTokenExpiry) {
        this.verificationTokenExpiry = verificationTokenExpiry;
    }
}