package com.agi.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "roles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String name;
    
    @Column(length = 200)
    private String description;
    
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserRole> userRoles = new ArrayList<>();
    
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RolePermission> rolePermissions = new ArrayList<>();
    
    @Builder
    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    public void addPermission(Permission permission) {
        RolePermission rolePermission = RolePermission.builder()
                .role(this)
                .permission(permission)
                .build();
        this.rolePermissions.add(rolePermission);
    }
    
    public void removePermission(Permission permission) {
        this.rolePermissions.removeIf(rolePermission -> rolePermission.getPermission().equals(permission));
    }
    
    public void updateDescription(String description) {
        this.description = description;
    }
    
    public void addUser(User user) {
        UserRole userRole = UserRole.builder()
                .user(user)
                .role(this)
                .build();
        this.userRoles.add(userRole);
    }
    
    public void removeUser(User user) {
        this.userRoles.removeIf(userRole -> userRole.getUser().equals(user));
    }
}
