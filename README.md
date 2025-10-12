# URL Shortener System
> 대규모 트래픽 환경에서의 **성능 개선, 새로운 기술 스택 실험, 트레이드오프(Trade-off) 기반의 기술 판단, 그리고 확장성 있는 아키텍처 설계 역량 강화**를 목표로 한 사이드 프로젝트

## 0. 프로젝트 개요

이 프로젝트는 Bitly처럼 긴 URL을 짧게 변환해주는 **URL 단축 서비스**를 직접 구현한 것입니다.

단순히 “짧은 링크를 만드는 기능”에 그치지 않고, 제한된 서버 자원에서 트래픽이 폭증하는 상황을 가정하고

- 수직적 확장(Scale-up)이 아닌 수평적 확장(Scale-out) 중심의 설계
- 유연하고 확장성 있는 코드 작성
- 수치로 증명하는 성능 개선
- 가용성과 확장성을 고려한 시스템 설계

같은 요소들을 목표로 합니다.


### 0.1 기술 스택
| 구분                   | 사용 기술                             |
| -------------------- |-----------------------------------|
| **Backend**          | Java, Spring Boot, Spring Data JPA |
| **Database / Cache** | MySQL, Redis                      |
| **DevOps / Infra**   | Docker     |
| **Test / Load**      | JUnit5, JMeter                |

## 1. 시스템 아키텍처
<img width="5008" height="2000" alt="image" src="https://github.com/user-attachments/assets/437d4db7-e038-4a8a-9d8b-eee20f0837d8" />




## 2. 주요 기능 및 기술적 도전 과제

### 2.1 비동기 도입을 통한 처리량(TPS) 55.9% 향상(Write 처리량 향상)

초기 동기 방식 URL 단축 API는 DB I/O 작업이 완료될 때까지 사용자의 요청을 블로킹하여, 높은 트래픽 상황에서 처리량(TPS)의 한계가 명확했습니다. 이를 개선하기 위해 비동기 처리 모델을 도입하였습니다.

#### 1) 스레드 풀 기반 비동기 아키텍처 도입
- 도전 과제
  - 초기 API는 동기 방식으로 동작하여, DB 저장과 같은 I/O Bound 작업이 완료될 때까지 요청 스레드가 블로킹되는 구조
  - 부하 테스트 결과, 트래픽이 증가함에 따라 커넥션 풀에서 커넥션을 획득하기 위한 대기 시간이 급증(Connection Acquire Time)했고, 이로 인해 응답 시간 지연 및 처리량 저하 현상 발생
  - 사용자는 DB 작업이 끝날 때까지 기다려야 했고, 이는 TPS를 저하시키는 원인

<img width="800" height="400" alt="SCR-20251012-omsn" src="https://github.com/user-attachments/assets/7eed6f39-b5d1-4907-8c13-e24e5c2ad94e" />

> 부하 테스트 중 Connection Acquire Time 급증

- 해결 방안
  - `@Async`와 `ThreadPoolTaskExecutor`를 활용하여 DB 영속화 로직을 비동기 방식으로 전환.
  - API 요청을 처리하는 메인 스레드는 DB 저장 작업을 별도의 스레드 풀에 위임하고, 즉시 사용자에게 응답을 반환하도록 로직 변경. 이를 통해 DB I/O 작업을 URL 단축 작업과 분리.

- 결과 및 기대 효과
  - 처리량 향상: **기존 783 TPS 수준에서 개선 후 1221 TPS 달성. 약 55.9% 개선**
<img width="3321" height="876" alt="image" src="https://github.com/user-attachments/assets/bac57c15-dbd6-42cc-aa7b-c7473fe4e112" />

  - 기존 로직
    - URL 단축 요청 -> 검증 로직 실행 -> URL 단축 로직 실행 -> 원본 URL- 단축 URL 매핑 정보 DB 저장 -> 응답
  - 변경 로직
    - URL 단축 요청 -> 검증 로직 실행 -> URL 단축 로직 실행 -> 응답
    - 원본 URL- 단축 URL 매핑 정보 DB 저장은 비동기로 진행

#### 2) 분산 락(Distributed Lock)을 활용한 데이터 정합성 보장
- 도전 과제
  -   "동일한 원본 URL은 항상 동일한 단축 URL로 매핑되어야 한다"는 핵심 비즈니스 요구사항이 존재. 하지만, 높은 트래픽 환경에서 동일한 원본 URL에 대한 단축 요청이 동시에 여러 스레드(또는 여러 서버 인스턴스)에 들어올 경우 Race Condition이 발생 가능성 존재
  -   이로 인해 하나의 원본 URL에 대해 여러 개의 다른 단축 URL이 생성되어 DB에 중복 저장되는 데이터 불일치 문제가 발생 가능 
- 해결 방안
  - 단일 인스턴스를 넘어 분산 환경에서도 동시성을 제어하기 위해 Redisson 분산 락을 도입.
  - 사용자의 단축 요청이 들어오면, 원본 URL을 Key로 사용하여 Lock을 획득하는 로직을 추가.
  - 락을 획득한 스레드만이 DB 조회 및 신규 URL 생성/저장 로직을 수행하도록 보장. 다른 스레드들은 락이 해제될 때까지 대기하거나, 즉시 실패 응답을 보내도록 처리하여 임계 영역(Critical Section)을 안전하게 보호.
- 결과 및 성과
  - 데이터 정합성 확보: 동시 요청으로 인한 데이터 중복 생성을 원천적으로 차단하여 서비스의 신뢰성과 데이터 무결성을 확보.
  - 안정적인 아키텍처 구축: 향후 애플리케이션을 여러 인스턴스로 수평 확장(Scale-out)하더라도 데이터 정합성 문제가 발생하지 않는 안정적인 서비스 운영 기반 마련.

#### 3) 시스템 안정성 확보를 위한 스레드 풀 제어
- 도전 과제
  - 비동기 전환으로 처리량을 높이는 데는 성공했지만, 예상보다 더 높은 부하가 발생하자 스레드 풀의 작업 큐가 가득 차 `TaskRejectedException`이 발생하며 시스템이 불안정해지는 새로운 문제 발생.

- 해결 방안
  - 큐가 가득 찼을 때 예외가 아닌 동기 방식으로 전환하기위해 스레드 풀의 거부 정책(Rejection Policy)을 기본 `AbortPolicy`에서 `CallerRunsPolicy`로 변경.
  - 이 정책은 작업 큐가 가득 찼을 때, 작업을 요청한 API 스레드가 직접 해당 작업을 동기적으로 처리하도록 유도. 백프레셔(Backpressure)를 적용하여 유입되는 트래픽의 속도를 조절하는 효과.

- 결과 및 기대 효과
  - 시스템 회복탄력성(Resilience) 확보: 예측 불가능한 트래픽 폭주 상황에서도 요청을 실패시키지 않고, 처리 속도를 늦추는 방식으로 시스템을 보호하여 서비스의 가용성과 안정성 향상.

```mermaid
sequenceDiagram
    participant User as User (클라이언트)
    participant Controller as UrlShorteningController
    participant Service as ShorteningServiceImpl
    participant Lock as RedissonClient (RLock)
    participant Async as AsyncUrlMappingService
    participant Repo as UrlMappingRepository
    participant DB as Database

    User->>Controller: URL 단축 요청 (originalUrl)
    Controller->>Service: getOrCreateShortUrl(originalUrl)
    Service->>Service: 검증 로직 실행 (shortenChain)
    Service->>Repo: findByOriginalUrl(originalUrl)
    Repo-->>Service: Optional.empty (신규 URL)

    Service->>Lock: tryLock(originalUrl)
    alt 락 획득 성공
        Service->>Service: 단축 코드 생성 (Base62 + IdSupplier)
        Service->>Async: saveToDbAsync(originalUrl, shortCode) (비동기)
        note right of Async: 별도 Thread에서 DB 저장 수행
        Service->>Lock: unlock()
        Service-->>Controller: shortUrl 반환
        Controller-->>User: 응답 (shortUrl)
        Async->>Repo: save(new UrlMapping(originalUrl, shortCode))
        Repo-->>Async: 저장 완료
    else 락 획득 실패
        Service-->>Controller: 예외 반환 ("잠시 후 다시 시도해주세요.")
        Controller-->>User: 오류 응답
    end
```

### 2.2 디자인 패턴을 활용한 유연하고 확장성 있는 설계
단순히 기능 요구사항을 만족시키는 코드를 넘어, 향후 변경에 유연하게 대처하고 유지보수 비용을 낮출 수 있는 구조를 설계하는 것을 목표로 했습니다. 이를 위해 두 가지 주요 디자인 패턴을 적용했습니다.

#### 1) 책임 연쇄 패턴(Chain-of-Responsibility)을 통한 검증 로직의 확장
- 도전 과제
  - URL 단축 요청, 리다이랙션 요청이 들어왔을 때 여러 검증 과정을 거쳐야 함.
  - 예를 들어, URL 형식 검증, 자체 URL 순환 단축(무한 리다이랙션) 방지 검증, 블랙리스트 검증 등 다양한 규칙이 필요할 수 있다. 이러한 검증 로직이 서비스 코드 내에 if-else 블록으로 얽혀있다면, 새로운 검증 규칙을 추가하거나 순서를 변경하기가 매우 어렵고 복잡해 질 것임.
- 해결 방안
  - 책임 연쇄 패턴(Chain-of-Responsibility)을 도입하여 각 검증 로직을 독립적인 객체로 분리.
  - 모든 검증 객체가 구현해야 하는 `ValidationHandler` 인터페이스를 정의.
  - `UrlFormatValidator`, `CircularShorteningValidator` 등 각 검증 책임을 가진 구체적인 핸들러 클래스를 구현.
  - 이 핸들러들을 체인(Chain)으로 연결하여, 요청이 들어오면 체인의 첫 번째 핸들러부터 순서대로 검증을 수행하도록 구성했음. 각 핸들러는 검증을 통과하면 다음 핸들러로 요청을 전달하고, 실패하면 즉시 처리를 중단.
- 결과 및 기대 효과
  - SRP(단일 책임 원칙) 준수: 각 검증 로직이 별도의 클래스로 분리되어 코드의 가독성과 유지보수성이 향상.
  - 유연한 확장 및 재구성: 새로운 검증 규칙이 필요할 때 새로운 핸들러 클래스를 구현하여 체인에 추가하기만 하면 된다. 또한, 설정 파일을 통해 검증 순서를 동적으로 변경하는 것도 가능해져 비즈니스 요구사항 변화에 유연하게 대응 가능.
#### 2) 전략 패턴(Strategy Pattern)을 통한 ID 생성 방식의 유연한 교체
- 도전 과제 
  - URL 단축기의 핵심인 고유 ID 생성 방식은 시스템 환경에 따라 최적의 전략이 다르다.
  - 분산/운영 환경: 여러 서버에서 동시에 ID를 생성해도 충돌이 없어야 하니 중앙화된 Redis의 INCR 같은 원자적 연산 필요.
  - 로컬/개발 환경: 간단한 테스트를 위해 외부 의존성(Redis) 없이 메모리 기반으로 빠르게 동작하는 방식이 효율적.
- 해결 방안
  - 전략 패턴(Strategy Pattern)을 적용하여 ID 생성 로직을 추상화.
  - `IdSupplier` 라는 인터페이스를 정의하고, 실제 생성 로직을 담은 구현체(`RedisIdSupplier`, `InMemoryIdSupplier`)를 추가.
  - 서비스 로직은 `IdSupplier` 인터페이스에만 의존하며, 실제 어떤 구현체를 사용할지는 Spring의 Profile(@Profile)이나 환경설정(application.yml)을 통해 외부에서 주입하도록 설계.
- 결과 및 기대 효과
  - 유연성 및 테스트 용이성: 운영 환경과 개발 환경의 ID 생성 전략을 코드를 수정하지 않고 손쉽게 전환 가능.
  - OCP(개방-폐쇄 원칙) 준수: 새로운 ID 생성 방식(ex. `DatabaseIdSupplier`)이 필요할 경우, 기존 코드를 수정하지 않고 `IdSupplier`의 새로운 구현체만 추가하면 되므로 용이한 확장성.

## 3. 기능 요구사항 정리
### 3.1 URL 단축 기능
- 입력
  - 사용자는 원본 URL을 입력한다.
- 처리
  - 시스템은 입력된 URL의 유효성을 검증한다. (e.g., http:// 또는 https://로 시작)
  - 고유하고 충돌 없는 코드(Short Code)를 생성한다. (예: aB1cD2e)
  - 원본 URL과 짧은 키를 데이터베이스에 매핑하여 저장한다.
- 출력
  - 생성된 전체 단축 URL을 사용자에게 반환한다. (예: https://jeonsonghun.com/aB1cD2e)
- 제약 조건 
  - 이미 단축된 자체 서비스 URL은 다시 단축할 수 없다. (무한 리다이렉션 방지)
### 3.2 단축 URL 리다이랙션 기능
- 입력
  - 사용자가 브라우저를 통해 단축 URL에 접속한다.
- 처리
  - 시스템은 URL의 PathVariable의 ShortCode를 파싱하여 데이터베이스에서 원본 URL을 조회한다.
  - 해당 ShortCode가 존재하면, 원본 URL로 HTTP 301 Moved Permanently 리다이렉션을 수행한다.
  - 해당 ShortCode가 존재하지 않으면, 404 Not Found 페이지를 반환한다.
- 부가 처리
  - 리다이렉션이 성공할 때마다 '클릭 로그'를 기록한다.
### 3.3 단축 URL 클릭 로그 기록 및 통계 조회 기능
- 로그 기록
  - 리다이렉션이 발생할 때마다 다음 정보를 수집하여 별도의 로그 저장소나 메시지 큐로 전송한다.
    - 단축 코드 (ShortCode)
    - 클릭 발생 시간 (Timestamp)
    - 접속 IP 주소 (IP Address)
    - 사용자 환경 정보 (User-Agent)
    - 유입 경로 (Referrer)
- 통계 조회
  - 사용자는 자신이 생성한 단축 URL의 통계 데이터를 조회할 수 있다.
  - 제공 데이터
    - 총 클릭 수: 해당 URL이 리다이렉션된 전체 횟수
    - 시간별 클릭 수: 일별 또는 시간별 클릭 수 추이 (그래프 시각화)
    - 유입 경로(Referrer) TOP 5: 어떤 웹사이트를 통해 유입되었는지 상위 5개 표시
    - 사용자 환경(User-Agent) 분석: 브라우저 종류, 운영체제(OS)별 접속 비율

## 4. ERD
```mermaid
erDiagram
    direction LR

    URL_MAPPING {
        BIGINT id PK
        VARCHAR originalUrl
        VARCHAR shortCode
        DATETIME createdAt
        DATETIME expiredAt
    }

    CLICK_LOG {
        BIGINT id PK
        BIGINT urlMaapingId FK
        VARCHAR ipAddress
        VARCHAR userAgent
        VARCHAR referrer
        VARCHAR acceptLanguage
        DATETIME clickedAt
    }

    URL_MAPPING ||--o{ CLICK_LOG : "One To Many"
    CLICK_LOG ||--o{ URL_MAPPING : "Many To One"  

```













