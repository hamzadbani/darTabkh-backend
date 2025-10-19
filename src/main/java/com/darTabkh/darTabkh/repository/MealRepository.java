package com.darTabkh.darTabkh.repository;

import com.darTabkh.darTabkh.entity.Category;
import com.darTabkh.darTabkh.entity.Meal;
import com.darTabkh.darTabkh.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long>, JpaSpecificationExecutor<Meal> {
    List<Meal> findByCooker(User cooker);
    List<Meal> findByCategory(Category category);
    List<Meal> findByCookerAndCategory(User cooker, Category category);
    
    @Query("SELECT m FROM Meal m WHERE " +
           "LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "EXISTS (SELECT i FROM m.ingredients i WHERE LOWER(i) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Meal> findByKeyword(@Param("keyword") String keyword);
    
    List<Meal> findByCategoryAndCooker_City(Category category, String city);
}
