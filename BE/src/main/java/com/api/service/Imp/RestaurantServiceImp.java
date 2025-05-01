package com.api.service.Imp;

import com.api.dto.request.AddRestaurantRequest;
import com.api.dto.request.AddressRequest;
import com.api.dto.response.GetFoodResponse;
import com.api.dto.response.PageResponse;
import com.api.dto.response.RestaurantResponse;
import com.api.entity.Address;
import com.api.entity.Order;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.entity.Restaurant;
import com.api.repository.OrderRepository;
import com.api.repository.RestaurantRepository;
import com.api.service.*;
import com.api.utils.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

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

        long accountId = accountService.addNewAccount(request.getUsername(), request.getPassword());
        long addressId = addressService.addNewAddress(request.getAddress());

        log.info("Add Restaurant address and account");
        newRestaurant.setAccount(accountService.getAccountById(accountId));
        newRestaurant.setAddress(addressService.getAddressById(addressId));
        newRestaurant.setStatus(RestaurantStatus.ACTIVE);

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
    public void handlePendingOrder(long restaurantId, long orderId, OrderStatus status) {
        log.info("Handle PendingOrder");
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        if (order.getCartDetails().get(0).getFood().getRestaurant().getId() != restaurantId) {
            log.error("Order {} not belong to restaurant {}", orderId, restaurantId);
            throw new AppException(ErrorCode.ORDER_NOT_BELONG_TO_RES);
        }
        if (!order.getStatus().equals(OrderStatus.PENDING)) {
            log.error("Order id {} is not PENDING", orderId);
            throw new AppException(ErrorCode.HANDLE_NOT_PENDING_ORDER);
        }
        if (status.equals(OrderStatus.PROCESSING) || status.equals(OrderStatus.REJECTED)) {
            log.info("Change order {} status from PENDING to {}", orderId, status);
            order.setStatus(status);
        }
        orderRepository.save(order);
    }

    @Override
    public List<RestaurantResponse> getRestaurants(String sortBy, double userLat, double userLon) {
        List<RestaurantResponse> content = restaurantRepository
                .findAll(Sort.by(Sort.Direction.ASC, sortBy))
                .stream()
                .filter(r -> r.getStatus().equals(RestaurantStatus.ACTIVE))
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
            var distance = locationService.getDistance(userLat, userLon,
                    restaurant.getAddress().getLat(), restaurant.getAddress().getLon());
            double distanceInMeters = distance.getDistance();
            String formattedDistance;

            if (distanceInMeters < 1000) {
                formattedDistance = (int) distanceInMeters + " m";
            } else {
                double distanceInKm = distanceInMeters / 1000;
                formattedDistance = String.format("%.1f km", distanceInKm);
            }

            response.setDistance(formattedDistance);
            response.setTimeDistance(
                    TimeUtil.formatDurationFromSeconds(distance.getDuration())
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
        return sj.add(address.getWard())
                .add(address.getDistrict())
                .add(address.getProvince())
                .toString();
    }
}
