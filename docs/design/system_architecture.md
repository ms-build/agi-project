# 통합 AGI 시스템 아키텍처 설계

## 1. 개요

이 문서는 Spring Boot 3.4.5, Java 17, MySQL 8, Gradle을 기반으로 하는 통합 AGI 시스템의 아키텍처를 설명합니다. 이 시스템은 자연어 처리, 도구 사용, 계획 수립, 지식 및 기억 관리, 멀티모달 처리, 자가 학습 등 다양한 인공지능 기능을 제공하며, 안전한 코드 및 도구 실행을 위한 **샌드박스 환경**을 포함하는 모듈화된 구조를 가집니다.

## 2. 시스템 아키텍처 개요

통합 AGI 시스템은 다음과 같은 주요 컴포넌트로 구성됩니다:

1.  **코어 시스템**: 모든 모듈을 통합하고 조정하는 중앙 시스템
2.  **자연어 처리 엔진**: 텍스트 분석, 생성, 이해 및 대화 처리
3.  **도구 사용 프레임워크**: 외부 도구 실행 및 관리 (샌드박스 연동)
4.  **계획 수립 모듈**: 작업 계획 생성 및 실행 관리
5.  **지식 및 기억 시스템**: 정보 저장, 검색 및 추론
6.  **멀티모달 처리 모듈**: 이미지, 오디오, 비디오 처리
7.  **자가 학습 모듈**: 피드백 기반 지속적 학습
8.  **설명 가능성 모듈**: 결정 과정 해석 및 시각화
9.  **감성 지능 모듈**: 감정 인식 및 공감 기반 응답
10. **적응형 학습 모듈**: 실시간 및 개인화된 학습
11. **강화 학습 모듈**: 에이전트 기반 학습 및 최적화
12. **영역 간 지식 전이 모듈**: 다양한 도메인 간 지식 전이
13. **창의적 생성 모듈**: 콘텐츠 및 아이디어 생성
14. **샌드박스 모듈**: 격리된 환경에서 코드 및 도구 실행

## 3. 시스템 계층 구조

통합 AGI 시스템은 다음과 같은 계층 구조로 설계됩니다:

```
+-------------------+     +-------------------+     +-------------------+
|                   |     |                   |     |                   |
|   클라이언트 계층    |---->|   인터페이스 계층   |---->|   애플리케이션 계층  |
| (Vue.js Frontend) |     | (REST API/WebSocket)|   | (Core Services)   |
|                   |     |                   |     |                   |
+-------------------+     +-------------------+     +--------+----------+
                                                            | ▲
                                                            ▼ |
+-------------------+     +-------------------+     +-------------------+     +-------------------+
|                   |     |                   |     |                   |     |                   |
|   데이터 접근 계층   |<----|   도메인 계층      |<----|   통합 모듈 계층    |<----|    샌드박스 모듈    |
| (Repositories)    |     | (Domain Models)   |     | (AI Modules)      |     | (Sandbox Module)  |
|                   |     |                   |     |                   |     |                   |
+-------------------+     +-------------------+     +-------------------+     +-------------------+
        |
        ▼
+-------------------+
|                   |
|   데이터 저장 계층   |
| (MySQL, Redis)    |
|                   |
+-------------------+
```

**계층 설명**: 샌드박스 모듈은 애플리케이션 계층(코어 시스템)의 관리를 받으며, 도메인 계층의 모델을 사용하고, 통합 모듈 계층(특히 도구 사용 프레임워크)과 상호작용하여 격리된 실행 환경을 제공합니다.

## 4. 주요 컴포넌트 상세 설계

### 4.1 코어 시스템 (Core System)

코어 시스템은 모든 모듈을 통합하고 조정하는 중앙 시스템입니다:

- **모듈 관리자 (ModuleManager)**: 모든 AI 모듈 및 **샌드박스 모듈**의 등록, 초기화, 관리
- **세션 관리자 (SessionManager)**: 사용자 세션 및 컨텍스트 관리
- **이벤트 버스 (EventBus)**: 모듈 간 비동기 통신 및 이벤트 처리
- **설정 관리자 (ConfigurationManager)**: 시스템 및 모듈 설정 관리
- **모니터링 서비스 (MonitoringService)**: 시스템 상태 및 성능 모니터링
- **샌드박스 조정자 (SandboxCoordinator)**: (코어 시스템 내 역할) 샌드박스 모듈과 상호작용하여 샌드박스 생성, 할당, 자원 관리 조정

```java
@Service
public class CoreSystem {
    private final ModuleManager moduleManager;
    private final SessionManager sessionManager;
    private final EventBus eventBus;
    private final ConfigurationManager configManager;
    private final MonitoringService monitoringService;
    private final SandboxModule sandboxModule; // 샌드박스 모듈 의존성 추가

    // 시스템 초기화 및 모듈 등록
    @PostConstruct
    public void initialize() {
        // 모든 모듈 등록 및 초기화
        moduleManager.registerModule(nlpEngine);
        moduleManager.registerModule(toolFramework);
        moduleManager.registerModule(planningModule);
        moduleManager.registerModule(sandboxModule); // 샌드박스 모듈 등록
        // ... 기타 모듈 등록

        // 모듈 간 의존성 설정
        moduleManager.setupDependencies();

        // 이벤트 리스너 등록
        eventBus.registerListeners();
    }

    // 사용자 요청 처리
    public Response processRequest(Request request) {
        // 세션 컨텍스트 로드
        Context context = sessionManager.getOrCreateContext(request.getSessionId());

        // 요청 유형에 따른 적절한 모듈 선택
        Module module = moduleManager.selectModuleForRequest(request, context);

        // 샌드박스 실행이 필요한 경우 샌드박스 할당 또는 생성
        if (requiresSandbox(request)) {
            Sandbox sandbox = sandboxModule.getOrCreateSandbox(request.getUserId(), context);
            context.setSandbox(sandbox);
        }

        // 모듈을 통한 요청 처리
        Response response = module.process(request, context);

        // 세션 컨텍스트 업데이트
        sessionManager.updateContext(request.getSessionId(), context);

        return response;
    }

    private boolean requiresSandbox(Request request) {
        // 도구 실행, 코드 실행 등 샌드박스가 필요한 요청 유형인지 판단하는 로직
        return request.getType() == RequestType.TOOL_EXECUTION || request.getType() == RequestType.CODE_EXECUTION;
    }
}
```

### 4.2 자연어 처리 엔진 (NLP Engine)

(기존 내용 유지)

### 4.3 도구 사용 프레임워크 (Tool Framework)

도구 사용 프레임워크는 외부 도구 실행 및 관리를 담당하며, **샌드박스 모듈과 연동하여 안전한 실행 환경을 제공**합니다:

- **도구 실행기 (ToolExecutor)**: 도구 명령 실행 (**샌드박스 내에서 실행**)
- **도구 관리자 (ToolManager)**: 도구 등록 및 관리
- **도구 선택기 (ToolSelector)**: 상황에 적합한 도구 선택
- **도구 등록기 (ToolRegistrar)**: 새로운 도구 등록 및 설정
- **도구 설정 모듈 (ToolConfiguration)**: 도구별 설정 관리

```java
@Service
public class ToolFramework implements Module {
    private final ToolExecutor toolExecutor;
    private final ToolManager toolManager;
    private final ToolSelector toolSelector;
    private final ToolRegistrar toolRegistrar;
    private final ToolConfiguration toolConfiguration;
    private final SandboxModule sandboxModule; // 샌드박스 모듈 의존성 추가

    // 도구 등록
    @PostConstruct
    public void registerTools() {
        // 기본 도구 등록 (실행 환경 명시: e.g., SANDBOX, HOST)
        toolRegistrar.registerTool(new WebSearchTool(ExecutionEnvironment.HOST));
        toolRegistrar.registerTool(new FileOperationTool(ExecutionEnvironment.SANDBOX));
        toolRegistrar.registerTool(new DatabaseQueryTool(ExecutionEnvironment.HOST));
        toolRegistrar.registerTool(new APICallTool(ExecutionEnvironment.HOST));
        toolRegistrar.registerTool(new CalculationTool(ExecutionEnvironment.HOST));
        toolRegistrar.registerTool(new CodeExecutionTool(ExecutionEnvironment.SANDBOX)); // 코드 실행 도구 추가
        // ... 기타 도구 등록
    }

    @Override
    public Response process(Request request, Context context) {
        // 요청에서 도구 사용 의도 파악
        ToolIntent toolIntent = toolSelector.detectToolIntent(request);

        if (toolIntent != null) {
            // 적합한 도구 선택
            Tool tool = toolSelector.selectTool(toolIntent, context);

            // 도구 파라미터 추출
            Map<String, Object> parameters = toolSelector.extractParameters(request, tool);

            // 도구 실행 (샌드박스 필요 여부 확인)
            ToolResult toolResult;
            if (tool.getExecutionEnvironment() == ExecutionEnvironment.SANDBOX) {
                Sandbox sandbox = context.getSandbox();
                if (sandbox == null) {
                    throw new IllegalStateException("Sandbox not available for tool execution");
                }
                // 샌드박스 모듈을 통해 도구 실행
                toolResult = sandboxModule.executeInSandbox(sandbox.getId(), tool, parameters);
            } else {
                // 호스트 환경에서 도구 실행
                toolResult = toolExecutor.execute(tool, parameters, context);
            }

            // 결과 처리
            return new Response(toolResult);
        }

        return new Response("No tool execution required");
    }

    // 새로운 도구 동적 등록
    public void dynamicRegisterTool(ToolDefinition definition) {
        Tool tool = toolRegistrar.createToolFromDefinition(definition);
        toolRegistrar.registerTool(tool);
    }
}
```

### 4.4 계획 수립 모듈 (Planning Module)

(기존 내용 유지 - 필요시 계획 단계에 샌드박스 관련 작업 추가 가능)

### 4.5 지식 및 기억 시스템 (Knowledge & Memory System)

(기존 내용 유지)

### 4.6 멀티모달 처리 모듈 (Multimodal Processing Module)

(기존 내용 유지)

### 4.7 자가 학습 모듈 (Self-Learning Module)

(기존 내용 유지)

### 4.8 설명 가능성 모듈 (Explainability Module)

(기존 내용 유지)

### 4.9 감성 지능 모듈 (Emotional Intelligence Module)

(기존 내용 유지)

### 4.10 적응형 학습 모듈 (Adaptive Learning Module)

(기존 내용 유지)

### 4.11 강화 학습 모듈 (Reinforcement Learning Module)

(기존 내용 유지)

### 4.12 영역 간 지식 전이 모듈 (Cross-Domain Knowledge Transfer Module)

(기존 내용 유지)

### 4.13 창의적 생성 모듈 (Creative Generation Module)

(기존 내용 유지)

### 4.14 샌드박스 모듈 (Sandbox Module)

샌드박스 모듈은 격리된 환경에서 코드 및 도구를 안전하게 실행하는 기능을 제공합니다. Docker와 같은 컨테이너 기술을 활용하여 각 사용자 또는 작업별로 독립적인 실행 환경을 제공합니다.

- **샌드박스 관리자 (SandboxManager)**: 샌드박스 인스턴스(컨테이너)의 생성, 시작, 중지, 삭제 등 라이프사이클 관리
- **샌드박스 실행기 (SandboxExecutor)**: 특정 샌드박스 내에서 명령어 또는 코드 실행
- **작업 공간 관리자 (WorkspaceManager)**: 사용자별 샌드박스 내 파일 시스템 및 데이터 관리
- **자원 모니터 (ResourceMonitor)**: 샌드박스의 CPU, 메모리, 디스크 사용량 모니터링 및 제한
- **보안 정책 관리자 (SecurityPolicyManager)**: 샌드박스 네트워크 접근, 시스템 콜 제한 등 보안 정책 관리

```java
@Service
public class SandboxModule implements Module {
    private final SandboxManager sandboxManager;
    private final SandboxExecutor sandboxExecutor;
    private final WorkspaceManager workspaceManager;
    private final ResourceMonitor resourceMonitor;
    private final SecurityPolicyManager securityPolicyManager;

    // 샌드박스 생성 또는 가져오기
    public Sandbox getOrCreateSandbox(String userId, Context context) {
        // 사용자 ID 기반으로 기존 샌드박스 확인
        Optional<Sandbox> existingSandbox = sandboxManager.findSandboxByUserId(userId);
        if (existingSandbox.isPresent()) {
            return existingSandbox.get();
        }

        // 새 샌드박스 설정 생성 (기본 설정 + 사용자별 설정)
        SandboxConfig config = createSandboxConfig(userId, context);

        // 샌드박스 생성 요청
        Sandbox sandbox = sandboxManager.createSandbox(config);

        // 작업 공간 초기화
        workspaceManager.initializeWorkspace(sandbox.getId(), userId);

        return sandbox;
    }

    // 샌드박스 내에서 도구 실행
    public ToolResult executeInSandbox(String sandboxId, Tool tool, Map<String, Object> parameters) {
        // 실행할 명령어 생성
        String command = tool.prepareCommand(parameters);

        // 보안 정책 확인
        if (!securityPolicyManager.isCommandAllowed(sandboxId, command)) {
            throw new SecurityException("Command execution denied by security policy");
        }

        // 자원 제한 설정
        ResourceLimits limits = resourceMonitor.getLimits(sandboxId);

        // 샌드박스 실행기 호출
        ExecutionResult executionResult = sandboxExecutor.executeCommand(sandboxId, command, limits);

        // 자원 사용량 기록
        resourceMonitor.recordUsage(sandboxId, executionResult.getResourceUsage());

        // 결과 변환
        return tool.parseResult(executionResult);
    }

    // 샌드박스 내에서 코드 실행
    public CodeExecutionResult executeCodeInSandbox(String sandboxId, String code, Language language) {
        // 코드 실행 명령어 준비
        String command = prepareCodeExecutionCommand(code, language);

        // 보안 및 자원 제한 적용하여 실행
        ExecutionResult executionResult = sandboxExecutor.executeCommand(sandboxId, command, resourceMonitor.getLimits(sandboxId));

        // 결과 파싱
        return parseCodeExecutionResult(executionResult);
    }

    // 샌드박스 파일 관리
    public void manageFile(String sandboxId, FileOperation operation, String path, byte[] content) {
        workspaceManager.handleFileOperation(sandboxId, operation, path, content);
    }

    @Override
    public Response process(Request request, Context context) {
        // 샌드박스 모듈 자체의 직접적인 요청 처리 (예: 샌드박스 관리 API)
        switch (request.getType()) {
            case MANAGE_SANDBOX:
                return handleSandboxManagement(request);
            case EXECUTE_CODE: // 코드 실행 요청 처리
                Sandbox sandbox = context.getSandbox();
                if (sandbox == null) {
                    throw new IllegalStateException("Sandbox not available for code execution");
                }
                CodeExecutionResult result = executeCodeInSandbox(sandbox.getId(), request.getCode(), request.getLanguage());
                return new Response(result);
            default:
                throw new UnsupportedOperationException("Unsupported sandbox request type");
        }
    }

    private Response handleSandboxManagement(Request request) {
        // 샌드박스 생성, 삭제, 상태 조회 등 관리 작업 수행
        // ...
        return new Response("Sandbox management operation completed");
    }

    // 기타 헬퍼 메서드...
    private SandboxConfig createSandboxConfig(String userId, Context context) { /* ... */ return null; }
    private String prepareCodeExecutionCommand(String code, Language language) { /* ... */ return null; }
    private CodeExecutionResult parseCodeExecutionResult(ExecutionResult result) { /* ... */ return null; }
}
```

## 5. 인터페이스 계층 설계

### 5.1 RESTful API

시스템은 다음과 같은 RESTful API 엔드포인트를 제공합니다 (샌드박스 관련 API 추가):

```java
@RestController
@RequestMapping("/api") // v1 제거
public class AGIController {
    private final CoreSystem coreSystem;

    // ... (기존 API 엔드포인트 - 경로는 /api/... 로 수정됨)

    // 코드 실행 API
    @PostMapping("/code/execute")
    public ResponseEntity<CodeExecutionResponse> executeCode(@RequestBody CodeExecutionRequest request) {
        Request coreRequest = new Request(RequestType.EXECUTE_CODE, request);
        Response coreResponse = coreSystem.processRequest(coreRequest);
        return ResponseEntity.ok(new CodeExecutionResponse(coreResponse));
    }

    // 샌드박스 관리 API (예시)
    @PostMapping("/sandbox")
    public ResponseEntity<SandboxResponse> createSandbox(@RequestBody SandboxCreateRequest request) {
        Request coreRequest = new Request(RequestType.MANAGE_SANDBOX, request); // MANAGE_SANDBOX 타입 사용
        Response coreResponse = coreSystem.processRequest(coreRequest);
        return ResponseEntity.ok(new SandboxResponse(coreResponse));
    }

    @GetMapping("/sandbox/{sandboxId}")
    public ResponseEntity<SandboxResponse> getSandboxStatus(@PathVariable String sandboxId) {
        // ... 샌드박스 상태 조회 로직 ...
        return ResponseEntity.ok(new SandboxResponse(/*...*/));
    }

    @DeleteMapping("/sandbox/{sandboxId}")
    public ResponseEntity<Void> deleteSandbox(@PathVariable String sandboxId) {
        // ... 샌드박스 삭제 로직 ...
        return ResponseEntity.noContent().build();
    }

    // 샌드박스 파일 관리 API (예시)
    @PostMapping("/sandbox/{sandboxId}/files")
    public ResponseEntity<FileOperationResponse> manageFile(@PathVariable String sandboxId, @RequestBody FileOperationRequest request) {
        // ... 샌드박스 파일 관리 로직 ...
        return ResponseEntity.ok(new FileOperationResponse(/*...*/));
    }
}
```

### 5.2 WebSocket 인터페이스

WebSocket 인터페이스는 실시간 상호작용 및 샌드박스 스트리밍 출력을 위해 사용됩니다:

- **실시간 대화**: 사용자-AGI 간 실시간 메시지 교환
- **샌드박스 출력 스트리밍**: 샌드박스 내에서 실행되는 프로세스의 실시간 출력(stdout, stderr)을 클라이언트로 스트리밍
- **비동기 작업 상태 업데이트**: 장기 실행 작업(예: 모델 학습, 복잡한 계획 실행)의 진행 상태 업데이트

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue"); // 브로드캐스트 및 특정 사용자 대상 메시지
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/agi-websocket").withSockJS(); // WebSocket 엔드포인트
    }
}

@Controller
public class WebSocketController {
    private final SimpMessagingTemplate messagingTemplate;
    private final SandboxModule sandboxModule;

    // 샌드박스 출력 구독 처리 (예시)
    @MessageMapping("/sandbox/{sandboxId}/subscribe")
    public void subscribeSandboxOutput(@DestinationVariable String sandboxId, Principal principal) {
        // 사용자가 해당 샌드박스에 접근 권한이 있는지 확인
        // sandboxModule에 콜백 등록하여 출력이 발생하면 /topic/sandbox/{sandboxId}/output 으로 메시지 전송
        sandboxModule.subscribeToOutput(sandboxId, principal.getName(), output -> {
            messagingTemplate.convertAndSend("/topic/sandbox/" + sandboxId + "/output", output);
        });
    }

    // 샌드박스 입력 전송 처리 (예시)
    @MessageMapping("/sandbox/{sandboxId}/input")
    public void sendInputToSandbox(@DestinationVariable String sandboxId, String input, Principal principal) {
        // 사용자가 해당 샌드박스에 접근 권한이 있는지 확인
        sandboxModule.sendInput(sandboxId, principal.getName(), input);
    }
}
```

## 6. 데이터 저장 계층 설계

- **MySQL 8**: 구조화된 데이터(사용자 정보, 대화 기록, 지식, 계획, 도구 설정, **샌드박스 설정 및 로그**) 저장
- **Redis**: 캐싱(세션 정보, 자주 사용되는 지식), 메시지 큐(비동기 작업 처리), 실시간 데이터(샌드박스 상태) 저장
- **Vector Store (e.g., Milvus, Pinecone)**: 지식 및 텍스트 임베딩 저장 및 유사성 검색

## 7. 기술 스택

- **언어**: Java 17
- **프레임워크**: Spring Boot 3.4.5 (WebFlux 포함 가능성 고려)
- **데이터베이스**: MySQL 8, Redis
- **빌드 도구**: Gradle 8.5+
- **ORM**: JPA (Hibernate)
- **쿼리**: QueryDSL
- **유틸리티**: Lombok
- **AI 라이브러리**: DL4J, Spring AI, Weka
- **멀티모달**: JavaCV
- **컨테이너화**: Docker (샌드박스 구현 및 배포)
- **API 문서화**: OpenAPI (Swagger)
- **메시징**: WebSocket (STOMP), Kafka/RabbitMQ (선택적)

## 8. 보안 고려 사항

- **인증/인가**: JWT 기반 토큰 인증, 역할 기반 접근 제어(RBAC)
- **API 보안**: HTTPS 적용, 입력값 검증, SQL 인젝션 방지
- **샌드박스 보안**: 컨테이너 격리 강화, 네트워크 정책 제한, 리소스 제한, 시스템 콜 필터링, 악성 코드 스캔
- **데이터 보안**: 민감 데이터 암호화(저장 및 전송 시), 데이터 접근 제어
- **의존성 관리**: 라이브러리 취약점 스캔 및 관리

## 9. Vue.js 프론트엔드 연동 계획

- RESTful API 및 WebSocket을 통해 백엔드와 통신
- 인증은 JWT 토큰 사용
- 상태 관리는 Vuex 또는 Pinia 사용
- UI 컴포넌트 라이브러리(예: Vuetify, Element Plus) 활용
- 샌드박스 인터페이스 구현 (코드 에디터, 파일 탐색기, 터미널 뷰)

## 10. 확장성 및 성능 고려 사항

- **수평 확장**: 각 모듈 및 서비스를 독립적으로 확장 가능하도록 설계 (Stateless 서비스 지향)
- **비동기 처리**: 메시지 큐(Redis Pub/Sub, Kafka, RabbitMQ)를 활용하여 시간이 오래 걸리는 작업(모델 학습, 복잡한 도구 실행) 비동기 처리
- **캐싱**: Redis를 활용하여 자주 접근하는 데이터(사용자 프로필, 지식, 샌드박스 설정) 캐싱
- **데이터베이스 최적화**: 적절한 인덱싱, 쿼리 최적화, 필요시 읽기/쓰기 분리 또는 샤딩 고려
- **샌드박스 풀링**: 자주 사용되는 환경의 샌드박스를 미리 생성하여 풀링함으로써 샌드박스 시작 시간 단축
- **로드 밸런싱**: API 게이트웨이 또는 로드 밸런서를 사용하여 트래픽 분산

