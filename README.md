# URL Shortener System

## 0. 프로젝트 개요

> 이 프로젝트는 [Bitly](https://bitly.com/)처럼 긴 URL을 짧게 변환해주는 URL 단축 서비스입니다.

### 계속 추가되는 고민 지점들
- 제한된 서버 자원에서 트래픽이 폭증하는 상황에 대한 고민
- 어떻게 패키지를 구성하고 클래스를 정의해야 깔끔한 코드가 만들어지는가에 대한 고민
- 핵심 기능의 유연한 확장과 유지보수가 용이한 구조 고민
- 동시 요청 상황에서 발생하는 데이터 부정합 문제를 해결하고 비즈니스 규칙의 일관성을 지키기 위한 고민
- 트래픽 폭주 상황에서도 시스템 다운 없이 안정적으로 요청을 처리하는 방법 고민
- 수평적 확장이 가능한 시스템 아키텍처를 설계하고 고민

### 여담
- 처음에는 '가상 면접 사례로 배우는 대규모 시스템 설계 기초'책에서 본 간단한 예시를 구현해보자 라는 가벼운 마음으로 시작
- 클린 코드, 비동기 도입으로 생긴 사이드 이펙트 등 고려할게 생각보다 많음
- 공부를 위한 프로젝트이므로, 공부하며 공유할 만한 부분은 [블로그](https://www.jeongseonghun.com/)나 README로 공유할 예정

### 기술 스택
Java, Spring Boot, JPA, MySQL, Redis, JUnit, JMeter

## 1. Project Structure
```
com.jeongseonghun.urlshortener
 ┣ 📂 api            → 외부의 요청을 받고 응답하는 웹 계층(@RestController)
 ┣ 📂 application    → 실제 비즈니스 로직을 처리하는 서비스 계층(@Service)
 ┣ 📂 config         → 애플리케이션의 설정을 담당
 ┣ 📂 domain         → 가장 핵심적인 도메인 모델과 비즈니스 규칙 정의(Entities, Interfaces)
 ┣ 📂 infrastructure → 도메인 설계 및 규칙들 구현을 담당하는 계층(@Component) 
 ┣ 📂 repository     → 데이터베이스와의 통신을 담당하는 데이터 영속성 계층(@Repository)
 ┣ 📂 support        → 특정 도메인에 종속되지 않는 범용 유틸리티 클래스 모음
 ┗ 🅒 UrlShortenerApplication.java
```

## 2. System Design
### 확장성을 고려한 구조 (To-Be)
<img width="2364" height="1204" alt="image" src="https://github.com/user-attachments/assets/5842043c-bcb1-486a-850f-11910d2153ee" />



## 3. Challenge

### 3.1 스레드 풀 기반 비동기 아키텍처 도입
- 도전 과제
  - 초기 API는 동기 방식으로 동작하여, DB 저장과 같은 I/O Bound 작업이 완료될 때까지 요청 스레드가 블로킹되는 구조
  - 부하 테스트 결과, 트래픽이 증가함에 따라 커넥션 풀에서 커넥션을 획득하기 위한 대기 시간이 급증(Connection Acquire Time)했고, 이로 인해 응답 시간 지연 및 처리량 저하 현상 발생
  - 사용자는 DB 작업이 끝날 때까지 기다려야 했고, 이는 TPS를 저하시키는 원인

<img width="800" height="400" alt="SCR-20251012-omsn" src="https://github.com/user-attachments/assets/7eed6f39-b5d1-4907-8c13-e24e5c2ad94e" />

> 부하 테스트 중 Connection Acquire Time 급증

- 해결 과정
  - `@Async`와 `ThreadPoolTaskExecutor`를 활용하여 DB 영속화 로직을 비동기 방식으로 전환
  - API 요청을 처리하는 메인 스레드는 DB 저장 작업을 별도의 스레드 풀에 위임하고, 즉시 사용자에게 응답을 반환하도록 로직 변경. 이를 통해 DB I/O 작업을 URL 단축 작업과 분리
 
  - 기존 로직
    - URL 단축 요청 -> 검증 로직 실행 -> URL 단축 로직 실행 -> 원본 URL- 단축 URL 매핑 정보 DB 저장 -> 응답
  - 변경 로직
    - URL 단축 요청 -> 검증 로직 실행 -> URL 단축 로직 실행 -> 응답
    - 원본 URL - 단축 URL 매핑 정보 DB 저장은 비동기로 진행
  
<img width="1200" height="500" alt="img_2" src="https://github.com/user-attachments/assets/b4a55152-e24a-476d-bd15-36a852d64526" />

- 결과
  - 처리량 향상: **기존 783 TPS 수준에서 개선 후 1221 TPS 달성. 약 55.9% 개선**
<img width="3321" height="876" alt="image" src="https://github.com/user-attachments/assets/bac57c15-dbd6-42cc-aa7b-c7473fe4e112" />


### 3.2 분산 락(Distributed Lock)을 활용한 비즈니스 규칙 준수
- 도전 과제
  -   "동일한 원본 URL은 항상 동일한 단축 URL로 매핑되어야 한다"는 핵심 비즈니스 요구사항이 존재. 하지만, 높은 트래픽 환경에서 동일한 원본 URL에 대한 단축 요청이 동시에 여러 스레드(또는 여러 서버 인스턴스)에 들어올 경우 Race Condition이 발생 가능성 존재
  -   이로 인해 하나의 원본 URL에 대해 여러 개의 다른 단축 URL이 생성되어 DB에 중복 저장되는 데이터 불일치 문제가 발생 가능 
- 해결 과정
  - 단일 인스턴스를 넘어 분산 환경에서도 동시성을 제어하기 위해 Redisson 분산 락을 도입
  - 사용자의 단축 요청이 들어오면, 원본 URL을 Key로 사용하여 Lock을 획득하는 로직을 추가
  - 락을 획득한 스레드만이 DB 조회 및 신규 URL 생성/저장 로직을 수행하도록 보장. 다른 스레드들은 락이 해제될 때까지 대기하거나, 즉시 실패 응답을 보내도록 처리하여 임계 영역을 안전하게 보호
- 결과
  - 동시 요청으로 인한 데이터 중복 생성을 원천적으로 차단하여 비즈니스 규칙 준수
  - 향후 애플리케이션을 여러 인스턴스로 수평 확장하더라도 비즈니스 규칙 위반이 발생하지 않는 운영 기반 마련

### 3.3 시스템 안정성 확보를 위한 스레드 풀 제어
- 도전 과제
  - 비동기 전환으로 처리량을 높이는 데는 성공했
  - 하지만 부하를 지속하자, 스레드 풀의 작업 큐가 가득 차 `TaskRejectedException`이 발생하며 시스템이 불안정해지는 문제 발생
- 해결 방안
  - 큐가 가득 찼을 때 예외가 아닌 동기 방식으로 전환하기위해 스레드 풀의 거부 정책(Rejection Policy)을 기본 `AbortPolicy`에서 `CallerRunsPolicy`로 변경
  - 이 정책은 작업 큐가 가득 찼을 때, 작업을 요청한 API 스레드가 직접 해당 작업을 동기적으로 처리
  - 백프레셔를 적용하여 유입되는 트래픽의 속도를 조절
- 결과 및 기대 효과
  - 부하가 지속되어도 요청을 실패시키지 않고, 처리 속도를 늦추는 방식을 도입하여 실패 응답이 아닌 정상적 응답 보장

### 3.4 전략 패턴(Strategy Pattern)을 통한 ID 생성 방식 교체 용이성 확보
- 도전 과제
  - URL 단축기의 핵심인 고유 ID 생성 방식은 시스템 환경에 따라 최적의 전략이 다름
  - 분산/운영 환경: 여러 서버에서 동시에 ID를 생성해도 충돌이 없어야 하니 중앙화된 Redis의 INCR 같은 원자적 연산 필요
  - 로컬/개발 환경: 간단한 테스트를 위해 외부 의존성(Redis) 없이 메모리 기반으로 빠르게 동작하는 방식이 효율적
- 해결 방안
  - 전략 패턴을 적용하여 ID 생성 로직을 추상화
  - `IdSupplier` 라는 인터페이스를 정의하고, 실제 생성 로직을 담은 구현체(`RedisIdSupplier`, `InMemoryIdSupplier`)를 추가
  - 서비스 로직은 `IdSupplier` 인터페이스에만 의존하며, 실제 어떤 구현체를 사용할지는 Spring의 Profile(@Profile)이나 환경설정(application.yml)을 통해 외부에서 주입하도록 설계
- 결과 및 기대 효과
  - 운영 환경과 개발 환경의 ID 생성 전략을 코드를 수정하지 않고 손쉽게 전환 가능
  - 새로운 ID 생성 방식(ex. `DatabaseIdSupplier`)이 필요할 경우, 기존 코드를 수정하지 않고 `IdSupplier`의 새로운 구현체만 추가하면 되어 확장에 유리













