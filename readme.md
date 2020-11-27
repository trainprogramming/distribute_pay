
# Table of Contents

1.  [필요 환경](#org89d32b7)
    1.  [기본 설정](#org99506c4)
    2.  [설정 변경](#org767d8e3)
2.  [실행 방법](#org816a302)
3.  [빌드 방법](#org6b3e2c5)
4.  [API](#org3913c6f)
    1.  [뿌리기](#orga1c33bc)
    2.  [받기](#orgefcc645)
    3.  [조회](#orgfa22bfb)
    4.  [Swagger 주소](#orgb1ce53c)
5.  [설계 및 구조](#org7c6767b)
    1.  [Hexagonal Architecture](#org7a6eafb)
    2.  [시스템 구조](#orgb14fcf6)
    3.  [Repository 별 용도](#orgffd08d3)
        1.  [mongodb](#orgb5acd07)
        2.  [redis](#org755d0d1)
6.  [해결전략](#org2db53a8)


<a id="org89d32b7"></a>

# 필요 환경

mongodb, redis 필요  
각 뿌리기 건에 대한 정보를 7일간 유지하기 위해서 Index 생성 필요  

    // mongodb 접속 후,
    use appchemist
    db.distributePay.createIndex( { "created": 1 }, { expireAfterSeconds: 604800 } )


<a id="org99506c4"></a>

## 기본 설정

mongodb : 127.0.0.1:27017  
redis : 127.0.0.1:6379  


<a id="org767d8e3"></a>

## 설정 변경

프로젝트ROOT/configuration/src/main/resourcse/application.yml 파일을 통해서 변경 가능  

    distributepay:
      redisHost: 127.0.0.1 (redis 주소)
      redisPort: 6379 (redis port)
      mongodbUrl: mongodb://127.0.0.1:27017 (mongodb 주소 및 포트)
      mongodbName: appchemist (mongodb db 이름)


<a id="org816a302"></a>

# 실행 방법

미리 빌드 버전 : <https://github.com/appchemist/distribute_pay/releases/tag/0.0.1>  

    java -jar distributePay-0.0.1-SNAPSHOT.jar


<a id="org6b3e2c5"></a>

# 빌드 방법

    // 프로젝트 root 디렉토리 이동
    ./gradlew build
    cd configuration/build/libs
    // 현재 디렉토리에 configuration-0.0.1-SNAPSHOT.jar가 결과물


<a id="org3913c6f"></a>

# API


<a id="orga1c33bc"></a>

## 뿌리기

-   URI: /v1/distributepay
-   Method : POST
-   Header :  
    -   X-USER-ID : 요청 사용자 아이디
    -   X-ROOM-ID : 사용자의 대화방 아이디
-   Body :  
    -   maxPay : 뿌리기 총 금액
    -   maxTarget : 뿌리기 대상 수


<a id="orgefcc645"></a>

## 받기

-   URI: /v1/distributepay/토큰
-   Method : PUT
-   Header :  
    -   X-USER-ID : 요청 사용자 아이디
    -   X-ROOM-ID : 사용자의 대화방 아이디


<a id="orgfa22bfb"></a>

## 조회

-   URI: /v1/distributepay/토큰
-   Method : GET
-   Header :  
    -   X-USER-ID : 요청 사용자 아이디
    -   X-ROOM-ID : 사용자의 대화방 아이디


<a id="orgb1ce53c"></a>

## Swagger 주소

URI: /swagger-ui.html  


<a id="org7c6767b"></a>

# 설계 및 구조


<a id="org7a6eafb"></a>

## Hexagonal Architecture

육각형 아키텍처를 기반으로 구성  

모듈은 core, data, common, configuration, web으로 구성  
core 모듈은 비지니스 로직 및 서비스 로직을 담당  
data 모듈은 DB 와 같은 외부 인터페이스 처리 로직 담당  
common 모듈은 DI와 관련된 어노테이션 의존성 제거 담당 그 외 공통적인 부분을 담당할 목적이지면 여기서는 어노테이션 의존성 제거를 위한 사용  
configuration 모듈은 스프링 부트의 설정을 담당  
web 모듈은 웹 와 같은 외부 인터페이스 처리 로직 담당  

기본적으로 각 모듈별 의존성은 상위 수준의 모듈을 향하도록 구성  

![module_dependency](https://user-images.githubusercontent.com/1546031/99945700-6f54e880-2db8-11eb-833d-f3a28a3fd1d4.png)  


<a id="orgb14fcf6"></a>

## 시스템 구조

![system_structure](https://user-images.githubusercontent.com/1546031/99947708-b98b9900-2dbb-11eb-9543-4d8a2260b283.png)  


<a id="orgffd08d3"></a>

## Repository 별 용도


<a id="orgb5acd07"></a>

### mongodb

페이를 나눈 정보를 저장하고 조회하는 용도  
7시간이 지나면 해당 도큐먼트를 삭제(index 생성 필요)  


<a id="org755d0d1"></a>

### redis

나눈 페이를 queue에 저장, 각 페이를 각 1회씩 분배  
페이 받기를 2회 이상 수행하지 못 하도록 lock 설정  


<a id="org2db53a8"></a>

# 해결전략

-   token은 room이 다른 경우, 중복해서 생성이 가능 (token + roomId를 key로 사용)
-   key의 중복은 mongodb에 insert 시도를 통해서 확인
-   중복 받기 방지는 받기 시도 시,  
    redis에 token:roomId:userId로 키가 없는 경우 저장한다.  
    키가 존재하는 경우 중복 받기로 처리
-   페이를 나눈 정보는 redis에 token:roomId를 키로 list 형태로 저장, queue와 같이 사용하여 한번의 요청에 하나씩 나눠주도록 처리
-   받기가 유효한 토큰은 redis에 생성한 list가 존재하거나 list의 길이가 0 이상인 경우 유효한 token으로 판단
-   redis에 저장한 키들은 모두 10분의 expire 시간을 주어서 받기가 유효한 시간에만 존재하도록 처리
-   조회가 7시간만 가능한 부분의 경우 mongodb의 index를 통해서 expire 시간을 주어서 7시간만 도큐먼트가 존재하도록 처리  
    단, 해당 인덱스의 경우 어노테이션 방식으로 에러가 발생해, mongodb에서 직접 생성을 해줘야 함

