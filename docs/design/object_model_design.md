# 통합 AGI 시스템 객체 모델 설계

## 1. 개요

이 문서는 Spring Boot 3.4.5, Java 17 기반의 통합 AGI 시스템을 위한 객체 모델 설계를 설명합니다. 객체 모델은 도메인 주도 설계(Domain-Driven Design) 원칙을 일부 적용하여 시스템의 핵심 개념과 관계를 클래스 및 인터페이스로 표현합니다. 이 설계는 시스템 아키텍처, 데이터베이스 스키마, API 설계와 일관성을 유지합니다.

## 2. 설계 원칙

1.  **응집도 높고 결합도 낮은 설계**: 각 클래스와 모듈은 특정 책임에 집중하고 다른 컴포넌트와의 의존성을 최소화합니다.
2.  **명확한 책임 분리**: Controller, Service, Repository, Domain Entity, DTO 등의 계층별 책임을 명확히 구분합니다.
3.  **인터페이스 기반 설계**: 주요 컴포넌트 간의 상호작용은 인터페이스를 통해 정의하여 유연성과 테스트 용이성을 높입니다.
4.  **불변성 활용**: 가능한 경우 불변 객체(Immutable Objects)를 사용하여 상태 관리의 복잡성을 줄이고 스레드 안전성을 높입니다.
5.  **DTO 사용**: API 계층과 서비스 계층 간 데이터 전달 시 DTO(Data Transfer Object)를 사용하여 도메인 모델 노출을 최소화하고 API 계약을 명확히 합니다.
6.  **JPA 엔티티 설계 가이드라인 준수**: 이전에 정의된 JPA 엔티티 설계 가이드라인(생성자 빌더, 보호된 기본 생성자 등)을 따릅니다.

## 3. 주요 패키지 구조

```
com.agi
├── AgiApplication.java
├── config          // 설정 클래스 (QuerydslConfig, SecurityConfig 등)
├── controller      // API 요청 처리 (RestController)
│   ├── dto         // Controller 계층 DTO (Request/Response)
│   └── impl        // Controller 구현체
├── domain          // 핵심 도메인 모델
│   ├── entity      // JPA 엔티티
│   ├── repository  // 데이터 접근 인터페이스 (Spring Data JPA)
│   └── vo          // 값 객체 (Value Objects)
├── service         // 비즈니스 로직 처리
│   ├── dto         // Service 계층 DTO
│   ├── impl        // Service 구현체
│   └── module      // AGI 핵심 모듈 인터페이스 및 구현
├── exception       // 사용자 정의 예외 클래스
├── util            // 유틸리티 클래스
└── common          // 공통 코드 (상수, 기본 클래스 등)
```

## 4. 핵심 도메인 객체 모델

### 4.1 사용자 도메인 (User Domain)

-   **Entity**: `User`, `Role`, `Permission`, `Session`
-   **Repository**: `UserRepository`, `RoleRepository`, `PermissionRepository`, `SessionRepository`
-   **Service**: `UserService`, `AuthService`
-   **Controller**: `UserController`, `AuthController`
-   **DTO**: `UserDto`, `RoleDto`, `PermissionDto`, `LoginRequest`, `RegisterRequest`, `JwtTokenResponse`

```java
// Entity 예시: User.java
@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    // ... 기타 필드

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_role",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @Builder
    public User(String username, String email, String passwordHash, String firstName, String lastName) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        // ... 필드 초기화
    }
}

// DTO 예시: UserDto.java
@Getter
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDateTime createdAt;
    private Set<String> roles;

    public static UserDto fromEntity(User user) {
        return UserDto.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            // ... 매핑
            .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
            .build();
    }
}

// Service 예시: UserService.java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDto registerUser(RegisterRequest request) {
        // 중복 검사
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists: " + request.getUsername());
        }
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        // 기본 역할 할당
        Role userRole = roleRepository.findByName("ROLE_USER")
            .orElseThrow(() -> new RoleNotFoundException("Default role ROLE_USER not found"));

        User newUser = User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .passwordHash(encodedPassword)
            .build();
        newUser.getRoles().add(userRole);

        User savedUser = userRepository.save(newUser);
        return UserDto.fromEntity(savedUser);
    }
    // ... 기타 메서드
}
```

### 4.2 대화 도메인 (Conversation Domain)

-   **Entity**: `Conversation`, `Message`, `Intent`, `Entity`, `Sentiment`
-   **Repository**: `ConversationRepository`, `MessageRepository`, `IntentRepository`, `EntityRepository`, `SentimentRepository`
-   **Service**: `ConversationService`, `MessageService`
-   **Controller**: `ConversationController` (NLP Controller 내 통합 가능)
-   **DTO**: `ConversationDto`, `MessageDto`, `IntentDto`, `EntityDto`, `SentimentDto`, `ConversationRequest`, `ConversationResponse`

```java
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
    private MessageRole role;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Lob
    private byte[] embedding;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> metadata;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Intent> intents = new ArrayList<>();

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EntityExtraction> entities = new ArrayList<>(); // Entity는 예약어일 수 있으므로 이름 변경

    @OneToOne(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private Sentiment sentiment;

    @Builder
    public Message(Conversation conversation, MessageRole role, String content, Map<String, Object> metadata) {
        this.id = UUID.randomUUID().toString();
        this.conversation = conversation;
        this.role = role;
        this.content = content;
        this.metadata = metadata;
        this.createdAt = LocalDateTime.now();
    }
    // 연관관계 편의 메서드 추가 가능
}

// DTO 예시: MessageDto.java
@Getter
@Builder
public class MessageDto {
    private String id;
    private String conversationId;
    private String role;
    private String content;
    private LocalDateTime createdAt;
    private Map<String, Object> metadata;
    private List<IntentDto> intents;
    private List<EntityDto> entities;
    private SentimentDto sentiment;

    public static MessageDto fromEntity(Message message) {
        return MessageDto.builder()
            .id(message.getId())
            .conversationId(message.getConversation().getId())
            .role(message.getRole().name())
            .content(message.getContent())
            .createdAt(message.getCreatedAt())
            .metadata(message.getMetadata())
            // ... 연관 DTO 매핑
            .build();
    }
}
```

### 4.3 도구 도메인 (Tool Domain)

-   **Entity**: `Tool`, `ToolParameter`, `ToolExecution`
-   **Repository**: `ToolRepository`, `ToolParameterRepository`, `ToolExecutionRepository`
-   **Service**: `ToolService`, `ToolExecutorService`
-   **Controller**: `ToolController`
-   **DTO**: `ToolDto`, `ToolParameterDto`, `ToolExecutionDto`, `ToolExecutionRequest`, `ToolExecutionResponse`
-   **Interface**: `Tool` (실제 도구 구현체 인터페이스)

```java
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
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private Session session;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> parameters;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> result;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExecutionStatus status;

    @Lob
    private String errorMessage;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    @Builder
    public ToolExecution(Tool tool, User user, Session session, Map<String, Object> parameters) {
        this.id = UUID.randomUUID().toString();
        this.tool = tool;
        this.user = user;
        this.session = session;
        this.parameters = parameters;
        this.status = ExecutionStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
    // 상태 변경 메서드 추가
}

// Interface 예시: Tool.java (실제 도구 구현체)
public interface ExecutableTool {
    String getName();
    String getDescription();
    List<ToolParameterDefinition> getParameterDefinitions();
    ToolResult execute(Map<String, Object> parameters, Context context);
}
```

### 4.4 계획 도메인 (Plan Domain)

-   **Entity**: `Plan`, `PlanStep`, `PlanExecution`, `StepExecution`
-   **Repository**: `PlanRepository`, `PlanStepRepository`, `PlanExecutionRepository`, `StepExecutionRepository`
-   **Service**: `PlanService`, `PlanExecutionService`
-   **Controller**: `PlanController`
-   **DTO**: `PlanDto`, `PlanStepDto`, `PlanExecutionDto`, `StepExecutionDto`, `PlanRequest`, `PlanResponse`

### 4.5 지식 및 기억 도메인 (Knowledge & Memory Domain)

-   **Entity**: `Knowledge`, `Memory`, `Context`, `KnowledgeRelation`
-   **Repository**: `KnowledgeRepository`, `MemoryRepository`, `ContextRepository`, `KnowledgeRelationRepository`
-   **Service**: `KnowledgeService`, `MemoryService`, `ContextService`, `ReasoningService`
-   **Controller**: `KnowledgeController`, `MemoryController`
-   **DTO**: `KnowledgeDto`, `MemoryDto`, `ContextDto`, `KnowledgeSearchRequest`, `KnowledgeSearchResponse`

### 4.6 멀티모달 도메인 (Multimodal Domain)

-   **Entity**: `ImageMetadata`, `AudioMetadata`, `VideoMetadata`, `MediaObject`
-   **Repository**: `ImageMetadataRepository`, `AudioMetadataRepository`, `VideoMetadataRepository`, `MediaObjectRepository`
-   **Service**: `MediaService`, `ImageProcessingService`, `AudioProcessingService`, `VideoProcessingService`
-   **Controller**: `MediaController`
-   **DTO**: `ImageMetadataDto`, `AudioMetadataDto`, `VideoMetadataDto`, `MediaProcessingRequest`, `MediaProcessingResponse`

### 4.7 학습 및 피드백 도메인 (Learning & Feedback Domain)

-   **Entity**: `Feedback`, `LearningData`, `ModelVersion`, `TrainingJob`
-   **Repository**: `FeedbackRepository`, `LearningDataRepository`, `ModelVersionRepository`, `TrainingJobRepository`
-   **Service**: `FeedbackService`, `LearningService`, `ModelManagementService`
-   **Controller**: `LearningController`
-   **DTO**: `FeedbackDto`, `ModelVersionDto`, `FeedbackRequest`

### 4.8 설명 가능성 도메인 (Explainability Domain)

-   **Entity**: `Explanation`
-   **Repository**: `ExplanationRepository`
-   **Service**: `ExplanationService`
-   **Controller**: `ExplanationController` (또는 다른 컨트롤러에 통합)
-   **DTO**: `ExplanationDto`, `ExplanationRequest`, `ExplanationResponse`

```java
// Entity 예시: Explanation.java
@Entity
@Table(name = "explanation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Explanation {
    @Id
    private String id;

    @Column(nullable = false)
    private String targetEntityId; // 설명 대상 ID (예: Message ID, Decision ID)

    @Column(nullable = false)
    private String targetEntityType; // 설명 대상 타입 (예: "Message", "PlanStep")

    @Lob
    @Column(nullable = false)
    private String explanationText; // 텍스트 설명

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> explanationData; // 시각화 데이터 등 추가 설명 정보

    @Column(length = 100)
    private String algorithmUsed; // 사용된 설명 알고리즘

    private Double confidenceScore;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Explanation(String targetEntityId, String targetEntityType, String explanationText, Map<String, Object> explanationData, String algorithmUsed, Double confidenceScore) {
        this.id = UUID.randomUUID().toString();
        this.targetEntityId = targetEntityId;
        this.targetEntityType = targetEntityType;
        this.explanationText = explanationText;
        this.explanationData = explanationData;
        this.algorithmUsed = algorithmUsed;
        this.confidenceScore = confidenceScore;
        this.createdAt = LocalDateTime.now();
    }
}

// DTO 예시: ExplanationDto.java
@Getter
@Builder
public class ExplanationDto {
    private String id;
    private String targetEntityId;
    private String targetEntityType;
    private String explanationText;
    private Map<String, Object> explanationData;
    private String algorithmUsed;
    private Double confidenceScore;
    private LocalDateTime createdAt;

    public static ExplanationDto fromEntity(Explanation explanation) {
        return ExplanationDto.builder()
            .id(explanation.getId())
            .targetEntityId(explanation.getTargetEntityId())
            .targetEntityType(explanation.getTargetEntityType())
            .explanationText(explanation.getExplanationText())
            .explanationData(explanation.getExplanationData())
            .algorithmUsed(explanation.getAlgorithmUsed())
            .confidenceScore(explanation.getConfidenceScore())
            .createdAt(explanation.getCreatedAt())
            .build();
    }
}
```

### 4.9 감성 지능 도메인 (Emotional Intelligence Domain)

-   **Entity**: `EmotionAnalysis`, `EmotionalResponseStrategy`
-   **Repository**: `EmotionAnalysisRepository`, `EmotionalResponseStrategyRepository`
-   **Service**: `EmotionService`
-   **Controller**: `EmotionController` (또는 다른 컨트롤러에 통합)
-   **DTO**: `EmotionAnalysisDto`, `EmotionalResponseStrategyDto`, `EmotionAnalysisRequest`

```java
// Entity 예시: EmotionAnalysis.java
@Entity
@Table(name = "emotion_analysis")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmotionAnalysis {
    @Id
    private String id;

    @Column(nullable = false)
    private String targetEntityId; // 감정 분석 대상 ID (예: Message ID, User ID, Session ID)

    @Column(nullable = false)
    private String targetEntityType; // 감정 분석 대상 타입

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    private Map<String, Double> detectedEmotions; // 감지된 감정과 강도 (예: {"joy": 0.8, "sadness": 0.1})

    @Column(length = 50)
    private String dominantEmotion; // 가장 우세한 감정

    @Column(nullable = false, updatable = false)
    private LocalDateTime analysisTimestamp;

    @Builder
    public EmotionAnalysis(String targetEntityId, String targetEntityType, Map<String, Double> detectedEmotions, String dominantEmotion) {
        this.id = UUID.randomUUID().toString();
        this.targetEntityId = targetEntityId;
        this.targetEntityType = targetEntityType;
        this.detectedEmotions = detectedEmotions;
        this.dominantEmotion = dominantEmotion;
        this.analysisTimestamp = LocalDateTime.now();
    }
}

// Entity 예시: EmotionalResponseStrategy.java
@Entity
@Table(name = "emotional_response_strategy")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmotionalResponseStrategy {
    @Id
    private String id;

    @Column(nullable = false, length = 50)
    private String triggerEmotion; // 이 전략을 발동시키는 감정

    @Column(nullable = false, length = 50)
    private String responseType; // 응답 유형 (예: "empathetic_message", "suggestion", "tone_adjustment")

    @Lob
    private String responseTemplate; // 응답 템플릿 (변수 사용 가능)

    private int priority; // 전략 우선순위

    private boolean isActive;

    @Builder
    public EmotionalResponseStrategy(String triggerEmotion, String responseType, String responseTemplate, int priority, boolean isActive) {
        this.id = UUID.randomUUID().toString();
        this.triggerEmotion = triggerEmotion;
        this.responseType = responseType;
        this.responseTemplate = responseTemplate;
        this.priority = priority;
        this.isActive = isActive;
    }
}
```

### 4.10 적응형 학습 도메인 (Adaptive Learning Domain)

-   **Entity**: `UserProfile`, `LearningPreference`, `AdaptationRule`
-   **Repository**: `UserProfileRepository`, `LearningPreferenceRepository`, `AdaptationRuleRepository`
-   **Service**: `AdaptiveLearningService`, `UserProfileService`
-   **Controller**: `AdaptiveLearningController` (또는 다른 컨트롤러에 통합)
-   **DTO**: `UserProfileDto`, `LearningPreferenceDto`, `AdaptationRuleDto`

```java
// Entity 예시: UserProfile.java
@Entity
@Table(name = "user_profile")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfile {
    @Id
    private Long userId; // User ID를 PK로 사용

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> interactionSummary; // 사용자 상호작용 요약 (예: 선호 주제, 자주 사용하는 도구)

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> knowledgeMap; // 사용자의 지식 수준 맵 (예: {"java": "intermediate", "python": "beginner"})

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LearningPreference> preferences = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime lastUpdatedAt;

    @Builder
    public UserProfile(User user) {
        this.user = user;
        this.userId = user.getId();
        this.lastUpdatedAt = LocalDateTime.now();
    }
    // 프로필 업데이트 메서드 추가
}

// Entity 예시: LearningPreference.java
@Entity
@Table(name = "learning_preference")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LearningPreference {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserProfile userProfile;

    @Column(nullable = false, length = 100)
    private String preferenceKey; // 선호도 키 (예: "learning_style", "difficulty_level", "preferred_format")

    @Column(nullable = false)
    private String preferenceValue; // 선호도 값 (예: "visual", "advanced", "video")

    @Builder
    public LearningPreference(UserProfile userProfile, String preferenceKey, String preferenceValue) {
        this.userProfile = userProfile;
        this.preferenceKey = preferenceKey;
        this.preferenceValue = preferenceValue;
    }
}
```

### 4.11 강화 학습 도메인 (Reinforcement Learning Domain)

-   **Entity**: `RLAgentState`, `RewardSignal`, `RLPolicy`
-   **Repository**: `RLAgentStateRepository`, `RewardSignalRepository`, `RLPolicyRepository`
-   **Service**: `RLService`
-   **Controller**: `RLController` (주로 내부 사용)
-   **DTO**: `RLAgentStateDto`, `RewardSignalDto`, `RLPolicyDto`

```java
// Entity 예시: RLAgentState.java
@Entity
@Table(name = "rl_agent_state")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RLAgentState {
    @Id
    private String id;

    @Column(nullable = false, length = 100)
    private String agentId; // 에이전트 식별자

    @Lob
    private byte[] stateRepresentation; // 에이전트 상태 표현 (직렬화된 객체 또는 JSON)

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Builder
    public RLAgentState(String agentId, byte[] stateRepresentation) {
        this.id = UUID.randomUUID().toString();
        this.agentId = agentId;
        this.stateRepresentation = stateRepresentation;
        this.timestamp = LocalDateTime.now();
    }
}

// Entity 예시: RewardSignal.java
@Entity
@Table(name = "reward_signal")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RewardSignal {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String triggerEntityId; // 보상 트리거 엔티티 ID (예: ToolExecution ID, Message ID)

    @Column(nullable = false)
    private String triggerEntityType; // 트리거 엔티티 타입

    @Column(nullable = false)
    private Double rewardValue; // 보상 값

    @Column(length = 100)
    private String rewardSource; // 보상 출처 (예: "user_feedback", "goal_completion")

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Builder
    public RewardSignal(String triggerEntityId, String triggerEntityType, Double rewardValue, String rewardSource) {
        this.triggerEntityId = triggerEntityId;
        this.triggerEntityType = triggerEntityType;
        this.rewardValue = rewardValue;
        this.rewardSource = rewardSource;
        this.timestamp = LocalDateTime.now();
    }
}

// Entity 예시: RLPolicy.java
@Entity
@Table(name = "rl_policy")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RLPolicy {
    @Id
    private String id;

    @Column(nullable = false, length = 100)
    private String policyName; // 정책 이름

    @Column(nullable = false)
    private String version; // 정책 버전

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_version_id")
    private ModelVersion modelVersion; // 연관된 모델 버전 (선택적)

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> parameters; // 정책 파라미터

    private boolean isActive;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public RLPolicy(String policyName, String version, ModelVersion modelVersion, Map<String, Object> parameters, boolean isActive) {
        this.id = UUID.randomUUID().toString();
        this.policyName = policyName;
        this.version = version;
        this.modelVersion = modelVersion;
        this.parameters = parameters;
        this.isActive = isActive;
        this.createdAt = LocalDateTime.now();
    }
}
```

### 4.12 영역 간 지식 전이 도메인 (Cross-domain Knowledge Transfer Domain)

-   **Entity**: `KnowledgeSource`, `KnowledgeMapping`, `TransferLearningTask`
-   **Repository**: `KnowledgeSourceRepository`, `KnowledgeMappingRepository`, `TransferLearningTaskRepository`
-   **Service**: `KnowledgeTransferService`
-   **Controller**: `KnowledgeTransferController` (주로 내부 사용)
-   **DTO**: `KnowledgeSourceDto`, `KnowledgeMappingDto`, `TransferLearningTaskDto`

```java
// Entity 예시: KnowledgeSource.java
@Entity
@Table(name = "knowledge_source")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KnowledgeSource {
    @Id
    private String id;

    @Column(nullable = false, length = 100)
    private String sourceName;

    @Column(nullable = false, length = 100)
    private String domain;

    @Lob
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> connectionInfo; // 연결 정보 (예: API 엔드포인트, DB 접속 정보)

    @Builder
    public KnowledgeSource(String sourceName, String domain, String description, Map<String, Object> connectionInfo) {
        this.id = UUID.randomUUID().toString();
        this.sourceName = sourceName;
        this.domain = domain;
        this.description = description;
        this.connectionInfo = connectionInfo;
    }
}

// Entity 예시: KnowledgeMapping.java
@Entity
@Table(name = "knowledge_mapping")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KnowledgeMapping {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sourceConcept;

    @Column(nullable = false)
    private String targetConcept;

    @Column(nullable = false, length = 50)
    private String relationType; // 관계 유형 (예: "equivalent", "related", "broader")

    private Double confidenceScore;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public KnowledgeMapping(String sourceConcept, String targetConcept, String relationType, Double confidenceScore) {
        this.sourceConcept = sourceConcept;
        this.targetConcept = targetConcept;
        this.relationType = relationType;
        this.confidenceScore = confidenceScore;
        this.createdAt = LocalDateTime.now();
    }
}
```

### 4.13 창의적 생성 도메인 (Creative Generation Domain)

-   **Entity**: `CreativeWork`, `GenerationPrompt`
-   **Repository**: `CreativeWorkRepository`, `GenerationPromptRepository`
-   **Service**: `CreativeGenerationService`
-   **Controller**: `CreativeGenerationController`
-   **DTO**: `CreativeWorkDto`, `GenerationPromptDto`, `CreativeGenerationRequest`, `CreativeGenerationResponse`

```java
// Entity 예시: CreativeWork.java
@Entity
@Table(name = "creative_work")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreativeWork {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CreativeWorkType type; // 예: TEXT, IMAGE, AUDIO, VIDEO, CODE

    @Lob
    private String contentReference; // 생성된 콘텐츠 (텍스트 자체 또는 파일 경로/URL)

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> generationParameters; // 생성 시 사용된 파라미터 (모델, 스타일, 길이 등)

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> metadata; // 추가 메타데이터 (제목, 태그 등)

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "creativeWork", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GenerationPrompt> prompts = new ArrayList<>();

    @Builder
    public CreativeWork(User user, CreativeWorkType type, String contentReference, Map<String, Object> generationParameters, Map<String, Object> metadata) {
        this.id = UUID.randomUUID().toString();
        this.user = user;
        this.type = type;
        this.contentReference = contentReference;
        this.generationParameters = generationParameters;
        this.metadata = metadata;
        this.createdAt = LocalDateTime.now();
    }
}

// Entity 예시: GenerationPrompt.java
@Entity
@Table(name = "generation_prompt")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GenerationPrompt {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creative_work_id", nullable = false)
    private CreativeWork creativeWork;

    @Lob
    @Column(nullable = false)
    private String promptText;

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @Builder
    public GenerationPrompt(CreativeWork creativeWork, String promptText) {
        this.creativeWork = creativeWork;
        this.promptText = promptText;
        this.timestamp = LocalDateTime.now();
    }
}

public enum CreativeWorkType {
    TEXT, IMAGE, AUDIO, VIDEO, CODE, MUSIC
}
```

## 5. 핵심 서비스 및 모듈 인터페이스

시스템 아키텍처에서 정의된 주요 모듈들은 인터페이스로 정의되고 구현됩니다.

```java
// Module 인터페이스 (아키텍처 설계 참조)
public interface Module {
    String getName();
    Response process(Request request, Context context);
    // 스트리밍 처리, 초기화 등 추가 메서드 가능
}

// NLP Engine 인터페이스 예시
public interface NLPEngine extends Module {
    ConversationResponse handleConversation(ConversationRequest request, Context context);
    AnalysisResponse analyzeText(AnalysisRequest request);
    GenerationResponse generateText(GenerationRequest request, Context context);
    // ... 기타 NLP 기능 인터페이스
}

// Tool Framework 인터페이스 예시
public interface ToolFramework extends Module {
    List<ToolDto> listAvailableTools();
    ToolExecutionResponse executeTool(ToolExecutionRequest request, Context context);
    void registerTool(ExecutableTool tool);
}

// Planning Module 인터페이스 예시
public interface PlanningModule extends Module {
    PlanResponse createPlan(PlanRequest request, Context context);
    PlanExecutionResponse executePlan(String planId, Context context);
    PlanStatusResponse monitorPlan(String planId);
}

// Explainability Module 인터페이스 예시
public interface ExplainabilityModule extends Module {
    ExplanationResponse generateExplanation(ExplanationRequest request, Context context);
}

// Emotional Intelligence Module 인터페이스 예시
public interface EmotionalIntelligenceModule extends Module {
    EmotionAnalysisResponse analyzeEmotion(EmotionAnalysisRequest request);
    // 감성 기반 응답 생성 등 추가 메서드 가능
}

// Adaptive Learning Module 인터페이스 예시
public interface AdaptiveLearningModule extends Module {
    void updateUserProfile(UserProfileUpdateRequest request);
    AdaptationResponse adaptContent(AdaptationRequest request, Context context);
}

// Reinforcement Learning Module 인터페이스 예시
public interface ReinforcementLearningModule extends Module {
    void recordReward(RewardSignalDto rewardSignal);
    Action recommendAction(StateRepresentation state, Context context);
}

// Knowledge Transfer Module 인터페이스 예시
public interface KnowledgeTransferModule extends Module {
    void transferKnowledge(KnowledgeTransferRequest request);
}

// Creative Generation Module 인터페이스 예시
public interface CreativeGenerationModule extends Module {
    CreativeGenerationResponse generateCreativeContent(CreativeGenerationRequest request, Context context);
}

// 기타 모듈 인터페이스 (KnowledgeMemorySystem, MultimodalProcessingModule 등)
```

## 6. 클래스 다이어그램 (UML - 주요 관계 예시)

(Markdown으로 UML 표현은 제한적이므로, 주요 관계를 텍스트로 설명)

-   **Controller -> Service**: Controller는 HTTP 요청을 받아 DTO로 변환 후 해당 Service 메서드를 호출합니다. Service의 응답 DTO를 받아 HTTP 응답으로 변환합니다.
-   **Service -> Repository**: Service는 비즈니스 로직을 수행하며, 데이터 영속성이 필요할 때 Repository 인터페이스를 통해 데이터베이스와 상호작용합니다.
-   **Service -> Service/Module**: Service는 다른 Service나 핵심 Module 인터페이스를 호출하여 필요한 기능을 위임할 수 있습니다. (예: `ConversationService`가 `NLPEngine` 모듈 호출)
-   **Entity <-> Repository**: Repository는 특정 Entity의 CRUD 작업을 담당합니다.
-   **Entity 관계**: `@OneToMany`, `@ManyToOne`, `@ManyToMany` 등을 사용하여 Entity 간의 관계를 정의합니다. (예: `Conversation` <-> `Message`)
-   **DTO <-> Entity**: Service 계층 또는 Mapper 유틸리티에서 DTO와 Entity 간의 변환을 수행합니다.

```mermaid
classDiagram
    class AuthController {
        +login(LoginRequest)
        +register(RegisterRequest)
    }
    class AuthService {
        +authenticate(String, String)
        +registerUser(RegisterRequest)
    }
    class UserService {
        +getUserById(Long)
        +updateUserProfile(Long, UserProfileDto)
    }
    class UserRepository {
        +findById(Long)
        +findByUsername(String)
        +save(User)
    }
    class User {
        -Long id
        -String username
        -String email
        -String passwordHash
        -Set~Role~ roles
    }
    class Role {
        -Long id
        -String name
    }
    class LoginRequest
    class RegisterRequest
    class UserDto

    AuthController --> AuthService
    AuthService --> UserRepository
    AuthService --> UserService
    UserService --> UserRepository
    UserRepository ..> User : manages
    User "1" *-- "*" Role : has
    AuthService ..> User : creates/updates
    AuthService ..> JwtTokenResponse : returns
    AuthController ..> LoginRequest : uses
    AuthController ..> RegisterRequest : uses
    UserService ..> UserDto : returns
```

## 7. 결론

이 객체 모델 설계는 통합 AGI 시스템의 핵심 도메인과 상호작용을 정의합니다. 명확한 책임 분리, 인터페이스 기반 설계, DTO 사용 등을 통해 유연하고 확장 가능하며 유지보수하기 쉬운 코드 구조를 목표로 합니다. 이 설계는 시스템 아키텍처, 데이터베이스 스키마, API 설계와 연계되어 시스템 전체의 일관성을 보장합니다. 실제 구현 시에는 세부적인 예외 처리, 로깅, 트랜잭션 관리 등이 추가적으로 고려되어야 합니다.
