package com.llu1ts.shopapp.service.impl;


import com.llu1ts.shopapp.dto.LoginDTO;
import com.llu1ts.shopapp.dto.PasswordDTO;
import com.llu1ts.shopapp.dto.UserDTO;
import com.llu1ts.shopapp.dto.UserUpdateDTO;
import com.llu1ts.shopapp.entity.Order;
import com.llu1ts.shopapp.entity.OrderStatus;
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
import com.llu1ts.shopapp.service.svc.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserImpl implements UserService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;

    @Override
    public void updatePassword(Long uid, PasswordDTO passwordDTO) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization").substring(7);
        String uidFromToken = jwtTokenUtils.getUserId(token);

        // Check nếu user hiện tại có thay đổi pass của user khác không
        if (!uidFromToken.equals(uid.toString())) {
            throw new AuthorizationException("Không cho phép thay đổi mật khẩu");
        }

        // Get info user hiện tại
        Optional<User> user = userRepository.findById(uid);
        if (user.isEmpty()) {
            throw new AuthorizationException("User not found");
        }

        // So sánh pass
        String oldPassword = user.get().getPassword();
        String oldPasswordFromRequest = passwordDTO.getOldPassword();
        if (!passwordEncoder.matches(oldPasswordFromRequest,oldPassword)) {
            throw new AuthorizationException("Old password doesn't match");
        }

        // So sánh 2 pass mới
        if (!passwordDTO.getNewPassword().equals(passwordDTO.getConfirmPassword())) {
            throw new AuthorizationException("Confirm password doesn't match");
        }

        String encryptedPassword = passwordEncoder.encode(passwordDTO.getNewPassword());
        user.get().setPassword(encryptedPassword);
        userRepository.save(user.get());
    }

    @Override
    public void deleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new AuthorizationException("User not found");
        }
        user.get().setIsActive(false);
        userRepository.save(user.get());
    }

    @Override
    public List<UserResponse> allUsers() {
        List<UserResponse> users = new ArrayList<>();
        userRepository.findAll().forEach(user -> {
            UserResponse userResponse = new UserResponse();
            BeanUtils.copyProperties(user, userResponse);
            users.add(userResponse);
        });
        return users;
    }

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

        Order order = new Order();
        order.setUserId(newUser);
        order.setOrderDate(new Date(System.currentTimeMillis()));
        order.setStatus(OrderStatus.PENDING);
        order.setActive(true);
        order.setAddress("");
        order.setPhoneNumber(newUser.getPhoneNumber());
        orderRepository.save(order);
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
        String token = request.getHeader("Authorization").substring(7);
        String uid = jwtTokenUtils.getUserId(token);

        if (jwtTokenUtils.isAdmin(token)) {
            Optional<User> user = userRepository.findById(id);
            if (user.isEmpty()) {
                throw new DataNotFoundException("User not found");
            }
            UserResponse userResponse = new UserResponse();
            BeanUtils.copyProperties(user.get(), userResponse);
            return userResponse;
        }

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
    public UserResponse updateUser(Long id, UserUpdateDTO dto) throws DataNotFoundException {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new AuthorizationException("Bad credentials");
        }
        Role role = roleRepository.findById(dto.getRoleId()).orElseThrow(() ->
                new DataNotFoundException("Role not found")
        );

        BeanUtils.copyProperties(dto, user.get());
        user.get().setRole(role);
        if (dto.getBirthday() != null) {
            user.get().setDateOfBirth(Date.from(dto.getBirthday().atStartOfDay(ZoneId.systemDefault()).toInstant()));

        }
        userRepository.updateUser(user.get());

        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(user.get(), userResponse);

        return userResponse;
    }
}
