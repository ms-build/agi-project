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

    private LocalDateTime updatedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> metadata; // 대화 관련 메타데이터 (예: 사용된 모델, 컨텍스트)

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<Message> messages = new ArrayList<>();

    @Builder
    public Conversation(User user, String title, Map<String, Object> metadata) {
        this.id = UUID.randomUUID().toString();
        this.user = user;
        this.title = title;
        this.metadata = metadata;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
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
    @Column(nullable = false)
    private MessageRole role; // USER, ASSISTANT, SYSTEM, TOOL

    @Lob
    @Column(nullable = false)
    private String content; // 메시지 내용

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> metadata; // 메시지 관련 메타데이터 (예: 감정 분석 결과, 사용된 도구 ID)

    @Builder
    public Message(Conversation conversation, MessageRole role, String content, Map<String, Object> metadata) {
        this.id = UUID.randomUUID().toString();
        this.conversation = conversation;
        this.role = role;
        this.content = content;
        this.metadata = metadata;
        this.createdAt = LocalDateTime.now();
        // 메시지 생성 시 대화 업데이트 시간 갱신
        this.conversation.updateTimestamp();
    }
}

public enum MessageRole {
    USER, ASSISTANT, SYSTEM, TOOL
}

// DTO 예시: MessageDto.java
@Getter
@Builder
public class MessageDto {
    private String id;
    private String conversationId;
    private MessageRole role;
    private String content;
    private LocalDateTime createdAt;
    private Map<String, Object> metadata;

    public static MessageDto fromEntity(Message message) {
        return MessageDto.builder()
                .id(message.getId())
                .conversationId(message.getConversation().getId())
                .role(message.getRole())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .metadata(message.getMetadata())
                .build();
    }
}
```

### 4.3 도구 도메인 (Tool Domain)

-   **Entity**: `Tool`, `ToolExecution`
-   **Repository**: `ToolRepository`, `ToolExecutionRepository`
-   **Service**: `ToolService`, `ToolExecutorService`
-   **Controller**: `ToolController`
-   **DTO**: `ToolDto`, `ToolExecutionDto`, `ToolExecutionRequest`, `ToolExecutionResponse`

```java
// Entity 예시: Tool.java
@Entity
@Table(name = "tool")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tool {
    @Id
    private String id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Lob
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> parametersSchema; // 입력 파라미터 스키마 (JSON Schema)

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> outputSchema; // 출력 결과 스키마 (JSON Schema)

    @Column(nullable = false, length = 100)
    private String category;

    private boolean isActive;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Tool(String name, String description, Map<String, Object> parametersSchema, Map<String, Object> outputSchema, String category) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.parametersSchema = parametersSchema;
        this.outputSchema = outputSchema;
        this.category = category;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }
}

// Entity 예시: ToolExecution.java
@Entity
@Table(name = "tool_execution")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ToolExecution {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tool_id", nullable = false)
    private Tool tool;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id") // Optional
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sandbox_id") // Optional, 샌드박스 내 실행 시
    private Sandbox sandbox;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> inputParameters;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExecutionStatus status;

    @Lob
    private String result; // 실행 결과 (JSON 문자열 또는 파일 경로)

    @Lob
    private String errorDetails; // 오류 발생 시 상세 내용

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    @Builder
    public ToolExecution(Tool tool, User user, Conversation conversation, Sandbox sandbox, Map<String, Object> inputParameters) {
        this.id = UUID.randomUUID().toString();
        this.tool = tool;
        this.user = user;
        this.conversation = conversation;
        this.sandbox = sandbox;
        this.inputParameters = inputParameters;
        this.status = ExecutionStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    // 상태 변경 및 결과 저장 메서드
    public void start() {
        this.status = ExecutionStatus.RUNNING;
        this.startedAt = LocalDateTime.now();
    }

    public void complete(String result) {
        this.status = ExecutionStatus.COMPLETED;
        this.result = result;
        this.completedAt = LocalDateTime.now();
    }

    public void fail(String errorDetails) {
        this.status = ExecutionStatus.FAILED;
        this.errorDetails = errorDetails;
        this.completedAt = LocalDateTime.now();
    }
}

public enum ExecutionStatus {
    PENDING, RUNNING, COMPLETED, FAILED, CANCELED
}

// DTO 예시: ToolExecutionDto.java
@Getter
@Builder
public class ToolExecutionDto {
    private String id;
    private String toolId;
    private String toolName;
    private String userId;
    private String conversationId; // Optional
    private String sandboxId; // Optional
    private Map<String, Object> inputParameters;
    private ExecutionStatus status;
    private String result; // JSON 파싱 필요
    private String errorDetails;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    public static ToolExecutionDto fromEntity(ToolExecution execution) {
        return ToolExecutionDto.builder()
                .id(execution.getId())
                .toolId(execution.getTool().getId())
                .toolName(execution.getTool().getName())
                .userId(execution.getUser().getId().toString())
                .conversationId(execution.getConversation() != null ? execution.getConversation().getId() : null)
                .sandboxId(execution.getSandbox() != null ? execution.getSandbox().getId() : null)
                .inputParameters(execution.getInputParameters())
                .status(execution.getStatus())
                .result(execution.getResult())
                .errorDetails(execution.getErrorDetails())
                .createdAt(execution.getCreatedAt())
                .startedAt(execution.getStartedAt())
                .completedAt(execution.getCompletedAt())
                .build();
    }
}
```

### 4.4 계획 도메인 (Plan Domain)

-   **Entity**: `Plan`, `PlanStep`
-   **Repository**: `PlanRepository`, `PlanStepRepository`
-   **Service**: `PlanService`, `PlanExecutionService`
-   **Controller**: `PlanController`
-   **DTO**: `PlanDto`, `PlanStepDto`, `PlanCreateRequest`, `PlanExecutionStatusDto`

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
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 200)
    private String name;

    @Lob
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExecutionStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("stepOrder ASC")
    private List<PlanStep> steps = new ArrayList<>();

    @Builder
    public Plan(User user, String name, String description) {
        this.id = UUID.randomUUID().toString();
        this.user = user;
        this.name = name;
        this.description = description;
        this.status = ExecutionStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    // 상태 변경, 단계 추가/삭제 등 메서드
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
    private Integer stepOrder;

    @Lob
    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExecutionStatus status;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> executionDetails; // 실행 정보 (예: 사용된 도구 ID, 결과 요약)

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    @Builder
    public PlanStep(Plan plan, Integer stepOrder, String description) {
        this.plan = plan;
        this.stepOrder = stepOrder;
        this.description = description;
        this.status = ExecutionStatus.PENDING;
    }
    // 상태 변경, 실행 정보 업데이트 등 메서드
}
```

### 4.5 지식 도메인 (Knowledge Domain)

-   **Entity**: `KnowledgeItem`, `KnowledgeTag`
-   **Repository**: `KnowledgeItemRepository`, `KnowledgeTagRepository`
-   **Service**: `KnowledgeService`
-   **Controller**: `KnowledgeController`
-   **DTO**: `KnowledgeItemDto`, `KnowledgeTagDto`, `KnowledgeSearchRequest`

```java
// Entity 예시: KnowledgeItem.java
@Entity
@Table(name = "knowledge_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KnowledgeItem {
    @Id
    private String id;

    @Column(nullable = false, length = 100)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false, length = 50)
    private String type; // FACT, CONCEPT, PROCEDURE, RULE 등

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> metadata; // 지식 관련 메타데이터 (예: 신뢰도, 출처)

    @OneToMany(mappedBy = "knowledgeItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<KnowledgeTag> tags = new HashSet<>();

    @Builder
    public KnowledgeItem(String title, String content, String type, User createdBy, Map<String, Object> metadata) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.content = content;
        this.type = type;
        this.createdBy = createdBy;
        this.metadata = metadata;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    // 태그 추가/삭제, 내용 업데이트 등 메서드
}
```

## 5. 핵심 서비스 및 모듈 인터페이스

### 5.1 사용자 관리 서비스 (UserService)

사용자 계정 관리, 인증, 권한 부여 등을 담당하는 서비스입니다.

```java
public interface UserService {
    UserDto createUser(UserCreateRequest request);
    UserDto getUserById(Long id);
    UserDto getUserByUsername(String username);
    List<UserDto> getAllUsers(Pageable pageable);
    UserDto updateUser(Long id, UserUpdateRequest request);
    void deleteUser(Long id);
    void changePassword(Long id, PasswordChangeRequest request);
    void assignRole(Long userId, Long roleId);
    void revokeRole(Long userId, Long roleId);
}

public interface AuthService {
    JwtTokenResponse login(LoginRequest request);
    void logout(String token);
    JwtTokenResponse refreshToken(String refreshToken);
    UserDto getCurrentUser();
    boolean hasPermission(String permission);
}
```

#### 5.1.1 주요 객체 관계

- `UserService`는 `UserRepository`를 통해 사용자 정보를 관리합니다.
- `AuthService`는 `JwtTokenProvider`를 사용하여 토큰 기반 인증을 처리합니다.
- `SecurityConfig`는 Spring Security 설정을 통해 인증 및 권한 부여 규칙을 정의합니다.

#### 5.1.2 데이터 흐름

1. 클라이언트가 로그인 요청을 보냅니다.
2. `AuthController`가 요청을 받아 `AuthService`로 전달합니다.
3. `AuthService`는 사용자 인증 후 JWT 토큰을 생성하여 반환합니다.
4. 이후 요청에서는 클라이언트가 JWT 토큰을 헤더에 포함하여 전송합니다.
5. `JwtAuthenticationFilter`가 토큰을 검증하고 사용자 인증 정보를 설정합니다.
6. `UserService`는 인증된 사용자 정보를 기반으로 요청을 처리합니다.

### 5.2 자연어 처리 서비스 (NlpService)

사용자와의 대화 처리, 의도 파악, 엔티티 추출 등을 담당하는 서비스입니다.

```java
public interface NlpService {
    MessageDto processMessage(String conversationId, MessageRequest request);
    List<IntentDto> extractIntents(String text, double confidenceThreshold);
    List<EntityDto> extractEntities(String text);
    SentimentAnalysisResult analyzeSentiment(String text);
    String generateResponse(String conversationId, Map<String, Object> context);
    void saveConversationContext(String conversationId, Map<String, Object> context);
}

public interface ConversationService {
    ConversationDto createConversation(ConversationCreateRequest request);
    ConversationDto getConversation(String id);
    List<ConversationDto> getUserConversations(Long userId, Pageable pageable);
    void deleteConversation(String id);
    List<MessageDto> getConversationMessages(String conversationId, Pageable pageable);
}
```

#### 5.2.1 주요 객체 관계

- `NlpService`는 `ConversationRepository`와 `MessageRepository`를 통해 대화 데이터를 관리합니다.
- `NlpService`는 `IntentClassifier`, `EntityExtractor`, `SentimentAnalyzer` 등의 컴포넌트를 활용합니다.
- `ConversationService`는 대화 세션 관리를 담당합니다.

#### 5.2.2 데이터 흐름

1. 클라이언트가 메시지를 전송합니다.
2. `NlpController`가 요청을 받아 `NlpService`로 전달합니다.
3. `NlpService`는 메시지를 분석하여 의도, 엔티티, 감정 등을 추출합니다.
4. 추출된 정보를 기반으로 적절한 응답을 생성합니다.
5. 필요한 경우 `ToolService`를 호출하여 도구 실행을 요청합니다.
6. 응답 메시지를 생성하여 클라이언트에 반환합니다.

### 5.3 도구 관리 서비스 (ToolService)

다양한 도구의 등록, 관리, 실행을 담당하는 서비스입니다.

```java
public interface ToolService {
    ToolDto registerTool(ToolRegistrationRequest request);
    ToolDto getTool(String id);
    List<ToolDto> getAllTools(Pageable pageable);
    List<ToolDto> getToolsByCategory(String category);
    ToolDto updateTool(String id, ToolUpdateRequest request);
    void deleteTool(String id);
    boolean validateToolParameters(String toolId, Map<String, Object> parameters);
}

public interface ToolExecutorService {
    ToolExecutionDto executeTool(ToolExecutionRequest request);
    ToolExecutionDto getExecution(String id);
    List<ToolExecutionDto> getUserExecutions(Long userId, Pageable pageable);
    void cancelExecution(String id);
    ToolExecutionStatus getExecutionStatus(String id);
}
```

#### 5.3.1 주요 객체 관계

- `ToolService`는 `ToolRepository`를 통해 도구 정보를 관리합니다.
- `ToolExecutorService`는 `ToolExecutionRepository`를 통해 도구 실행 정보를 관리합니다.
- `ToolExecutorService`는 `SandboxService`를 활용하여 안전한 도구 실행 환경을 제공합니다.

#### 5.3.2 데이터 흐름

1. `NlpService` 또는 클라이언트가 도구 실행을 요청합니다.
2. `ToolController`가 요청을 받아 `ToolExecutorService`로 전달합니다.
3. `ToolExecutorService`는 도구 파라미터를 검증합니다.
4. 필요한 경우 `SandboxService`를 통해 샌드박스 환경에서 도구를 실행합니다.
5. 실행 결과를 저장하고 클라이언트에 반환합니다.

### 5.4 계획 관리 서비스 (PlanService)

복잡한 작업의 계획 수립, 실행, 모니터링을 담당하는 서비스입니다.

```java
public interface PlanService {
    PlanDto createPlan(PlanCreateRequest request);
    PlanDto getPlan(String id);
    List<PlanDto> getUserPlans(Long userId, Pageable pageable);
    PlanDto updatePlan(String id, PlanUpdateRequest request);
    void deletePlan(String id);
    PlanStepDto addStep(String planId, PlanStepRequest request);
    void removeStep(String planId, Long stepId);
    void reorderSteps(String planId, List<Long> stepIds);
}

public interface PlanExecutionService {
    void startPlanExecution(String planId);
    void pausePlanExecution(String planId);
    void resumePlanExecution(String planId);
    void cancelPlanExecution(String planId);
    PlanExecutionStatusDto getPlanExecutionStatus(String planId);
    void executeNextStep(String planId);
    void completeStep(String planId, Long stepId, Map<String, Object> result);
    void failStep(String planId, Long stepId, String errorMessage);
}
```

#### 5.4.1 주요 객체 관계

- `PlanService`는 `PlanRepository`와 `PlanStepRepository`를 통해 계획 정보를 관리합니다.
- `PlanExecutionService`는 계획 실행을 조정하고 모니터링합니다.
- `PlanExecutionService`는 `ToolExecutorService`를 활용하여 계획 단계를 실행합니다.

#### 5.4.2 데이터 흐름

1. 클라이언트 또는 `NlpService`가 계획 생성을 요청합니다.
2. `PlanController`가 요청을 받아 `PlanService`로 전달합니다.
3. `PlanService`는 계획과 단계를 생성하고 저장합니다.
4. 클라이언트가 계획 실행을 요청합니다.
5. `PlanExecutionService`는 계획의 각 단계를 순차적으로 실행합니다.
6. 각 단계 실행 시 필요한 도구를 `ToolExecutorService`를 통해 호출합니다.
7. 실행 상태와 결과를 저장하고 클라이언트에 반환합니다.

### 5.5 지식 관리 서비스 (KnowledgeService)

시스템의 지식 베이스 관리, 검색, 업데이트를 담당하는 서비스입니다.

```java
public interface KnowledgeService {
    KnowledgeItemDto addKnowledgeItem(KnowledgeItemRequest request);
    KnowledgeItemDto getKnowledgeItem(String id);
    List<KnowledgeItemDto> searchKnowledge(KnowledgeSearchRequest request);
    KnowledgeItemDto updateKnowledgeItem(String id, KnowledgeItemRequest request);
    void deleteKnowledgeItem(String id);
    void addTag(String itemId, String tag);
    void removeTag(String itemId, String tag);
    List<String> getSimilarItems(String itemId, int limit);
}

public interface MemoryService {
    MemoryDto createMemory(MemoryCreateRequest request);
    MemoryDto getMemory(String id);
    List<MemoryDto> getUserMemories(Long userId, MemoryType type, Pageable pageable);
    List<MemoryDto> searchMemories(MemorySearchRequest request);
    void updateMemoryImportance(String id, double importance);
    void deleteMemory(String id);
    void createMemoryRelation(String sourceId, String targetId, String relationType);
    List<MemoryRelationDto> getMemoryRelations(String memoryId);
}
```

#### 5.5.1 주요 객체 관계

- `KnowledgeService`는 `KnowledgeItemRepository`와 `KnowledgeTagRepository`를 통해 지식 정보를 관리합니다.
- `MemoryService`는 `MemoryRepository`와 `MemoryRelationRepository`를 통해 기억 정보를 관리합니다.
- `VectorStoreService`는 벡터 임베딩 기반 유사성 검색을 지원합니다.

#### 5.5.2 데이터 흐름

1. `NlpService` 또는 클라이언트가 지식 검색을 요청합니다.
2. `KnowledgeController`가 요청을 받아 `KnowledgeService`로 전달합니다.
3. `KnowledgeService`는 키워드 또는 벡터 유사성 기반으로 관련 지식을 검색합니다.
4. 검색 결과를 클라이언트에 반환합니다.
5. 사용자 상호작용 정보는 `MemoryService`를 통해 저장됩니다.

### 5.6 멀티모달 서비스 (MultimodalService)

이미지, 오디오, 비디오 등 다양한 형식의 데이터 처리를 담당하는 서비스입니다.

```java
public interface ImageService {
    ImageMetadataDto uploadImage(MultipartFile file, ImageUploadRequest request);
    ImageMetadataDto getImageMetadata(String id);
    byte[] getImageContent(String id);
    List<ImageTagDto> analyzeImage(String id);
    List<ImageObjectDto> detectObjects(String id);
    ImageGenerationResultDto generateImage(ImageGenerationRequest request);
    void deleteImage(String id);
}

public interface AudioService {
    AudioMetadataDto uploadAudio(MultipartFile file, AudioUploadRequest request);
    AudioMetadataDto getAudioMetadata(String id);
    byte[] getAudioContent(String id);
    String transcribeAudio(String id);
    AudioGenerationResultDto generateAudio(AudioGenerationRequest request);
    void deleteAudio(String id);
}

public interface VideoService {
    VideoMetadataDto uploadVideo(MultipartFile file, VideoUploadRequest request);
    VideoMetadataDto getVideoMetadata(String id);
    byte[] getVideoContent(String id);
    String transcribeVideo(String id);
    List<VideoFrameDto> extractKeyFrames(String id, int maxFrames);
    VideoGenerationResultDto generateVideo(VideoGenerationRequest request);
    void deleteVideo(String id);
}
```

#### 5.6.1 주요 객체 관계

- `ImageService`는 `ImageMetadataRepository`와 `ImageTagRepository`를 통해 이미지 정보를 관리합니다.
- `AudioService`는 `AudioMetadataRepository`를 통해 오디오 정보를 관리합니다.
- `VideoService`는 `VideoMetadataRepository`를 통해 비디오 정보를 관리합니다.
- 각 서비스는 파일 저장을 위해 `FileStorageService`를 활용합니다.

#### 5.6.2 데이터 흐름

1. 클라이언트가 멀티모달 콘텐츠를 업로드합니다.
2. 해당 컨트롤러가 요청을 받아 적절한 서비스로 전달합니다.
3. 서비스는 파일을 저장하고 메타데이터를 추출합니다.
4. 필요한 경우 분석 작업(객체 감지, 음성 인식 등)을 수행합니다.
5. 처리 결과를 클라이언트에 반환합니다.

### 5.7 학습 및 피드백 서비스 (LearningService)

시스템의 학습 데이터 관리, 모델 학습, 피드백 처리를 담당하는 서비스입니다.

```java
public interface FeedbackService {
    FeedbackDto createFeedback(FeedbackRequest request);
    FeedbackDto getFeedback(String id);
    List<FeedbackDto> getEntityFeedback(String entityId, String entityType, Pageable pageable);
    FeedbackSummaryDto getFeedbackSummary(String entityId, String entityType);
    void deleteFeedback(String id);
}

public interface LearningDataService {
    LearningDataDto addLearningData(LearningDataRequest request);
    LearningDataDto getLearningData(String id);
    List<LearningDataDto> getLearningDataByType(String type, Pageable pageable);
    void deleteLearningData(String id);
    void exportLearningData(String type, String format, OutputStream outputStream);
}

public interface ModelTrainingService {
    TrainingJobDto createTrainingJob(TrainingJobRequest request);
    TrainingJobDto getTrainingJob(String id);
    List<TrainingJobDto> getModelTrainingJobs(String modelName, Pageable pageable);
    void cancelTrainingJob(String id);
    ModelVersionDto getModelVersion(String modelName, String version);
    List<ModelVersionDto> getModelVersions(String modelName, Pageable pageable);
    void activateModelVersion(String modelName, String version);
}
```

#### 5.7.1 주요 객체 관계

- `FeedbackService`는 `FeedbackRepository`를 통해 피드백 정보를 관리합니다.
- `LearningDataService`는 `LearningDataRepository`를 통해 학습 데이터를 관리합니다.
- `ModelTrainingService`는 `TrainingJobRepository`와 `ModelVersionRepository`를 통해 모델 학습 작업과 버전을 관리합니다.

#### 5.7.2 데이터 흐름

1. 클라이언트가 피드백을 제출합니다.
2. `FeedbackController`가 요청을 받아 `FeedbackService`로 전달합니다.
3. `FeedbackService`는 피드백을 저장하고 관련 엔티티에 연결합니다.
4. `LearningDataService`는 피드백과 상호작용 데이터를 학습 데이터로 변환합니다.
5. `ModelTrainingService`는 수집된 학습 데이터를 사용하여 모델 학습 작업을 실행합니다.
6. 학습된 모델은 버전 관리되며 필요시 활성화됩니다.

### 5.8 시스템 관리 서비스 (SystemService)

시스템 설정, 모니터링, 로깅, 작업 큐 관리를 담당하는 서비스입니다.

```java
public interface SettingService {
    SettingDto getSetting(String category, String key);
    Map<String, String> getCategorySettings(String category);
    void updateSetting(String category, String key, String value);
    void deleteSetting(String category, String key);
}

public interface MonitoringService {
    void recordMetric(String component, String metric, double value);
    List<MetricDto> getComponentMetrics(String component, LocalDateTime from, LocalDateTime to);
    SystemHealthDto getSystemHealth();
    List<HealthCheckDto> getComponentsHealth();
}

public interface TaskQueueService {
    String enqueueTask(TaskRequest request);
    TaskStatusDto getTaskStatus(String id);
    void cancelTask(String id);
    List<TaskStatusDto> getPendingTasks(String taskType, Pageable pageable);
    int getQueueSize(String taskType);
}
```

#### 5.8.1 주요 객체 관계

- `SettingService`는 `SettingRepository`를 통해 시스템 설정을 관리합니다.
- `MonitoringService`는 `MonitoringRepository`와 `HealthCheckRepository`를 통해 모니터링 데이터를 관리합니다.
- `TaskQueueService`는 `TaskQueueRepository`를 통해 비동기 작업을 관리합니다.

#### 5.8.2 데이터 흐름

1. 시스템 컴포넌트가 메트릭을 기록합니다.
2. `MonitoringService`는 메트릭을 저장하고 분석합니다.
3. 관리자가 시스템 상태를 조회합니다.
4. `SystemController`가 요청을 받아 `MonitoringService`로 전달합니다.
5. `MonitoringService`는 시스템 건강 상태를 수집하여 반환합니다.
6. 비동기 작업은 `TaskQueueService`를 통해 큐에 추가되고 관리됩니다.

### 5.9 샌드박스 모듈 (SandboxService)

안전한 코드 및 도구 실행 환경을 제공하는 서비스입니다.

```java
public interface SandboxService {
    SandboxDto createSandbox(SandboxCreateRequest request);
    SandboxDto getSandbox(String id);
    List<SandboxDto> getUserSandboxes(Long userId, Pageable pageable);
    void startSandbox(String id);
    void stopSandbox(String id);
    void deleteSandbox(String id);
    SandboxExecutionResultDto executeCommand(String id, CommandExecutionRequest request);
    SandboxFileDto uploadFile(String id, MultipartFile file, String targetPath);
    byte[] downloadFile(String id, String filePath);
    List<SandboxFileDto> listFiles(String id, String directoryPath);
    SandboxResourceUsageDto getResourceUsage(String id);
}

public interface SandboxSecurityService {
    void configureSecurity(String sandboxId, SecurityConfigRequest request);
    SecurityConfigDto getSecurityConfig(String sandboxId);
    void validateExecution(String sandboxId, CommandExecutionRequest request);
    void scanFileForThreats(String sandboxId, String filePath);
    List<SecurityViolationDto> getSecurityViolations(String sandboxId);
}

public interface SandboxTemplateService {
    SandboxTemplateDto createTemplate(SandboxTemplateRequest request);
    SandboxTemplateDto getTemplate(String id);
    List<SandboxTemplateDto> getAllTemplates(Pageable pageable);
    SandboxDto createSandboxFromTemplate(String templateId, SandboxCreateRequest request);
    void updateTemplate(String id, SandboxTemplateRequest request);
    void deleteTemplate(String id);
}
```

#### 5.9.1 주요 객체 관계

- `SandboxService`는 `SandboxRepository`와 `SandboxExecutionRepository`를 통해 샌드박스 정보를 관리합니다.
- `SandboxSecurityService`는 `SandboxSecurityRepository`를 통해 보안 설정을 관리합니다.
- `SandboxTemplateService`는 `SandboxTemplateRepository`를 통해 재사용 가능한 템플릿을 관리합니다.
- `ContainerManager`는 실제 컨테이너 생성 및 관리를 담당합니다.

#### 5.9.2 데이터 흐름

1. 클라이언트 또는 `ToolExecutorService`가 샌드박스 생성을 요청합니다.
2. `SandboxController`가 요청을 받아 `SandboxService`로 전달합니다.
3. `SandboxService`는 `ContainerManager`를 통해 격리된 컨테이너를 생성합니다.
4. 클라이언트가 샌드박스 내에서 명령 실행을 요청합니다.
5. `SandboxSecurityService`가 명령의 안전성을 검증합니다.
6. `SandboxService`는 검증된 명령을 샌드박스 내에서 실행합니다.
7. 실행 결과를 클라이언트에 반환합니다.

#### 5.9.3 샌드박스와 고급 AI 기능 통합

샌드박스 모듈은 다음 6개의 고급 AI 기능과 통합되어 안전하고 효과적인 실행 환경을 제공합니다:

##### 자가 학습 (Self-Learning) 통합

샌드박스 환경은 자가 학습 기능과 다음과 같이 통합됩니다:

```java
public interface SandboxLearningIntegration {
    LearningSessionDto createLearningSession(String sandboxId, LearningSessionRequest request);
    void recordExecutionResult(String sandboxId, String executionId, boolean success);
    void collectCodeSamples(String sandboxId, String filePath, CodeSampleType type);
    List<LearningInsightDto> getLearningInsights(String sandboxId);
    void applyLearnedPatterns(String sandboxId, String executionId);
}
```

**통합 시나리오:**
1. 샌드박스 내에서 코드 실행 시 성공/실패 패턴을 자동으로 수집합니다.
2. 수집된 패턴은 `LearningDataService`를 통해 학습 데이터로 변환됩니다.
3. 학습된 모델은 코드 실행 전 안전성 검증 및 최적화 제안에 활용됩니다.
4. 샌드박스는 실행 환경 최적화를 위해 자가 학습 결과를 적용합니다.

**데이터 흐름:**
- 샌드박스 실행 결과 → 학습 데이터 수집 → 패턴 학습 → 샌드박스 실행 최적화

##### 설명 가능성 (Explainability) 통합

샌드박스 환경은 설명 가능성 기능과 다음과 같이 통합됩니다:

```java
public interface SandboxExplainabilityIntegration {
    ExecutionExplanationDto explainExecution(String sandboxId, String executionId);
    SecurityDecisionExplanationDto explainSecurityDecision(String sandboxId, String executionId);
    ResourceUsageExplanationDto explainResourceUsage(String sandboxId);
    CodeAnalysisExplanationDto explainCodeAnalysis(String sandboxId, String filePath);
    void generateExecutionReport(String sandboxId, String executionId, OutputStream outputStream);
}
```

**통합 시나리오:**
1. 샌드박스 내 코드 실행 결과에 대한 상세 설명을 생성합니다.
2. 보안 제한으로 인한 실행 거부 시 그 이유를 명확히 설명합니다.
3. 리소스 사용량 급증 원인을 분석하여 설명합니다.
4. 코드 분석 결과를 시각화하여 사용자가 이해하기 쉽게 제공합니다.

**데이터 흐름:**
- 샌드박스 실행/보안 이벤트 → 설명 모델 처리 → 사용자 이해 가능한 설명 생성

##### 감성 지능 (Emotional Intelligence) 통합

샌드박스 환경은 감성 지능 기능과 다음과 같이 통합됩니다:

```java
public interface SandboxEmotionalIntegration {
    void recordUserFrustration(String sandboxId, String executionId);
    void adaptResponseStyle(String sandboxId, EmotionalState userState);
    EmotionalStateDto detectUserEmotionalState(String sandboxId, String messageId);
    List<EmotionalPatternDto> getUserEmotionalPatterns(String sandboxId);
    void configureFrustrationThreshold(String sandboxId, double threshold);
}
```

**통합 시나리오:**
1. 샌드박스 사용 중 사용자의 감정 상태를 모니터링합니다.
2. 반복된 오류로 인한 사용자 좌절감 감지 시 더 상세한 도움말을 제공합니다.
3. 사용자의 감정 상태에 따라 오류 메시지와 제안 스타일을 조정합니다.
4. 학습 과정에서 사용자의 감정적 반응을 고려하여 난이도를 조절합니다.

**데이터 흐름:**
- 사용자 메시지/행동 → 감정 분석 → 샌드박스 응답 스타일 조정

##### 적응형 학습 (Adaptive Learning) 통합

샌드박스 환경은 적응형 학습 기능과 다음과 같이 통합됩니다:

```java
public interface SandboxAdaptiveLearningIntegration {
    UserProfileDto getUserProfile(String sandboxId, Long userId);
    void updateUserProficiency(String sandboxId, String domain, ProficiencyLevel level);
    List<LearningRecommendationDto> getRecommendations(String sandboxId);
    void adaptEnvironment(String sandboxId, UserProfileDto profile);
    void trackLearningProgress(String sandboxId, String domain, double progress);
}
```

**통합 시나리오:**
1. 사용자의 코딩 스타일, 오류 패턴, 선호 도구 등을 분석하여 프로필을 구축합니다.
2. 사용자 숙련도에 따라 샌드박스 환경의 복잡성과 제공되는 도구를 조정합니다.
3. 사용자 학습 진행 상황에 따라 맞춤형 예제와 도전 과제를 제공합니다.
4. 사용자 피드백을 기반으로 환경 설정을 지속적으로 최적화합니다.

**데이터 흐름:**
- 사용자 행동 분석 → 프로필 업데이트 → 환경 적응 → 학습 진행 추적

##### 강화 학습 (Reinforcement Learning) 통합

샌드박스 환경은 강화 학습 기능과 다음과 같이 통합됩니다:

```java
public interface SandboxReinforcementLearningIntegration {
    void initializeAgent(String sandboxId, String agentType);
    void recordState(String sandboxId, Map<String, Object> state);
    ActionDto getNextAction(String sandboxId, Map<String, Object> state);
    void provideReward(String sandboxId, double reward, String reason);
    PolicyDto getCurrentPolicy(String sandboxId);
    void exportLearnedPolicy(String sandboxId, String format, OutputStream outputStream);
}
```

**통합 시나리오:**
1. 샌드박스 환경을 강화 학습 에이전트의 환경으로 활용합니다.
2. 코드 실행, 리소스 할당, 보안 설정 등을 에이전트의 행동으로 정의합니다.
3. 실행 성공, 효율성, 보안성 등을 보상으로 설정합니다.
4. 에이전트는 최적의 샌드박스 구성과 실행 전략을 학습합니다.
5. 학습된 정책은 새로운 샌드박스 인스턴스 생성 시 적용됩니다.

**데이터 흐름:**
- 환경 상태 관찰 → 에이전트 행동 결정 → 행동 실행 → 보상 수집 → 정책 업데이트

##### 영역 간 지식 전이 (Cross-Domain Knowledge Transfer) 통합

샌드박스 환경은 영역 간 지식 전이 기능과 다음과 같이 통합됩니다:

```java
public interface SandboxKnowledgeTransferIntegration {
    void registerDomain(String sandboxId, String domain, DomainKnowledgeDto knowledge);
    List<DomainMappingDto> getDomainMappings(String sourceDomain, String targetDomain);
    TransferTaskDto createTransferTask(String sandboxId, TransferTaskRequest request);
    TransferResultDto getTransferResult(String taskId);
    void applyTransferredKnowledge(String sandboxId, String taskId, String targetDomain);
    List<KnowledgeMetricDto> evaluateTransferEffectiveness(String sandboxId, String taskId);
}
```

**통합 시나리오:**
1. 한 프로그래밍 언어에서 학습된 패턴을 다른 언어로 전이합니다.
2. 특정 도메인(예: 이미지 처리)에서 학습된 최적화 기법을 다른 도메인(예: 텍스트 처리)에 적용합니다.
3. 보안 취약점 패턴을 다양한 언어와 프레임워크 간에 매핑합니다.
4. 샌드박스 환경 설정과 최적화 전략을 다른 유형의 샌드박스로 전이합니다.

**데이터 흐름:**
- 소스 도메인 지식 추출 → 도메인 간 매핑 생성 → 타겟 도메인에 지식 적용 → 전이 효과 평가

#### 5.9.4 샌드박스 보안 아키텍처

샌드박스 환경은 다음과 같은 다층 보안 아키텍처를 구현합니다:

1. **컨테이너 격리**: Docker 또는 유사 기술을 사용하여 물리적 격리 제공
2. **리소스 제한**: CPU, 메모리, 디스크, 네트워크 사용량 제한
3. **네트워크 필터링**: 허용된 IP 및 포트만 접근 가능
4. **시스템 콜 필터링**: seccomp 프로필을 통한 위험한 시스템 콜 차단
5. **파일 시스템 격리**: 읽기 전용 볼륨과 임시 볼륨 분리
6. **사용자 권한 제한**: 비특권 사용자로 실행
7. **실행 시간 제한**: 최대 실행 시간 설정
8. **코드 정적 분석**: 실행 전 코드 취약점 스캔

이러한 보안 계층은 고급 AI 기능과 통합되어 지속적으로 개선되고 최적화됩니다.

## 6. DTO 변환 전략

엔티티와 DTO 간의 변환은 다음과 같은 전략을 따릅니다:

### 6.1 엔티티 → DTO 변환

1. **정적 팩토리 메서드**: DTO 클래스 내에 `fromEntity` 정적 메서드를 구현합니다.
2. **매퍼 클래스**: 복잡한 변환 로직은 별도의 매퍼 클래스로 분리합니다.
3. **지연 로딩 처리**: 지연 로딩된 연관 엔티티는 필요한 경우에만 로드합니다.
4. **순환 참조 방지**: 양방향 관계에서 순환 참조가 발생하지 않도록 주의합니다.

### 6.2 DTO → 엔티티 변환

1. **생성자 또는 빌더**: 엔티티 생성 시 생성자 또는 빌더를 통해 DTO 값을 전달합니다.
2. **업데이트 메서드**: 기존 엔티티 업데이트 시 전용 메서드를 구현합니다.
3. **ID 기반 연관 관계**: 연관 엔티티는 ID를 통해 참조합니다.

### 6.3 변환 예시

```java
// 엔티티 → DTO 변환 예시
public static UserDto fromEntity(User user) {
    return UserDto.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .nickname(user.getNickname())
            .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
            .build();
}

// DTO → 엔티티 변환 예시 (생성)
public User toEntity(UserCreateRequest request) {
    return User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .nickname(request.getNickname())
            .password(passwordEncoder.encode(request.getPassword()))
            .build();
}

// DTO → 엔티티 변환 예시 (업데이트)
public void updateFromDto(User user, UserUpdateRequest request) {
    if (request.getNickname() != null) {
        user.updateNickname(request.getNickname());
    }
    if (request.getEmail() != null) {
        user.updateEmail(request.getEmail());
    }
}
```

## 7. 예외 처리 전략

시스템은 다음과 같은 예외 처리 전략을 사용합니다:

### 7.1 예외 계층 구조

```
- BaseException
  - BusinessException
    - EntityNotFoundException
    - InvalidParameterException
    - AccessDeniedException
    - DuplicateEntityException
  - SystemException
    - ExternalSystemException
    - InternalSystemException
```

### 7.2 예외 처리 방식

1. **@ControllerAdvice**: 전역 예외 처리기를 통해 일관된 오류 응답을 제공합니다.
2. **예외 코드**: 각 예외 유형에 고유한 코드를 할당하여 클라이언트가 쉽게 식별할 수 있도록 합니다.
3. **로깅**: 예외 발생 시 적절한 로그 레벨로 기록합니다.
4. **트랜잭션 관리**: 비즈니스 예외 발생 시 트랜잭션을 롤백합니다.

### 7.3 예외 응답 형식

```json
{
  "code": "USER_NOT_FOUND",
  "message": "사용자를 찾을 수 없습니다.",
  "timestamp": "2025-05-28T10:15:30.123Z",
  "path": "/api/users/123",
  "details": {
    "userId": 123
  }
}
```

## 8. 보안 설계

시스템은 다음과 같은 보안 설계를 적용합니다:

### 8.1 인증 및 권한 부여

1. **JWT 기반 인증**: 토큰 기반 인증을 통해 상태 비저장(Stateless) 인증을 구현합니다.
2. **역할 기반 접근 제어(RBAC)**: 사용자 역할에 따라 접근 권한을 관리합니다.
3. **메서드 수준 보안**: `@PreAuthorize` 어노테이션을 사용하여 메서드 수준에서 권한을 검사합니다.

### 8.2 데이터 보안

1. **암호화**: 민감한 데이터는 저장 및 전송 시 암호화합니다.
2. **비밀번호 해싱**: 사용자 비밀번호는 BCrypt 등의 강력한 해싱 알고리즘을 사용합니다.
3. **데이터 접근 제어**: 사용자는 자신의 데이터만 접근할 수 있도록 제한합니다.

### 8.3 API 보안

1. **HTTPS**: 모든 API 통신은 HTTPS를 통해 이루어집니다.
2. **CORS 설정**: 허용된 오리진만 API에 접근할 수 있도록 설정합니다.
3. **입력 검증**: 모든 사용자 입력은 서버 측에서 검증합니다.
4. **레이트 리밋**: API 호출 횟수를 제한하여 DoS 공격을 방지합니다.

### 8.4 샌드박스 보안

1. **격리**: 샌드박스 환경은 호스트 시스템과 완전히 격리됩니다.
2. **리소스 제한**: CPU, 메모리, 디스크 사용량을 제한합니다.
3. **네트워크 제한**: 외부 네트워크 접근을 제한합니다.
4. **실행 시간 제한**: 코드 실행 시간을 제한합니다.
5. **코드 검사**: 실행 전 코드를 정적 분석하여 위험 요소를 식별합니다.

## 9. 성능 최적화

시스템은 다음과 같은 성능 최적화 전략을 적용합니다:

### 9.1 데이터베이스 최적화

1. **인덱싱**: 자주 조회되는 필드에 인덱스를 설정합니다.
2. **페이징**: 대량의 데이터 조회 시 페이징을 적용합니다.
3. **지연 로딩**: 연관 엔티티는 필요할 때만 로드합니다.
4. **캐싱**: 자주 접근하는 데이터는 캐시에 저장합니다.

### 9.2 API 최적화

1. **응답 압축**: 응답 데이터를 GZIP으로 압축합니다.
2. **부분 응답**: 클라이언트가 필요한 필드만 반환합니다.
3. **비동기 처리**: 시간이 오래 걸리는 작업은 비동기로 처리합니다.

### 9.3 캐싱 전략

1. **애플리케이션 캐시**: 자주 사용되는 데이터는 메모리에 캐싱합니다.
2. **분산 캐시**: Redis를 사용하여 분산 환경에서 캐시를 공유합니다.
3. **캐시 무효화**: 데이터 변경 시 관련 캐시를 무효화합니다.

## 10. 확장성 설계

시스템은 다음과 같은 확장성 설계를 적용합니다:

### 10.1 수평적 확장

1. **무상태 설계**: 서비스는 상태를 저장하지 않아 여러 인스턴스로 확장 가능합니다.
2. **로드 밸런싱**: 여러 서비스 인스턴스 간에 부하를 분산합니다.
3. **샤딩**: 데이터베이스를 여러 샤드로 분할하여 확장성을 높입니다.

### 10.2 모듈식 설계

1. **마이크로서비스**: 기능별로 독립적인 서비스로 분리하여 개별적으로 확장 가능합니다.
2. **API 게이트웨이**: 클라이언트 요청을 적절한 서비스로 라우팅합니다.
3. **이벤트 기반 통신**: 서비스 간 느슨한 결합을 위해 이벤트 기반 통신을 사용합니다.

### 10.3 리소스 관리

1. **자동 스케일링**: 부하에 따라 자동으로 리소스를 확장합니다.
2. **컨테이너화**: Docker를 사용하여 일관된 환경을 제공합니다.
3. **오케스트레이션**: Kubernetes를 사용하여 컨테이너를 관리합니다.

## 11. 테스트 전략

시스템은 다음과 같은 테스트 전략을 적용합니다:

### 11.1 단위 테스트

1. **서비스 계층**: 각 서비스 메서드의 비즈니스 로직을 테스트합니다.
2. **레포지토리 계층**: 데이터 접근 로직을 테스트합니다.
3. **모킹**: 외부 의존성은 모킹하여 격리된 테스트를 수행합니다.

### 11.2 통합 테스트

1. **API 테스트**: 컨트롤러 엔드포인트를 테스트합니다.
2. **데이터베이스 통합**: 실제 데이터베이스와의 상호작용을 테스트합니다.
3. **서비스 간 통합**: 여러 서비스 간의 상호작용을 테스트합니다.

### 11.3 성능 테스트

1. **부하 테스트**: 시스템이 높은 부하에서도 정상 작동하는지 테스트합니다.
2. **스트레스 테스트**: 시스템의 한계를 테스트합니다.
3. **내구성 테스트**: 장시간 실행 시 안정성을 테스트합니다.

## 12. 배포 및 운영

시스템은 다음과 같은 배포 및 운영 전략을 적용합니다:

### 12.1 CI/CD 파이프라인

1. **지속적 통합**: 코드 변경 시 자동으로 빌드 및 테스트를 수행합니다.
2. **지속적 배포**: 테스트를 통과한 코드는 자동으로 배포합니다.
3. **환경 분리**: 개발, 테스트, 스테이징, 프로덕션 환경을 분리합니다.

### 12.2 모니터링 및 로깅

1. **애플리케이션 모니터링**: 애플리케이션 성능 및 오류를 모니터링합니다.
2. **인프라 모니터링**: 서버, 네트워크, 데이터베이스 등의 인프라를 모니터링합니다.
3. **중앙 집중식 로깅**: 모든 로그를 중앙 저장소에 수집하여 분석합니다.
4. **알림**: 문제 발생 시 즉시 알림을 보냅니다.

### 12.3 장애 복구

1. **백업 및 복구**: 정기적인 데이터 백업 및 복구 절차를 마련합니다.
2. **고가용성**: 중요 컴포넌트는 다중화하여 단일 장애점을 제거합니다.
3. **재해 복구**: 재해 발생 시 신속하게 복구할 수 있는 계획을 수립합니다.
