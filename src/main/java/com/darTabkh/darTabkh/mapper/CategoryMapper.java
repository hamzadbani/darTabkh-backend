package com.darTabkh.darTabkh.mapper;

import com.darTabkh.darTabkh.dto.CategoryRequest;
import com.darTabkh.darTabkh.dto.CategoryResponse;
import com.darTabkh.darTabkh.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "meals", ignore = true)
    Category toEntity(CategoryRequest categoryRequest);
    
    CategoryResponse toResponse(Category category);
}
