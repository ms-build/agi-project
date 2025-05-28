package com.agi.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "permissions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Permission {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 100)
    private String name;
    
    @Column
    private String description;
    
    @OneToMany(mappedBy = "permission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RolePermission> rolePermissions = new ArrayList<>();
    
    @Builder
    public Permission(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    public void addRole(Role role) {
        RolePermission rolePermission = RolePermission.builder()
                .role(role)
                .permission(this)
                .build();
        this.rolePermissions.add(rolePermission);
    }
    
    public void removeRole(Role role) {
        this.rolePermissions.removeIf(rolePermission -> rolePermission.getRole().equals(role));
    }
    
    public void updateDescription(String description) {
        this.description = description;
    }
}
