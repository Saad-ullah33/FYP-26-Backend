package com.propsightai;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_events")
public class AuditEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AuditEventType type;

    private Integer userId;

    @Column(length = 2000)
    private String details;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public AuditEvent() {}

    public AuditEvent(AuditEventType type, Integer userId, String details) {
        this.type = type;
        this.userId = userId;
        this.details = details;
    }

    // getters/setters

    public Long getId() { return id; }
    public AuditEventType getType() { return type; }
    public void setType(AuditEventType type) { this.type = type; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}