package com.darTabkh.darTabkh.specification;

import com.darTabkh.darTabkh.entity.Category;
import com.darTabkh.darTabkh.entity.Meal;
import com.darTabkh.darTabkh.entity.User;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MealSpecifications {

    public static Specification<Meal> hasTitle(String title) {
        return (root, query, criteriaBuilder) -> {
            if (title == null || title.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                criteriaBuilder.lower(root.get("title")),
                criteriaBuilder.lower(criteriaBuilder.literal("%" + title + "%"))
            );
        };
    }

    public static Specification<Meal> hasKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            
            String likePattern = "%" + keyword.toLowerCase() + "%";
            
            Predicate titlePredicate = criteriaBuilder.like(
                criteriaBuilder.lower(root.get("title")), likePattern
            );
            
            Predicate descriptionPredicate = criteriaBuilder.like(
                criteriaBuilder.lower(root.get("description")), likePattern
            );
            
            // For ingredients, we need to join and check the elements
            Join<Meal, String> ingredientsJoin = root.join("ingredients", JoinType.LEFT);
            Predicate ingredientsPredicate = criteriaBuilder.like(
                criteriaBuilder.lower(ingredientsJoin), likePattern
            );
            
            return criteriaBuilder.or(titlePredicate, descriptionPredicate, ingredientsPredicate);
        };
    }

    public static Specification<Meal> hasCategory(Long categoryId) {
        return (root, query, criteriaBuilder) -> {
            if (categoryId == null) {
                return criteriaBuilder.conjunction();
            }
            Join<Meal, Category> categoryJoin = root.join("category", JoinType.INNER);
            return criteriaBuilder.equal(categoryJoin.get("id"), categoryId);
        };
    }

    public static Specification<Meal> hasCookerCity(String city) {
        return (root, query, criteriaBuilder) -> {
            if (city == null || city.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            Join<Meal, User> cookerJoin = root.join("cooker", JoinType.INNER);
            return criteriaBuilder.equal(cookerJoin.get("city"), city);
        };
    }

    public static Specification<Meal> hasPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Meal> hasCooker(Long cookerId) {
        return (root, query, criteriaBuilder) -> {
            if (cookerId == null) {
                return criteriaBuilder.conjunction();
            }
            Join<Meal, User> cookerJoin = root.join("cooker", JoinType.INNER);
            return criteriaBuilder.equal(cookerJoin.get("id"), cookerId);
        };
    }

    public static Specification<Meal> combineSpecifications(List<Specification<Meal>> specifications) {
        if (specifications == null || specifications.isEmpty()) {
            return Specification.where(null);
        }
        
        Specification<Meal> result = specifications.get(0);
        for (int i = 1; i < specifications.size(); i++) {
            result = result.and(specifications.get(i));
        }
        return result;
    }
}
