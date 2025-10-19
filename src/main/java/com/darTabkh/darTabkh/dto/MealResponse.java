package com.darTabkh.darTabkh.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealResponse {
    private Long id;
    private String title;
    private String description;
    private List<String> images;
    private List<String> ingredients;
    private BigDecimal price;
    private CategoryResponse category;
    private UserResponse cooker;
}
