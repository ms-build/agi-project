# 통합 AGI 시스템 데이터베이스 스키마 설계

## 1. 개요

이 문서는 Spring Boot 3.4.5, Java 17, MySQL 8 기반의 통합 AGI 시스템을 위한 데이터베이스 스키마 설계를 설명합니다. 데이터베이스 설계는 자연어 처리, 도구 사용, 계획 수립, 지식 및 기억 관리, 멀티모달 처리, 자가 학습, **샌드박스 환경** 등 다양한 기능을 지원하기 위한 테이블 구조와 관계를 정의합니다.

## 2. 데이터베이스 설계 원칙

1. **정규화**: 데이터 중복을 최소화하고 데이터 무결성을 보장하기 위해 적절한 정규화 수준 유지
2. **성능 최적화**: 자주 사용되는 쿼리에 대한 인덱스 설계 및 필요 시 전략적 비정규화
3. **확장성**: 데이터 증가에 대비한 파티셔닝 및 샤딩 전략 고려
4. **유연성**: 새로운 기능 및 요구사항 변화에 대응할 수 있는 유연한 스키마 설계
5. **보안**: 민감한 데이터에 대한 암호화 및 접근 제어 고려
6. **격리**: 샌드박스 환경의 데이터 격리 및 안전한 관리

## 3. 데이터베이스 스키마 개요

통합 AGI 시스템의 데이터베이스는 다음과 같은 주요 영역으로 구성됩니다:

1. **사용자 및 인증 관리**: 사용자, 역할, 권한, 세션 관리
2. **자연어 처리**: 대화, 의도, 엔티티, 감정 분석 데이터
3. **도구 관리**: 도구 정의, 파라미터, 실행 기록
4. **계획 관리**: 계획, 단계, 실행 상태
5. **지식 및 기억**: 지식 베이스, 메모리 저장소, 컨텍스트 관리
6. **멀티모달 데이터**: 이미지, 오디오, 비디오 메타데이터
7. **학습 및 피드백**: 학습 데이터, 피드백, 모델 버전 관리
8. **시스템 관리**: 설정, 로그, 모니터링 데이터
9. **설명 가능성**: 설명 데이터, 설명 알고리즘 정보
10. **감성 지능**: 감정 분석, 감정 응답 전략
11. **적응형 학습**: 사용자 프로필, 학습 선호도, 적응 규칙
12. **강화 학습**: 에이전트 상태, 보상 신호, 정책
13. **영역 간 지식 전이**: 지식 소스, 지식 매핑, 전이 학습 작업
14. **창의적 생성**: 창의적 작업, 생성 프롬프트
15. **샌드박스 관리**: 샌드박스 인스턴스, 작업 공간, 실행 로그, 자원 제한, 보안 정책

## 4. 엔티티 관계 다이어그램 (ERD)

```
+----------------+                          +----------------+                          +----------------+
|     USER       |                          |      ROLE      |                          |   PERMISSION   |
+----------------+                          +----------------+                          +----------------+
| PK: id         |                          | PK: id         |                          | PK: id         |
| username       |                          | name           |                          | name           |
| email          |                          | description    |                          | description    |
| password_hash  |                          |                |                          |                |
| created_at     |                          |                |                          |                |
| updated_at     |                          |                |                          |                |
+----------------+                          +----------------+                          +----------------+
        |                                          |                                           |
        |                                          |                                           |
        v                                          v                                           v
+----------------+       +----------------+       +----------------+
|  USER_ROLES    |       |ROLE_PERMISSIONS|       |    SESSION     |
+----------------+       +----------------+       +----------------+
| PK: id         |       | PK: id         |       | PK: id         |
| FK: user_id    |       | FK: role_id    |       | FK: user_id    |
| FK: role_id    |       | FK: permission_id      | token          |
| assigned_at    |       | assigned_at    |       | expires_at     |
+----------------+       +----------------+       | last_active    |
        |                        |                +----------------+
        |                        |
        v                        v
+----------------+       +----------------+       +----------------+
| CONVERSATION   |       |    MESSAGE     |       |    INTENT      |
+----------------+       +----------------+       +----------------+
| PK: id         |<------| PK: id         |       | PK: id         |
| FK: user_id    |       | FK: conv_id    |<------| FK: message_id |
| title          |       | content        |       | name           |
| created_at     |       | role           |       | confidence     |
| updated_at     |       | created_at     |       +----------------+
| status         |       | embedding      |
+----------------+       +----------------+

+----------------+       +----------------+       +----------------+
|     TOOL       |       | TOOL_PARAMETER |       | TOOL_EXECUTION |
+----------------+       +----------------+       +----------------+
| PK: id         |<------| PK: id         |       | PK: id         |
| name           |       | FK: tool_id    |       | FK: tool_id    |
| description    |       | name           |       | FK: user_id    |
| category       |       | type           |<------| parameters     |
| version        |       | required       |       | result         |
| enabled        |       | default_value  |       | status         |
| exec_env       |       +----------------+       | created_at     |
+----------------+                                | completed_at   |
                                                  | FK: sandbox_id |
                                                  +----------------+

+----------------+       +----------------+       +----------------+
|     PLAN       |       |   PLAN_STEP    |       | PLAN_EXECUTION |
+----------------+       +----------------+       +----------------+
| PK: id         |<------| PK: id         |       | PK: id         |
| FK: user_id    |       | FK: plan_id    |<------| FK: plan_id    |
| title          |       | order_index    |       | FK: user_id    |
| description    |       | description    |       | status         |
| created_at     |       | status         |       | started_at     |
| updated_at     |       | depends_on     |       | completed_at   |
+----------------+       +----------------+       | result         |
                                                  +----------------+

+----------------+       +----------------+       +----------------+
|   KNOWLEDGE    |       |     MEMORY     |       |    CONTEXT     |
+----------------+       +----------------+       +----------------+
| PK: id         |       | PK: id         |       | PK: id         |
| title          |       | FK: user_id    |<------| FK: session_id |
| content        |       | type           |       | data           |
| source         |       | content        |       | created_at     |
| created_at     |       | importance     |       | updated_at     |
| embedding      |<------| created_at     |       +----------------+
| metadata       |       | last_accessed  |
+----------------+       | embedding      |
                         +----------------+

+----------------+       +----------------+       +----------------+
| IMAGE_METADATA |       | AUDIO_METADATA |       | VIDEO_METADATA |
+----------------+       +----------------+       +----------------+
| PK: id         |       | PK: id         |       | PK: id         |
| FK: user_id    |       | FK: user_id    |       | FK: user_id    |
| filename       |       | filename       |       | filename       |
| path           |       | path           |       | path           |
| width          |       | duration       |       | duration       |
| height         |       | format         |       | format         |
| format         |       | created_at     |       | resolution     |
| created_at     |       | embedding      |       | created_at     |
| embedding      |       +----------------+       | embedding      |
+----------------+                                +----------------+

+----------------+       +----------------+       +----------------+
|   FEEDBACK     |       | LEARNING_DATA  |       |  MODEL_VERSION |
+----------------+       +----------------+       +----------------+
| PK: id         |       | PK: id         |<------| PK: id         |
| FK: user_id    |       | type           |       | model_name     |
| FK: entity_id  |       | input_data     |       | version        |
| entity_type    |       | output_data    |       | path           |
| rating         |       | created_at     |       | performance    |
| comment        |       | metadata       |       | created_at     |
| created_at     |       +----------------+       | is_active      |
+----------------+                                +----------------+

+----------------+       +----------------+       +----------------+
|    SETTING     |       |      LOG       |       |   MONITORING   |
+----------------+       +----------------+       +----------------+
| PK: id         |       | PK: id         |       | PK: id         |
| category       |       | level          |       | component      |
| key            |       | message        |       | metric         |
| value          |       | timestamp      |       | value          |
| description    |       | component      |       | timestamp      |
+----------------+       | user_id        |       +----------------+
                         +----------------+

+----------------+       +----------------+       +----------------+
|  EXPLANATION   |       |EMOTION_ANALYSIS|       |EMOTIONAL_RESP  |
+----------------+       +----------------+       +----------------+
| PK: id         |       | PK: id         |       | PK: id         |
| target_id      |       | target_id      |       | trigger_emotion|
| target_type    |       | target_type    |       | response_type  |
| explanation    |       | emotions       |       | template       |
| algorithm      |       | dominant       |       | priority       |
| confidence     |       | timestamp      |       | is_active      |
+----------------+       +----------------+       +----------------+

+----------------+       +----------------+       +----------------+
| USER_PROFILE   |       |LEARNING_PREF   |       |ADAPTATION_RULE |
+----------------+       +----------------+       +----------------+
| PK: user_id    |<------| PK: id         |       | PK: id         |
| interaction    |       | FK: user_id    |       | condition      |
| knowledge_map  |       | pref_key       |       | action         |
| last_updated   |       | pref_value     |       | priority       |
+----------------+       +----------------+       | is_active      |
                                                  +----------------+

+----------------+       +----------------+       +----------------+
| RL_AGENT_STATE |       | REWARD_SIGNAL  |       |   RL_POLICY    |
+----------------+       +----------------+       +----------------+
| PK: id         |       | PK: id         |       | PK: id         |
| agent_id       |       | trigger_id     |       | name           |
| state_rep      |       | trigger_type   |       | version        |
| timestamp      |       | reward_value   |       | parameters     |
+----------------+       | reward_source  |       | is_active      |
                         +----------------+       +----------------+

+----------------+       +----------------+       +----------------+
|KNOWLEDGE_SOURCE|       |KNOWLEDGE_MAPPING|      |TRANSFER_TASK   |
+----------------+       +----------------+       +----------------+
| PK: id         |       | PK: id         |       | PK: id         |
| name           |       | source_concept |       | source_id      |
| domain         |       | target_concept |       | target_id      |
| description    |       | relation_type  |       | status         |
| connection_info|       | confidence     |       | result         |
+----------------+       +----------------+       +----------------+

+----------------+       +----------------+
| CREATIVE_WORK  |       |GENERATION_PROMPT|
+----------------+       +----------------+
| PK: id         |<------| PK: id         |
| FK: user_id    |       | FK: work_id    |
| type           |       | prompt_text    |
| content_ref    |       | timestamp      |
| parameters     |       +----------------+
| metadata       |
+----------------+

+----------------+       +----------------+       +----------------+
|    SANDBOX     |       |SANDBOX_WORKSPACE|      |SANDBOX_EXECUTION|
+----------------+       +----------------+       +----------------+
| PK: id         |<------| PK: id         |<------| PK: id         |
| FK: user_id    |       | FK: sandbox_id |       | FK: sandbox_id |
| status         |       | root_path      |       | command        |
| created_at     |       | size_bytes     |       | started_at     |
| last_active    |       | created_at     |       | completed_at   |
| container_id   |       | last_modified  |       | exit_code      |
| config         |       +----------------+       | stdout         |
+----------------+                                | stderr         |
        |                                         +----------------+
        |
+----------------+       +----------------+       +----------------+
|SANDBOX_RESOURCE|       |SANDBOX_SECURITY|       |  SANDBOX_FILE  |
+----------------+       +----------------+       +----------------+
| PK: id         |       | PK: id         |       | PK: id         |
| FK: sandbox_id |       | FK: sandbox_id |       | FK: sandbox_id |
| cpu_limit      |       | network_policy |       | path           |
| memory_limit   |       | syscall_policy |       | content_hash   |
| disk_limit     |       | mount_policy   |       | size_bytes     |
| network_limit  |       | env_variables  |       | created_at     |
| timeout        |       | capabilities   |       | last_modified  |
+----------------+       +----------------+       +----------------+
```

## 5. 테이블 상세 설계

### 5.1 사용자 및 인증 관리

#### 5.1.1 USER 테이블

사용자 계정 정보를 저장합니다.

```sql
CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL, -- object_model에는 password지만 hash 저장
    email VARCHAR(100) NOT NULL UNIQUE,
    nickname VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- object_model에는 없지만 추가
    last_login_at TIMESTAMP NULL,

    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_is_active (is_active)
);
```

#### 5.1.2 ROLE 테이블

사용자 역할을 정의합니다.

```sql
CREATE TABLE role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,

    INDEX idx_name (name)
);
```

#### 5.1.3 PERMISSION 테이블

역할에 부여될 수 있는 권한을 정의합니다.

```sql
CREATE TABLE permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,

    INDEX idx_name (name)
);
```

#### 5.1.4 USER_ROLES 테이블

사용자와 역할 간의 다대다 관계를 매핑합니다. 각 할당에 대한 시간 정보도 함께 저장합니다.

```sql
CREATE TABLE user_roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE,
    UNIQUE INDEX idx_user_role (user_id, role_id),
    INDEX idx_role_id (role_id)
);
```

#### 5.1.5 ROLE_PERMISSIONS 테이블

역할과 권한 간의 다대다 관계를 매핑합니다. 각 할당에 대한 시간 정보도 함께 저장합니다.

```sql
CREATE TABLE role_permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permission(id) ON DELETE CASCADE,
    UNIQUE INDEX idx_role_permission (role_id, permission_id),
    INDEX idx_permission_id (permission_id)
);
```

#### 5.1.6 SESSION 테이블

사용자 세션 정보를 관리합니다.

```sql
CREATE TABLE session (
    id VARCHAR(36) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE, -- JWT 또는 세션 토큰
    ip_address VARCHAR(45),
    user_agent VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_active TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,

    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_token (token),
    INDEX idx_expires_at (expires_at)
);
```
### 5.2 자연어 처리

#### 5.2.1 CONVERSATION 테이블

사용자와 시스템 간의 대화 세션을 저장합니다.

```sql
CREATE TABLE conversation (
    id VARCHAR(36) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(200),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    status ENUM('active', 'archived', 'deleted') DEFAULT 'active',
    metadata JSON, -- 대화 관련 메타데이터 (사용된 모델, 컨텍스트 등)
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
);
```

#### 5.2.2 MESSAGE 테이블

대화 내 개별 메시지를 저장합니다.

```sql
CREATE TABLE message (
    id VARCHAR(36) PRIMARY KEY,
    conversation_id VARCHAR(36) NOT NULL,
    role ENUM('user', 'assistant', 'system', 'tool') NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    embedding BLOB, -- 벡터 임베딩 저장 (검색 및 유사성 비교용)
    metadata JSON, -- 메시지 관련 메타데이터 (감정 분석 결과, 사용된 도구 ID 등)
    
    FOREIGN KEY (conversation_id) REFERENCES conversation(id) ON DELETE CASCADE,
    INDEX idx_conversation_id (conversation_id),
    INDEX idx_role (role),
    INDEX idx_created_at (created_at)
);
```

#### 5.2.3 INTENT 테이블

메시지에서 추출된 사용자 의도를 저장합니다.

```sql
CREATE TABLE intent (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    message_id VARCHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL, -- 의도 이름 (예: search_product, ask_help)
    confidence DECIMAL(5,4) NOT NULL, -- 신뢰도 점수 (0.0000 ~ 1.0000)
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (message_id) REFERENCES message(id) ON DELETE CASCADE,
    INDEX idx_message_id (message_id),
    INDEX idx_name (name)
);
```

#### 5.2.4 ENTITY 테이블

메시지에서 추출된 엔티티(개체)를 저장합니다.

```sql
CREATE TABLE entity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    message_id VARCHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL, -- 엔티티 이름 (예: product, location)
    value VARCHAR(255) NOT NULL, -- 엔티티 값 (예: "iPhone", "서울")
    type VARCHAR(50) NOT NULL, -- 엔티티 타입 (예: product_name, city)
    start_pos INT, -- 원본 텍스트 내 시작 위치
    end_pos INT, -- 원본 텍스트 내 종료 위치
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (message_id) REFERENCES message(id) ON DELETE CASCADE,
    INDEX idx_message_id (message_id),
    INDEX idx_name (name),
    INDEX idx_type (type)
);
```

#### 5.2.5 SENTIMENT 테이블

메시지의 감정 분석 결과를 저장합니다.

```sql
CREATE TABLE sentiment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    message_id VARCHAR(36) NOT NULL,
    score DECIMAL(5,4) NOT NULL, -- 감정 점수 (-1.0000 ~ 1.0000)
    label ENUM('positive', 'negative', 'neutral', 'mixed') NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (message_id) REFERENCES message(id) ON DELETE CASCADE,
    INDEX idx_message_id (message_id),
    INDEX idx_label (label)
);
```

### 5.3 도구 관리

#### 5.3.1 TOOL 테이블

시스템에서 사용 가능한 도구 정의를 저장합니다.

```sql
CREATE TABLE tool (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    category VARCHAR(50),
    version VARCHAR(20),
    enabled BOOLEAN DEFAULT TRUE,
    exec_env ENUM('host', 'sandbox', 'both') DEFAULT 'host', -- 실행 환경 추가
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_name (name),
    INDEX idx_category (category),
    INDEX idx_exec_env (exec_env)
);
```

#### 5.3.2 TOOL_PARAMETER 테이블

도구 파라미터 정의를 저장합니다.

```sql
CREATE TABLE tool_parameter (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tool_id VARCHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    type VARCHAR(50) NOT NULL,
    required BOOLEAN DEFAULT FALSE,
    default_value VARCHAR(255),
    
    FOREIGN KEY (tool_id) REFERENCES tool(id) ON DELETE CASCADE,
    UNIQUE KEY uk_tool_param (tool_id, name)
);
```

#### 5.3.3 TOOL_EXECUTION 테이블

도구 실행 기록을 저장합니다.

```sql
CREATE TABLE tool_execution (
    id VARCHAR(36) PRIMARY KEY,
    tool_id VARCHAR(36) NOT NULL,
    user_id BIGINT,
    sandbox_id VARCHAR(36), -- 샌드박스 ID 추가
    parameters JSON,
    result JSON,
    status ENUM('pending', 'running', 'completed', 'failed') DEFAULT 'pending',
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    
    FOREIGN KEY (tool_id) REFERENCES tool(id),
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE SET NULL,
    FOREIGN KEY (sandbox_id) REFERENCES sandbox(id) ON DELETE SET NULL,
    INDEX idx_tool_id (tool_id),
    INDEX idx_user_id (user_id),
    INDEX idx_sandbox_id (sandbox_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
);
```

### 5.4 계획 관리

#### 5.4.1 PLAN 테이블

계획 정보를 저장합니다.

```sql
CREATE TABLE plan (
    id VARCHAR(36) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    status ENUM('pending', 'running', 'completed', 'failed', 'canceled') DEFAULT 'pending',
    started_at TIMESTAMP NULL,
    completed_at TIMESTAMP NULL,
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
);
```

#### 5.4.2 PLAN_STEP 테이블

계획의 개별 단계를 저장합니다.

```sql
CREATE TABLE plan_step (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    plan_id VARCHAR(36) NOT NULL,
    order_index INT NOT NULL, -- 단계 순서
    description TEXT NOT NULL,
    status ENUM('pending', 'running', 'completed', 'failed', 'skipped') DEFAULT 'pending',
    depends_on VARCHAR(255), -- 의존성 있는 단계 ID들 (쉼표로 구분)
    started_at TIMESTAMP NULL,
    completed_at TIMESTAMP NULL,
    execution_details JSON, -- 실행 세부 정보
    
    FOREIGN KEY (plan_id) REFERENCES plan(id) ON DELETE CASCADE,
    INDEX idx_plan_id (plan_id),
    INDEX idx_status (status),
    UNIQUE KEY uk_plan_step_order (plan_id, order_index)
);
```

#### 5.4.3 PLAN_EXECUTION 테이블

계획 실행 기록을 저장합니다.

```sql
CREATE TABLE plan_execution (
    id VARCHAR(36) PRIMARY KEY,
    plan_id VARCHAR(36) NOT NULL,
    user_id BIGINT NOT NULL,
    status ENUM('pending', 'running', 'completed', 'failed', 'canceled') DEFAULT 'pending',
    started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    result JSON, -- 실행 결과 요약
    error_details TEXT, -- 오류 발생 시 상세 내용
    
    FOREIGN KEY (plan_id) REFERENCES plan(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_plan_id (plan_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_started_at (started_at)
);
```

### 5.5 지식 및 기억 시스템

#### 5.5.1 KNOWLEDGE 테이블

시스템의 지식 베이스 항목을 저장합니다.

```sql
CREATE TABLE knowledge (
    id VARCHAR(36) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    source VARCHAR(255), -- 지식 출처
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    embedding BLOB, -- 벡터 임베딩 저장 (검색 및 유사성 비교용)
    metadata JSON, -- 지식 관련 메타데이터
    
    FULLTEXT INDEX ft_title_content (title, content),
    INDEX idx_source (source),
    INDEX idx_created_at (created_at)
);
```

#### 5.5.2 MEMORY 테이블

사용자별 기억 정보를 저장합니다.

```sql
CREATE TABLE memory (
    id VARCHAR(36) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type ENUM('episodic', 'semantic', 'procedural', 'preference') NOT NULL,
    content TEXT NOT NULL,
    importance DECIMAL(3,2) DEFAULT 0.50, -- 중요도 점수 (0.00 ~ 1.00)
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_accessed TIMESTAMP NULL,
    access_count INT DEFAULT 0,
    embedding BLOB, -- 벡터 임베딩 저장 (검색 및 유사성 비교용)
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_user_id_type (user_id, type),
    INDEX idx_importance (importance),
    INDEX idx_created_at (created_at),
    INDEX idx_last_accessed (last_accessed)
);
```

#### 5.5.3 CONTEXT 테이블

세션별 컨텍스트 정보를 저장합니다.

```sql
CREATE TABLE context (
    id VARCHAR(36) PRIMARY KEY,
    session_id VARCHAR(36) NOT NULL,
    data JSON NOT NULL, -- 컨텍스트 데이터
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (session_id) REFERENCES session(id) ON DELETE CASCADE,
    INDEX idx_session_id (session_id),
    INDEX idx_created_at (created_at)
);
```

#### 5.5.4 KNOWLEDGE_TAG 테이블

지식 항목에 대한 태그를 저장합니다.

```sql
CREATE TABLE knowledge_tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    knowledge_id VARCHAR(36) NOT NULL,
    tag VARCHAR(50) NOT NULL,
    
    FOREIGN KEY (knowledge_id) REFERENCES knowledge(id) ON DELETE CASCADE,
    INDEX idx_knowledge_id (knowledge_id),
    INDEX idx_tag (tag),
    UNIQUE KEY uk_knowledge_tag (knowledge_id, tag)
);
```

#### 5.5.5 MEMORY_RELATION 테이블

기억 항목 간의 관계를 저장합니다.

```sql
CREATE TABLE memory_relation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    source_memory_id VARCHAR(36) NOT NULL,
    target_memory_id VARCHAR(36) NOT NULL,
    relation_type VARCHAR(50) NOT NULL, -- 관계 유형 (예: causes, precedes, contradicts)
    strength DECIMAL(3,2) DEFAULT 0.50, -- 관계 강도 (0.00 ~ 1.00)
    
    FOREIGN KEY (source_memory_id) REFERENCES memory(id) ON DELETE CASCADE,
    FOREIGN KEY (target_memory_id) REFERENCES memory(id) ON DELETE CASCADE,
    INDEX idx_source_memory_id (source_memory_id),
    INDEX idx_target_memory_id (target_memory_id),
    INDEX idx_relation_type (relation_type),
    UNIQUE KEY uk_memory_relation (source_memory_id, target_memory_id, relation_type)
);
```

### 5.6 멀티모달 데이터

#### 5.6.1 IMAGE_METADATA 테이블

이미지 파일의 메타데이터를 저장합니다.

```sql
CREATE TABLE image_metadata (
    id VARCHAR(36) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    filename VARCHAR(255) NOT NULL,
    path VARCHAR(512) NOT NULL,
    width INT,
    height INT,
    format VARCHAR(20),
    size_bytes BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    embedding BLOB, -- 이미지 임베딩 벡터 (유사 이미지 검색용)
    tags JSON, -- 이미지 태그 및 레이블
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_filename (filename),
    INDEX idx_created_at (created_at)
);
```

#### 5.6.2 AUDIO_METADATA 테이블

오디오 파일의 메타데이터를 저장합니다.

```sql
CREATE TABLE audio_metadata (
    id VARCHAR(36) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    filename VARCHAR(255) NOT NULL,
    path VARCHAR(512) NOT NULL,
    duration INT, -- 초 단위
    format VARCHAR(20),
    sample_rate INT,
    channels INT,
    size_bytes BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    embedding BLOB, -- 오디오 임베딩 벡터 (유사 오디오 검색용)
    transcript TEXT, -- 음성 인식 결과
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_filename (filename),
    INDEX idx_created_at (created_at)
);
```

#### 5.6.3 VIDEO_METADATA 테이블

비디오 파일의 메타데이터를 저장합니다.

```sql
CREATE TABLE video_metadata (
    id VARCHAR(36) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    filename VARCHAR(255) NOT NULL,
    path VARCHAR(512) NOT NULL,
    duration INT, -- 초 단위
    format VARCHAR(20),
    resolution VARCHAR(20), -- 예: 1920x1080
    fps DECIMAL(5,2), -- 초당 프레임 수
    size_bytes BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    embedding BLOB, -- 비디오 임베딩 벡터 (유사 비디오 검색용)
    transcript TEXT, -- 음성 인식 결과
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_filename (filename),
    INDEX idx_created_at (created_at)
);
```

#### 5.6.4 MEDIA_TAG 테이블

미디어 파일에 대한 태그를 저장합니다.

```sql
CREATE TABLE media_tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    media_id VARCHAR(36) NOT NULL,
    media_type ENUM('image', 'audio', 'video') NOT NULL,
    tag VARCHAR(50) NOT NULL,
    confidence DECIMAL(5,4), -- 태그 신뢰도 (0.0000 ~ 1.0000)
    
    INDEX idx_media_id_type (media_id, media_type),
    INDEX idx_tag (tag)
);
```

#### 5.6.5 MEDIA_OBJECT 테이블

미디어 내 감지된 객체 정보를 저장합니다.

```sql
CREATE TABLE media_object (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    media_id VARCHAR(36) NOT NULL,
    media_type ENUM('image', 'video') NOT NULL,
    object_class VARCHAR(100) NOT NULL, -- 객체 클래스 (예: person, car)
    confidence DECIMAL(5,4) NOT NULL, -- 객체 감지 신뢰도 (0.0000 ~ 1.0000)
    bounding_box JSON, -- 객체 위치 정보 (x, y, width, height)
    frame_number INT, -- 비디오의 경우 프레임 번호
    
    INDEX idx_media_id_type (media_id, media_type),
    INDEX idx_object_class (object_class)
);
```

### 5.7 학습 및 피드백

#### 5.7.1 FEEDBACK 테이블

사용자 피드백 정보를 저장합니다.

```sql
CREATE TABLE feedback (
    id VARCHAR(36) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    entity_id VARCHAR(36) NOT NULL, -- 피드백 대상 ID (메시지, 도구 실행 등)
    entity_type VARCHAR(50) NOT NULL, -- 피드백 대상 유형 (message, tool_execution 등)
    rating INT, -- 평점 (1-5)
    comment TEXT, -- 상세 피드백 내용
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_entity_id_type (entity_id, entity_type),
    INDEX idx_rating (rating),
    INDEX idx_created_at (created_at)
);
```

#### 5.7.2 LEARNING_DATA 테이블

모델 학습을 위한 데이터를 저장합니다.

```sql
CREATE TABLE learning_data (
    id VARCHAR(36) PRIMARY KEY,
    type VARCHAR(50) NOT NULL, -- 학습 데이터 유형 (conversation, tool_usage 등)
    input_data JSON NOT NULL, -- 입력 데이터
    output_data JSON NOT NULL, -- 출력/레이블 데이터
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    metadata JSON, -- 학습 데이터 관련 메타데이터
    
    INDEX idx_type (type),
    INDEX idx_created_at (created_at)
);
```

#### 5.7.3 MODEL_VERSION 테이블

모델 버전 정보를 저장합니다.

```sql
CREATE TABLE model_version (
    id VARCHAR(36) PRIMARY KEY,
    model_name VARCHAR(100) NOT NULL,
    version VARCHAR(50) NOT NULL,
    path VARCHAR(512) NOT NULL, -- 모델 파일 경로
    performance JSON, -- 성능 지표
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT FALSE,
    
    INDEX idx_model_name (model_name),
    INDEX idx_is_active (is_active),
    UNIQUE KEY uk_model_version (model_name, version)
);
```

#### 5.7.4 TRAINING_JOB 테이블

모델 학습 작업 정보를 저장합니다.

```sql
CREATE TABLE training_job (
    id VARCHAR(36) PRIMARY KEY,
    model_name VARCHAR(100) NOT NULL,
    status ENUM('pending', 'running', 'completed', 'failed') DEFAULT 'pending',
    parameters JSON, -- 학습 파라미터
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    started_at TIMESTAMP NULL,
    completed_at TIMESTAMP NULL,
    result_model_id VARCHAR(36), -- 학습 결과 모델 ID
    metrics JSON, -- 학습 결과 지표
    
    INDEX idx_model_name (model_name),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
);
```

#### 5.7.5 EVALUATION_RESULT 테이블

모델 평가 결과를 저장합니다.

```sql
CREATE TABLE evaluation_result (
    id VARCHAR(36) PRIMARY KEY,
    model_version_id VARCHAR(36) NOT NULL,
    dataset_name VARCHAR(100) NOT NULL,
    metrics JSON NOT NULL, -- 평가 지표
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (model_version_id) REFERENCES model_version(id) ON DELETE CASCADE,
    INDEX idx_model_version_id (model_version_id),
    INDEX idx_dataset_name (dataset_name)
);
```

### 5.8 시스템 관리

#### 5.8.1 SETTING 테이블

시스템 설정 정보를 저장합니다.

```sql
CREATE TABLE setting (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category VARCHAR(50) NOT NULL,
    key VARCHAR(100) NOT NULL,
    value TEXT NOT NULL,
    description TEXT,
    is_encrypted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_category_key (category, key),
    INDEX idx_category (category)
);
```

#### 5.8.2 LOG 테이블

시스템 로그를 저장합니다.

```sql
CREATE TABLE log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    level ENUM('trace', 'debug', 'info', 'warn', 'error', 'fatal') NOT NULL,
    message TEXT NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    component VARCHAR(100), -- 로그 발생 컴포넌트
    user_id BIGINT, -- 관련 사용자 ID (있는 경우)
    session_id VARCHAR(36), -- 관련 세션 ID (있는 경우)
    request_id VARCHAR(36), -- 관련 요청 ID (있는 경우)
    stack_trace TEXT, -- 오류 발생 시 스택 트레이스
    
    INDEX idx_level (level),
    INDEX idx_timestamp (timestamp),
    INDEX idx_component (component),
    INDEX idx_user_id (user_id)
);
```

#### 5.8.3 MONITORING 테이블

시스템 모니터링 지표를 저장합니다.

```sql
CREATE TABLE monitoring (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    component VARCHAR(100) NOT NULL, -- 모니터링 대상 컴포넌트
    metric VARCHAR(100) NOT NULL, -- 지표 이름
    value DECIMAL(20,6) NOT NULL, -- 지표 값
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_component_metric (component, metric),
    INDEX idx_timestamp (timestamp)
);
```

#### 5.8.4 TASK_QUEUE 테이블

비동기 작업 큐를 관리합니다.

```sql
CREATE TABLE task_queue (
    id VARCHAR(36) PRIMARY KEY,
    task_type VARCHAR(100) NOT NULL,
    priority INT DEFAULT 0,
    status ENUM('pending', 'processing', 'completed', 'failed', 'canceled') DEFAULT 'pending',
    payload JSON NOT NULL,
    result JSON,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    started_at TIMESTAMP NULL,
    completed_at TIMESTAMP NULL,
    retry_count INT DEFAULT 0,
    max_retries INT DEFAULT 3,
    
    INDEX idx_task_type (task_type),
    INDEX idx_status (status),
    INDEX idx_priority_status (priority, status),
    INDEX idx_created_at (created_at)
);
```

#### 5.8.5 HEALTH_CHECK 테이블

시스템 구성 요소의 상태 확인 결과를 저장합니다.

```sql
CREATE TABLE health_check (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    component VARCHAR(100) NOT NULL,
    status ENUM('up', 'down', 'degraded') NOT NULL,
    details JSON,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_component (component),
    INDEX idx_status (status),
    INDEX idx_timestamp (timestamp)
);
```

### 5.9 샌드박스 관리

#### 5.9.1 SANDBOX 테이블

샌드박스 인스턴스 정보를 저장합니다.

```sql
CREATE TABLE sandbox (
    id VARCHAR(36) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    status ENUM('creating', 'running', 'paused', 'stopped', 'failed', 'deleted') DEFAULT 'creating',
    container_id VARCHAR(64),
    image_name VARCHAR(255) NOT NULL,
    image_tag VARCHAR(50) NOT NULL DEFAULT 'latest',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    started_at TIMESTAMP,
    last_active TIMESTAMP,
    expires_at TIMESTAMP,
    config JSON, -- 샌드박스 구성 정보 (포트, 볼륨 등)
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_container_id (container_id),
    INDEX idx_created_at (created_at),
    INDEX idx_expires_at (expires_at)
);
```

#### 5.9.2 SANDBOX_WORKSPACE 테이블

샌드박스 작업 공간 정보를 저장합니다.

```sql
CREATE TABLE sandbox_workspace (
    id VARCHAR(36) PRIMARY KEY,
    sandbox_id VARCHAR(36) NOT NULL,
    root_path VARCHAR(255) NOT NULL,
    size_bytes BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (sandbox_id) REFERENCES sandbox(id) ON DELETE CASCADE,
    INDEX idx_sandbox_id (sandbox_id)
);
```

#### 5.9.3 SANDBOX_EXECUTION 테이블

샌드박스 내 명령어 실행 기록을 저장합니다.

```sql
CREATE TABLE sandbox_execution (
    id VARCHAR(36) PRIMARY KEY,
    sandbox_id VARCHAR(36) NOT NULL,
    command TEXT NOT NULL,
    working_dir VARCHAR(255),
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    exit_code INT,
    stdout LONGTEXT,
    stderr LONGTEXT,
    resource_usage JSON, -- CPU, 메모리 등 자원 사용량
    
    FOREIGN KEY (sandbox_id) REFERENCES sandbox(id) ON DELETE CASCADE,
    INDEX idx_sandbox_id (sandbox_id),
    INDEX idx_started_at (started_at)
);
```

#### 5.9.4 SANDBOX_RESOURCE 테이블

샌드박스 자원 제한 정보를 저장합니다.

```sql
CREATE TABLE sandbox_resource (
    id VARCHAR(36) PRIMARY KEY,
    sandbox_id VARCHAR(36) NOT NULL,
    cpu_limit INT, -- CPU 코어 수 또는 비율
    memory_limit BIGINT, -- 바이트 단위
    disk_limit BIGINT, -- 바이트 단위
    network_limit BIGINT, -- 바이트/초 단위
    timeout INT, -- 초 단위 (샌드박스 최대 실행 시간)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (sandbox_id) REFERENCES sandbox(id) ON DELETE CASCADE,
    UNIQUE KEY uk_sandbox_resource (sandbox_id)
);
```

#### 5.9.5 SANDBOX_SECURITY 테이블

샌드박스 보안 정책 정보를 저장합니다.

```sql
CREATE TABLE sandbox_security (
    id VARCHAR(36) PRIMARY KEY,
    sandbox_id VARCHAR(36) NOT NULL,
    network_policy JSON, -- 네트워크 접근 정책 (허용/차단 IP, 포트 등)
    syscall_policy JSON, -- 시스템 콜 필터링 정책
    mount_policy JSON, -- 볼륨 마운트 정책
    env_variables JSON, -- 환경 변수
    capabilities JSON, -- Linux 커널 기능 제한
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (sandbox_id) REFERENCES sandbox(id) ON DELETE CASCADE,
    UNIQUE KEY uk_sandbox_security (sandbox_id)
);
```

#### 5.9.6 SANDBOX_FILE 테이블

샌드박스 내 파일 정보를 저장합니다.

```sql
CREATE TABLE sandbox_file (
    id VARCHAR(36) PRIMARY KEY,
    sandbox_id VARCHAR(36) NOT NULL,
    path VARCHAR(255) NOT NULL,
    content_hash VARCHAR(64), -- 파일 내용의 해시값
    size_bytes BIGINT DEFAULT 0,
    mime_type VARCHAR(100),
    is_directory BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (sandbox_id) REFERENCES sandbox(id) ON DELETE CASCADE,
    UNIQUE KEY uk_sandbox_file_path (sandbox_id, path),
    INDEX idx_sandbox_id (sandbox_id),
    INDEX idx_content_hash (content_hash)
);
```

#### 5.9.7 SANDBOX_PORT 테이블

샌드박스 포트 매핑 정보를 저장합니다.

```sql
CREATE TABLE sandbox_port (
    id VARCHAR(36) PRIMARY KEY,
    sandbox_id VARCHAR(36) NOT NULL,
    container_port INT NOT NULL,
    host_port INT NOT NULL,
    protocol ENUM('tcp', 'udp') DEFAULT 'tcp',
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (sandbox_id) REFERENCES sandbox(id) ON DELETE CASCADE,
    UNIQUE KEY uk_sandbox_port (sandbox_id, container_port, protocol),
    INDEX idx_sandbox_id (sandbox_id)
);
```

#### 5.9.8 SANDBOX_TEMPLATE 테이블

재사용 가능한 샌드박스 템플릿 정보를 저장합니다.

```sql
CREATE TABLE sandbox_template (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    image_name VARCHAR(255) NOT NULL,
    image_tag VARCHAR(50) NOT NULL DEFAULT 'latest',
    config JSON, -- 기본 구성 정보
    resource_config JSON, -- 기본 자원 제한 정보
    security_config JSON, -- 기본 보안 정책 정보
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    
    UNIQUE KEY uk_template_name (name),
    INDEX idx_is_active (is_active)
);
```

### 5.10 설명 가능성

#### 5.10.1 EXPLANATION 테이블

시스템 결정 및 추론에 대한 설명을 저장합니다.

```sql
CREATE TABLE explanation (
    id VARCHAR(36) PRIMARY KEY,
    target_id VARCHAR(36) NOT NULL, -- 설명 대상 ID (메시지, 도구 실행 등)
    target_type VARCHAR(50) NOT NULL, -- 설명 대상 유형 (message, tool_execution 등)
    explanation TEXT NOT NULL, -- 설명 내용
    algorithm VARCHAR(100), -- 사용된 설명 알고리즘
    confidence DECIMAL(5,4), -- 설명 신뢰도 (0.0000 ~ 1.0000)
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_target_id_type (target_id, target_type),
    INDEX idx_algorithm (algorithm),
    INDEX idx_created_at (created_at)
);
```

#### 5.10.2 EXPLANATION_FEATURE 테이블

설명에 사용된 주요 특성 정보를 저장합니다.

```sql
CREATE TABLE explanation_feature (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    explanation_id VARCHAR(36) NOT NULL,
    feature_name VARCHAR(100) NOT NULL,
    feature_value TEXT NOT NULL,
    importance DECIMAL(5,4) NOT NULL, -- 특성 중요도 (0.0000 ~ 1.0000)
    
    FOREIGN KEY (explanation_id) REFERENCES explanation(id) ON DELETE CASCADE,
    INDEX idx_explanation_id (explanation_id),
    INDEX idx_feature_name (feature_name),
    INDEX idx_importance (importance)
);
```

#### 5.10.3 EXPLANATION_TEMPLATE 테이블

설명 템플릿을 저장합니다.

```sql
CREATE TABLE explanation_template (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    template_text TEXT NOT NULL, -- 템플릿 텍스트 (변수 포함)
    target_type VARCHAR(50) NOT NULL, -- 적용 대상 유형
    complexity_level ENUM('simple', 'moderate', 'detailed') DEFAULT 'moderate',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_name (name),
    INDEX idx_target_type (target_type),
    INDEX idx_complexity_level (complexity_level),
    INDEX idx_is_active (is_active)
);
```

### 5.11 감성 지능

#### 5.11.1 EMOTION_ANALYSIS 테이블

메시지나 콘텐츠의 감정 분석 결과를 저장합니다.

```sql
CREATE TABLE emotion_analysis (
    id VARCHAR(36) PRIMARY KEY,
    target_id VARCHAR(36) NOT NULL, -- 분석 대상 ID (메시지, 오디오 등)
    target_type VARCHAR(50) NOT NULL, -- 분석 대상 유형 (message, audio 등)
    emotions JSON NOT NULL, -- 감정 분석 결과 (감정 유형별 점수)
    dominant_emotion VARCHAR(50), -- 주요 감정
    intensity DECIMAL(5,4), -- 감정 강도 (0.0000 ~ 1.0000)
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_target_id_type (target_id, target_type),
    INDEX idx_dominant_emotion (dominant_emotion),
    INDEX idx_timestamp (timestamp)
);
```

#### 5.11.2 EMOTIONAL_RESPONSE 테이블

감정 기반 응답 전략을 저장합니다.

```sql
CREATE TABLE emotional_response (
    id VARCHAR(36) PRIMARY KEY,
    trigger_emotion VARCHAR(50) NOT NULL, -- 트리거 감정
    response_type VARCHAR(100) NOT NULL, -- 응답 유형
    template TEXT NOT NULL, -- 응답 템플릿
    priority INT DEFAULT 0, -- 우선순위
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_trigger_emotion (trigger_emotion),
    INDEX idx_response_type (response_type),
    INDEX idx_priority (priority),
    INDEX idx_is_active (is_active)
);
```

#### 5.11.3 USER_EMOTION_HISTORY 테이블

사용자별 감정 변화 이력을 저장합니다.

```sql
CREATE TABLE user_emotion_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    emotion VARCHAR(50) NOT NULL,
    intensity DECIMAL(5,4) NOT NULL, -- 감정 강도 (0.0000 ~ 1.0000)
    context VARCHAR(255), -- 감정 발생 컨텍스트
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_emotion (emotion),
    INDEX idx_timestamp (timestamp)
);
```

#### 5.11.4 EMPATHY_MODEL 테이블

공감 모델 정보를 저장합니다.

```sql
CREATE TABLE empathy_model (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    parameters JSON, -- 모델 파라미터
    version VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_name_version (name, version),
    INDEX idx_is_active (is_active)
);
```

### 5.12 적응형 학습

#### 5.12.1 USER_PROFILE 테이블

사용자 프로필 정보를 저장합니다.

```sql
CREATE TABLE user_profile (
    user_id BIGINT PRIMARY KEY,
    interaction_history JSON, -- 상호작용 이력 요약
    knowledge_map JSON, -- 사용자 지식 맵
    learning_style VARCHAR(50), -- 학습 스타일 (예: visual, auditory, kinesthetic)
    proficiency_level VARCHAR(50), -- 숙련도 수준
    last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_learning_style (learning_style),
    INDEX idx_proficiency_level (proficiency_level)
);
```

#### 5.12.2 LEARNING_PREFERENCE 테이블

사용자별 학습 선호도를 저장합니다.

```sql
CREATE TABLE learning_preference (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    pref_key VARCHAR(100) NOT NULL, -- 선호도 키 (예: content_type, difficulty_level)
    pref_value VARCHAR(255) NOT NULL, -- 선호도 값
    confidence DECIMAL(5,4) DEFAULT 0.50, -- 신뢰도 (0.0000 ~ 1.0000)
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_pref (user_id, pref_key),
    INDEX idx_pref_key_value (pref_key, pref_value)
);
```

#### 5.12.3 ADAPTATION_RULE 테이블

적응형 학습 규칙을 저장합니다.

```sql
CREATE TABLE adaptation_rule (
    id VARCHAR(36) PRIMARY KEY,
    condition TEXT NOT NULL, -- 적용 조건 (JSON 또는 규칙 표현식)
    action TEXT NOT NULL, -- 수행 작업
    priority INT DEFAULT 0, -- 우선순위
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_priority (priority),
    INDEX idx_is_active (is_active)
);
```

#### 5.12.4 LEARNING_SESSION 테이블

사용자 학습 세션 정보를 저장합니다.

```sql
CREATE TABLE learning_session (
    id VARCHAR(36) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    topic VARCHAR(255) NOT NULL,
    start_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    end_time TIMESTAMP NULL,
    duration INT, -- 초 단위
    progress DECIMAL(5,2), -- 진행률 (0.00 ~ 100.00)
    metrics JSON, -- 학습 지표 (정확도, 완료 항목 등)
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_topic (topic),
    INDEX idx_start_time (start_time)
);
```

#### 5.12.5 ADAPTATION_LOG 테이블

적응형 학습 시스템의 적응 이력을 저장합니다.

```sql
CREATE TABLE adaptation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    rule_id VARCHAR(36), -- 적용된 규칙 ID
    context JSON NOT NULL, -- 적응 시 컨텍스트
    adaptation_type VARCHAR(100) NOT NULL, -- 적응 유형
    adaptation_details JSON NOT NULL, -- 적응 세부 정보
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (rule_id) REFERENCES adaptation_rule(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_adaptation_type (adaptation_type),
    INDEX idx_timestamp (timestamp)
);
```

### 5.13 강화 학습

#### 5.13.1 RL_AGENT_STATE 테이블

강화 학습 에이전트의 상태 정보를 저장합니다.

```sql
CREATE TABLE rl_agent_state (
    id VARCHAR(36) PRIMARY KEY,
    agent_id VARCHAR(100) NOT NULL, -- 에이전트 식별자
    state_representation JSON NOT NULL, -- 상태 표현
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    metadata JSON, -- 추가 메타데이터
    
    INDEX idx_agent_id (agent_id),
    INDEX idx_timestamp (timestamp)
);
```

#### 5.13.2 REWARD_SIGNAL 테이블

강화 학습 보상 신호를 저장합니다.

```sql
CREATE TABLE reward_signal (
    id VARCHAR(36) PRIMARY KEY,
    agent_id VARCHAR(100) NOT NULL, -- 에이전트 식별자
    trigger_id VARCHAR(36) NOT NULL, -- 보상 트리거 ID (행동, 상태 전이 등)
    trigger_type VARCHAR(50) NOT NULL, -- 트리거 유형
    reward_value DECIMAL(10,6) NOT NULL, -- 보상 값
    reward_source VARCHAR(100) NOT NULL, -- 보상 출처
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    context JSON, -- 보상 컨텍스트
    
    INDEX idx_agent_id (agent_id),
    INDEX idx_trigger_id_type (trigger_id, trigger_type),
    INDEX idx_reward_source (reward_source),
    INDEX idx_timestamp (timestamp)
);
```

#### 5.13.3 RL_POLICY 테이블

강화 학습 정책 정보를 저장합니다.

```sql
CREATE TABLE rl_policy (
    id VARCHAR(36) PRIMARY KEY,
    agent_id VARCHAR(100) NOT NULL, -- 에이전트 식별자
    name VARCHAR(100) NOT NULL,
    version VARCHAR(50) NOT NULL,
    parameters JSON NOT NULL, -- 정책 파라미터
    performance_metrics JSON, -- 성능 지표
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT FALSE,
    
    INDEX idx_agent_id (agent_id),
    INDEX idx_is_active (is_active),
    UNIQUE KEY uk_agent_name_version (agent_id, name, version)
);
```

#### 5.13.4 RL_EPISODE 테이블

강화 학습 에피소드 정보를 저장합니다.

```sql
CREATE TABLE rl_episode (
    id VARCHAR(36) PRIMARY KEY,
    agent_id VARCHAR(100) NOT NULL, -- 에이전트 식별자
    policy_id VARCHAR(36) NOT NULL, -- 사용된 정책 ID
    start_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    end_time TIMESTAMP NULL,
    total_reward DECIMAL(12,6), -- 총 보상
    steps_count INT DEFAULT 0, -- 단계 수
    success BOOLEAN, -- 성공 여부
    metadata JSON, -- 추가 메타데이터
    
    FOREIGN KEY (policy_id) REFERENCES rl_policy(id) ON DELETE CASCADE,
    INDEX idx_agent_id (agent_id),
    INDEX idx_start_time (start_time)
);
```

#### 5.13.5 RL_ACTION 테이블

강화 학습 에이전트의 행동을 저장합니다.

```sql
CREATE TABLE rl_action (
    id VARCHAR(36) PRIMARY KEY,
    episode_id VARCHAR(36) NOT NULL, -- 에피소드 ID
    state_id VARCHAR(36) NOT NULL, -- 상태 ID
    action_type VARCHAR(100) NOT NULL, -- 행동 유형
    action_details JSON NOT NULL, -- 행동 세부 정보
    step_number INT NOT NULL, -- 에피소드 내 단계 번호
    reward DECIMAL(10,6), -- 해당 행동에 대한 보상
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (episode_id) REFERENCES rl_episode(id) ON DELETE CASCADE,
    FOREIGN KEY (state_id) REFERENCES rl_agent_state(id) ON DELETE CASCADE,
    INDEX idx_episode_id (episode_id),
    INDEX idx_action_type (action_type),
    INDEX idx_step_number (step_number)
);
```

### 5.14 영역 간 지식 전이

#### 5.14.1 KNOWLEDGE_SOURCE 테이블

지식 소스 정보를 저장합니다.

```sql
CREATE TABLE knowledge_source (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    domain VARCHAR(100) NOT NULL, -- 지식 도메인 (예: 의학, 금융, 법률)
    description TEXT,
    connection_info JSON, -- 소스 연결 정보
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    
    INDEX idx_domain (domain),
    INDEX idx_is_active (is_active),
    UNIQUE KEY uk_name (name)
);
```

#### 5.14.2 KNOWLEDGE_MAPPING 테이블

도메인 간 지식 매핑 정보를 저장합니다.

```sql
CREATE TABLE knowledge_mapping (
    id VARCHAR(36) PRIMARY KEY,
    source_concept VARCHAR(255) NOT NULL, -- 소스 개념
    target_concept VARCHAR(255) NOT NULL, -- 타겟 개념
    source_domain VARCHAR(100) NOT NULL, -- 소스 도메인
    target_domain VARCHAR(100) NOT NULL, -- 타겟 도메인
    relation_type VARCHAR(100) NOT NULL, -- 관계 유형 (예: is_equivalent_to, is_similar_to)
    confidence DECIMAL(5,4) NOT NULL, -- 매핑 신뢰도 (0.0000 ~ 1.0000)
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_source_concept (source_concept),
    INDEX idx_target_concept (target_concept),
    INDEX idx_domains (source_domain, target_domain),
    INDEX idx_relation_type (relation_type),
    INDEX idx_confidence (confidence)
);
```

#### 5.14.3 TRANSFER_TASK 테이블

지식 전이 작업 정보를 저장합니다.

```sql
CREATE TABLE transfer_task (
    id VARCHAR(36) PRIMARY KEY,
    source_id VARCHAR(36) NOT NULL, -- 소스 지식 ID
    target_id VARCHAR(36) NOT NULL, -- 타겟 지식 ID
    task_type VARCHAR(100) NOT NULL, -- 작업 유형
    status ENUM('pending', 'running', 'completed', 'failed') DEFAULT 'pending',
    parameters JSON, -- 작업 파라미터
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    started_at TIMESTAMP NULL,
    completed_at TIMESTAMP NULL,
    result JSON, -- 작업 결과
    
    INDEX idx_source_id (source_id),
    INDEX idx_target_id (target_id),
    INDEX idx_task_type (task_type),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
);
```

#### 5.14.4 DOMAIN_ONTOLOGY 테이블

도메인 온톨로지 정보를 저장합니다.

```sql
CREATE TABLE domain_ontology (
    id VARCHAR(36) PRIMARY KEY,
    domain VARCHAR(100) NOT NULL,
    concept VARCHAR(255) NOT NULL,
    definition TEXT NOT NULL,
    relationships JSON, -- 개념 간 관계
    attributes JSON, -- 개념 속성
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_domain (domain),
    INDEX idx_concept (concept),
    UNIQUE KEY uk_domain_concept (domain, concept)
);
```

#### 5.14.5 TRANSFER_METRIC 테이블

지식 전이 성능 지표를 저장합니다.

```sql
CREATE TABLE transfer_metric (
    id VARCHAR(36) PRIMARY KEY,
    transfer_task_id VARCHAR(36) NOT NULL,
    metric_name VARCHAR(100) NOT NULL, -- 지표 이름
    metric_value DECIMAL(10,6) NOT NULL, -- 지표 값
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (transfer_task_id) REFERENCES transfer_task(id) ON DELETE CASCADE,
    INDEX idx_transfer_task_id (transfer_task_id),
    INDEX idx_metric_name (metric_name)
);
```

### 5.15 창의적 생성

#### 5.15.1 CREATIVE_WORK 테이블

창의적 작업 정보를 저장합니다.

```sql
CREATE TABLE creative_work (
    id VARCHAR(36) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type ENUM('text', 'image', 'audio', 'video', 'code', 'mixed') NOT NULL,
    title VARCHAR(255) NOT NULL,
    content_ref VARCHAR(512), -- 콘텐츠 참조 경로 또는 ID
    parameters JSON, -- 생성 파라미터
    metadata JSON, -- 추가 메타데이터
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_type (type),
    INDEX idx_created_at (created_at)
);
```

#### 5.15.2 GENERATION_PROMPT 테이블

창의적 생성에 사용된 프롬프트를 저장합니다.

```sql
CREATE TABLE generation_prompt (
    id VARCHAR(36) PRIMARY KEY,
    work_id VARCHAR(36) NOT NULL,
    prompt_text TEXT NOT NULL,
    prompt_type VARCHAR(50) NOT NULL, -- 프롬프트 유형 (예: text, image, audio)
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (work_id) REFERENCES creative_work(id) ON DELETE CASCADE,
    INDEX idx_work_id (work_id),
    INDEX idx_prompt_type (prompt_type)
);
```

#### 5.15.3 CREATIVE_FEEDBACK 테이블

창의적 작업에 대한 피드백을 저장합니다.

```sql
CREATE TABLE creative_feedback (
    id VARCHAR(36) PRIMARY KEY,
    work_id VARCHAR(36) NOT NULL,
    user_id BIGINT NOT NULL,
    rating INT, -- 평점 (1-5)
    comment TEXT,
    aspects JSON, -- 다양한 측면에 대한 평가 (창의성, 품질 등)
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (work_id) REFERENCES creative_work(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_work_id (work_id),
    INDEX idx_user_id (user_id),
    INDEX idx_rating (rating)
);
```

#### 5.15.4 CREATIVE_VERSION 테이블

창의적 작업의 버전 정보를 저장합니다.

```sql
CREATE TABLE creative_version (
    id VARCHAR(36) PRIMARY KEY,
    work_id VARCHAR(36) NOT NULL,
    version_number INT NOT NULL,
    content_ref VARCHAR(512) NOT NULL, -- 콘텐츠 참조 경로 또는 ID
    changes_description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (work_id) REFERENCES creative_work(id) ON DELETE CASCADE,
    INDEX idx_work_id (work_id),
    UNIQUE KEY uk_work_version (work_id, version_number)
);
```

#### 5.15.5 INSPIRATION_SOURCE 테이블

창의적 작업의 영감 소스를 저장합니다.

```sql
CREATE TABLE inspiration_source (
    id VARCHAR(36) PRIMARY KEY,
    work_id VARCHAR(36) NOT NULL,
    source_type VARCHAR(50) NOT NULL, -- 소스 유형 (예: image, text, url)
    source_ref VARCHAR(512) NOT NULL, -- 소스 참조
    influence_level DECIMAL(5,4), -- 영향도 (0.0000 ~ 1.0000)
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (work_id) REFERENCES creative_work(id) ON DELETE CASCADE,
    INDEX idx_work_id (work_id),
    INDEX idx_source_type (source_type)
);
```

## 6. 데이터베이스 최적화 전략

### 6.1 인덱싱 전략

- **기본 키 인덱스**: 모든 테이블은 기본 키에 대한 인덱스를 자동으로 가짐
- **외래 키 인덱스**: 모든 외래 키에 대한 인덱스 생성
- **검색 필드 인덱스**: 자주 검색되는 필드에 대한 인덱스 생성 (예: 사용자명, 이메일, 상태 등)
- **복합 인덱스**: 자주 함께 검색되는 필드에 대한 복합 인덱스 생성
- **전문 검색 인덱스**: 텍스트 검색이 필요한 필드에 대한 전문 검색 인덱스 생성

### 6.2 파티셔닝 전략

- **로그 테이블**: 시간 기반 파티셔닝 (예: 월별 또는 분기별)
- **대화 및 메시지 테이블**: 사용자 ID 기반 파티셔닝 또는 시간 기반 파티셔닝
- **샌드박스 실행 로그**: 시간 기반 파티셔닝 (일별 또는 주별)

### 6.3 캐싱 전략

- **Redis 캐싱**: 자주 접근하는 데이터(사용자 프로필, 세션, 설정 등)에 대한 Redis 캐싱
- **쿼리 캐싱**: 자주 실행되는 복잡한 쿼리에 대한 결과 캐싱
- **샌드박스 상태 캐싱**: 활성 샌드박스의 상태 정보를 Redis에 캐싱하여 빠른 접근 제공

## 7. 데이터 마이그레이션 및 버전 관리

### 7.1 마이그레이션 도구

- **Flyway**: 데이터베이스 스키마 변경 관리
- **Liquibase**: 대안으로 고려 가능

### 7.2 마이그레이션 전략

- **버전 관리**: 모든 스키마 변경은 버전 관리되며 순차적으로 적용
- **롤백 계획**: 각 마이그레이션에 대한 롤백 스크립트 준비
- **데이터 보존**: 스키마 변경 시 기존 데이터 보존 방안 마련

### 7.3 샌드박스 데이터 관리

- **정기적 정리**: 오래된 샌드박스 인스턴스 및 관련 데이터 자동 정리
- **백업 전략**: 중요 샌드박스 작업 공간에 대한 백업 정책
- **데이터 격리**: 사용자별 샌드박스 데이터 격리 보장

## 8. 보안 고려 사항

### 8.1 데이터 암호화

- **저장 데이터 암호화**: 민감한 정보(비밀번호, API 키 등)에 대한 암호화
- **전송 데이터 암호화**: SSL/TLS를 통한 데이터 전송 암호화

### 8.2 접근 제어

- **역할 기반 접근 제어(RBAC)**: 사용자 역할에 따른 데이터 접근 제어
- **행 수준 보안**: 특정 테이블에 대한 행 수준 접근 제어

### 8.3 감사 추적

- **변경 로깅**: 중요 데이터 변경에 대한 감사 로그 기록
- **접근 로깅**: 민감한 데이터에 대한 접근 로그 기록

### 8.4 샌드박스 보안

- **데이터 격리**: 샌드박스 간 데이터 격리 보장
- **자원 제한**: 샌드박스별 자원 사용량 제한 및 모니터링
- **보안 정책 적용**: 샌드박스별 네트워크, 시스템 콜 등 보안 정책 적용
- **악성 코드 스캔**: 샌드박스 파일에 대한 악성 코드 스캔

## 9. 확장성 고려 사항

### 9.1 수평적 확장

- **읽기/쓰기 분리**: 읽기 전용 복제본을 통한 읽기 작업 분산
- **샤딩**: 대규모 테이블에 대한 샤딩 전략 마련

### 9.2 수직적 확장

- **리소스 최적화**: 데이터베이스 서버 리소스 최적화
- **인덱스 최적화**: 인덱스 사용량 및 성능 모니터링 및 최적화

### 9.3 샌드박스 확장성

- **샌드박스 풀링**: 자주 사용되는 샌드박스 템플릿의 인스턴스 풀 관리
- **자원 할당 최적화**: 사용 패턴에 따른 샌드박스 자원 할당 최적화
- **분산 저장소**: 샌드박스 파일 저장을 위한 분산 저장소 활용
