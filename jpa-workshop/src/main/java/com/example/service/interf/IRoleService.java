package com.example.service.interf;

import java.util.List;
import java.util.Set;

import com.example.dto.RoleRequestDTO;
import com.example.dto.RoleResponseDTO;
import com.example.model.Role;

public interface IRoleService {
    
    List<RoleResponseDTO> findAll();

    RoleResponseDTO findById(Long id);

    Role findByIdDB(Long id);
    
    RoleResponseDTO findByName(String name);

    Role findByNameDB(String name);

    RoleResponseDTO create(RoleRequestDTO roleRequestDTO);

    RoleResponseDTO update(Long id, RoleRequestDTO roleRequestDTO);
    
    RoleResponseDTO delete(Long id);

    RoleResponseDTO addPermission(Long roleId, Long permissionId);

    RoleResponseDTO removePermission(Long roleId, Long permissionId);

    RoleResponseDTO setPermissions(Long roleId, Set<Long> permissionIds);

}
