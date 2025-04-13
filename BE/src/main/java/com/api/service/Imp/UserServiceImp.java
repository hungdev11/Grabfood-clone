package com.api.service.Imp;

import com.api.repository.UserRepository;
import com.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImp implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public Long getUserIdByPhone(String phone) {
        return userRepository.findByPhone(phone).getId();
    }
}
