package com.agi.user.repository;

import com.agi.user.entity.UserRole;
import com.agi.user.entity.User;
import com.agi.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findByUser(User user);
    
    List<UserRole> findByRole(Role role);
    
    Optional<UserRole> findByUserAndRole(User user, Role role);
    
    boolean existsByUserAndRole(User user, Role role);
    
    @Query("SELECT ur FROM UserRole ur WHERE ur.user.id = :userId")
    List<UserRole> findByUserId(Long userId);
    
    @Query("SELECT ur FROM UserRole ur WHERE ur.role.id = :roleId")
    List<UserRole> findByRoleId(Long roleId);
}
