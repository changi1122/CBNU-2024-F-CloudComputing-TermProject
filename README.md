# 2024-F 클라우드 컴퓨팅 TermProject

## 개요

이 프로젝트는 AWS SDK를 사용하여 AWS EC2 인스턴스를 동적으로 관리하는 자바 기반의 콘솔 애플리케이션입니다. 사용자는 명령어 기반의 메뉴 인터페이스를 통해 EC2 인스턴스의 상태를 확인 및 제어(시작, 중지, 이미지로부터 인스턴스 생성)할 수 있습니다. 또한, HTCondor 클러스터에 작업 제출 및 상태확인, S3 버킷에 실행 결과 업로드 기능을 제공합니다.

## 주요 기능

### 1. **EC2 인스턴스 관리**

- **인스턴스 조회:** 현재 EC2 인스턴스 목록 출력
- **인스턴스 시작/중지/재부팅:** 인스턴스 상태 변경
- **가용 영역 및 리전 조회:** AWS 리전 및 가용 영역 확인

### 2. **EC2 명령 실행 및 HTCondor 작업 관리**

- **셸 명령 실행:** 선택한 EC2 인스턴스에서 원격으로 셸 명령어 실행
- **HTCondor 작업 제출:** S3 버킷을 통해 작업 명세서 및 스크립트를 업로드한 후, EC2 인스턴스에서 작업 실행
- **HTCondor 상태 조회:** `condor_status` 및 `condor_q` 명령어 실행을 통해 작업 상태 확인

### 3. **작업 결과 업로드**

- **작업 결과 업로드:** EC2에서 실행된 HTCondor 작업 결과를 S3 버킷에 업로드

## 메뉴

1. **list instance:** 모든 EC2 인스턴스 목록 출력
2. **available zones:** 현재 리전의 가용 영역 목록 조회
3. **start instance:** 선택한 인스턴스를 시작
4. **available regions:** AWS의 모든 리전 목록 확인
5. **stop instance:** 선택한 인스턴스를 중지
6. **create instance:** 새로운 EC2 인스턴스 생성
7. **reboot instance:** 인스턴스 재부팅
8. **list images:** 현재 계정의 AMI 목록 조회
9. **shell command:** EC2 인스턴스에서 원격으로 셸 명령 실행
10. **submit jobs:** S3에 업로드된 HTCondor 작업을 제출
11. **condor_status:** HTCondor 작업 상태 조회
12. **condor_q:** 현재 실행 중인 HTCondor 작업 큐 조회
13. **upload result to S3:** HTCondor 작업 결과를 S3에 업로드
