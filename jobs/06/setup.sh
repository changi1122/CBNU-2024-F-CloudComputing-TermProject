#!/bin/bash

# 디렉터리 준비
sudo -u ec2-user mkdir run_0
sudo -u ec2-user mkdir run_1

# 사용할 파일 복사
sudo -u ec2-user cp file.txt run_0/
sudo -u ec2-user cp file.txt run_1/