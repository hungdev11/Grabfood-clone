package com.api.service.Imp;

import com.api.dto.request.AddToCartRequest;
import com.api.dto.request.CartUpdateRequest;
import com.api.dto.response.AdditionalFoodCartResponse;
import com.api.dto.response.CartDetailResponse;
import com.api.dto.response.CartResponse;
import com.api.entity.*;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.repository.*;
import com.api.service.CartService;
import com.api.service.FoodService;
import com.api.service.VoucherDetailService;
import com.api.service.VoucherService;
import com.api.utils.FoodStatus;
import com.api.utils.VoucherApplyType;
import com.api.utils.VoucherStatus;
import com.api.utils.VoucherType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CartServiceImp implements CartService {
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final FoodRepository foodRepository;
    private final UserRepository userRepository;
    private final FoodService foodService;
    private final VoucherService voucherService;
    private final VoucherDetailService voucherDetailService;

    public void clearCart(Cart cart) {
        int beforeSize = cart.getCartDetails().size();
        cart.getCartDetails().removeIf(cd -> cd.getCart() != null && cd.getOrder() == null);
        int afterSize = cart.getCartDetails().size();

        log.info("Removed {} cart items", beforeSize - afterSize);
        cartRepository.save(cart);
    }

    private boolean compareTwoList(List<Long> list1, List<Long> list2) {
        log.debug("🔎 Comparing lists -> list1: {}, list2: {}", list1, list2);
        if (list1 == null || list2 == null)
            return false;
        if (list1.size() != list2.size())
            return false;
        List<Long> copy1 = new ArrayList<>(list1);
        List<Long> copy2 = new ArrayList<>(list2);
        Collections.sort(copy1);
        Collections.sort(copy2);
        boolean isEqual = copy1.equals(copy2);
        log.debug("✅ Compare result: {}", isEqual);
        return isEqual;
    }

    @Override
    public void createCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Cart cart = cartRepository.findByUser(user).orElse(null);
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cartRepository.save(cart);
            log.info("Created new cart for userId: {}", userId);
        }
    }

    @Override
    public void addToCart(Long userId, AddToCartRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        List<CartDetail> currentItems = cartDetailRepository.findByCartIdAndOrderIsNull(cart.getId());

        Food food = foodRepository.findById(request.getFoodId())
                .orElseThrow(() -> new AppException(ErrorCode.FOOD_NOT_FOUND));

        if (food.getStatus() != FoodStatus.ACTIVE) {
            throw new AppException(ErrorCode.FOOD_NOT_PUBLIC_FOR_CUSTOMER);
        }

        // Nếu có món khác restaurant trong giỏ → clear giỏ
        if (!currentItems.isEmpty()
                && !Objects.equals(food.getRestaurant(), currentItems.getFirst().getFood().getRestaurant())) {
            log.info("Restaurant in request is different from current cart, clearing cart");
            clearCart(cart);
        }

        // Check món đã có trong giỏ
        // Lọc các món trong giỏ và kiểm tra nếu đã tồn tại
        CartDetail existingCartDetail = currentItems.stream()
                .filter(item -> item.getFood().getId().equals(request.getFoodId())
                        && compareTwoList(item.getIds(), request.getAdditionalItems()))
                .findFirst() // Trả về phần tử đầu tiên thỏa mãn điều kiện
                .orElse(null); // Nếu không tìm thấy thì trả về null

        if (existingCartDetail != null) {
            int updatedQuantity = existingCartDetail.getQuantity() + request.getQuantity();
            existingCartDetail.setQuantity(updatedQuantity);
            existingCartDetail.setNote(request.getNote());
            cartDetailRepository.save(existingCartDetail);
            log.info("Updated quantity for foodId: {} in cartId: {} to {}", request.getFoodId(), cart.getId(),
                    updatedQuantity);
            return;
        }

        // Kiểm tra món phụ
        for (long foodId : request.getAdditionalItems()) {
            Food additionalFood = foodRepository.findById(foodId)
                    .orElseThrow(() -> new AppException(ErrorCode.FOOD_NOT_FOUND));

            boolean isValid = food.getMainFoods().stream()
                    .anyMatch(fma -> fma.getMainFood().equals(food) && fma.getAdditionFood().equals(additionalFood));

            if (!isValid) {
                throw new AppException(ErrorCode.ADDITIONAL_FOOD_NOT_FOUND);
            }
        }

        // Thêm mới vào giỏ
        CartDetail cartDetail = CartDetail.builder()
                .cart(cart)
                .food(food)
                .quantity(request.getQuantity())
                .ids(request.getAdditionalItems())
                .note(request.getNote())
                .build();

        cartDetailRepository.save(cartDetail);
        log.info("Added foodId: {} with quantity: {} to cartId: {}", food.getId(), request.getQuantity(), cart.getId());
    }

    @Override
    public void removeFromCart(Long userId, Long foodId, List<Long> additionalItems) {
        // Tìm giỏ hàng theo cartId
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("Cart not found for user id: {}", userId);
                    return new AppException(ErrorCode.CART_NOT_FOUND);
                });

        log.info("Found cart with cartId: {}", cart.getId());

        // Lấy danh sách các món trong giỏ (không có order)
        List<CartDetail> currentItems = cartDetailRepository.findByCartIdAndOrderIsNull(cart.getId());

        // Tìm CartDetail cần xóa
        CartDetail existingCartDetail = currentItems.stream()
                .filter(item -> item.getFood().getId().equals(foodId)
                        && compareTwoList(item.getIds(), additionalItems))
                .findFirst()
                .orElse(null);

        // Kiểm tra xem có tìm thấy CartDetail không
        if (existingCartDetail != null) {
            log.info("Found item to remove: foodId = {}, cartId = {}", foodId, cart.getId());

            // Xóa CartDetail khỏi giỏ hàng
            cart.getCartDetails().remove(existingCartDetail);

            // Lưu lại giỏ hàng đã cập nhật
            cartRepository.save(cart);

            log.info("Removed item with foodId: {} from cartId: {}", foodId, cart.getId());
        } else {
            log.warn("Item with foodId: {} not found in cartId: {}", foodId, cart.getId());
        }
    }

    @Override
    public void updateCart(CartUpdateRequest request) {
        // Tìm giỏ hàng
        Cart cart = cartRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> {
                    log.error("❌ Cart not found for userId: {}", request.getUserId());
                    return new AppException(ErrorCode.CART_NOT_FOUND);
                });
        log.info("🔄 Updating cartId: {} for userId: {}", cart.getId(), request.getUserId());

        // Tìm cart item cũ
        CartDetail cartDetail = cartDetailRepository.findById(request.getCartDetailId())
                .orElseThrow(() -> {
                    log.error("❌ Cart item not found with id: {}", request.getCartDetailId());
                    return new AppException(ErrorCode.CART_ITEM_NOT_FOUND);
                });

        // Kiểm tra món có trong cart không
        if (!cart.getCartDetails().contains(cartDetail)) {
            log.warn("⚠️ Cart item with id: {} is not part of cartId: {}", request.getCartDetailId(), cart.getId());
            throw new AppException(ErrorCode.CART_ITEM_NOT_FOUND);
        }

        // Lấy các món hiện tại trong giỏ chưa order
        List<CartDetail> currentItems = cartDetailRepository.findByCartIdAndOrderIsNull(cart.getId());

        log.info("🛒 Current cart items for cartId {}: {}", cart.getId(), currentItems.size());

        // Tìm món tương tự (same food + addition items)
        CartDetail existingCartDetail = currentItems.stream()
                .filter(item -> item.getFood().getId().equals(request.getFoodId())
                        && compareTwoList(item.getIds(), request.getAdditionFoodIds()))
                .findFirst()
                .orElse(null);

        if (existingCartDetail != null) {
            log.info("🔍 Found existing cart item with id: {}", existingCartDetail.getId());
        } else {
            log.info("➕ No existing similar item found. Will update current cartDetail.");
        }

        // Nếu khác id → gộp số lượng
        if (existingCartDetail != null && !existingCartDetail.getId().equals(request.getCartDetailId())) {
            int newQuantity = existingCartDetail.getQuantity() + request.getNewQuantity();
            existingCartDetail.setQuantity(newQuantity);

            log.info("🔁 Merging cartDetailId: {} into existingCartDetailId: {}, new quantity: {}",
                    request.getCartDetailId(), existingCartDetail.getId(), newQuantity);

            cart.getCartDetails().removeIf(i -> i.getId().equals(request.getCartDetailId()));
            cartDetailRepository.save(existingCartDetail);

            log.info("✅ Merged and saved item with id: {}", existingCartDetail.getId());
            return;
        }

        // Nếu update chính nó
        cartDetail.setQuantity(request.getNewQuantity());
        cartDetail.setIds(request.getAdditionFoodIds());
        // cartDetail.setNote(request.get);

        cartDetailRepository.save(cartDetail);

        log.info("✅ Updated cartDetailId: {} with new quantity: {}, additionFoodIds: {}",
                cartDetail.getId(), request.getNewQuantity(), request.getAdditionFoodIds());
    }

    @Override
    public void updateCartDetailQuantity(CartUpdateRequest request) {
        // Tìm giỏ hàng
        Cart cart = cartRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
        log.info("Cart id:" + cart.getId());
        log.info("CartDetail ID: " + request.getCartDetailId());
        // Tìm chi tiết món hàng trong giỏ
        CartDetail cartDetail = cartDetailRepository.findById(request.getCartDetailId())
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));

        // Kiểm tra nếu món hàng không thuộc giỏ hàng này
        if (!cart.getCartDetails().contains(cartDetail)) {
            throw new AppException(ErrorCode.CART_ITEM_NOT_FOUND);
        }

        // Cập nhật số lượng mới cho cartDetail
        cartDetail.setQuantity(request.getNewQuantity());

        // Lưu thay đổi vào database
        cartDetailRepository.save(cartDetail);
    }

    @Transactional
    @Override
    public CartResponse getAllCartDetailUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        List<CartDetail> cartDetails = cartDetailRepository.findByCartIdAndOrderIsNull(cart.getId());

        // Remove inactive food items
        List<CartDetail> inactiveCartDetails = cartDetails.stream()
                .filter(cd -> cd.getFood().getStatus() != FoodStatus.ACTIVE)
                .toList();

        if (!inactiveCartDetails.isEmpty()) {
            cartDetailRepository.deleteAll(inactiveCartDetails);
            cartDetails.removeAll(inactiveCartDetails);
        }

        if (cartDetails.isEmpty()) {
            return CartResponse.builder()
                    .cartId(cart.getId())
                    .listItem(Collections.emptyList())
                    .build();
        }

        List<Food> mainFoods = cartDetails.stream()
                .map(CartDetail::getFood)
                .toList();

        Long restaurantId = mainFoods.get(0).getRestaurant().getId();

        // Get valid vouchers
        List<Voucher> allVouchers = voucherService.getVoucherOfRestaurant(restaurantId).stream()
                .filter(v -> v.getStatus() == VoucherStatus.ACTIVE &&
                        v.getValue() != null &&
                        v.getValue().compareTo(BigDecimal.ZERO) > 0)
                .toList();

        LocalDateTime now = LocalDateTime.now();

        // Restaurant-level vouchers
        List<Voucher> validRestaurantVouchers = allVouchers.stream()
                .filter(v -> v.getApplyType() == VoucherApplyType.ALL &&
                        v.getVoucherDetails().stream()
                                .anyMatch(vd -> vd.getStartDate().isBefore(now) && vd.getEndDate().isAfter(now)))
                .toList();

        // Food-level voucher details
        List<VoucherDetail> validVoucherDetails = voucherDetailService
                .getVoucherDetailByVoucherInAndFoodInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        allVouchers, mainFoods, now);

        Map<Long, VoucherDetail> foodIdToVoucherDetail = validVoucherDetails.stream()
                .collect(Collectors.toMap(
                        vd -> vd.getFood().getId(),
                        vd -> vd,
                        (vd1, vd2) -> vd1 // keep first
                ));

        List<CartDetailResponse> cartDetailResponseList = cartDetails.stream().map(cartDetail -> {
            Food food = cartDetail.getFood();
            BigDecimal basePrice = foodService.getCurrentPrice(food.getId());
            BigDecimal finalPrice = applyVouchers(basePrice, food, validRestaurantVouchers, foodIdToVoucherDetail);

            // Handle additional foods
            List<AdditionalFoodCartResponse> additionalItems = new ArrayList<>();
            List<Long> validAdditionalIds = new ArrayList<>();

            for (Long id : cartDetail.getIds()) {
                Optional<Food> aFoodOpt = foodRepository.findById(id);
                if (aFoodOpt.isPresent()) {
                    Food aFood = aFoodOpt.get();
                    if (aFood.getStatus() == FoodStatus.ACTIVE) {
                        BigDecimal aBasePrice = foodService.getCurrentPrice(aFood.getId());
                        BigDecimal aFinalPrice = applyVouchers(aBasePrice, aFood, validRestaurantVouchers,
                                foodIdToVoucherDetail);

                        additionalItems.add(AdditionalFoodCartResponse.builder()
                                .id(aFood.getId())
                                .name(aFood.getName())
                                .price(aFinalPrice)
                                .build());

                        validAdditionalIds.add(id);
                    }
                }
            }

            cartDetail.setIds(validAdditionalIds);
            cartDetailRepository.save(cartDetail);

            return CartDetailResponse.builder()
                    .id(cartDetail.getId())
                    .foodName(food.getName())
                    .price(finalPrice)
                    .foodId(food.getId())
                    .note(cartDetail.getNote())
                    .quantity(cartDetail.getQuantity())
                    .food_img(food.getImage())
                    .additionFoods(additionalItems)
                    .build();
        }).toList();

        return CartResponse.builder()
                .restaurantId(restaurantId)
                .cartId(cart.getId())
                .listItem(cartDetailResponseList)
                .restaurantName(mainFoods.get(0).getRestaurant().getName())
                .build();
    }

    @Override
    public void deleteCartDetail(Long cartDetailId) {
        cartDetailRepository.deleteById(cartDetailId);
    }

    @Override
    public boolean checkRestaurantOpen(long cartId) {
        List<CartDetail> cartDetails = cartDetailRepository.findByCartIdAndOrderIsNull(cartId);
        boolean isRestaurantOpen = false;
        if (!cartDetails.isEmpty()) {
            Restaurant restaurant = cartDetails.getFirst().getFood().getRestaurant();
            isRestaurantOpen = restaurant.getOpeningHour().isBefore(LocalTime.now())
                    && restaurant.getClosingHour().isAfter(LocalTime.now());
        }
        return isRestaurantOpen;
    }

    private BigDecimal applyVouchers(BigDecimal originalPrice, Food food,
            List<Voucher> restaurantVouchers,
            Map<Long, VoucherDetail> foodIdToVoucherDetail) {
        if (originalPrice == null)
            return BigDecimal.ZERO;

        BigDecimal price = originalPrice;

        // Apply restaurant-level voucher
        for (Voucher voucher : restaurantVouchers) {
            if (voucher.getRestaurant().equals(food.getRestaurant())) {
                price = applySingleVoucher(price, voucher);
            }
        }

        // Apply food-level voucher
        VoucherDetail foodVoucher = foodIdToVoucherDetail.get(food.getId());
        if (foodVoucher != null) {
            price = applySingleVoucher(price, foodVoucher.getVoucher());
        }

        return price.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : price;
    }

    private BigDecimal applySingleVoucher(BigDecimal price, Voucher voucher) {
        if (voucher.getType() == VoucherType.FIXED) {
            return price.subtract(voucher.getValue());
        } else if (voucher.getType() == VoucherType.PERCENTAGE) {
            BigDecimal discount = price.multiply(voucher.getValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            return price.subtract(discount);
        }
        return price;
    }

}
