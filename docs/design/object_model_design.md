# 통합 AGI 시스템 객체 모델 설계

## 1. 개요

이 문서는 Spring Boot 3.4.5, Java 17 기반의 통합 AGI 시스템을 위한 객체 모델 설계를 설명합니다. 객체 모델은 도메인 주도 설계(DDD) 원칙을 따르며, JPA(Java Persistence API)를 사용하여 데이터베이스와 상호작용합니다. 이 설계는 시스템의 핵심 도메인, 서비스, 저장소, DTO(Data Transfer Object) 구조를 정의하며, **샌드박스 환경에서의 안전한 코드 및 도구 실행**을 위한 객체 모델을 포함합니다.

## 2. 설계 원칙

1. **도메인 주도 설계(DDD)**: 핵심 도메인 로직을 엔티티와 값 객체에 집중시킵니다.
2. **계층형 아키텍처**: 프레젠테이션(Controller), 애플리케이션(Service), 도메인(Entity, Repository), 인프라스트럭처(DB, 외부 API) 계층으로 분리합니다.
3. **JPA 엔티티**: 데이터베이스 테이블과 매핑되는 영속성 객체로 설계합니다.
4. **DTO 분리**: 계층 간 데이터 전송을 위해 DTO를 사용하며, 엔티티를 직접 노출하지 않습니다.
5. **Lombok 활용**: `@Getter`, `@Builder`, `@NoArgsConstructor` 등을 사용하여 코드 간결성을 높입니다.
6. **인터페이스 기반 설계**: 서비스 및 저장소는 인터페이스를 정의하고 구현체에서 로직을 처리합니다.
7. **의존성 주입(DI)**: Spring 프레임워크의 DI를 활용하여 객체 간 의존성을 관리합니다.
8. **예외 처리**: 비즈니스 예외와 시스템 예외를 구분하여 처리합니다.
9. **테스트 용이성**: 단위 테스트 및 통합 테스트가 용이하도록 설계합니다.
10. **샌드박스 격리**: 샌드박스 관련 객체는 다른 도메인과 명확히 분리하여 관리합니다.

## 3. 주요 패키지 구조

```
com.agi
├── config          // 설정 클래스 (JPA, Security, DL4J 등)
├── controller      // API 요청 처리 (REST Controller)
├── dto             // 데이터 전송 객체
│   ├── request
│   └── response
├── entity          // JPA 엔티티
├── enums           // 열거형 타입
├── exception       // 사용자 정의 예외
├── repository      // JPA Repository 인터페이스
├── service         // 비즈니스 로직 처리
│   └── impl        // 서비스 구현체
├── util            // 유틸리티 클래스
└── AgiApplication.java // 메인 애플리케이션
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
    private String contentType; // 예: TEXT, MARKDOWN, URL, FILE_REFERENCE

    @Column(nullable = false, length = 100)
    private String source; // 출처

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> metadata; // 추가 메타데이터 (예: 관련 도메인, 신뢰도 점수)

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "knowledge_item_tags",
        joinColumns = @JoinColumn(name = "knowledge_item_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<KnowledgeTag> tags = new HashSet<>();

    @Builder
    public KnowledgeItem(String title, String content, String contentType, String source, Map<String, Object> metadata) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.content = content;
        this.contentType = contentType;
        this.source = source;
        this.metadata = metadata;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Builder
    public KnowledgeTag(String name) {
        this.name = name;
    }
}
```

### 4.6 메모리 도메인 (Memory Domain)

-   **Entity**: `MemoryRecord`
-   **Repository**: `MemoryRecordRepository`
-   **Service**: `MemoryService`
-   **Controller**: `MemoryController`
-   **DTO**: `MemoryRecordDto`, `MemoryRecallRequest`

```java
// Entity 예시: MemoryRecord.java
@Entity
@Table(name = "memory_record")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemoryRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String memoryType; // 예: CONVERSATION_SUMMARY, USER_PREFERENCE, FACT, TASK_CONTEXT

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime lastAccessedAt;

    private Double relevanceScore; // 검색 시 관련성 점수 (임시 필드)

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> metadata; // 추가 메타데이터 (예: 관련 엔티티 ID, 출처)

    @Builder
    public MemoryRecord(User user, String memoryType, String content, Map<String, Object> metadata) {
        this.user = user;
        this.memoryType = memoryType;
        this.content = content;
        this.metadata = metadata;
        this.createdAt = LocalDateTime.now();
        this.lastAccessedAt = LocalDateTime.now();
    }
}
```

### 4.7 멀티모달 도메인 (Multimodal Domain)

-   **Entity**: `MediaAsset`
-   **Repository**: `MediaAssetRepository`
-   **Service**: `MediaProcessingService`
-   **Controller**: `MediaController`
-   **DTO**: `MediaAssetDto`, `MediaProcessRequest`

```java
// Entity 예시: MediaAsset.java
@Entity
@Table(name = "media_asset")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MediaAsset {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String mediaType; // IMAGE, AUDIO, VIDEO

    @Column(nullable = false, length = 255)
    private String originalFileName;

    @Column(nullable = false, length = 255)
    private String storagePath; // 저장 경로 (로컬 또는 클라우드)

    @Column(nullable = false, length = 100)
    private String mimeType;

    @Column(nullable = false)
    private Long sizeBytes;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> processingResults; // 처리 결과 (예: 텍스트 추출, 객체 감지, 자막)

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public MediaAsset(User user, String mediaType, String originalFileName, String storagePath, String mimeType, Long sizeBytes) {
        this.id = UUID.randomUUID().toString();
        this.user = user;
        this.mediaType = mediaType;
        this.originalFileName = originalFileName;
        this.storagePath = storagePath;
        this.mimeType = mimeType;
        this.sizeBytes = sizeBytes;
        this.createdAt = LocalDateTime.now();
    }
}
```

### 4.8 자가 학습 도메인 (Self-Learning Domain)

-   **Entity**: `ModelVersion`, `TrainingLog`, `UserFeedback`
-   **Repository**: `ModelVersionRepository`, `TrainingLogRepository`, `UserFeedbackRepository`
-   **Service**: `LearningService`, `ModelManagementService`
-   **Controller**: `LearningController`
-   **DTO**: `ModelVersionDto`, `TrainingLogDto`, `UserFeedbackDto`, `FeedbackRequest`

```java
// Entity 예시: ModelVersion.java
@Entity
@Table(name = "model_version")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModelVersion {
    @Id
    private String id;

    @Column(nullable = false, length = 100)
    private String modelName; // 모델 이름 (예: sentiment_analyzer, text_generator)

    @Column(nullable = false, length = 50)
    private String version;

    @Lob
    private String description;

    @Column(nullable = false, length = 255)
    private String modelPath; // 학습된 모델 파일 경로

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> trainingParameters; // 학습 파라미터

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> evaluationMetrics; // 평가 지표

    private boolean isActive;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public ModelVersion(String modelName, String version, String description, String modelPath, Map<String, Object> trainingParameters, Map<String, Object> evaluationMetrics) {
        this.id = UUID.randomUUID().toString();
        this.modelName = modelName;
        this.version = version;
        this.description = description;
        this.modelPath = modelPath;
        this.trainingParameters = trainingParameters;
        this.evaluationMetrics = evaluationMetrics;
        this.isActive = false;
        this.createdAt = LocalDateTime.now();
    }
}

// Entity 예시: UserFeedback.java
@Entity
@Table(name = "user_feedback")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserFeedback {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String targetEntityId; // 피드백 대상 엔티티 ID (예: Message ID, ToolExecution ID)

    @Column(nullable = false)
    private String targetEntityType; // 피드백 대상 엔티티 타입

    @Column(nullable = false)
    private Integer rating; // 평점 (예: 1-5)

    @Lob
    private String comment;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public UserFeedback(User user, String targetEntityId, String targetEntityType, Integer rating, String comment) {
        this.user = user;
        this.targetEntityId = targetEntityId;
        this.targetEntityType = targetEntityType;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = LocalDateTime.now();
    }
}
```

### 4.9 설명 가능성 도메인 (Explainability Domain)

-   **Entity**: `Explanation`
-   **Repository**: `ExplanationRepository`
-   **Service**: `ExplainabilityService`
-   **Controller**: `ExplainabilityController`
-   **DTO**: `ExplanationDto`, `ExplanationRequest`

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
    private String targetEntityId; // 설명 대상 엔티티 ID (예: Message ID, Decision ID)

    @Column(nullable = false)
    private String targetEntityType; // 설명 대상 엔티티 타입

    @Column(nullable = false, length = 100)
    private String explanationType; // 설명 유형 (예: LIME, SHAP, FEATURE_IMPORTANCE)

    @Lob
    @Column(nullable = false)
    private String explanationContent; // 설명 내용 (텍스트 또는 JSON)

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> parameters; // 설명 생성 시 사용된 파라미터

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Explanation(String targetEntityId, String targetEntityType, String explanationType, String explanationContent, Map<String, Object> parameters) {
        this.id = UUID.randomUUID().toString();
        this.targetEntityId = targetEntityId;
        this.targetEntityType = targetEntityType;
        this.explanationType = explanationType;
        this.explanationContent = explanationContent;
        this.parameters = parameters;
        this.createdAt = LocalDateTime.now();
    }
}
```

### 4.10 감성 지능 도메인 (Emotional Intelligence Domain)

-   **Entity**: `EmotionAnalysis`, `EmotionalResponseStrategy`
-   **Repository**: `EmotionAnalysisRepository`, `EmotionalResponseStrategyRepository`
-   **Service**: `EmotionalIntelligenceService`
-   **Controller**: `EmotionController`
-   **DTO**: `EmotionAnalysisDto`, `EmotionalResponseStrategyDto`, `EmotionDetectionRequest`

```java
// Entity 예시: EmotionAnalysis.java
@Entity
@Table(name = "emotion_analysis")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmotionAnalysis {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String targetEntityId; // 분석 대상 엔티티 ID (예: Message ID)

    @Column(nullable = false)
    private String targetEntityType; // 분석 대상 엔티티 타입

    @Column(nullable = false, length = 50)
    private String dominantEmotion; // 주 감정

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Double> emotionScores; // 감정별 점수 (예: {"happy": 0.8, "sad": 0.1})

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public EmotionAnalysis(String targetEntityId, String targetEntityType, String dominantEmotion, Map<String, Double> emotionScores) {
        this.targetEntityId = targetEntityId;
        this.targetEntityType = targetEntityType;
        this.dominantEmotion = dominantEmotion;
        this.emotionScores = emotionScores;
        this.createdAt = LocalDateTime.now();
    }
}

// Entity 예시: EmotionalResponseStrategy.java
@Entity
@Table(name = "emotional_response_strategy")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmotionalResponseStrategy {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String detectedEmotion; // 감지된 감정

    @Column(nullable = false, length = 50)
    private String responseStyle; // 응답 스타일 (예: EMPATHETIC, NEUTRAL, SUPPORTIVE)

    private Double intensityThreshold; // 감정 강도 임계값

    @Lob
    private String responseTemplate; // 응답 템플릿

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> parameters; // 추가 파라미터

    @Builder
    public EmotionalResponseStrategy(String detectedEmotion, String responseStyle, Double intensityThreshold, String responseTemplate, Map<String, Object> parameters) {
        this.detectedEmotion = detectedEmotion;
        this.responseStyle = responseStyle;
        this.intensityThreshold = intensityThreshold;
        this.responseTemplate = responseTemplate;
        this.parameters = parameters;
    }
}
```

### 4.11 적응형 학습 도메인 (Adaptive Learning Domain)

-   **Entity**: `UserProfile`, `LearningPreference`, `AdaptationRule`
-   **Repository**: `UserProfileRepository`, `LearningPreferenceRepository`, `AdaptationRuleRepository`
-   **Service**: `AdaptiveLearningService`
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

// Entity 예시: AdaptationRule.java
@Entity
@Table(name = "adaptation_rule")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdaptationRule {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String ruleName;

    @Lob
    private String conditionExpression; // 규칙 적용 조건 (예: SpEL)

    @Lob
    private String actionExpression; // 적용할 액션 (예: 파라미터 조정, 추천 변경)

    private boolean isActive;

    @Builder
    public AdaptationRule(String ruleName, String conditionExpression, String actionExpression) {
        this.ruleName = ruleName;
        this.conditionExpression = conditionExpression;
        this.actionExpression = actionExpression;
        this.isActive = true;
    }
}
```

### 4.12 강화 학습 도메인 (Reinforcement Learning Domain)

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
    private String agentId;

    @Column(nullable = false, length = 100)
    private String environmentId; // 상호작용 환경 ID (예: Sandbox ID, Conversation ID)

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> stateRepresentation; // 상태 표현 (예: 특징 벡터)

    @Column(nullable = false)
    private LocalDateTime lastUpdatedAt;

    @Builder
    public RLAgentState(String agentId, String environmentId, Map<String, Object> stateRepresentation) {
        this.agentId = agentId;
        this.environmentId = environmentId;
        this.stateRepresentation = stateRepresentation;
        this.lastUpdatedAt = LocalDateTime.now();
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
    private String agentId;

    @Column(nullable = false)
    private String environmentId;

    @Column(nullable = false)
    private String actionId; // 보상을 유발한 행동 ID

    @Column(nullable = false)
    private Double rewardValue;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public RewardSignal(String agentId, String environmentId, String actionId, Double rewardValue) {
        this.agentId = agentId;
        this.environmentId = environmentId;
        this.actionId = actionId;
        this.rewardValue = rewardValue;
        this.createdAt = LocalDateTime.now();
    }
}

// Entity 예시: RLPolicy.java
@Entity
@Table(name = "rl_policy")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RLPolicy {
    @Id
    private String policyId;

    @Column(nullable = false, length = 100)
    private String agentId;

    @Column(nullable = false, length = 100)
    private String policyType; // 정책 유형 (예: Q-Learning, PPO)

    @Lob
    private byte[] policyData; // 학습된 정책 데이터 (직렬화된 객체)

    @Column(nullable = false)
    private LocalDateTime lastUpdatedAt;

    @Builder
    public RLPolicy(String policyId, String agentId, String policyType, byte[] policyData) {
        this.policyId = policyId;
        this.agentId = agentId;
        this.policyType = policyType;
        this.policyData = policyData;
        this.lastUpdatedAt = LocalDateTime.now();
    }
}
```

### 4.13 영역 간 지식 전이 도메인 (Cross-domain Knowledge Transfer Domain)

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
    private String domain; // 지식 소스 도메인 (예: "Python Programming", "Web Scraping")

    @Lob
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> metadata; // 추가 메타데이터 (예: 관련 모델, 데이터셋 경로)

    @Builder
    public KnowledgeSource(String sourceName, String domain, String description, Map<String, Object> metadata) {
        this.id = UUID.randomUUID().toString();
        this.sourceName = sourceName;
        this.domain = domain;
        this.description = description;
        this.metadata = metadata;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_knowledge_id", nullable = false)
    private KnowledgeSource sourceKnowledge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_knowledge_id", nullable = false)
    private KnowledgeSource targetKnowledge;

    @Column(nullable = false, length = 100)
    private String mappingType; // 매핑 유형 (예: ANALOGY, TRANSLATION, GENERALIZATION)

    @Lob
    private String mappingRule; // 매핑 규칙 또는 변환 로직

    private Double confidenceScore; // 매핑 신뢰도

    @Builder
    public KnowledgeMapping(KnowledgeSource sourceKnowledge, KnowledgeSource targetKnowledge, String mappingType, String mappingRule, Double confidenceScore) {
        this.sourceKnowledge = sourceKnowledge;
        this.targetKnowledge = targetKnowledge;
        this.mappingType = mappingType;
        this.mappingRule = mappingRule;
        this.confidenceScore = confidenceScore;
    }
}

// Entity 예시: TransferLearningTask.java
@Entity
@Table(name = "transfer_learning_task")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransferLearningTask {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_model_id", nullable = false)
    private ModelVersion sourceModel;

    @Column(nullable = false, length = 100)
    private String targetDomain;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> taskParameters; // 전이 학습 파라미터

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExecutionStatus status;

    private String resultModelId; // 결과 모델 ID

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    @Builder
    public TransferLearningTask(ModelVersion sourceModel, String targetDomain, Map<String, Object> taskParameters) {
        this.id = UUID.randomUUID().toString();
        this.sourceModel = sourceModel;
        this.targetDomain = targetDomain;
        this.taskParameters = taskParameters;
        this.status = ExecutionStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
    // 상태 변경 메서드
}
```

### 4.14 창의적 생성 도메인 (Creative Generation Domain)

-   **Entity**: `CreativeWork`, `GenerationPrompt`
-   **Repository**: `CreativeWorkRepository`, `GenerationPromptRepository`
-   **Service**: `CreativeGenerationService`
-   **Controller**: `CreativeController`
-   **DTO**: `CreativeWorkDto`, `GenerationPromptDto`, `GenerationRequest`

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
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String workType; // 생성물 유형 (예: TEXT, IMAGE, MUSIC, CODE)

    @Lob
    private String content; // 생성된 콘텐츠 (텍스트 또는 참조 경로)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prompt_id", nullable = false)
    private GenerationPrompt prompt;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> metadata; // 추가 메타데이터 (예: 사용된 모델, 스타일)

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public CreativeWork(User user, String workType, String content, GenerationPrompt prompt, Map<String, Object> metadata) {
        this.id = UUID.randomUUID().toString();
        this.user = user;
        this.workType = workType;
        this.content = content;
        this.prompt = prompt;
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
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Lob
    @Column(nullable = false)
    private String promptText;

    @Column(nullable = false, length = 100)
    private String targetWorkType; // 생성 목표 유형

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> parameters; // 생성 파라미터 (예: 스타일, 길이, 온도)

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public GenerationPrompt(User user, String promptText, String targetWorkType, Map<String, Object> parameters) {
        this.id = UUID.randomUUID().toString();
        this.user = user;
        this.promptText = promptText;
        this.targetWorkType = targetWorkType;
        this.parameters = parameters;
        this.createdAt = LocalDateTime.now();
    }
}
```

### 4.15 샌드박스 도메인 (Sandbox Domain)

-   **Entity**: `Sandbox`, `SandboxResource`, `SandboxSecurityPolicy`
-   **Repository**: `SandboxRepository`, `SandboxResourceRepository`, `SandboxSecurityPolicyRepository`
-   **Service**: `SandboxService`, `SandboxLifecycleManager`
-   **Controller**: `SandboxController`
-   **DTO**: `SandboxDto`, `SandboxResourceDto`, `SandboxSecurityPolicyDto`, `SandboxCreateRequest`, `CodeExecutionRequest`, `CodeExecutionResponse`

```java
// Entity 예시: Sandbox.java
@Entity
@Table(name = "sandbox")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sandbox {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String environmentType; // 예: PYTHON_3_11, NODE_20, BASH

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SandboxStatus status; // CREATED, RUNNING, PAUSED, STOPPED, ERROR

    @Column(unique = true)
    private String containerId; // Docker 컨테이너 ID

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime lastStartedAt;

    private LocalDateTime lastStoppedAt;

    private LocalDateTime lastActiveAt; // API 호출 등 마지막 활동 시간

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> configuration; // 추가 설정 (예: 포트 매핑, 볼륨 마운트)

    @OneToMany(mappedBy = "sandbox", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SandboxResource> resources = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "security_policy_id")
    private SandboxSecurityPolicy securityPolicy;

    @Builder
    public Sandbox(User user, String environmentType, Map<String, Object> configuration, SandboxSecurityPolicy securityPolicy) {
        this.id = UUID.randomUUID().toString();
        this.user = user;
        this.environmentType = environmentType;
        this.status = SandboxStatus.CREATED;
        this.configuration = configuration;
        this.securityPolicy = securityPolicy;
        this.createdAt = LocalDateTime.now();
        this.lastActiveAt = LocalDateTime.now();
    }

    // 상태 변경 및 컨테이너 ID 설정 메서드
    public void updateStatus(SandboxStatus status) {
        this.status = status;
        LocalDateTime now = LocalDateTime.now();
        this.lastActiveAt = now;
        if (status == SandboxStatus.RUNNING) this.lastStartedAt = now;
        if (status == SandboxStatus.STOPPED || status == SandboxStatus.ERROR) this.lastStoppedAt = now;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
        this.lastActiveAt = LocalDateTime.now();
    }

    public void updateLastActiveTime() {
        this.lastActiveAt = LocalDateTime.now();
    }
}

public enum SandboxStatus {
    CREATED, STARTING, RUNNING, PAUSING, PAUSED, STOPPING, STOPPED, ERROR, DELETING
}

// Entity 예시: SandboxResource.java
@Entity
@Table(name = "sandbox_resource")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SandboxResource {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sandbox_id", nullable = false)
    private Sandbox sandbox;

    @Column(nullable = false, length = 50)
    private String resourceType; // CPU, MEMORY, DISK, NETWORK

    @Column(nullable = false)
    private String limitValue; // 제한 값 (예: "2 cores", "4Gi", "10Gi", "100 Mbps")

    @Column(nullable = false)
    private String usageValue; // 현재 사용량 (모니터링)

    @Column(nullable = false)
    private LocalDateTime lastMonitoredAt;

    @Builder
    public SandboxResource(Sandbox sandbox, String resourceType, String limitValue) {
        this.sandbox = sandbox;
        this.resourceType = resourceType;
        this.limitValue = limitValue;
        this.usageValue = "0"; // 초기값
        this.lastMonitoredAt = LocalDateTime.now();
    }
    // 사용량 업데이트 메서드
}

// Entity 예시: SandboxSecurityPolicy.java
@Entity
@Table(name = "sandbox_security_policy")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SandboxSecurityPolicy {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String policyName;

    private boolean allowNetworkAccess;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> allowedHosts; // 허용된 호스트 목록

    private Long maxExecutionTimeSeconds; // 최대 실행 시간 (초)

    private Long maxFileSizeMb; // 최대 파일 크기 (MB)

    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> forbiddenCommands; // 금지된 명령어 목록

    @Builder
    public SandboxSecurityPolicy(String policyName, boolean allowNetworkAccess, List<String> allowedHosts, Long maxExecutionTimeSeconds, Long maxFileSizeMb, List<String> forbiddenCommands) {
        this.policyName = policyName;
        this.allowNetworkAccess = allowNetworkAccess;
        this.allowedHosts = allowedHosts;
        this.maxExecutionTimeSeconds = maxExecutionTimeSeconds;
        this.maxFileSizeMb = maxFileSizeMb;
        this.forbiddenCommands = forbiddenCommands;
    }
}

// DTO 예시: SandboxDto.java
@Getter
@Builder
public class SandboxDto {
    private String id;
    private String userId;
    private String environmentType;
    private SandboxStatus status;
    private String containerId;
    private LocalDateTime createdAt;
    private LocalDateTime lastStartedAt;
    private LocalDateTime lastStoppedAt;
    private LocalDateTime lastActiveAt;
    private Map<String, Object> configuration;
    private List<SandboxResourceDto> resources;
    private SandboxSecurityPolicyDto securityPolicy;

    public static SandboxDto fromEntity(Sandbox sandbox) {
        return SandboxDto.builder()
                .id(sandbox.getId())
                .userId(sandbox.getUser().getId().toString())
                .environmentType(sandbox.getEnvironmentType())
                .status(sandbox.getStatus())
                .containerId(sandbox.getContainerId())
                .createdAt(sandbox.getCreatedAt())
                .lastStartedAt(sandbox.getLastStartedAt())
                .lastStoppedAt(sandbox.getLastStoppedAt())
                .lastActiveAt(sandbox.getLastActiveAt())
                .configuration(sandbox.getConfiguration())
                .resources(sandbox.getResources().stream().map(SandboxResourceDto::fromEntity).collect(Collectors.toList()))
                .securityPolicy(sandbox.getSecurityPolicy() != null ? SandboxSecurityPolicyDto.fromEntity(sandbox.getSecurityPolicy()) : null)
                .build();
    }
}

// DTO 예시: SandboxResourceDto.java
@Getter
@Builder
public class SandboxResourceDto {
    private String resourceType;
    private String limitValue;
    private String usageValue;
    private LocalDateTime lastMonitoredAt;

    public static SandboxResourceDto fromEntity(SandboxResource resource) {
        return SandboxResourceDto.builder()
                .resourceType(resource.getResourceType())
                .limitValue(resource.getLimitValue())
                .usageValue(resource.getUsageValue())
                .lastMonitoredAt(resource.getLastMonitoredAt())
                .build();
    }
}

// DTO 예시: SandboxSecurityPolicyDto.java
@Getter
@Builder
public class SandboxSecurityPolicyDto {
    private String policyName;
    private boolean allowNetworkAccess;
    private List<String> allowedHosts;
    private Long maxExecutionTimeSeconds;
    private Long maxFileSizeMb;
    private List<String> forbiddenCommands;

    public static SandboxSecurityPolicyDto fromEntity(SandboxSecurityPolicy policy) {
        return SandboxSecurityPolicyDto.builder()
                .policyName(policy.getPolicyName())
                .allowNetworkAccess(policy.isAllowNetworkAccess())
                .allowedHosts(policy.getAllowedHosts())
                .maxExecutionTimeSeconds(policy.getMaxExecutionTimeSeconds())
                .maxFileSizeMb(policy.getMaxFileSizeMb())
                .forbiddenCommands(policy.getForbiddenCommands())
                .build();
    }
}
```

## 5. 핵심 서비스 및 모듈 인터페이스

각 도메인별 핵심 서비스 인터페이스를 정의합니다. 구현체는 `service.impl` 패키지에 위치합니다.

-   `AuthService`: 사용자 인증, JWT 토큰 발급/검증
-   `UserService`: 사용자 정보 관리 (CRUD)
-   `ConversationService`: 대화 및 메시지 관리
-   `NlpService`: 자연어 이해, 생성, 분석 (내부적으로 DL4J, Spring AI 등 활용)
-   `ToolService`: 도구 정보 관리, 도구 검색/등록
-   `ToolExecutorService`: 도구 실행 요청 처리, 결과 반환 (필요시 `SandboxService` 호출)
-   `PlanService`: 계획 생성, 수정, 조회
-   `PlanExecutionService`: 계획 단계별 실행 관리 (필요시 `ToolExecutorService` 또는 `SandboxService` 호출)
-   `KnowledgeService`: 지식 저장, 검색, 관리 (Vector DB 연동 등)
-   `MemoryService`: 사용자별 단기/장기 기억 관리, 컨텍스트 유지
-   `MediaProcessingService`: 이미지, 오디오, 비디오 처리 (내부적으로 JavaCV, DL4J 등 활용, 필요시 `SandboxService` 호출)
-   `LearningService`: 사용자 피드백 처리, 모델 학습/평가 관리
-   `ModelManagementService`: 학습된 모델 버전 관리, 배포
-   `ExplainabilityService`: 모델 예측 및 시스템 결정 과정 설명 생성
-   `EmotionalIntelligenceService`: 텍스트/음성 기반 감정 분석, 공감적 응답 생성
-   `AdaptiveLearningService`: 사용자 프로필 기반 시스템 동작 개인화
-   `RLService`: 강화 학습 에이전트 관리, 학습 루프 실행
-   `KnowledgeTransferService`: 도메인 간 지식 전이 작업 관리
-   `CreativeGenerationService`: 텍스트, 이미지 등 창의적 콘텐츠 생성
-   `SandboxService`: 샌드박스 생성, 상태 관리, 코드/명령 실행 요청 처리
-   `SandboxLifecycleManager`: 샌드박스 리소스 관리, 비활성 샌드박스 정리

## 6. DTO 변환 전략

-   **Entity → DTO**: 각 DTO 클래스 내에 `fromEntity(Entity entity)` 정적 팩토리 메서드를 구현하여 변환합니다. 서비스 계층에서 이 메서드를 호출하여 Controller로 DTO를 반환합니다.
-   **Request DTO → Entity**: 서비스 계층에서 Request DTO를 받아 필요한 유효성 검증 후, `@Builder`를 사용하여 엔티티를 생성하거나 기존 엔티티를 수정합니다.
-   **Lazy Loading 처리**: DTO 변환 시 필요한 연관 엔티티 정보만 로드하도록 주의합니다. `@Transactional(readOnly = true)` 어노테이션을 서비스 메서드에 적용하고, 필요한 경우 Fetch Join을 사용하거나 DTO 변환 로직 내에서 프록시 객체 초기화를 수행합니다.
-   **민감 정보 제외**: DTO 변환 시 비밀번호 등 민감 정보는 제외합니다.

## 7. 샌드박스 모듈과 다른 모듈 간의 통합

샌드박스 모듈은 안전한 코드 및 도구 실행 환경을 제공하며, 다른 핵심 AI 모듈들과 긴밀하게 통합되어 시스템의 지능적인 동작을 지원합니다.

### 7.1 자연어 처리 엔진 (NLP Engine) 통합

-   **상호작용**: 사용자의 자연어 명령어를 처리하여 샌드박스에서 실행할 코드나 도구를 식별합니다 (명령어 처리, 자연어 이해). 샌드박스 실행 결과를 자연어로 요약하거나 설명하여 사용자에게 전달합니다 (자연어 생성).
-   **데이터 교환**: 처리된 명령어, 실행할 코드/스크립트, 샌드박스 실행 로그, 생성된 자연어 설명.
-   **객체/인터페이스**: `NlpService`, `SandboxService`, `Message`, `ToolExecution`.
-   **시나리오**: 사용자가 "파이썬으로 현재 폴더 파일 목록 출력해줘"라고 요청하면, NLP 엔진이 이를 해석하여 `SandboxService`에 코드 실행을 요청하고, 결과를 받아 사용자에게 자연어로 설명합니다.

### 7.2 도구 사용 프레임워크 (Tool Framework) 통합

-   **상호작용**: `ToolSelector`가 선택한 도구가 샌드박스 실행을 요구할 경우, `ToolExecutor`는 `SandboxService`를 호출하여 안전한 환경에서 도구를 실행합니다.
-   **데이터 교환**: 실행할 도구 정보 (`Tool`), 입력 파라미터, 샌드박스 ID, 실행 결과 (`ToolExecution.result`), 오류 로그 (`ToolExecution.errorDetails`).
-   **객체/인터페이스**: `ToolExecutorService`, `SandboxService`, `Tool`, `ToolExecution`, `Sandbox`.
-   **시나리오**: 웹 검색 도구가 선택되면, `ToolExecutorService`는 `SandboxService`를 통해 격리된 브라우저 환경(샌드박스)에서 검색을 수행하고 결과를 `ToolExecution`에 기록합니다.

### 7.3 계획 수립 모듈 (Planning Module) 통합

-   **상호작용**: `PlanningEngine`이 생성한 계획 단계(`PlanStep`) 중 일부는 샌드박스 실행을 포함할 수 있습니다. `PlanExecutionService`는 해당 단계에서 `SandboxService`를 호출합니다. 샌드박스 실행 결과는 계획 진행 상태 업데이트에 사용됩니다.
-   **데이터 교환**: 실행할 작업 내용 (`PlanStep.description`), 샌드박스 실행 결과, 계획 상태 업데이트 정보.
-   **객체/인터페이스**: `PlanExecutionService`, `SandboxService`, `Plan`, `PlanStep`, `ToolExecution` (PlanStep과 연관).
-   **시나리오**: "데이터 분석 보고서 작성" 계획의 "데이터 전처리 스크립트 실행" 단계에서 `PlanExecutionService`는 `SandboxService`를 호출하여 파이썬 스크립트를 실행합니다.

### 7.4 지식 및 기억 시스템 (Knowledge & Memory System) 통합

-   **상호작용**: 샌드박스에서 성공적으로 실행된 코드 스니펫, 유용한 스크립트, 도구 사용 결과 등은 `KnowledgeItem`으로 저장될 수 있습니다. `MemoryService`는 특정 샌드박스 작업과 관련된 컨텍스트(`MemoryRecord`)를 저장하고 인출하여 장기적인 작업 흐름을 지원합니다.
-   **데이터 교환**: 샌드박스 실행 로그, 성공적인 코드/스크립트, 도구 결과, 관련 컨텍스트 정보.
-   **객체/인터페이스**: `KnowledgeService`, `MemoryService`, `SandboxService`, `ToolExecution`, `KnowledgeItem`, `MemoryRecord`.
-   **시나리오**: 샌드박스에서 특정 라이브러리를 사용하는 파이썬 코드가 성공적으로 실행되면, 해당 코드는 `KnowledgeItem`으로 저장되어 나중에 유사한 요청 시 재사용될 수 있습니다.

### 7.5 멀티모달 처리 기능 (Multimodal Processing) 통합

-   **상호작용**: 이미지/오디오/비디오 파일을 처리하는 도구(예: OCR, STT)는 샌드박스 환경 내에서 실행될 수 있습니다. `MediaProcessingService`는 샌드박스를 활용하여 외부 라이브러리나 도구를 안전하게 실행합니다.
-   **데이터 교환**: 처리할 미디어 파일 경로 (`MediaAsset.storagePath`), 처리 옵션, 처리 결과 (`MediaAsset.processingResults`).
-   **객체/인터페이스**: `MediaProcessingService`, `SandboxService`, `MediaAsset`, `ToolExecution`.
-   **시나리오**: 사용자가 이미지를 업로드하고 텍스트 추출을 요청하면, `MediaProcessingService`는 샌드박스 내에서 OCR 도구를 실행하여 결과를 `MediaAsset`에 저장합니다.

### 7.6 자가 학습 기능 (Self-Learning) 통합

-   **상호작용**: 샌드박스 내 도구 실행 성공/실패 결과 (`ToolExecution`) 및 관련 사용자 피드백 (`UserFeedback`)은 `LearningService`로 전달되어 모델 개선에 활용됩니다. 예를 들어, 특정 유형의 코드 생성 실패 패턴을 학습하여 모델을 개선할 수 있습니다.
-   **데이터 교환**: `ToolExecution` 상태 및 결과, `UserFeedback` 데이터, 학습 데이터셋, 업데이트된 모델 정보 (`ModelVersion`).
-   **객체/인터페이스**: `LearningService`, `SandboxService`, `ToolExecution`, `UserFeedback`, `ModelVersion`.
-   **시나리오**: 사용자가 샌드박스에서 실행된 코드 생성 결과에 대해 낮은 평점(`UserFeedback`)을 주면, `LearningService`는 해당 코드와 피드백을 분석하여 코드 생성 모델(`ModelVersion`) 업데이트를 트리거할 수 있습니다.

### 7.7 설명 가능성 (Explainability) 통합

-   **상호작용**: `ExplainabilityService`는 샌드박스 내에서 발생한 오류의 원인(`ToolExecution.errorDetails`)이나 특정 도구/코드 실행 결정 과정을 설명하는 데 사용될 수 있습니다. 샌드박스 실행 로그나 상태 정보를 분석하여 설명을 생성합니다.
-   **데이터 교환**: 설명 요청 대상 ID (`ToolExecution` ID, `PlanStep` ID), 샌드박스 로그, 실행 컨텍스트, 생성된 설명 (`Explanation`).
-   **객체/인터페이스**: `ExplainabilityService`, `SandboxService`, `ToolExecution`, `PlanStep`, `Explanation`.
-   **시나리오**: 샌드박스에서 스크립트 실행이 실패했을 때, 사용자가 "왜 실패했어?"라고 물으면, `ExplainabilityService`는 관련 `ToolExecution` 로그를 분석하여 실패 원인에 대한 `Explanation`을 생성합니다.

### 7.8 감성 지능 (Emotional Intelligence) 통합

-   **상호작용**: 샌드박스 작업 요청 메시지(`Message`)의 감정 분석 결과(`EmotionAnalysis`)를 바탕으로 `EmotionalIntelligenceService`는 샌드박스 실행 전 경고나 안내 메시지의 톤을 조절할 수 있습니다. 샌드박스 작업 실패 시 공감적인 응답을 생성하는 데 활용될 수 있습니다.
-   **데이터 교환**: 사용자 메시지, 감정 분석 결과 (`EmotionAnalysis`), 생성된 응답 텍스트.
-   **객체/인터페이스**: `EmotionalIntelligenceService`, `SandboxService`, `Message`, `EmotionAnalysis`, `EmotionalResponseStrategy`.
-   **시나리오**: 사용자가 좌절감을 표현하며 샌드박스 작업 재시도를 요청할 경우(`EmotionAnalysis`), `EmotionalIntelligenceService`는 공감적인 응답(`EmotionalResponseStrategy`)을 생성하고, `SandboxService`는 주의 깊게 작업을 처리합니다.

### 7.9 적응형 학습 (Adaptive Learning) 통합

-   **상호작용**: `AdaptiveLearningService`는 사용자의 샌드박스 사용 패턴(자주 사용하는 언어, 성공률 등)을 `UserProfile`에 기록하고, 이를 바탕으로 샌드박스 환경 설정(예: 기본 언어 버전, 리소스 할당량)이나 관련 도구 추천을 개인화합니다.
-   **데이터 교환**: `ToolExecution` 기록, `UserProfile` 데이터, `LearningPreference`, 개인화된 설정/추천.
-   **객체/인터페이스**: `AdaptiveLearningService`, `SandboxService`, `UserProfile`, `ToolExecution`, `LearningPreference`.
-   **시나리오**: 사용자가 주로 파이썬 3.11 버전의 샌드박스 작업을 성공적으로 수행하는 것을 `AdaptiveLearningService`가 학습하면, 이후 파이썬 관련 작업 요청 시 기본 샌드박스 환경으로 파이썬 3.11을 우선 제안할 수 있습니다.

### 7.10 강화 학습 (Reinforcement Learning) 통합

-   **상호작용**: 샌드박스는 강화 학습 에이전트(`Agent`)가 상호작용하는 환경(`Environment`) 역할을 할 수 있습니다. 에이전트는 샌드박스 내에서 특정 목표(예: 코드 최적화, 작업 성공률 최대화)를 달성하기 위해 행동(`Action`, 예: 코드 수정, 파라미터 조정)을 수행하고, 결과에 따라 보상(`RewardSignal`)을 받습니다. `RLPolicy`는 샌드박스 상태(`State`)를 기반으로 최적의 행동을 결정합니다.
-   **데이터 교환**: 샌드박스 상태 정보, 에이전트 행동, 실행 결과 (`ToolExecution`), 보상 신호 (`RewardSignal`), 학습된 정책 (`RLPolicy`).
-   **객체/인터페이스**: `SandboxService` (Environment 역할), `RLService` (Agent 역할), `RLAgentState`, `RewardSignal`, `RLPolicy`, `ToolExecution`.
-   **시나리오**: 코드 실행 시간을 단축하는 목표를 가진 RL 에이전트는 샌드박스 내에서 코드를 반복 실행하며 다양한 최적화 기법(행동)을 시도하고, 실행 시간(결과)에 따라 보상을 받아 정책을 업데이트합니다.

### 7.11 영역 간 지식 전이 (Cross-domain Knowledge Transfer) 통합

-   **상호작용**: 샌드박스에서 특정 언어(예: Python)로 작성된 성공적인 스크립트나 문제 해결 패턴(`KnowledgeItem`)은 `KnowledgeService`에 의해 분석되어 다른 언어(예: JavaScript)나 유사한 문제(`KnowledgeMapping`)에 적용될 수 있습니다. 샌드박스 환경 자체를 특정 영역의 지식을 테스트하거나 전이시키는 실험 공간(`TransferLearningTask`)으로 활용할 수 있습니다.
-   **데이터 교환**: 성공적인 샌드박스 실행 결과 (`ToolExecution`), 저장된 지식 (`KnowledgeItem`), 지식 매핑 정보 (`KnowledgeMapping`), 전이 학습 작업 정보 (`TransferLearningTask`).
-   **객체/인터페이스**: `KnowledgeService`, `SandboxService`, `ToolExecution`, `KnowledgeItem`, `KnowledgeMapping`, `TransferLearningTask`.
-   **시나리오**: 샌드박스에서 파이썬으로 구현된 데이터 정규화 로직(`KnowledgeItem`)이 성공적이었다면, `KnowledgeService`는 이 로직을 자바스크립트로 변환하여 유사한 웹 기반 데이터 처리 작업에 적용하도록 제안할 수 있습니다.

## 8. 추가 고려 사항

-   **트랜잭션 관리**: 서비스 계층에서 `@Transactional` 어노테이션을 사용하여 트랜잭션을 관리합니다. 읽기 전용 작업에는 `readOnly = true` 옵션을 사용합니다.
-   **동시성 제어**: 여러 사용자가 동시에 시스템을 사용할 경우 발생할 수 있는 동시성 문제를 고려하여 낙관적 락(Optimistic Lock) 또는 비관적 락(Pessimistic Lock) 적용을 검토합니다. 특히 `Sandbox` 상태 변경 등에 주의합니다.
-   **성능 최적화**: N+1 문제 방지를 위해 Fetch Join을 적절히 사용하고, 필요한 경우 인덱스를 추가합니다. 대용량 데이터 처리 시에는 배치 처리나 비동기 처리를 고려합니다.
-   **로깅**: 주요 서비스 메서드 호출, 예외 발생, 샌드박스 작업 등 중요한 이벤트에 대해 로그를 기록합니다.
-   **보안**: Spring Security를 사용하여 인증 및 인가를 처리하고, DTO 변환 시 민감 정보를 필터링합니다. 샌드박스 보안 정책을 철저히 적용합니다.

이 객체 모델 설계는 시스템 개발의 기반을 제공하며, 실제 구현 과정에서 요구사항 변경이나 기술적 제약에 따라 수정될 수 있습니다.

