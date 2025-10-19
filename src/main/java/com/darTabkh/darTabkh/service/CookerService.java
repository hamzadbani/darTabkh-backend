package com.darTabkh.darTabkh.service;

import com.darTabkh.darTabkh.dto.UserResponse;
import com.darTabkh.darTabkh.entity.Role;
import com.darTabkh.darTabkh.entity.User;
import com.darTabkh.darTabkh.mapper.UserMapper;
import com.darTabkh.darTabkh.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CookerService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserResponse> getCookersByCity(String city) {
        List<User> cookers = userRepository.findByRoleAndCity(Role.COOKER, city);
        return cookers.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getAllCookers() {
        List<User> cookers = userRepository.findByRole(Role.COOKER);
        return cookers.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }
}
