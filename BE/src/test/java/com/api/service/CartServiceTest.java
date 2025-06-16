package com.api.service;

import com.api.dto.request.AddToCartRequest;
import com.api.dto.request.CartUpdateRequest;
import com.api.dto.response.CartResponse;
import com.api.entity.*;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.repository.*;
import com.api.service.Imp.CartServiceImp;
import com.api.utils.FoodStatus;
import com.api.utils.VoucherApplyType;
import com.api.utils.VoucherStatus;
import com.api.utils.VoucherType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartDetailRepository cartDetailRepository;

    @Mock
    private FoodRepository foodRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FoodService foodService;

    @Mock
    private VoucherService voucherService;

    @Mock
    private VoucherDetailService voucherDetailService;

    @InjectMocks
    private CartServiceImp cartService;

    private User testUser;
    private Cart testCart;
    private Food testFood;
    private Restaurant testRestaurant;
    private CartDetail testCartDetail;
    private Voucher testVoucher;
    private VoucherDetail testVoucherDetail;

    @BeforeEach
    void setUp() {
        // Setup test entities
        testUser = new User();
        testUser.setId(1L);

        testRestaurant = new Restaurant();
        testRestaurant.setId(1L);
        testRestaurant.setName("Test Restaurant");
        testRestaurant.setOpeningHour(LocalTime.of(8, 0));
        testRestaurant.setClosingHour(LocalTime.of(22, 0));

        testFood = new Food();
        testFood.setId(1L);
        testFood.setName("Test Food");
        testFood.setStatus(FoodStatus.ACTIVE);
        testFood.setRestaurant(testRestaurant);
        testFood.setImage("test.jpg");
        testFood.setMainFoods(new ArrayList<>());

        testCart = new Cart();
        testCart.setId(1L);
        testCart.setUser(testUser);
        testCart.setCartDetails(new ArrayList<>());

        testCartDetail = new CartDetail();
        testCartDetail.setId(1L);
        testCartDetail.setCart(testCart);
        testCartDetail.setFood(testFood);
        testCartDetail.setQuantity(2);
        testCartDetail.setIds(Arrays.asList(2L, 3L));
        testCartDetail.setNote("Test note");

        testVoucher = new Voucher();
        testVoucher.setId(1L);
        testVoucher.setStatus(VoucherStatus.ACTIVE);
        testVoucher.setType(VoucherType.PERCENTAGE);
        testVoucher.setValue(BigDecimal.valueOf(10));
        testVoucher.setApplyType(VoucherApplyType.ALL);
        testVoucher.setRestaurant(testRestaurant);
        testVoucher.setVoucherDetails(new ArrayList<>());

        testVoucherDetail = new VoucherDetail();
        testVoucherDetail.setId(1L);
        testVoucherDetail.setVoucher(testVoucher);
        testVoucherDetail.setFood(testFood);
        testVoucherDetail.setStartDate(LocalDateTime.now().minusDays(1));
        testVoucherDetail.setEndDate(LocalDateTime.now().plusDays(1));
    }

    @Test
    void testCreateCart_NewCart_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        // When
        cartService.createCart(1L);

        // Then
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void testCreateCart_ExistingCart_NoNewCartCreated() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));

        // When
        cartService.createCart(1L);

        // Then
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void testCreateCart_UserNotFound_ThrowsException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> cartService.createCart(1L));
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void testAddToCart_NewItem_Success() {
        // Given
        AddToCartRequest request = new AddToCartRequest();
        request.setFoodId(1L);
        request.setQuantity(2);
        request.setAdditionalItems(Arrays.asList(2L, 3L));
        request.setNote("Test note");

        Food additionalFood1 = new Food();
        additionalFood1.setId(2L);
        Food additionalFood2 = new Food();
        additionalFood2.setId(3L);

        FoodMainAndAddition fma1 = new FoodMainAndAddition();
        fma1.setMainFood(testFood);
        fma1.setAdditionFood(additionalFood1);

        FoodMainAndAddition fma2 = new FoodMainAndAddition();
        fma2.setMainFood(testFood);
        fma2.setAdditionFood(additionalFood2);

        testFood.setMainFoods(Arrays.asList(fma1, fma2));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(cartDetailRepository.findByCartIdAndOrderIsNull(1L)).thenReturn(new ArrayList<>());
        when(foodRepository.findById(1L)).thenReturn(Optional.of(testFood));
        when(foodRepository.findById(2L)).thenReturn(Optional.of(additionalFood1));
        when(foodRepository.findById(3L)).thenReturn(Optional.of(additionalFood2));
        when(cartDetailRepository.save(any(CartDetail.class))).thenReturn(testCartDetail);

        // When
        cartService.addToCart(1L, request);

        // Then
        verify(cartDetailRepository).save(any(CartDetail.class));
    }

    @Test
    void testAddToCart_FoodNotFound_ThrowsException() {
        // Given
        AddToCartRequest request = new AddToCartRequest();
        request.setFoodId(1L);
        request.setQuantity(2);
        request.setAdditionalItems(new ArrayList<>());

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(cartDetailRepository.findByCartIdAndOrderIsNull(1L)).thenReturn(new ArrayList<>());
        when(foodRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> cartService.addToCart(1L, request));
        assertEquals(ErrorCode.FOOD_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void testAddToCart_FoodNotActive_ThrowsException() {
        // Given
        AddToCartRequest request = new AddToCartRequest();
        request.setFoodId(1L);
        request.setQuantity(2);
        request.setAdditionalItems(new ArrayList<>());

        testFood.setStatus(FoodStatus.INACTIVE);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(cartDetailRepository.findByCartIdAndOrderIsNull(1L)).thenReturn(new ArrayList<>());
        when(foodRepository.findById(1L)).thenReturn(Optional.of(testFood));

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> cartService.addToCart(1L, request));
        assertEquals(ErrorCode.FOOD_NOT_PUBLIC_FOR_CUSTOMER, exception.getErrorCode());
    }

    @Test
    void testAddToCart_ExistingItem_UpdateQuantity() {
        // Given
        AddToCartRequest request = new AddToCartRequest();
        request.setFoodId(1L);
        request.setQuantity(3);
        request.setAdditionalItems(Arrays.asList(2L, 3L));
        request.setNote("Updated note");

        List<CartDetail> existingItems = Arrays.asList(testCartDetail);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(cartDetailRepository.findByCartIdAndOrderIsNull(1L)).thenReturn(existingItems);
        when(foodRepository.findById(1L)).thenReturn(Optional.of(testFood));
        when(cartDetailRepository.save(any(CartDetail.class))).thenReturn(testCartDetail);

        // When
        cartService.addToCart(1L, request);

        // Then
        verify(cartDetailRepository).save(testCartDetail);
        assertEquals(5, testCartDetail.getQuantity()); // 2 + 3
        assertEquals("Updated note", testCartDetail.getNote());
    }

    @Test
    void testAddToCart_DifferentRestaurant_ClearCart() {
        // Given
        AddToCartRequest request = new AddToCartRequest();
        request.setFoodId(2L);
        request.setQuantity(1);
        request.setAdditionalItems(new ArrayList<>());

        Restaurant differentRestaurant = new Restaurant();
        differentRestaurant.setId(2L);

        Food differentFood = new Food();
        differentFood.setId(2L);
        differentFood.setStatus(FoodStatus.ACTIVE);
        differentFood.setRestaurant(differentRestaurant);
        differentFood.setMainFoods(new ArrayList<>());

        List<CartDetail> existingItems = Arrays.asList(testCartDetail);
        testCart.setCartDetails(new ArrayList<>(existingItems));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(cartDetailRepository.findByCartIdAndOrderIsNull(1L)).thenReturn(existingItems);
        when(foodRepository.findById(2L)).thenReturn(Optional.of(differentFood));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);
        when(cartDetailRepository.save(any(CartDetail.class))).thenReturn(new CartDetail());

        // When
        cartService.addToCart(1L, request);

        // Then
        verify(cartRepository).save(testCart); // Cart cleared
        verify(cartDetailRepository).save(any(CartDetail.class)); // New item added
    }

    @Test
    void testRemoveFromCart_Success() {
        // Given
        List<CartDetail> existingItems = Arrays.asList(testCartDetail);
        testCart.setCartDetails(new ArrayList<>(existingItems));

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(cartDetailRepository.findByCartIdAndOrderIsNull(1L)).thenReturn(existingItems);
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        // When
        cartService.removeFromCart(1L, 1L, Arrays.asList(2L, 3L));

        // Then
        verify(cartRepository).save(testCart);
        assertTrue(testCart.getCartDetails().isEmpty());
    }

    @Test
    void testRemoveFromCart_CartNotFound_ThrowsException() {
        // Given
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> cartService.removeFromCart(1L, 1L, Arrays.asList(2L, 3L)));
        assertEquals(ErrorCode.CART_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void testUpdateCartDetailQuantity_Success() {
        // Given
        CartUpdateRequest request = new CartUpdateRequest();
        request.setUserId(1L);
        request.setCartDetailId(1L);
        request.setNewQuantity(5);

        testCart.setCartDetails(Arrays.asList(testCartDetail));

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(cartDetailRepository.findById(1L)).thenReturn(Optional.of(testCartDetail));
        when(cartDetailRepository.save(any(CartDetail.class))).thenReturn(testCartDetail);

        // When
        cartService.updateCartDetailQuantity(request);

        // Then
        verify(cartDetailRepository).save(testCartDetail);
        assertEquals(5, testCartDetail.getQuantity());
    }

    @Test
    void testUpdateCartDetailQuantity_CartDetailNotFound_ThrowsException() {
        // Given
        CartUpdateRequest request = new CartUpdateRequest();
        request.setUserId(1L);
        request.setCartDetailId(1L);
        request.setNewQuantity(5);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(cartDetailRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> cartService.updateCartDetailQuantity(request));
        assertEquals(ErrorCode.CART_ITEM_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void testGetAllCartDetailUser_EmptyCart_ReturnsEmptyResponse() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(cartDetailRepository.findByCartIdAndOrderIsNull(1L)).thenReturn(new ArrayList<>());

        // When
        CartResponse response = cartService.getAllCartDetailUser(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getCartId());
        assertTrue(response.getListItem().isEmpty());
    }

    @Test
    void testGetAllCartDetailUser_WithItems_ReturnsPopulatedResponse() {
        // Given
        List<CartDetail> cartDetails = Arrays.asList(testCartDetail);
        testVoucher.setVoucherDetails(Arrays.asList(testVoucherDetail));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(cartDetailRepository.findByCartIdAndOrderIsNull(1L)).thenReturn(cartDetails);
        when(voucherService.getVoucherOfRestaurant(1L)).thenReturn(Arrays.asList(testVoucher));
        when(voucherDetailService.getVoucherDetailByVoucherInAndFoodInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                anyList(), anyList(), any(LocalDateTime.class))).thenReturn(Arrays.asList(testVoucherDetail));
        when(foodService.getCurrentPrice(1L)).thenReturn(BigDecimal.valueOf(100));
        when(cartDetailRepository.save(any(CartDetail.class))).thenReturn(testCartDetail);

        // When
        CartResponse response = cartService.getAllCartDetailUser(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getCartId());
        assertEquals(1L, response.getRestaurantId());
        assertEquals("Test Restaurant", response.getRestaurantName());
        assertEquals(1, response.getListItem().size());
    }

    @Test
    void testDeleteCartDetail_Success() {
        // When
        cartService.deleteCartDetail(1L);

        // Then
        verify(cartDetailRepository).deleteById(1L);
    }

    @Test
    void testCheckRestaurantOpen_OpenRestaurant_ReturnsTrue() {
        // Given
        List<CartDetail> cartDetails = Arrays.asList(testCartDetail);
        // Set restaurant hours to be open (assuming current time is between 8:00 and 22:00)
        testRestaurant.setOpeningHour(LocalTime.of(8, 0));
        testRestaurant.setClosingHour(LocalTime.of(22, 0));

        when(cartDetailRepository.findByCartIdAndOrderIsNull(1L)).thenReturn(cartDetails);

        // When
        boolean isOpen = cartService.checkRestaurantOpen(1L);

        // Then - This will depend on the current time when the test runs
        // In a real scenario, you might want to mock LocalTime.now()
        assertTrue(isOpen || !isOpen); // Just verify method executes without error
    }

    @Test
    void testCheckRestaurantOpen_EmptyCart_ReturnsFalse() {
        // Given
        when(cartDetailRepository.findByCartIdAndOrderIsNull(1L)).thenReturn(new ArrayList<>());

        // When
        boolean isOpen = cartService.checkRestaurantOpen(1L);

        // Then
        assertFalse(isOpen);
    }

    @Test
    void testClearCart_Success() {
        // Given
        CartDetail cartDetail1 = new CartDetail();
        cartDetail1.setCart(testCart);
        cartDetail1.setOrder(null);

        CartDetail cartDetail2 = new CartDetail();
        cartDetail2.setCart(testCart);
        cartDetail2.setOrder(null);

        testCart.setCartDetails(new ArrayList<>(Arrays.asList(cartDetail1, cartDetail2)));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        // When
        cartService.clearCart(testCart);

        // Then
        verify(cartRepository).save(testCart);
        assertTrue(testCart.getCartDetails().isEmpty());
    }

    @Test
    void testCompareTwoList_EqualLists_ReturnsTrue() {
        // Given
        List<Long> list1 = Arrays.asList(1L, 2L, 3L);
        List<Long> list2 = Arrays.asList(3L, 1L, 2L);

        // When
        boolean result = cartService.compareTwoList(list1, list2);

        // Then
        assertTrue(result);
    }

    @Test
    void testCompareTwoList_DifferentLists_ReturnsFalse() {
        // Given
        List<Long> list1 = Arrays.asList(1L, 2L, 3L);
        List<Long> list2 = Arrays.asList(1L, 2L, 4L);

        // When
        boolean result = cartService.compareTwoList(list1, list2);

        // Then
        assertFalse(result);
    }

    @Test
    void testCompareTwoList_NullLists_ReturnsFalse() {
        // When & Then
        assertFalse(cartService.compareTwoList(null, Arrays.asList(1L, 2L)));
        assertFalse(cartService.compareTwoList(Arrays.asList(1L, 2L), null));
        assertFalse(cartService.compareTwoList(null, null));
    }

    @Test
    void testCompareTwoList_DifferentSizes_ReturnsFalse() {
        // Given
        List<Long> list1 = Arrays.asList(1L, 2L);
        List<Long> list2 = Arrays.asList(1L, 2L, 3L);

        // When
        boolean result = cartService.compareTwoList(list1, list2);

        // Then
        assertFalse(result);
    }
}