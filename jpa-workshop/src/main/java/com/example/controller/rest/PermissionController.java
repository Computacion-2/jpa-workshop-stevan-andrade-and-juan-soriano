package com.example.controller.rest;

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

import com.example.model.Permission;
import com.example.service.PermissionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor 
public class PermissionController {

    private final PermissionService permissionService;

    // @GetMapping
    // public List<PermissionResponse> findAll() {
    //     return permissionService.findAll().stream().map(PermissionResponse::from).toList();
    // }

    // @GetMapping("/{id}")
    // public PermissionResponse findById(@PathVariable Long id) {
    //     return PermissionResponse.from(permissionService.findById(id));
    // }

    // @PostMapping
    // @ResponseStatus(HttpStatus.CREATED)
    // public PermissionResponse create(@RequestBody PermissionRequest request) {
    //     return PermissionResponse.from(permissionService.create(request.name(), request.description()));
    // }

    // @PutMapping("/{id}")
    // public PermissionResponse update(@PathVariable Long id, @RequestBody PermissionRequest request) {
    //     return PermissionResponse.from(permissionService.update(id, request.name(), request.description()));
    // }

    // @DeleteMapping("/{id}")
    // @ResponseStatus(HttpStatus.NO_CONTENT)
    // public void delete(@PathVariable Long id) {
    //     permissionService.delete(id);
    // }
}
