게시판 만들기 (Spring boot + React)
=

>작업기간: 2024.11.18 ~ 2024.11.29
>Backend Repository: https://github.com/
>Frontend Repository: https://github.com/

React와 Spring boot를 활용한 SPA 게시판 프로젝트입니다. 아래와 같은 문제에 대해 고민하며 프로젝트를 진행했고, 최선의 해결책을 찾아서 코드를 작성했습니다. 

- 회원 인증/인가(JWT)
- N + 1 문제
- Entity 양방향 관계, JSON 직렬화 문제


# 📚목차
- [프로젝트 소개와 기능](#1-소개와-기능)
  - [프로젝트 소개](#소개) 
  - [구현 기능](#구현기능)

- [프로젝트 구조 및 설계](#2-구조-및-설계)
    - [DB 설계](#DB-설계)
    - [API 설계](#API-설계)

- [기술 스택](#3-기술-스택)
  - [백엔드](#Backend)
  - [프론트엔드](#Frontend)

- [실행 화면](#4-실행-화면)
- [트러블 슈팅](#5-트러블-슈팅)

- [회고](#6-회고)
    - [아쉬운 점](#1-아쉬운-점)
    - [후기](#2-후기)


## 1. 소개와 기능
### 💬소개
> 지금까지 배운 것을 기반으로 SPA 방식의 프로젝트를 처음으로 구현해보았습니다. <br>
> 웹 프로그래밍의 기본 소양으로 여겨지는 게시판을 직접 만들어 보면서, 
> 효율적인 데이터 처리와 데이터 변환 등 다양한 개발 문제에 직면하고 해결책을 찾아갔습니다.

- CRUD + Rest API + SPA
- 개발 기간 : 2024.11.18 ~ 2024.11.29
- 참여 인원 : 1명

<br>

### 🛠️구현기능

- **게시판 기능**
    - 모든 게시글 및 특정 게시글 조회
    - 게시글 검색 (제목, 내용, 작성자)
    - 게시글 작성 [회원]
    - 게시글 수정 [회원, 게시글 작성자]
    - 게시글 삭제 [회원, 게시글 작성자]
    - 게시글 답글 작성 [회원]

- **댓글 기능**
    - 댓글 조회
    - 댓글 작성 [회원]
    - 댓글 수정 [회원, 댓글 작성자]
    - 댓글 삭제 [회원, 댓글 작성자]

- **회원 기능 + JWT**
    - 회원가입
    - 로그인/로그아웃

<br>

## 2. 구조 및 설계


<br>

## 3. 기술 스택
### 📌Backend
| 기술                | 버전     |
|-------------------|--------|
| Spring Boot       | 3.1.5  |
| Spring Web        | 3.1.0  |
| Spring Security   | 3.0.4  |
| Spring Data Jpa   | 3.0.4  |
| JSON Web Token    | 0.11.5 |
| MySQL Connector J | 8.0.38 |

### 🎨Frontend
| 기술                  | 버전      |
|---------------------|---------|
| React               | 18.2.0  |



## 4. 실행 화면

- Main

  <img width="575" alt="FrontEnd-Main" src="">

- Join

  <img width="575" alt="FrontEnd-Join" src="">
  <img width="575" alt="isExistEmail" src="">
  <img width="575" alt="Join" src="">

- List

  <img width="575" alt="FrontEnd-List" src="">

- SearchList

  <img width="575" alt="Search" src="">
  <img width="575" alt="Searching" src="">

- BoardWrite

  <img width="575" alt="Write" src="">
  <img width="575" alt="BoardWrite" src="">

- BoardUpdate

  <img width="575" alt="BoardUpdate" src="https://github.com/jhcode33/react-spring-blog-backend/assets/125725072/02cf74f7-786f-447f-8fad-aa0689edbabe">

- Comment

  <img width="575" alt="CommentPaging" src="">

- File-Download

  <img width="575" alt="FileDownload" src="">

## 5. 트러블 슈팅🤔
### N + 1 해결 

<br>



## 6. 회고
### 1. 아쉬운 점




### 2. 후기
