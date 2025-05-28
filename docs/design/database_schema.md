# 통합 AGI 시스템 데이터베이스 스키마 설계

## 1. 개요

이 문서는 Spring Boot 3.4.5, Java 17, MySQL 8 기반의 통합 AGI 시스템을 위한 데이터베이스 스키마 설계를 설명합니다. 데이터베이스 설계는 자연어 처리, 도구 사용, 계획 수립, 지식 및 기억 관리, 멀티모달 처리, 자가 학습 등 다양한 기능을 지원하기 위한 테이블 구조와 관계를 정의합니다.

## 2. 데이터베이스 설계 원칙

1. **정규화**: 데이터 중복을 최소화하고 데이터 무결성을 보장하기 위해 적절한 정규화 수준 유지
2. **성능 최적화**: 자주 사용되는 쿼리에 대한 인덱스 설계 및 필요 시 전략적 비정규화
3. **확장성**: 데이터 증가에 대비한 파티셔닝 및 샤딩 전략 고려
4. **유연성**: 새로운 기능 및 요구사항 변화에 대응할 수 있는 유연한 스키마 설계
5. **보안**: 민감한 데이터에 대한 암호화 및 접근 제어 고려

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

## 4. 엔티티 관계 다이어그램 (ERD)

```
+----------------+       +----------------+       +----------------+
|     USER       |       |      ROLE      |       |   PERMISSION   |
+----------------+       +----------------+       +----------------+
| PK: id         |<----->| PK: id         |<----->| PK: id         |
| username       |       | name           |       | name           |
| email          |       | description    |       | description    |
| password_hash  |       |                |       |                |
| created_at     |       |                |       |                |
| updated_at     |       |                |       |                |
+----------------+       +----------------+       +----------------+
        |
        |
+----------------+       +----------------+       +----------------+
|    SESSION     |       | CONVERSATION   |       |    MESSAGE     |
+----------------+       +----------------+       +----------------+
| PK: id         |<------| PK: id         |<------| PK: id         |
| FK: user_id    |       | FK: user_id    |       | FK: conv_id    |
| token          |       | title          |       | content        |
| expires_at     |       | created_at     |       | role           |
| last_active    |       | updated_at     |       | created_at     |
+----------------+       | status         |       | embedding      |
                         +----------------+       +----------------+
                                 |
                                 |
+----------------+       +----------------+       +----------------+
|    INTENT      |       |     ENTITY     |       |   SENTIMENT    |
+----------------+       +----------------+       +----------------+
| PK: id         |<------| PK: id         |       | PK: id         |
| FK: message_id |       | FK: message_id |<------| FK: message_id |
| name           |       | name           |       | score          |
| confidence     |       | value          |       | label          |
+----------------+       | type           |       +----------------+
                         +----------------+

+----------------+       +----------------+       +----------------+
|     TOOL       |       | TOOL_PARAMETER |       | TOOL_EXECUTION |
+----------------+       +----------------+       +----------------+
| PK: id         |<------| PK: id         |       | PK: id         |
| name           |       | FK: tool_id    |       | FK: tool_id    |
| description    |       | name           |       | FK: user_id    |
| category       |       | type           |<------| parameters     |
| version        |       | required       |       | result         |
| enabled        |       | default_value  |       | status         |
+----------------+       +----------------+       | created_at     |
                                                  | completed_at   |
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
```

## 5. 테이블 상세 설계

### 5.1 사용자 및 인증 관리

#### 5.1.1 USER 테이블

사용자 정보를 저장합니다.

```sql
CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    profile_image_url VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP,
    
    INDEX idx_username (username),
    INDEX idx_email (email)
);
```

#### 5.1.2 ROLE 테이블

사용자 역할 정보를 저장합니다.

```sql
CREATE TABLE role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### 5.1.3 USER_ROLE 테이블

사용자와 역할 간의 다대다 관계를 관리합니다.

```sql
CREATE TABLE user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_role (user_id, role_id)
);
```

#### 5.1.4 PERMISSION 테이블

시스템 권한 정보를 저장합니다.

```sql
CREATE TABLE permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### 5.1.5 ROLE_PERMISSION 테이블

역할과 권한 간의 다대다 관계를 관리합니다.

```sql
CREATE TABLE role_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permission(id) ON DELETE CASCADE,
    UNIQUE KEY uk_role_permission (role_id, permission_id)
);
```

#### 5.1.6 SESSION 테이블

사용자 세션 정보를 저장합니다.

```sql
CREATE TABLE session (
    id VARCHAR(36) PRIMARY KEY,
    user_id BIGINT,
    token VARCHAR(255) NOT NULL,
    ip_address VARCHAR(45),
    user_agent VARCHAR(255),
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_active TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
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
    user_id BIGINT,
    title VARCHAR(255),
    status ENUM('active', 'archived', 'deleted') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    metadata JSON,
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE SET NULL,
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
    role ENUM('user', 'system', 'assistant', 'tool') NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    embedding BLOB,
    metadata JSON,
    
    FOREIGN KEY (conversation_id) REFERENCES conversation(id) ON DELETE CASCADE,
    INDEX idx_conversation_id (conversation_id),
    INDEX idx_created_at (created_at)
);
```

#### 5.2.3 INTENT 테이블

메시지에서 감지된 사용자 의도를 저장합니다.

```sql
CREATE TABLE intent (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    message_id VARCHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL,
    confidence DECIMAL(5,4) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (message_id) REFERENCES message(id) ON DELETE CASCADE,
    INDEX idx_message_id (message_id),
    INDEX idx_name (name)
);
```

#### 5.2.4 ENTITY 테이블

메시지에서 추출된 엔티티를 저장합니다.

```sql
CREATE TABLE entity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    message_id VARCHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL,
    value VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    start_pos INT,
    end_pos INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (message_id) REFERENCES message(id) ON DELETE CASCADE,
    INDEX idx_message_id (message_id),
    INDEX idx_type (type)
);
```

#### 5.2.5 SENTIMENT 테이블

메시지의 감정 분석 결과를 저장합니다.

```sql
CREATE TABLE sentiment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    message_id VARCHAR(36) NOT NULL,
    score DECIMAL(5,4) NOT NULL,
    label ENUM('positive', 'negative', 'neutral') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (message_id) REFERENCES message(id) ON DELETE CASCADE,
    INDEX idx_message_id (message_id)
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
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    icon_url VARCHAR(255),
    documentation_url VARCHAR(255),
    
    INDEX idx_category (category),
    INDEX idx_enabled (enabled)
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
    type ENUM('string', 'number', 'boolean', 'array', 'object') NOT NULL,
    required BOOLEAN DEFAULT FALSE,
    default_value VARCHAR(255),
    validation_regex VARCHAR(255),
    
    FOREIGN KEY (tool_id) REFERENCES tool(id) ON DELETE CASCADE,
    UNIQUE KEY uk_tool_parameter (tool_id, name)
);
```

#### 5.3.3 TOOL_EXECUTION 테이블

도구 실행 기록을 저장합니다.

```sql
CREATE TABLE tool_execution (
    id VARCHAR(36) PRIMARY KEY,
    tool_id VARCHAR(36) NOT NULL,
    user_id BIGINT,
    session_id VARCHAR(36),
    parameters JSON,
    result JSON,
    status ENUM('pending', 'running', 'completed', 'failed') NOT NULL,
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    
    FOREIGN KEY (tool_id) REFERENCES tool(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE SET NULL,
    FOREIGN KEY (session_id) REFERENCES session(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
);
```

### 5.4 계획 관리

#### 5.4.1 PLAN 테이블

작업 계획을 저장합니다.

```sql
CREATE TABLE plan (
    id VARCHAR(36) PRIMARY KEY,
    user_id BIGINT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    goal TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    status ENUM('draft', 'active', 'completed', 'failed', 'cancelled') DEFAULT 'draft',
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
);
```

#### 5.4.2 PLAN_STEP 테이블

계획의 개별 단계를 저장합니다.

```sql
CREATE TABLE plan_step (
    id VARCHAR(36) PRIMARY KEY,
    plan_id VARCHAR(36) NOT NULL,
    order_index INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status ENUM('pending', 'in_progress', 'completed', 'failed', 'skipped') DEFAULT 'pending',
    depends_on VARCHAR(255),
    estimated_duration INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (plan_id) REFERENCES plan(id) ON DELETE CASCADE,
    INDEX idx_plan_id (plan_id),
    INDEX idx_status (status)
);
```

#### 5.4.3 PLAN_EXECUTION 테이블

계획 실행 기록을 저장합니다.

```sql
CREATE TABLE plan_execution (
    id VARCHAR(36) PRIMARY KEY,
    plan_id VARCHAR(36) NOT NULL,
    user_id BIGINT,
    status ENUM('pending', 'running', 'completed', 'failed', 'cancelled') DEFAULT 'pending',
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    result JSON,
    error_message TEXT,
    
    FOREIGN KEY (plan_id) REFERENCES plan(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE SET NULL,
    INDEX idx_plan_id (plan_id),
    INDEX idx_status (status)
);
```

#### 5.4.4 STEP_EXECUTION 테이블

계획 단계 실행 기록을 저장합니다.

```sql
CREATE TABLE step_execution (
    id VARCHAR(36) PRIMARY KEY,
    plan_execution_id VARCHAR(36) NOT NULL,
    plan_step_id VARCHAR(36) NOT NULL,
    status ENUM('pending', 'in_progress', 'completed', 'failed', 'skipped') DEFAULT 'pending',
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    result JSON,
    error_message TEXT,
    
    FOREIGN KEY (plan_execution_id) REFERENCES plan_execution(id) ON DELETE CASCADE,
    FOREIGN KEY (plan_step_id) REFERENCES plan_step(id) ON DELETE CASCADE,
    INDEX idx_plan_execution_id (plan_execution_id),
    INDEX idx_status (status)
);
```

### 5.5 지식 및 기억 시스템

#### 5.5.1 KNOWLEDGE 테이블

시스템의 지식 베이스를 저장합니다.

```sql
CREATE TABLE knowledge (
    id VARCHAR(36) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    source VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    embedding BLOB,
    metadata JSON,
    
    INDEX idx_title (title),
    INDEX idx_source (source),
    INDEX idx_created_at (created_at)
);
```

#### 5.5.2 KNOWLEDGE_RELATION 테이블

지식 항목 간의 관계를 저장합니다.

```sql
CREATE TABLE knowledge_relation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    source_id VARCHAR(36) NOT NULL,
    target_id VARCHAR(36) NOT NULL,
    relation_type VARCHAR(50) NOT NULL,
    weight DECIMAL(5,4),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (source_id) REFERENCES knowledge(id) ON DELETE CASCADE,
    FOREIGN KEY (target_id) REFERENCES knowledge(id) ON DELETE CASCADE,
    INDEX idx_source_id (source_id),
    INDEX idx_target_id (target_id),
    INDEX idx_relation_type (relation_type)
);
```

#### 5.5.3 MEMORY 테이블

시스템의 메모리(기억)를 저장합니다.

```sql
CREATE TABLE memory (
    id VARCHAR(36) PRIMARY KEY,
    user_id BIGINT,
    type VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    importance DECIMAL(5,4) DEFAULT 0.5,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_accessed TIMESTAMP,
    embedding BLOB,
    metadata JSON,
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_type (type),
    INDEX idx_importance (importance),
    INDEX idx_created_at (created_at),
    INDEX idx_last_accessed (last_accessed)
);
```

#### 5.5.4 CONTEXT 테이블

대화 및 작업 컨텍스트를 저장합니다.

```sql
CREATE TABLE context (
    id VARCHAR(36) PRIMARY KEY,
    session_id VARCHAR(36) NOT NULL,
    data JSON NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (session_id) REFERENCES session(id) ON DELETE CASCADE,
    INDEX idx_session_id (session_id),
    INDEX idx_created_at (created_at)
);
```

### 5.6 멀티모달 데이터

#### 5.6.1 IMAGE_METADATA 테이블

이미지 메타데이터를 저장합니다.

```sql
CREATE TABLE image_metadata (
    id VARCHAR(36) PRIMARY KEY,
    user_id BIGINT,
    filename VARCHAR(255) NOT NULL,
    path VARCHAR(255) NOT NULL,
    width INT,
    height INT,
    format VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    embedding BLOB,
    metadata JSON,
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_filename (filename),
    INDEX idx_created_at (created_at)
);
```

#### 5.6.2 AUDIO_METADATA 테이블

오디오 메타데이터를 저장합니다.

```sql
CREATE TABLE audio_metadata (
    id VARCHAR(36) PRIMARY KEY,
    user_id BIGINT,
    filename VARCHAR(255) NOT NULL,
    path VARCHAR(255) NOT NULL,
    duration INT,
    format VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    embedding BLOB,
    metadata JSON,
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_filename (filename),
    INDEX idx_created_at (created_at)
);
```

#### 5.6.3 VIDEO_METADATA 테이블

비디오 메타데이터를 저장합니다.

```sql
CREATE TABLE video_metadata (
    id VARCHAR(36) PRIMARY KEY,
    user_id BIGINT,
    filename VARCHAR(255) NOT NULL,
    path VARCHAR(255) NOT NULL,
    duration INT,
    format VARCHAR(20),
    resolution VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    embedding BLOB,
    metadata JSON,
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_filename (filename),
    INDEX idx_created_at (created_at)
);
```

#### 5.6.4 MEDIA_OBJECT 테이블

멀티모달 객체 간의 관계를 저장합니다.

```sql
CREATE TABLE media_object (
    id VARCHAR(36) PRIMARY KEY,
    object_id VARCHAR(36) NOT NULL,
    object_type VARCHAR(50) NOT NULL,
    parent_id VARCHAR(36),
    parent_type VARCHAR(50),
    relation_type VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    metadata JSON,
    
    INDEX idx_object_id_type (object_id, object_type),
    INDEX idx_parent_id_type (parent_id, parent_type),
    INDEX idx_relation_type (relation_type)
);
```

### 5.7 학습 및 피드백

#### 5.7.1 FEEDBACK 테이블

사용자 피드백을 저장합니다.

```sql
CREATE TABLE feedback (
    id VARCHAR(36) PRIMARY KEY,
    user_id BIGINT,
    entity_id VARCHAR(36) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    rating INT,
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    metadata JSON,
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_entity_id_type (entity_id, entity_type),
    INDEX idx_rating (rating),
    INDEX idx_created_at (created_at)
);
```

#### 5.7.2 LEARNING_DATA 테이블

학습 데이터를 저장합니다.

```sql
CREATE TABLE learning_data (
    id VARCHAR(36) PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    input_data JSON NOT NULL,
    output_data JSON NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    metadata JSON,
    
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
    version VARCHAR(20) NOT NULL,
    path VARCHAR(255) NOT NULL,
    performance JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT FALSE,
    metadata JSON,
    
    UNIQUE KEY uk_model_version (model_name, version),
    INDEX idx_model_name (model_name),
    INDEX idx_is_active (is_active),
    INDEX idx_created_at (created_at)
);
```

#### 5.7.4 TRAINING_JOB 테이블

모델 훈련 작업을 저장합니다.

```sql
CREATE TABLE training_job (
    id VARCHAR(36) PRIMARY KEY,
    model_name VARCHAR(100) NOT NULL,
    status ENUM('pending', 'running', 'completed', 'failed', 'cancelled') DEFAULT 'pending',
    configuration JSON NOT NULL,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    result_model_version_id VARCHAR(36),
    error_message TEXT,
    
    FOREIGN KEY (result_model_version_id) REFERENCES model_version(id) ON DELETE SET NULL,
    INDEX idx_model_name (model_name),
    INDEX idx_status (status),
    INDEX idx_started_at (started_at)
);
```

### 5.8 설명 가능성 (Explainability)

#### 5.8.1 EXPLANATION 테이블

시스템 결정 및 행동에 대한 설명을 저장합니다.

```sql
CREATE TABLE explanation (
    id VARCHAR(36) PRIMARY KEY,
    target_entity_id VARCHAR(36) NOT NULL,
    target_entity_type VARCHAR(50) NOT NULL,
    explanation_text TEXT NOT NULL,
    explanation_data JSON,
    algorithm_used VARCHAR(100),
    confidence_score DECIMAL(5,4),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_target_entity (target_entity_id, target_entity_type),
    INDEX idx_algorithm (algorithm_used),
    INDEX idx_created_at (created_at)
);
```

#### 5.8.2 EXPLANATION_FEATURE 테이블

설명에 사용된 주요 특성을 저장합니다.

```sql
CREATE TABLE explanation_feature (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    explanation_id VARCHAR(36) NOT NULL,
    feature_name VARCHAR(100) NOT NULL,
    feature_value VARCHAR(255),
    importance DECIMAL(10,6) NOT NULL,
    
    FOREIGN KEY (explanation_id) REFERENCES explanation(id) ON DELETE CASCADE,
    INDEX idx_explanation_id (explanation_id),
    INDEX idx_importance (importance)
);
```

### 5.9 감성 지능 (Emotional Intelligence)

#### 5.9.1 EMOTION_ANALYSIS 테이블

감정 분석 결과를 저장합니다.

```sql
CREATE TABLE emotion_analysis (
    id VARCHAR(36) PRIMARY KEY,
    target_entity_id VARCHAR(36) NOT NULL,
    target_entity_type VARCHAR(50) NOT NULL,
    detected_emotions JSON NOT NULL,
    dominant_emotion VARCHAR(50),
    analysis_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_target_entity (target_entity_id, target_entity_type),
    INDEX idx_dominant_emotion (dominant_emotion),
    INDEX idx_analysis_timestamp (analysis_timestamp)
);
```

#### 5.9.2 EMOTIONAL_RESPONSE_STRATEGY 테이블

감정 기반 응답 전략을 저장합니다.

```sql
CREATE TABLE emotional_response_strategy (
    id VARCHAR(36) PRIMARY KEY,
    trigger_emotion VARCHAR(50) NOT NULL,
    response_type VARCHAR(50) NOT NULL,
    response_template TEXT,
    priority INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_trigger_emotion (trigger_emotion),
    INDEX idx_response_type (response_type),
    INDEX idx_priority (priority),
    INDEX idx_is_active (is_active)
);
```

### 5.10 적응형 학습 (Adaptive Learning)

#### 5.10.1 USER_PROFILE 테이블

사용자 학습 프로필을 저장합니다.

```sql
CREATE TABLE user_profile (
    user_id BIGINT PRIMARY KEY,
    interaction_summary JSON,
    knowledge_map JSON,
    last_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_last_updated_at (last_updated_at)
);
```

#### 5.10.2 LEARNING_PREFERENCE 테이블

사용자 학습 선호도를 저장합니다.

```sql
CREATE TABLE learning_preference (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    preference_key VARCHAR(100) NOT NULL,
    preference_value VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES user_profile(user_id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_preference (user_id, preference_key),
    INDEX idx_preference_key (preference_key)
);
```

#### 5.10.3 ADAPTATION_RULE 테이블

콘텐츠 적응 규칙을 저장합니다.

```sql
CREATE TABLE adaptation_rule (
    id VARCHAR(36) PRIMARY KEY,
    condition_expression TEXT NOT NULL,
    action_expression TEXT NOT NULL,
    priority INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_priority (priority),
    INDEX idx_is_active (is_active)
);
```

### 5.11 강화 학습 (Reinforcement Learning)

#### 5.11.1 RL_AGENT_STATE 테이블

강화 학습 에이전트 상태를 저장합니다.

```sql
CREATE TABLE rl_agent_state (
    id VARCHAR(36) PRIMARY KEY,
    agent_id VARCHAR(100) NOT NULL,
    state_representation BLOB NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_agent_id (agent_id),
    INDEX idx_timestamp (timestamp)
);
```

#### 5.11.2 REWARD_SIGNAL 테이블

보상 신호를 저장합니다.

```sql
CREATE TABLE reward_signal (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    trigger_entity_id VARCHAR(36) NOT NULL,
    trigger_entity_type VARCHAR(50) NOT NULL,
    reward_value DOUBLE NOT NULL,
    reward_source VARCHAR(100),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_trigger_entity (trigger_entity_id, trigger_entity_type),
    INDEX idx_reward_source (reward_source),
    INDEX idx_timestamp (timestamp)
);
```

#### 5.11.3 RL_POLICY 테이블

강화 학습 정책을 저장합니다.

```sql
CREATE TABLE rl_policy (
    id VARCHAR(36) PRIMARY KEY,
    policy_name VARCHAR(100) NOT NULL,
    version VARCHAR(20) NOT NULL,
    model_version_id VARCHAR(36),
    parameters JSON,
    is_active BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (model_version_id) REFERENCES model_version(id) ON DELETE SET NULL,
    UNIQUE KEY uk_policy_version (policy_name, version),
    INDEX idx_policy_name (policy_name),
    INDEX idx_is_active (is_active),
    INDEX idx_created_at (created_at)
);
```

### 5.12 영역 간 지식 전이 (Cross-domain Knowledge Transfer)

#### 5.12.1 KNOWLEDGE_SOURCE 테이블

지식 소스를 저장합니다.

```sql
CREATE TABLE knowledge_source (
    id VARCHAR(36) PRIMARY KEY,
    source_name VARCHAR(100) NOT NULL,
    domain VARCHAR(100) NOT NULL,
    description TEXT,
    connection_info JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_source_name (source_name),
    INDEX idx_domain (domain)
);
```

#### 5.12.2 KNOWLEDGE_MAPPING 테이블

개념 간 매핑을 저장합니다.

```sql
CREATE TABLE knowledge_mapping (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    source_concept VARCHAR(255) NOT NULL,
    target_concept VARCHAR(255) NOT NULL,
    relation_type VARCHAR(50) NOT NULL,
    confidence_score DECIMAL(5,4),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_source_concept (source_concept),
    INDEX idx_target_concept (target_concept),
    INDEX idx_relation_type (relation_type)
);
```

#### 5.12.3 TRANSFER_LEARNING_TASK 테이블

전이 학습 작업을 저장합니다.

```sql
CREATE TABLE transfer_learning_task (
    id VARCHAR(36) PRIMARY KEY,
    source_domain_id VARCHAR(36) NOT NULL,
    target_domain_id VARCHAR(36) NOT NULL,
    status ENUM('pending', 'running', 'completed', 'failed', 'cancelled') DEFAULT 'pending',
    configuration JSON,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    result JSON,
    error_message TEXT,
    
    INDEX idx_source_domain_id (source_domain_id),
    INDEX idx_target_domain_id (target_domain_id),
    INDEX idx_status (status),
    INDEX idx_started_at (started_at)
);
```

### 5.13 창의적 생성 (Creative Generation)

#### 5.13.1 CREATIVE_WORK 테이블

생성된 창의적 작업을 저장합니다.

```sql
CREATE TABLE creative_work (
    id VARCHAR(36) PRIMARY KEY,
    user_id BIGINT,
    type ENUM('TEXT', 'IMAGE', 'AUDIO', 'VIDEO', 'CODE', 'MUSIC') NOT NULL,
    content_reference TEXT NOT NULL,
    generation_parameters JSON,
    metadata JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_type (type),
    INDEX idx_created_at (created_at)
);
```

#### 5.13.2 GENERATION_PROMPT 테이블

생성에 사용된 프롬프트를 저장합니다.

```sql
CREATE TABLE generation_prompt (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    creative_work_id VARCHAR(36) NOT NULL,
    prompt_text TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (creative_work_id) REFERENCES creative_work(id) ON DELETE CASCADE,
    INDEX idx_creative_work_id (creative_work_id),
    INDEX idx_timestamp (timestamp)
);
```

### 5.14 시스템 관리

#### 5.14.1 SETTING 테이블

시스템 설정을 저장합니다.

```sql
CREATE TABLE setting (
    id VARCHAR(36) PRIMARY KEY,
    category VARCHAR(50) NOT NULL,
    key VARCHAR(100) NOT NULL,
    value TEXT NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_category_key (category, key),
    INDEX idx_category (category)
);
```

#### 5.14.2 LOG 테이블

시스템 로그를 저장합니다.

```sql
CREATE TABLE log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    level VARCHAR(20) NOT NULL,
    message TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    component VARCHAR(100),
    user_id BIGINT,
    
    INDEX idx_level (level),
    INDEX idx_timestamp (timestamp),
    INDEX idx_component (component),
    INDEX idx_user_id (user_id)
);
```

#### 5.14.3 MONITORING 테이블

시스템 모니터링 데이터를 저장합니다.

```sql
CREATE TABLE monitoring (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    component VARCHAR(100) NOT NULL,
    metric VARCHAR(100) NOT NULL,
    value DOUBLE NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_component (component),
    INDEX idx_metric (metric),
    INDEX idx_timestamp (timestamp)
);
```

## 6. 데이터베이스 최적화 전략

### 6.1 인덱스 전략

- 자주 조회되는 필드에 인덱스 적용
- 복합 인덱스를 통한 쿼리 최적화
- 전체 텍스트 검색을 위한 인덱스 구성
- 인덱스 사용 모니터링 및 주기적 최적화

### 6.2 파티셔닝 전략

- 대용량 테이블(로그, 모니터링, 메시지 등)에 대한 시간 기반 파티셔닝
- 사용자 ID 기반 샤딩 고려
- 파티션 관리 자동화 스크립트 구현

### 6.3 캐싱 전략

- 자주 접근하는 데이터에 대한 Redis 캐싱
- 세션 데이터 캐싱
- 임베딩 벡터 캐싱
- 캐시 무효화 전략 구현

### 6.4 백업 및 복구 전략

- 일일 전체 백업
- 시간별 증분 백업
- 지리적 복제 구성
- 복구 절차 문서화 및 정기 테스트

## 7. 마이그레이션 및 버전 관리

### 7.1 마이그레이션 도구

- Flyway 또는 Liquibase를 사용한 데이터베이스 마이그레이션 관리
- 버전 관리된 마이그레이션 스크립트
- 롤백 전략 구현

### 7.2 버전 관리 전략

- 시맨틱 버저닝 적용
- 마이그레이션 스크립트 명명 규칙 정의
- 변경 로그 유지

### 7.3 데이터 마이그레이션 전략

- 대용량 데이터 마이그레이션을 위한 배치 처리
- 다운타임 최소화 전략
- 데이터 검증 절차

## 8. 보안 고려사항

### 8.1 데이터 암호화

- 민감한 사용자 데이터 암호화 저장
- 전송 중 데이터 암호화 (TLS/SSL)
- 암호화 키 관리 전략

### 8.2 접근 제어

- 데이터베이스 사용자 권한 최소화
- 역할 기반 접근 제어
- 감사 로깅 구현

### 8.3 개인정보 보호

- GDPR 및 기타 개인정보 보호법 준수
- 개인 식별 정보 관리 전략
- 데이터 삭제 및 익명화 절차

## 9. 결론

이 데이터베이스 스키마 설계는 통합 AGI 시스템의 다양한 기능을 지원하기 위한 기반을 제공합니다. 설계는 확장성, 성능, 보안을 고려하여 작성되었으며, 시스템의 진화에 따라 지속적으로 개선될 수 있습니다. 실제 구현 시에는 데이터 볼륨, 사용 패턴, 성능 요구사항에 따라 추가적인 최적화가 필요할 수 있습니다.
