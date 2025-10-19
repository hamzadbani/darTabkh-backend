package com.darTabkh.darTabkh.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealFilterRequest {
    private String title;
    private String keyword;
    private Long categoryId;
    private String cookerCity;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Long cookerId;
}
