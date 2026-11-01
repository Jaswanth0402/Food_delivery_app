package com.jaswanth.fooddeliveryplatform.restaurant.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class RestaurantRegistrationRequest {

    @NotBlank(message = "Restaurant name is required")
    @Size(max = 150, message = "Restaurant name must not exceed 150 characters")
    private  String name;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{9,14}$", message = "Phone must contain 10 to 15 digits")
    private String phone;

    @NotNull(message = "Latitude is required for live delivery dispatch")
    @DecimalMin(value = "-90.00000000", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.00000000", message = "Latitude must be between -90 and 90")
    private BigDecimal latitude;

    @NotNull(message = "Longitude is required for live delivery dispatch")
    @DecimalMin(value = "-180.00000000", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.00000000", message = "Longitude must be between -180 and 180")
    private BigDecimal longitude;

    public RestaurantRegistrationRequest(){}

    public RestaurantRegistrationRequest(String name, String description, String address,
                                         String phone, BigDecimal latitude, BigDecimal longitude) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }


}
