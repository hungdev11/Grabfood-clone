package com.api.service.Imp;

import com.api.dto.request.AddRestaurantRequest;
import com.api.dto.request.AddressRequest;
import com.api.dto.response.GetFoodResponse;
import com.api.dto.response.PageResponse;
import com.api.dto.response.RestaurantResponse;
import com.api.entity.Address;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.entity.Restaurant;
import com.api.repository.RestaurantRepository;
import com.api.service.AccountService;
import com.api.service.AddressService;
import com.api.service.LocationService;
import com.api.service.RestaurantService;
import com.api.utils.GeoUtils;
import com.api.utils.RestaurantStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    public RestaurantResponse getRestaurantResponse(long id) {
        log.info("Get Restaurant response: {}", id);
        Restaurant restaurant = getRestaurant(id);
        return RestaurantResponse.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .address(getAddressText(restaurant.getAddress()))
                .image(restaurant.getImage())
                .description(restaurant.getDescription())
                .openingHour(restaurant.getOpeningHour())
                .closingHour(restaurant.getClosingHour())
                .phone(restaurant.getPhone())
                .rating(BigDecimal.ZERO)
                .build();
    }

    @Override
    public PageResponse<List<RestaurantResponse>> getRestaurants(String sortBy, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sortBy));

        Page<Restaurant> restaurantPage = restaurantRepository.findAll(pageable);

        List<RestaurantResponse> content = restaurantPage.getContent().stream()
                .filter(r -> r.getStatus().equals(RestaurantStatus.ACTIVE))
                .map(restaurant -> RestaurantResponse.builder()
                        .id(restaurant.getId())
                        .name(restaurant.getName())
                        .description(restaurant.getDescription())
                        .image(restaurant.getImage())
                        .rating(BigDecimal.ONE.multiply(BigDecimal.valueOf(5)))
                        .distance(GeoUtils.haversine(
                                10.85307, 106.791679,
                                restaurant.getAddress().getLat(), restaurant.getAddress().getLon()))
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
                .map(restaurant -> RestaurantResponse.builder()
                        .id(restaurant.getId())
                        .name(restaurant.getName())
                        .description(restaurant.getDescription())
                        .image(restaurant.getImage())
                        .rating(BigDecimal.ONE.multiply(BigDecimal.valueOf(5)))
                        .distance(GeoUtils.haversine(
                                lat, lon,
                                restaurant.getAddress().getLat(), restaurant.getAddress().getLon()))
                        .build())
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
