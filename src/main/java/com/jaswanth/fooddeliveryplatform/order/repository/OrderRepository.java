package com.jaswanth.fooddeliveryplatform.order.repository;

import com.jaswanth.fooddeliveryplatform.order.entity.Order;
import com.jaswanth.fooddeliveryplatform.order.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByIdAndCustomerId(Long id, Long customerId);

    Optional<Order> findByIdAndRestaurantId(Long id, Long restaurantId);

    Page<Order> findByCustomerIdOrderByCreatedAtDesc(Long customerId, Pageable pageable);

    Page<Order> findByRestaurantIdOrderByCreatedAtDesc(Long restaurantId, Pageable pageable);

    List<Order> findByOrderStatusAndDeliveryPartnerIsNull(OrderStatus orderStatus);

    List<Order> findByDeliveryPartnerIdAndOrderStatusIn(Long deliveryPartnerId, List<OrderStatus> statuses);

    /**
     * Atomic SQL statement preventing race condition where two drivers accept the same order simultaneously.
     */
    @Modifying
    @Query("UPDATE Order o SET o.deliveryPartner.id = :driverId " +
            "WHERE o.id = :orderId AND o.deliveryPartner IS NULL AND o.orderStatus = :expectedStatus")
    int assignDriverIfUnassigned(@Param("orderId") Long orderId,
                                 @Param("driverId") Long driverId,
                                 @Param("expectedStatus") OrderStatus expectedStatus);
}