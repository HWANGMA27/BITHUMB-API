# Market Integration Project

## 프로젝트 개요
Market Integration Project는 Spring WebFlux를 사용하여 빗썸(Bithumb) API를 활용한 멀티모듈 프로젝트입니다.<br><br>
현재는 빗썸의 퍼블릭 및 프라이빗 API를 통해 암호화폐 거래소 데이터를 조회하고 관리하는 기능을 제공합니다.<br>
추후 빗썸 외 거래소의 API를 조회하는 모듈을 추가 해 여러 거래소의 데이터를 한번에 조회하고 관리하는 기능을 제공하고자 합니다.

## 주요 기능
- **퍼블릭 API**: 현재 시장 가격, 오더북, 거래 내역 등의 데이터를 조회합니다.
- **프라이빗 API**: 계정 정보, 잔액 조회, 주문 내역, 거래 내역 등을 관리합니다.
- **WebFlux**: 비동기 및 논블로킹 방식으로 고성능의 API 호출을 구현합니다.
- **RabbitMQ**: 메시지 큐를 사용하여 모듈 간 통신을 수행합니다.
- **스케줄러**: 주기적으로 데이터를 갱신하거나 특정 작업을 실행합니다.
- **슬랙알림**: 데이터 갱신 결과를 슬랙 채널로 알리는 기능을 합니다.
- **사용자 Secret 암호화**: 거래소 private API 접근에 필요한 사용자 Secret을 암호화하여 데이터베이스에 안전하게 저장합니다.

## 모듈 구성 및 장점
- **api-broker**: 스케줄러에서 큐로 실행 명령을 받으면 bithumb-api를 호출하고 자체 비즈니스 로직으로 작업을 수행합니다. 이후 저장이 필요한 정보는 JPA를 사용해 DB에 저장합니다.
- **bithumb-api**: WebFlux를 사용하여 비동기 방식으로 빗썸 API를 호출 후 데이터를 반환하는 역할을 합니다.(빗썸 API V1.2.0)
- **spring-scheduler**: 스케줄링 작업을 처리하며, RabbitMQ를 통해 api-broker와 통신합니다.
- **slack**: api-broker에서 실행 결과를 받아 슬랙에 전송합니다.
- **멀티모듈 구성 장점**: 각 모듈이 독립적으로 개발, 테스트, 배포될 수 있어 유지보수성과 확장성이 높습니다.

## 사용 기술
<p align="left">
  <img src="https://img.shields.io/badge/JAVA-%23007396.svg?&style=for-the-badge&logo=java&logoColor=white"/>
  <img src="https://img.shields.io/badge/JPA/Hibernate-%236B584B.svg?&style=for-the-badge&logo=hibernate&logoColor=white"/>
  <img src="https://img.shields.io/badge/RabbitMQ-%23FF6600.svg?&style=for-the-badge&logo=rabbitmq&logoColor=white"/>
  <img src="https://img.shields.io/badge/Slack%20Webhook-%234A154B.svg?&style=for-the-badge&logo=slack&logoColor=white"/>
  <img src="https://img.shields.io/badge/Docker-%232496ED.svg?&style=for-the-badge&logo=docker&logoColor=white"/>
</p>
<p align="left">
  <img src="https://img.shields.io/badge/Spring%20Web%20MVC-%236DB33F.svg?&style=for-the-badge&logo=spring&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring%20WebFlux-%236DB33F.svg?&style=for-the-badge&logo=spring&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring%20Scheduler-%236DB33F.svg?&style=for-the-badge&logo=spring&logoColor=white"/>
</p>

## 파일 구조
- **api-broker/**: 실제 비즈니스 로직과 DB 관련 소스 코드
- **bithumb-api/**: 빗썸 API 호출 관련 소스 코드
- **spring-scheduler/**: 스케줄링 관련 소스 코드
- **slack/**: 슬랙 알림 관련 소스 코드
- **.gitignore**: Git ignore 파일
- **README.md**: 프로젝트 설명서
- **build.gradle**: Gradle 빌드 파일
- **gradlew**: Gradle Wrapper 실행 파일 (Unix)
- **gradlew.bat**: Gradle Wrapper 실행 파일 (Windows)
- **settings.gradle**: Gradle 설정 파일

## 설치 및 사용 방법
1. **저장소 클론**:
   ```bash
   git clone https://github.com/HWANGMA27/Market-Integration-Project.git
   cd [MODULE-NAME]
2.	**의존성 설치**:
    ```bash
    ./gradlew build
3.	**애플리케이션 실행**:
    ```bash
    ./gradlew bootRun

## 설정

1. api-broker의 ManualExecController에 addUser api를 호출 해 유저와 유저 API키와 시크릿을 암호화하여 저장 할 수 있습니다.<br>
2. api-broker의 application.yml 파일을 수정하여 유저 키를 암호화 하는데 사용할 시크릿을 설정을 할 수 있습니다.<br>
3. spring-scheduler의 application.yml 파일을 수정하여 스케쥴 실행 간격을 변경 할 수 있습니다.<br>
4. slack의 application.yml 파일을 수정하여 알림을 보낼 슬랙 채널을 변경 할 수 있습니다.<br>
ex) api-broker <br>
 ```bash
 api:
  bithumb:
    secret-key: "YOUR_SECRET_KEY"
```
ex) spring-scheduler <br>
   ```bash
   scheduler:
     cron:
       time: "0 0/1 * * * ?"  # 크론 타임 설정
```
ex) slack <br>
   ```bash
   slack:
     base-url: https://hooks.slack.com
     channel:
       warning: /service/...  # base-url 뒷부분 추가
       alert: 
```

## 슬랙 알림
알림에 포함되는 내용:
- 성공 여부
- 조회 대상 (예: Bithumb, Upbit 등)
- 작업 종류 (예: 저장, 업데이트 등)
- 대상 테이블 명
- 성공 시 반영된 DB 행 수

<img width="453" alt="스크린샷 2024-07-24 오전 12 26 26" src="https://github.com/user-attachments/assets/1bf88e6a-555f-41bc-b830-0c09ace2d2e3">


## 도커 컴포즈 지원 예정
추후 도커 컴포즈를 지원하여 쉽게 배포하고 관리할 수 있도록 할 예정입니다.

#### 참조 
https://apidocs.bithumb.com/
