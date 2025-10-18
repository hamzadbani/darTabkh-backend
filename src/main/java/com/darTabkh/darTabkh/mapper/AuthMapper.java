package com.darTabkh.darTabkh.mapper;

import com.darTabkh.darTabkh.dto.AuthResponse;
import com.darTabkh.darTabkh.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AuthMapper {
    
    AuthMapper INSTANCE = Mappers.getMapper(AuthMapper.class);
    
    @Mapping(target = "token", ignore = true)
    @Mapping(target = "type", ignore = true)
    AuthResponse toAuthResponse(User user);
}
