#!/bin/bash

echo "Docker container 와 volume을 중단 및 제거하는 중..."
docker-compose down -v

echo "Docker container들을 재시작 하는 중..."
docker-compose up -d --build

echo "Docker 환경이 재시작되어 준비되었습니다. 이 경로로 접속하세요. http://localhost:8080/"