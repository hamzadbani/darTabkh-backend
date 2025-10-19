package com.darTabkh.darTabkh.mapper;

import com.darTabkh.darTabkh.dto.OrderRequest;
import com.darTabkh.darTabkh.dto.OrderResponse;
import com.darTabkh.darTabkh.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {MealMapper.class, UserMapper.class})
public interface OrderMapper {
    
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reference", ignore = true)
    @Mapping(target = "meal", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "cooker", ignore = true)
    @Mapping(target = "unitPrice", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "confirmedAt", ignore = true)
    @Mapping(target = "deliveredAt", ignore = true)
    @Mapping(target = "cancelledAt", ignore = true)
    @Mapping(target = "cancelReason", ignore = true)
    Order toEntity(OrderRequest orderRequest);
    
    OrderResponse toResponse(Order order);
}
