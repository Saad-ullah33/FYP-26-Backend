package com.propsightai.Model;

import com.propsightai.Role.ActivityEventType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "activity_events", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_property_id", columnList = "property_id"),
    @Index(name = "idx_event_type", columnList = "event_type"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
public class ActivityEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ActivityEventID")
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "property_id")
    private Integer propertyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private ActivityEventType eventType;

    @Column(name = "metadata")
    private String metadata; // JSON for extensibility (e.g., bid amount, duration)

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public ActivityEvent() {
    }

    public ActivityEvent(Integer userId, Integer propertyId, ActivityEventType eventType) {
        this.userId = userId;
        this.propertyId = propertyId;
        this.eventType = eventType;
    }

    public ActivityEvent(Integer userId, Integer propertyId, ActivityEventType eventType, String metadata) {
        this.userId = userId;
        this.propertyId = propertyId;
        this.eventType = eventType;
        this.metadata = metadata;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Integer propertyId) {
        this.propertyId = propertyId;
    }

    public ActivityEventType getEventType() {
        return eventType;
    }

    public void setEventType(ActivityEventType eventType) {
        this.eventType = eventType;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
