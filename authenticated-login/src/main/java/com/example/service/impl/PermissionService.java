package com.example.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.exception.PermissionEmptyException;
import com.example.exception.PermissionNoFoundException;
import com.example.model.Permission;
import com.example.repository.IPermissionRepository;
import com.example.service.interf.IPermissionService;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class PermissionService implements IPermissionService {
    
    private final IPermissionRepository permissionRepository;

    @Override
    public List<Permission> findAll() {
        return permissionRepository.findAll();
    }

    @Override
    public Permission findById(int id) throws PermissionEmptyException, PermissionNoFoundException {
        
        if (id < 0) throw new PermissionEmptyException("No info provided to search Permission");

        Optional<Permission> permissionFound = permissionRepository.findById(id);

        if (permissionFound.isEmpty()) throw new PermissionNoFoundException("Permission didn't found in DB");

        return permissionFound.get();
    }

    @Override
    public void create(Permission permissionToAdd) throws PermissionEmptyException {
        
        if (permissionToAdd == null) throw new PermissionEmptyException("No info provided");

        permissionRepository.save(permissionToAdd);

    }

    @Override
    public void update(Permission permissionToUpdate) throws PermissionEmptyException, PermissionNoFoundException {
        
        if (permissionToUpdate == null) throw new PermissionNoFoundException("No info provided to search Permission");

        findById(permissionToUpdate.getId());

        permissionRepository.save(permissionToUpdate);

    }

    @Override
    public void delete(Permission permissionToDelete) throws PermissionEmptyException, PermissionNoFoundException {
        
        if (permissionToDelete == null) throw new PermissionEmptyException("No info provided to search Permission");

        findById(permissionToDelete.getId());

        permissionRepository.delete(permissionToDelete);
        
    }

}
