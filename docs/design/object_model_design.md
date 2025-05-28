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

### 3.7 계층형 구조와 도메인 중심 구조 비교

#### 3.7.1 계층형 패키지 구조

계층형 패키지 구조는 애플리케이션의 기술적 계층을 기준으로 코드를 구성합니다.

```
com.agi
├── config          // 설정 클래스
├── controller      // 모든 컨트롤러
├── dto             // 모든 DTO
├── entity          // 모든 엔티티
├── repository      // 모든 저장소
├── service         // 모든 서비스
└── util            // 유틸리티
```

**장점**:
- 단순성과 직관성
- 일관된 구조
- 기술 중심 분리
- 초기 개발 속도

**단점**:
- 낮은 응집도
- 확장성 제한
- 도메인 경계 불명확
- 병렬 개발 어려움
- 기능 변경 시 여러 패키지 수정 필요

#### 3.7.2 도메인 중심 패키지 구조

도메인 중심 패키지 구조는 비즈니스 도메인을 기준으로 코드를 구성합니다.

```
com.agi
├── user            // 사용자 관리 도메인
├── conversation    // 대화 관리 도메인
├── tool            // 도구 관리 도메인
├── sandbox         // 샌드박스 도메인
└── common          // 공통 컴포넌트
```

**장점**:
- 높은 응집도
- 명확한 도메인 경계
- 유지보수성 향상
- 확장성
- 병렬 개발 효율성
- 마이크로서비스 전환 용이

**단점**:
- 초기 설계 복잡성
- 도메인 간 중복 가능성
- 학습 곡선

#### 3.7.3 AGI 시스템에 도메인 중심 구조가 적합한 이유

1. **복잡성 관리**: AGI 시스템의 다양한 도메인을 효과적으로 캡슐화
2. **확장성 요구**: 지속적으로 새로운 기능과 도메인 추가 용이
3. **팀 협업 효율성**: 여러 개발자가 동시에 작업할 때 병렬 개발 가능
4. **도메인 특화 요구사항 대응**: 각 도메인의 고유한 요구사항과 제약조건 수용
5. **마이크로서비스 전환 가능성**: 향후 규모 확장에 따른 아키텍처 전환 용이
6. **고급 AI 기능 통합**: 자가 학습, 설명 가능성 등의 고급 기능을 각 도메인에 맞게 통합

## 4. 핵심 도메인 객체 모델

각 도메인은 Entity, Repository, Service, Controller, DTO로 구성됩니다. 여기서는 주요 Entity와 DTO 예시를 중심으로 설명합니다.

### 4.1 사용자 도메인 (User Domain)

-   **Entity**: `User`, `Role`, `Permission`, `UserRole`, `RolePermission`
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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRole> userRoles = new HashSet<>();

    @Builder
    public User(String username, String password, String email, String nickname) {
        this.username = username;
        this.password = password; // 실제로는 암호화 필요
        this.email = email;
        this.nickname = nickname;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }
    
    // 역할 추가 메서드
    public void addRole(Role role) {
        UserRole userRole = new UserRole(this, role);
        this.userRoles.add(userRole);
    }
    
    // 역할 제거 메서드
    public void removeRole(Role role) {
        this.userRoles.removeIf(userRole -> userRole.getRole().equals(role));
    }
    
    // 비밀번호 변경, 활성 상태 변경 등 메서드 추가
}

// 중간 테이블 엔티티 예시: UserRole.java
@Entity
@Table(name = "user_roles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRole {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;
    
    @Column(nullable = false)
    private LocalDateTime assignedAt;
    
    public UserRole(User user, Role role) {
        this.user = user;
        this.role = role;
        this.assignedAt = LocalDateTime.now();
    }
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
                .roles(user.getUserRoles().stream()
                      .map(ur -> ur.getRole().getName())
                      .collect(Collectors.toSet()))
                .build();
    }
}
```

### 4.2 대화 도메인 (Conversation Domain)

-   **Entity**: `Conversation`, `Message`
-   **Repository**: `ConversationRepository`, `MessageRepository`
-   **Service**: `ConversationService`
-   **Controller**: `ConversationController` (또는 NlpController에 통합)
-   **DTO**: `ConversationDto`, `MessageDto`, `ConversationRequest`, `ConversationResponse`

```java
// Entity 예시: Conversation.java
@Entity
@Table(name = "conversation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Conversation {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 200)
    private String title;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime lastMessageAt;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<Message> messages = new ArrayList<>();

    @Builder
    public Conversation(String id, User user, String title) {
        this.id = id;
        this.user = user;
        this.title = title;
        this.createdAt = LocalDateTime.now();
        this.lastMessageAt = this.createdAt;
    }

    public void addMessage(Message message) {
        this.messages.add(message);
        this.lastMessageAt = message.getCreatedAt();
    }
}

// Entity 예시: Message.java
@Entity
@Table(name = "message")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MessageType type;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Message(String id, Conversation conversation, MessageType type, String content) {
        this.id = id;
        this.conversation = conversation;
        this.type = type;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }
}
```

### 4.3 도구 도메인 (Tool Domain)

-   **Entity**: `Tool`, `ToolParameter`, `ToolExecution`
-   **Repository**: `ToolRepository`, `ToolParameterRepository`, `ToolExecutionRepository`
-   **Service**: `ToolService`, `ToolExecutionService`
-   **Controller**: `ToolController`
-   **DTO**: `ToolDto`, `ToolParameterDto`, `ToolExecuteRequest`, `ToolExecutionResultDto`

```java
// Entity 예시: Tool.java
@Entity
@Table(name = "tool")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tool {
    @Id
    private String id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ToolType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ToolStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime lastUpdatedAt;

    @OneToMany(mappedBy = "tool", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ToolParameter> parameters = new ArrayList<>();

    @Builder
    public Tool(String id, String name, String description, ToolType type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.status = ToolStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.lastUpdatedAt = this.createdAt;
    }

    public void addParameter(ToolParameter parameter) {
        this.parameters.add(parameter);
        parameter.setTool(this);
    }

    public void updateStatus(ToolStatus status) {
        this.status = status;
        this.lastUpdatedAt = LocalDateTime.now();
    }
}

// Entity 예시: ToolParameter.java
@Entity
@Table(name = "tool_parameter")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ToolParameter {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tool_id", nullable = false)
    private Tool tool;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 30)
    private String type;

    private boolean required;

    @Column(columnDefinition = "TEXT")
    private String defaultValue;

    @Builder
    public ToolParameter(String name, String description, String type, boolean required, String defaultValue) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.required = required;
        this.defaultValue = defaultValue;
    }

    void setTool(Tool tool) {
        this.tool = tool;
    }
}
```

### 4.4 계획 도메인 (Plan Domain)

-   **Entity**: `Plan`, `PlanStep`
-   **Repository**: `PlanRepository`, `PlanStepRepository`
-   **Service**: `PlanService`
-   **Controller**: `PlanController`
-   **DTO**: `PlanDto`, `PlanStepDto`, `PlanCreateRequest`, `PlanUpdateRequest`

```java
// Entity 예시: Plan.java
@Entity
@Table(name = "plan")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Plan {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PlanStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("stepNumber ASC")
    private List<PlanStep> steps = new ArrayList<>();

    @Builder
    public Plan(String id, Conversation conversation, String title, String description) {
        this.id = id;
        this.conversation = conversation;
        this.title = title;
        this.description = description;
        this.status = PlanStatus.CREATED;
        this.createdAt = LocalDateTime.now();
    }

    public void addStep(PlanStep step) {
        this.steps.add(step);
        step.setPlan(this);
    }

    public void updateStatus(PlanStatus status) {
        this.status = status;
        if (status == PlanStatus.COMPLETED) {
            this.completedAt = LocalDateTime.now();
        }
    }
}

// Entity 예시: PlanStep.java
@Entity
@Table(name = "plan_step")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlanStep {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Column(nullable = false)
    private int stepNumber;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PlanStepStatus status;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    @Builder
    public PlanStep(int stepNumber, String title, String description) {
        this.stepNumber = stepNumber;
        this.title = title;
        this.description = description;
        this.status = PlanStepStatus.PENDING;
    }

    void setPlan(Plan plan) {
        this.plan = plan;
    }

    public void updateStatus(PlanStepStatus status) {
        this.status = status;
        if (status == PlanStepStatus.IN_PROGRESS && this.startedAt == null) {
            this.startedAt = LocalDateTime.now();
        } else if (status == PlanStepStatus.COMPLETED && this.completedAt == null) {
            this.completedAt = LocalDateTime.now();
        }
    }
}
```

### 4.5 지식 도메인 (Knowledge Domain)

-   **Entity**: `Knowledge`, `KnowledgeTag`
-   **Repository**: `KnowledgeRepository`, `KnowledgeTagRepository`
-   **Service**: `KnowledgeService`
-   **Controller**: `KnowledgeController`
-   **DTO**: `KnowledgeDto`, `KnowledgeCreateRequest`, `KnowledgeSearchRequest`

```java
// Entity 예시: Knowledge.java
@Entity
@Table(name = "knowledge")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Knowledge {
    @Id
    private String id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private KnowledgeType type;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime lastUpdatedAt;

    @ManyToMany
    @JoinTable(
        name = "knowledge_tags",
        joinColumns = @JoinColumn(name = "knowledge_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<KnowledgeTag> tags = new HashSet<>();

    @Builder
    public Knowledge(String id, String title, String content, KnowledgeType type) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.type = type;
        this.createdAt = LocalDateTime.now();
        this.lastUpdatedAt = this.createdAt;
    }

    public void addTag(KnowledgeTag tag) {
        this.tags.add(tag);
        tag.getKnowledgeList().add(this);
    }

    public void removeTag(KnowledgeTag tag) {
        this.tags.remove(tag);
        tag.getKnowledgeList().remove(this);
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
        this.lastUpdatedAt = LocalDateTime.now();
    }
}

// Entity 예시: KnowledgeTag.java
@Entity
@Table(name = "knowledge_tag")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KnowledgeTag {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @ManyToMany(mappedBy = "tags")
    private Set<Knowledge> knowledgeList = new HashSet<>();

    public KnowledgeTag(String name) {
        this.name = name;
    }
}
```

### 4.6 멀티모달 도메인 (Multimodal Domain)

-   **Entity**: `ImageMetadata`, `AudioMetadata`, `VideoMetadata`, `MediaTag`
-   **Repository**: `ImageMetadataRepository`, `AudioMetadataRepository`, `VideoMetadataRepository`
-   **Service**: `ImageService`, `AudioService`, `VideoService`
-   **Controller**: `ImageController`, `AudioController`, `VideoController`
-   **DTO**: `ImageMetadataDto`, `AudioMetadataDto`, `VideoMetadataDto`, `ImageUploadRequest`

```java
// Entity 예시: ImageMetadata.java
@Entity
@Table(name = "image_metadata")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageMetadata {
    @Id
    private String id;

    @Column(nullable = false, length = 200)
    private String filename;

    @Column(nullable = false, length = 100)
    private String contentType;

    @Column(nullable = false)
    private long size;

    @Column(nullable = false, length = 500)
    private String path;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ImageFormat format;

    private Integer width;

    private Integer height;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    @ManyToMany
    @JoinTable(
        name = "image_tags",
        joinColumns = @JoinColumn(name = "image_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<MediaTag> tags = new HashSet<>();

    @Builder
    public ImageMetadata(String id, String filename, String contentType, long size, String path, 
                        ImageFormat format, Integer width, Integer height, String description) {
        this.id = id;
        this.filename = filename;
        this.contentType = contentType;
        this.size = size;
        this.path = path;
        this.format = format;
        this.width = width;
        this.height = height;
        this.description = description;
        this.uploadedAt = LocalDateTime.now();
    }

    public void addTag(MediaTag tag) {
        this.tags.add(tag);
    }

    public void removeTag(MediaTag tag) {
        this.tags.remove(tag);
    }
}

// Entity 예시: MediaTag.java
@Entity
@Table(name = "media_tag")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MediaTag {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    public MediaTag(String name) {
        this.name = name;
    }
}
```

### 4.7 샌드박스 도메인 (Sandbox Domain)

-   **Entity**: `Sandbox`, `SandboxTemplate`, `SandboxExecution`
-   **Repository**: `SandboxRepository`, `SandboxTemplateRepository`, `SandboxExecutionRepository`
-   **Service**: `SandboxService`, `SandboxExecutionService`
-   **Controller**: `SandboxController`
-   **DTO**: `SandboxDto`, `SandboxCreateRequest`, `SandboxExecutionResultDto`

```java
// Entity 예시: Sandbox.java
@Entity
@Table(name = "sandbox")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sandbox {
    @Id
    private String id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SandboxStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime lastActiveAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private SandboxTemplate template;

    @Builder
    public Sandbox(String id, String name, String description, User user, SandboxTemplate template) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = SandboxStatus.CREATED;
        this.createdAt = LocalDateTime.now();
        this.lastActiveAt = this.createdAt;
        this.user = user;
        this.template = template;
    }

    public void updateStatus(SandboxStatus status) {
        this.status = status;
        this.lastActiveAt = LocalDateTime.now();
    }
}
```

## 5. 결론

이 문서에서는 통합 AGI 시스템의 객체 모델 설계를 설명했습니다. 도메인 중심 패키지 구조를 채택하여 높은 응집도와 낮은 결합도를 달성하고, 각 도메인의 핵심 엔티티와 DTO를 정의했습니다. 이 설계는 확장성, 유지보수성, 테스트 용이성을 고려하여 작성되었으며, 향후 시스템 확장에 대비한 유연한 구조를 제공합니다.

주요 도메인인 사용자, 대화, 도구, 계획, 지식, 멀티모달, 샌드박스 등에 대한 객체 모델을 정의하고, 각 도메인 간의 상호작용 방식을 설명했습니다. 또한, 도메인 중심 구조와 계층형 구조를 비교하여 AGI 시스템에 도메인 중심 구조가 적합한 이유를 제시했습니다.

이 설계를 기반으로 개발된 AGI 시스템은 사용자의 요구사항을 효과적으로 처리하고, 안전한 샌드박스 환경에서 코드 및 도구를 실행할 수 있는 기반을 제공할 것입니다.
