package com.agi.user.repository;

import com.agi.user.entity.RolePermission;
import com.agi.user.entity.Role;
import com.agi.user.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    List<RolePermission> findByRole(Role role);
    
    List<RolePermission> findByPermission(Permission permission);
    
    Optional<RolePermission> findByRoleAndPermission(Role role, Permission permission);
    
    boolean existsByRoleAndPermission(Role role, Permission permission);
    
    @Query("SELECT rp FROM RolePermission rp WHERE rp.role.id = :roleId")
    List<RolePermission> findByRoleId(Long roleId);
    
    @Query("SELECT rp FROM RolePermission rp WHERE rp.permission.id = :permissionId")
    List<RolePermission> findByPermissionId(Long permissionId);
}
