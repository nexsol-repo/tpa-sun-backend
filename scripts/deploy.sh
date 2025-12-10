#!/bin/bash

# 사용법: ./deploy.sh [dev|prod]
TARGET_ENV=$1

if [ -z "$TARGET_ENV" ]; then
  echo "⚠️ Usage: ./deploy.sh [dev|prod]"
  exit 1
fi

APP_NAME="tpa-sun-api"
BASE_PATH="/home/nex3/app/${APP_NAME}"

# 환경별 설정 (Nginx Conf 경로 및 기본 포트)
if [ "$TARGET_ENV" == "prod" ]; then
  ENV_FILE=".env.prod"
  NGINX_CONF="/etc/nginx/conf.d/tpa-sun-api-prod.conf"
  DEFAULT_PORT="8091" # Prod: 8091 ~ 8092
else
  ENV_FILE=".env.dev"
  NGINX_CONF="/etc/nginx/conf.d/tpa-sun-api-dev.conf"
  DEFAULT_PORT="8081" # Dev: 8081 ~ 8082
fi

echo "🚀 Starting Deployment for $TARGET_ENV environment..."

# 1. 환경변수 파일 준비 (.env 생성)
if [ -f "${BASE_PATH}/${ENV_FILE}" ]; then
  cp "${BASE_PATH}/${ENV_FILE}" "${BASE_PATH}/.env"
  # 현재 쉘에 환경변수 로드 (스크립트 내 사용 목적)
  set -a
  source "${BASE_PATH}/.env"
  set +a
else
  echo "❌ Environment file ${ENV_FILE} not found at ${BASE_PATH}"
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
# .env에 DOCKER_IMAGE가 있지만, CI에서 주입된 값이 있다면 우선순위를 가질 수 있음
# export DOCKER_IMAGE=${DOCKER_IMAGE:-"tpa-sun-backend:${TARGET_ENV}"}

COMPOSE_PROJECT_NAME="${APP_NAME}-${TARGET_ENV}-${TARGET_COLOR}"

# -p 옵션으로 프로젝트 이름을 지정하여 중복 실행 방지 및 격리
docker compose -f docker-compose.app.yml -p $COMPOSE_PROJECT_NAME up -d

# 5. Health Check
echo "🏥 Health Checking ($TARGET_PORT)..."
for i in {1..12}; do
  STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:${TARGET_PORT}/health)
  if [ "$STATUS" == "200" ]; then
    echo "✅ Health Check Passed!"
    break
  fi
  echo "⏳ Waiting... ($i/12) HTTP $STATUS"
  sleep 5
done

if [ "$STATUS" != "200" ]; then
  echo "❌ Health Check Failed. Rolling back..."
  docker compose -f docker-compose.app.yml -p $COMPOSE_PROJECT_NAME down
  exit 1
fi

# 6. Nginx 설정 변경 & Reload
echo "🔄 Switching Nginx Traffic..."
# 환경별 Nginx 설정 파일에서 proxy_pass 포트 변경
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