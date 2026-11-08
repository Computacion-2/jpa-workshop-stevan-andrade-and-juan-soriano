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

import com.example.dto.RoleRequestDTO;
import com.example.dto.RoleResponseDTO;
import com.example.service.interf.IRoleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor 
public class RoleController {

    private final IRoleService roleService;

    @GetMapping
    public List<RoleResponseDTO> findAll() {
        return roleService.findAll();
    }

    @GetMapping("/{id}")
    public RoleResponseDTO findById(@PathVariable Long id) {
        return roleService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoleResponseDTO create(@RequestBody RoleRequestDTO request) {
        return roleService.create(request);
    }

    @PutMapping("/{id}")
    public RoleResponseDTO update(@PathVariable Long id, @RequestBody RoleRequestDTO request) {
        return roleService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        roleService.delete(id);
    }

    @PostMapping("/{roleId}/permissions/{permissionId}")
    public RoleResponseDTO addPermission(@PathVariable Long roleId, @PathVariable Long permissionId) {
        return roleService.addPermission(roleId, permissionId);
    }

    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    public RoleResponseDTO removePermission(@PathVariable Long roleId, @PathVariable Long permissionId) {
        return roleService.removePermission(roleId, permissionId);
    }
}
