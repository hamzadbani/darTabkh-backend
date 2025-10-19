package com.darTabkh.darTabkh.mapper;

import com.darTabkh.darTabkh.dto.MealRequest;
import com.darTabkh.darTabkh.dto.MealResponse;
import com.darTabkh.darTabkh.entity.Meal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class, UserMapper.class})
public interface MealMapper {
    
    MealMapper INSTANCE = Mappers.getMapper(MealMapper.class);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "cooker", ignore = true)
    Meal toEntity(MealRequest mealRequest);
    
    MealResponse toResponse(Meal meal);
}
