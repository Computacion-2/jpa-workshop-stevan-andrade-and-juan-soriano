package com.example.service.interf;

import java.util.List;

import com.example.exception.PermissionEmptyException;
import com.example.exception.PermissionNoFoundException;
import com.example.exception.RoleEmptyExcepcion;
import com.example.exception.RoleNoFoundException;
import com.example.model.Role;

public interface IRoleService {
    
    //Querys
    List<Role> findAll();
    Role findById(int id) throws RoleEmptyExcepcion, RoleNoFoundException;
    boolean existsByName(String name);

    //Interaction with roles table
    void create(Role roleToAdd) throws RoleEmptyExcepcion;
    void update(Role roleToUpdate) throws RoleEmptyExcepcion, RoleNoFoundException;
    void delete(Role roleToDelete)throws RoleEmptyExcepcion, RoleNoFoundException;

    void assignPermissions(int roleId, List<Integer> permissionIds)
            throws RoleEmptyExcepcion, RoleNoFoundException, PermissionEmptyException, PermissionNoFoundException;

}
