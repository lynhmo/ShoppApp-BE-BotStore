package com.llu1ts.shopapp.service.svc;

import com.llu1ts.shopapp.dto.LoginDTO;
import com.llu1ts.shopapp.dto.UserDTO;
import com.llu1ts.shopapp.entity.User;
import com.llu1ts.shopapp.exception.AuthorizationException;
import com.llu1ts.shopapp.exception.DataNotFoundException;
import com.llu1ts.shopapp.response.JwtResponse;

public interface UserService {
    void createUser(UserDTO user) throws DataNotFoundException;

    JwtResponse login(LoginDTO dto) throws AuthorizationException;
}
