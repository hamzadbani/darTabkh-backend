package com.darTabkh.darTabkh.mapper;

import com.darTabkh.darTabkh.dto.RegisterRequest;
import com.darTabkh.darTabkh.dto.UserResponse;
import com.darTabkh.darTabkh.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    User toEntity(RegisterRequest registerRequest);
    
    UserResponse toResponse(User user);
}
