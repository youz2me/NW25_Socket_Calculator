# Socket을 활용한 Client-Server 통신 기반 네트워크 계산기 애플리케이션

<br>

## 🚀 프로젝트 소개

Socket 통신 기반 계산기를 구현했습니다. \
클라이언트는 GUI를 통해 사용자 입력을 받고, 서버로 계산 요청을 전송해 결과를 받아옵니다. \
HTTP 스타일의 프로토콜과 계층화된 아키텍처를 통해 확장성과 유지보수성을 고려했습니다.

<br>

## 📚 주요 기능

- **사칙연산 지원**: 덧셈, 뺄셈, 곱셈, 나눗셈
- **실시간 네트워크 통신**: Java Socket을 통한 Client-Server 통신
- **GUI 계산기**: Swing을 활용한 Mac 기본 계산기 UI 재현
- **멀티스레드 처리**: ThreadPool을 사용한 동시 다중 클라이언트 지원
- **네트워크 로깅**: NetworkLogger 클래스 구현을 통한 상세한 요청/응답 로깅 구현
- **커스텀 예외 처리**: 계층화된 예외 구조로 명확한 에러 처리

<br>

## 🏰 아키텍처

### 전체 시스템 구조
![](https://github.com/user-attachments/assets/0cf6b6bc-e185-4aef-98dd-7cd62ff22fcf)


### 📦 Client 모듈

#### 패키지 구조
```
client/
├── view/
│   ├── CalculatorView.java         # GUI 메인 화면
│   ├── RoundButton.java            # 커스텀 원형 버튼
│   ├── ButtonType.java             # 버튼 타입 정의
│   └── exception/
│       ├── ViewException.java      # View 레이어 예외 (Base)
│       └── CalculationFailedException.java
└── service/
    ├── NetworkService.java         # 네트워크 통신 담당
    ├── NetworkConfiguration.java  # 서버 설정 관리
    └── exception/
        ├── NetworkException.java   # 네트워크 예외 (Base)
        ├── ConnectionFailedException.java
        ├── InvalidResponseException.java
        └── ServerErrorException.java
```

#### 계층 설명

##### **View Layer (Presentation)**
- **역할**: 사용자 인터페이스 및 입력 처리
- **CalculatorView**
  - Swing 기반 GUI (Mac 기본 계산기 스타일)
  - 사용자 입력 → NetworkService 호출
  - NetworkException 캐치 → ViewException으로 변환
- **예외 흐름**: `NetworkException` → `CalculationFailedException` → 사용자에게 "계산 오류" 표시

##### **Service Layer (Network)**
- **역할**: 서버와의 Socket 통신
- **NetworkService**
  - Socket 연결 생성 및 관리
  - Request 객체 생성 및 전송
  - Response 객체 수신 및 파싱
  - 통신 실패 시 NetworkException 계층 throw
- **NetworkConfiguration**
  - `server_info.dat` 파일에서 서버 정보 로드
  - Swift의 `URLSessionConfiguration` 패턴 차용

#### **의존성 방향**
```
CalculatorView
    ↓ calls
NetworkService (throws NetworkException)
    ↓ catches → throws
ViewException
```

### 🖥️ Server 모듈

#### 패키지 구조
```
server/
├── application/
│   └── MainApplication.java        # Entry Point
├── controller/
│   └── CalculatorController.java  # 요청/응답 처리
├── service/
│   ├── CalculatorService.java     # 비즈니스 로직
│   └── exception/
│       ├── ServiceException.java   # Service 예외 (Base)
│       └── DivideByZeroException.java
└── exception/
    ├── InvalidSyntaxException.java # 공통 검증 예외
    └── InvalidOperationException.java
```

#### 계층 설명

##### **Application Layer (Infrastructure)**
- **역할**: 서버 인프라 관리
- **MainApplication**
  - 서버 진입점 (`main` 메서드)
  - ServerSocket 생성 및 관리
  - ThreadPool(ExecutorService) 관리
  - 클라이언트 연결 수락 (`accept()`)
  - 각 연결마다 CalculatorController 생성 및 실행
  - 서버 생명주기 관리 (시작/종료)

##### **Controller Layer (Request Handler)**
- **역할**: 개별 클라이언트 요청 처리
- **CalculatorController**
  - `Runnable` 구현 → ThreadPool에서 실행
  - Socket I/O 스트림 관리 (BufferedReader, PrintWriter)
  - Request 파싱 (Protocol → Object)
  - CalculatorService 호출
  - Response 생성 (Object → Protocol)
  - 예외 처리 및 에러 응답 생성
  - NetworkLogger를 통한 로깅
- **생명주기**: 요청 시작 ~ 응답 완료 (단일 요청-응답 사이클)

##### **Service Layer (Business Logic)**
- **역할**: 비즈니스 로직 수행
- **CalculatorService**
  - 요청 body 파싱 (`"ADD 10 20"` → Operation, num1, num2)
  - 입력 검증 (구문, 연산자, 숫자 형식)
  - 사칙연산 수행
  - 비즈니스 규칙 적용 (0으로 나누기 검증)
  - 도메인 예외 throw (ServiceException, InvalidSyntax 등)

#### **의존성 방향**
```
MainApplication
    ↓ creates & executes
CalculatorController
    ↓ calls
CalculatorService
```

#### **Exception 처리**
```
CalculatorController
    ↓ throws InvalidSyntax/InvalidOperation (검증 실패)
    ↓ calls
CalculatorService
    ↓ throws InvalidSyntax/InvalidOperation (파싱 실패)
    ↓ throws ServiceException → DivideByZeroException (비즈니스 규칙 위반)
    ↓
CalculatorController catches
    → Response(StatusCode.BAD_REQUEST, errorType.message)
```

### 📡 Protocol 모듈

#### 패키지 구조
```
protocol/
├── request/
│   ├── Request.java                # 요청 객체
│   └── Method.java                 # HTTP Method (POST)
└── response/
    ├── Response.java               # 응답 객체
    ├── StatusCode.java             # HTTP Status Code
    └── ErrorType.java              # 에러 타입 정의
```

#### 역할
- **Client ↔ Server 통신 규약 정의**
- HTTP 스타일 프로토콜 구조
- **Request**: `Method` + `Body` (예: `POST` + `"ADD 10 20"`)
- **Response**: `StatusCode` + `Message` + `Data` (예: `200` + `"Success"` + `"30.0"`)

#### 사용처
- **Client**: NetworkService에서 Request 생성 및 Response 파싱
- **Server**: CalculatorController에서 Request 파싱 및 Response 생성

#### 프로토콜 포맷

**Request (Client → Server)**
```
[Line 1] Method (POST)
[Line 2] Body (Operation Operand1 Operand2)
```

**Response (Server → Client)**
```
[Line 1] StatusCode (200, 400, 500)
[Line 2] Message
[Line 3] Data
```

### 🔧 Core 모듈

#### 패키지 구조
```
core/
├── Operation.java                  # 연산 타입 (ADD, SUB, MUL, DIV)
└── NetworkLogger.java              # 네트워크 로깅 유틸리티
```

### 역할
- **Client와 Server 모두 사용하는 공유 컴포넌트**
- **Operation**: 연산 타입 정의 및 변환 메서드
  - `fromString("ADD")` → `Operation.ADD`
  - `fromSymbol("+")` → `Operation.ADD`
- **NetworkLogger**: MoyaLoggingPlugin 스타일 로깅
  - `logRequest()`: 요청 로깅 (emoji + timestamp)
  - `logResponse()`: 응답 로깅
  - `logError()`: 에러 로깅
  - `logConnection()`: 연결 이벤트 로깅

#### 사용처
- **Client**: CalculatorView (Operation), NetworkService (NetworkLogger)
- **Server**: CalculatorController (NetworkLogger), CalculatorService (Operation)

<br>

## 📑 SOLID 원칙 적용

### 1. Single Responsibility Principle (SRP)

각 클래스가 단일 책임만을 가지도록 해 SRP를 따르도록 구현했습니다.
- `Client` 레이어에서`CalculatorController`는 프로토콜 파싱과 응답 생성에만 집중하고, 계산 로직은 `CalculatorService`에 위임하도록 클래스를 분리했습니다.
- `Server` 레이어에서 `NetworkService`는 통신에만 집중하고, 서버 정보는 `NetworkConfiguration`이 관리하도록 구현했습니다.

```
✓ CalculatorController: 요청/응답 처리만 담당
✓ CalculatorService: 계산 로직만 담당
✓ NetworkService: 네트워크 통신만 담당
✓ NetworkConfiguration: 설정 관리만 담당
✓ NetworkLogger: 로깅만 담당
✓ CalculatorView: UI 표시 및 사용자 입력만 담당
```

### 2. Open/Closed Principle (OCP)

확장에는 열려있고, 수정에는 닫혀있도록 구현해 OCP를 따르도록 했습니다.

```java
// Operation enum으로 새로운 연산 추가 가능
public enum Operation {
    ADD("+"),
    SUB("-"),
    MUL("*"),
    DIV("/");
    // 새로운 연산 추가 시 기존 코드 수정 없이 확장 가능
}

// StatusCode enum으로 새로운 상태 코드 추가 가능
public enum StatusCode {
    OK(200, "OK"),
    BAD_REQUEST(400, "Bad Request"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");
    // 새로운 상태 코드 추가 가능
}
```

### 3. Liskov Substitution Principle (LSP)

파생 클래스가 기반 클래스를 대체할 수 있도록 구현해 LSP를 준수했습니다.

```
// Exception 계층 구조
NetworkException
├── ConnectionFailedException
├── InvalidResponseException
└── ServerErrorException

CalculatorException
├── DivideByZeroException
├── InvalidOperationException
└── InvalidSyntaxException
```

```java
// 상위 타입으로 통일된 예외 처리
catch (NetworkException e) {
    displayTextField.setText("네트워크 오류");
}

catch (CalculatorException e) {
    return new Response(StatusCode.BAD_REQUEST, e.getErrorType().message, "");
}
```

### 4. Interface Segregation Principle (ISP)

클라이언트가 사용하지 않는 인터페이스에 의존하지 않도록 구현해 ISP를 준수했습니다.

```java
// CalculatorController는 Runnable만 구현
public final class CalculatorController implements Runnable {
    @Override
    public void run() {
        // 스레드 실행 로직만 구현
    }
}
```

### 5. Dependency Inversion Principle (DIP)

고수준 모듈은 저수준 모듈에 의존하지 않도록 구현해 DIP를 준수했습니다.
- `NetworkService`는 설정이 어떻게 로드되는지 알 필요 없이 `NetworkConfiguration`을 통해 필요한 정보만 얻어옵니다.
- 테스트 시 `NetworkConfiguration`을 Mock 객체로 교체 가능합니다.

```java
// NetworkService는 구체적인 구현이 아닌 추상화된 NetworkConfiguration에 의존
public final class NetworkService {
    private final NetworkConfiguration networkConfiguration;

    public NetworkService(NetworkConfiguration networkConfiguration) {
        this.networkConfiguration = networkConfiguration;
    }
}

// CalculatorController는 구체적인 구현이 아닌 CalculatorService에 의존
public final class CalculatorController implements Runnable {
    private final CalculatorService calculatorService;

    public CalculatorController(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.calculatorService = new CalculatorService();
    }
}
```

<br>

## 🤔 설계 주요 포인트

### 1. ThreadPool을 사용한 멀티스레드 환경 구축

ThreadPool을 사용해 자원 관리와 확장성 측면에서 효율적으로 작동하는 코드를 구현했습니다.
- 성능: Thread 생성/소멸 오버헤드 감소
- 자원 관리: 동시 연결 수 제한으로 서버 안정성 확보
- 확장성: 많은 클라이언트 동시 처리 가능

```java
private static final int THREAD_POOL_SIZE = 10;
private final ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
```

### 2. 프로토콜 설계: HTTP 스타일 채택
HTTP 스타일의 프로토콜을 구현해 확장성과 명확성을 높이고 표준화된 코드를 작성했습니다.

- 확장성: 추후 다양한 Method와 StatusCode 추가 가능
- 명확성: Request/Response 구조가 명확하여 유지보수 용이
- 표준화: 널리 알려진 패턴으로 이해하기 쉬움

```
Request Format:
POST
ADD 10.0 20.0

Response Format:
200
Success
30.0
```

### 3. 예외 처리: 커스텀 예외 계층 구조

커스텀 Exception을 정의해 코드를 간결하게 작성하고, 케이스를 분리함으로써 명확성을 높이고자 했습니다.
  - 명확성: 예외 타입만으로 어떤 오류인지 즉시 파악 가능
  - 간결성: 예외 메시지 없이도 타입으로 오류 상황 전달
  - 계층화: 공통 처리와 개별 처리를 분리 가능

```
// 서버 예외
CalculatorException (base)
├── DivideByZeroException
├── InvalidOperationException
└── InvalidSyntaxException

// 클라이언트 예외
NetworkException (base)
├── ConnectionFailedException
├── InvalidResponseException
└── ServerErrorException
```

### 4. 3-Tier Architecture를 통한 계층 분리

3-Tier Architecture를 채택해 용도에 맞게 계층을 분리하고 의존성을 단방향으로 구현해 확장성과 재사용성이 높고 유지보수에 용이한 코드를 구현했습니다.

- 유지보수: 각 계층의 책임이 명확하여 수정 용이
- 테스트: 계층별로 독립적인 테스트 가능
- 확장성: 새로운 기능 추가 시 해당 계층만 수정
- 재사용성: Service 로직을 다른 Controller에서도 사용 가능

### 5. 네트워크 통신 Logger 구현

단순 System.out이 아닌 구조화된 Logger를 사용함으로써 가독성을 높이고 디버깅이 용이하도록 했습니다.
- 가독성: 이모지와 구조화된 포맷으로 로그 파악 용이
- 디버깅: 타임스탬프와 상세 정보로 문제 추적 가능
- iOS 경험 활용: Moya 라이브러리의 검증된 패턴 적용

```
----------------------------------------------------
1️⃣ ⬇️ RECEIVED REQUEST [2025-11-10 02:45:30.123]
----------------------------------------------------
2️⃣ Client: /127.0.0.1
   Method: POST
   Body: ADD 10.0 20.0
------------------- END REQUEST -------------------
```

### 6. 네이밍 컨벤션

`Spring Boot`와 `Swift`의 네이밍 컨벤션을 차용해 실제 `client`와 `server`가 통신하는 구조를 재현하고, 역할을 명확히 명시하고자 했습니다.

- **서버**
    - `MainApplication`: Spring Boot의 메인 클래스 네이밍 차용
    - `CalculatorController`: @Controller 역할 명시
    - `CalculatorService`: @Service 역할 명시

- **클라이언트**
    - `NetworkService`: iOS의 네트워크 서비스 패턴
    - `NetworkConfiguration`: URLSessionConfiguration 패턴

<br>

## 📚 프로토콜 설계

### Request Format

```
[Line 1] Method (POST)
[Line 2] Body (Operation Operand1 Operand2)
```

**예시:**
```
POST
ADD 10.0 20.0
```

### Response Format

```
[Line 1] StatusCode (200, 400, 500)
[Line 2] Message
[Line 3] Data
```

**예시:**
```
200
Success
30.0
```

### Status Codes

| Code | Name | Description |
|------|------|-------------|
| 200 | OK | 정상 처리 |
| 400 | Bad Request | 잘못된 요청 (구문 오류, 연산 오류, 0으로 나누기) |
| 500 | Internal Server Error | 서버 내부 오류 |

### Operations

| Symbol | Operation | Example |
|--------|-----------|---------|
| + | ADD | 10 + 20 = 30 |
| - | SUB | 50 - 20 = 30 |
| * | MUL | 5 * 6 = 30 |
| / | DIV | 90 / 3 = 30 |

### Error Types

| ErrorType | Description | StatusCode |
|-----------|-------------|------------|
| DIVIDE_BY_ZERO | 0으로 나누기 시도 | 400 |
| INVALID_OPERATION | 지원하지 않는 연산 | 400 |
| INVALID_SYNTAX | 잘못된 요청 형식 | 400 |

<br>

## 🚀 실행 방법

### 1. 프로젝트 클론

```bash
git clone <repository-url>
cd NW25_Socket_Calculator
```

### 2. 컴파일

```bash
# bin 디렉토리 생성
mkdir -p bin

# 전체 소스 컴파일
find src -name "*.java" -print0 | xargs -0 javac -d bin -encoding UTF-8
```

### 3. 서버 설정 파일 생성

`server_info.dat` 파일을 프로젝트 루트에 생성:

```
호스트 주소
포트 번호
```

### 4. 서버 실행

```bash
java -cp bin server.application.MainApplication
```

**출력 예시:**
```
Calculator Server starting on port 000...
Server started successfully
Waiting for client connections...
```

### 5. 클라이언트 실행

```bash
java -cp bin client.view.CalculatorView
```

GUI 계산기 창이 열립니다.

### 6. 서버 종료

```bash
# 실행 중인 서버 프로세스 확인
ps aux | grep MainApplication

# 프로세스 종료
kill <PID>
```

<br>

## 🏰 프로젝트 구조

```
NW25_Socket_Calculator/
├── src/
│   ├── client/
│   │   ├── presentation/        # 클라이언트 뷰
│   │   │   ├── CalculatorView.java
│   │   │   ├── RoundButton.java
│   │   │   ├── ButtonType.java
│   │   │   └── exception/       # View 레이어 예외
│   │   │       ├── ViewException.java
│   │   │       └── CalculationFailedException.java
│   │   └── service/             # 클라이언트 서비스
│   │       ├── NetworkService.java
│   │       ├── NetworkConfiguration.java
│   │       └── exception/       # Service 레이어 예외 (네트워크)
│   │           ├── NetworkException.java
│   │           ├── ConnectionFailedException.java
│   │           ├── InvalidResponseException.java
│   │           └── ServerErrorException.java
│   ├── server/
│   │   ├── application/         # 서버 애플리케이션
│   │   │   └── MainApplication.java
│   │   ├── controller/          # 서버 컨트롤러
│   │   │   └── CalculatorController.java
│   │   ├── service/             # 서버 서비스
│   │   │   ├── CalculatorService.java
│   │   │   └── exception/       # Service 레이어 예외
│   │   │       ├── ServiceException.java
│   │   │       └── DivideByZeroException.java
│   │   └── exception/           # 공통 검증 예외
│   │       ├── InvalidSyntaxException.java
│   │       └── InvalidOperationException.java
│   ├── protocol/                # 통신 프로토콜 (Client ↔ Server 공유)
│   │   ├── request/             # 요청 프로토콜
│   │   │   ├── Request.java
│   │   │   └── Method.java
│   │   └── response/            # 응답 프로토콜
│   │       ├── Response.java
│   │       ├── StatusCode.java
│   │       └── ErrorType.java
│   └── core/                    # 공유 컴포넌트 (Client ↔ Server 공유)
│       ├── Operation.java
│       └── NetworkLogger.java
├── bin/                         # 컴파일된 클래스 파일
├── server_info.dat              # 서버 설정 파일 (git ignored)
├── .gitignore
└── README.md
```

---

## ✒️ 네트워크 로그 예시

### 정상 요청/응답

**클라이언트 로그:**
```
----------------------------------------------------
1️⃣ ⬆️ SENDING REQUEST [2025-11-10 02:45:30.123]
----------------------------------------------------
2️⃣ Client: localhost:503
   Method: POST
   Body: ADD 10.0 20.0
------------------- END REQUEST -------------------
------------------- RESPONSE -------------------
3️⃣ ⬇️ RECEIVED RESPONSE [2025-11-10 02:45:30.125]
   Client: localhost:503
   Status Code: [200]
   Message: Success
4️⃣ Data: 30.0
------------------- END RESPONSE -------------------
```

**서버 로그:**
```
----------------------------------------------------
1️⃣ ⬇️ RECEIVED REQUEST [2025-11-10 02:45:30.124]
----------------------------------------------------
2️⃣ Client: /127.0.0.1
   Method: POST
   Body: ADD 10.0 20.0
------------------- END REQUEST -------------------
------------------- RESPONSE -------------------
3️⃣ ⬆️ SENDING RESPONSE [2025-11-10 02:45:30.125]
   Client: /127.0.0.1
   Status Code: [200]
   Message: Success
4️⃣ Data: 30.0
------------------- END RESPONSE -------------------
Client disconnected: /127.0.0.1
```

### 에러 처리 (0으로 나누기)

**클라이언트 로그:**
```
----------------------------------------------------
1️⃣ ⬆️ SENDING REQUEST [2025-11-10 02:46:15.456]
----------------------------------------------------
2️⃣ Client: localhost:503
   Method: POST
   Body: DIV 10.0 0.0
------------------- END REQUEST -------------------
------------------- RESPONSE -------------------
3️⃣ ⬇️ RECEIVED RESPONSE [2025-11-10 02:46:15.458]
   Client: localhost:503
   Status Code: [400]
   Message: Cannot divide by zero
4️⃣ Data:
------------------- END RESPONSE -------------------
```

**서버 로그:**
```
----------------------------------------------------
1️⃣ ⬇️ RECEIVED REQUEST [2025-11-10 02:46:15.457]
----------------------------------------------------
2️⃣ Client: /127.0.0.1
   Method: POST
   Body: DIV 10.0 0.0
------------------- END REQUEST -------------------
❌ ERROR in Calculator [2025-11-10 02:46:15.457]
   Type: DivideByZeroException
   Message: Cannot divide by zero
------------------- END ERROR -------------------
------------------- RESPONSE -------------------
3️⃣ ⬆️ ERROR RESPONSE [2025-11-10 02:46:15.458]
   Client: /127.0.0.1
   Status Code: [400]
   Message: Cannot divide by zero
4️⃣ Data:
------------------- END RESPONSE -------------------
```

---
