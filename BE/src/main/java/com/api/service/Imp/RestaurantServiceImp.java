package com.api.service.Imp;

import com.api.dto.request.AddRestaurantRequest;
import com.api.dto.request.AddressRequest;
import com.api.dto.request.UpdateRestaurantRequest;
import com.api.dto.response.GetFoodResponse;
import com.api.dto.response.PageResponse;
import com.api.dto.response.RestaurantResponse;
import com.api.entity.*;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.repository.AddressRepository;
import com.api.repository.OrderRepository;
import com.api.repository.RestaurantRepository;
import com.api.repository.RoleRepository;
import com.api.service.*;
import com.api.service.strategy.OrderNotificationStrategyFactory;
import com.api.service.strategy.OrderStatusNotificationStrategy;
import com.api.utils.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantServiceImp implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final AccountService accountService;
    private final AddressService addressService;
    private final LocationService locationService;
    private final ReviewService reviewService;
    private final OrderRepository orderRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final RoleRepository roleRepository;

    private static final double MOVING_SPEED_PER_HOUR = 50;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public long addRestaurant(AddRestaurantRequest request) {
        log.info("Add Restaurant");
        Restaurant newRestaurant = Restaurant.builder()
                .name(request.getName())
                .image(request.getImage())
                .phone(request.getPhone())
                .email(request.getEmail())
                .openingHour(request.getOpeningHour())
                .closingHour(request.getClosingHour())
                .description(request.getDescription())
                .build();

//        long accountId = accountService.addNewAccount(request.getUsername(), request.getPassword());
        long addressId = addressService.addNewAddress(request.getAddress());

        log.info("Add Restaurant address");
//        newRestaurant.setAccount(accountService.getAccountById(accountId));
        newRestaurant.setAddress(addressService.getAddressById(addressId));
        newRestaurant.setStatus(RestaurantStatus.PENDING);

        log.info("Persist Restaurant");
        return restaurantRepository.save(newRestaurant).getId();
    }

    @Override
    public Restaurant getRestaurant(long id) {
        log.info("Get Restaurant: {}", id);
        return restaurantRepository.findById(id).orElseThrow( () -> {
            log.error("Restaurant id {} not found", id);
            return new AppException(ErrorCode.RESTAURANT_NOT_FOUND);
        });
    }

    @Override
    public RestaurantResponse getRestaurantResponse(long id, double userLat, double userLon) {
        log.info("Get Restaurant response: {}", id);
        Restaurant restaurant = getRestaurant(id);
        RestaurantResponse restaurantResponse = RestaurantResponse.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .address(getAddressText(restaurant.getAddress()))
                .image(restaurant.getImage())
                .description(restaurant.getDescription())
                .openingHour(restaurant.getOpeningHour())
                .closingHour(restaurant.getClosingHour())
                .phone(restaurant.getPhone())
                .email(restaurant.getEmail())
                .rating(reviewService.calculateAvgRating(restaurant.getId()))
                .build();
        return addDistance(userLat, userLon, restaurant, restaurantResponse);
    }

    @Override
    public PageResponse<List<RestaurantResponse>> getRestaurantsForAdmin(String sortBy, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sortBy));

        Page<Restaurant> restaurantPage = restaurantRepository.findAll(pageable);

        List<RestaurantResponse> content = restaurantPage.getContent().stream()
                .filter(r -> r.getStatus().equals(RestaurantStatus.ACTIVE))
                .map(restaurant -> RestaurantResponse.builder()
                        .id(restaurant.getId())
                        .name(restaurant.getName())
                        .description(restaurant.getDescription())
                        .image(restaurant.getImage())
                        .restaurantVouchersInfo(restaurantVouchersInfo(restaurant))
                        .rating(reviewService.calculateAvgRating(restaurant.getId()))
                        .build())
                .toList();

        return PageResponse.<List<RestaurantResponse>>builder()
                .page(page)
                .size(pageSize)
                .total(restaurantPage.getTotalElements())
                .items(content)
                .build();
    }

    @Override
    public void handleOrder(long restaurantId, long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        if (order.getCartDetails().get(0).getFood().getRestaurant().getId() != restaurantId) {
            log.error("Order {} not belong to restaurant {}", orderId, restaurantId);
            throw new AppException(ErrorCode.ORDER_NOT_BELONG_TO_RES);
        }
        if (!order.getStatus().equals(OrderStatus.PENDING) && !order.getStatus().equals(OrderStatus.PROCESSING)) {
            log.error("Restaurant can not handle order {} with status", orderId, order.getStatus());
            throw new AppException(ErrorCode.HANDLE_NOT_PENDING_ORDER);
        }
        if (status.equals(OrderStatus.PROCESSING) || status.equals(OrderStatus.REJECTED)) {
            log.info("Change order {} status from PENDING to {}", orderId, status);
            order.setStatus(status);
        } else if (status.equals(OrderStatus.SHIPPING)) {
//            flow can be changed when app has function that delivery guy can approve order or not
//            algorithm for find delivery guy
//            Shipper shipper = shipperRepository.findById(1L);
//            assign order to shipper
//            shipper.getOrders().add(order);
//            order.shipper(shipper);
//            shipperRepository.save(shipper);
//            send notification to delivery guy
//            sendNotifyToDeliveryWhenResChangeOrderToShipping(shipper, order);
            log.info("Change order {} status from PROCESSING to {}", orderId, status);
            order.setStatus(status);
        }
        orderRepository.save(order);
        // tạo thông báo push tại đây đến /topic/client/{userId}
        sendNotifyToUserWhenResChangeOrderStatus(order);
    }

    @Override
    @Transactional
    public void updateRestaurantInfo(long restaurantId, UpdateRestaurantRequest request) {
        log.info("Update Restaurant info: {}", request);

        Restaurant restaurant = getRestaurant(restaurantId);
        log.error("{}", request.getAddress() != null);
        if (request.getName() != null) restaurant.setName(request.getName());
        if (request.getImage() != null && !request.getImage().isBlank()) restaurant.setImage(request.getImage());
        if (request.getDescription() != null) restaurant.setDescription(request.getDescription());
        if (request.getOpeningHour() != null) restaurant.setOpeningHour(request.getOpeningHour());
        if (request.getClosingHour() != null) restaurant.setClosingHour(request.getClosingHour());
        if (request.getPhone() != null) restaurant.setPhone(request.getPhone());
        if (request.getAddress() != null) {
            Address address = restaurant.getAddress();
            address.setDefault(false);
            address.setLon(request.getAddress().getLongitude());
            address.setLat(request.getAddress().getLatitude());
            address.setWard(request.getAddress().getWard());
            address.setDistrict(request.getAddress().getDistrict());
            address.setProvince(request.getAddress().getProvince());
            address.setDetail(request.getAddress().getDetail());
            addressRepository.save(address);
        }
        restaurantRepository.save(restaurant);
    }

    @Override
    @Transactional
    public void approveRestaurant(long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + restaurantId));

        // Change status to ACTIVE
        restaurant.setStatus(RestaurantStatus.ACTIVE);

        // Create user account for restaurant with phone number as username
        String username = restaurant.getPhone();
        String password = "123";
        Role role = roleRepository.findByRoleName("ROLE_RES");
        Account account = Account.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build();
        restaurant.setAccount(account);

        restaurantRepository.save(restaurant);

        // Send email with account information
        emailService.sendRestaurantAccountInfo(restaurant.getEmail(), username, password);

        log.info("Restaurant approved and account created for restaurant ID: {}", restaurantId);
    }

    @Override
    public void rejectRestaurant(long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + restaurantId));
    // Check if restaurant is already active and has an account
        if ((restaurant.getStatus() == RestaurantStatus.ACTIVE || restaurant.getStatus() == RestaurantStatus.INACTIVE) && restaurant.getAccount() != null) {
            log.error("Cannot reject restaurant with ID: {} because it's already active with an account", restaurantId);
            throw new AppException(ErrorCode.RESTAURANT_ALREADY_ACTIVE);
        }
        restaurant.setStatus(RestaurantStatus.REJECTED);
        restaurantRepository.save(restaurant);

        log.info("Restaurant rejected with ID: {}", restaurantId);
    }
    @Override
    public void setRestaurantStatus(long restaurantId, String status) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new AppException(ErrorCode.RESTAURANT_NOT_FOUND));
        if ("ACTIVE".equalsIgnoreCase(status)) {
            restaurant.setStatus(RestaurantStatus.ACTIVE);
        } else if ("INACTIVE".equalsIgnoreCase(status)) {
            restaurant.setStatus(RestaurantStatus.INACTIVE);
        } else {
            throw new AppException(ErrorCode.INVALID_STATUS);
        }
        restaurantRepository.save(restaurant);
    }

    @Override
    public List<RestaurantResponse> searchRestaurants(String query, boolean isForCustomer) {
        List<Restaurant> restaurants;
        if (isForCustomer) {
            restaurants = restaurantRepository.findByNameContainingIgnoreCaseAndStatus(query, RestaurantStatus.ACTIVE);
        } else {
            restaurants = restaurantRepository.findByNameContainingIgnoreCase(query);
        }

        return restaurants.stream()
                .map(this::mapToRestaurantResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RestaurantResponse> getPendingRestaurants() {
        List<Restaurant> pendingRestaurants = restaurantRepository.findAllByStatus(RestaurantStatus.PENDING);
        return pendingRestaurants.stream()
                .map(this::mapToRestaurantResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Long getRestaurantByUsername(String username) {
        log.info("Get restaurant by username: {}", username);
        Account account = accountService.getAccountByUsername(username);
        if (account == null) {
            log.error("Account not found with username: {}", username);
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        Restaurant restaurant = restaurantRepository.findByAccount(account)
                .orElseThrow(() -> new AppException(ErrorCode.RESTAURANT_NOT_FOUND));
        return restaurant.getId();
    }

    @Override
    public List<RestaurantResponse> getAllRestaurants() {
        return restaurantRepository.findAll()
                .stream()
                .map(this::mapToRestaurantResponse)
                .collect(Collectors.toList());
    }


    private void sendNotifyToUserWhenResChangeOrderStatus(Order order) {
        var user = order.getUser();
        long userId = user.getId();
        Account account = user.getAccount();
        // create notification here
        OrderStatusNotificationStrategy strategy = OrderNotificationStrategyFactory.getStrategy(order.getStatus());
        String subject = strategy.getSubject(order);
        String body = strategy.getBody(order);
        notificationService.createNewNotification(account, subject, body, NotificationType.ORDER_STATUS_CHANGED);

        notificationService.sendUserNotificationWhenOrderStatusChanged(userId);
        log.info("send order notify to user {}", userId);
    }

//    private void sendNotifyToDeliveryWhenResChangeOrderToShipping(Shipper shipper, Order order) {
//        Account account = shipper.getAccount();
//        // create notification here
//        Restaurant restaurant = order.getCartDetails().getFirst().getFood().getRestaurant();
//        String subject = "Có đơn hàng tại mới";
//        String body = "Nhận tại nhà hàng " + restaurant.getName() + ".\n" +
//                "Từ " + getAddressText(restaurant.getAddress()) + " đến " + order.getAddress();
//        notificationService.createNewNotification(account, subject, body, NotificationType.NEW_ORDER_TO_SHIPPING);
//
//        notificationService.sendDeliveryGuyNotificationWhenOrderStatusChanged(shipper.getId());
//        log.info("send order notify to delivery guy id {}", shipper.getId());
//    }

    @Override
    public List<RestaurantResponse> getRestaurants(String sortBy, double userLat, double userLon) {
        List<RestaurantResponse> content = restaurantRepository
                .findAll(Sort.by(Sort.Direction.ASC, sortBy))
                .stream()
                .filter(r -> {
                    if (userLat != -1 && userLon != -1) {
                        log.info("user lat: {} lon: {}", userLat, userLon);
                        double distance = GeoUtils.haversine(userLat, userLon,
                                r.getAddress().getLat(), r.getAddress().getLon());
                        log.info("distance: {}", distance);
                        return r.getStatus().equals(RestaurantStatus.ACTIVE) && distance <= 8;
                    }
                    return r.getStatus().equals(RestaurantStatus.ACTIVE);
                })
                .map(restaurant -> {
                    RestaurantResponse response = RestaurantResponse.builder()
                            .id(restaurant.getId())
                            .name(restaurant.getName())
                            .description(restaurant.getDescription())
                            .image(restaurant.getImage())
                            .address(getAddressText(restaurant.getAddress()))
                            .openingHour(restaurant.getOpeningHour())
                            .closingHour(restaurant.getClosingHour())
                            .phone(restaurant.getPhone())
                            .restaurantVouchersInfo(restaurantVouchersInfo(restaurant))
                            .rating(reviewService.calculateAvgRating(restaurant.getId()))
                            .build();
                    return addDistance(userLat, userLon, restaurant, response);
                })
                .toList();
        return content;
    }

    private RestaurantResponse addDistance(double userLat, double userLon, Restaurant restaurant, RestaurantResponse response) {
        if (userLat != -1 && userLon != -1) {
            var distance = GeoUtils.haversine(userLat, userLon,
                    restaurant.getAddress().getLat(), restaurant.getAddress().getLon());
            double distanceInMeters = distance*1000;
            String formattedDistance;

            if (distanceInMeters < 1000) {
                formattedDistance = (int) distanceInMeters + " m";
            } else {
                double distanceInKm = distanceInMeters / 1000;
                formattedDistance = String.format("%.1f km", distanceInKm);
            }

            response.setDistance(formattedDistance);
            response.setTimeDistance(
                    TimeUtil.formatDurationFromSeconds(GeoUtils.estimateTravelTime(distance, MOVING_SPEED_PER_HOUR) * 3600)
            );
        }
        return response;
    }

    private List<String> restaurantVouchersInfo(Restaurant restaurant) {
        List<String> result = new ArrayList<>();
        log.info("Get voucher of restaurant {}", restaurant.getId());

        LocalDateTime now = LocalDateTime.now();

        restaurant.getVouchers().stream()
                .filter(voucher ->
                        voucher.getStatus() == VoucherStatus.ACTIVE &&
                                voucher.getVoucherDetails().stream()
                                        .anyMatch(vd -> now.isAfter(vd.getStartDate()) && now.isBefore(vd.getEndDate()))
                )
                .forEach(voucher -> {
                    StringBuilder builder = new StringBuilder("Giảm ");
                    if (voucher.getType().equals(VoucherType.FIXED)) {
                        builder.append(voucher.getValue().setScale(0)).append("đ");
                    } else if (voucher.getType().equals(VoucherType.PERCENTAGE)) {
                        builder.append(voucher.getValue().setScale(0)).append("%");
                    }
                    result.add(builder.toString());
                });

        return result;
    }


    @Override
    public List<RestaurantResponse> getNearbyRestaurants(double lat, double lon, double radiusKm) {
        double latDiff = radiusKm / 111.0;
        double lonDiff = radiusKm / (111.0 * Math.cos(Math.toRadians(lat)));

        double minLat = lat - latDiff;
        double maxLat = lat + latDiff;
        double minLon = lon - lonDiff;
        double maxLon = lon + lonDiff;

        return restaurantRepository
                .findNearbyRestaurants(lat, lon, radiusKm, minLat, maxLat, minLon, maxLon)
                .stream().filter(r -> r.getStatus().equals(RestaurantStatus.ACTIVE))
                .map(restaurant -> {
                    double distanceInMeters = GeoUtils.haversine(
                            lat, lon,
                            restaurant.getAddress().getLat(),
                            restaurant.getAddress().getLon()
                    );

                    String formattedDistance = TimeUtil.formatDistance(distanceInMeters);
                    return RestaurantResponse.builder()
                            .id(restaurant.getId())
                            .name(restaurant.getName())
                            .description(restaurant.getDescription())
                            .image(restaurant.getImage())
                            .rating(reviewService.calculateAvgRating(restaurant.getId()))
                            .distance(formattedDistance)
                            .build();
                })
                .toList();
    }


    private String getAddressText(Address address) {
        log.info("parse address to text");
        StringJoiner sj = new StringJoiner(", ");
        return sj.add(address.getDetail())
                .add(address.getWard())
                .add(address.getDistrict())
                .add(address.getProvince())
                .toString();
    }
    private RestaurantResponse mapToRestaurantResponse(Restaurant restaurant) {
        // Implement mapping logic from Restaurant entity to RestaurantResponse
        return RestaurantResponse.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .description(restaurant.getDescription())
                .image(restaurant.getImage())
                .rating(reviewService.calculateAvgRating(restaurant.getId()))
                .address(restaurant.getAddress().getDetail() + ", " +
                        restaurant.getAddress().getWard() + ", " +
                        restaurant.getAddress().getDistrict() + ", " +
                        restaurant.getAddress().getProvince())
                .phone(restaurant.getPhone())
                .status(restaurant.getStatus()+"")
                .email(restaurant.getEmail())
                .openingHour(restaurant.getOpeningHour())
                .closingHour(restaurant.getClosingHour())

                // Add other fields as needed
                .build();
    }
}
