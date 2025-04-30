package com.api.dto.response;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class GetOrderGroupResponse {
    List<String> statusList;
    List<OrderResponse> orders;
}
