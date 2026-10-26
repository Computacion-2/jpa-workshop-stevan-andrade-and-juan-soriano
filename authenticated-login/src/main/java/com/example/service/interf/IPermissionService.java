package com.example.service.interf;

import java.util.List;

import com.example.exception.PermissionEmptyException;
import com.example.exception.PermissionNoFoundException;
import com.example.model.Permission;

public interface IPermissionService {
    
    //Querys
    List<Permission> findAll();
    Permission findById(int id) throws PermissionEmptyException, PermissionNoFoundException;

    //Interaction with Permission Table
    void create(Permission permissionToAdd) throws PermissionEmptyException;
    void update(Permission permissionToUpdate) throws PermissionEmptyException, PermissionNoFoundException;
    void delete(Permission permissionToDelete) throws PermissionEmptyException, PermissionNoFoundException;

}
