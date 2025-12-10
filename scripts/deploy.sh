#!/bin/bash

# 사용법: ./deploy.sh [dev|prod]
TARGET_ENV=$1

if [ -z "$TARGET_ENV" ]; then
  echo "⚠️ Usage: ./deploy.sh [dev|prod]"
  exit 1
fi

APP_NAME="tpa-sun-api"
BASE_PATH="/home/nex3/app/${APP_NAME}"

# 환경별 설정
if [ "$TARGET_ENV" == "prod" ]; then
  ENV_FILE=".env.prod"
  NGINX_CONF="/etc/nginx/conf.d/tpa-sun-api-prod.conf"
  DEFAULT_PORT="8091"
else
  ENV_FILE=".env.dev"
  NGINX_CONF="/etc/nginx/conf.d/tpa-sun-api-dev.conf"
  DEFAULT_PORT="8081"
fi

echo "🚀 Starting Deployment for $TARGET_ENV environment (App: $APP_NAME)..."

# 1. 환경변수 파일 준비
if [ -f "${BASE_PATH}/${ENV_FILE}" ]; then
  echo "📄 Copying ${ENV_FILE} to .env"
  cp "${BASE_PATH}/${ENV_FILE}" "${BASE_PATH}/.env"
else
  echo "❌ Environment file ${ENV_FILE} not found at ${BASE_PATH}"
  echo "   Please create it manually on the server."
  exit 1
fi

# 2. 현재 실행 중인 포트 확인
CURRENT_PORT_FILE="${BASE_PATH}/current_port_${TARGET_ENV}.txt"
CURRENT_PORT=$(cat $CURRENT_PORT_FILE 2>/dev/null || echo "$DEFAULT_PORT")

# 3. 포트 스위칭 로직
if [ "$TARGET_ENV" == "dev" ]; then
  if [ "$CURRENT_PORT" == "8081" ]; then
    TARGET_PORT="8082"; TARGET_COLOR="green"
  else
    TARGET_PORT="8081"; TARGET_COLOR="blue"
  fi
elif [ "$TARGET_ENV" == "prod" ]; then
  if [ "$CURRENT_PORT" == "8091" ]; then
    TARGET_PORT="8092"; TARGET_COLOR="green"
  else
    TARGET_PORT="8091"; TARGET_COLOR="blue"
  fi
fi

echo "🔄 $TARGET_ENV Deployment: $CURRENT_PORT -> $TARGET_PORT ($TARGET_COLOR)"

# 4. 컨테이너 실행
export HOST_PORT=$TARGET_PORT
if [ -z "$DOCKER_IMAGE" ]; then
  export DOCKER_IMAGE="tpa-sun-api:${TARGET_ENV}"
fi

COMPOSE_PROJECT_NAME="${APP_NAME}-${TARGET_ENV}-${TARGET_COLOR}"

docker compose -f docker-compose.app.yml -p $COMPOSE_PROJECT_NAME up -d

echo "🏥 Health Checking ($TARGET_PORT)..."

# [수정] 5회 반복 (5초 간격, 총 25초 대기)
for i in {1..5}; do
  STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:${TARGET_PORT}/health)

  if [ "$STATUS" == "200" ]; then
    echo "✅ Health Check Passed!"
    break
  fi

  echo "⏳ Waiting... ($i/5) HTTP $STATUS"
  sleep 5
done

if [ "$STATUS" != "200" ]; then
  echo "❌ Health Check Failed. Status: $STATUS"

  # 실패 시 로그 출력
  echo "--- Docker Logs (Last 50 lines) ---"
  docker compose -f docker-compose.app.yml -p $COMPOSE_PROJECT_NAME logs --tail 50
  echo "-----------------------------------"

  echo "Rolling back..."
  docker compose -f docker-compose.app.yml -p $COMPOSE_PROJECT_NAME down
  exit 1
fi

# 6. Nginx 설정 변경 & Reload
echo "🔄 Switching Nginx Traffic..."
sudo sed -i "s/127.0.0.1:[0-9]\{4\}/127.0.0.1:${TARGET_PORT}/g" $NGINX_CONF
sudo nginx -s reload

# 7. 포트 기록 업데이트 & 구버전 종료
echo "$TARGET_PORT" > $CURRENT_PORT_FILE

if [ "$TARGET_COLOR" == "blue" ]; then
  OLD_COLOR="green"
else
  OLD_COLOR="blue"
fi

OLD_PROJECT_NAME="${APP_NAME}-${TARGET_ENV}-${OLD_COLOR}"
echo "🛑 Stopping old container ($OLD_PROJECT_NAME)..."
HOST_PORT=$CURRENT_PORT docker compose -f docker-compose.app.yml -p $OLD_PROJECT_NAME down

echo "🎉 $TARGET_ENV Deployment Finished!"