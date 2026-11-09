package com.example.service.impl;

import com.example.exception.ResourceNotFoundException;
import com.example.dto.PermissionRequestDTO;
import com.example.dto.PermissionResponseDTO;
import com.example.mapper.IPermissionMapper;
import com.example.model.Permission;
import com.example.repository.PermissionRepository;
import com.example.service.interf.IPermissionService;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor 
public class PermissionService implements IPermissionService {

    private final PermissionRepository permissionRepository;
    private final IPermissionMapper permissionMapper;

    /*
    Hey Sori, how we must change service to return dto, templates from MVC don't will show info. Because we was providing 
    entity data. That's ones of your task to fix. It's important you don't create another service.
    */
   @Override 
    public List<PermissionResponseDTO> findAll() {

        List<Permission> permissions = permissionRepository.findAll();
        List<PermissionResponseDTO> permissionResponseDTOs = new ArrayList<PermissionResponseDTO>();
        
        if (!permissions.isEmpty()) 
            for (Permission permission : permissions) permissionResponseDTOs.add(permissionMapper.toResponse(permission));

        return permissionResponseDTOs;

    }

    @Override 
    public PermissionResponseDTO findById(Long id) {
        
        Optional<Permission> permissionSearch = permissionRepository.findById(id);

        Permission permission = permissionSearch.orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con id " + id));

        return permissionMapper.toResponse(permission);

    }

    @Override 
    public Permission findByIdDB(Long id) {

        if (id < 0) throw new IllegalArgumentException();

        Optional<Permission> permissionSearch = permissionRepository.findById(id);
        return permissionSearch.orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con id " + id));
    }

    @Override
    public PermissionResponseDTO create(PermissionRequestDTO permissionRequestDTO) {

        if (permissionRequestDTO == null) throw new IllegalArgumentException();

        Permission permissionToAdd = permissionMapper.toEntity(permissionRequestDTO);
        Permission permissionAdded = permissionRepository.save(permissionToAdd);

        return permissionMapper.toResponse(permissionAdded);

    }

    @Override 
    public PermissionResponseDTO update(Long id, PermissionRequestDTO permissionRequestDTO) {

        Permission permission = findByIdDB(id);
        permission.setName(permissionRequestDTO.getName());
        permission.setDescription(permissionRequestDTO.getDescription());
        return permissionMapper.toResponse(permissionRepository.save(permission));

    }

    @Override 
    public PermissionResponseDTO delete(Long id) {

        Permission permission = findByIdDB(id);
        if (!permission.getRolePermissions().isEmpty()) {
            throw new IllegalArgumentException("No se puede eliminar un permiso que tiene roles asignados");
        }

        permissionRepository.delete(permission);
        return permissionMapper.toResponse(permission);

    }
}
