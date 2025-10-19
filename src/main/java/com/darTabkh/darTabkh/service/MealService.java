package com.darTabkh.darTabkh.service;

import com.darTabkh.darTabkh.dto.MealFilterRequest;
import com.darTabkh.darTabkh.dto.MealRequest;
import com.darTabkh.darTabkh.dto.MealResponse;
import com.darTabkh.darTabkh.dto.PageRequest;
import com.darTabkh.darTabkh.dto.PageResponse;
import com.darTabkh.darTabkh.entity.Category;
import com.darTabkh.darTabkh.entity.Meal;
import com.darTabkh.darTabkh.entity.Role;
import com.darTabkh.darTabkh.entity.User;
import com.darTabkh.darTabkh.exception.ResourceNotFoundException;
import com.darTabkh.darTabkh.mapper.MealMapper;
import com.darTabkh.darTabkh.repository.CategoryRepository;
import com.darTabkh.darTabkh.repository.MealRepository;
import com.darTabkh.darTabkh.repository.UserRepository;
import com.darTabkh.darTabkh.specification.MealSpecifications;
import com.darTabkh.darTabkh.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MealService {
    
    private final MealRepository mealRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final MealMapper mealMapper;
    
    public PageResponse<MealResponse> getAllMeals(PageRequest pageRequest) {
        Pageable pageable = PaginationUtil.toPageable(pageRequest);
        Page<Meal> mealPage = mealRepository.findAll(pageable);
        
        PageResponse<Meal> mealPageResponse = PaginationUtil.toPageResponse(mealPage);
        
        List<MealResponse> mealResponses = mealPage.getContent().stream()
                .map(mealMapper::toResponse)
                .collect(Collectors.toList());
        
        return PageResponse.<MealResponse>builder()
                .content(mealResponses)
                .page(mealPageResponse.getPage())
                .size(mealPageResponse.getSize())
                .totalElements(mealPageResponse.getTotalElements())
                .totalPages(mealPageResponse.getTotalPages())
                .first(mealPageResponse.getFirst())
                .last(mealPageResponse.getLast())
                .hasNext(mealPageResponse.getHasNext())
                .hasPrevious(mealPageResponse.getHasPrevious())
                .build();
    }
    
    public PageResponse<MealResponse> getAllMealsWithFilters(MealFilterRequest filterRequest, PageRequest pageRequest) {
        Pageable pageable = PaginationUtil.toPageable(pageRequest);
        
        // Build specifications based on filter request
        List<Specification<Meal>> specifications = new ArrayList<>();
        
        if (filterRequest.getTitle() != null && !filterRequest.getTitle().trim().isEmpty()) {
            specifications.add(MealSpecifications.hasTitle(filterRequest.getTitle()));
        }
        
        if (filterRequest.getKeyword() != null && !filterRequest.getKeyword().trim().isEmpty()) {
            specifications.add(MealSpecifications.hasKeyword(filterRequest.getKeyword()));
        }
        
        if (filterRequest.getCategoryId() != null) {
            specifications.add(MealSpecifications.hasCategory(filterRequest.getCategoryId()));
        }
        
        if (filterRequest.getCookerCity() != null && !filterRequest.getCookerCity().trim().isEmpty()) {
            specifications.add(MealSpecifications.hasCookerCity(filterRequest.getCookerCity()));
        }
        
        if (filterRequest.getCookerId() != null) {
            specifications.add(MealSpecifications.hasCooker(filterRequest.getCookerId()));
        }
        
        if (filterRequest.getMinPrice() != null || filterRequest.getMaxPrice() != null) {
            specifications.add(MealSpecifications.hasPriceRange(filterRequest.getMinPrice(), filterRequest.getMaxPrice()));
        }
        
        Specification<Meal> combinedSpec = MealSpecifications.combineSpecifications(specifications);
        
        Page<Meal> mealPage = mealRepository.findAll(combinedSpec, pageable);
        
        List<MealResponse> mealResponses = mealPage.getContent().stream()
                .map(mealMapper::toResponse)
                .collect(Collectors.toList());
        
        return PageResponse.<MealResponse>builder()
                .content(mealResponses)
                .page(mealPage.getNumber())
                .size(mealPage.getSize())
                .totalElements(mealPage.getTotalElements())
                .totalPages(mealPage.getTotalPages())
                .first(mealPage.isFirst())
                .last(mealPage.isLast())
                .hasNext(mealPage.hasNext())
                .hasPrevious(mealPage.hasPrevious())
                .build();
    }
    
    public List<MealResponse> getMealsByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId.toString()));
        
        List<Meal> meals = mealRepository.findByCategory(category);
        return meals.stream()
                .map(mealMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    public List<MealResponse> searchMeals(String keyword) {
        List<Meal> meals = mealRepository.findByKeyword(keyword);
        return meals.stream()
                .map(mealMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    public List<MealResponse> getMealsByCategoryAndCity(Long categoryId, String city) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId.toString()));
        
        List<Meal> meals = mealRepository.findByCategoryAndCooker_City(category, city);
        return meals.stream()
                .map(mealMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    public PageResponse<MealResponse> getCookerMeals(PageRequest pageRequest) {
        User cooker = getCurrentCooker();
        Pageable pageable = PaginationUtil.toPageable(pageRequest);
        Specification<Meal> spec = MealSpecifications.hasCooker(cooker.getId());
        
        Page<Meal> mealPage = mealRepository.findAll(spec, pageable);
        
        List<MealResponse> mealResponses = mealPage.getContent().stream()
                .map(mealMapper::toResponse)
                .collect(Collectors.toList());
        
        return PageResponse.<MealResponse>builder()
                .content(mealResponses)
                .page(mealPage.getNumber())
                .size(mealPage.getSize())
                .totalElements(mealPage.getTotalElements())
                .totalPages(mealPage.getTotalPages())
                .first(mealPage.isFirst())
                .last(mealPage.isLast())
                .hasNext(mealPage.hasNext())
                .hasPrevious(mealPage.hasPrevious())
                .build();
    }
    
    public MealResponse createMeal(MealRequest request) {
        User cooker = getCurrentCooker();
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId().toString()));
        
        Meal meal = mealMapper.toEntity(request);
        meal.setCategory(category);
        meal.setCooker(cooker);
        
        Meal savedMeal = mealRepository.save(meal);
        return mealMapper.toResponse(savedMeal);
    }
    
    public MealResponse updateMeal(Long mealId, MealRequest request) {
        User cooker = getCurrentCooker();
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new ResourceNotFoundException("Meal", "id", mealId.toString()));
        
        // Check if the meal belongs to the current cooker
        if (!meal.getCooker().getId().equals(cooker.getId())) {
            throw new RuntimeException("You can only update your own meals");
        }
        
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId().toString()));
        
        meal.setTitle(request.getTitle());
        meal.setDescription(request.getDescription());
        meal.setImages(request.getImages());
        meal.setIngredients(request.getIngredients());
        meal.setPrice(request.getPrice());
        meal.setCategory(category);
        
        Meal updatedMeal = mealRepository.save(meal);
        return mealMapper.toResponse(updatedMeal);
    }
    
    public void deleteMeal(Long mealId) {
        User cooker = getCurrentCooker();
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new ResourceNotFoundException("Meal", "id", mealId.toString()));
        
        // Check if the meal belongs to the current cooker
        if (!meal.getCooker().getId().equals(cooker.getId())) {
            throw new RuntimeException("You can only delete your own meals");
        }
        
        mealRepository.delete(meal);
    }
    
    private User getCurrentCooker() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        
        if (user.getRole() != Role.COOKER) {
            throw new RuntimeException("Only cooks can perform this action");
        }
        
        return user;
    }
}
