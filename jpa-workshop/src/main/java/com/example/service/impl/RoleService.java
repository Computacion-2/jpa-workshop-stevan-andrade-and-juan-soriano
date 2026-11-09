package com.example.service.impl;

import com.example.exception.ResourceNotFoundException;
import com.example.dto.RoleRequestDTO;
import com.example.dto.RoleResponseDTO;
import com.example.mapper.IRoleMapper;
import com.example.model.Permission;
import com.example.model.Role;
import com.example.model.RolePermission;
import com.example.repository.RoleRepository;
import com.example.service.interf.IPermissionService;
import com.example.service.interf.IRoleService;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor 
public class RoleService implements IRoleService {

    private final RoleRepository roleRepository;
    private final IPermissionService permissionService;
    private final IRoleMapper roleMapper;

    @Override 
    public List<RoleResponseDTO> findAll() {

        List<Role> roles = roleRepository.findAll();
        List<RoleResponseDTO> roleResponseDTOs = new ArrayList<RoleResponseDTO>();
        
        if (!roles.isEmpty())
            for (Role role : roles) roleResponseDTOs.add(roleMapper.toResponse(role));

        return roleResponseDTOs;

    }

    @Override 
    public RoleResponseDTO findById(Long id) {

        if (id < 0) throw new IllegalArgumentException();
        Optional<Role> role = roleRepository.findById(id);

        return roleMapper.toResponse(role
            .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id " + id)));

    }

    @Override 
    public Role findByIdDB(Long id) {

        if (id < 0) throw new IllegalArgumentException();
        Optional<Role> role = roleRepository.findById(id);

        return role.orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id " + id));

    }

    @Override 
    public RoleResponseDTO findByName(String name) {

        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException();
        Optional<Role> role = roleRepository.findByName(name);

        return roleMapper.toResponse(role
            .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: " + name)));

    }

    @Override 
    public Role findByNameDB(String name) {

        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException();
        Optional<Role> role = roleRepository.findByName(name);

        return role.orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: " + name));

    }

    @Override 
    public RoleResponseDTO create(RoleRequestDTO roleRequestDTO) {
        
        if (roleRequestDTO == null) throw new IllegalArgumentException();
        if (roleRequestDTO.getPermissionIds() == null || roleRequestDTO.getPermissionIds().isEmpty()) {
            throw new IllegalArgumentException("El rol debe tener al menos un permiso");
        }

        Role role = roleMapper.toEntity(roleRequestDTO);

        for (Long permissionId : roleRequestDTO.getPermissionIds()) {
            Permission permission = permissionService.findByIdDB(permissionId);
            role.addPermission(permission);
        }

        return roleMapper.toResponse(roleRepository.save(role));
    }

    @Override 
    public RoleResponseDTO update(Long id, RoleRequestDTO roleRequestDTO) {

        //Checks role with that ID exist, if didn't found anything throws an exception
        findByIdDB(id);
        Role role = roleMapper.toEntity(roleRequestDTO);
        role.setId(id);

        return roleMapper.toResponse(roleRepository.save(role));
    }

    @Override 
    public RoleResponseDTO delete(Long id) {
        
        Role role = findByIdDB(id);
        // User can exist without a role defined
        if (!role.getUsers().isEmpty()) {
            throw new IllegalArgumentException("No se puede eliminar un rol que tiene usuarios");
        }

        roleRepository.delete(role);
        return roleMapper.toResponse(role);
    }

    @Override 
    public RoleResponseDTO addPermission(Long roleId, Long permissionId) {

        Role role = findByIdDB(roleId);
        Permission permission = permissionService.findByIdDB(permissionId);

        for (RolePermission rolePermission : role.getRolePermissions()) 
            if (rolePermission.getPermission().getId().equals(permissionId)) 
                throw new IllegalArgumentException("El rol ya tiene ese permiso");
            
        role.addPermission(permission);
        return roleMapper.toResponse(roleRepository.save(role));

    }

    @Override 
    public RoleResponseDTO removePermission(Long roleId, Long permissionId) {

        Role role = findByIdDB(roleId);
        RolePermission toRemove = null;
        for (RolePermission rolePermission : role.getRolePermissions()) 
            if (rolePermission.getPermission().getId().equals(permissionId)) 
                toRemove = rolePermission;
            
        if (toRemove == null) 
            throw new IllegalArgumentException("El rol no tiene ese permiso");
        
        if (role.getRolePermissions().size() == 1) 
            throw new IllegalArgumentException("El rol debe conservar al menos un permiso");
        
        role.getRolePermissions().remove(toRemove);
        return roleMapper.toResponse(roleRepository.save(role));

    }

    @Override 
    @Transactional
    public RoleResponseDTO setPermissions(Long roleId, Set<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            throw new IllegalArgumentException("El rol debe tener al menos un permiso");
        }
        Role role = findByIdDB(roleId);
        // se quitan los que no se marcaron y se agregan los nuevos, sin tocar los que ya tenía
        role.getRolePermissions().removeIf(rp -> !permissionIds.contains(rp.getPermission().getId()));
        Set<Long> currentIds = role.getRolePermissions().stream()
                .map(rp -> rp.getPermission().getId())
                .collect(Collectors.toSet());
        for (Long permissionId : permissionIds) {
            if (!currentIds.contains(permissionId)) {
                role.addPermission(permissionService.findByIdDB(permissionId));
            }
        }
        return roleMapper.toResponse(roleRepository.save(role));
    }
}
