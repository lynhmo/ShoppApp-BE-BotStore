package com.llu1ts.shopapp.service.impl;


import com.llu1ts.shopapp.dto.LoginDTO;
import com.llu1ts.shopapp.dto.OrderDTO;
import com.llu1ts.shopapp.dto.UserDTO;
import com.llu1ts.shopapp.entity.Role;
import com.llu1ts.shopapp.entity.User;
import com.llu1ts.shopapp.exception.AuthorizationException;
import com.llu1ts.shopapp.exception.DataNotFoundException;
import com.llu1ts.shopapp.repo.OrderRepository;
import com.llu1ts.shopapp.repo.RoleRepository;
import com.llu1ts.shopapp.repo.UserRepository;
import com.llu1ts.shopapp.response.JwtResponse;
import com.llu1ts.shopapp.response.UserResponse;
import com.llu1ts.shopapp.security.JwtTokenUtils;
import com.llu1ts.shopapp.service.svc.OrderService;
import com.llu1ts.shopapp.service.svc.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserImpl implements UserService {

    private final OrderService orderService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;


    @Value("${jwt.expiration}")
    private long expirationTime;

    @Override
    public void createUser(UserDTO userDTO) throws DataNotFoundException {
        if (userRepository.existsByPhoneNumber(userDTO.getPhoneNumber())) {
            throw new DataIntegrityViolationException("Username is already in use");
        }
        // Để role user mặc định
        String user = "USER";
        Role role = roleRepository.findByName(user.toUpperCase()).orElseThrow(() ->
                new DataNotFoundException("Role not found")
        );

        User newUser = new User();
        BeanUtils.copyProperties(userDTO, newUser);
        newUser.setRole(role);

        if (userDTO.getFacebookAccountId() == null) {
            newUser.setFacebookAccountId(0L);
            userDTO.setFacebookAccountId(0L);
        }
        if (userDTO.getGoogleAccountId() == null) {
            newUser.setGoogleAccountId(0L);
            userDTO.setGoogleAccountId(0L);
        }

        if (userDTO.getFacebookAccountId() == 0 && userDTO.getGoogleAccountId() == 0) {
            String password = passwordEncoder.encode(userDTO.getPassword());
            newUser.setPassword(password);
        }
        newUser.setIsActive(true); // set active user
        userRepository.save(newUser);

        // Đồng thời tạo cho user 1 order/cart

        OrderDTO orderDTO = OrderDTO.builder()
                .userId(newUser.getId())
                .phoneNumber(userDTO.getPhoneNumber())
                .address("")
                .build();

        orderService.createOrder(orderDTO);
    }

    @Override
    public JwtResponse login(LoginDTO dto) throws AuthorizationException {
        Optional<User> user = userRepository.findByPhoneNumber(dto.getPhoneNumber());
        if (user.isEmpty()) {
            throw new AuthorizationException("Bad credentials");
        }
        User currentUser = user.get();
        if (currentUser.getFacebookAccountId() == 0
                && currentUser.getGoogleAccountId() == 0
                && !passwordEncoder.matches(dto.getPassword(), currentUser.getPassword())) {
            throw new AuthorizationException("Bad credentials");
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(dto.getPhoneNumber(), dto.getPassword(),
                        currentUser.getAuthorities());
        authenticationManager.authenticate(authenticationToken);


        JwtResponse jwtResponse = new JwtResponse();
        String token = jwtTokenUtils.generateToken(currentUser);
        long expTime = jwtTokenUtils.getExpirationDate(token).getTime();
        String role = jwtTokenUtils.getRole(token) == null ? "" : jwtTokenUtils.getRole(token);
        jwtResponse.setRole(role);
        jwtResponse.setAccessToken(token);
        jwtResponse.setExpiresIn(expTime - System.currentTimeMillis());
        return jwtResponse;
    }

    @Override
    public UserResponse getUserById(Long id) throws DataNotFoundException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String uid = jwtTokenUtils.getUserId(request.getHeader("Authorization").substring(7));
        if (!uid.equals(id.toString())) {
            throw new AuthorizationException("Cannot get data from other user");
        }
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new DataNotFoundException("User not found");
        }
        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(user.get(), userResponse);
        return userResponse;
    }

    @Override
    public UserResponse updateUser(Long id, UserDTO user) throws DataNotFoundException {
        return null;
    }
}
