package com.example.service.interf;

import java.util.List;

import com.example.dto.PermissionRequestDTO;
import com.example.dto.PermissionResponseDTO;
import com.example.model.Permission;

public interface IPermissionService {
    
    List<PermissionResponseDTO> findAll();

    PermissionResponseDTO findById(Long id);

    Permission findByIdDB(Long id);

    PermissionResponseDTO create(PermissionRequestDTO permissionRequestDTO);

    PermissionResponseDTO update(Long id, PermissionRequestDTO permissionRequestDTO);

    PermissionResponseDTO delete(Long id);

}
