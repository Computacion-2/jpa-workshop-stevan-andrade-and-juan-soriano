package com.example.controller.rest;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.User;
import com.example.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor 
public class UserController {

    private final UserService userService;

    // @GetMapping
    // public List<UserResponse> findAll() {
    //     return userService.findAll().stream().map(UserResponse::from).toList();
    // }

    // @GetMapping("/{id}")
    // public UserResponse findById(@PathVariable Long id) {
    //     return UserResponse.from(userService.findById(id));
    // }

    // @PostMapping
    // @ResponseStatus(HttpStatus.CREATED)
    // public UserResponse create(@RequestBody UserRequest request) {
    //     return UserResponse.from(userService.create(
    //             request.name(), request.email(), request.password(), request.roleId()));
    // }

    // @PutMapping("/{id}")
    // public UserResponse update(@PathVariable Long id, @RequestBody UserRequest request) {
    //     return UserResponse.from(userService.update(
    //             id, request.name(), request.email(), request.password(), request.roleId()));
    // }

    // @DeleteMapping("/{id}")
    // @ResponseStatus(HttpStatus.NO_CONTENT)
    // public void delete(@PathVariable Long id) {
    //     userService.delete(id);
    // }
}
