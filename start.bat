@echo off
echo "Docker Deamon 상태 확인 중..."
docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo "Error: Docker Engine이 실행 중이지 않습니다. Docker Desktop을 실행하고 다시 시도하세요."
    pause
    exit /b
)
echo "테스트 환경 구성 중..."
docker-compose down -v
docker-compose up --build -d
pause