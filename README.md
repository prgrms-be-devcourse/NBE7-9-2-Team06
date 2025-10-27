# NBE7-9-2-Team06
# **PetWalk** 반려동물과 야외에서 함께 방문할 수 있는 장소 제공 서비스

---

## **📜 프로젝트 개요**
반려인의 위치를 기반으로 동물병원, 공원, 카페 등 반려동물 동반 가능 장소를 탐색하고,

장소를 리뷰•공유할 수 있는 반려동물 지도 기반 커뮤니티 서비스

---

## 💁‍♂️ 팀원 소개 / 역할

| 최지혁 | 유호준 | 윤예지 | 이창중 | 주정윤 |
|:--:|:--:|:--:|:--:|:--:|
| [![](https://github.com/hodakrer.png?size=100)](https://github.com/hodakrer)<br>**최지혁**<br>팀장<br>개발 1 | [![](https://github.com/dbghwns123.png?size=100)](https://github.com/dbghwns123)<br>**유호준**<br>팀원<br>개발 2 | [![](https://github.com/dpwl0974.png?size=100)](https://github.com/dpwl0974)<br>**윤예지**<br>팀원<br>개발 3 | [![](https://github.com/DEV-Cheeze.png?size=100)](https://github.com/DEV-Cheeze)<br>**이창중**<br>팀원<br>개발 4 | [![](https://github.com/zoooooz2.png?size=100)](https://github.com/zoooooz2)<br>**주정윤**<br>팀원<br>개발 5 |


## **⭐** 주요 기능
- **1. 핵심 탐색:** 내 주변 반려동물 동반 가능 장소 검색
- **2. 커뮤니티:** 리뷰 작성 및 포인트 리워드
- **3. 사용자:** 회원가입/로그인 및 마이페이지 (반려동물 관리)
- **4. 커머스:** 포인트 기반 상품 주문 시스템

### **1. 핵심 탐색 (장소 검색) 🗺️**

- **주요 기능:** 사용자 위치 기반 (반경 1~30km) 장소 검색
- **검색 방식:**
    - **1) 키워드 검색:** '애견카페' 등 단어와 **부분 일치**하는 장소 검색
    - **2) 카테고리 검색:** '동물병원' 등 카테고리와 **완전 일치**하는 장소 검색
- **✨ 핵심 로직 (정확도 향상):**
    - 카카오 API 검색 결과 중, **반려동물 관련 카테고리가 아닌 장소는 필터링** (e.g., 일반 음식점, 카페 제외)
    - `카카오 맵 API`에 없는 데이터는 **자체 `CSV` 파일로 보완**하여 검색

---

### **2. 커뮤니티 (리뷰 & 포인트) 🌟**

- **리뷰 (CRUD):**
    - **C (Create):** 텍스트, 이미지(`AWS S3` 활용) 업로드로 리뷰 생성
    - **R (Read):**
        - 장소 ID별 리뷰 목록 조회
        - 유저 ID별 (내가 쓴) 리뷰 목록 조회
    - **U/D (제한):** 포인트 정책 및 상품 구매와 연동되어 **수정 및 삭제는 제한**
- **포인트 시스템 (보상):**
    - **지급 기준:** 텍스트 리뷰 (50P), 텍스트+이미지 리뷰 (100P)
    - **제한 로직:**
        1. **일일 적립 한도 (1,000 포인트)**
            - 유저의 '오늘 획득 포인트'를 DB에서 체크
            - 한도 초과 시, "`일일 포인트 적립 한도를 초과하여, 포인트가 적립되지 않았습니다.`" 알림 및 포인트 미지급
        2. **일일 한 장소 한개의 리뷰 적립 제한**
            - 오늘 날짜, 장소 ID와 유저 ID, 리뷰 ID를 DB에서 체크
            - 일일 한 장소에 두 개 이상의 리뷰를 작성할 시, “`이미 오늘 해당 장소의 리뷰 포인트를 지급받아, 포인트가 적립되지 않았습니다.` ” 알림 및 포인트 미지급
    - **R (Read)** : 유저 포인트 적립 내역 조회

---

### **3-1. 사용자 (회원가입 & 로그인) 👤**

- **회원가입:**
    - 이메일, 비밀번호, 닉네임, 주소, 우편 번호 등 입력
    - **이메일 인증**:
        - 이메일 인증(인증코드 입력) 성공 시 회원가입 가능
    - **유효성 검사:**
        - 중복 검사 (이메일, 닉네임)
        - 형식 및 길이 검사 (빈 칸, 특수문자 등)
    - **성공 시:** 사용자 ID 반환
- **로그인:**
    - 닉네임, 비밀번호 기반 인증
    - **유효성 검사**
        - 형식 및 길이 검사 (빈 칸, 특수문자 등)
    - **성공 시:** **액세스 토큰(Access Token) 반환**
- **로그아웃:**
    - 세션스토리지에 있는 토큰 삭제

---

### **3-2. 사용자 (마이페이지 & 반려동물 관리) 🐾**

- **내 정보 조회:**
    - 토큰을 기반으로 사용자 정보 (주소 포함) 조회
    - 작성한 리뷰 조회
    - 적립된 포인트 조회
    - 사용자 반려동물 조회
- **반려동물 관리 (CUD):**
    - **C (Create):** 반려동물 정보 등록 (이름, 성별, 생년월일, 품종)
    - **U (Update):** 기존 반려동물 정보 수정
    - **D (Delete):** 등록된 반려동물 삭제
    - *(모든 기능은 액세스 토큰으로 인증된 사용자에 한해 실행)*

---

### **4. 커머스 (상품 주문) 🛒**

- **주문 생성 (핵심 로직):**
    - **[조건] 포인트 검사:** 사용자의 (보유 포인트) >= (총 결제 가격)
    - **[실행]** 조건 충족 시, **포인트 차감**
    - **[예외]** 조건 불충족 시, "포인트 부족"예외 처리
- **주문 상태 관리:**
    - `Scheduler` 활용
    - **조회:** 사용자의 전체 주문 내역 조회
    - **취소:** **'주문 완료(Ordered)'** 상태일 때 사용자가 직접 **'취소(Canceled)'** 가능
    - **배송 (자동화):**
        - 스케줄링 기능으로 매일 특정 시간, **'Ordered'** 상태의 주문을 'Delivered*로 일괄 변경 (Canceled 제외)


# 참고한 레퍼런스

- https://github.com/emperor-juwon/TourWithPet_teamproject?tab=readme-ov-file#spring-boot%EB%A5%BC-%ED%99%9C%EC%9A%A9%ED%95%9C-%EB%B0%98%EB%A0%A4%EB%8F%99%EB%AC%BC-%EB%8F%99%EB%B0%98-%EC%97%AC%ED%96%89-%ED%94%8C%EB%9E%AB%ED%8F%BC-%EB%A7%8C%EB%93%A4%EA%B8%B0

---

# 역할 분담

| 이름 | 역할 | 담당 기능 |
| --- | --- | --- |
| 최지혁 | 팀장 / ppt 제작 | 상품 주문 |
| 유호준 | ppt 제작 | 검색 |
| 이창중 | ppt 제작 | 마이페이지 |
| 윤예지 | ppt 제작 | 리뷰/포인트 |
| 주정윤 | 서기 / 발표 | 회원가입/로그인 |

---

## 🔧기술 스택
<div style="text-align: left;">
    <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=OpenJDK&logoColor=white" alt="Java">
    <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Java">
    <img src="https://img.shields.io/badge/springsecurity-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white" alt="Java">
    <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white" alt="Java">
    <img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Java">
    <img src="https://img.shields.io/badge/h2database-09476B?style=for-the-badge&logo=h2database&logoColor=white" alt="Java">
    <img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white" alt="Java">
    <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white" alt="Java">
    <img src="https://img.shields.io/badge/nextdotjs-000000?style=for-the-badge&logo=nextdotjs&logoColor=white" alt="Java">
</div>

---

## **🔗 ERD (Entity Relationship Diagram)**
<img width="837" height="521" alt="image" src="https://github.com/user-attachments/assets/6557eecd-1f39-406a-9289-feec4d7d91a4" />

---

## ⚙️ 시스템 아키텍처
<img width="3353" height="1645" alt="image (1)" src="https://github.com/user-attachments/assets/e846dda2-10d3-425c-99ca-260c7f831774" />

---

## 🎞️ 시연 영상
- 추후 삽입 예정

---
## 📃 코딩 컨벤션

### 🚀 GitHub Flow

우리 팀은 기존 **main / feature** 브랜치 구조에 **develop** 브랜치를 추가해 안정성과 협업 효율을 높였습니다.

- **main**
    - 초기 상태 백업용 브랜치
- **develop**
    - 새로운 기능 개발이 통합되는 기준 브랜치
    - 브랜치 보호 규칙 적용 (PR + 리뷰 후 머지)
- **feature/#(이슈 번호)**
    - 개별 기능 개발용 브랜치
    - 이슈 단위로 생성하여 작업
    - 작업 완료 후 PR을 통해 develop에 머지

---

### **🔄 작업 순서**

1. **이슈 생성** → 작업 단위 정의
2. **브랜치 생성** → develop 브랜치에서 이슈별 작업 브랜치 생성
3. **Commit & Push**
4. **PR 생성 & 코드 리뷰** → 최소 2명 승인 필요
5. **Merge & 브랜치 정리**
    - 리뷰 완료 후 develop 브랜치로 Merge
    - Merge 후 이슈별 작업 브랜치 삭제

---

### ⚙️ 네이밍 & 작성 규칙

1. **이슈**
    - 제목 규칙 : `[타입] 작업내용`
    - 예시 : `[feat] 로그인 기능 추가`
    - 본문은 템플릿에 맞춰서 작성
2. **PR**
    - 제목 규칙 : `[타입] 작업내용`
    - 예시 : `[feat] 로그인 기능 추가`
    - 본문은 템플릿에 맞춰서 작성
3. **브랜치**
    - 생성 기준 : `develop` 브랜치에서 생성
    - 명명 규칙 : `타입/작업 내용`
    - 예시: `feat/조회 기능 개발`
    - `main`과 `develop` 브랜치는 브랜치 보호 규칙이 적용되어, 반드시 PR을 통해 최소 2명의 팀원 리뷰 승인 후에만 머지할 수 있다.
4. **Commit Message 규칙**


    | 타입 | 의미 |
    | --- | --- |
    | **feat** | 새로운 기능 추가 |
    | **fix** | 버그 수정 |
    | **docs** | 문서 수정 (README, 주석 등) |
    | **style** | 코드 스타일 변경 (포맷팅, 세미콜론 등. 기능 변화 없음) |
    | **refactor** | 코드 리팩토링 (동작 변화 없음) |
    | **test** | 테스트 코드 추가/수정 |
    | **chore** | 빌드, 패키지 매니저, 설정 파일 등 유지보수 작업(환경 설정) |
    | **remove** | 파일, 폴더 삭제 |
    | **rename** | 파일, 폴더명 수정 |
    - `타입 : 작업내용`
    - 예시: `feat : 로그인 기능 추가`
