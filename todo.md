# DTO 및 Enum 클래스 생성 계획

## 1. 사용자 도메인 (User Domain)

### DTO 클래스
- **Request DTO**
  - UserCreateRequest: 사용자 생성 요청
  - UserUpdateRequest: 사용자 정보 수정 요청
  - PasswordChangeRequest: 비밀번호 변경 요청
  - LoginRequest: 로그인 요청

- **Response DTO**
  - UserDto: 사용자 정보 응답
  - JwtTokenResponse: JWT 토큰 응답

### Enum 클래스
- UserRole: 사용자 역할 (ADMIN, USER, MANAGER, GUEST)

## 2. 대화 도메인 (Conversation Domain)

### DTO 클래스
- **Request DTO**
  - ConversationCreateRequest: 대화 생성 요청
  - MessageRequest: 메시지 전송 요청

- **Response DTO**
  - ConversationDto: 대화 정보 응답
  - MessageDto: 메시지 정보 응답

### Enum 클래스
- MessageType: 메시지 유형 (USER, ASSISTANT, SYSTEM, TOOL)

## 3. 도구 도메인 (Tool Domain)

### DTO 클래스
- **Request DTO**
  - ToolExecuteRequest: 도구 실행 요청

- **Response DTO**
  - ToolExecutionResultDto: 도구 실행 결과 응답
  - ToolDto: 도구 정보 응답
  - ToolParameterDto: 도구 파라미터 정보 응답

### Enum 클래스
- ToolStatus: 도구 상태 (PENDING, RUNNING, COMPLETED, FAILED)
- ToolType: 도구 유형 (SYSTEM, USER_DEFINED, EXTERNAL)

## 4. 계획 도메인 (Plan Domain)

### DTO 클래스
- **Request DTO**
  - PlanCreateRequest: 계획 생성 요청
  - PlanStepRequest: 계획 단계 요청

- **Response DTO**
  - PlanDto: 계획 정보 응답
  - PlanStepDto: 계획 단계 정보 응답

### Enum 클래스
- PlanStatus: 계획 상태 (DRAFT, ACTIVE, COMPLETED, CANCELLED)
- PlanStepStatus: 계획 단계 상태 (PENDING, IN_PROGRESS, COMPLETED, FAILED, SKIPPED)

## 5. 지식 도메인 (Knowledge Domain)

### DTO 클래스
- **Request DTO**
  - KnowledgeCreateRequest: 지식 생성 요청
  - KnowledgeSearchRequest: 지식 검색 요청

- **Response DTO**
  - KnowledgeDto: 지식 정보 응답

### Enum 클래스
- KnowledgeType: 지식 유형 (FACT, CONCEPT, PROCEDURE, PRINCIPLE)

## 6. 멀티모달 도메인 (Multimodal Domain)

### 이미지 하위 도메인
- **Request DTO**
  - ImageUploadRequest: 이미지 업로드 요청

- **Response DTO**
  - ImageMetadataDto: 이미지 메타데이터 응답

- **Enum 클래스**
  - ImageFormat: 이미지 형식 (JPEG, PNG, GIF, SVG, WEBP)

### 오디오 하위 도메인
- **Request DTO**
  - AudioUploadRequest: 오디오 업로드 요청

- **Response DTO**
  - AudioMetadataDto: 오디오 메타데이터 응답

- **Enum 클래스**
  - AudioFormat: 오디오 형식 (MP3, WAV, OGG, FLAC)

### 비디오 하위 도메인
- **Request DTO**
  - VideoUploadRequest: 비디오 업로드 요청

- **Response DTO**
  - VideoMetadataDto: 비디오 메타데이터 응답

- **Enum 클래스**
  - VideoFormat: 비디오 형식 (MP4, AVI, MOV, WEBM)

## 7. 학습 도메인 (Learning Domain)

### 피드백 하위 도메인
- **Request DTO**
  - FeedbackCreateRequest: 피드백 생성 요청

- **Response DTO**
  - FeedbackDto: 피드백 정보 응답

- **Enum 클래스**
  - FeedbackCategory: 피드백 카테고리 (ACCURACY, HELPFULNESS, SAFETY, CREATIVITY)

## 8. 시스템 도메인 (System Domain)

### 설정 하위 도메인
- **Request DTO**
  - SettingUpdateRequest: 설정 업데이트 요청

- **Response DTO**
  - SettingDto: 설정 정보 응답

- **Enum 클래스**
  - SettingCategory: 설정 카테고리 (SECURITY, PERFORMANCE, NOTIFICATION, APPEARANCE)

## 9. 샌드박스 도메인 (Sandbox Domain)

### DTO 클래스
- **Request DTO**
  - SandboxCreateRequest: 샌드박스 생성 요청

- **Response DTO**
  - SandboxDto: 샌드박스 정보 응답

### Enum 클래스
- SandboxStatus: 샌드박스 상태 (CREATING, RUNNING, STOPPED, PAUSED, DELETED)

## 10. AI 도메인 (AI Domain)

### NLP 하위 도메인
- **Request DTO**
  - IntentAnalysisRequest: 의도 분석 요청

- **Response DTO**
  - IntentAnalysisDto: 의도 분석 결과 응답

- **Enum 클래스**
  - IntentType: 의도 유형 (QUESTION, COMMAND, STATEMENT, GREETING)
