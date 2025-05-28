# 통합 AGI 시스템 설계 문서 교차 검증 보고서

## 1. 개요

이 문서는 통합 AGI 시스템의 5개 설계 문서 간의 교차 검증 결과를 제공합니다. 검증 대상 문서는 다음과 같습니다:

1. 시스템 아키텍처 설계 (`system_architecture.md`)
2. 데이터베이스 스키마 설계 (`database_schema.md`)
3. API 설계 (`api_design.md`)
4. 객체 모델 설계 (`object_model_design.md`)
5. 설계 검증 보고서 (`design_validation.md`)

이 교차 검증의 목적은 각 설계 문서 간의 일관성, 정합성, 완전성을 확인하고, 특히 ERD, 테이블, 컬럼명, 엔티티, 필드명 등의 정확한 매핑을 검증하는 것입니다.

## 2. 교차 검증 방법론

교차 검증은 다음과 같은 방법으로 수행되었습니다:

1. **구조적 매핑**: 각 도메인별로 테이블-엔티티, 컬럼-필드, API-DTO 간의 매핑을 표로 정리
2. **코드 예시 검증**: 각 문서에 포함된 코드 예시의 일관성 검증
3. **용어 일관성**: 모든 문서에서 사용된 용어의 일관성 검증
4. **누락 항목 식별**: 한 문서에는 있지만 다른 문서에는 누락된 항목 식별
5. **불일치 항목 식별**: 문서 간 정의가 불일치하는 항목 식별

## 3. 주요 도메인별 교차 검증 결과

### 3.1 사용자 도메인 (User Domain)

#### 3.1.1 테이블-엔티티-API 매핑

| 데이터베이스 스키마 | 객체 모델 설계 | API 설계 | 일관성 |
|-------------------|--------------|---------|-------|
| USER 테이블 | User 엔티티 | /api/users, /api/auth | ✅ 일관성 유지 |
| USER.id (PK) | User.id | userId 파라미터 | ✅ 일관성 유지 |
| USER.username | User.username | username 필드 | ✅ 일관성 유지 |
| USER.email | User.email | email 필드 | ✅ 일관성 유지 |
| USER.password_hash | User.passwordHash | password 필드(요청만) | ✅ 일관성 유지 |
| USER.role | User.role | role 필드 | ✅ 일관성 유지 |
| USER.created_at | User.createdAt | createdAt 필드 | ✅ 일관성 유지 |
| USER.last_login_at | User.lastLoginAt | lastLoginAt 필드 | ✅ 일관성 유지 |
| USER_ROLE 테이블 | Role 열거형 | role 필드 | ✅ 일관성 유지 |
| USER_PERMISSION 테이블 | Permission 열거형 | 권한 검증 로직 | ✅ 일관성 유지 |

#### 3.1.2 코드 예시 검증

**객체 모델 설계의 User 엔티티 코드:**
```java
@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    private String id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime lastLoginAt;

    @Builder
    public User(String username, String email, String passwordHash, Role role) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }
}
```

**API 설계의 UserResponse DTO:**
```json
{
  "id": "uuid-user-123",
  "username": "john_doe",
  "email": "john@example.com",
  "role": "USER",
  "createdAt": "2025-05-28T10:00:00Z",
  "lastLoginAt": "2025-05-28T14:00:00Z"
}
```

**데이터베이스 스키마의 USER 테이블:**
```sql
CREATE TABLE user (
    id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    last_login_at TIMESTAMP
);
```

#### 3.1.3 일관성 검증 결과

사용자 도메인의 테이블, 엔티티, API 간의 매핑은 일관성이 잘 유지되고 있습니다. 컬럼명과 필드명이 명명 규칙(DB는 snake_case, Java는 camelCase)에 맞게 일관되게 사용되고 있으며, API 응답 필드도 이와 일치합니다.

### 3.2 대화 도메인 (Conversation Domain)

#### 3.2.1 테이블-엔티티-API 매핑

| 데이터베이스 스키마 | 객체 모델 설계 | API 설계 | 일관성 |
|-------------------|--------------|---------|-------|
| CONVERSATION 테이블 | Conversation 엔티티 | /api/nlp/conversation | ✅ 일관성 유지 |
| CONVERSATION.id (PK) | Conversation.id | conversationId 파라미터 | ✅ 일관성 유지 |
| CONVERSATION.user_id (FK) | Conversation.user | userId 필드 | ✅ 일관성 유지 |
| CONVERSATION.title | Conversation.title | title 필드 | ✅ 일관성 유지 |
| CONVERSATION.created_at | Conversation.createdAt | timestamp 필드 | ✅ 일관성 유지 |
| CONVERSATION.updated_at | Conversation.updatedAt | - (응답에 없음) | ⚠️ API 응답에 누락 |
| MESSAGE 테이블 | Message 엔티티 | ConversationResponse 내 메시지 | ✅ 일관성 유지 |
| MESSAGE.id (PK) | Message.id | messageId 필드 | ✅ 일관성 유지 |
| MESSAGE.conversation_id (FK) | Message.conversation | conversationId 필드 | ✅ 일관성 유지 |
| MESSAGE.role | Message.role | - (응답에 없음) | ⚠️ API 응답에 누락 |
| MESSAGE.content | Message.content | text 필드 | ⚠️ 필드명 불일치 (content vs text) |
| MESSAGE.timestamp | Message.timestamp | timestamp 필드 | ✅ 일관성 유지 |
| MESSAGE.metadata | Message.metadata | metadata 필드 | ✅ 일관성 유지 |

#### 3.2.2 코드 예시 검증

**객체 모델 설계의 Conversation 및 Message 엔티티 코드:**
```java
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

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Conversation(User user, String title) {
        this.id = UUID.randomUUID().toString();
        this.user = user;
        this.title = title;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}

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

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private MessageRole role;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> metadata;

    @Builder
    public Message(Conversation conversation, MessageRole role, String content, Map<String, Object> metadata) {
        this.id = UUID.randomUUID().toString();
        this.conversation = conversation;
        this.role = role;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.metadata = metadata;
    }
}
```

**API 설계의 ConversationResponse DTO:**
```json
{
  "conversationId": "uuid-conv-456",
  "messageId": "uuid-msg-789",
  "text": "오늘 서울 날씨는 맑고 최고 기온은 25도입니다.",
  "timestamp": "2025-05-28T14:05:00Z",
  "metadata": {
    "intent": "날씨 질문",
    "entities": [{"type": "location", "value": "서울"}]
  }
}
```

**데이터베이스 스키마의 CONVERSATION 및 MESSAGE 테이블:**
```sql
CREATE TABLE conversation (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    title VARCHAR(200),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE message (
    id VARCHAR(36) PRIMARY KEY,
    conversation_id VARCHAR(36) NOT NULL,
    role VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    metadata JSON,
    FOREIGN KEY (conversation_id) REFERENCES conversation(id)
);
```

#### 3.2.3 일관성 검증 결과

대화 도메인에서 몇 가지 불일치 사항이 발견되었습니다:
1. `CONVERSATION.updated_at` 필드가 API 응답에 포함되지 않음
2. `MESSAGE.role` 필드가 API 응답에 포함되지 않음
3. `MESSAGE.content`와 API의 `text` 필드 간 명칭 불일치

이러한 불일치는 API 응답 구조를 수정하거나, DTO 변환 과정에서 적절히 매핑하여 해결할 수 있습니다.

### 3.3 도구 도메인 (Tool Domain)

#### 3.3.1 테이블-엔티티-API 매핑

| 데이터베이스 스키마 | 객체 모델 설계 | API 설계 | 일관성 |
|-------------------|--------------|---------|-------|
| TOOL 테이블 | Tool 엔티티 | /api/tools | ✅ 일관성 유지 |
| TOOL.id (PK) | Tool.id | toolId 파라미터 | ✅ 일관성 유지 |
| TOOL.name | Tool.name | toolName 필드 | ✅ 일관성 유지 |
| TOOL.description | Tool.description | description 필드 | ✅ 일관성 유지 |
| TOOL.category | Tool.category | category 필드 | ✅ 일관성 유지 |
| TOOL.execution_type | Tool.executionType | executionType 필드 | ✅ 일관성 유지 |
| TOOL.parameters_schema | Tool.parametersSchema | parametersSchema 필드 | ✅ 일관성 유지 |
| TOOL.created_at | Tool.createdAt | createdAt 필드 | ✅ 일관성 유지 |
| TOOL_EXECUTION 테이블 | ToolExecution 엔티티 | /api/tools/execute | ✅ 일관성 유지 |
| TOOL_EXECUTION.id (PK) | ToolExecution.id | executionId 필드 | ✅ 일관성 유지 |
| TOOL_EXECUTION.tool_id (FK) | ToolExecution.tool | toolId 필드 | ✅ 일관성 유지 |
| TOOL_EXECUTION.user_id (FK) | ToolExecution.user | userId 필드 | ✅ 일관성 유지 |
| TOOL_EXECUTION.parameters | ToolExecution.parameters | parameters 필드 | ✅ 일관성 유지 |
| TOOL_EXECUTION.result | ToolExecution.result | result 필드 | ✅ 일관성 유지 |
| TOOL_EXECUTION.status | ToolExecution.status | status 필드 | ✅ 일관성 유지 |
| TOOL_EXECUTION.started_at | ToolExecution.startedAt | - (응답에 없음) | ⚠️ API 응답에 누락 |
| TOOL_EXECUTION.completed_at | ToolExecution.completedAt | completedAt 필드 | ✅ 일관성 유지 |
| TOOL_EXECUTION.sandbox_id (FK) | ToolExecution.sandbox | sandboxId 필드 | ✅ 일관성 유지 |

#### 3.3.2 코드 예시 검증

**객체 모델 설계의 Tool 및 ToolExecution 엔티티 코드:**
```java
@Entity
@Table(name = "tool")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tool {
    @Id
    private String id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 50)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExecutionType executionType;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> parametersSchema;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Tool(String name, String description, String category, ExecutionType executionType, Map<String, Object> parametersSchema) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.category = category;
        this.executionType = executionType;
        this.parametersSchema = parametersSchema;
        this.createdAt = LocalDateTime.now();
    }
}

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

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> parameters;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> result;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExecutionStatus status;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sandbox_id")
    private Sandbox sandbox;

    @Builder
    public ToolExecution(Tool tool, User user, Map<String, Object> parameters, Sandbox sandbox) {
        this.id = UUID.randomUUID().toString();
        this.tool = tool;
        this.user = user;
        this.parameters = parameters;
        this.status = ExecutionStatus.PENDING;
        this.startedAt = LocalDateTime.now();
        this.sandbox = sandbox;
    }
}
```

**API 설계의 ToolExecutionResponse DTO:**
```json
{
  "executionId": "uuid-exec-abc",
  "status": "completed",
  "result": [
    {"title": "...", "url": "...", "snippet": "..."},
    ...
  ],
  "completedAt": "2025-05-28T14:10:00Z"
}
```

**데이터베이스 스키마의 TOOL 및 TOOL_EXECUTION 테이블:**
```sql
CREATE TABLE tool (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    category VARCHAR(50) NOT NULL,
    execution_type VARCHAR(20) NOT NULL,
    parameters_schema JSON,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE tool_execution (
    id VARCHAR(36) PRIMARY KEY,
    tool_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    parameters JSON,
    result JSON,
    status VARCHAR(20) NOT NULL,
    started_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    sandbox_id VARCHAR(36),
    FOREIGN KEY (tool_id) REFERENCES tool(id),
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (sandbox_id) REFERENCES sandbox(id)
);
```

#### 3.3.3 일관성 검증 결과

도구 도메인에서 한 가지 불일치 사항이 발견되었습니다:
1. `TOOL_EXECUTION.started_at` 필드가 API 응답에 포함되지 않음

이는 API 응답 구조를 수정하여 해결할 수 있습니다. 그 외에는 테이블, 엔티티, API 간의 매핑이 일관성 있게 유지되고 있습니다.

### 3.4 샌드박스 도메인 (Sandbox Domain)

#### 3.4.1 테이블-엔티티-API 매핑

| 데이터베이스 스키마 | 객체 모델 설계 | API 설계 | 일관성 |
|-------------------|--------------|---------|-------|
| SANDBOX 테이블 | Sandbox 엔티티 | /api/sandbox | ✅ 일관성 유지 |
| SANDBOX.id (PK) | Sandbox.id | id 파라미터 | ✅ 일관성 유지 |
| SANDBOX.user_id (FK) | Sandbox.user | userId 필드 | ✅ 일관성 유지 |
| SANDBOX.template_id (FK) | Sandbox.template | templateId 필드 | ✅ 일관성 유지 |
| SANDBOX.name | Sandbox.name | name 필드 | ✅ 일관성 유지 |
| SANDBOX.description | Sandbox.description | description 필드 | ✅ 일관성 유지 |
| SANDBOX.image_name | Sandbox.imageName | imageName 필드 | ✅ 일관성 유지 |
| SANDBOX.image_tag | Sandbox.imageTag | imageTag 필드 | ✅ 일관성 유지 |
| SANDBOX.container_id | Sandbox.containerId | - (응답에 없음) | ⚠️ API 응답에 누락 |
| SANDBOX.status | Sandbox.status | status 필드 | ✅ 일관성 유지 |
| SANDBOX.created_at | Sandbox.createdAt | createdAt 필드 | ✅ 일관성 유지 |
| SANDBOX.started_at | Sandbox.startedAt | startedAt 필드 | ✅ 일관성 유지 |
| SANDBOX.last_active_at | Sandbox.lastActiveAt | lastActive 필드 | ⚠️ 필드명 불일치 (lastActiveAt vs lastActive) |
| SANDBOX.expires_at | Sandbox.expiresAt | expiresAt 필드 | ✅ 일관성 유지 |
| SANDBOX_RESOURCE 테이블 | SandboxResource 엔티티 | resources 객체 | ✅ 일관성 유지 |
| SANDBOX_RESOURCE.sandbox_id (PK, FK) | SandboxResource.sandboxId | - (중첩 객체) | ✅ 일관성 유지 |
| SANDBOX_RESOURCE.cpu_limit | SandboxResource.cpuLimit | cpuLimit 필드 | ✅ 일관성 유지 |
| SANDBOX_RESOURCE.memory_limit | SandboxResource.memoryLimit | memoryLimit 필드 | ✅ 일관성 유지 |
| SANDBOX_RESOURCE.disk_limit | SandboxResource.diskLimit | diskLimit 필드 | ✅ 일관성 유지 |
| SANDBOX_RESOURCE.network_limit | SandboxResource.networkLimit | networkLimit 필드 | ✅ 일관성 유지 |
| SANDBOX_RESOURCE.timeout | SandboxResource.timeout | timeout 필드 | ✅ 일관성 유지 |
| SANDBOX_RESOURCE.cpu_usage | SandboxResource.cpuUsage | cpuUsage 필드 | ✅ 일관성 유지 |
| SANDBOX_RESOURCE.memory_usage | SandboxResource.memoryUsage | memoryUsage 필드 | ✅ 일관성 유지 |
| SANDBOX_RESOURCE.disk_usage | SandboxResource.diskUsage | diskUsage 필드 | ✅ 일관성 유지 |
| SANDBOX_SECURITY 테이블 | SandboxSecurity 엔티티 | security 객체 | ✅ 일관성 유지 |
| CODE_EXECUTION 테이블 | CodeExecution 엔티티 | /api/sandbox/{sandboxId}/code/execute | ✅ 일관성 유지 |
| SANDBOX_FILE 테이블 | SandboxFile 엔티티 | /api/sandbox/{sandboxId}/files | ✅ 일관성 유지 |
| SANDBOX_PORT 테이블 | SandboxPort 엔티티 | /api/sandbox/{sandboxId}/ports | ✅ 일관성 유지 |

#### 3.4.2 코드 예시 검증

**객체 모델 설계의 Sandbox 엔티티 코드:**
```java
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
    private String imageName;

    @Column(nullable = false, length = 50)
    private String imageTag;

    @Column(nullable = false, length = 50)
    private String containerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SandboxStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime startedAt;
    
    private LocalDateTime lastActiveAt;
    
    private LocalDateTime expiresAt;

    @OneToOne(mappedBy = "sandbox", cascade = CascadeType.ALL, orphanRemoval = true)
    private SandboxResource resource;

    @OneToOne(mappedBy = "sandbox", cascade = CascadeType.ALL, orphanRemoval = true)
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
    public Sandbox(User user, SandboxTemplate template, String name, String description, String imageName, String imageTag, String containerId, LocalDateTime expiresAt) {
        this.id = UUID.randomUUID().toString();
        this.user = user;
        this.template = template;
        this.name = name;
        this.description = description;
        this.imageName = imageName;
        this.imageTag = imageTag;
        this.containerId = containerId;
        this.status = SandboxStatus.CREATED;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = expiresAt;
    }

    // 상태 변경 메서드
    public void start() {
        if (this.status == SandboxStatus.CREATED || this.status == SandboxStatus.STOPPED) {
            this.status = SandboxStatus.RUNNING;
            this.startedAt = LocalDateTime.now();
            this.lastActiveAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Cannot start sandbox in " + this.status + " state");
        }
    }

    // 기타 메서드...
}
```

**API 설계의 SandboxResponse DTO:**
```json
{
  "id": "uuid-sandbox-123",
  "userId": "uuid-user-456",
  "name": "Python 개발 환경",
  "description": "Python 코드 개발 및 실행을 위한 샌드박스",
  "status": "running",
  "imageName": "python",
  "imageTag": "3.11-slim",
  "createdAt": "2025-05-28T13:00:00Z",
  "startedAt": "2025-05-28T13:01:00Z",
  "lastActive": "2025-05-28T14:15:00Z",
  "expiresAt": "2025-05-29T13:00:00Z",
  "resources": {
    "cpuLimit": 2,
    "memoryLimit": 2048,
    "diskLimit": 10240,
    "networkLimit": 1024,
    "timeout": 3600,
    "cpuUsage": 0.5,
    "memoryUsage": 512,
    "diskUsage": 1024
  },
  "workspace": {
    "rootPath": "/workspace",
    "sizeBytes": 1024000
  }
}
```

**데이터베이스 스키마의 SANDBOX 테이블:**
```sql
CREATE TABLE sandbox (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    template_id VARCHAR(36),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    image_name VARCHAR(50) NOT NULL,
    image_tag VARCHAR(50) NOT NULL,
    container_id VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    started_at TIMESTAMP,
    last_active_at TIMESTAMP,
    expires_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (template_id) REFERENCES sandbox_template(id)
);

CREATE TABLE sandbox_resource (
    sandbox_id VARCHAR(36) PRIMARY KEY,
    cpu_limit INT NOT NULL,
    memory_limit INT NOT NULL,
    disk_limit INT NOT NULL,
    network_limit INT NOT NULL,
    timeout INT NOT NULL,
    cpu_usage FLOAT,
    memory_usage INT,
    disk_usage INT,
    FOREIGN KEY (sandbox_id) REFERENCES sandbox(id)
);
```

#### 3.4.3 일관성 검증 결과

샌드박스 도메인에서 몇 가지 불일치 사항이 발견되었습니다:
1. `SANDBOX.container_id` 필드가 API 응답에 포함되지 않음
2. `SANDBOX.last_active_at`와 API의 `lastActive` 필드 간 명칭 불일치
3. API 응답에 `workspace` 객체가 있지만, 데이터베이스 스키마나 엔티티에는 명확히 정의되지 않음

이러한 불일치는 API 응답 구조를 수정하거나, DTO 변환 과정에서 적절히 매핑하여 해결할 수 있습니다.

## 4. 종합 검증 결과

### 4.1 일관성 유지 항목

1. **명명 규칙**: 대부분의 경우 데이터베이스는 snake_case, Java 엔티티는 camelCase, API는 camelCase를 일관되게 사용
2. **주요 도메인 구조**: 사용자, 대화, 도구, 계획, 지식, 샌드박스 등 주요 도메인의 기본 구조가 모든 문서에서 일관되게 유지됨
3. **관계 정의**: 엔티티 간 관계(1:1, 1:N, N:M)가 데이터베이스 스키마와 객체 모델에서 일관되게 정의됨
4. **API 경로 구조**: RESTful API 경로가 도메인 구조와 일관되게 정의됨

### 4.2 불일치 항목

1. **필드명 불일치**:
   - `MESSAGE.content`와 API의 `text` 필드
   - `SANDBOX.last_active_at`와 API의 `lastActive` 필드

2. **API 응답 누락 필드**:
   - `CONVERSATION.updated_at`
   - `MESSAGE.role`
   - `TOOL_EXECUTION.started_at`
   - `SANDBOX.container_id`

3. **추가 구조**:
   - API 응답의 `workspace` 객체가 데이터베이스 스키마나 엔티티에 명확히 정의되지 않음

### 4.3 개선 권장사항

1. **필드명 통일**:
   - API 응답의 필드명을 엔티티 필드명과 일치시키거나, 명확한 매핑 규칙 정의
   - 예: `MESSAGE.content`와 API의 `text` 필드를 통일

2. **누락 필드 추가**:
   - API 응답에 누락된 필드 추가 또는 의도적 생략 이유 문서화
   - 예: `TOOL_EXECUTION.started_at` 필드를 API 응답에 추가

3. **추가 구조 정의**:
   - API 응답에만 있는 구조(예: `workspace`)를 데이터베이스 스키마와 엔티티에도 정의
   - 또는 이러한 구조가 어떻게 생성되는지 명확히 문서화

4. **DTO 변환 로직 명확화**:
   - 엔티티-DTO 변환 과정에서의 필드 매핑 규칙 명확화
   - 특히 명칭이 다른 필드들의 매핑 방법 문서화

## 5. 결론

통합 AGI 시스템의 5개 설계 문서는 전반적으로 높은 수준의 일관성과 정합성을 유지하고 있습니다. 발견된 불일치 항목들은 대부분 API 응답 구조와 관련된 것으로, DTO 변환 과정에서 적절히 처리할 수 있는 수준입니다.

이 교차 검증 보고서를 바탕으로 설계 문서를 보완하고, 특히 API 응답 구조와 데이터베이스 스키마, 엔티티 간의 일관성을 높인다면, 더욱 견고하고 유지보수가 용이한 시스템을 구현할 수 있을 것입니다.

각 도메인별 상세 검증 결과는 이 보고서의 해당 섹션을 참조하시기 바랍니다. 특히 샌드박스 도메인은 시스템의 핵심 기능으로, 발견된 불일치 항목들을 우선적으로 해결하는 것이 좋겠습니다.
