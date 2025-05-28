# JPA 엔티티 설계 가이드라인

## 다대다(ManyToMany) 관계 처리 원칙

JPA에서 엔티티 간 다대다 관계를 설계할 때는 `@ManyToMany` 어노테이션을 직접 사용하지 않고, 중간 테이블을 명시적으로 생성하여 `@OneToMany`와 `@ManyToOne` 관계의 조합으로 구현합니다.

### 권장 방식

```java
// 권장되지 않는 방식 (지양)
@Entity
public class User {
    @Id @GeneratedValue
    private Long id;
    
    @ManyToMany
    @JoinTable(name = "user_role",
               joinColumns = @JoinColumn(name = "user_id"),
               inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
}

@Entity
public class Role {
    @Id @GeneratedValue
    private Long id;
    
    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();
}
```

```java
// 권장되는 방식 (지향)
@Entity
public class User {
    @Id @GeneratedValue
    private Long id;
    
    @OneToMany(mappedBy = "user")
    private Set<UserRole> userRoles = new HashSet<>();
}

@Entity
public class Role {
    @Id @GeneratedValue
    private Long id;
    
    @OneToMany(mappedBy = "role")
    private Set<UserRole> userRoles = new HashSet<>();
}

@Entity
public class UserRole {
    @Id @GeneratedValue
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
    
    // 추가 필드 확장 가능
    private LocalDateTime assignedAt;
    private String assignedBy;
    private boolean active;
}
```

### 장점

1. **명확한 데이터 모델링**: 중간 테이블이 명시적으로 엔티티로 표현되어 데이터 모델이 더 명확해집니다.
2. **확장성**: 중간 테이블에 추가 필드(생성일, 상태, 메타데이터 등)를 쉽게 추가할 수 있습니다.
3. **성능 최적화**: 필요한 데이터만 조회하는 쿼리 최적화가 용이합니다.
4. **데이터 무결성**: 중간 테이블에 대한 제약조건을 명시적으로 관리할 수 있습니다.
5. **비즈니스 로직**: 관계 자체에 대한 비즈니스 로직을 중간 엔티티에 구현할 수 있습니다.

### 구현 시 고려사항

1. 중간 테이블의 이름은 두 엔티티의 이름을 조합하여 명명합니다. (예: `UserRole`, `ProductCategory`)
2. 중간 테이블에는 항상 고유 식별자(`@Id`)를 부여합니다.
3. 양방향 관계를 설정할 때는 `mappedBy` 속성을 사용하여 관계의 주인을 명확히 합니다.
4. 중간 테이블에 필요한 추가 필드를 정의하고 적절한 인덱스를 설정합니다.
5. 연관 관계 편의 메서드를 구현하여 객체 그래프 탐색을 용이하게 합니다.

이 가이드라인을 준수함으로써 더 유지보수하기 쉽고, 확장 가능하며, 성능이 최적화된 엔티티 모델을 구현할 수 있습니다.
