package com.example.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.model.Role;

@Repository 
public interface IRoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByName(String name);
    boolean existsByName(String name);
    
}
