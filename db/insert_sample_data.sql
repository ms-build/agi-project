-- 통합 AGI 시스템 샘플 데이터 삽입 스크립트
-- 생성일: 2025-05-28

USE agi_system;

-- 1. 사용자 및 인증 관리 테이블 샘플 데이터
-- 1.1 USER 테이블
INSERT INTO user (id, username, password_hash, email, nickname, is_active, created_at, last_login_at) VALUES
(1, 'admin', '$2a$10$hKDVYxLefVHV/vV76Nc.3OC4UuWbJ9TqQ.BZKW1TqpocOQ/d.hpO2', 'admin@agi-system.com', '관리자', TRUE, '2025-01-01 00:00:00', '2025-05-28 09:00:00'),
(2, 'user1', '$2a$10$6oNrHdY5U9xliAYuaXK85uvTunwKsrqxTY4KakyFZ.KRF5KlR8m0a', 'user1@example.com', '사용자1', TRUE, '2025-01-15 10:30:00', '2025-05-27 14:20:00'),
(3, 'user2', '$2a$10$9XdQnGzJ3vn1wOBst2WZi.JR.jH8wuF52G6PhSr8HzjLUHdBgr/3S', 'user2@example.com', '사용자2', TRUE, '2025-02-10 15:45:00', '2025-05-28 11:15:00');

-- 1.2 ROLE 테이블
INSERT INTO role (id, name, description) VALUES
(1, 'ROLE_ADMIN', '시스템 관리자 역할'),
(2, 'ROLE_USER', '일반 사용자 역할'),
(3, 'ROLE_DEVELOPER', '개발자 역할');

-- 1.3 PERMISSION 테이블
INSERT INTO permission (id, name, description) VALUES
(1, 'READ_USER', '사용자 정보 조회 권한'),
(2, 'WRITE_USER', '사용자 정보 수정 권한'),
(3, 'READ_SANDBOX', '샌드박스 조회 권한'),
(4, 'WRITE_SANDBOX', '샌드박스 생성 및 수정 권한'),
(5, 'ADMIN_SYSTEM', '시스템 관리 권한');

-- 1.4 USER_ROLE 테이블
INSERT INTO user_role (user_id, role_id) VALUES
(1, 1), -- admin은 ROLE_ADMIN
(1, 2), -- admin은 ROLE_USER도 가짐
(2, 2), -- user1은 ROLE_USER
(3, 2), -- user2는 ROLE_USER
(3, 3); -- user2는 ROLE_DEVELOPER도 가짐

-- 1.5 ROLE_PERMISSION 테이블
INSERT INTO role_permission (role_id, permission_id) VALUES
(1, 1), -- ROLE_ADMIN은 READ_USER 권한
(1, 2), -- ROLE_ADMIN은 WRITE_USER 권한
(1, 3), -- ROLE_ADMIN은 READ_SANDBOX 권한
(1, 4), -- ROLE_ADMIN은 WRITE_SANDBOX 권한
(1, 5), -- ROLE_ADMIN은 ADMIN_SYSTEM 권한
(2, 1), -- ROLE_USER는 READ_USER 권한
(2, 3), -- ROLE_USER는 READ_SANDBOX 권한
(3, 1), -- ROLE_DEVELOPER는 READ_USER 권한
(3, 3), -- ROLE_DEVELOPER는 READ_SANDBOX 권한
(3, 4); -- ROLE_DEVELOPER는 WRITE_SANDBOX 권한

-- 1.6 SESSION 테이블
INSERT INTO session (id, user_id, token, ip_address, user_agent, created_at, last_active, expires_at) VALUES
('f47ac10b-58cc-4372-a567-0e02b2c3d479', 1, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxIiwibmFtZSI6ImFkbWluIiwiaWF0IjoxNjE2MTUzODM5fQ.4JmvV1qdcYjJXAdYBNLM7A', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', '2025-05-28 09:00:00', '2025-05-28 10:30:00', '2025-05-29 09:00:00'),
('a1b2c3d4-e5f6-4a5b-9c8d-7e6f5a4b3c2d', 2, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIyIiwibmFtZSI6InVzZXIxIiwiaWF0IjoxNjE2MTUzODQwfQ.5KmvW2qdcYjJXAdYBNLM7B', '192.168.1.101', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36', '2025-05-27 14:20:00', '2025-05-27 16:45:00', '2025-05-28 14:20:00');

-- 2. 자연어 처리 테이블 샘플 데이터
-- 2.1 CONVERSATION 테이블
INSERT INTO conversation (id, user_id, title, created_at, status, metadata) VALUES
('conv-2025-05-28-001', 2, '날씨 정보 문의', '2025-05-28 10:15:00', 'active', '{"model": "gpt-6", "temperature": 0.7}'),
('conv-2025-05-28-002', 3, '프로젝트 계획 도움', '2025-05-28 11:30:00', 'active', '{"model": "gpt-6", "temperature": 0.5, "context": "project_planning"}');

-- 2.2 MESSAGE 테이블
INSERT INTO message (id, conversation_id, role, content, created_at, metadata) VALUES
('msg-2025-05-28-001', 'conv-2025-05-28-001', 'user', '오늘 서울의 날씨는 어때?', '2025-05-28 10:15:10', '{"sentiment": "neutral", "intent": "weather_query"}'),
('msg-2025-05-28-002', 'conv-2025-05-28-001', 'assistant', '서울의 현재 날씨는 맑고 기온은 24°C입니다. 오후에는 소나기가 내릴 가능성이 있으니 우산을 챙기시는 것이 좋겠습니다.', '2025-05-28 10:15:15', '{"source": "weather_api", "confidence": 0.95}'),
('msg-2025-05-28-003', 'conv-2025-05-28-002', 'user', '새로운 웹 개발 프로젝트 계획을 세우는데 도와줄래?', '2025-05-28 11:30:10', '{"sentiment": "positive", "intent": "project_planning"}'),
('msg-2025-05-28-004', 'conv-2025-05-28-002', 'assistant', '물론이죠! 웹 개발 프로젝트 계획을 세우기 위해 몇 가지 정보가 필요합니다. 1. 프로젝트의 목적과 주요 기능은 무엇인가요? 2. 예상 개발 기간은 어느 정도인가요? 3. 사용할 기술 스택에 대한 선호도가 있으신가요?', '2025-05-28 11:30:20', '{"confidence": 0.92}');

-- 2.3 INTENT 테이블
INSERT INTO intent (id, message_id, name, confidence, created_at) VALUES
('intent-001', 'msg-2025-05-28-001', 'weather_query', 0.95, '2025-05-28 10:15:11'),
('intent-002', 'msg-2025-05-28-003', 'project_planning', 0.88, '2025-05-28 11:30:11');

-- 2.4 ENTITY 테이블
INSERT INTO entity (id, message_id, name, value, type, start_pos, end_pos, created_at) VALUES
('entity-001', 'msg-2025-05-28-001', 'location', '서울', 'city', 3, 5, '2025-05-28 10:15:11'),
('entity-002', 'msg-2025-05-28-001', 'time', '오늘', 'date', 0, 2, '2025-05-28 10:15:11'),
('entity-003', 'msg-2025-05-28-003', 'project_type', '웹 개발', 'development_category', 4, 8, '2025-05-28 11:30:11');

-- 2.5 SENTIMENT 테이블
INSERT INTO sentiment (id, message_id, score, label, created_at) VALUES
('sentiment-001', 'msg-2025-05-28-001', 0.5, 'neutral', '2025-05-28 10:15:11'),
('sentiment-002', 'msg-2025-05-28-003', 0.7, 'positive', '2025-05-28 11:30:11');

-- 3. 도구 관리 테이블 샘플 데이터
-- 3.1 TOOL 테이블
INSERT INTO tool (id, name, description, category, version, enabled, execution_type, parameters_schema, created_at) VALUES
('tool-001', 'weather_lookup', '특정 위치의 날씨 정보를 조회하는 도구', 'information', '1.0.0', TRUE, 'api', '{"type":"object","properties":{"location":{"type":"string","description":"날씨를 조회할 위치"},"date":{"type":"string","description":"날씨를 조회할 날짜(선택사항)"}}}', '2025-01-10 09:00:00'),
('tool-002', 'code_generator', '프로그래밍 언어로 코드를 생성하는 도구', 'development', '1.2.0', TRUE, 'sandbox', '{"type":"object","properties":{"language":{"type":"string","description":"프로그래밍 언어"},"task":{"type":"string","description":"수행할 작업 설명"},"constraints":{"type":"string","description":"제약 조건(선택사항)"}}}', '2025-02-15 14:30:00');

-- 3.2 TOOL_PARAMETER 테이블
INSERT INTO tool_parameter (id, tool_id, name, type, description, required, default_value) VALUES
('param-001', 'tool-001', 'location', 'string', '날씨를 조회할 위치(도시명 또는 좌표)', TRUE, NULL),
('param-002', 'tool-001', 'date', 'string', '날씨를 조회할 날짜(YYYY-MM-DD 형식)', FALSE, 'today'),
('param-003', 'tool-002', 'language', 'string', '코드를 생성할 프로그래밍 언어', TRUE, NULL),
('param-004', 'tool-002', 'task', 'string', '코드가 수행해야 할 작업에 대한 설명', TRUE, NULL),
('param-005', 'tool-002', 'constraints', 'string', '코드 생성 시 고려해야 할 제약 조건', FALSE, NULL);

-- 3.3 TOOL_EXECUTION 테이블
INSERT INTO tool_execution (id, tool_id, user_id, parameters, result, status, started_at, completed_at, sandbox_id) VALUES
('exec-001', 'tool-001', 2, '{"location":"서울","date":"2025-05-28"}', '{"temperature":24,"condition":"sunny","humidity":60,"wind_speed":5.2,"precipitation_chance":30}', 'completed', '2025-05-28 10:15:12', '2025-05-28 10:15:14', NULL),
('exec-002', 'tool-002', 3, '{"language":"java","task":"사용자 등록 API를 위한 Spring Boot Controller 클래스 작성"}', '{"code":"@RestController\\n@RequestMapping(\\\"/api/users\\\")\\npublic class UserController {\\n    @Autowired\\n    private UserService userService;\\n\\n    @PostMapping\\n    public ResponseEntity<UserDto> registerUser(@RequestBody UserRegistrationRequest request) {\\n        UserDto createdUser = userService.registerUser(request);\\n        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);\\n    }\\n}"}', 'completed', '2025-05-28 11:35:00', '2025-05-28 11:35:10', 'sandbox-001');

-- 4. 계획 관리 테이블 샘플 데이터
-- 4.1 PLAN 테이블
INSERT INTO plan (id, user_id, title, description, created_at, status) VALUES
('plan-001', 2, '날씨 앱 개발 계획', '사용자 위치 기반 날씨 정보를 제공하는 모바일 앱 개발 계획', '2025-05-20 09:30:00', 'active'),
('plan-002', 3, '데이터 분석 보고서 작성', '지난 분기 판매 데이터 분석 및 인사이트 도출 보고서 작성', '2025-05-25 14:00:00', 'active');

-- 4.2 PLAN_STEP 테이블
INSERT INTO plan_step (id, plan_id, order_index, description, status, depends_on) VALUES
('step-001', 'plan-001', 0, '요구사항 분석 및 기능 정의', 'completed', NULL),
('step-002', 'plan-001', 1, 'UI/UX 디자인', 'in_progress', 'step-001'),
('step-003', 'plan-001', 2, '백엔드 API 개발', 'pending', 'step-001'),
('step-004', 'plan-001', 3, '프론트엔드 개발', 'pending', 'step-002,step-003'),
('step-005', 'plan-002', 0, '데이터 수집 및 전처리', 'completed', NULL),
('step-006', 'plan-002', 1, '데이터 분석 및 시각화', 'in_progress', 'step-005'),
('step-007', 'plan-002', 2, '인사이트 도출 및 보고서 작성', 'pending', 'step-006');

-- 4.3 PLAN_EXECUTION 테이블
INSERT INTO plan_execution (id, plan_id, user_id, status, started_at, completed_at, result) VALUES
('planexec-001', 'plan-001', 2, 'running', '2025-05-27 10:00:00', NULL, NULL),
('planexec-002', 'plan-002', 3, 'running', '2025-05-26 15:30:00', NULL, NULL);

-- 5. 지식 및 기억 시스템 테이블 샘플 데이터
-- 5.1 KNOWLEDGE 테이블
INSERT INTO knowledge (id, title, content, source, created_at, metadata) VALUES
('knowledge-001', 'Spring Boot 기본 구조', 'Spring Boot는 스프링 프레임워크를 기반으로 한 자바 웹 애플리케이션 개발 도구로, 주요 구성 요소는 다음과 같습니다: 1. 컨트롤러(Controller): 클라이언트 요청을 처리하고 응답을 반환합니다. 2. 서비스(Service): 비즈니스 로직을 처리합니다. 3. 레포지토리(Repository): 데이터베이스 접근을 담당합니다. 4. 엔티티(Entity): 데이터베이스 테이블과 매핑되는 객체입니다.', 'https://spring.io/guides', '2025-01-15 10:00:00', '{"category":"programming","tags":["java","spring","web-development"]}'),
('knowledge-002', '효과적인 데이터 시각화 방법', '데이터 시각화는 복잡한 데이터를 이해하기 쉽게 표현하는 기술입니다. 효과적인 데이터 시각화를 위한 주요 원칙은 다음과 같습니다: 1. 목적 명확화: 전달하고자 하는 메시지를 명확히 합니다. 2. 적절한 차트 선택: 데이터 유형과 목적에 맞는 차트를 선택합니다. 3. 단순화: 불필요한 요소를 제거하고 핵심에 집중합니다. 4. 색상 활용: 색상을 전략적으로 사용하여 중요한 정보를 강조합니다.', 'Data Visualization Handbook, 2024 Edition', '2025-03-10 14:30:00', '{"category":"data-science","tags":["visualization","data-analysis","reporting"]}');

-- 5.2 MEMORY 테이블
INSERT INTO memory (id, user_id, type, content, importance, created_at, last_accessed) VALUES
('memory-001', 2, 'preference', '사용자는 파란색 테마를 선호하며, 기술 관련 주제에 관심이 많음', 0.7500, '2025-04-10 09:15:00', '2025-05-28 10:15:00'),
('memory-002', 2, 'interaction', '사용자는 날씨 정보를 자주 요청하며, 특히 서울 지역의 날씨에 관심이 많음', 0.8000, '2025-05-01 11:30:00', '2025-05-28 10:15:00'),
('memory-003', 3, 'preference', '사용자는 코드 예제를 포함한 상세한 설명을 선호함', 0.8500, '2025-04-15 16:45:00', '2025-05-28 11:35:00');

-- 5.3 CONTEXT 테이블
INSERT INTO context (id, session_id, data, created_at) VALUES
('context-001', 'f47ac10b-58cc-4372-a567-0e02b2c3d479', '{"current_task":"system_monitoring","recent_topics":["server_performance","database_optimization"],"user_state":"focused"}', '2025-05-28 09:30:00'),
('context-002', 'a1b2c3d4-e5f6-4a5b-9c8d-7e6f5a4b3c2d', '{"current_task":"weather_inquiry","recent_topics":["seoul_weather","umbrella_recommendation"],"user_state":"curious"}', '2025-05-27 14:30:00');

-- 5.4 KNOWLEDGE_TAG 테이블
INSERT INTO knowledge_tag (id, knowledge_id, tag) VALUES
(1, 'knowledge-001', 'java'),
(2, 'knowledge-001', 'spring'),
(3, 'knowledge-001', 'web-development'),
(4, 'knowledge-002', 'data-visualization'),
(5, 'knowledge-002', 'data-analysis');

-- 5.5 MEMORY_RELATION 테이블
INSERT INTO memory_relation (id, source_memory_id, target_memory_id, relation_type, strength) VALUES
(1, 'memory-001', 'memory-002', 'associated', 0.6500),
(2, 'memory-002', 'memory-003', 'contrasts', 0.4000);

-- 6. 멀티모달 데이터 테이블 샘플 데이터
-- 6.1 IMAGE_METADATA 테이블
INSERT INTO image_metadata (id, user_id, filename, path, width, height, format, size_bytes, created_at, metadata) VALUES
('img-001', 2, 'seoul_weather_map.jpg', '/storage/images/2025/05/seoul_weather_map.jpg', 1920, 1080, 'jpg', 2457600, '2025-05-28 10:20:00', '{"description":"서울 지역 날씨 지도","tags":["weather","map","seoul"],"source":"weather_service"}'),
('img-002', 3, 'project_architecture.png', '/storage/images/2025/05/project_architecture.png', 2560, 1440, 'png', 3686400, '2025-05-28 11:40:00', '{"description":"웹 프로젝트 아키텍처 다이어그램","tags":["architecture","diagram","web-development"],"source":"user_generated"}');

-- 6.2 AUDIO_METADATA 테이블
INSERT INTO audio_metadata (id, user_id, filename, path, duration, format, size_bytes, created_at, metadata) VALUES
('audio-001', 2, 'meeting_recording.mp3', '/storage/audio/2025/05/meeting_recording.mp3', 1800, 'mp3', 21600000, '2025-05-27 15:00:00', '{"description":"프로젝트 킥오프 미팅 녹음","participants":["user1","admin"],"topics":["project_planning","task_assignment"]}'),
('audio-002', 3, 'voice_memo.wav', '/storage/audio/2025/05/voice_memo.wav', 120, 'wav', 10584000, '2025-05-28 09:45:00', '{"description":"개발 아이디어 음성 메모","topics":["feature_ideas","implementation_notes"]}');

-- 6.3 VIDEO_METADATA 테이블
INSERT INTO video_metadata (id, user_id, filename, path, duration, format, resolution, size_bytes, created_at, metadata) VALUES
('video-001', 2, 'app_demo.mp4', '/storage/video/2025/05/app_demo.mp4', 300, 'mp4', '1920x1080', 450000000, '2025-05-26 16:30:00', '{"description":"날씨 앱 데모 영상","tags":["demo","app","weather"],"source":"screen_recording"}'),
('video-002', 3, 'tutorial.webm', '/storage/video/2025/05/tutorial.webm', 900, 'webm', '1280x720', 810000000, '2025-05-27 11:20:00', '{"description":"Spring Boot 개발 튜토리얼","tags":["tutorial","spring-boot","java"],"source":"screen_recording"}');

-- 6.4 MEDIA_TAG 테이블
INSERT INTO media_tag (id, media_id, media_type, tag, confidence) VALUES
(1, 'img-001', 'image', 'weather', 0.9500),
(2, 'img-001', 'image', 'map', 0.9800),
(3, 'img-001', 'image', 'seoul', 0.9700),
(4, 'img-002', 'image', 'architecture', 0.9200),
(5, 'video-001', 'video', 'demo', 0.8800),
(6, 'video-001', 'video', 'weather', 0.9100),
(7, 'video-002', 'video', 'tutorial', 0.9600);

-- 6.5 MEDIA_OBJECT 테이블
INSERT INTO media_object (id, media_id, media_type, object_type, bounding_box, confidence) VALUES
(1, 'img-001', 'image', 'map', '{"x":10,"y":15,"width":1900,"height":1050}', 0.9800),
(2, 'img-001', 'image', 'weather_icon', '{"x":500,"y":300,"width":50,"height":50}', 0.9200),
(3, 'img-002', 'image', 'diagram', '{"x":5,"y":10,"width":2550,"height":1420}', 0.9700),
(4, 'video-001', 'video', 'person', '{"x":800,"y":400,"width":300,"height":600}', 0.8500),
(5, 'video-001', 'video', 'mobile_device', '{"x":600,"y":500,"width":200,"height":400}', 0.9000);

-- 7. 학습 및 피드백 테이블 샘플 데이터
-- 7.1 FEEDBACK 테이블
INSERT INTO feedback (id, user_id, entity_id, entity_type, rating, comment, created_at) VALUES
('feedback-001', 2, 'msg-2025-05-28-002', 'message', 5, '정확한 날씨 정보와 우산 챙기라는 조언이 유용했어요', '2025-05-28 10:16:00'),
('feedback-002', 3, 'msg-2025-05-28-004', 'message', 4, '질문이 명확해서 좋았지만, 기술 스택 추천도 해주면 더 좋을 것 같아요', '2025-05-28 11:31:00'),
('feedback-003', 3, 'exec-002', 'tool_execution', 5, '정확하게 원하는 코드를 생성해줬어요', '2025-05-28 11:36:00');

-- 7.2 LEARNING_DATA 테이블
INSERT INTO learning_data (id, type, input_data, output_data, created_at, metadata) VALUES
('learning-001', 'conversation', '{"messages":[{"role":"user","content":"오늘 서울의 날씨는 어때?"},{"role":"assistant","content":"서울의 현재 날씨는 맑고 기온은 24°C입니다. 오후에는 소나기가 내릴 가능성이 있으니 우산을 챙기시는 것이 좋겠습니다."}]}', '{"quality_score":0.92,"user_satisfaction":5,"entities_extracted":["서울","오늘"]}', '2025-05-28 10:17:00', '{"source":"user_feedback","learning_category":"response_quality"}'),
('learning-002', 'tool_execution', '{"tool":"code_generator","parameters":{"language":"java","task":"사용자 등록 API를 위한 Spring Boot Controller 클래스 작성"}}', '{"execution_time":10,"code_quality":0.95,"user_satisfaction":5}', '2025-05-28 11:37:00', '{"source":"execution_metrics","learning_category":"tool_performance"}');

-- 7.3 MODEL_VERSION 테이블
INSERT INTO model_version (id, model_name, version, path, performance, created_at, is_active) VALUES
('model-001', 'conversation-model', '1.0.0', '/models/conversation/v1.0.0/', '{"accuracy":0.92,"latency":120,"memory_usage":2.4}', '2025-01-01 00:00:00', FALSE),
('model-002', 'conversation-model', '1.1.0', '/models/conversation/v1.1.0/', '{"accuracy":0.94,"latency":110,"memory_usage":2.2}', '2025-03-15 00:00:00', TRUE),
('model-003', 'code-generation-model', '1.0.0', '/models/code-generation/v1.0.0/', '{"accuracy":0.90,"latency":200,"memory_usage":3.5}', '2025-02-01 00:00:00', TRUE);

-- 7.4 TRAINING_JOB 테이블
INSERT INTO training_job (id, model_name, status, parameters, started_at, completed_at, result_model_id, metrics) VALUES
('training-001', 'conversation-model', 'completed', '{"epochs":100,"batch_size":64,"learning_rate":0.001,"dataset":"conversation_dataset_v3"}', '2025-03-10 10:00:00', '2025-03-14 18:30:00', 'model-002', '{"final_loss":0.05,"validation_accuracy":0.94,"training_time":435600}'),
('training-002', 'code-generation-model', 'completed', '{"epochs":150,"batch_size":32,"learning_rate":0.0005,"dataset":"code_dataset_v2"}', '2025-01-25 09:00:00', '2025-01-31 22:15:00', 'model-003', '{"final_loss":0.08,"validation_accuracy":0.90,"training_time":522900}'),
('training-003', 'conversation-model', 'running', '{"epochs":120,"batch_size":64,"learning_rate":0.0008,"dataset":"conversation_dataset_v4"}', '2025-05-27 08:00:00', NULL, NULL, NULL);

-- 7.5 EVALUATION_RESULT 테이블
INSERT INTO evaluation_result (id, model_version_id, dataset_name, metrics, created_at) VALUES
('eval-001', 'model-002', 'conversation_test_set_v1', '{"accuracy":0.94,"precision":0.92,"recall":0.95,"f1_score":0.93,"latency_ms":110}', '2025-03-15 01:00:00'),
('eval-002', 'model-003', 'code_test_set_v1', '{"accuracy":0.90,"precision":0.88,"recall":0.92,"f1_score":0.90,"latency_ms":200}', '2025-02-01 01:00:00');

-- 8. 시스템 관리 테이블 샘플 데이터
-- 8.1 SETTING 테이블
INSERT INTO setting (id, category, key, value, description) VALUES
('setting-001', 'system', 'max_conversation_length', '100', '대화당 최대 메시지 수'),
('setting-002', 'system', 'default_model', 'gpt-6', '기본 대화 모델'),
('setting-003', 'security', 'session_timeout', '86400', '세션 타임아웃(초)'),
('setting-004', 'sandbox', 'default_timeout', '300', '샌드박스 기본 타임아웃(초)'),
('setting-005', 'sandbox', 'max_memory', '4096', '샌드박스 최대 메모리(MB)');

-- 8.2 LOG 테이블
INSERT INTO log (id, level, message, timestamp, component, user_id, trace_id) VALUES
(1, 'INFO', '시스템 시작됨', '2025-05-28 00:00:01', 'system', NULL, NULL),
(2, 'INFO', '사용자 로그인', '2025-05-28 09:00:00', 'auth', 1, 'trace-001'),
(3, 'INFO', '대화 시작됨', '2025-05-28 10:15:00', 'conversation', 2, 'trace-002'),
(4, 'INFO', '도구 실행 요청', '2025-05-28 10:15:12', 'tool', 2, 'trace-003'),
(5, 'INFO', '도구 실행 완료', '2025-05-28 10:15:14', 'tool', 2, 'trace-003'),
(6, 'ERROR', '데이터베이스 연결 지연', '2025-05-28 12:30:00', 'database', NULL, 'trace-004'),
(7, 'WARN', '높은 CPU 사용량 감지', '2025-05-28 13:45:00', 'monitoring', NULL, NULL);

-- 8.3 MONITORING 테이블
INSERT INTO monitoring (id, component, metric, value, timestamp) VALUES
(1, 'system', 'cpu_usage', 45.20, '2025-05-28 10:00:00'),
(2, 'system', 'memory_usage', 6144.00, '2025-05-28 10:00:00'),
(3, 'database', 'connection_count', 25.00, '2025-05-28 10:00:00'),
(4, 'api', 'request_count', 1250.00, '2025-05-28 10:00:00'),
(5, 'api', 'average_response_time', 120.50, '2025-05-28 10:00:00'),
(6, 'system', 'cpu_usage', 78.60, '2025-05-28 13:45:00'),
(7, 'system', 'memory_usage', 7168.00, '2025-05-28 13:45:00');

-- 8.4 TASK_QUEUE 테이블
INSERT INTO task_queue (id, task_type, priority, status, payload, created_at, started_at, completed_at) VALUES
('task-001', 'model_training', 10, 'processing', '{"model":"conversation-model","version":"1.2.0","parameters":{"epochs":120,"batch_size":64}}', '2025-05-27 08:00:00', '2025-05-27 08:01:00', NULL),
('task-002', 'data_export', 5, 'pending', '{"user_id":2,"data_type":"conversation","format":"json"}', '2025-05-28 14:00:00', NULL, NULL),
('task-003', 'system_backup', 8, 'completed', '{"backup_type":"full","destination":"s3://agi-system-backups/2025-05-28/"}', '2025-05-28 01:00:00', '2025-05-28 01:01:00', '2025-05-28 02:30:00');

-- 8.5 HEALTH_CHECK 테이블
INSERT INTO health_check (id, component, status, details, timestamp) VALUES
(1, 'api_server', 'healthy', '{"response_time":45,"active_connections":18}', '2025-05-28 10:00:00'),
(2, 'database', 'healthy', '{"response_time":12,"active_connections":25}', '2025-05-28 10:00:00'),
(3, 'model_service', 'healthy', '{"response_time":85,"active_models":3}', '2025-05-28 10:00:00'),
(4, 'sandbox_service', 'degraded', '{"response_time":250,"active_sandboxes":12,"issue":"high_load"}', '2025-05-28 13:45:00'),
(5, 'api_server', 'healthy', '{"response_time":60,"active_connections":22}', '2025-05-28 14:00:00');

-- 9. 샌드박스 관리 테이블 샘플 데이터
-- 9.1 SANDBOX 테이블
INSERT INTO sandbox (id, user_id, status, container_id, image_name, image_tag, created_at, started_at, last_active, expires_at, config) VALUES
('sandbox-001', 3, 'running', 'container-12345', 'agi-sandbox', 'latest', '2025-05-28 11:34:00', '2025-05-28 11:34:30', '2025-05-28 11:35:10', '2025-05-28 12:34:30', '{"exposed_ports":[8080],"environment_variables":{"JAVA_HOME":"/usr/lib/jvm/java-17-openjdk"},"volumes":[{"host":"/tmp/sandbox-001","container":"/workspace"}]}'),
('sandbox-002', 2, 'paused', 'container-67890', 'agi-sandbox', 'python', '2025-05-27 15:20:00', '2025-05-27 15:20:30', '2025-05-27 16:30:00', '2025-05-28 15:20:30', '{"exposed_ports":[8888],"environment_variables":{"PYTHON_VERSION":"3.11"},"volumes":[{"host":"/tmp/sandbox-002","container":"/workspace"}]}');

-- 9.2 SANDBOX_WORKSPACE 테이블
INSERT INTO sandbox_workspace (id, sandbox_id, root_path, size_bytes, created_at) VALUES
('workspace-001', 'sandbox-001', '/tmp/sandbox-001', 1048576, '2025-05-28 11:34:00'),
('workspace-002', 'sandbox-002', '/tmp/sandbox-002', 2097152, '2025-05-27 15:20:00');

-- 9.3 SANDBOX_EXECUTION 테이블
INSERT INTO sandbox_execution (id, sandbox_id, command, working_dir, started_at, completed_at, exit_code, stdout, stderr, resource_usage) VALUES
('sandboxexec-001', 'sandbox-001', 'javac UserController.java', '/workspace', '2025-05-28 11:35:00', '2025-05-28 11:35:05', 0, 'Compiled successfully', '', '{"cpu_usage":15.2,"memory_usage":256}'),
('sandboxexec-002', 'sandbox-001', 'java UserController', '/workspace', '2025-05-28 11:35:06', '2025-05-28 11:35:10', 0, 'UserController started on port 8080', '', '{"cpu_usage":25.5,"memory_usage":512}'),
('sandboxexec-003', 'sandbox-002', 'python data_analysis.py', '/workspace', '2025-05-27 16:00:00', '2025-05-27 16:10:00', 0, 'Analysis completed. Results saved to results.csv', '', '{"cpu_usage":45.8,"memory_usage":1024}');

-- 9.4 SANDBOX_RESOURCE 테이블
INSERT INTO sandbox_resource (id, sandbox_id, cpu_limit, memory_limit, disk_limit, network_limit, timeout) VALUES
('resource-001', 'sandbox-001', 2, 2147483648, 10737418240, 10485760, 3600),
('resource-002', 'sandbox-002', 4, 4294967296, 21474836480, 20971520, 7200);

-- 9.5 SANDBOX_SECURITY 테이블
INSERT INTO sandbox_security (id, sandbox_id, network_policy, syscall_policy, mount_policy, env_variables, capabilities) VALUES
('security-001', 'sandbox-001', '{"outbound":["api.github.com","maven.org"],"inbound":[{"port":8080,"protocol":"tcp"}]}', '{"allowed":["read","write","open","close","stat"],"denied":["mount","umount","ptrace"]}', '{"readonly":["/usr/lib"],"readwrite":["/workspace"]}', '{"JAVA_HOME":"/usr/lib/jvm/java-17-openjdk","PATH":"/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"}', '{"allowed":["NET_BIND_SERVICE"],"denied":["SYS_ADMIN","NET_ADMIN"]}'),
('security-002', 'sandbox-002', '{"outbound":["pypi.org","api.github.com"],"inbound":[{"port":8888,"protocol":"tcp"}]}', '{"allowed":["read","write","open","close","stat"],"denied":["mount","umount","ptrace"]}', '{"readonly":["/usr/lib"],"readwrite":["/workspace"]}', '{"PYTHON_VERSION":"3.11","PATH":"/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"}', '{"allowed":["NET_BIND_SERVICE"],"denied":["SYS_ADMIN","NET_ADMIN"]}');

-- 9.6 SANDBOX_FILE 테이블
INSERT INTO sandbox_file (id, sandbox_id, path, content_hash, size_bytes, mime_type, is_directory, created_at) VALUES
('file-001', 'sandbox-001', '/workspace/UserController.java', 'a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2', 2048, 'text/x-java-source', FALSE, '2025-05-28 11:34:45'),
('file-002', 'sandbox-001', '/workspace/UserService.java', 'b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3', 1536, 'text/x-java-source', FALSE, '2025-05-28 11:34:50'),
('file-003', 'sandbox-002', '/workspace/data_analysis.py', 'c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4', 4096, 'text/x-python', FALSE, '2025-05-27 15:30:00'),
('file-004', 'sandbox-002', '/workspace/data', 'd4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5', 0, 'inode/directory', TRUE, '2025-05-27 15:30:10'),
('file-005', 'sandbox-002', '/workspace/data/sales.csv', 'e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6', 102400, 'text/csv', FALSE, '2025-05-27 15:30:20');

-- 9.7 SANDBOX_PORT 테이블
INSERT INTO sandbox_port (id, sandbox_id, container_port, host_port, protocol, description) VALUES
('port-001', 'sandbox-001', 8080, 49152, 'tcp', 'Java 웹 애플리케이션 포트'),
('port-002', 'sandbox-002', 8888, 49153, 'tcp', 'Jupyter Notebook 포트');

-- 9.8 SANDBOX_TEMPLATE 테이블
INSERT INTO sandbox_template (id, name, description, image_name, image_tag, config, resource_config, security_config) VALUES
('template-001', 'java-development', 'Java 개발 환경', 'agi-sandbox', 'java', '{"exposed_ports":[8080],"environment_variables":{"JAVA_HOME":"/usr/lib/jvm/java-17-openjdk"}}', '{"cpu_limit":2,"memory_limit":2147483648,"disk_limit":10737418240,"timeout":3600}', '{"network_policy":{"outbound":["api.github.com","maven.org"]},"capabilities":{"allowed":["NET_BIND_SERVICE"]}}'),
('template-002', 'python-data-science', 'Python 데이터 과학 환경', 'agi-sandbox', 'python', '{"exposed_ports":[8888],"environment_variables":{"PYTHON_VERSION":"3.11"}}', '{"cpu_limit":4,"memory_limit":4294967296,"disk_limit":21474836480,"timeout":7200}', '{"network_policy":{"outbound":["pypi.org","api.github.com"]},"capabilities":{"allowed":["NET_BIND_SERVICE"]}}');

-- 10. 설명 가능성 테이블 샘플 데이터
-- 10.1 EXPLANATION 테이블
INSERT INTO explanation (id, target_id, target_type, explanation, algorithm, confidence, created_at) VALUES
('explanation-001', 'msg-2025-05-28-002', 'message', '이 응답은 현재 서울의 날씨 상태(맑음), 기온(24°C), 그리고 오후 강수 확률(30%)을 기반으로 생성되었습니다. 특히 오후 소나기 가능성이 있어 우산을 챙기라는 조언을 추가했습니다.', 'feature_attribution', 0.92, '2025-05-28 10:15:16'),
('explanation-002', 'exec-001', 'tool_execution', '날씨 조회 도구는 서울의 위치 정보를 OpenWeatherMap API에 전송하여 현재 날씨 데이터를 받아왔습니다. 응답에는 현재 기온, 날씨 상태, 습도, 풍속, 강수 확률이 포함되어 있습니다.', 'process_tracing', 0.95, '2025-05-28 10:15:15');

-- 10.2 EXPLANATION_FEATURE 테이블
INSERT INTO explanation_feature (id, explanation_id, feature_name, feature_value, importance) VALUES
(1, 'explanation-001', 'current_temperature', '24°C', 0.8000),
(2, 'explanation-001', 'weather_condition', '맑음', 0.7500),
(3, 'explanation-001', 'precipitation_probability', '30%', 0.9000),
(4, 'explanation-002', 'api_source', 'OpenWeatherMap', 0.6000),
(5, 'explanation-002', 'location_parameter', '서울', 0.9500);

-- 10.3 EXPLANATION_TEMPLATE 테이블
INSERT INTO explanation_template (id, name, template_text, target_type, complexity_level, is_active) VALUES
('template-001', 'weather_response', '이 응답은 {{location}}의 현재 날씨 상태({{condition}}), 기온({{temperature}}), 그리고 {{time_period}} 강수 확률({{precipitation_probability}})을 기반으로 생성되었습니다.{% if precipitation_probability > 20 %} 특히 {{time_period}} {{precipitation_type}} 가능성이 있어 {{recommendation}}을 추가했습니다.{% endif %}', 'message', 'moderate', TRUE),
('template-002', 'tool_execution_simple', '{{tool_name}} 도구는 {{input_summary}}를 처리하여 {{output_summary}}를 생성했습니다.', 'tool_execution', 'simple', TRUE),
('template-003', 'tool_execution_detailed', '{{tool_name}} 도구는 {{input_detail}}을 {{api_name}}에 전송하여 데이터를 받아왔습니다. 응답에는 {{output_details}}이 포함되어 있습니다.', 'tool_execution', 'detailed', TRUE);

-- 11. 감성 지능 테이블 샘플 데이터
-- 11.1 EMOTION_ANALYSIS 테이블
INSERT INTO emotion_analysis (id, target_id, target_type, emotions, dominant_emotion, intensity, timestamp) VALUES
('emotion-001', 'msg-2025-05-28-001', 'message', '{"neutral":0.7,"curious":0.2,"happy":0.05,"anxious":0.05}', 'neutral', 0.7, '2025-05-28 10:15:11'),
('emotion-002', 'msg-2025-05-28-003', 'message', '{"excited":0.6,"curious":0.3,"neutral":0.1}', 'excited', 0.6, '2025-05-28 11:30:11'),
('emotion-003', 'feedback-001', 'feedback', '{"happy":0.8,"satisfied":0.7,"grateful":0.5}', 'happy', 0.8, '2025-05-28 10:16:01');

-- 11.2 EMOTIONAL_RESPONSE 테이블
INSERT INTO emotional_response (id, trigger_emotion, response_type, template, priority, is_active) VALUES
('emotional-resp-001', 'curious', 'informative', '흥미로운 질문이네요! {{detailed_information}}', 10, TRUE),
('emotional-resp-002', 'anxious', 'reassuring', '걱정하지 마세요. {{reassurance}} {{solution}}', 20, TRUE),
('emotional-resp-003', 'excited', 'enthusiastic', '멋지네요! {{positive_reinforcement}} {{detailed_response}}', 10, TRUE),
('emotional-resp-004', 'frustrated', 'empathetic', '불편을 드려 죄송합니다. {{acknowledgement}} {{solution}}', 30, TRUE);

-- 11.3 USER_EMOTION_HISTORY 테이블
INSERT INTO user_emotion_history (id, user_id, emotion, intensity, context, timestamp) VALUES
(1, 2, 'neutral', 0.7, 'weather_query', '2025-05-28 10:15:11'),
(2, 2, 'happy', 0.8, 'feedback_submission', '2025-05-28 10:16:01'),
(3, 3, 'excited', 0.6, 'project_planning', '2025-05-28 11:30:11'),
(4, 3, 'satisfied', 0.7, 'code_generation', '2025-05-28 11:36:01');

-- 11.4 EMPATHY_MODEL 테이블
INSERT INTO empathy_model (id, name, description, parameters, version, is_active) VALUES
('empathy-model-001', 'text-emotion-analyzer', '텍스트 기반 감정 분석 모델', '{"model_type":"transformer","embedding_dim":768,"num_layers":12,"num_attention_heads":12}', '1.0.0', FALSE),
('empathy-model-002', 'text-emotion-analyzer', '텍스트 기반 감정 분석 모델 (개선 버전)', '{"model_type":"transformer","embedding_dim":1024,"num_layers":24,"num_attention_heads":16}', '2.0.0', TRUE),
('empathy-model-003', 'multimodal-emotion-analyzer', '텍스트, 음성, 이미지 기반 감정 분석 모델', '{"model_type":"multimodal-transformer","embedding_dim":1536,"num_layers":36,"num_attention_heads":24}', '1.0.0', TRUE);

-- 12. 적응형 학습 테이블 샘플 데이터
-- 12.1 USER_PROFILE 테이블
INSERT INTO user_profile (user_id, interaction_history, knowledge_map, learning_style, proficiency_level) VALUES
(2, '{"total_interactions":42,"avg_session_length":15,"frequent_topics":["weather","technology","travel"]}', '{"weather":0.8,"technology":0.7,"travel":0.5,"finance":0.3}', 'visual', 'intermediate'),
(3, '{"total_interactions":78,"avg_session_length":25,"frequent_topics":["programming","data_science","ai"]}', '{"programming":0.9,"data_science":0.8,"ai":0.7,"web_development":0.6}', 'kinesthetic', 'advanced');

-- 12.2 LEARNING_PREFERENCE 테이블
INSERT INTO learning_preference (id, user_id, pref_key, pref_value, confidence) VALUES
(1, 2, 'content_type', 'visual', 0.85),
(2, 2, 'detail_level', 'moderate', 0.70),
(3, 2, 'example_frequency', 'high', 0.65),
(4, 3, 'content_type', 'code', 0.90),
(5, 3, 'detail_level', 'high', 0.85),
(6, 3, 'example_frequency', 'very_high', 0.80);

-- 12.3 ADAPTATION_RULE 테이블
INSERT INTO adaptation_rule (id, condition, action, priority, description) VALUES
('rule-001', '{"learning_style":"visual","topic":"any"}', '{"include_diagrams":true,"use_color_coding":true}', 10, '시각적 학습자를 위한 다이어그램 및 색상 코딩 포함'),
('rule-002', '{"proficiency_level":"beginner","topic":"any"}', '{"simplify_language":true,"increase_examples":true,"reduce_jargon":true}', 20, '초보자를 위한 언어 단순화 및 예제 증가'),
('rule-003', '{"learning_style":"kinesthetic","topic":"programming"}', '{"include_interactive_code":true,"suggest_exercises":true}', 15, '실습형 학습자를 위한 프로그래밍 학습 시 대화형 코드 및 연습 문제 제공');

-- 12.4 LEARNING_SESSION 테이블
INSERT INTO learning_session (id, user_id, topic, start_time, end_time, duration, progress, metrics) VALUES
('session-001', 2, 'weather_patterns', '2025-05-28 10:15:00', '2025-05-28 10:30:00', 900, 100.00, '{"questions_asked":3,"information_retained":0.85,"engagement_level":0.75}'),
('session-002', 3, 'spring_boot_development', '2025-05-28 11:30:00', NULL, NULL, 65.00, '{"questions_asked":5,"information_retained":0.90,"engagement_level":0.95}');

-- 12.5 ADAPTATION_LOG 테이블
INSERT INTO adaptation_log (id, user_id, rule_id, context, adaptation_type, adaptation_details, timestamp) VALUES
(1, 2, 'rule-001', '{"session_id":"session-001","topic":"weather_patterns","current_interaction":2}', 'content_format', '{"added_diagram":true,"used_color_coding":true,"diagram_type":"weather_map"}', '2025-05-28 10:18:00'),
(2, 3, 'rule-003', '{"session_id":"session-002","topic":"spring_boot_development","current_interaction":3}', 'content_format', '{"added_interactive_code":true,"suggested_exercises":true,"code_language":"java"}', '2025-05-28 11:40:00');

-- 13. 강화 학습 테이블 샘플 데이터
-- 13.1 RL_AGENT_STATE 테이블
INSERT INTO rl_agent_state (id, agent_id, state_representation, timestamp, metadata) VALUES
('state-001', 'conversation-agent', '{"context_length":3,"user_satisfaction":0.8,"topic":"weather","session_duration":300}', '2025-05-28 10:15:10', '{"state_version":"1.0","encoding_method":"json"}'),
('state-002', 'conversation-agent', '{"context_length":4,"user_satisfaction":0.9,"topic":"weather","session_duration":600}', '2025-05-28 10:20:00', '{"state_version":"1.0","encoding_method":"json"}'),
('state-003', 'code-agent', '{"context_length":2,"user_satisfaction":0.7,"topic":"programming","session_duration":300}', '2025-05-28 11:35:00', '{"state_version":"1.0","encoding_method":"json"}');

-- 13.2 REWARD_SIGNAL 테이블
INSERT INTO reward_signal (id, agent_id, trigger_id, trigger_type, reward_value, reward_source, timestamp, context) VALUES
('reward-001', 'conversation-agent', 'msg-2025-05-28-002', 'message', 0.8, 'user_satisfaction', '2025-05-28 10:15:15', '{"feedback_type":"implicit","session_id":"f47ac10b-58cc-4372-a567-0e02b2c3d479"}'),
('reward-002', 'conversation-agent', 'feedback-001', 'feedback', 1.0, 'explicit_feedback', '2025-05-28 10:16:00', '{"feedback_type":"explicit","rating":5,"session_id":"f47ac10b-58cc-4372-a567-0e02b2c3d479"}'),
('reward-003', 'code-agent', 'exec-002', 'tool_execution', 0.9, 'execution_success', '2025-05-28 11:35:10', '{"feedback_type":"implicit","session_id":"a1b2c3d4-e5f6-4a5b-9c8d-7e6f5a4b3c2d"}');

-- 13.3 RL_POLICY 테이블
INSERT INTO rl_policy (id, agent_id, name, version, parameters, performance_metrics, is_active) VALUES
('policy-001', 'conversation-agent', 'weather_conversation_policy', '1.0.0', '{"algorithm":"ppo","gamma":0.99,"lambda":0.95,"clip_ratio":0.2,"value_coef":0.5,"entropy_coef":0.01}', '{"average_reward":0.75,"success_rate":0.85,"convergence_episodes":1000}', FALSE),
('policy-002', 'conversation-agent', 'weather_conversation_policy', '1.1.0', '{"algorithm":"ppo","gamma":0.99,"lambda":0.95,"clip_ratio":0.2,"value_coef":0.6,"entropy_coef":0.02}', '{"average_reward":0.82,"success_rate":0.90,"convergence_episodes":800}', TRUE),
('policy-003', 'code-agent', 'code_generation_policy', '1.0.0', '{"algorithm":"dqn","gamma":0.95,"epsilon":0.1,"learning_rate":0.001,"batch_size":64}', '{"average_reward":0.70,"success_rate":0.80,"convergence_episodes":1200}', TRUE);

-- 13.4 RL_EPISODE 테이블
INSERT INTO rl_episode (id, agent_id, policy_id, start_time, end_time, total_reward, steps_count, success, metadata) VALUES
('episode-001', 'conversation-agent', 'policy-002', '2025-05-28 10:15:00', '2025-05-28 10:16:00', 1.8, 2, TRUE, '{"conversation_id":"conv-2025-05-28-001","topic":"weather"}'),
('episode-002', 'code-agent', 'policy-003', '2025-05-28 11:35:00', '2025-05-28 11:36:00', 0.9, 1, TRUE, '{"tool_execution_id":"exec-002","language":"java"}');

-- 13.5 RL_ACTION 테이블
INSERT INTO rl_action (id, episode_id, state_id, action_type, action_details, step_number, reward, timestamp) VALUES
('action-001', 'episode-001', 'state-001', 'generate_response', '{"response_id":"msg-2025-05-28-002","response_strategy":"informative","include_recommendation":true}', 0, 0.8, '2025-05-28 10:15:15'),
('action-002', 'episode-001', 'state-002', 'process_feedback', '{"feedback_id":"feedback-001","adaptation_strategy":"reinforce_successful_pattern"}', 1, 1.0, '2025-05-28 10:16:00'),
('action-003', 'episode-002', 'state-003', 'generate_code', '{"code_id":"exec-002","language":"java","template":"controller","customization_level":"high"}', 0, 0.9, '2025-05-28 11:35:10');

-- 14. 영역 간 지식 전이 테이블 샘플 데이터
-- 14.1 KNOWLEDGE_SOURCE 테이블
INSERT INTO knowledge_source (id, name, domain, description, connection_info, is_active) VALUES
('source-001', 'weather_knowledge_base', 'meteorology', '기상학 및 날씨 예측 관련 지식 베이스', '{"type":"vector_db","endpoint":"http://knowledge-service/weather","auth_method":"api_key"}', TRUE),
('source-002', 'programming_knowledge_base', 'computer_science', '프로그래밍 언어 및 소프트웨어 개발 관련 지식 베이스', '{"type":"vector_db","endpoint":"http://knowledge-service/programming","auth_method":"api_key"}', TRUE),
('source-003', 'general_knowledge_base', 'general', '일반 상식 및 다양한 도메인의 기본 지식', '{"type":"vector_db","endpoint":"http://knowledge-service/general","auth_method":"api_key"}', TRUE);

-- 14.2 KNOWLEDGE_MAPPING 테이블
INSERT INTO knowledge_mapping (id, source_concept, target_concept, relation_type, confidence) VALUES
('mapping-001', 'weather:precipitation', 'general:water_cycle', 'is_part_of', 0.95),
('mapping-002', 'programming:object', 'general:abstraction', 'is_instance_of', 0.85),
('mapping-003', 'programming:algorithm', 'mathematics:problem_solving', 'is_application_of', 0.90),
('mapping-004', 'weather:forecast_model', 'mathematics:statistical_model', 'is_type_of', 0.80),
('mapping-005', 'programming:debugging', 'general:problem_solving', 'is_instance_of', 0.75);

-- 14.3 TRANSFER_TASK 테이블
INSERT INTO transfer_task (id, source_id, target_id, status, parameters, created_at, completed_at, result) VALUES
('transfer-001', 'source-001', 'source-003', 'completed', '{"transfer_method":"analogy_mapping","concepts":["precipitation","temperature","humidity"],"target_domain":"general"}', '2025-05-15 10:00:00', '2025-05-15 12:30:00', '{"success_rate":0.85,"transferred_concepts":3,"new_mappings":5}'),
('transfer-002', 'source-002', 'source-003', 'running', '{"transfer_method":"concept_alignment","concepts":["object","class","inheritance","polymorphism"],"target_domain":"general"}', '2025-05-28 09:00:00', NULL, NULL);

-- 14.4 DOMAIN_ONTOLOGY 테이블
INSERT INTO domain_ontology (id, domain, ontology_data, version, is_active) VALUES
('ontology-001', 'meteorology', '{"concepts":{"precipitation":{"definition":"Water falling from clouds","related":["rain","snow","hail"]},"temperature":{"definition":"Measure of heat energy","related":["celsius","fahrenheit","kelvin"]},"humidity":{"definition":"Amount of water vapor in air","related":["relative_humidity","dew_point"]}}}', '1.0.0', TRUE),
('ontology-002', 'computer_science', '{"concepts":{"object":{"definition":"Instance of a class","related":["class","instance","property"]},"algorithm":{"definition":"Step-by-step procedure","related":["complexity","efficiency","correctness"]},"data_structure":{"definition":"Organization of data","related":["array","list","tree","graph"]}}}', '1.0.0', TRUE),
('ontology-003', 'general', '{"concepts":{"water_cycle":{"definition":"Natural cycle of water movement","related":["evaporation","condensation","precipitation"]},"abstraction":{"definition":"Simplification of complex reality","related":["concept","model","theory"]},"problem_solving":{"definition":"Process of finding solutions","related":["analysis","synthesis","evaluation"]}}}', '1.0.0', TRUE);

-- 14.5 TRANSFER_METRIC 테이블
INSERT INTO transfer_metric (id, transfer_task_id, metric_name, metric_value) VALUES
('metric-001', 'transfer-001', 'concept_alignment_accuracy', 0.85),
('metric-002', 'transfer-001', 'knowledge_retention', 0.92),
('metric-003', 'transfer-001', 'transfer_efficiency', 0.78),
('metric-004', 'transfer-001', 'novel_inference_capability', 0.65);

-- 15. 창의적 생성 테이블 샘플 데이터
-- 15.1 CREATIVE_WORK 테이블
INSERT INTO creative_work (id, user_id, type, title, content_ref, parameters, metadata) VALUES
('work-001', 2, 'text', '미래 도시의 날씨 제어 시스템', '/storage/creative/text/future_weather_control_system.md', '{"style":"technical","length":"medium","creativity_level":0.7}', '{"inspiration":["weather_control","smart_cities"],"target_audience":"general","purpose":"entertainment"}'),
('work-002', 3, 'image', '미래 프로그래머의 작업 환경', '/storage/creative/image/future_programming_workspace.png', '{"style":"futuristic","resolution":"high","creativity_level":0.8}', '{"inspiration":["programming","future_technology"],"target_audience":"developers","purpose":"visualization"}');

-- 15.2 GENERATION_PROMPT 테이블
INSERT INTO generation_prompt (id, work_id, prompt_text, timestamp) VALUES
('prompt-001', 'work-001', '2050년 미래 도시에서 날씨를 제어하는 첨단 시스템에 대한 기술적 설명을 작성해주세요. 이 시스템은 어떤 원리로 작동하며, 어떤 기술적 도전과 윤리적 문제가 있을지 포함해주세요.', '2025-05-27 14:00:00'),
('prompt-002', 'work-002', '2050년 미래의 프로그래머가 사용할 작업 환경을 시각화해주세요. 홀로그램 디스플레이, 뇌-컴퓨터 인터페이스, 인공지능 코딩 어시스턴트 등의 요소를 포함하고, 미래적이고 세련된 디자인으로 표현해주세요.', '2025-05-28 10:00:00');

-- 15.3 CREATIVE_FEEDBACK 테이블
INSERT INTO creative_feedback (id, work_id, user_id, rating, comment, created_at) VALUES
('creative-feedback-001', 'work-001', 1, 5, '미래 기술에 대한 상상력이 뛰어나면서도 과학적 원리에 기반한 설명이 인상적입니다.', '2025-05-27 16:00:00'),
('creative-feedback-002', 'work-001', 3, 4, '기술적 설명은 훌륭하지만, 윤리적 문제에 대한 논의가 더 깊이 있었으면 좋겠습니다.', '2025-05-27 17:30:00'),
('creative-feedback-003', 'work-002', 2, 5, '미래 기술을 시각화한 방식이 매우 창의적이고 세부 묘사가 뛰어납니다.', '2025-05-28 11:00:00');

-- 15.4 CREATIVE_VERSION 테이블
INSERT INTO creative_version (id, work_id, version_number, content_ref, changes_description, created_at) VALUES
('version-001', 'work-001', 1, '/storage/creative/text/future_weather_control_system_v1.md', '초기 버전', '2025-05-27 14:30:00'),
('version-002', 'work-001', 2, '/storage/creative/text/future_weather_control_system_v2.md', '기술적 설명 보강 및 윤리적 문제 추가', '2025-05-27 15:45:00'),
('version-003', 'work-001', 3, '/storage/creative/text/future_weather_control_system.md', '피드백 반영 및 최종 수정', '2025-05-28 09:00:00'),
('version-004', 'work-002', 1, '/storage/creative/image/future_programming_workspace_v1.png', '초기 스케치', '2025-05-28 10:15:00'),
('version-005', 'work-002', 2, '/storage/creative/image/future_programming_workspace.png', '세부 요소 추가 및 색상 개선', '2025-05-28 10:45:00');

-- 15.5 INSPIRATION_SOURCE 테이블
INSERT INTO inspiration_source (id, work_id, source_type, source_ref, influence_level, description) VALUES
('inspiration-001', 'work-001', 'article', 'https://science.org/future-weather-modification', 0.8000, '미래 날씨 수정 기술에 관한 과학 기사'),
('inspiration-002', 'work-001', 'book', 'Smart Cities: Technology and Ethics', 0.6000, '스마트 시티의 기술과 윤리에 관한 책'),
('inspiration-003', 'work-001', 'conversation', 'conv-2025-05-28-001', 0.4000, '날씨 관련 이전 대화'),
('inspiration-004', 'work-002', 'image', 'https://design.org/future-workspaces', 0.7000, '미래 작업 공간 디자인 갤러리'),
('inspiration-005', 'work-002', 'article', 'https://tech.org/brain-computer-interfaces-2040', 0.9000, '2040년 뇌-컴퓨터 인터페이스 전망 기사');
