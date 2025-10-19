package com.darTabkh.darTabkh.controller;

import com.darTabkh.darTabkh.dto.MealFilterRequest;
import com.darTabkh.darTabkh.dto.MealRequest;
import com.darTabkh.darTabkh.dto.MealResponse;
import com.darTabkh.darTabkh.dto.PageRequest;
import com.darTabkh.darTabkh.dto.PageResponse;
import com.darTabkh.darTabkh.service.MealService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meals")
@RequiredArgsConstructor
@Tag(name = "Meal", description = "Meal management APIs")
public class MealController {
    
    private final MealService mealService;
    
    @GetMapping
    @Operation(summary = "Get all meals with pagination")
    @ApiResponse(responseCode = "200", description = "Meals retrieved successfully")
    public ResponseEntity<PageResponse<MealResponse>> getAllMeals(
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        
        PageRequest pageRequest = PageRequest.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();
        
        PageResponse<MealResponse> meals = mealService.getAllMeals(pageRequest);
        return ResponseEntity.ok(meals);
    }
    
    @PostMapping("/search/filters")
    @Operation(summary = "Search meals with filters and pagination")
    @ApiResponse(responseCode = "200", description = "Meals retrieved successfully")
    public ResponseEntity<PageResponse<MealResponse>> searchMealsWithFilters(
            @RequestBody MealFilterRequest filterRequest,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        
        PageRequest pageRequest = PageRequest.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();
        
        PageResponse<MealResponse> meals = mealService.getAllMealsWithFilters(filterRequest, pageRequest);
        return ResponseEntity.ok(meals);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search meals by keyword")
    @ApiResponse(responseCode = "200", description = "Meals retrieved successfully")
    public ResponseEntity<List<MealResponse>> searchMeals(
            @RequestParam @NotBlank(message = "Search keyword is required") String keyword) {
        List<MealResponse> meals = mealService.searchMeals(keyword);
        return ResponseEntity.ok(meals);
    }
    
    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get meals by category")
    @ApiResponse(responseCode = "200", description = "Meals retrieved successfully")
    public ResponseEntity<List<MealResponse>> getMealsByCategory(@PathVariable Long categoryId) {
        List<MealResponse> meals = mealService.getMealsByCategory(categoryId);
        return ResponseEntity.ok(meals);
    }
    
    @GetMapping("/category/{categoryId}/city/{city}")
    @Operation(summary = "Get meals by category and city")
    @ApiResponse(responseCode = "200", description = "Meals retrieved successfully")
    public ResponseEntity<List<MealResponse>> getMealsByCategoryAndCity(
            @PathVariable Long categoryId,
            @PathVariable @NotBlank(message = "City is required") String city) {
        List<MealResponse> meals = mealService.getMealsByCategoryAndCity(categoryId, city);
        return ResponseEntity.ok(meals);
    }
    
    @GetMapping("/cooker")
    @Operation(summary = "Get meals created by current cooker with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Meals retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Only cooks can access this endpoint")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<PageResponse<MealResponse>> getCookerMeals(
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        
        PageRequest pageRequest = PageRequest.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();
        
        PageResponse<MealResponse> meals = mealService.getCookerMeals(pageRequest);
        return ResponseEntity.ok(meals);
    }
    
    @PostMapping
    @Operation(summary = "Create a new meal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Meal created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Only cooks can create meals")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<MealResponse> createMeal(@Valid @RequestBody MealRequest request) {
        MealResponse meal = mealService.createMeal(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(meal);
    }
    
    @PutMapping("/{mealId}")
    @Operation(summary = "Update a meal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Meal updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "You can only update your own meals"),
            @ApiResponse(responseCode = "404", description = "Meal not found")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<MealResponse> updateMeal(
            @PathVariable Long mealId,
            @Valid @RequestBody MealRequest request) {
        MealResponse meal = mealService.updateMeal(mealId, request);
        return ResponseEntity.ok(meal);
    }
    
    @DeleteMapping("/{mealId}")
    @Operation(summary = "Delete a meal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Meal deleted successfully"),
            @ApiResponse(responseCode = "403", description = "You can only delete your own meals"),
            @ApiResponse(responseCode = "404", description = "Meal not found")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Void> deleteMeal(@PathVariable Long mealId) {
        mealService.deleteMeal(mealId);
        return ResponseEntity.noContent().build();
    }
}
