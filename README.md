#API neighborApi v1.3.0
================================
----
```
2024-05-15 version update
 - Add rbac authority authentication
```


#API 표준 프로젝트 v1.2.0
================================
배포
----
```
2024-04-01 version update
 - BaseEntity 추가 (extends BaseEntity)
   - 생성일시, 수정일시 자동 반영
   - DynamicRepository Filter 동작하도록 serializeMap에 추가
 - CommonErrorCode 개선 (CommonErrorCode)
   - 표준 Error message 상태 코드 정립
   - 개선에 따른 로그 출력 방식 변경
   - Swagger Response 변경
   - CustomException Throwable 받도록 parameter 추가
 - messageProperties 정리
 - Service CUD 로직 개선
   - Optional 처리방식 개선
   - toEntityKey 사용
   - CommonErrorCode 개선에 따른 변경
 - Mapper 에 toEntityKey 추가 (CUD 로직 개선)
```

#API 표준 프로젝트 v1.1.0
================================
배포
----
```
2024-03-29 version update
 - Spring boot version upgrade 3.2.4
 - DB migration (oracle -> mariaDB)
 - JWT 인증 방식 개선
 - logout 기능 추가
```

#API 표준 프로젝트 v1.0.0
================================
배포
----
```
2024-03-22 v1.0.0 배포
2024-03-27 수정사항
 - oracle to mariadb migration
 - build.gradle, querydls 설정 코드 개선, generated 폴더 변경
 - CustomUserDetailsService, 권한 없을 경우 불필요 로직 안타게 변경
 - querydsl sample code 추가
 - jar 배포 테스트 (완료)
```

Spec
----
```
 - Java 17 or latest
 - Spring boot 3.2.3
```

적용 라이브러리
--------
```
 - JPA (querydsl, mapstruct)
 - Lombok
 - Swagger
```

인증 및 보안
------------------
```
 - Jasypt (properties 암호화)
 - JWT (Token 인증)
 - RSA (비대칭 암호화)
 - Spring srcurity (CORS, CSRF)

 ```
 
유효성 체크
------------------
```
 - Validation

 ```

로깅
------------------
```
 - Information, Exception, Request, Response 로깅
 - 각 기능 별 파일 관리
 - Exception 분 단위, Information, Request, Response 시간 단위 TimeBasedRollingPolicy
 - Request, Response는 AOP로 동작

 ```
 
표준화 관련
------------------
```
 - 응답 객체 ItemResponse (단일 정보 리턴), ItemsResponse(복수 정보 리턴), ErrorResponse(오류 정보 리턴)
 - 응답 객체 생성 GenerateResponse
   - ItemResponse, ItemsResponse를 생성해 리턴
   - Exception ErrorReponse 리턴
 - JPA 멀티 filtering, sorting, paging 처리
   - DynamicRepository 구현
 - Exception (GlobalExceptionHandler)
   - API에서 발생하는 모든 Exception 처리
 - Swagger Document
   - Operation 마다 발생하는 Response 자동 생성
   - Swagger 에서 기능 테스트 가능 환경 구성

 ```