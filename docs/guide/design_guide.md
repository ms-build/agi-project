# 설계 가이드라인

## 목차
1. [JPA 엔티티 설계 가이드라인](#jpa-엔티티-설계-가이드라인)
   - [다대다(ManyToMany) 관계 처리 원칙](#1-다대다manytomany-관계-처리-원칙)
   - [Setter 지양 및 빌더 패턴 사용 원칙](#2-setter-지양-및-빌더-패턴-사용-원칙)
   - [DB 테이블 스키마와 엔티티 간 정합성 유지 원칙](#3-db-테이블-스키마와-엔티티-간-정합성-유지-원칙)
   - [DTO 사용 원칙 (Request/Response 분리)](#4-dto-사용-원칙-requestresponse-분리)
   - [기본 생성자 접근 제한 원칙](#5-기본-생성자-접근-제한-원칙)
   - [생성자 레벨 빌더 패턴 적용 원칙](#6-생성자-레벨-빌더-패턴-적용-원칙)
2. [코드 변경 및 테스트 프로세스](#코드-변경-및-테스트-프로세스)
   - [코드 변경 워크플로우](#코드-변경-워크플로우)
   - [컴파일 테스트](#컴파일-테스트)
   - [빌드 테스트](#빌드-테스트)
   - [테스트 통과 확인](#테스트-통과-확인)
   - [Git 커밋 및 푸시](#git-커밋-및-푸시)

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

## 4. DTO 사용 원칙 (Request/Response 분리)

컨트롤러 계층이나 서비스 계층에서 엔티티(@Entity)를 직접 파라미터로 받거나 반환하지 않고, 반드시 Request DTO와 Response DTO를 사용하여 데이터를 전달합니다.

### 권장 방식

```java
// 엔티티
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id @GeneratedValue
    private Long id;
    private String username;
    private String email;
    private int age;

    @Builder
    public Member(String username, String email, int age) {
        this.username = username;
        this.email = email;
        this.age = age;
    }
}

// Request DTO
@Getter
@NoArgsConstructor
public class MemberCreateRequest {
    private String username;
    private String email;
    private int age;

    public Member toEntity() {
        return Member.builder()
            .username(username)
            .email(email)
            .age(age)
            .build();
    }
}

// Response DTO
@Getter
public class MemberResponse {
    private Long id;
    private String username;
    private String email;

    public MemberResponse(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
        this.email = member.getEmail();
        // 필요한 정보만 노출 (예: age 제외)
    }
}

// 컨트롤러 예시
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/members")
    public ResponseEntity<MemberResponse> createMember(@RequestBody MemberCreateRequest request) {
        Member member = request.toEntity();
        Member savedMember = memberService.save(member);
        MemberResponse response = new MemberResponse(savedMember);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/members/{id}")
    public ResponseEntity<MemberResponse> getMember(@PathVariable Long id) {
        Member member = memberService.findById(id);
        MemberResponse response = new MemberResponse(member);
        return ResponseEntity.ok(response);
    }
}
```

### 장점

1.  **계층 간 분리**: 프레젠테이션 계층(Controller)과 도메인 계층(Entity)의 의존성을 낮춥니다.
2.  **API 스펙 명확화**: API 요청 및 응답 형식을 명확하게 정의하여 외부 시스템과의 연동을 용이하게 합니다.
3.  **데이터 노출 제어**: 엔티티의 모든 필드가 아닌, API 스펙에 필요한 필드만 선택적으로 노출할 수 있습니다.
4.  **엔티티 보호**: 외부로부터 엔티티의 직접적인 변경을 방지하고, DTO를 통한 유효성 검증 계층을 추가할 수 있습니다.
5.  **유연성**: 엔티티 구조 변경 시 API 스펙에 미치는 영향을 최소화할 수 있습니다.

### 구현 시 고려사항

1.  요청(Request)과 응답(Response) DTO를 명확히 분리합니다.
2.  DTO는 단순 데이터 전달 객체이므로 비즈니스 로직을 포함하지 않도록 합니다.
3.  엔티티와 DTO 간 변환 로직은 DTO 내부(생성자, 정적 팩토리 메서드) 또는 별도의 매퍼 클래스(MapStruct 등)를 이용합니다.
4.  필요에 따라 DTO에 유효성 검증 어노테이션(@NotNull, @Size 등)을 추가합니다.

## 5. 기본 생성자 접근 제한 원칙

JPA 엔티티에는 `@NoArgsConstructor(access = AccessLevel.PROTECTED)`를 사용하여 기본 생성자의 접근 수준을 `protected`로 제한합니다.

### 권장 방식

```java
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 접근 제한
public class Order {
    @Id @GeneratedValue
    private Long id;
    private String orderNumber;
    private LocalDateTime orderDate;

    @Builder
    public Order(String orderNumber) {
        this.orderNumber = orderNumber;
        this.orderDate = LocalDateTime.now();
    }
}

// 사용 예시
// Order order = new Order(); // 컴파일 오류 발생 (protected 접근 불가)
Order order = Order.builder()
    .orderNumber("ORD12345")
    .build();
```

### 장점

1.  **객체 생성 방식 강제**: 빌더 패턴이나 정적 팩토리 메서드를 통한 객체 생성을 유도하여 일관성을 유지합니다.
2.  **무분별한 객체 생성 방지**: 의미 없는 기본 생성자를 통한 객체 생성을 막아 잠재적인 오류를 줄입니다.
3.  **JPA 호환성 유지**: JPA 스펙에서는 엔티티에 기본 생성자(public 또는 protected)를 요구하므로, `protected`로 설정하여 스펙을 만족시키면서 외부에서의 직접적인 사용은 제한합니다.

### 구현 시 고려사항

1.  Lombok의 `@NoArgsConstructor(access = AccessLevel.PROTECTED)`를 사용합니다.
2.  객체 생성이 필요한 경우, 반드시 빌더 패턴이나 정적 팩토리 메서드를 제공합니다.

## 6. 생성자 레벨 빌더 패턴 적용 원칙

Lombok의 `@Builder` 어노테이션을 엔티티 클래스 레벨이 아닌, 특정 생성자 레벨에 적용하여 빌더를 생성합니다.

### 권장 방식

```java
// 권장되지 않는 방식 (지양): 클래스 레벨 빌더
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder // 클래스 레벨에 적용
@AllArgsConstructor // 모든 필드를 받는 생성자가 필요
public class Item {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private int price;
    private int stockQuantity;
    private LocalDateTime registeredAt;
}

// 사용 예시 (지양)
Item item = Item.builder()
    .name("상품A")
    .price(10000)
    .stockQuantity(10)
    .registeredAt(LocalDateTime.now()) // id는 자동 생성되지만 빌더에 포함될 수 있음
    .build();
```

```java
// 권장되는 방식 (지향): 생성자 레벨 빌더
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private int price;
    private int stockQuantity;
    private LocalDateTime registeredAt;

    @Builder // 생성자 레벨에 적용
    public Item(String name, int price, int stockQuantity) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.registeredAt = LocalDateTime.now(); // 생성 시점 자동 설정
    }
    
    // 필요 시 다른 생성자 추가 가능
}

// 사용 예시 (지향)
Item item = Item.builder()
    .name("상품A")
    .price(10000)
    .stockQuantity(10)
    // registeredAt은 생성자에서 자동 설정됨
    .build();
```

### 장점

1.  **명확한 생성 책임**: 빌더를 통해 객체를 생성할 때 어떤 필드들이 초기화되는지 명확하게 제어할 수 있습니다.
2.  **필수 값 강제 용이**: 빌더를 적용할 생성자의 파라미터를 통해 필수 값을 강제하기 용이합니다.
3.  **내부 로직 통합**: 생성자 내에서 초기화 로직(예: `registeredAt` 자동 설정)을 통합하여 관리할 수 있습니다.
4.  **유연성**: 여러 종류의 생성자에 각각 다른 빌더를 적용하여 다양한 객체 생성 시나리오를 지원할 수 있습니다.

### 구현 시 고려사항

1.  빌더를 적용할 생성자를 명시적으로 정의하고 `@Builder` 어노테이션을 해당 생성자 위에 붙입니다.
2.  클래스 레벨의 `@Builder`는 사용하지 않습니다.
3.  생성자에서 초기화할 필드와 그렇지 않은 필드를 명확히 구분합니다.
4.  JPA 요구사항인 `@NoArgsConstructor(access = AccessLevel.PROTECTED)`는 별도로 유지합니다.

# 코드 변경 및 테스트 프로세스

## 코드 변경 워크플로우

모든 코드 변경은 다음 워크플로우를 따라 진행합니다:

1. 코드 변경
2. 컴파일 테스트 (./gradlew compileJava)
3. 빌드 테스트 (./gradlew build)
4. 테스트 통과 확인
5. Git 커밋 및 푸시

이 프로세스를 철저히 준수하여 GitHub에 푸시되는 코드의 품질을 보장합니다.

## 컴파일 테스트

코드 변경 후 반드시 컴파일 테스트를 수행하여 문법 오류, 타입 오류, 순환 참조 등의 기본적인 문제를 확인합니다.

```bash
# 프로젝트 루트 디렉토리에서 실행
./gradlew compileJava
```

컴파일 테스트는 다음과 같은 문제를 감지합니다:
- 문법 오류
- 타입 불일치
- 누락된 의존성
- 기본적인 순환 참조
- 패키지 구조 문제

## 빌드 테스트

컴파일 테스트 통과 후, 전체 빌드 테스트를 수행하여 단위 테스트, 통합 테스트 등 모든 테스트가 통과하는지 확인합니다.

```bash
# 프로젝트 루트 디렉토리에서 실행
./gradlew build
```

빌드 테스트는 다음 단계를 포함합니다:
- 컴파일
- 단위 테스트 실행
- 통합 테스트 실행
- JAR 파일 생성
- 정적 분석 (설정된 경우)

## 테스트 통과 확인

빌드 결과를 확인하여 모든 테스트가 통과했는지 확인합니다. 테스트 실패 시 원인을 분석하고 수정합니다.

```bash
# 테스트 결과 상세 확인
./gradlew test --info

# 테스트 리포트 확인 (HTML)
# build/reports/tests/test/index.html
```

테스트 실패 시 체크리스트:
- 실패한 테스트 케이스 확인
- 실패 원인 분석 (로그 확인)
- 코드 수정 및 재테스트
- 모든 테스트 통과 확인

## Git 커밋 및 푸시

모든 테스트가 통과한 후에만 Git 커밋 및 푸시를 진행합니다.

```bash
# 변경 파일 확인
git status

# 변경 파일 스테이징
git add <변경된_파일_경로>

# 커밋
git commit -m "명확한 커밋 메시지"

# 푸시
git push origin <브랜치명>
```

커밋 메시지 작성 가이드라인:
- 명확하고 간결한 제목 (50자 이내)
- 필요시 본문에 상세 설명 추가
- 관련 이슈 번호 포함 (있는 경우)
- 변경 유형 명시 (feat, fix, refactor, docs 등)

예시:
```
feat: 회원 가입 기능 구현

- 이메일 인증 프로세스 추가
- 중복 회원 검증 로직 구현
- 비밀번호 암호화 적용

관련 이슈: #123
```

이 가이드라인들을 종합적으로 준수함으로써 더욱 견고하고 유지보수하기 쉬우며, 협업에 용이한 코드베이스를 구축할 수 있습니다.
