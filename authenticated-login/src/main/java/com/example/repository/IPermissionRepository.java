package com.example.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.Permission;

public interface IPermissionRepository extends JpaRepository<Permission, Integer> {

    Optional<Permission> findByName(String name);
    
}
