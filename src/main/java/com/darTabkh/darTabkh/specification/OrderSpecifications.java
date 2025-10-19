package com.darTabkh.darTabkh.specification;

import com.darTabkh.darTabkh.entity.Meal;
import com.darTabkh.darTabkh.entity.Order;
import com.darTabkh.darTabkh.entity.OrderStatus;
import com.darTabkh.darTabkh.entity.User;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderSpecifications {

    public static Specification<Order> hasReference(String reference) {
        return (root, query, criteriaBuilder) -> {
            if (reference == null || reference.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                criteriaBuilder.lower(root.get("reference")),
                criteriaBuilder.lower(criteriaBuilder.literal("%" + reference + "%"))
            );
        };
    }

    public static Specification<Order> hasMealId(Long mealId) {
        return (root, query, criteriaBuilder) -> {
            if (mealId == null) {
                return criteriaBuilder.conjunction();
            }
            Join<Order, Meal> mealJoin = root.join("meal", JoinType.INNER);
            return criteriaBuilder.equal(mealJoin.get("id"), mealId);
        };
    }

    public static Specification<Order> hasClientId(Long clientId) {
        return (root, query, criteriaBuilder) -> {
            if (clientId == null) {
                return criteriaBuilder.conjunction();
            }
            Join<Order, User> clientJoin = root.join("client", JoinType.INNER);
            return criteriaBuilder.equal(clientJoin.get("id"), clientId);
        };
    }

    public static Specification<Order> hasCookerId(Long cookerId) {
        return (root, query, criteriaBuilder) -> {
            if (cookerId == null) {
                return criteriaBuilder.conjunction();
            }
            Join<Order, User> cookerJoin = root.join("cooker", JoinType.INNER);
            return criteriaBuilder.equal(cookerJoin.get("id"), cookerId);
        };
    }

    public static Specification<Order> hasStatus(OrderStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    public static Specification<Order> hasDateRange(LocalDateTime fromDate, LocalDateTime toDate) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (fromDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), fromDate));
            }
            
            if (toDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), toDate));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Order> hasClientCity(String city) {
        return (root, query, criteriaBuilder) -> {
            if (city == null || city.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            Join<Order, User> clientJoin = root.join("client", JoinType.INNER);
            return criteriaBuilder.equal(clientJoin.get("city"), city);
        };
    }

    public static Specification<Order> hasCookerCity(String city) {
        return (root, query, criteriaBuilder) -> {
            if (city == null || city.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            Join<Order, User> cookerJoin = root.join("cooker", JoinType.INNER);
            return criteriaBuilder.equal(cookerJoin.get("city"), city);
        };
    }

    public static Specification<Order> combineSpecifications(List<Specification<Order>> specifications) {
        if (specifications == null || specifications.isEmpty()) {
            return Specification.where(null);
        }
        
        Specification<Order> result = specifications.get(0);
        for (int i = 1; i < specifications.size(); i++) {
            result = result.and(specifications.get(i));
        }
        return result;
    }
}
