# 통합 AGI 시스템 API 설계

## 1. 개요

이 문서는 Spring Boot 3.4.5, Java 17 기반의 통합 AGI 시스템을 위한 API 설계를 설명합니다. API는 RESTful 원칙을 따르며, WebSocket을 통해 실시간 통신을 지원합니다. 이 API는 Vue.js 프론트엔드 및 외부 시스템과의 연동을 위해 설계되었습니다.

## 2. API 설계 원칙

1. **RESTful**: HTTP 메서드(GET, POST, PUT, DELETE 등)를 사용하여 자원을 명확하게 표현하고 조작합니다.
2. **자원 중심**: API 엔드포인트는 명사 중심의 자원 경로를 사용합니다.
3. **표준 HTTP 상태 코드**: 요청 처리 결과를 명확하게 나타내기 위해 표준 HTTP 상태 코드를 사용합니다.
4. **JSON 형식**: 요청 및 응답 본문은 JSON 형식을 기본으로 사용합니다.
5. **인증 및 권한 부여**: JWT(JSON Web Token)를 사용하여 안전한 인증 및 역할 기반 권한 부여를 구현합니다.
6. **일관성**: API 엔드포인트, 요청/응답 구조, 에러 처리 방식의 일관성을 유지합니다.
7. **문서화**: OpenAPI(Swagger)를 사용하여 API 명세를 자동으로 생성하고 문서화합니다.

## 3. 인증 및 권한 부여

- **인증**: 모든 API 요청(로그인/회원가입 제외)은 `Authorization: Bearer <JWT_TOKEN>` 헤더를 통해 JWT 토큰을 전달해야 합니다.
- **권한 부여**: 사용자 역할(Role) 및 권한(Permission)에 따라 API 접근이 제어됩니다. 각 엔드포인트는 필요한 권한을 명시합니다.

## 4. 에러 처리

- **표준 에러 응답 형식**: 에러 발생 시 일관된 JSON 형식으로 응답합니다.
  ```json
  {
    "timestamp": "2025-05-28T14:00:00Z",
    "status": 404,
    "error": "Not Found",
    "message": "Resource not found with id: 123",
    "path": "/api/resource/123",
    "errorCode": "RESOURCE_NOT_FOUND"
  }
  ```
- **주요 HTTP 상태 코드**:
  - `200 OK`: 요청 성공
  - `201 Created`: 자원 생성 성공
  - `204 No Content`: 요청 성공 (응답 본문 없음)
  - `400 Bad Request`: 잘못된 요청 (파라미터 오류, 유효성 검사 실패 등)
  - `401 Unauthorized`: 인증 실패 (유효하지 않은 토큰)
  - `403 Forbidden`: 권한 없음
  - `404 Not Found`: 요청한 자원 없음
  - `409 Conflict`: 자원 충돌 (중복 생성 등)
  - `500 Internal Server Error`: 서버 내부 오류

## 5. RESTful API 엔드포인트

### 5.1 사용자 및 인증 관리 (`/api/auth`, `/api/users`)

- `POST /api/auth/register`: 사용자 회원가입
- `POST /api/auth/login`: 사용자 로그인 (JWT 토큰 발급)
- `POST /api/auth/refresh`: JWT 토큰 갱신
- `GET /api/users/me`: 현재 로그인된 사용자 정보 조회
- `PUT /api/users/me`: 현재 로그인된 사용자 정보 수정
- `GET /api/users/{userId}`: 특정 사용자 정보 조회 (관리자 권한)
- `GET /api/users`: 사용자 목록 조회 (관리자 권한)
- `PUT /api/users/{userId}/role`: 사용자 역할 변경 (관리자 권한)

### 5.2 자연어 처리 엔진 (`/api/nlp`)

- `POST /api/nlp/conversation`: 대화 시작 또는 메시지 전송
- `GET /api/nlp/conversation/{conversationId}`: 특정 대화 기록 조회
- `GET /api/nlp/conversations`: 사용자 대화 목록 조회
- `POST /api/nlp/analyze/sentiment`: 텍스트 감정 분석
- `POST /api/nlp/analyze/keywords`: 텍스트 키워드 추출
- `POST /api/nlp/analyze/entities`: 텍스트 엔티티 추출
- `POST /api/nlp/analyze/intent`: 텍스트 의도 파악
- `POST /api/nlp/generate/text`: 텍스트 생성 요청
- `POST /api/nlp/qa`: 질의응답 요청
- `POST /api/nlp/translate`: 텍스트 번역 요청
- `POST /api/nlp/speech/recognize`: 음성 인식 요청 (오디오 파일 업로드)
- `POST /api/nlp/speech/synthesize`: 텍스트 음성 합성 요청
- `POST /api/nlp/command`: 자연어 명령어 처리 요청

### 5.3 도구 사용 프레임워크 (`/api/tools`)

- `GET /api/tools`: 사용 가능한 도구 목록 조회
- `GET /api/tools/{toolId}`: 특정 도구 정보 조회
- `POST /api/tools/execute`: 특정 도구 실행 요청
- `GET /api/tools/executions/{executionId}`: 도구 실행 결과 조회
- `POST /api/tools/register`: 새로운 도구 등록 (관리자 권한)

### 5.4 계획 수립 모듈 (`/api/plans`)

- `POST /api/plans`: 새로운 계획 생성
- `GET /api/plans`: 사용자 계획 목록 조회
- `GET /api/plans/{planId}`: 특정 계획 정보 조회
- `PUT /api/plans/{planId}`: 계획 수정
- `DELETE /api/plans/{planId}`: 계획 삭제
- `POST /api/plans/{planId}/execute`: 계획 실행 시작
- `GET /api/plans/{planId}/execution`: 계획 실행 상태 및 결과 조회
- `POST /api/plans/{planId}/steps`: 계획 단계 추가
- `PUT /api/plans/{planId}/steps/{stepId}`: 계획 단계 수정
- `DELETE /api/plans/{planId}/steps/{stepId}`: 계획 단계 삭제

### 5.5 지식 및 기억 시스템 (`/api/knowledge`, `/api/memory`)

- `POST /api/knowledge`: 새로운 지식 저장
- `GET /api/knowledge`: 지식 검색
- `GET /api/knowledge/{knowledgeId}`: 특정 지식 조회
- `PUT /api/knowledge/{knowledgeId}`: 지식 수정
- `DELETE /api/knowledge/{knowledgeId}`: 지식 삭제
- `GET /api/memory`: 사용자 메모리 조회 (타입 필터링 가능)
- `POST /api/memory/recall`: 관련 메모리 회상 요청
- `POST /api/context/update`: 현재 세션 컨텍스트 업데이트
- `GET /api/context`: 현재 세션 컨텍스트 조회

### 5.6 멀티모달 처리 모듈 (`/api/media`)

- `POST /api/media/image/process`: 이미지 처리 요청 (파일 업로드, 처리 유형 명시)
- `POST /api/media/audio/process`: 오디오 처리 요청 (파일 업로드, 처리 유형 명시)
- `POST /api/media/video/process`: 비디오 처리 요청 (파일 업로드, 처리 유형 명시)
- `GET /api/media/{mediaId}`: 처리된 미디어 메타데이터 조회
- `GET /api/media`: 사용자 미디어 목록 조회

### 5.7 자가 학습 모듈 (`/api/learning`)

- `POST /api/learning/feedback`: 사용자 피드백 제출
- `POST /api/learning/trigger`: 학습 프로세스 수동 트리거 (관리자 권한)
- `GET /api/learning/models`: 학습된 모델 버전 목록 조회
- `GET /api/learning/models/{modelName}/active`: 특정 모델의 활성 버전 조회
- `PUT /api/learning/models/{modelName}/activate`: 특정 모델 버전 활성화 (관리자 권한)

### 5.8 설명 가능성 모듈 (`/api/explain`)

- `POST /api/explain/decision`: 특정 결정 과정에 대한 설명 요청
- `POST /api/explain/model`: 특정 모델 해석 요청
- `POST /api/explain/bias`: 모델 편향성 분석 요청

### 5.9 감성 지능 모듈 (`/api/emotion`)

- `POST /api/emotion/detect`: 텍스트 또는 오디오 기반 감정 감지 요청
- `GET /api/emotion/history`: 사용자 감정 이력 조회

### 5.10 적응형 학습 모듈 (`/api/adaptive`)

- `POST /api/adaptive/personalize`: 사용자 데이터 기반 모델 개인화 요청
- `POST /api/adaptive/adapt`: 현재 컨텍스트 기반 모델 적응 요청

### 5.11 강화 학습 모듈 (`/api/rl`)

- `POST /api/rl/agents`: 새로운 강화 학습 에이전트 생성 (관리자 권한)
- `GET /api/rl/agents`: 에이전트 목록 조회 (관리자 권한)
- `POST /api/rl/agents/{agentId}/train`: 에이전트 학습 시작 (관리자 권한)
- `GET /api/rl/agents/{agentId}/policy`: 에이전트 정책 실행 요청

### 5.12 영역 간 지식 전이 모듈 (`/api/transfer`)

- `POST /api/transfer/knowledge`: 도메인 간 지식 전이 요청 (관리자 권한)
- `POST /api/transfer/fuse`: 다중 지식 소스 융합 요청 (관리자 권한)

### 5.13 창의적 생성 모듈 (`/api/creative`)

- `POST /api/creative/generate/idea`: 아이디어 생성 요청
- `POST /api/creative/generate/content`: 텍스트 콘텐츠 생성 요청
- `POST /api/creative/generate/image`: 이미지 생성 요청
- `POST /api/creative/generate/audio`: 오디오 생성 요청
- `POST /api/creative/solve`: 창의적 문제 해결 요청

### 5.14 시스템 관리 (`/api/admin`)

- `GET /api/admin/settings`: 시스템 설정 조회 (관리자 권한)
- `PUT /api/admin/settings`: 시스템 설정 수정 (관리자 권한)
- `GET /api/admin/logs`: 시스템 로그 조회 (관리자 권한)
- `GET /api/admin/monitoring`: 시스템 모니터링 데이터 조회 (관리자 권한)

## 6. WebSocket API 엔드포인트

WebSocket은 `/ws` 경로를 통해 연결됩니다. STOMP 프로토콜을 사용하여 메시지를 교환합니다.

### 6.1 구독 (Subscribe)

- `/topic/responses/{sessionId}`: 특정 세션에 대한 일반 응답 수신
- `/topic/stream/{sessionId}`: 특정 세션에 대한 스트리밍 응답 수신 (텍스트 생성 등)
- `/topic/progress/{sessionId}`: 특정 세션의 장기 실행 작업 진행 상태 수신
- `/user/queue/errors`: 현재 사용자에게 발생하는 오류 메시지 수신

### 6.2 발행 (Publish)

- `/app/conversation`: 대화 메시지 전송 (세션 ID 포함)
- `/app/stream-request`: 스트리밍 응답 요청 (세션 ID, 요청 내용 포함)
- `/app/cancel-task`: 장기 실행 작업 취소 요청 (세션 ID, 작업 ID 포함)

## 7. 요청/응답 형식 (DTO 예시)

### 7.1 대화 요청 (ConversationRequest)

```json
{
  "sessionId": "uuid-session-123",
  "conversationId": "uuid-conv-456", // Optional, 없으면 새 대화 시작
  "text": "오늘 날씨 어때?",
  "metadata": { // Optional
    "location": "서울"
  }
}
```

### 7.2 대화 응답 (ConversationResponse)

```json
{
  "conversationId": "uuid-conv-456",
  "messageId": "uuid-msg-789",
  "text": "오늘 서울 날씨는 맑고 최고 기온은 25도입니다.",
  "timestamp": "2025-05-28T14:05:00Z",
  "metadata": { // Optional
    "intent": "날씨 질문",
    "entities": [{"type": "location", "value": "서울"}]
  }
}
```

### 7.3 도구 실행 요청 (ToolExecutionRequest)

```json
{
  "toolName": "web_search",
  "parameters": {
    "query": "AGI 최신 기술 동향",
    "maxResults": 5
  }
}
```

### 7.4 도구 실행 응답 (ToolExecutionResponse)

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

## 8. API 문서화

- **OpenAPI(Swagger)**: Springdoc 라이브러리를 사용하여 API 명세를 자동으로 생성합니다.
- **문서 접근**: `/swagger-ui.html` 경로를 통해 API 문서를 웹 인터페이스로 제공합니다.
- **명세 파일**: `/v3/api-docs` 경로를 통해 OpenAPI 3.0 명세 파일을 JSON 형식으로 제공합니다.

## 9. 결론

이 API 설계는 통합 AGI 시스템의 다양한 기능을 외부 시스템 및 프론트엔드와 연동하기 위한 인터페이스를 정의합니다. RESTful 원칙과 WebSocket을 활용하여 효율적이고 실시간성 있는 통신을 지원하며, JWT 기반 인증과 표준화된 에러 처리, OpenAPI를 통한 문서화를 통해 개발 편의성과 시스템 안정성을 높입니다.
