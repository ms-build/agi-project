# 통합 AGI 시스템 객체 모델 설계

## 1. 개요

이 문서는 Spring Boot 3.4.5, Java 17 기반의 통합 AGI 시스템을 위한 객체 모델 설계를 설명합니다. 객체 모델은 도메인 주도 설계(DDD) 원칙을 따르며, JPA(Java Persistence API)를 사용하여 데이터베이스와 상호작용합니다. 이 설계는 시스템의 핵심 도메인, 서비스, 저장소, DTO(Data Transfer Object) 구조를 정의하며, **샌드박스 환경에서의 안전한 코드 및 도구 실행**을 위한 객체 모델을 포함합니다.

## 2. 설계 원칙

1. **도메인 주도 설계(DDD)**: 핵심 도메인 로직을 엔티티와 값 객체에 집중시킵니다.
2. **도메인 중심 패키지 구조**: 기능적으로 관련된 컴포넌트를 도메인별로 그룹화하여 응집도를 높입니다.
3. **JPA 엔티티**: 데이터베이스 테이블과 매핑되는 영속성 객체로 설계합니다.
4. **DTO 분리**: 계층 간 데이터 전송을 위해 DTO를 사용하며, 엔티티를 직접 노출하지 않습니다.
5. **Lombok 활용**: `@Getter`, `@Builder`, `@NoArgsConstructor` 등을 사용하여 코드 간결성을 높입니다.
6. **인터페이스 기반 설계**: 서비스 및 저장소는 인터페이스를 정의하고 구현체에서 로직을 처리합니다.
7. **의존성 주입(DI)**: Spring 프레임워크의 DI를 활용하여 객체 간 의존성을 관리합니다.
8. **예외 처리**: 비즈니스 예외와 시스템 예외를 구분하여 처리합니다.
9. **테스트 용이성**: 단위 테스트 및 통합 테스트가 용이하도록 설계합니다.
10. **샌드박스 격리**: 샌드박스 관련 객체는 다른 도메인과 명확히 분리하여 관리합니다.

## 3. 주요 패키지 구조

AGI 시스템은 도메인 중심 패키지 구조(Domain-Centric Package Structure)를 채택하여 관련 기능을 도메인별로 그룹화합니다. 이 구조는 기존의 계층형 구조보다 높은 응집도와 유지보수성을 제공합니다.

### 3.1 도메인 중심 패키지 구조 개요

```
com.agi
├── user                  // 사용자 관리 도메인
│   ├── controller        // 사용자 관련 API 컨트롤러
│   ├── dto               // 사용자 관련 DTO
│   ├── entity            // 사용자 관련 엔티티
│   ├── repository        // 사용자 관련 저장소
│   ├── service           // 사용자 관련 서비스
│   └── exception         // 사용자 관련 예외
├── conversation          // 대화 관리 도메인
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── repository
│   ├── service
│   └── exception
├── tool                  // 도구 관리 도메인
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── repository
│   ├── service
│   └── exception
├── plan                  // 계획 관리 도메인
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── repository
│   ├── service
│   └── exception
├── knowledge             // 지식 관리 도메인
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── repository
│   ├── service
│   └── exception
├── multimodal            // 멀티모달 데이터 도메인
│   ├── image             // 이미지 하위 도메인
│   ├── audio             // 오디오 하위 도메인
│   ├── video             // 비디오 하위 도메인
│   └── common            // 멀티모달 공통 컴포넌트
├── learning              // 학습 및 피드백 도메인
│   ├── feedback          // 피드백 하위 도메인
│   ├── model             // 모델 관리 하위 도메인
│   ├── training          // 학습 작업 하위 도메인
│   └── evaluation        // 평가 하위 도메인
├── system                // 시스템 관리 도메인
│   ├── setting           // 설정 하위 도메인
│   ├── monitoring        // 모니터링 하위 도메인
│   ├── logging           // 로깅 하위 도메인
│   └── task              // 작업 큐 하위 도메인
├── sandbox               // 샌드박스 도메인
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── repository
│   ├── service
│   ├── security          // 샌드박스 보안
│   ├── container         // 컨테이너 관리
│   ├── template          // 템플릿 관리
│   └── integration       // 고급 AI 기능 통합
│       ├── learning      // 자가 학습 통합
│       ├── explainability // 설명 가능성 통합
│       ├── emotional     // 감성 지능 통합
│       ├── adaptive      // 적응형 학습 통합
│       ├── reinforcement // 강화 학습 통합
│       └── transfer      // 영역 간 지식 전이 통합
├── ai                    // AI 핵심 기능 도메인
│   ├── nlp               // 자연어 처리
│   ├── vision            // 컴퓨터 비전
│   ├── reasoning         // 추론 엔진
│   └── generation        // 생성 모델
├── common                // 공통 컴포넌트
│   ├── config            // 전역 설정
│   ├── exception         // 공통 예외
│   ├── util              // 유틸리티
│   ├── security          // 보안 공통 컴포넌트
│   └── validation        // 검증 공통 컴포넌트
└── AgiApplication.java   // 메인 애플리케이션
```

### 3.2 도메인별 패키지 구조 상세

각 도메인 패키지는 다음과 같은 내부 구조를 가집니다:

#### 3.2.1 Controller 패키지

각 도메인의 Controller 패키지는 해당 도메인의 API 엔드포인트를 정의합니다.

```java
// 예시: com.agi.user.controller.UserController
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
    
    // 기타 API 엔드포인트
}
```

#### 3.2.2 DTO 패키지

각 도메인의 DTO 패키지는 요청/응답 DTO를 포함합니다.

```java
// 예시: com.agi.user.dto.UserDto
@Getter
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String nickname;
    private boolean isActive;
    private LocalDateTime createdAt;
    private Set<String> roles;
    
    // fromEntity 메서드 등
}

// 요청 DTO 예시
@Getter
public class UserCreateRequest {
    @NotBlank
    private String username;
    
    @NotBlank
    @Email
    private String email;
    
    @NotBlank
    private String password;
    
    private String nickname;
}
```

#### 3.2.3 Entity 패키지

각 도메인의 Entity 패키지는 JPA 엔티티를 포함합니다.

```java
// 예시: com.agi.user.entity.User
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    private String nickname;
    
    private boolean isActive;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // 생성자, 비즈니스 메서드 등
}
```

#### 3.2.4 Repository 패키지

각 도메인의 Repository 패키지는 JPA 저장소 인터페이스를 포함합니다.

```java
// 예시: com.agi.user.repository.UserRepository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.lastLoginAt < :date")
    List<User> findInactiveUsers(LocalDateTime date);
}
```

#### 3.2.5 Service 패키지

각 도메인의 Service 패키지는 비즈니스 로직을 처리하는 서비스 인터페이스와 구현체를 포함합니다.

```java
// 예시: com.agi.user.service.UserService (인터페이스)
public interface UserService {
    UserDto getUserById(Long id);
    UserDto getUserByUsername(String username);
    UserDto createUser(UserCreateRequest request);
    UserDto updateUser(Long id, UserUpdateRequest request);
    void deleteUser(Long id);
    void changePassword(Long id, PasswordChangeRequest request);
}

// 예시: com.agi.user.service.impl.UserServiceImpl (구현체)
@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return UserDto.fromEntity(user);
    }
    
    // 기타 메서드 구현
}
```

#### 3.2.6 Exception 패키지

각 도메인의 Exception 패키지는 도메인 특화 예외를 포함합니다.

```java
// 예시: com.agi.user.exception.UserAlreadyExistsException
public class UserAlreadyExistsException extends BusinessException {
    public UserAlreadyExistsException(String message) {
        super("USER_ALREADY_EXISTS", message);
    }
}
```

### 3.3 도메인 간 상호작용

도메인 간 상호작용은 서비스 인터페이스를 통해 이루어집니다. 이를 통해 도메인 간 결합도를 낮추고 유연성을 높입니다.

```java
// 예시: com.agi.conversation.service.impl.ConversationServiceImpl
@Service
public class ConversationServiceImpl implements ConversationService {
    private final ConversationRepository conversationRepository;
    private final UserService userService; // 다른 도메인의 서비스 사용
    
    @Autowired
    public ConversationServiceImpl(
        ConversationRepository conversationRepository,
        UserService userService
    ) {
        this.conversationRepository = conversationRepository;
        this.userService = userService;
    }
    
    @Override
    public ConversationDto createConversation(ConversationCreateRequest request) {
        // UserService를 통해 사용자 정보 조회
        UserDto userDto = userService.getUserById(request.getUserId());
        
        // 대화 생성 로직
        // ...
    }
}
```

### 3.4 공통 컴포넌트 (Common)

여러 도메인에서 공유하는 공통 컴포넌트는 `common` 패키지에 위치합니다.

#### 3.4.1 Config 패키지

전역 설정 클래스를 포함합니다.

```java
// 예시: com.agi.common.config.SecurityConfig
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // 보안 설정
}

// 예시: com.agi.common.config.JpaConfig
@Configuration
@EnableJpaAuditing
public class JpaConfig {
    // JPA 설정
}
```

#### 3.4.2 Exception 패키지

공통 예외 클래스와 전역 예외 처리기를 포함합니다.

```java
// 예시: com.agi.common.exception.BusinessException
public abstract class BusinessException extends RuntimeException {
    private final String code;
    
    protected BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }
    
    public String getCode() {
        return code;
    }
}

// 예시: com.agi.common.exception.GlobalExceptionHandler
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        ErrorResponse response = new ErrorResponse(ex.getCode(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    // 기타 예외 처리 메서드
}
```

#### 3.4.3 Util 패키지

유틸리티 클래스를 포함합니다.

```java
// 예시: com.agi.common.util.DateUtils
public class DateUtils {
    public static LocalDateTime now() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }
    
    // 기타 날짜 관련 유틸리티 메서드
}
```

### 3.5 도메인 중심 구조의 장점

1. **높은 응집도**: 관련 기능이 모두 한 패키지에 모여 있어 코드 탐색과 이해가 쉬워집니다.
2. **낮은 결합도**: 도메인 간 상호작용은 서비스 인터페이스를 통해 이루어져 결합도가 낮아집니다.
3. **유지보수성 향상**: 특정 도메인 변경 시 해당 패키지만 집중적으로 수정하면 됩니다.
4. **확장성**: 새로운 도메인 추가 시 기존 코드에 영향을 최소화하며 독립적인 패키지로 추가할 수 있습니다.
5. **병렬 개발**: 팀원들이 서로 다른 도메인을 동시에 개발할 수 있어 개발 효율성이 높아집니다.
6. **테스트 용이성**: 도메인별로 독립적인 테스트가 가능합니다.
7. **마이크로서비스 전환 용이**: 향후 마이크로서비스 아키텍처로 전환할 경우, 도메인별 패키지가 독립적인 서비스로 분리하기 쉽습니다.

### 3.6 도메인 간 의존성 관리

도메인 간 의존성은 다음과 같은 원칙을 따릅니다:

1. **단방향 의존성**: 순환 의존성을 방지하기 위해 의존성은 단방향으로 설계합니다.
2. **인터페이스 의존성**: 구현체가 아닌 인터페이스에 의존하도록 합니다.
3. **공통 도메인 모델**: 여러 도메인에서 공유하는 모델은 별도의 공통 패키지에 위치시킵니다.
4. **이벤트 기반 통신**: 강한 결합을 피하기 위해 필요한 경우 이벤트 기반 통신을 사용합니다.

```java
// 예시: 이벤트 기반 통신
// com.agi.user.event.UserCreatedEvent
public class UserCreatedEvent {
    private final Long userId;
    private final String username;
    
    // 생성자, getter 등
}

// com.agi.user.service.impl.UserServiceImpl
@Service
public class UserServiceImpl implements UserService {
    private final ApplicationEventPublisher eventPublisher;
    
    // 사용자 생성 후 이벤트 발행
    @Override
    public UserDto createUser(UserCreateRequest request) {
        // 사용자 생성 로직
        User user = // ...
        
        // 이벤트 발행
        eventPublisher.publishEvent(new UserCreatedEvent(user.getId(), user.getUsername()));
        
        return UserDto.fromEntity(user);
    }
}

// com.agi.notification.listener.UserEventListener
@Component
public class UserEventListener {
    private final NotificationService notificationService;
    
    @EventListener
    public void handleUserCreatedEvent(UserCreatedEvent event) {
        // 사용자 생성 이벤트 처리
        notificationService.sendWelcomeNotification(event.getUserId());
    }
}
```

## 4. 핵심 도메인 객체 모델

각 도메인은 Entity, Repository, Service, Controller, DTO로 구성됩니다. 여기서는 주요 Entity와 DTO 예시를 중심으로 설명합니다.

### 4.1 사용자 도메인 (User Domain)

-   **Entity**: `User`, `Role`, `Permission`
-   **Repository**: `UserRepository`, `RoleRepository`, `PermissionRepository`
-   **Service**: `UserService`, `AuthService`
-   **Controller**: `UserController`, `AuthController`
-   **DTO**: `UserDto`, `RoleDto`, `PermissionDto`, `LoginRequest`, `RegisterRequest`, `JwtTokenResponse`

```java
// Entity 예시: User.java
@Entity
@Table(name = "users") // DB 테이블명과 일치
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 50)
    private String nickname;

    private boolean isActive;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime lastLoginAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @Builder
    public User(String username, String password, String email, String nickname) {
        this.username = username;
        this.password = password; // 실제로는 암호화 필요
        this.email = email;
        this.nickname = nickname;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }
    // 비밀번호 변경, 활성 상태 변경 등 메서드 추가
}

// DTO 예시: UserDto.java
@Getter
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String nickname;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    private Set<String> roles;

    public static UserDto fromEntity(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .build();
    }
}
```
