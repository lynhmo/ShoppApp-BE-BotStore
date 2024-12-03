package com.llu1ts.shopapp.service.impl;


import com.llu1ts.shopapp.dto.UserDTO;
import com.llu1ts.shopapp.entity.Role;
import com.llu1ts.shopapp.entity.User;
import com.llu1ts.shopapp.exception.DataNotFoundException;
import com.llu1ts.shopapp.repo.RoleRepository;
import com.llu1ts.shopapp.repo.UserRepository;
import com.llu1ts.shopapp.service.svc.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UserImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public User creatUser(UserDTO user) throws DataNotFoundException {
        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            throw new DataIntegrityViolationException("Phone number already exists");
        }
        User newUser = new User();
        BeanUtils.copyProperties(user, newUser);
        Role role = roleRepository.findById(user.getRoleId()).orElseThrow(() ->
                new DataNotFoundException("Role id ")
        );
        newUser.setRole(role);

        if (user.getFacebookAccountId() == 0 && user.getGoogleAccountId() == 0) {
            //TODO
        }

        return userRepository.save(newUser);
    }

    @Override
    public User login(String phone, String password) {
        return null;
    }
}
