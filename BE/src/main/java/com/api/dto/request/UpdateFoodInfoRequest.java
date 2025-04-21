package com.api.dto.request;

import com.api.utils.FoodKind;
import com.api.utils.FoodStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFoodInfoRequest {
    private Optional<String> name = Optional.empty();
    private Optional<String> image = Optional.empty();
    private Optional<String> foodType = Optional.empty();
    private Optional<FoodKind> foodKind = Optional.empty();
    private Optional<String> description = Optional.empty();
    private Optional<FoodStatus> status = Optional.empty();
    private Optional<BigDecimal> oldPrice = Optional.empty();
    private Optional<BigDecimal> newPrice = Optional.empty();
    private Optional<Set<Integer>> additionalIds = Optional.empty();
}