package com.darTabkh.darTabkh.util;

import com.darTabkh.darTabkh.dto.PageRequest;
import com.darTabkh.darTabkh.dto.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginationUtil {
    
    public static Pageable toPageable(PageRequest pageRequest) {
        Sort.Direction direction = Sort.Direction.fromString(pageRequest.getSortDirection());
        Sort sort = Sort.by(direction, pageRequest.getSortBy());
        
        return org.springframework.data.domain.PageRequest.of(
            pageRequest.getPage(),
            pageRequest.getSize(),
            sort
        );
    }
    
    public static Pageable toPageable(int page, int size, String sortBy, String sortDirection) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Sort sort = Sort.by(direction, sortBy);
        
        return org.springframework.data.domain.PageRequest.of(page, size, sort);
    }
    
    public static <T> PageResponse<T> toPageResponse(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
}
