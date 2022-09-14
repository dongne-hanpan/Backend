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

![image](https://user-images.githubusercontent.com/86644517/189128656-792b52b7-9cc9-4a31-8cd2-b1ad597a7a57.png)

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

- 유효성 검사
- 중복 확인

### 2. 로그인 페이지

- 로그인 검사
  - 아이디, 패스워드가 회원가입시 입력한 내용과 동일한지 확인

### 3. 모집글 작성

- 로그인 여부 확인
  - Jwt를 통해 로그인시 token 발급. Refresh Token 발급여부로 로그인 현황 확인 가능

- 게시글 작성 및 수정시 공백으로 작성하지 않도록 하기
  - Entity의 컬럼값에 NotNull 부여하여 공백의 데이터가 들어오지 않도록 함. 
  - 프론트에서도 알림창으로 이용자에게 알려줌
  
- 작성자만이 게시글을 삭제할 수 있게 하기 
  - Header의 token를 통해 해당 유저가 게시글의 Host인지 확인 후 삭제
  
- 작성된 게시물은 비회원도 확인 가능
  - Spring Security 접근권한 모두 허용
  
### 4. 모집 신청

- 로그인 했을 때 신청할 수 있도록 하기
  - 매치 리스트는 비회원도 확인 가능하지만 신청시 로그인 화면으로 이동
  
- 유저가 모집글을 보고 신청을 하면 해당 매치의 호스트에게 알림이 가도록 하기
  - 신청버튼을 누를 시 Request_User_List Table에 User정보와 Match정보 그리고 입력한 Comment가 저장됨. 
  - Host계정 로그인시 상단 알림창에서 신청한 유저 정보 확인 가능
  
  ![image](https://user-images.githubusercontent.com/86644517/190033452-56cec819-ca55-491f-bfed-78d6f0c81063.png)
  
- 호스트는 신청한 유저의 정보와 Comment를 토대로 수락/거절 할 수 있음
  - 수락의 경우 Request_User_List Table에서 삭제되며 User_ist_in_Match Table에 새로 저장됨 (누가 어떤 매치에 소속되었는지 관리하는 Table)
  - 거절시 Request_User_List Table에서 삭제 됨.
  
- 최대 모집 인원이 모두 채워진 매치에는 신청할 수 없도록 하기
  - Host가 게시물 작성시 설정한 최대 인원수와 
    User_ist_in_Match 테이블에서 해당 매치에 소속된 인원을 count하여 현재 소속된 인원을 비교하여 인원모집이 완료되었는지 확인 가능
  
- 이미 소속된 매치에는 신청할 수 없도록 하기

### 5. 모집 완료, 결과 입력

- 호스트가 설정한 인원수가 모두 채워질경우 [모집 완료]로 변경됨
  - Host가 [모집완료] 버튼을 누를 시 MatchStatus가 recruit -> done 으로 변경됨
  
- 모집 완료된 게시물은 더이상 List에 보이지 않음
  - matchStatus가 done인 게시물은 더이상 List에 보이지 않고 마이페이지에서만 확인 가능.
  
- 정해진 약속시간으로부터 24시간이 지나면 해당 매치는 자동으로 모집 마감
  - 스케쥴러를 이용하여 Host가 설정한 시간으로부터 24시간 뒤 자동으로 matchStatus 변경시켜줄 예정
  
- 결과 입력을 통해 매치에서 획득한 나의 점수를 입력할 수 있음(입력된 점수는 랭킹에 반영)
  - 결과 입력시 Bowling Table에 나의 점수가 기록됨. 
  
- 같이 경기를 한 유저에게 리뷰를 남길 수 있음
  - User_ist_in_Match Table에서 같은 매치에 소속된 유저인지 확인 후 해당 유저에게 리뷰와 매너점수평가(10점만점)를 남길 수 있음

### 6. 마이페이지

- 프로필 이미지를 설정할 수 있도록 하기
- 스포츠별 내가 획득한 점수와 매치목록을 확인할 수 있도록 하기

### 7. 채팅
- 같은 매치에 소속된 유저들과 채팅 가능하도록 함
  - User_List_In_Match Table에서 같은 매치에 소속된 유저인지 확인하여 소속된 유저끼리만 채팅 가능하도록 함

- 채팅방에 보낸 메세지는 Message Table에 저장되어 채팅방을 나갔다 들어오거나 새로들어온 유저가 이전의 채팅을 볼 수 있도록 채팅이 유지되도록 함


## API 리스트

API명세서는 Swagger를 통해 작성했습니다.
http://54.197.65.103/swagger-ui.html

## 트러블 슈팅

https://github.com/dongne-hanpan/Backend/wiki
