# IntelliJ 프로젝트 설정 가이드

이 문서는 IntelliJ IDEA에서 AGI 샌드박스 프로젝트를 설정하는 방법을 안내합니다.

## 사전 요구사항

- IntelliJ IDEA (최신 버전 권장)
- JDK 17 설치
- MySQL 8 설치 및 실행
- Gradle 8.5 이상 설치

## 프로젝트 불러오기

1. IntelliJ IDEA를 실행합니다.
2. 시작 화면에서 `Open` 또는 `File > Open`을 선택합니다.
3. 프로젝트 루트 디렉토리(`agi-project`)를 선택하고 `OK`를 클릭합니다.
4. Gradle 프로젝트로 인식되면 자동으로 의존성을 다운로드하고 프로젝트를 구성합니다.

## JDK 설정

1. `File > Project Structure`를 선택합니다.
2. `Project` 섹션에서 `Project SDK`를 JDK 17로 설정합니다.
3. `Project language level`을 17로 설정합니다.
4. `Apply` 및 `OK`를 클릭합니다.

## Gradle 설정

1. `File > Settings` (Windows/Linux) 또는 `IntelliJ IDEA > Preferences` (macOS)를 선택합니다.
2. `Build, Execution, Deployment > Build Tools > Gradle`로 이동합니다.
3. `Gradle JVM`이 JDK 17로 설정되어 있는지 확인합니다.
4. `Build and run using`과 `Run tests using`이 모두 `Gradle`로 설정되어 있는지 확인합니다.
5. `Apply` 및 `OK`를 클릭합니다.

## 플러그인 설치

다음 플러그인을 설치하면 개발 생산성을 높일 수 있습니다:

1. `File > Settings > Plugins`로 이동합니다.
2. 다음 플러그인을 검색하여 설치합니다:
   - Lombok
   - JPA Buddy
   - Database Navigator
   - Spring Boot Assistant

## 데이터베이스 연결 설정

1. `View > Tool Windows > Database`를 선택합니다.
2. `+` 버튼을 클릭하고 `Data Source > MySQL`을 선택합니다.
3. 다음 정보를 입력합니다:
   - Host: localhost
   - Port: 3306
   - User: root
   - Password: (설정한 비밀번호)
   - Database: agi
4. `Test Connection`을 클릭하여 연결을 테스트합니다.
5. `Apply` 및 `OK`를 클릭합니다.

## 애플리케이션 실행

1. `src/main/java/com/agi/sandbox/AgiSandboxApplication.java` 파일을 엽니다.
2. 클래스 옆의 실행 버튼(녹색 삼각형)을 클릭하거나 마우스 오른쪽 버튼을 클릭하고 `Run 'AgiSandboxApplication'`을 선택합니다.
3. 애플리케이션이 성공적으로 시작되면 콘솔에 Spring Boot 로고와 시작 로그가 표시됩니다.

## 문제 해결

### QueryDSL 생성 파일 인식 문제

IntelliJ에서 QueryDSL의 Q클래스를 인식하지 못하는 경우:

1. `./gradlew clean build`를 실행하여 Q클래스를 생성합니다.
2. `File > Invalidate Caches / Restart`를 선택합니다.
3. IntelliJ가 재시작된 후 프로젝트를 다시 로드합니다.

### Lombok 어노테이션 인식 문제

Lombok 어노테이션이 작동하지 않는 경우:

1. `File > Settings > Build, Execution, Deployment > Compiler > Annotation Processors`로 이동합니다.
2. `Enable annotation processing`을 체크합니다.
3. `Apply` 및 `OK`를 클릭합니다.

### DL4J 의존성 문제

DL4J 관련 의존성 문제가 발생하는 경우:

1. `File > Invalidate Caches / Restart`를 선택합니다.
2. IntelliJ가 재시작된 후 `./gradlew --refresh-dependencies`를 실행합니다.
