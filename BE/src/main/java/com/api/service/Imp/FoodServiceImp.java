package com.api.service.Imp;

import com.api.dto.request.AddAdditionalFoodsRequest;
import com.api.dto.request.AddFoodRequest;
import com.api.dto.request.AdjustFoodPriceRequest;
import com.api.dto.request.UpdateFoodInfoRequest;
import com.api.dto.response.ApiResponse;
import com.api.dto.response.GetFoodGroupResponse;
import com.api.dto.response.GetFoodResponse;
import com.api.dto.response.PageResponse;
import com.api.entity.*;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.repository.FoodMainAndAdditionalRepository;
import com.api.repository.FoodRepository;
import com.api.service.*;
import com.api.utils.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
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
    private final VoucherService voucherService;
    private final VoucherDetailService voucherDetailService;

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
        return price;
    }


    @Override
    public BigDecimal getFoodPriceIn(long foodId, LocalDateTime time) {
        log.info("Get food price of {} in {}", foodId, time);
        Food food = getFoodById(foodId);
        BigDecimal price = BigDecimal.ZERO;

        for (FoodDetail foodDetail : food.getFoodDetails()) {
            LocalDateTime start = foodDetail.getStartTime();
            LocalDateTime end = foodDetail.getEndTime();

            boolean isInRange = (end == null && start.isBefore(time)) ||
                    (start.isBefore(time) && end.isAfter(time));

            if (isInRange) {
                price = foodDetail.getPrice();
                break;
            }
        }
        //return price;
        return applyVoucher(food, price, time);
    }

    private BigDecimal applyVoucher(Food food, BigDecimal price, LocalDateTime time) {
        for (VoucherDetail vd : food.getVoucherDetails()) {
            if (time.isBefore(vd.getStartDate()) || time.isAfter(vd.getEndDate())) continue;

            Voucher voucher = vd.getVoucher();
            // nếu trong lúc đặt order vẫn hiệu lực nhưng sau đó k còn hiệu lực thì vẫn áp dụng, nên xét status k hợp lý
            //if (voucher.getStatus() != VoucherStatus.ACTIVE) continue;
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

    @Override
    @Modifying
    @Transactional
    public void updateFoodInfo(long restaurantId, long foodId, UpdateFoodInfoRequest request) {
        log.info("Update food {} of restaurant {}", foodId, restaurantId);
        Food food = getFoodByIdAndRestaurantId(foodId, restaurantId);

        request.getName().ifPresent(name -> {
            log.info("Updating food name to '{}'", name);
            food.setName(name);
        });

        request.getImage().ifPresent(image -> {
            log.info("Updating food image to '{}'", image);
            food.setImage(image);
        });

        request.getDescription().ifPresent(description -> {
            log.info("Updating food description to '{}'", description);
            food.setDescription(description);
        });

        request.getStatus().ifPresent(status -> {
            log.info("Updating food status to '{}'", status);
            food.setStatus(status);
        });

        request.getFoodKind().ifPresent(kind -> {
            log.info("Updating food kind to '{}'", kind);
            food.setKind(kind);
        });

        request.getFoodType().ifPresent(typeName -> {
            log.info("Updating food type to '{}'", typeName);
            FoodType type = foodTypeService.getFoodTypeByName(typeName);
            food.setType(type);
        });

        request.getAdditionalIds()
                .ifPresent(ids -> {
                    log.info("Updating additional food IDs to {}", ids);

                    // Always clear existing relations, even if new list is empty
                    for (FoodMainAndAddition foodMainAndAddition : food.getMainFoods()) {
                        Food additionalFood = foodMainAndAddition.getAdditionFood();
                        additionalFood.getAdditionFoods().remove(foodMainAndAddition);
                        foodMainAndAdditionalRepository.delete(foodMainAndAddition);
                    }
                    food.getMainFoods().clear();

                    if (!ids.isEmpty()) {
                        addAdditionalFoodToFoodOfRestaurant(AddAdditionalFoodsRequest.builder()
                                .foodId(foodId)
                                .restaurantId(restaurantId)
                                .additionalFoodIds(ids)
                                .build());
                    }
                });

        if (request.getOldPrice().isPresent() && request.getNewPrice().isPresent()) {
            BigDecimal oldP = request.getOldPrice().get();
            BigDecimal newP = request.getNewPrice().get();

            if (oldP.compareTo(newP) != 0) {
                log.info("Adjusting price from {} to {}", oldP, newP);
                adjustFoodPrice(AdjustFoodPriceRequest.builder()
                        .foodId(foodId)
                        .restaurantId(restaurantId)
                        .oldPrice(oldP)
                        .newPrice(newP)
                        .build());
            } else {
                log.info("Price unchanged ({}), skipping adjustment", oldP);
            }
        }

        log.info("Saving updated food");
        foodRepository.save(food);
    }

    @Override
    public GetFoodGroupResponse getFoodGroupOfRestaurant(long restaurantId, boolean isForCustomer) {
        log.info("Get food group of restaurant {}", restaurantId);
        Restaurant restaurant = restaurantService.getRestaurant(restaurantId);

        log.info("Get types of restaurant {}", restaurant);
        List<Food> foods = restaurant.getFoods();
        if (isForCustomer) {
            foods = foods.stream()
                    .filter(f -> f.getStatus() == FoodStatus.ACTIVE)
                    .toList();
        }

        foods = foods.stream()
                .filter(f -> !f.getKind().equals(FoodKind.ADDITIONAL))
                .toList();

        Set<String> types = foods.stream()
                .filter(f -> f.getType() != null)
                .map(f -> f.getType().getName())
                .collect(Collectors.toSet());


        log.info("Get voucher of restaurant {} in present", restaurantId);
        List<Voucher> voucherList = voucherService.getVoucherOfRestaurant(restaurantId).stream()
                .filter(v -> v.getStatus().equals(VoucherStatus.ACTIVE)
                        && v.getValue() != null
                        && v.getValue().compareTo(BigDecimal.ZERO) > 0).toList();

        List<VoucherDetail> voucherDetailList = voucherDetailService
                .getVoucherDetailByVoucherInAndFoodInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(voucherList, foods, LocalDateTime.now());

        Map<Long, VoucherDetail> foodIdToVoucherDetail = voucherDetailList.stream()
                .collect(Collectors.toMap(
                        vd -> vd.getFood().getId(),
                        vd -> vd,
                        (vd1, vd2) -> vd1 // nếu có 2 voucher thì chọn cái đầu
                ));

        log.info("Get foods of restaurant {}", restaurantId);
        List<GetFoodResponse> foodResponses = foods.stream()
                .map(f -> {
                    BigDecimal currentPrice = getCurrentPrice(f.getId());
                    VoucherDetail voucherDetail = foodIdToVoucherDetail.get(f.getId());
                    BigDecimal discountPrice = voucherDetail != null
                            ? calculateDiscountPrice(currentPrice, voucherDetail.getVoucher())
                            : currentPrice;
                    GetFoodResponse foodResponse = GetFoodResponse.builder()
                            .id(f.getId())
                            .name(f.getName())
                            .image(f.getImage())
                            .description(f.getDescription())
                            .price(currentPrice)
                            .discountPrice(discountPrice)
                            .rating(BigDecimal.ZERO)
                            .type(f.getType().getName())
                            .kind(f.getKind().name())
                            .build();
                    if (!isForCustomer) {
                        foodResponse.setStatus(f.getStatus());
                    }
                    return foodResponse;
                })
                .toList();

        return GetFoodGroupResponse.builder()
                .types(types)
                .foods(foodResponses)
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

    private BigDecimal calculateDiscountPrice(BigDecimal originalPrice, Voucher voucher) {
        if (voucher == null || voucher.getValue() == null) return originalPrice;

        BigDecimal discountPrice = originalPrice;

        if (voucher.getType().equals(VoucherType.FIXED)) {
            discountPrice = originalPrice.subtract(voucher.getValue());
        } else if (voucher.getType().equals(VoucherType.PERCENTAGE)) {
            BigDecimal discount = originalPrice.multiply(voucher.getValue()).divide(BigDecimal.valueOf(100));
            discountPrice = originalPrice.subtract(discount);
        }

        return discountPrice.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : discountPrice;
    }

}
