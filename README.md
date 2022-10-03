# 프로젝트 소개
스파르타 코딩캠프 6주간 진행되는 [동네한판] 프로젝트 Server GitHub 입니다.

- 개발 인원 : 3명
- 개발 기간 : 2022.08.26 ~
- 주요 기능 : 
  - Jwt 통한 로그인/회원가입
  - CRUD 기능
  - STOMP Websocket 이용한 매칭된 사용자간의 채팅
  
---
   
## 개발환경 및 배포환경
- 개발 언어 : Java 11
- 개발 환경 : SpringBoot 2.6.11, Graddle, Spring Data JPA, Spring Security, MySQL


---

## DB 설계

![image](https://user-images.githubusercontent.com/86644517/193514412-a98dc34b-980c-4c14-9b62-f7d2c3fa0585.png)

![image](https://user-images.githubusercontent.com/86644517/189131470-b0e151b9-8e84-4c59-82ae-3d70e44dfecf.png)

![image](https://user-images.githubusercontent.com/86644517/189131517-f92323a9-1448-4382-8ce5-11c295ef75aa.png)

![image](https://user-images.githubusercontent.com/86644517/189131564-96114a7c-d5ed-4f88-a6c1-9e9eae5baddb.png)

![image](https://user-images.githubusercontent.com/86644517/189131630-5933b72a-2bf0-4815-9a40-3749dcaea865.png)

![image](https://user-images.githubusercontent.com/86644517/189131674-55c0d4ec-013f-4cba-a4a5-591b66c42512.png)

![image](https://user-images.githubusercontent.com/86644517/189131728-d823bf76-d10b-401e-b9fe-722cc157060a.png)

![image](https://user-images.githubusercontent.com/86644517/189132199-77572f44-b215-42c2-9da2-6200a98d5e14.png)

---

## 요구사항 분석

### 1. 회원 가입 페이지

- 유효성 검사 (Id, Password 형식 확인)
- 중복 확인

### 2. 로그인 페이지

- 로그인 검사
  - 아이디, 패스워드가 회원가입시 입력한 내용과 동일한지 확인

### 3. 모집글 작성

- 로그인 여부 확인
  - Jwt를 통해 로그인시 token 발급. Refresh Token 발급여부로 로그인 현황 확인 가능

- 게시글 작성 시 공백으로 작성되지 않도록 하기
  - Entity의 컬럼값에 NotNull 부여하여 공백의 데이터가 들어오지 않도록 함. 
  - 프론트에서도 알림창으로 이용자에게 알려줌
  
- 작성자만이 채팅방을 삭제할 수 있게 하기 
  - Host인지 확인 후 채팅방 삭제 (대화내용 모두 지워짐)
  - Host가 아닌경우 '나가기' 누를 시 채팅방만 나가지도록 함
  - 삭제 된 채팅방에는 더 이상 접근 불가
  
- 작성된 게시물은 비회원도 확인 가능
  - Spring Security 접근권한 모두 허용
  
### 4. 모집 신청

- 로그인 했을 때 신청할 수 있도록 하기
  - 게시물 리스트는 비회원도 확인 가능하지만 신청시 로그인 화면으로 이동
  
- 유저가 모집글을 보고 신청을 하면 해당 매치의 호스트에게 알림이 가도록 하기
  - 신청버튼을 누를 시 Request_User_List Table에 User정보와 Match정보가 저장됨. 
  - 계정 로그인시 상단 알림창에서 내가 개설한  채팅방에 신청한 유저 정보 확인 가능
  
  ![image](https://user-images.githubusercontent.com/86644517/190033452-56cec819-ca55-491f-bfed-78d6f0c81063.png)
  
- 호스트는 신청한 유저의 정보를 토대로 수락/거절 할 수 있음
  - 수락하면 Request_User_List Table에서 삭제되며 User_ist_in_Match Table에 새로 저장됨 (누가 어떤 매치에 소속되었는지 관리하는 Table)
  - 거절시 Request_User_List Table에서 삭제 됨.
  
- 최대 모집 인원이 모두 채워진 매치에는 신청할 수 없도록 하기
  - Host가 게시물 작성시 설정한 최대 인원수와 
    User_ist_in_Match 테이블에서 해당 매치에 소속된 인원을 count하여 현재 소속된 인원을 비교하여 인원모집이 완료되었는지 확인 가능
  
- 이미 소속된 매치에는 신청할 수 없고, 다시한번 신청하기를 누르면 '신청 취소' 할 수 있음

### 5. 모집 완료, 결과 입력

- Host가 [모집 완료]버튼 누를 수 있음
  - Host가 [모집완료] 버튼을 누를 시 MatchStatus가 recruit -> reserved로 변경됨
  - reserved는 인원 모집이 마감되었다는 뜻
  - 해당 버튼은 Host만 누를 수 있도록 함
  
- 모집 완료된 게시물은 더이상 List에 보이지 않음
  - matchStatus가 recruit가 아닌 게시물은 더이상 List에 보이지 않고, 내가 속한 채팅방은 마이페이지에서 확인 가능.  

- 결과 입력을 통해 매치에서 획득한 나의 점수를 입력할 수 있음
  - 결과 입력시 Bowling Table에 나의 점수가 기록됨. 

- 경기가 종료되면 matchStatus가 reserved -> done 으로 변경됨
  - 스케쥴러를 이용하여 정해진 약속 시간으로부터 72시간이 지나면 matchStatus는 reserved -> done 으로 변경됨
  - 또는 모든 인원이 경기를 마치고 [결과입력]을 통해 자신의 점수를 입력하면 reserved -> done 으로 변경됨

- 같이 경기를 한 유저에게 리뷰를 남길 수 있음
  - User_ist_in_Match Table에서 같은 매치에 소속된 유저인지 확인 후 해당 유저에게 리뷰와 매너점수평가(10점만점)를 남길 수 있음
  - 자기 자신 평가, 같은 유저 두번 평가 불가능

### 6. 마이페이지

- S3 bucket을 이용하여 프로필 이미지를 저장할 수 있도록 함
- 스포츠별로 내가 획득한 점수와 매치목록을 확인할 수 있도록 하기
- 입력받은 후기를 마이페이지에서 볼 수 있도록 함
- 나의 매너점수, 경기 수, 평균 점수등 개인적인 통계를 볼 수 있음

### 7. 채팅

- STOMP를 통해 실시간 채팅 구현

- 같은 매치에 소속된 유저들끼리만 채팅 가능하도록 함
  - User_List_In_Match Table에서 같은 매치에 소속된 유저인지 확인하여 소속된 유저끼리만 채팅 가능하도록 함

- 채팅방에 보낸 메세지는 Message Table에 저장되어 채팅방을 나갔다 들어오거나 새로들어온 유저가 이전의 채팅을 볼 수 있도록 채팅이 유지되도록 함

### 8. AccessToken 정책

- 토큰의 Expired-Time은 30분이며 wtFilter에서 유효성 검사를 실시하여 모든 authenticated 요청을 보낼 때마다 ValidationCheck 실시.
- 토큰 만료시 401 statusCode 반환 됨. 이 때 front에서 만료된 토큰과 함께 reissue API 요청 -> 새로운 토큰 발급

## API 리스트

API명세서는 Swagger를 통해 확인 하실 수 있습니다.
http://3.36.131.248/swagger-ui.html#/

## 트러블 슈팅

https://github.com/dongne-hanpan/Backend/wiki
