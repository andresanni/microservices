package com.andresanni.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderLineItemdsDto {

    private Long id;
    private String skuCode;
    private BigDecimal price;
    private Integer quantity;
}
