

# Yakku MSA 프로젝트

## 프로젝트 개요
- 일반적인 상품 구매 프로세스를 구현하여 기본적인 e-commerce 기능을 제공합니다.
- 특정 시간에 많은 요청이 몰리는 예약 구매 상황에서 필요한 동시성 처리 로직으로 시스템 안정성을 확보 하였습니다.
- 마이크로서비스 아키텍처를 기반으로 서비스를 분리하고, 서비스 간 효율적인 데이터 통신 및 관리 방식을 구현했습니다.
- API Gateway와 Eureka Service Discovery를 통해 마이크로서비스 간의 유연한 통신과 확장성을 확보했습니다.

이 프로젝트를 통해 MSA의 기본 구성 방법을 익히고, 대규모 트래픽 처리, 서비스 간 통신, 데이터 일관성 유지 등 다양한 기술적 과제들을 접해보았습니다.

## 기술 스택
### Backend
![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/Springboot_3.3.0-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-6DB33F?style=for-the-badge&logo=spring&logoColor=white)

### Database
![MySQL](https://img.shields.io/badge/MySQL-00000F?style=for-the-badge&logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)
### Etc
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Eureka](https://img.shields.io/badge/Eureka_Client-2496ED?style=for-the-badge&logo=spring&logoColor=white)
![Feign](https://img.shields.io/badge/Feign_Client-brightgreen?style=for-the-badge)


## 아키텍처 개요
![아키텍처 다이어그램](images/architecture-diagram.png)

Yakku 프로젝트는 다음과 같은 주요 서비스로 구성되어 있습니다:
- API Gateway
- Eureka Server (서비스 디스커버리)
- Customer Service
- Product Service
- Order Service
- Payment Service

각 서비스의 상세 설명은 [SERVICES.md](SERVICES.md)를 참조하세요.

## 주요 기능
- E-commerce 주요 기능인 상품, 주문, 위시리스트 기능
- JWT를 사용한 사용자 식별, 로그인 기능
- 대량의 주문 요청
- [주요 기능 4]

## 시작하기
프로젝트 설정 및 실행 방법에 대한 자세한 내용은 [DEVELOPMENT.md](DEVELOPMENT.md)를 참조하세요.

## 연락처
- 개발자: 최종우 (Choi Jong Woo)
- 이메일: muvnelik@naver.com
- GitHub: https://github.com/Jonggae
- Blog: https://jonggae.tistory.com

## 추가 문서
- [아키텍처 상세](ARCHITECTURE.md)
- [서비스 설명](SERVICES.md)
- [개발 가이드](DEVELOPMENT.md)
- [배포 프로세스](DEPLOYMENT.md)