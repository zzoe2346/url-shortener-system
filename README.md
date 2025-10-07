# URL Shortener System

> 대규모 트래픽 환경에서의 성능 개선, 새로운 기술 스택 실험, 트레이드오프(Trade-off) 기반의 기술 판단, 그리고 확장성 있는 아키텍처 설계 역량 강화를 목표로 한 사이드 프로젝트

---

## 프로젝트 개요

이 프로젝트는 Bitly처럼 긴 URL을 짧게 변환해주는 **URL 단축 서비스**를 직접 구현한 것입니다.
단순히 “짧은 링크를 만드는 기능”에 그치지 않고, **대규모 트래픽 환경에서의 설계·확장성·가용성·트레이드오프 분석**을 핵심 목표로 합니다.

특히, **제한된 서버 자원에서 인기를 얻어 트래픽이 폭증하는 상황**을 가정하고,

- 수직적 확장(Scale-up)이 아닌 **수평적 확장(Scale-out)** 중심의 설계
- 실제 서비스 수준의 **안정성과 복원력(Resilience)** 확보
  
를 주요 목표로 합니다.

**키워드:** 디자인 패턴, 가용성(Availability), 확장성(Scalability), 트레이드오프(Trade-off), Kafka, Redis, 부하 테스트

---

## 기술 스택

| 구분                   | 사용 기술                             |
| -------------------- |-----------------------------------|
| **Backend**          | Java 17, Spring Boot, Spring Data JPA |
| **Database / Cache** | MySQL, Redis                      |
| **DevOps / Infra**   | AWS (EC2, RDS, MKS 등), Docker     |
| **Test / Load**      | JUnit5, Locust, k6                |
| **ETC**              | Kafka                             |

---



