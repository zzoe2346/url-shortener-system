# URL Shortener System
분산 환경에서도 초당 1,000건의 요청을 처리하는 고가용성 URL Shortener
## 개요
## 기술 스택
- **Backend:** Java, Spring Boot
- **Database:** MySQL, Redis
- **Frontend:** TypeScript, React
- **DevOps/Infra:** AWS(EC2, RDS), Docker, GitHub Actions
- **Etc:** Swagger, JUnit5, Locust
## 아키텍처

### 시스템 아키텍처 다이어그램
### Java Class 패키지 구성
```
com.jeongseonghun.urlshortener
 ┣ 📂 config         → 스프링 설정 (예: RedisConfig, SwaggerConfig 등)
 ┣ 📂 controller     → REST API 진입점 (ShortenController)
 ┣ 📂 service        → 비즈니스 로직 (ShortenService)
 ┣ 📂 domain
 ┃   ┣ 📂 entity     → 엔티티
 ┃   ┣ 📂 repository → JPA/Redis Repository 인터페이스
 ┣ 📂 dto            → 요청/응답 DTO (ShortenRequest, ShortenResponse)
 ┣ 📂 exception      → 커스텀 예외 (UrlNotFoundException 등)
 ┣ 📂 util           → Base62 인코딩, ID 생성기 같은 유틸
 ┗ 🅒 UrlShortenerApplication.java
```
### ERD
## 핵심 기능 및 구현 내용
### 5.1. 고유한 Short URL 생성 알고리즘
- 문제: 어떻게 중복되지 않고, 길이가 짧으며, URL-safe한 문자열을 생성할 것인가?
- 해결: Base62 인코딩, NanoID, 혹은 해시 함수 + 충돌 처리 전략 등을 사용한 경험과 그 이유를 설명합니다.
### 5.2. 대용량 트래픽 처리
- 문제: 인기 있는 URL에 순간적으로 트래픽이 몰릴 때 어떻게 시스템 다운을 막을 것인가?
- 해결: Redis를 이용한 캐싱 전략 (Original URL 캐싱)을 통해 DB 부하를 줄인 경험을 서술합니다.
### 5.3. [심화] 단축 URL 통계 및 분석 기능
- 문제: 리디렉션 성능에 영향을 주지 않으면서 클릭 로그를 어떻게 수집하고 처리할 것인가?
- 해결: Kafka, RabbitMQ 같은 메시지 큐를 사용해 비동기적으로 로그를 처리하고 배치(Batch) 작업으로 DB에 저장한 경험을 어필합니다.
### 5.4. [심화] Custom URL 기능 및 유효성 검증
- 사용자가 원하는 문자열로 단축 URL을 만드는 기능과, 이 과정에서 발생할 수 있는 중복 및 유효성 문제를 어떻게 처리했는지 설명합니다.
### 5.5. API 문서 자동화
- Spring REST Docs나 Swagger를 이용해 API 명세를 자동으로 생성하고 제공한 경험을 보여줍니다.


