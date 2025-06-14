package com.api.service;

import com.api.dto.request.AddRestaurantRequest;
import com.api.dto.request.AddressRequest;
import com.api.dto.request.UpdateRestaurantRequest;
import com.api.dto.response.RestaurantResponse;
import com.api.entity.*;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.repository.AddressRepository;
import com.api.repository.OrderRepository;
import com.api.repository.RestaurantRepository;
import com.api.repository.RoleRepository;
import com.api.service.Imp.RestaurantServiceImp;
import com.api.utils.OrderStatus;
import com.api.utils.RestaurantStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private AccountService accountService;
    @Mock
    private AddressService addressService;
    @Mock
    private ReviewService reviewService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private EmailService emailService;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private OrderAssignmentService orderAssignmentService;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RestaurantServiceImp restaurantService;

    private Restaurant testRestaurant;
    private Address testAddress;
    private AddRestaurantRequest addRestaurantRequest;
    private UpdateRestaurantRequest updateRestaurantRequest;
    private Order testOrder;
    private Account testAccount;
    private Role testRole;

    @BeforeEach
    void setUp() {
        // Setup test data
        testAddress = Address.builder()
                .detail("123 Test Street")
                .ward("Test Ward")
                .district("Test District")
                .province("Test Province")
                .lat(10.762622)
                .lon(106.660172)
                .build();
        testAddress.setId(1L);

        testRestaurant = Restaurant.builder()
                .name("Test Restaurant")
                .image("test-image.jpg")
                .phone("0123456789")
                .email("test@restaurant.com")
                .openingHour(LocalTime.of(8, 0))
                .closingHour(LocalTime.of(22, 0))
                .description("Test description")
                .address(testAddress)
                .status(RestaurantStatus.ACTIVE)
                .vouchers(new ArrayList<>())
                .build();
        testRestaurant.setId(1L);

        AddressRequest addressRequest = AddressRequest.builder()
                .detail("123 Test Street")
                .ward("Test Ward")
                .district("Test District")
                .province("Test Province")
                .latitude(10.762622)
                .longitude(106.660172)
                .build();

        addRestaurantRequest = AddRestaurantRequest.builder()
                .name("Test Restaurant")
                .image("test-image.jpg")
                .phone("0123456789")
                .email("test@restaurant.com")
                .openingHour(LocalTime.of(8, 0))
                .closingHour(LocalTime.of(22, 0))
                .description("Test description")
                .address(addressRequest)
                .build();

        updateRestaurantRequest = UpdateRestaurantRequest.builder()
                .name("Updated Restaurant")
                .description("Updated description")
                .build();

        testAccount = Account.builder()
                .username("testuser")
                .password("encodedPassword")
                .build();
        testAccount.setId(1L);

        testRole = Role.builder()
                .roleName("ROLE_RES")
                .build();
        testRole.setId(1L);

        // Setup test order
        User testUser = User.builder()
                .account(testAccount)
                .build();
        testUser.setId(1L);

        Food testFood = Food.builder()
                .restaurant(testRestaurant)
                .build();
        testFood.setId(1L);

        CartDetail cartDetail = CartDetail.builder()
                .food(testFood)
                .quantity(2)
                .build();
        cartDetail.setId(1L);

        testOrder = Order.builder()
                .user(testUser)
                .status(OrderStatus.PENDING)
                .cartDetails(Arrays.asList(cartDetail))
                .build();
        testOrder.setId(1L);
    }

    @Test
    void addRestaurant_ShouldReturnRestaurantId_WhenValidRequest() {
        // Given
        when(addressService.addNewAddress(any(AddressRequest.class))).thenReturn(1L);
        when(addressService.getAddressById(1L)).thenReturn(testAddress);
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(testRestaurant);

        // When
        long result = restaurantService.addRestaurant(addRestaurantRequest);

        // Then
        assertEquals(1L, result);
        verify(addressService).addNewAddress(any(AddressRequest.class));
        verify(addressService).getAddressById(1L);
        verify(restaurantRepository).save(any(Restaurant.class));
    }

    @Test
    void getRestaurant_ShouldReturnRestaurant_WhenRestaurantExists() {
        // Given
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(testRestaurant));

        // When
        Restaurant result = restaurantService.getRestaurant(1L);

        // Then
        assertNotNull(result);
        assertEquals(testRestaurant.getId(), result.getId());
        assertEquals(testRestaurant.getName(), result.getName());
        verify(restaurantRepository).findById(1L);
    }

    @Test
    void getRestaurant_ShouldThrowException_WhenRestaurantNotExists() {
        // Given
        when(restaurantRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> restaurantService.getRestaurant(1L));
        assertEquals(ErrorCode.RESTAURANT_NOT_FOUND, exception.getErrorCode());
        verify(restaurantRepository).findById(1L);
    }

    @Test
    void getRestaurantResponse_ShouldReturnRestaurantResponse_WhenValidInput() {
        // Given
        double userLat = 10.762622;
        double userLon = 106.660172;
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(testRestaurant));
        when(reviewService.calculateAvgRating(1L)).thenReturn(BigDecimal.valueOf(4.5));

        // When
        RestaurantResponse result = restaurantService.getRestaurantResponse(1L, userLat, userLon);

        // Then
        assertNotNull(result);
        assertEquals(testRestaurant.getId(), result.getId());
        assertEquals(testRestaurant.getName(), result.getName());
        assertEquals(BigDecimal.valueOf(4.5), result.getRating());
        assertNotNull(result.getDistance());
        verify(restaurantRepository).findById(1L);
        verify(reviewService).calculateAvgRating(1L);
    }

    @Test
    void handleOrder_ShouldUpdateOrderStatus_WhenValidProcessingStatus() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        restaurantService.handleOrder(1L, 1L, OrderStatus.PROCESSING);

        // Then
        assertEquals(OrderStatus.PROCESSING, testOrder.getStatus());
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(testOrder);
        verify(notificationService).createNewNotification(any(), any(), any(), any());
        verify(notificationService).sendUserNotificationWhenOrderStatusChanged(1L);
    }

    @Test
    void handleOrder_ShouldAssignShipper_WhenStatusIsReadyForPickup() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        restaurantService.handleOrder(1L, 1L, OrderStatus.READY_FOR_PICKUP);

        // Then
        assertEquals(OrderStatus.READY_FOR_PICKUP, testOrder.getStatus());
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(testOrder);
        verify(orderAssignmentService).assignOrderToOptimalShipper(1L);
    }

    @Test
    void handleOrder_ShouldThrowException_WhenOrderNotBelongToRestaurant() {
        // Given
        Restaurant anotherRestaurant = new Restaurant();
        anotherRestaurant.setId(2L);
        testOrder.getCartDetails().get(0).getFood().setRestaurant(anotherRestaurant);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> restaurantService.handleOrder(1L, 1L, OrderStatus.PROCESSING));
        assertEquals(ErrorCode.ORDER_NOT_BELONG_TO_RES, exception.getErrorCode());
    }

    @Test
    void handleOrder_ShouldThrowException_WhenOrderNotFound() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> restaurantService.handleOrder(1L, 1L, OrderStatus.PROCESSING));
        assertEquals(ErrorCode.ORDER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void updateRestaurantInfo_ShouldUpdateRestaurant_WhenValidRequest() {
        // Given
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(testRestaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(testRestaurant);

        // When
        restaurantService.updateRestaurantInfo(1L, updateRestaurantRequest);

        // Then
        assertEquals("Updated Restaurant", testRestaurant.getName());
        assertEquals("Updated description", testRestaurant.getDescription());
        verify(restaurantRepository).findById(1L);
        verify(restaurantRepository).save(testRestaurant);
    }

    @Test
    void updateRestaurantInfo_ShouldUpdateAddress_WhenAddressProvided() {
        // Given
        AddressRequest newAddressRequest = AddressRequest.builder()
                .detail("456 New Street")
                .ward("New Ward")
                .district("New District")
                .province("New Province")
                .latitude(10.123456)
                .longitude(106.123456)
                .build();
        updateRestaurantRequest.setAddress(newAddressRequest);

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(testRestaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(testRestaurant);
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);

        // When
        restaurantService.updateRestaurantInfo(1L, updateRestaurantRequest);

        // Then
        verify(addressRepository).save(any(Address.class));
        verify(restaurantRepository).save(testRestaurant);
    }

    @Test
    void approveRestaurant_ShouldCreateAccountAndSetActive_WhenValidRestaurant() {
        // Given
        testRestaurant.setStatus(RestaurantStatus.PENDING);
        testRestaurant.setAccount(null);

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(testRestaurant));
        when(roleRepository.findByRoleName("ROLE_RES")).thenReturn(testRole);
        when(passwordEncoder.encode("123")).thenReturn("encodedPassword");
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(testRestaurant);

        // When
        restaurantService.approveRestaurant(1L);

        // Then
        assertEquals(RestaurantStatus.ACTIVE, testRestaurant.getStatus());
        assertNotNull(testRestaurant.getAccount());
        assertEquals(testRestaurant.getPhone(), testRestaurant.getAccount().getUsername());
        verify(restaurantRepository).findById(1L);
        verify(roleRepository).findByRoleName("ROLE_RES");
        verify(passwordEncoder).encode("123");
        verify(restaurantRepository).save(testRestaurant);
        verify(emailService).sendRestaurantAccountInfo(testRestaurant.getEmail(),
                testRestaurant.getPhone(), "123");
    }

    @Test
    void rejectRestaurant_ShouldSetRejectedStatus_WhenValidRestaurant() {
        // Given
        testRestaurant.setStatus(RestaurantStatus.PENDING);
        testRestaurant.setAccount(null);

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(testRestaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(testRestaurant);

        // When
        restaurantService.rejectRestaurant(1L);

        // Then
        assertEquals(RestaurantStatus.REJECTED, testRestaurant.getStatus());
        verify(restaurantRepository).findById(1L);
        verify(restaurantRepository).save(testRestaurant);
    }

    @Test
    void rejectRestaurant_ShouldThrowException_WhenRestaurantAlreadyActive() {
        // Given
        testRestaurant.setStatus(RestaurantStatus.ACTIVE);
        testRestaurant.setAccount(testAccount);

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(testRestaurant));

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> restaurantService.rejectRestaurant(1L));
        assertEquals(ErrorCode.RESTAURANT_ALREADY_ACTIVE, exception.getErrorCode());
    }

    @Test
    void setRestaurantStatus_ShouldSetActiveStatus_WhenValidInput() {
        // Given
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(testRestaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(testRestaurant);

        // When
        restaurantService.setRestaurantStatus(1L, "ACTIVE");

        // Then
        assertEquals(RestaurantStatus.ACTIVE, testRestaurant.getStatus());
        verify(restaurantRepository).save(testRestaurant);
    }

    @Test
    void setRestaurantStatus_ShouldSetInactiveStatus_WhenValidInput() {
        // Given
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(testRestaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(testRestaurant);

        // When
        restaurantService.setRestaurantStatus(1L, "INACTIVE");

        // Then
        assertEquals(RestaurantStatus.INACTIVE, testRestaurant.getStatus());
        verify(restaurantRepository).save(testRestaurant);
    }

    @Test
    void setRestaurantStatus_ShouldThrowException_WhenInvalidStatus() {
        // Given
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(testRestaurant));

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> restaurantService.setRestaurantStatus(1L, "INVALID_STATUS"));
        assertEquals(ErrorCode.INVALID_STATUS, exception.getErrorCode());
    }

    @Test
    void searchRestaurants_ShouldReturnActiveRestaurants_WhenForCustomer() {
        // Given
        List<Restaurant> restaurants = Arrays.asList(testRestaurant);
        when(restaurantRepository.findByNameContainingIgnoreCaseAndStatus("test", RestaurantStatus.ACTIVE))
                .thenReturn(restaurants);
        when(reviewService.calculateAvgRating(1L)).thenReturn(BigDecimal.valueOf(4.5));

        // When
        List<RestaurantResponse> result = restaurantService.searchRestaurants("test", true);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testRestaurant.getName(), result.get(0).getName());
        verify(restaurantRepository).findByNameContainingIgnoreCaseAndStatus("test", RestaurantStatus.ACTIVE);
    }

    @Test
    void searchRestaurants_ShouldReturnAllRestaurants_WhenNotForCustomer() {
        // Given
        List<Restaurant> restaurants = Arrays.asList(testRestaurant);
        when(restaurantRepository.findByNameContainingIgnoreCase("test")).thenReturn(restaurants);
        when(reviewService.calculateAvgRating(1L)).thenReturn(BigDecimal.valueOf(4.5));

        // When
        List<RestaurantResponse> result = restaurantService.searchRestaurants("test", false);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(restaurantRepository).findByNameContainingIgnoreCase("test");
    }

    @Test
    void getPendingRestaurants_ShouldReturnPendingRestaurants() {
        // Given
        testRestaurant.setStatus(RestaurantStatus.PENDING);
        List<Restaurant> pendingRestaurants = Arrays.asList(testRestaurant);
        when(restaurantRepository.findAllByStatus(RestaurantStatus.PENDING)).thenReturn(pendingRestaurants);
        when(reviewService.calculateAvgRating(1L)).thenReturn(BigDecimal.valueOf(4.5));

        // When
        List<RestaurantResponse> result = restaurantService.getPendingRestaurants();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testRestaurant.getName(), result.get(0).getName());
        verify(restaurantRepository).findAllByStatus(RestaurantStatus.PENDING);
    }

    @Test
    void getRestaurantByUsername_ShouldReturnRestaurantId_WhenValidUsername() {
        // Given
        testRestaurant.setAccount(testAccount);
        when(accountService.getAccountByUsername("testuser")).thenReturn(testAccount);
        when(restaurantRepository.findByAccount(testAccount)).thenReturn(Optional.of(testRestaurant));

        // When
        Long result = restaurantService.getRestaurantByUsername("testuser");

        // Then
        assertEquals(1L, result);
        verify(accountService).getAccountByUsername("testuser");
        verify(restaurantRepository).findByAccount(testAccount);
    }

    @Test
    void getRestaurantByUsername_ShouldThrowException_WhenAccountNotFound() {
        // Given
        when(accountService.getAccountByUsername("testuser")).thenReturn(null);

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> restaurantService.getRestaurantByUsername("testuser"));
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void getRestaurantByUsername_ShouldThrowException_WhenRestaurantNotFound() {
        // Given
        when(accountService.getAccountByUsername("testuser")).thenReturn(testAccount);
        when(restaurantRepository.findByAccount(testAccount)).thenReturn(Optional.empty());

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> restaurantService.getRestaurantByUsername("testuser"));
        assertEquals(ErrorCode.RESTAURANT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void getAllRestaurants_ShouldReturnAllRestaurants() {
        // Given
        List<Restaurant> restaurants = Arrays.asList(testRestaurant);
        when(restaurantRepository.findAll()).thenReturn(restaurants);
        when(reviewService.calculateAvgRating(1L)).thenReturn(BigDecimal.valueOf(4.5));

        // When
        List<RestaurantResponse> result = restaurantService.getAllRestaurants();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testRestaurant.getName(), result.get(0).getName());
        verify(restaurantRepository).findAll();
    }

    @Test
    void getRestaurants_ShouldReturnActiveRestaurants_WhenNoLocationProvided() {
        // Given
        List<Restaurant> restaurants = Arrays.asList(testRestaurant);
        when(restaurantRepository.findAll(Sort.by(Sort.Direction.ASC, "name"))).thenReturn(restaurants);
        when(reviewService.calculateAvgRating(1L)).thenReturn(BigDecimal.valueOf(4.5));

        // When
        List<RestaurantResponse> result = restaurantService.getRestaurants("name", -1, -1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testRestaurant.getName(), result.get(0).getName());
        verify(restaurantRepository).findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    @Test
    void getNearbyRestaurants_ShouldReturnNearbyActiveRestaurants() {
        // Given
        double userLat = 10.762622;
        double userLon = 106.660172;
        double radiusKm = 5.0;

        List<Restaurant> restaurants = Arrays.asList(testRestaurant);
        when(restaurantRepository.findNearbyRestaurants(anyDouble(), anyDouble(),
                anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(restaurants);
        when(reviewService.calculateAvgRating(1L)).thenReturn(BigDecimal.valueOf(4.5));

        // When
        List<RestaurantResponse> result = restaurantService.getNearbyRestaurants(userLat, userLon, radiusKm);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testRestaurant.getName(), result.get(0).getName());
        assertNotNull(result.get(0).getDistance());
        verify(restaurantRepository).findNearbyRestaurants(anyDouble(), anyDouble(),
                anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    void getNearbyRestaurants_ShouldReturnAllRestaurants_WhenNoNearbyFound() {
        // Given
        double userLat = 10.762622;
        double userLon = 106.660172;
        double radiusKm = 5.0;

        when(restaurantRepository.findNearbyRestaurants(anyDouble(), anyDouble(),
                anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(new ArrayList<>());
        when(restaurantRepository.findAll()).thenReturn(Arrays.asList(testRestaurant));
        when(reviewService.calculateAvgRating(1L)).thenReturn(BigDecimal.valueOf(4.5));

        // When
        List<RestaurantResponse> result = restaurantService.getNearbyRestaurants(userLat, userLon, radiusKm);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(restaurantRepository).findNearbyRestaurants(anyDouble(), anyDouble(),
                anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyDouble());
        verify(restaurantRepository).findAll();
    }
}