package com.llu1ts.shopapp.service.svc;

import com.llu1ts.shopapp.dto.LoginDTO;
import com.llu1ts.shopapp.dto.UserDTO;
import com.llu1ts.shopapp.exception.AuthorizationException;
import com.llu1ts.shopapp.exception.DataNotFoundException;
import com.llu1ts.shopapp.response.JwtResponse;
import com.llu1ts.shopapp.response.UserResponse;

public interface UserService {
    void createUser(UserDTO user) throws DataNotFoundException;

    UserResponse getUserById(Long id) throws DataNotFoundException;

    UserResponse updateUser(Long id, UserDTO user) throws DataNotFoundException;

    JwtResponse login(LoginDTO dto) throws AuthorizationException;
}
