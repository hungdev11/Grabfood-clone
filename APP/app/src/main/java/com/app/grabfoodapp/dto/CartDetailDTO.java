package com.app.grabfoodapp.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartDetailDTO implements Serializable {
    private long restaurantId;
    private long id;
    private long foodId;
    private String foodName;
    private int quantity;
    private List<AdditionFood> additionFoods;
    private BigDecimal price;
    private String note;
    private String food_img;
}
