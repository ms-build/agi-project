-- 통합 AGI 시스템 데이터베이스 스키마 생성 스크립트
-- 생성일: 2025-05-28

-- 데이터베이스 생성
CREATE DATABASE IF NOT EXISTS agi_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE agi_system;

-- 1. 사용자 및 인증 관리 테이블
-- 1.1 USER 테이블
CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    nickname VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP NULL,

    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_is_active (is_active)
);

-- 1.2 ROLE 테이블
CREATE TABLE role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,

    INDEX idx_name (name)
);

-- 1.3 PERMISSION 테이블
CREATE TABLE permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,

    INDEX idx_name (name)
);

-- 1.4 USER_ROLE 테이블
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

-- 1.5 ROLE_PERMISSION 테이블
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

-- 1.6 SESSION 테이블
CREATE TABLE session (
    id VARCHAR(36) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
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

-- 2. 자연어 처리 테이블
-- 2.1 CONVERSATION 테이블
CREATE TABLE conversation (
    id VARCHAR(36) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(200),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    status ENUM('active', 'archived', 'deleted') DEFAULT 'active',
    metadata JSON,
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
);

-- 2.2 MESSAGE 테이블
CREATE TABLE message (
    id VARCHAR(36) PRIMARY KEY,
    conversation_id VARCHAR(36) NOT NULL,
    role ENUM('user', 'assistant', 'system', 'tool') NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    embedding BLOB,
    metadata JSON,
    
    FOREIGN KEY (conversation_id) REFERENCES conversation(id) ON DELETE CASCADE,
    INDEX idx_conversation_id (conversation_id),
    INDEX idx_role (role),
    INDEX idx_created_at (created_at)
);

-- 2.3 INTENT 테이블
CREATE TABLE intent (
    id VARCHAR(36) PRIMARY KEY,
    message_id VARCHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL,
    confidence DECIMAL(5,4) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (message_id) REFERENCES message(id) ON DELETE CASCADE,
    INDEX idx_message_id (message_id),
    INDEX idx_name (name),
    INDEX idx_confidence (confidence)
);

-- 2.4 ENTITY 테이블
CREATE TABLE entity (
    id VARCHAR(36) PRIMARY KEY,
    message_id VARCHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL,
    value TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    start_pos INT,
    end_pos INT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (message_id) REFERENCES message(id) ON DELETE CASCADE,
    INDEX idx_message_id (message_id),
    INDEX idx_name (name),
    INDEX idx_type (type)
);

-- 2.5 SENTIMENT 테이블
CREATE TABLE sentiment (
    id VARCHAR(36) PRIMARY KEY,
    message_id VARCHAR(36) NOT NULL,
    score DECIMAL(5,4) NOT NULL,
    label VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (message_id) REFERENCES message(id) ON DELETE CASCADE,
    INDEX idx_message_id (message_id),
    INDEX idx_label (label)
);

-- 3. 도구 관리 테이블
-- 3.1 TOOL 테이블
CREATE TABLE tool (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    category VARCHAR(50) NOT NULL,
    version VARCHAR(50) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    execution_type VARCHAR(50) NOT NULL,
    parameters_schema JSON,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_category (category),
    INDEX idx_enabled (enabled),
    INDEX idx_execution_type (execution_type)
);

-- 3.2 TOOL_PARAMETER 테이블
CREATE TABLE tool_parameter (
    id VARCHAR(36) PRIMARY KEY,
    tool_id VARCHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    description TEXT,
    required BOOLEAN DEFAULT FALSE,
    default_value TEXT,
    
    FOREIGN KEY (tool_id) REFERENCES tool(id) ON DELETE CASCADE,
    UNIQUE KEY uk_tool_param (tool_id, name),
    INDEX idx_tool_id (tool_id)
);

-- 3.3 TOOL_EXECUTION 테이블
CREATE TABLE tool_execution (
    id VARCHAR(36) PRIMARY KEY,
    tool_id VARCHAR(36) NOT NULL,
    user_id BIGINT NOT NULL,
    parameters JSON,
    result JSON,
    status ENUM('pending', 'running', 'completed', 'failed') NOT NULL,
    started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    error_message TEXT,
    sandbox_id VARCHAR(36),
    
    FOREIGN KEY (tool_id) REFERENCES tool(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_tool_id (tool_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_started_at (started_at)
);

-- 4. 계획 관리 테이블
-- 4.1 PLAN 테이블
CREATE TABLE plan (
    id VARCHAR(36) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    status ENUM('draft', 'active', 'completed', 'cancelled') DEFAULT 'draft',
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
);

-- 4.2 PLAN_STEP 테이블
CREATE TABLE plan_step (
    id VARCHAR(36) PRIMARY KEY,
    plan_id VARCHAR(36) NOT NULL,
    order_index INT NOT NULL,
    description TEXT NOT NULL,
    status ENUM('pending', 'in_progress', 'completed', 'failed', 'skipped') DEFAULT 'pending',
    depends_on VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (plan_id) REFERENCES plan(id) ON DELETE CASCADE,
    INDEX idx_plan_id (plan_id),
    INDEX idx_status (status),
    UNIQUE KEY uk_plan_order (plan_id, order_index)
);

-- 4.3 PLAN_EXECUTION 테이블
CREATE TABLE plan_execution (
    id VARCHAR(36) PRIMARY KEY,
    plan_id VARCHAR(36) NOT NULL,
    user_id BIGINT NOT NULL,
    status ENUM('pending', 'running', 'completed', 'failed', 'cancelled') DEFAULT 'pending',
    started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    result JSON,
    
    FOREIGN KEY (plan_id) REFERENCES plan(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_plan_id (plan_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
);

-- 5. 지식 및 기억 시스템 테이블
-- 5.1 KNOWLEDGE 테이블
CREATE TABLE knowledge (
    id VARCHAR(36) PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    source VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    embedding BLOB,
    metadata JSON,
    
    INDEX idx_title (title),
    INDEX idx_source (source),
    INDEX idx_created_at (created_at)
);

-- 5.2 MEMORY 테이블
CREATE TABLE memory (
    id VARCHAR(36) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    importance DECIMAL(5,4) DEFAULT 0.5000,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_accessed TIMESTAMP NULL,
    embedding BLOB,
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_type (type),
    INDEX idx_importance (importance),
    INDEX idx_created_at (created_at),
    INDEX idx_last_accessed (last_accessed)
);

-- 5.3 CONTEXT 테이블
CREATE TABLE context (
    id VARCHAR(36) PRIMARY KEY,
    session_id VARCHAR(36) NOT NULL,
    data JSON NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (session_id) REFERENCES session(id) ON DELETE CASCADE,
    INDEX idx_session_id (session_id),
    INDEX idx_created_at (created_at)
);

-- 5.4 KNOWLEDGE_TAG 테이블
CREATE TABLE knowledge_tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    knowledge_id VARCHAR(36) NOT NULL,
    tag VARCHAR(100) NOT NULL,
    
    FOREIGN KEY (knowledge_id) REFERENCES knowledge(id) ON DELETE CASCADE,
    INDEX idx_knowledge_id (knowledge_id),
    INDEX idx_tag (tag),
    UNIQUE KEY uk_knowledge_tag (knowledge_id, tag)
);

-- 5.5 MEMORY_RELATION 테이블
CREATE TABLE memory_relation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    source_memory_id VARCHAR(36) NOT NULL,
    target_memory_id VARCHAR(36) NOT NULL,
    relation_type VARCHAR(50) NOT NULL,
    strength DECIMAL(5,4) DEFAULT 0.5000,
    
    FOREIGN KEY (source_memory_id) REFERENCES memory(id) ON DELETE CASCADE,
    FOREIGN KEY (target_memory_id) REFERENCES memory(id) ON DELETE CASCADE,
    INDEX idx_source_memory_id (source_memory_id),
    INDEX idx_target_memory_id (target_memory_id),
    INDEX idx_relation_type (relation_type),
    UNIQUE KEY uk_memory_relation (source_memory_id, target_memory_id, relation_type)
);

-- 6. 멀티모달 데이터 테이블
-- 6.1 IMAGE_METADATA 테이블
CREATE TABLE image_metadata (
    id VARCHAR(36) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    filename VARCHAR(255) NOT NULL,
    path VARCHAR(255) NOT NULL,
    width INT,
    height INT,
    format VARCHAR(20),
    size_bytes BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    embedding BLOB,
    metadata JSON,
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_filename (filename),
    INDEX idx_created_at (created_at)
);

-- 6.2 AUDIO_METADATA 테이블
CREATE TABLE audio_metadata (
    id VARCHAR(36) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    filename VARCHAR(255) NOT NULL,
    path VARCHAR(255) NOT NULL,
    duration INT,
    format VARCHAR(20),
    size_bytes BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    embedding BLOB,
    metadata JSON,
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_filename (filename),
    INDEX idx_created_at (created_at)
);

-- 6.3 VIDEO_METADATA 테이블
CREATE TABLE video_metadata (
    id VARCHAR(36) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    filename VARCHAR(255) NOT NULL,
    path VARCHAR(255) NOT NULL,
    duration INT,
    format VARCHAR(20),
    resolution VARCHAR(20),
    size_bytes BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    embedding BLOB,
    metadata JSON,
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_filename (filename),
    INDEX idx_created_at (created_at)
);

-- 6.4 MEDIA_TAG 테이블
CREATE TABLE media_tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    media_id VARCHAR(36) NOT NULL,
    media_type ENUM('image', 'audio', 'video') NOT NULL,
    tag VARCHAR(100) NOT NULL,
    confidence DECIMAL(5,4) DEFAULT 1.0000,
    
    INDEX idx_media_id_type (media_id, media_type),
    INDEX idx_tag (tag)
);

-- 6.5 MEDIA_OBJECT 테이블
CREATE TABLE media_object (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    media_id VARCHAR(36) NOT NULL,
    media_type ENUM('image', 'video') NOT NULL,
    object_type VARCHAR(100) NOT NULL,
    bounding_box JSON,
    confidence DECIMAL(5,4) DEFAULT 1.0000,
    
    INDEX idx_media_id_type (media_id, media_type),
    INDEX idx_object_type (object_type)
);

-- 7. 학습 및 피드백 테이블
-- 7.1 FEEDBACK 테이블
CREATE TABLE feedback (
    id VARCHAR(36) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    entity_id VARCHAR(36) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    rating INT NOT NULL,
    comment TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_entity_id_type (entity_id, entity_type),
    INDEX idx_rating (rating),
    INDEX idx_created_at (created_at)
);

-- 7.2 LEARNING_DATA 테이블
CREATE TABLE learning_data (
    id VARCHAR(36) PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    input_data JSON NOT NULL,
    output_data JSON NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    metadata JSON,
    
    INDEX idx_type (type),
    INDEX idx_created_at (created_at)
);

-- 7.3 MODEL_VERSION 테이블
CREATE TABLE model_version (
    id VARCHAR(36) PRIMARY KEY,
    model_name VARCHAR(100) NOT NULL,
    version VARCHAR(50) NOT NULL,
    path VARCHAR(255) NOT NULL,
    performance JSON,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT FALSE,
    
    UNIQUE KEY uk_model_version (model_name, version),
    INDEX idx_model_name (model_name),
    INDEX idx_is_active (is_active)
);

-- 7.4 TRAINING_JOB 테이블
CREATE TABLE training_job (
    id VARCHAR(36) PRIMARY KEY,
    model_name VARCHAR(100) NOT NULL,
    status ENUM('pending', 'running', 'completed', 'failed') DEFAULT 'pending',
    parameters JSON,
    started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    result_model_id VARCHAR(36),
    metrics JSON,
    
    INDEX idx_model_name (model_name),
    INDEX idx_status (status),
    INDEX idx_started_at (started_at)
);

-- 7.5 EVALUATION_RESULT 테이블
CREATE TABLE evaluation_result (
    id VARCHAR(36) PRIMARY KEY,
    model_version_id VARCHAR(36) NOT NULL,
    dataset_name VARCHAR(100) NOT NULL,
    metrics JSON NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (model_version_id) REFERENCES model_version(id) ON DELETE CASCADE,
    INDEX idx_model_version_id (model_version_id),
    INDEX idx_dataset_name (dataset_name)
);

-- 8. 시스템 관리 테이블
-- 8.1 SETTING 테이블
CREATE TABLE setting (
    id VARCHAR(36) PRIMARY KEY,
    category VARCHAR(100) NOT NULL,
    key VARCHAR(100) NOT NULL,
    value TEXT NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_category_key (category, key),
    INDEX idx_category (category)
);

-- 8.2 LOG 테이블
CREATE TABLE log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    level VARCHAR(20) NOT NULL,
    message TEXT NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    component VARCHAR(100),
    user_id BIGINT,
    trace_id VARCHAR(36),
    additional_data JSON,
    
    INDEX idx_level (level),
    INDEX idx_timestamp (timestamp),
    INDEX idx_component (component),
    INDEX idx_user_id (user_id),
    INDEX idx_trace_id (trace_id)
);

-- 8.3 MONITORING 테이블
CREATE TABLE monitoring (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    component VARCHAR(100) NOT NULL,
    metric VARCHAR(100) NOT NULL,
    value DECIMAL(20,6) NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_component (component),
    INDEX idx_metric (metric),
    INDEX idx_timestamp (timestamp)
);

-- 8.4 TASK_QUEUE 테이블
CREATE TABLE task_queue (
    id VARCHAR(36) PRIMARY KEY,
    task_type VARCHAR(100) NOT NULL,
    priority INT DEFAULT 0,
    status ENUM('pending', 'processing', 'completed', 'failed') DEFAULT 'pending',
    payload JSON NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    started_at TIMESTAMP NULL,
    completed_at TIMESTAMP NULL,
    result JSON,
    error_message TEXT,
    
    INDEX idx_task_type (task_type),
    INDEX idx_priority (priority),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
);

-- 8.5 HEALTH_CHECK 테이블
CREATE TABLE health_check (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    component VARCHAR(100) NOT NULL,
    status ENUM('healthy', 'degraded', 'unhealthy') NOT NULL,
    details TEXT,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_component (component),
    INDEX idx_status (status),
    INDEX idx_timestamp (timestamp)
);

-- 9. 샌드박스 관리 테이블
-- 9.1 SANDBOX 테이블
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
    config JSON,
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_container_id (container_id),
    INDEX idx_created_at (created_at),
    INDEX idx_expires_at (expires_at)
);

-- 9.2 SANDBOX_WORKSPACE 테이블
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

-- 9.3 SANDBOX_EXECUTION 테이블
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
    resource_usage JSON,
    
    FOREIGN KEY (sandbox_id) REFERENCES sandbox(id) ON DELETE CASCADE,
    INDEX idx_sandbox_id (sandbox_id),
    INDEX idx_started_at (started_at)
);

-- 9.4 SANDBOX_RESOURCE 테이블
CREATE TABLE sandbox_resource (
    id VARCHAR(36) PRIMARY KEY,
    sandbox_id VARCHAR(36) NOT NULL,
    cpu_limit INT,
    memory_limit BIGINT,
    disk_limit BIGINT,
    network_limit BIGINT,
    timeout INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (sandbox_id) REFERENCES sandbox(id) ON DELETE CASCADE,
    UNIQUE KEY uk_sandbox_resource (sandbox_id)
);

-- 9.5 SANDBOX_SECURITY 테이블
CREATE TABLE sandbox_security (
    id VARCHAR(36) PRIMARY KEY,
    sandbox_id VARCHAR(36) NOT NULL,
    network_policy JSON,
    syscall_policy JSON,
    mount_policy JSON,
    env_variables JSON,
    capabilities JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (sandbox_id) REFERENCES sandbox(id) ON DELETE CASCADE,
    UNIQUE KEY uk_sandbox_security (sandbox_id)
);

-- 9.6 SANDBOX_FILE 테이블
CREATE TABLE sandbox_file (
    id VARCHAR(36) PRIMARY KEY,
    sandbox_id VARCHAR(36) NOT NULL,
    path VARCHAR(255) NOT NULL,
    content_hash VARCHAR(64),
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

-- 9.7 SANDBOX_PORT 테이블
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

-- 9.8 SANDBOX_TEMPLATE 테이블
CREATE TABLE sandbox_template (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    image_name VARCHAR(255) NOT NULL,
    image_tag VARCHAR(50) NOT NULL DEFAULT 'latest',
    config JSON,
    resource_config JSON,
    security_config JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    
    UNIQUE KEY uk_template_name (name),
    INDEX idx_is_active (is_active)
);

-- 10. 설명 가능성 테이블
-- 10.1 EXPLANATION 테이블
CREATE TABLE explanation (
    id VARCHAR(36) PRIMARY KEY,
    target_id VARCHAR(36) NOT NULL,
    target_type VARCHAR(50) NOT NULL,
    explanation TEXT NOT NULL,
    algorithm VARCHAR(100),
    confidence DECIMAL(5,4),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_target_id_type (target_id, target_type),
    INDEX idx_algorithm (algorithm),
    INDEX idx_created_at (created_at)
);

-- 10.2 EXPLANATION_FEATURE 테이블
CREATE TABLE explanation_feature (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    explanation_id VARCHAR(36) NOT NULL,
    feature_name VARCHAR(100) NOT NULL,
    feature_value TEXT NOT NULL,
    importance DECIMAL(5,4) NOT NULL,
    
    FOREIGN KEY (explanation_id) REFERENCES explanation(id) ON DELETE CASCADE,
    INDEX idx_explanation_id (explanation_id),
    INDEX idx_feature_name (feature_name),
    INDEX idx_importance (importance)
);

-- 10.3 EXPLANATION_TEMPLATE 테이블
CREATE TABLE explanation_template (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    template_text TEXT NOT NULL,
    target_type VARCHAR(50) NOT NULL,
    complexity_level ENUM('simple', 'moderate', 'detailed') DEFAULT 'moderate',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_name (name),
    INDEX idx_target_type (target_type),
    INDEX idx_complexity_level (complexity_level),
    INDEX idx_is_active (is_active)
);

-- 11. 감성 지능 테이블
-- 11.1 EMOTION_ANALYSIS 테이블
CREATE TABLE emotion_analysis (
    id VARCHAR(36) PRIMARY KEY,
    target_id VARCHAR(36) NOT NULL,
    target_type VARCHAR(50) NOT NULL,
    emotions JSON NOT NULL,
    dominant_emotion VARCHAR(50),
    intensity DECIMAL(5,4),
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_target_id_type (target_id, target_type),
    INDEX idx_dominant_emotion (dominant_emotion),
    INDEX idx_timestamp (timestamp)
);

-- 11.2 EMOTIONAL_RESPONSE 테이블
CREATE TABLE emotional_response (
    id VARCHAR(36) PRIMARY KEY,
    trigger_emotion VARCHAR(50) NOT NULL,
    response_type VARCHAR(100) NOT NULL,
    template TEXT NOT NULL,
    priority INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_trigger_emotion (trigger_emotion),
    INDEX idx_response_type (response_type),
    INDEX idx_priority (priority),
    INDEX idx_is_active (is_active)
);

-- 11.3 USER_EMOTION_HISTORY 테이블
CREATE TABLE user_emotion_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    emotion VARCHAR(50) NOT NULL,
    intensity DECIMAL(5,4) NOT NULL,
    context VARCHAR(255),
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_emotion (emotion),
    INDEX idx_timestamp (timestamp)
);

-- 11.4 EMPATHY_MODEL 테이블
CREATE TABLE empathy_model (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    parameters JSON,
    version VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_name_version (name, version),
    INDEX idx_is_active (is_active)
);

-- 12. 적응형 학습 테이블
-- 12.1 USER_PROFILE 테이블
CREATE TABLE user_profile (
    user_id BIGINT PRIMARY KEY,
    interaction_history JSON,
    knowledge_map JSON,
    learning_style VARCHAR(50),
    proficiency_level VARCHAR(50),
    last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_learning_style (learning_style),
    INDEX idx_proficiency_level (proficiency_level)
);

-- 12.2 LEARNING_PREFERENCE 테이블
CREATE TABLE learning_preference (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    pref_key VARCHAR(100) NOT NULL,
    pref_value VARCHAR(255) NOT NULL,
    confidence DECIMAL(5,4) DEFAULT 0.50,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_pref (user_id, pref_key),
    INDEX idx_pref_key_value (pref_key, pref_value)
);

-- 12.3 ADAPTATION_RULE 테이블
CREATE TABLE adaptation_rule (
    id VARCHAR(36) PRIMARY KEY,
    condition TEXT NOT NULL,
    action TEXT NOT NULL,
    priority INT DEFAULT 0,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_priority (priority),
    INDEX idx_is_active (is_active)
);

-- 12.4 LEARNING_SESSION 테이블
CREATE TABLE learning_session (
    id VARCHAR(36) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    topic VARCHAR(255) NOT NULL,
    start_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    end_time TIMESTAMP NULL,
    duration INT,
    progress DECIMAL(5,2),
    metrics JSON,
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_topic (topic),
    INDEX idx_start_time (start_time)
);

-- 12.5 ADAPTATION_LOG 테이블
CREATE TABLE adaptation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    rule_id VARCHAR(36),
    context JSON NOT NULL,
    adaptation_type VARCHAR(100) NOT NULL,
    adaptation_details JSON NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (rule_id) REFERENCES adaptation_rule(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_adaptation_type (adaptation_type),
    INDEX idx_timestamp (timestamp)
);

-- 13. 강화 학습 테이블
-- 13.1 RL_AGENT_STATE 테이블
CREATE TABLE rl_agent_state (
    id VARCHAR(36) PRIMARY KEY,
    agent_id VARCHAR(100) NOT NULL,
    state_representation JSON NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    metadata JSON,
    
    INDEX idx_agent_id (agent_id),
    INDEX idx_timestamp (timestamp)
);

-- 13.2 REWARD_SIGNAL 테이블
CREATE TABLE reward_signal (
    id VARCHAR(36) PRIMARY KEY,
    agent_id VARCHAR(100) NOT NULL,
    trigger_id VARCHAR(36) NOT NULL,
    trigger_type VARCHAR(50) NOT NULL,
    reward_value DECIMAL(10,6) NOT NULL,
    reward_source VARCHAR(100) NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    context JSON,
    
    INDEX idx_agent_id (agent_id),
    INDEX idx_trigger_id_type (trigger_id, trigger_type),
    INDEX idx_reward_source (reward_source),
    INDEX idx_timestamp (timestamp)
);

-- 13.3 RL_POLICY 테이블
CREATE TABLE rl_policy (
    id VARCHAR(36) PRIMARY KEY,
    agent_id VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    version VARCHAR(50) NOT NULL,
    parameters JSON NOT NULL,
    performance_metrics JSON,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT FALSE,
    
    INDEX idx_agent_id (agent_id),
    INDEX idx_is_active (is_active),
    UNIQUE KEY uk_agent_name_version (agent_id, name, version)
);

-- 13.4 RL_EPISODE 테이블
CREATE TABLE rl_episode (
    id VARCHAR(36) PRIMARY KEY,
    agent_id VARCHAR(100) NOT NULL,
    policy_id VARCHAR(36) NOT NULL,
    start_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    end_time TIMESTAMP NULL,
    total_reward DECIMAL(12,6),
    steps_count INT DEFAULT 0,
    success BOOLEAN,
    metadata JSON,
    
    FOREIGN KEY (policy_id) REFERENCES rl_policy(id) ON DELETE CASCADE,
    INDEX idx_agent_id (agent_id),
    INDEX idx_start_time (start_time)
);

-- 13.5 RL_ACTION 테이블
CREATE TABLE rl_action (
    id VARCHAR(36) PRIMARY KEY,
    episode_id VARCHAR(36) NOT NULL,
    state_id VARCHAR(36) NOT NULL,
    action_type VARCHAR(100) NOT NULL,
    action_details JSON NOT NULL,
    step_number INT NOT NULL,
    reward DECIMAL(10,6),
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (episode_id) REFERENCES rl_episode(id) ON DELETE CASCADE,
    FOREIGN KEY (state_id) REFERENCES rl_agent_state(id) ON DELETE CASCADE,
    INDEX idx_episode_id (episode_id),
    INDEX idx_action_type (action_type),
    INDEX idx_step_number (step_number)
);

-- 14. 영역 간 지식 전이 테이블
-- 14.1 KNOWLEDGE_SOURCE 테이블
CREATE TABLE knowledge_source (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    domain VARCHAR(100) NOT NULL,
    description TEXT,
    connection_info JSON,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    
    INDEX idx_domain (domain),
    INDEX idx_is_active (is_active),
    UNIQUE KEY uk_name (name)
);

-- 14.2 KNOWLEDGE_MAPPING 테이블
CREATE TABLE knowledge_mapping (
    id VARCHAR(36) PRIMARY KEY,
    source_concept VARCHAR(255) NOT NULL,
    target_concept VARCHAR(255) NOT NULL,
    relation_type VARCHAR(100) NOT NULL,
    confidence DECIMAL(5,4) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_source_concept (source_concept),
    INDEX idx_target_concept (target_concept),
    INDEX idx_relation_type (relation_type),
    INDEX idx_confidence (confidence)
);

-- 14.3 TRANSFER_TASK 테이블
CREATE TABLE transfer_task (
    id VARCHAR(36) PRIMARY KEY,
    source_id VARCHAR(36) NOT NULL,
    target_id VARCHAR(36) NOT NULL,
    status ENUM('pending', 'running', 'completed', 'failed') DEFAULT 'pending',
    parameters JSON,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    result JSON,
    
    INDEX idx_source_id (source_id),
    INDEX idx_target_id (target_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
);

-- 14.4 DOMAIN_ONTOLOGY 테이블
CREATE TABLE domain_ontology (
    id VARCHAR(36) PRIMARY KEY,
    domain VARCHAR(100) NOT NULL,
    ontology_data JSON NOT NULL,
    version VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    
    UNIQUE KEY uk_domain_version (domain, version),
    INDEX idx_domain (domain),
    INDEX idx_is_active (is_active)
);

-- 14.5 TRANSFER_METRIC 테이블
CREATE TABLE transfer_metric (
    id VARCHAR(36) PRIMARY KEY,
    transfer_task_id VARCHAR(36) NOT NULL,
    metric_name VARCHAR(100) NOT NULL,
    metric_value DECIMAL(10,6) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (transfer_task_id) REFERENCES transfer_task(id) ON DELETE CASCADE,
    INDEX idx_transfer_task_id (transfer_task_id),
    INDEX idx_metric_name (metric_name)
);

-- 15. 창의적 생성 테이블
-- 15.1 CREATIVE_WORK 테이블
CREATE TABLE creative_work (
    id VARCHAR(36) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    content_ref VARCHAR(255),
    parameters JSON,
    metadata JSON,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_type (type),
    INDEX idx_created_at (created_at)
);

-- 15.2 GENERATION_PROMPT 테이블
CREATE TABLE generation_prompt (
    id VARCHAR(36) PRIMARY KEY,
    work_id VARCHAR(36) NOT NULL,
    prompt_text TEXT NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (work_id) REFERENCES creative_work(id) ON DELETE CASCADE,
    INDEX idx_work_id (work_id)
);

-- 15.3 CREATIVE_FEEDBACK 테이블
CREATE TABLE creative_feedback (
    id VARCHAR(36) PRIMARY KEY,
    work_id VARCHAR(36) NOT NULL,
    user_id BIGINT NOT NULL,
    rating INT NOT NULL,
    comment TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (work_id) REFERENCES creative_work(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_work_id (work_id),
    INDEX idx_user_id (user_id),
    INDEX idx_rating (rating)
);

-- 15.4 CREATIVE_VERSION 테이블
CREATE TABLE creative_version (
    id VARCHAR(36) PRIMARY KEY,
    work_id VARCHAR(36) NOT NULL,
    version_number INT NOT NULL,
    content_ref VARCHAR(255) NOT NULL,
    changes_description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (work_id) REFERENCES creative_work(id) ON DELETE CASCADE,
    UNIQUE KEY uk_work_version (work_id, version_number),
    INDEX idx_work_id (work_id)
);

-- 15.5 INSPIRATION_SOURCE 테이블
CREATE TABLE inspiration_source (
    id VARCHAR(36) PRIMARY KEY,
    work_id VARCHAR(36) NOT NULL,
    source_type VARCHAR(50) NOT NULL,
    source_ref VARCHAR(255) NOT NULL,
    influence_level DECIMAL(5,4) DEFAULT 0.5000,
    description TEXT,
    
    FOREIGN KEY (work_id) REFERENCES creative_work(id) ON DELETE CASCADE,
    INDEX idx_work_id (work_id),
    INDEX idx_source_type (source_type),
    INDEX idx_influence_level (influence_level)
);
