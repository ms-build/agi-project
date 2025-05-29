# 패키지 구조 불일치 및 누락 파일 목록

## 1. Learning 도메인

### 1.1 Learning/Evaluation
- 패키지 구조만 존재하고 실제 파일이 전혀 없음
- 필요한 파일:
  - dto/request/EvaluationRequest.java
  - dto/response/EvaluationResultDto.java
  - entity/Evaluation.java
  - enums/EvaluationMetric.java
  - repository/EvaluationRepository.java

### 1.2 Learning/Model
- 패키지 구조만 존재하고 실제 파일이 전혀 없음
- 필요한 파일:
  - dto/request/ModelTrainingRequest.java
  - dto/response/ModelInfoDto.java
  - entity/Model.java
  - enums/ModelType.java
  - repository/ModelRepository.java

### 1.3 Learning/Training
- 패키지 구조만 존재하고 실제 파일이 전혀 없음
- 필요한 파일:
  - dto/request/TrainingRequest.java
  - dto/response/TrainingStatusDto.java
  - entity/Training.java
  - enums/TrainingStatus.java
  - repository/TrainingRepository.java

## 2. System 도메인

### 2.1 System/Logging
- 패키지 구조만 존재하고 실제 파일이 전혀 없음
- 필요한 파일:
  - dto/request/LogRequest.java
  - dto/response/LogDto.java
  - entity/Log.java
  - enums/LogLevel.java
  - repository/LogRepository.java

### 2.2 System/Task
- 패키지 구조만 존재하고 실제 파일이 전혀 없음
- 필요한 파일:
  - dto/request/TaskRequest.java
  - dto/response/TaskDto.java
  - entity/Task.java
  - enums/TaskStatus.java
  - repository/TaskRepository.java

## 3. 기타 도메인 검토 필요
- Knowledge 도메인
- Multimodal 도메인
- User 도메인
- Tool 도메인
- Plan 도메인
- Conversation 도메인
- Sandbox 도메인
