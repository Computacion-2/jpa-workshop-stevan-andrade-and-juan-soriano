package com.example.repository;

import com.example.model.RolePermission;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {

    boolean existsByPermissionId(Long permissionId);

    Optional<RolePermission> findByRoleIdAndPermissionId(Long roleId, Long permissionId);
}
