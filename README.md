# NBE7-9-2-Team06
# **PetWalk** 반려동물과 야외에서 함께 방문할 수 있는 장소 제공 서비스

---

## **📜 프로젝트 개요**


---

## 💁‍♂️ 팀원 소개 / 역할

| 최지혁 | 유호준 | 윤예지 | 이창중 | 주정윤 |
|:--:|:--:|:--:|:--:|:--:|
| [![](https://github.com/hodakrer.png?size=100)](https://github.com/hodakrer)<br>**최지혁**<br>팀장<br>개발 1 | [![](https://github.com/dbghwns123.png?size=100)](https://github.com/dbghwns123)<br>**유호준**<br>팀원<br>개발 2 | [![](https://github.com/dpwl0974.png?size=100)](https://github.com/dpwl0974)<br>**윤예지**<br>팀원<br>개발 3 | [![](https://github.com/DEV-Cheeze.png?size=100)](https://github.com/DEV-Cheeze)<br>**이창중**<br>팀원<br>개발 4 | [![](https://github.com/zoooooz2.png?size=100)](https://github.com/zoooooz2)<br>**주정윤**<br>팀원<br>개발 5 |

---

## 📝 유저 스토리
### 👤 고객(사용자)

- **C-1 [조회]**

  나는 고객으로서, --

---

## **⭐** 주요 기능

### **👤 사용자 기능**

- CRUD


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
- 추후 삽입 예정

---

## ⚙️ 시스템 아키텍처
- 추후 삽입 예정

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
    - 명명 규칙 : `타입/#이슈번호`
    - 예시: `feat/#1`
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
    - `타입 : 작업내용 #이슈번호`
    - 예시: `feat : 로그인 기능 추가#1`