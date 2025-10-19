package com.darTabkh.darTabkh.service;

import com.darTabkh.darTabkh.dto.CategoryRequest;
import com.darTabkh.darTabkh.dto.CategoryResponse;
import com.darTabkh.darTabkh.dto.PageRequest;
import com.darTabkh.darTabkh.dto.PageResponse;
import com.darTabkh.darTabkh.entity.Category;
import com.darTabkh.darTabkh.exception.ResourceNotFoundException;
import com.darTabkh.darTabkh.mapper.CategoryMapper;
import com.darTabkh.darTabkh.repository.CategoryRepository;
import com.darTabkh.darTabkh.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    
    public PageResponse<CategoryResponse> getAllCategories(PageRequest pageRequest) {
        Pageable pageable = PaginationUtil.toPageable(pageRequest);
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        
        List<CategoryResponse> categoryResponses = categoryPage.getContent().stream()
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList());
        
        return PageResponse.<CategoryResponse>builder()
                .content(categoryResponses)
                .page(categoryPage.getNumber())
                .size(categoryPage.getSize())
                .totalElements(categoryPage.getTotalElements())
                .totalPages(categoryPage.getTotalPages())
                .first(categoryPage.isFirst())
                .last(categoryPage.isLast())
                .hasNext(categoryPage.hasNext())
                .hasPrevious(categoryPage.hasPrevious())
                .build();
    }
    
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id.toString()));
        return categoryMapper.toResponse(category);
    }
    
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new RuntimeException("Category with name '" + request.getName() + "' already exists");
        }
        
        Category category = categoryMapper.toEntity(request);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toResponse(savedCategory);
    }
}
