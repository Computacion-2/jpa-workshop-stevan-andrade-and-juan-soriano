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

import com.example.dto.PermissionRequestDTO;
import com.example.dto.PermissionResponseDTO;
import com.example.service.interf.IPermissionService;

import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor 
public class PermissionController {

    private final IPermissionService permissionService;

    @GetMapping
    public List<PermissionResponseDTO> findAll() {
        return permissionService.findAll();
    }

    @GetMapping("/{id}")
    public PermissionResponseDTO findById(@PathVariable Long id) {
        return permissionService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PermissionResponseDTO create(@Valid @RequestBody PermissionRequestDTO request) {
        return permissionService.create(request);
    }

    @PutMapping("/{id}")
    public PermissionResponseDTO update(@PathVariable Long id, @Valid @RequestBody PermissionRequestDTO request) {
        return permissionService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        permissionService.delete(id);
    }
}
