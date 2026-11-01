package com.jaswanth.fooddeliveryplatform.delivery.entity;


import com.jaswanth.fooddeliveryplatform.restaurant.entity.ApprovalStatus;
import com.jaswanth.fooddeliveryplatform.user.entity.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_partner_profiles")
public class DeliveryPartnerProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "vehicle_number", nullable = false, length = 50)
    private String vehicleNumber;

    @Column(name = "license_number", nullable = false, length = 50)
    private String licenseNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", nullable = false, length = 20)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    @Column(name = "is_online", nullable = false)
    private boolean online = false;

    @Column(name = "current_latitude", precision = 10, scale = 8)
    private BigDecimal currentLatitude;

    @Column(name = "current_longitude", precision = 11, scale = 8)
    private BigDecimal currentLongitude;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    public DeliveryPartnerProfile() {}

    public DeliveryPartnerProfile(User user, String vehicleNumber, String licenseNumber) {
        this.user = user;
        this.vehicleNumber = vehicleNumber;
        this.licenseNumber = licenseNumber;
        this.approvalStatus = ApprovalStatus.PENDING;
        this.online = false;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getVehicleNumber() { return vehicleNumber; }
    public String getLicenseNumber() { return licenseNumber; }
    public ApprovalStatus getApprovalStatus() { return approvalStatus; }
    public boolean isOnline() { return online; }
    public BigDecimal getCurrentLatitude() { return currentLatitude; }
    public BigDecimal getCurrentLongitude() { return currentLongitude; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setApprovalStatus(ApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; }
    public void setOnline(boolean online) { this.online = online; }
    public void setCurrentLatitude(BigDecimal currentLatitude) { this.currentLatitude = currentLatitude; }
    public void setCurrentLongitude(BigDecimal currentLongitude) { this.currentLongitude = currentLongitude; }
}
