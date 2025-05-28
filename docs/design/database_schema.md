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
    step_id VARCHAR(36) NOT NULL,
    status ENUM('pending', 'running', 'completed', 'failed', 'skipped') DEFAULT 'pending',
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    result JSON,
    error_message TEXT,
    
    FOREIGN KEY (plan_execution_id) REFERENCES plan_execution(id) ON DELETE CASCADE,
    FOREIGN KEY (step_id) REFERENCES plan_step(id) ON DELETE CASCADE,
    INDEX idx_plan_execution_id (plan_execution_id),
    INDEX idx_step_id (step_id)
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
    source_url VARCHAR(255),
    category VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    embedding BLOB,
    metadata JSON,
    
    FULLTEXT INDEX ft_content (content),
    INDEX idx_category (category),
    INDEX idx_created_at (created_at)
);
```

#### 5.5.2 MEMORY 테이블

시스템의 메모리(단기 및 장기)를 저장합니다.

```sql
CREATE TABLE memory (
    id VARCHAR(36) PRIMARY KEY,
    user_id BIGINT,
    type ENUM('short_term', 'long_term', 'episodic', 'semantic') NOT NULL,
    content TEXT NOT NULL,
    importance DECIMAL(3,2) DEFAULT 0.5,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_accessed TIMESTAMP,
    expiry_at TIMESTAMP,
    embedding BLOB,
    metadata JSON,
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_type (type),
    INDEX idx_importance (importance),
    INDEX idx_last_accessed (last_accessed),
    INDEX idx_expiry_at (expiry_at)
);
```

#### 5.5.3 CONTEXT 테이블

대화 및 작업 컨텍스트를 저장합니다.

```sql
CREATE TABLE context (
    id VARCHAR(36) PRIMARY KEY,
    session_id VARCHAR(36) NOT NULL,
    data JSON NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    
    FOREIGN KEY (session_id) REFERENCES session(id) ON DELETE CASCADE,
    INDEX idx_session_id (session_id),
    INDEX idx_expires_at (expires_at)
);
```

#### 5.5.4 KNOWLEDGE_RELATION 테이블

지식 항목 간의 관계를 저장합니다.

```sql
CREATE TABLE knowledge_relation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    source_id VARCHAR(36) NOT NULL,
    target_id VARCHAR(36) NOT NULL,
    relation_type VARCHAR(50) NOT NULL,
    weight DECIMAL(3,2) DEFAULT 1.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (source_id) REFERENCES knowledge(id) ON DELETE CASCADE,
    FOREIGN KEY (target_id) REFERENCES knowledge(id) ON DELETE CASCADE,
    INDEX idx_source_id (source_id),
    INDEX idx_target_id (target_id),
    INDEX idx_relation_type (relation_type)
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
    mime_type VARCHAR(100) NOT NULL,
    width INT,
    height INT,
    size_bytes BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    embedding BLOB,
    metadata JSON,
    tags JSON,
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
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
    mime_type VARCHAR(100) NOT NULL,
    duration_seconds INT,
    sample_rate INT,
    channels INT,
    size_bytes BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    embedding BLOB,
    metadata JSON,
    transcription TEXT,
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
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
    mime_type VARCHAR(100) NOT NULL,
    duration_seconds INT,
    width INT,
    height INT,
    fps DECIMAL(5,2),
    size_bytes BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    embedding BLOB,
    metadata JSON,
    transcription TEXT,
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
);
```

#### 5.6.4 MEDIA_OBJECT 테이블

이미지 내 감지된 객체를 저장합니다.

```sql
CREATE TABLE media_object (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    media_id VARCHAR(36) NOT NULL,
    media_type ENUM('image', 'video') NOT NULL,
    object_type VARCHAR(100) NOT NULL,
    confidence DECIMAL(5,4) NOT NULL,
    x_min INT,
    y_min INT,
    x_max INT,
    y_max INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_media_id (media_id),
    INDEX idx_media_type (media_type),
    INDEX idx_object_type (object_type)
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
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_entity_id_type (entity_id, entity_type),
    INDEX idx_rating (rating)
);
```

#### 5.7.2 LEARNING_DATA 테이블

모델 학습 데이터를 저장합니다.

```sql
CREATE TABLE learning_data (
    id VARCHAR(36) PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    input_data JSON NOT NULL,
    output_data JSON NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    source VARCHAR(50),
    quality_score DECIMAL(3,2),
    metadata JSON,
    
    INDEX idx_type (type),
    INDEX idx_created_at (created_at),
    INDEX idx_quality_score (quality_score)
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
    description TEXT,
    performance_metrics JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT FALSE,
    created_by VARCHAR(100),
    
    UNIQUE KEY uk_model_version (model_name, version),
    INDEX idx_model_name (model_name),
    INDEX idx_is_active (is_active)
);
```

#### 5.7.4 TRAINING_JOB 테이블

모델 학습 작업을 저장합니다.

```sql
CREATE TABLE training_job (
    id VARCHAR(36) PRIMARY KEY,
    model_name VARCHAR(100) NOT NULL,
    configuration JSON NOT NULL,
    status ENUM('pending', 'running', 'completed', 'failed') DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    result_model_version_id VARCHAR(36),
    error_message TEXT,
    
    INDEX idx_model_name (model_name),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
);
```

### 5.8 시스템 관리

#### 5.8.1 SETTING 테이블

시스템 설정을 저장합니다.

```sql
CREATE TABLE setting (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category VARCHAR(50) NOT NULL,
    key VARCHAR(100) NOT NULL,
    value TEXT NOT NULL,
    description TEXT,
    is_encrypted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_category_key (category, key)
);
```

#### 5.8.2 LOG 테이블

시스템 로그를 저장합니다.

```sql
CREATE TABLE log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    level ENUM('trace', 'debug', 'info', 'warn', 'error', 'fatal') NOT NULL,
    message TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    component VARCHAR(100),
    user_id BIGINT,
    session_id VARCHAR(36),
    request_id VARCHAR(36),
    stack_trace TEXT,
    
    INDEX idx_level (level),
    INDEX idx_timestamp (timestamp),
    INDEX idx_component (component),
    INDEX idx_user_id (user_id)
);
```

#### 5.8.3 MONITORING 테이블

시스템 모니터링 데이터를 저장합니다.

```sql
CREATE TABLE monitoring (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    component VARCHAR(100) NOT NULL,
    metric VARCHAR(100) NOT NULL,
    value DECIMAL(20,4) NOT NULL,
    unit VARCHAR(20),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_component (component),
    INDEX idx_metric (metric),
    INDEX idx_timestamp (timestamp)
);
```

## 6. 데이터베이스 최적화 전략

### 6.1 인덱스 전략

1. **기본 인덱스**: 모든 외래 키, 자주 검색되는 필드에 인덱스 적용
2. **복합 인덱스**: 자주 함께 사용되는 필드에 복합 인덱스 적용
3. **전문 검색 인덱스**: 텍스트 검색이 필요한 필드에 FULLTEXT 인덱스 적용
4. **인덱스 모니터링**: 인덱스 사용 패턴을 모니터링하고 필요에 따라 조정

### 6.2 파티셔닝 전략

1. **시간 기반 파티셔닝**: 로그, 메시지 등 시간 기반 데이터에 적용
2. **범위 파티셔닝**: 사용자 ID 등 범위 기반 데이터에 적용
3. **해시 파티셔닝**: 고르게 분산이 필요한 데이터에 적용

### 6.3 캐싱 전략

1. **애플리케이션 캐시**: 자주 사용되는 데이터를 애플리케이션 메모리에 캐싱
2. **Redis 캐시**: 세션, 토큰, 자주 사용되는 데이터를 Redis에 캐싱
3. **쿼리 캐시**: 자주 실행되는 복잡한 쿼리 결과를 캐싱

### 6.4 데이터 보관 정책

1. **아카이빙**: 오래된 데이터를 별도의 아카이브 테이블로 이동
2. **삭제**: 불필요한 데이터를 정기적으로 삭제
3. **압축**: 자주 사용되지 않는 데이터를 압축하여 저장

## 7. 데이터 마이그레이션 및 버전 관리

### 7.1 마이그레이션 도구

1. **Flyway**: 데이터베이스 스키마 변경 관리
2. **Liquibase**: 복잡한 데이터베이스 변경 관리

### 7.2 마이그레이션 전략

1. **점진적 마이그레이션**: 대규모 변경을 작은 단계로 나누어 적용
2. **다운타임 최소화**: 마이그레이션 중 다운타임을 최소화하는 전략 적용
3. **롤백 계획**: 문제 발생 시 롤백할 수 있는 계획 수립

### 7.3 버전 관리

1. **스키마 버전 관리**: 데이터베이스 스키마 버전을 명시적으로 관리
2. **변경 이력 관리**: 모든 스키마 변경 이력을 문서화
3. **호환성 유지**: 이전 버전과의 호환성을 고려한 변경 관리

## 8. 보안 고려사항

### 8.1 데이터 암호화

1. **저장 데이터 암호화**: 민감한 데이터는 저장 시 암호화
2. **전송 데이터 암호화**: 데이터베이스 연결 시 SSL/TLS 사용
3. **암호화 키 관리**: 암호화 키를 안전하게 관리

### 8.2 접근 제어

1. **최소 권한 원칙**: 필요한 최소한의 권한만 부여
2. **역할 기반 접근 제어**: 역할에 따른 접근 권한 관리
3. **감사 로깅**: 데이터베이스 접근 및 변경 로깅

### 8.3 개인정보 보호

1. **개인정보 식별**: 개인정보를 명확히 식별하고 관리
2. **익명화**: 필요 시 데이터 익명화 처리
3. **삭제 정책**: 개인정보 삭제 정책 수립 및 적용

## 9. 결론

이 데이터베이스 스키마 설계는 Spring Boot 3.4.5, Java 17, MySQL 8 기반의 통합 AGI 시스템을 위한 기본 구조를 제공합니다. 사용자 관리, 자연어 처리, 도구 사용, 계획 수립, 지식 및 기억 관리, 멀티모달 처리, 자가 학습 등 다양한 기능을 지원하는 테이블 구조와 관계를 정의했습니다. 이 설계는 확장성, 성능, 보안을 고려하여 작성되었으며, 필요에 따라 추가적인 최적화와 조정이 가능합니다.
