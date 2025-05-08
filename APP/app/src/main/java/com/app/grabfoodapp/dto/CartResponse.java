package com.app.grabfoodapp.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse implements Serializable {
    private long cartId;
    private String restaurantName;
    private List<CartDetailDTO> listItem;
}
