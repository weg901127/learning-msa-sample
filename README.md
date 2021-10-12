# learning-msa-sample

Naver spring cloud 사내 교육용 sample test app 입니다. 

#### Service Discovery 
모듈간 Client side loadBalancing 을 하기 위함
- eureka

#### Client side load balance 실습관련
- simple-test
    - order-simple
    - product-simple

#### Spring Cloud Stream: 실습관련

- product
- order-process
    - domain
    - order
    - order-consumer
- payment
- adaptor
    - domain: feign client 에서 사용할 domain adaptor
    - message: 비동기 주문을 위한 order message
    

모듈이 있습니다. 교육이 진행됨에 따라서 사용됩니다.

## 준비사항 

### Java 11 다운로드 
 - https://jdk.java.net/archive/
 - Java 11 을 본인에 맞는 OS따라 다운로드 

##### Java11 Path 설정

.bash_profile 열고 아래 Path 설정

```shell script
# Java11 PATH
JAVA_HOME="/설치위치/jdk-11.0.2.jdk/Contents/Home/"
PATH="$JAVA_HOME/bin:${PATH}"
export PATH
``` 

```shell script
# java -verion
openjdk version "11.0.2" 2019-01-15
OpenJDK Runtime Environment 18.9 (build 11.0.2+9)
OpenJDK 64-Bit Server VM 18.9 (build 11.0.2+9, mixed mode)
```

### Docker 다운로드 (docker compose를 위해 edge version 다운로드)
 - mac: https://docs.docker.com/docker-for-mac/edge-release-notes/
 - windows: https://docs.docker.com/docker-for-windows/edge-release-notes/

### intellij 다운로드
- https://www.jetbrains.com/ko-kr/idea/download/#section=mac
커뮤니티 버전도 상관없음

### Docker infra 설치
프로젝트의 docker-compose 폴더로 이동
```shell script
# docker-compose -f docker-lecture-infra-compose.yml up -d
```