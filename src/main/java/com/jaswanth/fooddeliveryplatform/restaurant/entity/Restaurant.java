//package com.jaswanth.fooddeliveryplatform.restaurant.entity;
//
//import com.jaswanth.fooddeliveryplatform.user.entity.User;
//import jakarta.persistence.*;
//import lombok.*;
//import org.hibernate.annotations.CreationTimestamp;
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "restaurants")
//@Getter
//@Setter
////@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class Restaurant {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "owner_id", nullable = false)
//    private Long ownerId;
//
//    @Column(nullable = false, length = 100)
//    private String name;
//
//    @Column(length = 255)
//    private String description;
//
//    @Column(nullable = false, length = 255)
//    private String address;
//
//    @Column(length = 15)
//    private String phone;
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "approval_status",nullable = false, length = 20)
//    private ApprovalStatus approvalStatus;
//
//    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
//    private LocalDateTime createdAt;
//
//    public Restaurant() {
//    }
//
//    public Restaurant(
//            Long ownerId,
//            String name,
//            String description,
//            String address,
//            String phone,
//            ApprovalStatus approvalStatus
//    ) {
//        this.ownerId = ownerId;
//        this.name = name;
//        this.description = description;
//        this.address = address;
//        this.phone = phone;
//        this.approvalStatus = approvalStatus;
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public Long getOwnerId() {
//        return ownerId;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public String getAddress() {
//        return address;
//    }
//
//    public String getPhone() {
//        return phone;
//    }
//
//    public ApprovalStatus getApprovalStatus() {
//        return approvalStatus;
//    }
//
//    public LocalDateTime getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public void setAddress(String address) {
//        this.address = address;
//    }
//
//    public void setPhone(String phone) {
//        this.phone = phone;
//    }
//
//    public void setApprovalStatus(ApprovalStatus approvalStatus) {
//        this.approvalStatus = approvalStatus;
//    }
//}


package com.jaswanth.fooddeliveryplatform.restaurant.entity;

import com.jaswanth.fooddeliveryplatform.user.entity.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "restaurants")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Association targets an @Entity class (User), not java.lang.Long
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(length = 15)
    private String phone;

    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", nullable = false, length = 20)
    private ApprovalStatus approvalStatus;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public Restaurant() {}

    public Restaurant(User owner, String name, String description, String address, String phone,BigDecimal latitude, BigDecimal longitude, ApprovalStatus approvalStatus) {
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.address = address;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.approvalStatus = approvalStatus;
    }

    public Long getId() { return id; }
    public Long getOwnerId() {
        return owner != null ? owner.getId() : null;
    }
    public User getOwner() { return owner; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public ApprovalStatus getApprovalStatus() { return approvalStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public BigDecimal getLatitude() { return latitude; }
    public BigDecimal getLongitude() { return longitude; }

    public void setOwner(User owner) { this.owner = owner; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setAddress(String address) { this.address = address; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    public void setApprovalStatus(ApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; }
}