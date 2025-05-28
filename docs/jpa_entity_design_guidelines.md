# JPA 엔티티 설계 가이드라인

## 1. 다대다(ManyToMany) 관계 처리 원칙

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

## 2. Setter 지양 및 빌더 패턴 사용 원칙

JPA 엔티티를 설계할 때 `@Setter`를 사용하지 않고, 대신 빌더 패턴을 활용하여 객체를 생성하고 값을 설정합니다.

### 권장 방식

```java
// 권장되지 않는 방식 (지양)
@Entity
@Setter
public class Product {
    @Id @GeneratedValue
    private Long id;
    
    private String name;
    private BigDecimal price;
    private String description;
    private LocalDateTime createdAt;
    private boolean active;
    
    // 기본 생성자
    public Product() {}
}

// 사용 예시 (지양)
Product product = new Product();
product.setName("스마트폰");
product.setPrice(new BigDecimal("1000000"));
product.setDescription("최신 스마트폰");
product.setCreatedAt(LocalDateTime.now());
product.setActive(true);
```

```java
// 권장되는 방식 (지향)
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 요구사항
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 빌더용
@Builder
public class Product {
    @Id @GeneratedValue
    private Long id;
    
    private String name;
    private BigDecimal price;
    private String description;
    private LocalDateTime createdAt;
    private boolean active;
    
    // 비즈니스 메서드
    public void updateDetails(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    public void deactivate() {
        this.active = false;
    }
}

// 사용 예시 (지향)
Product product = Product.builder()
    .name("스마트폰")
    .price(new BigDecimal("1000000"))
    .description("최신 스마트폰")
    .createdAt(LocalDateTime.now())
    .active(true)
    .build();
    
// 상태 변경은 의미 있는 비즈니스 메서드를 통해 수행
product.updateDetails("고급 스마트폰", "프리미엄 최신 스마트폰");
product.deactivate();
```

### 장점

1. **불변성 보장**: 객체 생성 후 상태 변경을 제한하여 불변성을 보장합니다.
2. **안전한 객체 생성**: 필수 값을 명확히 지정하고 유효성 검사를 빌더 내에서 수행할 수 있습니다.
3. **가독성 향상**: 많은 파라미터를 가진 객체 생성 시 가독성이 크게 향상됩니다.
4. **유지보수성**: 필드가 추가되거나 변경되어도 기존 코드에 영향을 최소화합니다.
5. **도메인 무결성**: 상태 변경은 의미 있는 비즈니스 메서드를 통해서만 가능하도록 제한합니다.

### 구현 시 고려사항

1. JPA 요구사항을 위해 `@NoArgsConstructor(access = AccessLevel.PROTECTED)`를 사용합니다.
2. 빌더 패턴 구현을 위해 Lombok의 `@Builder`와 `@AllArgsConstructor(access = AccessLevel.PRIVATE)`를 함께 사용합니다.
3. 상태 변경이 필요한 경우, setter 대신 의미 있는 이름의 비즈니스 메서드를 구현합니다.
4. 빌더 내에서 필수 값 검증 로직을 추가하여 유효한 객체만 생성되도록 합니다.
5. 연관 관계 설정을 위한 편의 메서드를 구현하여 일관된 객체 그래프를 유지합니다.

## 3. DB 테이블 스키마와 엔티티 간 정합성 유지 원칙

DB 테이블 스키마와 JPA 엔티티, 그리고 각 클래스의 필드와 메서드들 간의 정합성을 항상 유지해야 합니다.

### 권장 방식

```java
// 권장되지 않는 방식 (지양): DB 스키마와 엔티티 간 불일치
// DB 테이블: USERS (id, username, email, password, created_at)
@Entity
@Table(name = "USERS")
public class User {
    @Id @GeneratedValue
    private Long id;
    
    private String username; // DB 컬럼명과 일치
    private String email;    // DB 컬럼명과 일치
    
    @Column(name = "pwd")   // DB 컬럼명과 불일치 (DB는 password)
    private String password;
    
    // DB에 존재하는 created_at 컬럼이 엔티티에 누락됨
    
    @Transient
    private String temporaryToken; // DB에 없는 필드지만 @Transient 표시 없음
}
```

```java
// 권장되는 방식 (지향): DB 스키마와 엔티티 간 정합성 유지
// DB 테이블: USERS (id, username, email, password, created_at)
@Entity
@Table(name = "USERS")
public class User {
    @Id @GeneratedValue
    private Long id;
    
    private String username;
    private String email;
    
    @Column(name = "password") // DB 컬럼명과 명시적으로 일치시킴
    private String password;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Transient // DB에 저장되지 않는 필드 명시
    private String temporaryToken;
    
    // 엔티티 변경 시 DB 스키마 변경 코멘트 추가
    /**
     * 주의: 이 필드를 추가할 경우 DB 스키마 변경 필요:
     * ALTER TABLE USERS ADD COLUMN last_login_at TIMESTAMP;
     */
    // @Column(name = "last_login_at")
    // private LocalDateTime lastLoginAt;
}
```

### 장점

1. **데이터 일관성**: DB 스키마와 엔티티 간 일관성을 유지하여 데이터 불일치 문제를 방지합니다.
2. **명확한 매핑**: 컬럼명이 다른 경우 `@Column` 어노테이션으로 명시적 매핑을 제공합니다.
3. **유지보수성 향상**: 스키마 변경 시 엔티티도 함께 업데이트되어 유지보수가 용이합니다.
4. **버그 감소**: 필드와 컬럼 간 불일치로 인한 런타임 오류를 사전에 방지합니다.
5. **문서화**: 코드 자체가 DB 스키마의 문서 역할을 할 수 있습니다.

### 구현 시 고려사항

1. 엔티티 클래스의 필드명은 가능한 DB 컬럼명과 일치시킵니다.
2. 필드명과 컬럼명이 다를 경우 반드시 `@Column(name = "컬럼명")` 어노테이션을 사용합니다.
3. DB에 저장되지 않는 필드는 반드시 `@Transient` 어노테이션을 사용합니다.
4. 엔티티 변경 시 해당 변경이 DB 스키마에 미치는 영향을 주석으로 명시합니다.
5. 가능하면 DB 마이그레이션 스크립트(Flyway, Liquibase 등)를 함께 관리합니다.
6. 복합 키 사용 시 `@IdClass` 또는 `@EmbeddedId`를 사용하여 명확하게 매핑합니다.
7. 인덱스와 제약조건은 `@Table`, `@Index`, `@UniqueConstraint` 등을 통해 엔티티에 명시합니다.

이 가이드라인을 준수함으로써 더 유지보수하기 쉽고, 확장 가능하며, 성능이 최적화된 엔티티 모델을 구현할 수 있습니다.
