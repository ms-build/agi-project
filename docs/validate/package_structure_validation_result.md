# 패키지 구조 검증 및 보완 작업 결과

## 완료된 작업

### 엔티티 ID 타입 통일
- [x] Conversation, Message, Feedback, Intent 등 여러 엔티티의 ID 타입을 String에서 Long으로 통일
- [x] 모든 엔티티에 @GeneratedValue(strategy = GenerationType.IDENTITY) 추가하여 자동 생성되도록 설정

### DTO-엔티티 매핑 수정
- [x] UserDto, ConversationDto, MessageDto, PlanDto, PlanStepDto 등에서 엔티티와 필드명/타입 불일치 문제 해결
- [x] PlanStepDto에서 엔티티의 실제 필드명(description, orderIndex)에 맞게 매핑 로직 수정

### Enum 타입 통일
- [x] Plan.PlanStatus 내부 enum을 외부 enums 패키지로 분리하여 DTO와 엔티티가 동일한 타입을 참조하도록 수정
- [x] MessageDto의 role 필드를 MessageType enum으로 변경하여 타입 일관성 확보

### 누락된 엔티티 생성
- [x] Knowledge, KnowledgeTag 등 누락된 엔티티를 설계 문서에 맞게 생성
- [x] 필요한 DTO, Repository 인터페이스 추가

### 컴파일 및 빌드 테스트
- [x] 모든 컴파일 오류 해결
- [x] 빌드 성공 확인

## 남은 작업
- [ ] Controller 구현체 작성 (사용자 요청에 따라 미구현)
- [ ] Service 구현체 작성 (사용자 요청에 따라 미구현)
- [ ] 단위 테스트 작성
- [ ] 통합 테스트 작성
