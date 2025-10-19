package com.darTabkh.darTabkh.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class MealRequest {
    
    @NotBlank(message = "Meal title is required")
    @Size(min = 2, max = 200, message = "Meal title must be between 2 and 200 characters")
    private String title;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    @Size(max = 10, message = "Maximum 10 images allowed")
    private List<@Size(max = 500, message = "Image URL too long") String> images;
    
    private List<@Size(max = 200, message = "Ingredient name too long") String> ingredients;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;
    
    @NotNull(message = "Category is required")
    private Long categoryId;
}
