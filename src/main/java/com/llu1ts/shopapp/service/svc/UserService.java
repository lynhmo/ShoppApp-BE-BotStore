package com.llu1ts.shopapp.service.svc;

import com.llu1ts.shopapp.dto.UserDTO;
import com.llu1ts.shopapp.entity.User;
import com.llu1ts.shopapp.exception.DataNotFoundException;

public interface UserService {
    User creatUser(UserDTO user) throws DataNotFoundException;

    User login(String phone, String password);
}
