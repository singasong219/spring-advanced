# SPRING ADVANCED
## Lv6. AuthException 클래스 추가

### 1. 문제 인식 및 정의
프로젝트에 `AuthException`클래스가 없어서 인증/인가 오류 상황에서 `InvalidRequestException`으로 대체하고 있었다.
인증 오류와 잘못된 요청 오류는 성격이 다른데 같은 예외 클래스로 처리하면 오류의 의미가 불명확해진다.

### 2. 해결 방안

#### 2-1. 의사결정 과정
- 인증/인가 오류는 별도의 예외 클래스로 분리하는 것이 명확하다.
- `AuthException`클래스를 새로 만들어 `UNAUTHORIZED(401)` 로 처리하도록 한다.

#### 2-2. 해결 과정
- `AuthException.java` 클래스 신규 생성
- `AdminAuthInterceptor` 클래스에서 `InvalidRequestException` 대신 `AuthException` 사용
- `GlobalExceptionHandler`클래스에서 `AuthException`을 `UNAUTHORIZED(401)`로 처리

### 3. 해결 완료

#### 3-1. 회고
예외 클래스를 목적에 맞게 분리함으로써 오류 상황을 더 명확하게 표현할 수 있게 되었다.
인증 오류는 401, 잘못된 요청은 400으로 HTTP 상태 코드도 정확하게 반환된다.

#### 3-2. 전후 비교
- Before: 인증 오류 시 `InvalidRequestException` 사용 → 400 BAD_REQUEST 반환
- After: 인증 오류 시 `AuthException` 사용 → 401 UNAUTHORIZED 반환
