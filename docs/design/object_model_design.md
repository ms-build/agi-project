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
    private String version;

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

### 4.14 창의적 생성 도메인 (Creative Generation Domain)

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

### 4.15 샌드박스 도메인 (Sandbox Domain)

-   **Entity**: `Sandbox`, `SandboxTemplate`, `SandboxResource`, `SandboxSecurity`, `SandboxFile`, `SandboxPort`, `CodeExecution`, `SandboxLog`
-   **Repository**: `SandboxRepository`, `SandboxTemplateRepository`, `SandboxResourceRepository`, `SandboxSecurityRepository`, `SandboxFileRepository`, `SandboxPortRepository`, `CodeExecutionRepository`, `SandboxLogRepository`
-   **Service**: `SandboxService`, `SandboxFileService`, `CodeExecutionService`, `SandboxMonitoringService`
-   **Controller**: `SandboxController`, `SandboxFileController`, `CodeExecutionController`
-   **DTO**: `SandboxDto`, `SandboxTemplateDto`, `SandboxResourceDto`, `SandboxSecurityDto`, `SandboxFileDto`, `SandboxPortDto`, `CodeExecutionDto`, `SandboxCreateRequest`, `SandboxResponse`, `CodeExecutionRequest`, `CodeExecutionResponse`

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private SandboxTemplate template;

    @Column(nullable = false, length = 100)
    private String name;

    @Lob
    private String description;

    @Column(nullable = false, length = 50)
    private String imageName; // Docker 이미지 이름

    @Column(nullable = false, length = 50)
    private String imageTag; // Docker 이미지 태그

    @Column(length = 100) // Nullable 허용
    private String containerId; // Docker 컨테이너 ID

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SandboxStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime startedAt;

    private LocalDateTime lastActiveAt;

    private LocalDateTime expiresAt;

    @OneToOne(mappedBy = "sandbox", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private SandboxResource resource;

    @OneToOne(mappedBy = "sandbox", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private SandboxSecurity security;

    @OneToMany(mappedBy = "sandbox", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SandboxFile> files = new ArrayList<>();

    @OneToMany(mappedBy = "sandbox", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SandboxPort> ports = new ArrayList<>();

    @OneToMany(mappedBy = "sandbox", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CodeExecution> codeExecutions = new ArrayList<>();

    @OneToMany(mappedBy = "sandbox", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SandboxLog> logs = new ArrayList<>();

    @Builder
    public Sandbox(User user, SandboxTemplate template, String name, String description, String imageName, String imageTag, LocalDateTime expiresAt) {
        this.id = UUID.randomUUID().toString();
        this.user = user;
        this.template = template;
        this.name = name;
        this.description = description;
        this.imageName = imageName;
        this.imageTag = imageTag;
        this.status = SandboxStatus.CREATED;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = expiresAt;
    }

    // 상태 변경 및 컨테이너 ID 설정 메서드
    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public void start(String containerId) {
        if (this.status == SandboxStatus.CREATED || this.status == SandboxStatus.STOPPED) {
            this.status = SandboxStatus.RUNNING;
            this.containerId = containerId;
            this.startedAt = LocalDateTime.now();
            this.lastActiveAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Cannot start sandbox in " + this.status + " state");
        }
    }

    public void stop() {
        if (this.status == SandboxStatus.RUNNING || this.status == SandboxStatus.PAUSED) {
            this.status = SandboxStatus.STOPPED;
            this.containerId = null; // 컨테이너 ID 제거
            this.lastActiveAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Cannot stop sandbox in " + this.status + " state");
        }
    }

    public void pause() {
        if (this.status == SandboxStatus.RUNNING) {
            this.status = SandboxStatus.PAUSED;
            this.lastActiveAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Cannot pause sandbox in " + this.status + " state");
        }
    }

    public void updateLastActive() {
        this.lastActiveAt = LocalDateTime.now();
    }

    public void extendExpiration(Duration duration) {
        if (this.expiresAt != null) {
            this.expiresAt = this.expiresAt.plus(duration);
        } else {
            this.expiresAt = LocalDateTime.now().plus(duration);
        }
    }
}

public enum SandboxStatus {
    CREATED, RUNNING, PAUSED, STOPPED, ERROR, DELETED
}

// Entity 예시: SandboxResource.java
@Entity
@Table(name = "sandbox_resource")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SandboxResource {
    @Id
    private String sandboxId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "sandbox_id")
    private Sandbox sandbox;

    @Column(nullable = false)
    private Integer cpuLimit; // CPU 코어 수 제한 (예: 1000 = 1 core)

    @Column(nullable = false)
    private Integer memoryLimitMb; // 메모리 제한 (MB)

    @Column(nullable = false)
    private Integer diskLimitMb; // 디스크 제한 (MB)

    @Column(nullable = false)
    private Integer networkLimitKbps; // 네트워크 대역폭 제한 (Kbps)

    @Column(nullable = false)
    private Integer timeoutSeconds; // 최대 실행 시간 (초)

    // 실시간 사용량 필드 (DB 저장 X, 모니터링용)
    @Transient
    private Double cpuUsagePercent;
    @Transient
    private Integer memoryUsageMb;
    @Transient
    private Integer diskUsageMb;

    @Builder
    public SandboxResource(Sandbox sandbox, Integer cpuLimit, Integer memoryLimitMb, Integer diskLimitMb, Integer networkLimitKbps, Integer timeoutSeconds) {
        this.sandbox = sandbox;
        this.sandboxId = sandbox.getId();
        this.cpuLimit = cpuLimit;
        this.memoryLimitMb = memoryLimitMb;
        this.diskLimitMb = diskLimitMb;
        this.networkLimitKbps = networkLimitKbps;
        this.timeoutSeconds = timeoutSeconds;
    }
}

// Entity 예시: CodeExecution.java
@Entity
@Table(name = "code_execution")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CodeExecution {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sandbox_id", nullable = false)
    private Sandbox sandbox;

    @Column(nullable = false, length = 50)
    private String language;

    @Lob
    @Column(nullable = false)
    private String code;

    @Column(length = 255)
    private String workingDirectory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExecutionStatus status;

    private Integer exitCode;

    @Lob
    private String stdout;

    @Lob
    private String stderr;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    private Double cpuTimeSeconds; // CPU 사용 시간
    private Long memoryPeakBytes; // 최대 메모리 사용량

    @Builder
    public CodeExecution(Sandbox sandbox, String language, String code, String workingDirectory) {
        this.id = UUID.randomUUID().toString();
        this.sandbox = sandbox;
        this.language = language;
        this.code = code;
        this.workingDirectory = workingDirectory;
        this.status = ExecutionStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    // 상태 및 결과 업데이트 메서드
    public void start() {
        this.status = ExecutionStatus.RUNNING;
        this.startedAt = LocalDateTime.now();
    }

    public void complete(int exitCode, String stdout, String stderr, Double cpuTimeSeconds, Long memoryPeakBytes) {
        this.status = ExecutionStatus.COMPLETED;
        this.exitCode = exitCode;
        this.stdout = stdout;
        this.stderr = stderr;
        this.cpuTimeSeconds = cpuTimeSeconds;
        this.memoryPeakBytes = memoryPeakBytes;
        this.completedAt = LocalDateTime.now();
    }

    public void fail(String stderr) {
        this.status = ExecutionStatus.FAILED;
        this.stderr = stderr;
        this.completedAt = LocalDateTime.now();
    }
}

// DTO 예시: SandboxDto.java
@Getter
@Builder
public class SandboxDto {
    private String id;
    private String userId;
    private String templateId; // Optional
    private String name;
    private String description;
    private String imageName;
    private String imageTag;
    private String containerId; // Nullable
    private SandboxStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime lastActiveAt;
    private LocalDateTime expiresAt;
    private SandboxResourceDto resources; // 포함
    private SandboxSecurityDto security; // 포함

    public static SandboxDto fromEntity(Sandbox sandbox) {
        return SandboxDto.builder()
                .id(sandbox.getId())
                .userId(sandbox.getUser().getId().toString())
                .templateId(sandbox.getTemplate() != null ? sandbox.getTemplate().getId() : null)
                .name(sandbox.getName())
                .description(sandbox.getDescription())
                .imageName(sandbox.getImageName())
                .imageTag(sandbox.getImageTag())
                .containerId(sandbox.getContainerId())
                .status(sandbox.getStatus())
                .createdAt(sandbox.getCreatedAt())
                .startedAt(sandbox.getStartedAt())
                .lastActiveAt(sandbox.getLastActiveAt())
                .expiresAt(sandbox.getExpiresAt())
                .resources(sandbox.getResource() != null ? SandboxResourceDto.fromEntity(sandbox.getResource()) : null)
                .security(sandbox.getSecurity() != null ? SandboxSecurityDto.fromEntity(sandbox.getSecurity()) : null)
                .build();
    }
}

// DTO 예시: SandboxResourceDto.java
@Getter
@Builder
public class SandboxResourceDto {
    private Integer cpuLimit;
    private Integer memoryLimitMb;
    private Integer diskLimitMb;
    private Integer networkLimitKbps;
    private Integer timeoutSeconds;
    // 실시간 사용량 필드 (Service에서 채워서 전달)
    private Double cpuUsagePercent;
    private Integer memoryUsageMb;
    private Integer diskUsageMb;

    public static SandboxResourceDto fromEntity(SandboxResource resource) {
        return SandboxResourceDto.builder()
                .cpuLimit(resource.getCpuLimit())
                .memoryLimitMb(resource.getMemoryLimitMb())
                .diskLimitMb(resource.getDiskLimitMb())
                .networkLimitKbps(resource.getNetworkLimitKbps())
                .timeoutSeconds(resource.getTimeoutSeconds())
                .build();
    }
}

// DTO 예시: CodeExecutionDto.java
@Getter
@Builder
public class CodeExecutionDto {
    private String id;
    private String sandboxId;
    private String language;
    private ExecutionStatus status;
    private Integer exitCode;
    private String stdout;
    private String stderr;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private Double cpuTimeSeconds;
    private Long memoryPeakBytes;

    public static CodeExecutionDto fromEntity(CodeExecution execution) {
        return CodeExecutionDto.builder()
                .id(execution.getId())
                .sandboxId(execution.getSandbox().getId())
                .language(execution.getLanguage())
                .status(execution.getStatus())
                .exitCode(execution.getExitCode())
                .stdout(execution.getStdout())
                .stderr(execution.getStderr())
                .createdAt(execution.getCreatedAt())
                .startedAt(execution.getStartedAt())
                .completedAt(execution.getCompletedAt())
                .cpuTimeSeconds(execution.getCpuTimeSeconds())
                .memoryPeakBytes(execution.getMemoryPeakBytes())
                .build();
    }
}
```

## 5. 핵심 서비스 및 모듈 인터페이스

-   **AuthService**: 사용자 인증, JWT 토큰 관리
-   **UserService**: 사용자 정보 관리, 역할 및 권한 관리
-   **ConversationService**: 대화 생성, 메시지 저장, 대화 기록 조회
-   **NlpService**: 자연어 처리 요청 처리 (분석, 생성, 번역 등)
-   **ToolService**: 도구 정보 조회, 도구 등록 관리
-   **ToolExecutorService**: 도구 실행 요청 처리, 결과 저장
-   **PlanService**: 계획 생성, 수정, 삭제, 조회
-   **PlanExecutionService**: 계획 실행 제어, 단계별 상태 관리
-   **KnowledgeService**: 지식 저장, 검색, 태그 관리
-   **MemoryService**: 메모리 기록 저장, 조회, 회상
-   **MediaProcessingService**: 이미지, 오디오, 비디오 처리 요청
-   **LearningService**: 사용자 피드백 처리, 모델 학습 트리거
-   **ModelManagementService**: 학습된 모델 버전 관리, 활성화
-   **ExplainabilityService**: 설명 생성 요청 처리
-   **EmotionalIntelligenceService**: 감정 분석, 감성적 응답 생성
-   **AdaptiveLearningService**: 사용자 프로필 관리, 개인화 및 적응 처리
-   **RLService**: 강화 학습 에이전트 관리, 학습 및 정책 실행
-   **KnowledgeTransferService**: 영역 간 지식 전이 및 융합 처리
-   **CreativeGenerationService**: 창의적 콘텐츠 생성 요청 처리
-   **SandboxService**: 샌드박스 생성, 상태 관리, 자원 및 보안 설정
-   **SandboxFileService**: 샌드박스 내 파일 관리 (CRUD, 업로드/다운로드)
-   **CodeExecutionService**: 샌드박스 내 코드 실행 요청 처리
-   **SandboxMonitoringService**: 샌드박스 자원 사용량 모니터링

## 6. DTO 변환 전략

-   **Controller**: Request DTO를 받아 Service에 전달하고, Service로부터 받은 DTO 또는 Entity를 Response DTO로 변환하여 반환합니다.
-   **Service**: Controller로부터 받은 Request DTO를 Entity로 변환하여 Repository에 전달하거나, Repository로부터 받은 Entity를 DTO로 변환하여 Controller에 반환합니다.
-   **변환 로직**: DTO 내부에 `fromEntity()` 정적 메서드를 두거나, 별도의 Mapper 클래스(예: MapStruct 사용)를 활용하여 변환 로직을 구현합니다.
-   **주의**: Entity를 Controller 계층까지 직접 전달하지 않도록 주의합니다.

## 7. 결론

이 객체 모델 설계는 통합 AGI 시스템의 복잡한 도메인을 효과적으로 관리하고, 계층 간 역할을 명확히 분리하여 유지보수성과 확장성을 높이는 것을 목표로 합니다. JPA 엔티티와 DTO를 분리하고, 서비스 인터페이스를 통해 비즈니스 로직을 캡슐화하며, 샌드박스 환경을 위한 객체 모델을 포함하여 안전하고 격리된 실행 환경을 지원합니다. 이 설계를 기반으로 각 모듈의 상세 구현을 진행할 수 있습니다.
