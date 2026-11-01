package com.jaswanth.fooddeliveryplatform.cart.service;

import com.jaswanth.fooddeliveryplatform.cart.dto.AddToCartRequest;
import com.jaswanth.fooddeliveryplatform.cart.dto.CartItemResponse;
import com.jaswanth.fooddeliveryplatform.cart.dto.CartResponse;
import com.jaswanth.fooddeliveryplatform.cart.entity.Cart;
import com.jaswanth.fooddeliveryplatform.cart.entity.CartItem;
import com.jaswanth.fooddeliveryplatform.cart.repository.CartItemRepository;
import com.jaswanth.fooddeliveryplatform.cart.repository.CartRepository;
import com.jaswanth.fooddeliveryplatform.common.exception.common.BusinessConflictException;
import com.jaswanth.fooddeliveryplatform.common.exception.common.ResourceNotFoundException;
import com.jaswanth.fooddeliveryplatform.common.exception.common.UserNotFoundException;
import com.jaswanth.fooddeliveryplatform.coupon.entity.Coupon;
import com.jaswanth.fooddeliveryplatform.coupon.service.CouponService;
import com.jaswanth.fooddeliveryplatform.restaurant.entity.ApprovalStatus;
import com.jaswanth.fooddeliveryplatform.restaurant.entity.MenuItem;
import com.jaswanth.fooddeliveryplatform.restaurant.repository.MenuItemRepository;
import com.jaswanth.fooddeliveryplatform.user.entity.User;
import com.jaswanth.fooddeliveryplatform.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    private static final BigDecimal PACKAGING_FEE = new BigDecimal("20.00");
    private static final BigDecimal DELIVERY_FEE = new BigDecimal("40.00");
    private static final BigDecimal TAX_RATE = new BigDecimal("0.05"); // 5% GST
    private static final int MAX_ITEM_QUANTITY = 20;

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MenuItemRepository menuItemRepository;
    private final UserRepository userRepository;
    private final CouponService couponService;

    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           MenuItemRepository menuItemRepository,
                           UserRepository userRepository,
                           CouponService couponService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.menuItemRepository = menuItemRepository;
        this.userRepository = userRepository;
        this.couponService = couponService;
    }

    @Override
    @Transactional
    public CartResponse getCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return buildCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse addToCart(AddToCartRequest request, Long userId) {
        Cart cart = getOrCreateCart(userId);

        MenuItem menuItem = menuItemRepository.findById(request.getMenuItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + request.getMenuItemId()));

        // 1. Restaurant must be approved & active
        if (menuItem.getRestaurant().getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new BusinessConflictException("Cannot add items from this restaurant because it is not active.");
        }

        // 2. Dish must be in stock
        if (!menuItem.isAvailable()) {
            throw new BusinessConflictException("'" + menuItem.getName() + "' is currently out of stock.");
        }

        // 3. Single Restaurant Constraint (Swiggy / Zomato multi-restaurant prevention)
        if (cart.getRestaurant() != null && !cart.getRestaurant().getId().equals(menuItem.getRestaurant().getId())) {
            throw new BusinessConflictException(
                    "Your cart contains items from '" + cart.getRestaurant().getName() +
                            "'. Please clear your cart to order from '" + menuItem.getRestaurant().getName() + "'."
            );
        }

        // Bind cart to restaurant if first item
        if (cart.getRestaurant() == null) {
            cart.setRestaurant(menuItem.getRestaurant());
        }

        // 4. Update quantity if item already in cart, else create a new item row
        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartIdAndMenuItemId(cart.getId(), menuItem.getId());
        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            int updatedQuantity = existingItem.getQuantity() + request.getQuantity();
            if (updatedQuantity > MAX_ITEM_QUANTITY) {
                throw new BusinessConflictException("Cannot exceed maximum limit of " + MAX_ITEM_QUANTITY + " items for a single dish.");
            }
            existingItem.setQuantity(updatedQuantity);
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem(cart, menuItem, request.getQuantity());
            cart.addItem(newItem);
            cartItemRepository.save(newItem);
        }

        cartRepository.save(cart);
        return buildCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse updateItemQuantity(Long cartItemId, int quantity, Long userId) {
        Cart cart = getOrCreateCart(userId);

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        // Ownership verification
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BusinessConflictException("This cart item does not belong to your cart.");
        }

        if (quantity <= 0) {
            cart.removeItem(item);
            cartItemRepository.delete(item);

            // If cart is now completely empty, reset restaurant and coupons
            if (cart.getItems().isEmpty()) {
                cart.setRestaurant(null);
                cart.setAppliedCoupon(null);
            }
        } else {
            if (quantity > MAX_ITEM_QUANTITY) {
                throw new BusinessConflictException("Cannot exceed maximum limit of " + MAX_ITEM_QUANTITY + " items for a single dish.");
            }
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }

        cartRepository.save(cart);
        return buildCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse removeItemFromCart(Long cartItemId, Long userId) {
        return updateItemQuantity(cartItemId, 0, userId);
    }

    @Override
    @Transactional
    public CartResponse applyCoupon(String couponCode, Long userId) {
        Cart cart = getOrCreateCart(userId);

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BusinessConflictException("Cannot apply a coupon to an empty cart.");
        }

        BigDecimal subtotal = calculateSubtotal(cart);

        // Delegate coupon validation to CouponService (handles validity, expiry, min-order threshold)
        Coupon validCoupon = couponService.getValidCouponOrThrow(couponCode.trim(), userId, subtotal);

        cart.setAppliedCoupon(validCoupon);
        Cart savedCart = cartRepository.save(cart);

        return buildCartResponse(savedCart);
    }

    @Override
    @Transactional
    public CartResponse removeCoupon(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cart.setAppliedCoupon(null);
        Cart savedCart = cartRepository.save(cart);
        return buildCartResponse(savedCart);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cart.clearItems();
        cart.setRestaurant(null);
        cart.setAppliedCoupon(null);
        cartRepository.save(cart);
    }

    // =========================================================================
    // Internal Helper Methods
    // =========================================================================

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
            Cart newCart = new Cart(user);
            return cartRepository.save(newCart);
        });
    }

    private BigDecimal calculateSubtotal(Cart cart) {
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return cart.getItems().stream()
                .map(item -> item.getMenuItem().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private CartResponse buildCartResponse(Cart cart) {
        List<CartItemResponse> itemResponses = new ArrayList<>();
        BigDecimal itemTotal = BigDecimal.ZERO;

        if (cart.getItems() != null) {
            for (CartItem item : cart.getItems()) {
                BigDecimal lineTotal = item.getMenuItem().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                itemTotal = itemTotal.add(lineTotal);

                itemResponses.add(new CartItemResponse(
                        item.getId(),
                        item.getMenuItem().getId(),
                        item.getMenuItem().getName(),
                        item.getMenuItem().getPrice(),
                        item.getQuantity(),
                        lineTotal
                ));
            }
        }

        // Empty cart edge case
        if (itemResponses.isEmpty()) {
            return new CartResponse(
                    cart.getId(),
                    null,
                    null,
                    itemResponses,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    null,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
            );
        }

        // Discount calculation with auto-drop if cart drops below threshold
        BigDecimal discountAmount = BigDecimal.ZERO;
        String appliedCouponCode = null;

        if (cart.getAppliedCoupon() != null) {
            Coupon coupon = cart.getAppliedCoupon();
            if (coupon.isValid(itemTotal)) {
                discountAmount = coupon.calculateDiscount(itemTotal);
                appliedCouponCode = coupon.getCode();
            } else {
                // If items were removed and total dropped below min order, clear coupon
                cart.setAppliedCoupon(null);
                cartRepository.save(cart);
            }
        }

        // GST & Fee structure
        BigDecimal discountedFoodTotal = itemTotal.subtract(discountAmount).max(BigDecimal.ZERO);
        BigDecimal packagingFee = PACKAGING_FEE;
        BigDecimal deliveryFee = DELIVERY_FEE;
        BigDecimal taxAmount = discountedFoodTotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = discountedFoodTotal.add(packagingFee).add(deliveryFee).add(taxAmount);

        return new CartResponse(
                cart.getId(),
                cart.getRestaurant().getId(),
                cart.getRestaurant().getName(),
                itemResponses,
                itemTotal,
                discountAmount,
                appliedCouponCode,
                deliveryFee,
                packagingFee,
                taxAmount,
                totalAmount
        );
    }
}