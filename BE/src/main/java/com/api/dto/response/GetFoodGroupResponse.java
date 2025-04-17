package com.api.dto.response;

import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetFoodGroupResponse {
    Set<String> types;
    List<GetFoodResponse> foods;
}
