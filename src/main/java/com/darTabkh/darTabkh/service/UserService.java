package com.darTabkh.darTabkh.service;

import com.darTabkh.darTabkh.dto.UserResponse;
import com.darTabkh.darTabkh.entity.User;
import com.darTabkh.darTabkh.exception.ResourceNotFoundException;
import com.darTabkh.darTabkh.mapper.UserMapper;
import com.darTabkh.darTabkh.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return userMapper.toResponse(user);
    }
}
