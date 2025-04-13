package com.api.service.Imp;

import com.api.dto.request.AddAdditionalFoodsRequest;
import com.api.dto.request.AddFoodRequest;
import com.api.dto.request.AdjustFoodPriceRequest;
import com.api.dto.response.GetFoodResponse;
import com.api.dto.response.PageResponse;
import com.api.entity.*;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.repository.FoodMainAndAdditionalRepository;
import com.api.repository.FoodRepository;
import com.api.service.FoodService;
import com.api.service.FoodTypeService;
import com.api.service.RestaurantService;
import com.api.utils.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class FoodServiceImp implements FoodService {
    private final FoodRepository foodRepository;
    private final FoodTypeService foodTypeService;
    private final RestaurantService restaurantService;
    private final FoodMainAndAdditionalRepository foodMainAndAdditionalRepository;

    @Override
    @Transactional
    public long addFood(AddFoodRequest request) {
        log.info("Adding new food");
        Restaurant restaurant = restaurantService.getRestaurant(request.getRestaurantId());
        FoodType foodType = foodTypeService.getFoodTypeByName(request.getType());

        if (foodRepository.existsByRestaurantAndNameAndTypeAndKind(
                restaurant, request.getName(), foodType, request.getKind())) {
            log.error("Food already exists");
            throw new AppException(ErrorCode.FOOD_OF_RETAURANT_EXISTED);
        }

        Food newFood = Food.builder()
                .name(request.getName())
                .description(request.getDescription())
                .kind(request.getKind())
                .image(request.getImage())
                .type(foodType)
                .status(FoodStatus.ACTIVE)
                .restaurant(restaurant)
                .build();

        newFood = foodRepository.save(newFood);

        FoodDetail foodDetail = FoodDetail.builder()
                .price(request.getPrice())
                .startTime(LocalDateTime.now())
                .endTime(null)
                .food(newFood)
                .build();
        newFood.getFoodDetails().add(foodDetail);

        foodType.getFoods().add(newFood);
        restaurant.getFoods().add(newFood);

        return newFood.getId();
    }

    @Override
    @Transactional
    public long adjustFoodPrice(AdjustFoodPriceRequest request) {
        log.info("adjust Food Price");
        if (request.getOldPrice().equals(request.getNewPrice())) {
            log.error("Prices are the same");
            throw new AppException(ErrorCode.FOOD_PRICE_REDUNDANT);
        }

        Food food = getFoodByIdAndRestaurantId(request.getFoodId(), request.getRestaurantId());

        FoodDetail newestDetail = food.getFoodDetails().stream()
                .max(Comparator.comparing(FoodDetail::getStartTime))
                .orElseThrow(() -> {
                    log.error("Food id {} not found any detail", request.getFoodId());
                    return new AppException(ErrorCode.FOOD_NOT_FOUND);
                });

        if (newestDetail != null) {
            if (newestDetail.getPrice().compareTo(request.getOldPrice()) != 0) {
                log.error("Previous price conflict {} vs {}", newestDetail.getPrice(), request.getOldPrice());
                throw new AppException(ErrorCode.FOOD_DETAIL_CONFLICT_PRICE);
            }
            newestDetail.setEndTime(LocalDateTime.now());
        }

        FoodDetail newFoodDetail = FoodDetail.builder()
                .price(request.getNewPrice())
                .startTime(LocalDateTime.now())
                .endTime(null)
                .food(food)
                .build();

        food.getFoodDetails().add(newFoodDetail);

        return foodRepository.save(food).getId();
    }

    @Override
    public BigDecimal getCurrentPrice(long foodId) {
        log.info("Current price of {}", foodId);
        Food food = getFoodById(foodId);

        BigDecimal price = food.getFoodDetails().stream()
                .filter(fd -> fd.getEndTime() == null)
                .map(FoodDetail::getPrice)
                .findFirst()
                .orElse(BigDecimal.ZERO);

        LocalDateTime now = LocalDateTime.now();
        return applyVoucher(food, price, now);
    }


    @Override
    public BigDecimal getFoodPriceIn(long foodId, LocalDateTime time) {
        log.info("Get food price of {} in {}", foodId, time);
        Food food = getFoodById(foodId);
        BigDecimal price = BigDecimal.ZERO;

        for (FoodDetail foodDetail : food.getFoodDetails()) {
            LocalDateTime start = foodDetail.getStartTime();
            LocalDateTime end = foodDetail.getEndTime();

            boolean isInRange = (start == null || !time.isBefore(start)) &&
                    (end == null || !time.isAfter(end));

            if (isInRange) {
                price = foodDetail.getPrice();
                break;
            }
        }
        return applyVoucher(food, price, time);
    }

    private BigDecimal applyVoucher(Food food, BigDecimal price, LocalDateTime time) {
        for (VoucherDetail vd : food.getVoucherDetails()) {
            if (!vd.getStartDate().isBefore(time) || !vd.getEndDate().isAfter(time)) continue;

            Voucher voucher = vd.getVoucher();
            if (voucher.getStatus() != VoucherStatus.ACTIVE) continue;
            if (!food.getRestaurant().equals(voucher.getRestaurant())) continue;

            if (voucher.getType() == VoucherType.PERCENTAGE) {
                BigDecimal discount = voucher.getValue().min(BigDecimal.valueOf(100));
                price = price.subtract(price.multiply(discount).divide(BigDecimal.valueOf(100)));
            } else if (voucher.getType() == VoucherType.FIXED) {
                price = price.compareTo(voucher.getValue()) < 0
                        ? BigDecimal.ZERO
                        : price.subtract(voucher.getValue());
            }
            break;
        }
        return price;
    }

    @Override
    public GetFoodResponse getFood(long foodId, boolean isForCustomer) {
        log.info("Get food info {}", foodId);
        Food food = getFoodById(foodId);

        if (isForCustomer && food.getStatus() == FoodStatus.INACTIVE) {
            log.error("Food id {} not public", foodId);
            throw new AppException(ErrorCode.FOOD_NOT_PUBLIC_FOR_CUSTOMER);
        }

        return GetFoodResponse.builder()
                .name(food.getName())
                .image(food.getImage())
                .description(food.getDescription())
                .price(getCurrentPrice(food.getId()))
                .rating(BigDecimal.ZERO)
                .build();
    }

    @Override
    public PageResponse<List<GetFoodResponse>> getFoodsOfRestaurant(long restaurantId, boolean isForCustomer, int page, int pageSize) {
        log.info("Get foods of restaurant {}", restaurantId);

        if (page < 0) {
            log.warn("Invalid page number: {}. Defaulting to 0.", page);
            page = 0;
        }
        log.info("{} foods in page {}", pageSize, page);

        Restaurant restaurant = restaurantService.getRestaurant(restaurantId);

        Pageable pageable = PageRequest.of(page, pageSize);

        Page<Food> foodPage;
        if (isForCustomer) {
            // Truy vấn tất cả các món ăn có trạng thái ACTIVE
            foodPage = foodRepository.findByRestaurantAndStatus(restaurant, FoodStatus.ACTIVE, pageable);

            // Lọc món ăn có loại là MAIN hoặc BOTH
            foodPage = new PageImpl<>(
                    foodPage.stream()
                            .filter(f -> f.getKind().equals(FoodKind.MAIN) || f.getKind().equals(FoodKind.BOTH))
                            .collect(Collectors.toList()),
                    pageable,
                    foodPage.getTotalElements()
            );
        }
        else {
            foodPage = foodRepository.findByRestaurant(restaurant, pageable);
        }

        List<GetFoodResponse> foodResponses = foodResponsesToEndUser(foodPage, isForCustomer);

        return PageResponse.<List<GetFoodResponse>>builder()
                .page(page)
                .size(pageSize)
                .total(foodPage.getTotalElements())
                .items(foodResponses)
                .build();
    }

    @Override
    public void changeFoodStatus(long restaurantId, long foodId, FoodStatus foodStatus) {
        log.info("Change food status of {} in {} to {}", foodId, restaurantId, foodStatus);
        Food food = getFoodByIdAndRestaurantId(foodId, restaurantId);
        food.setStatus(foodStatus);
        foodRepository.save(food);
    }

    @Override
    public PageResponse<List<GetFoodResponse>> getAdditionalFoodsOfRestaurant(
            long restaurantId, boolean isForCustomer, int page, int pageSize) {
        log.info("Get additional foods of restaurant {}, for customer {} ", restaurantId, isForCustomer);

        if (page < 0) {
            log.warn("Invalid page number: {}. Defaulting to 0.", page);
            page = 0;
        }

        Restaurant restaurant = restaurantService.getRestaurant(restaurantId);
        Pageable pageable = PageRequest.of(page, pageSize);

        Stream<Food> foodStream = restaurant.getFoods().stream()
                .filter(f -> !f.getKind().equals(FoodKind.MAIN));

        if (isForCustomer) {
            foodStream = foodStream.filter(f -> f.getStatus() == FoodStatus.ACTIVE);
        }

        Page<Food> foodPage = PageUtils.convertListToPage(foodStream.toList(), pageable);

        List<GetFoodResponse> foodResponses = foodResponsesToEndUser(foodPage, isForCustomer);

        return PageResponse.<List<GetFoodResponse>>builder()
                .page(page)
                .size(pageSize)
                .total(foodPage.getTotalElements())
                .items(foodResponses)
                .build();
    }

    @Override
    @Transactional
    public void addAdditionalFoodToFoodOfRestaurant(AddAdditionalFoodsRequest request) {
         log.info("Add additional foods to food {} of restaurant {}",request.getFoodId(), request.getRestaurantId());
         Food food = getFoodByIdAndRestaurantId(request.getFoodId(), request.getRestaurantId());

         if (food.getKind().equals(FoodKind.ADDITIONAL)) {
             log.error("Cannot add additional to food {} because it is an additional food", request.getFoodId());
             throw new AppException(ErrorCode.FOOD_ADDITIONAL);
         }

         Set<Integer> idList = request.getAdditionalFoodIds();
         if (Objects.isNull(idList) || idList.isEmpty()) {
             log.warn("Nothing to add to the food {}", request.getFoodId());
             return;
         }

         for (Integer id : idList) {
             Food additionalFood = getFoodByIdAndRestaurantId(id, request.getRestaurantId());
             if (!additionalFood.getId().equals(food.getId()) && !additionalFood.getKind().equals(FoodKind.MAIN)) {
                if (!foodMainAndAdditionalRepository.existsByMainFoodAndAdditionFood(food, additionalFood)) {
                    log.info("Main {}, addition {}", food.getId(), additionalFood.getId());
                    FoodMainAndAddition foodMainAndAddition = FoodMainAndAddition.builder()
                            .mainFood(food)
                            .additionFood(additionalFood)
                            .build();
                    food.getMainFoods().add(foodMainAndAddition);
                    additionalFood.getAdditionFoods().add(foodMainAndAddition);
                    foodMainAndAdditionalRepository.save(foodMainAndAddition);
                }
             }
         }
    }

    @Override
    public PageResponse<List<GetFoodResponse>> getAdditionalFoodsOfFood(long restaurantId, long foodId, boolean isForCustomer, int page, int pageSize) {
        log.info("Get additional foods of food {} of restaurant {}, for customer {}", foodId, restaurantId, isForCustomer);
        if (page < 0) {
            log.warn("Invalid page number: {}. Defaulting to 0.", page);
            page = 0;
        }

        Food food = getFoodByIdAndRestaurantId(foodId, restaurantId);

        if (food.getKind().equals(FoodKind.ADDITIONAL)) {
            log.error("Cannot get additional of food {} because it is an additional food", food.getId());
            throw new AppException(ErrorCode.FOOD_ADDITIONAL);
        }

        if (isForCustomer && food.getStatus() == FoodStatus.INACTIVE) {
            log.error("Food id {} not public", foodId);
            throw new AppException(ErrorCode.FOOD_NOT_PUBLIC_FOR_CUSTOMER);
        }

        Pageable pageable = PageRequest.of(page, pageSize);

        Stream<Food> foodStream = food.getMainFoods().stream()
                .map(FoodMainAndAddition::getAdditionFood);

        if (isForCustomer) {
            foodStream = foodStream.filter(f -> f.getStatus() == FoodStatus.ACTIVE);
        }

        Page<Food> foodPage = PageUtils.convertListToPage(foodStream.toList(), pageable);

        List<GetFoodResponse> foodResponses = foodResponsesToEndUser(foodPage, isForCustomer);

        return PageResponse.<List<GetFoodResponse>>builder()
                .page(page)
                .size(pageSize)
                .total(foodPage.getTotalElements())
                .items(foodResponses)
                .build();
    }

//====================================================================================================================================

    private Food getFoodById(long id) {
        log.info("Get food id {}", id);
        return foodRepository.findById(id).orElseThrow(() -> {
            log.error("Food not found");
            return new AppException(ErrorCode.FOOD_NOT_FOUND);
        });
    }

    private Food getFoodByIdAndRestaurantId (long foodId, long restaurantId) {
        log.info("Get food id {} and restaurant id {}", foodId, restaurantId);
        Restaurant restaurant = restaurantService.getRestaurant(restaurantId);

        Food food = getFoodById(foodId);

        if (!food.getRestaurant().equals(restaurant)) {
            log.error("Food id {} not belong to restaurant {}", foodId, restaurantId);
            throw new AppException(ErrorCode.FOOD_RESTAURANT_NOT_FOUND);
        }
        return food;
    }

    private List<GetFoodResponse> foodResponsesToEndUser(Page<Food> foodPage, boolean isForCustomer) {
        log.info("Response to user? {}", isForCustomer);

        return foodPage.getContent().stream()
                .map(food -> {
                    GetFoodResponse.GetFoodResponseBuilder responseBuilder = GetFoodResponse.builder()
                            .id(food.getId())
                            .name(food.getName())
                            .image(food.getImage())
                            .description(food.getDescription())
                            .price(getCurrentPrice(food.getId()))
                            .rating(BigDecimal.ZERO);

                    if (!isForCustomer) {
                        responseBuilder.status(food.getStatus());
                    }

                    return responseBuilder.build();
                })
                .collect(Collectors.toList());
    }

}
