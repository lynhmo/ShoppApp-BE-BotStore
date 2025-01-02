package com.llu1ts.shopapp.controller;


import com.llu1ts.shopapp.dto.LoginDTO;
import com.llu1ts.shopapp.dto.UserDTO;
import com.llu1ts.shopapp.dto.UserUpdateDTO;
import com.llu1ts.shopapp.exception.DataNotFoundException;
import com.llu1ts.shopapp.exception.ErrorResponse;
import com.llu1ts.shopapp.response.SuccessResponse;
import com.llu1ts.shopapp.service.svc.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserCtrl {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDTO user, BindingResult bindingResult) throws DataNotFoundException {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errors.toString());
        }

        if (!user.getPassword().equals(user.getRetypePassword())) {
            return ResponseEntity.badRequest().body("Confirm password is not match");
        }

        userService.createUser(user);

        return ResponseEntity.ok(new SuccessResponse("Register successful", "1", null));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO user, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errors.toString());
        }
        return ResponseEntity.ok(userService.login(user));

    }

    @GetMapping("/user-details/{id}")
    public ResponseEntity<?> userDetail(@PathVariable Long id) throws DataNotFoundException {
        return ResponseEntity.ok(userService.getUserById(id));
    }


    @GetMapping("/all")
    public ResponseEntity<?> allUsers() throws DataNotFoundException {
        return ResponseEntity.ok(userService.allUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO user, BindingResult bindingResult) throws DataNotFoundException {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(new ErrorResponse(errors.toString(), "400"));
        }
        return ResponseEntity.ok(userService.updateUser(id, user));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) throws Exception {
        userService.deleteUser(id);
        return ResponseEntity.ok(new SuccessResponse("Delete successful", "1", null));
    }

}
