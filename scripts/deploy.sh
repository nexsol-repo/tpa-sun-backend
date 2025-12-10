#!/bin/bash

# Usage: ./deploy.sh [dev|prod]
TARGET_ENV=$1

if [ -z "$TARGET_ENV" ]; then
  echo "⚠️ Usage: ./deploy.sh [dev|prod]"
  exit 1
fi

APP_NAME="tpa-sun-api"
BASE_PATH="/home/nex3/app/${APP_NAME}"

# Environment specific settings
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

# 1. Prepare .env file
if [ -f "${BASE_PATH}/${ENV_FILE}" ]; then
  echo "📄 Copying ${ENV_FILE} to .env"
  cp "${BASE_PATH}/${ENV_FILE}" "${BASE_PATH}/.env"
else
  echo "❌ Environment file ${ENV_FILE} not found at ${BASE_PATH}"
  echo "   Please create it manually on the server."
  exit 1
fi

# 2. Check currently running port
CURRENT_PORT_FILE="${BASE_PATH}/current_port_${TARGET_ENV}.txt"
# If file doesn't exist, default to DEFAULT_PORT
if [ -f "$CURRENT_PORT_FILE" ]; then
    CURRENT_PORT=$(cat "$CURRENT_PORT_FILE")
else
    CURRENT_PORT="$DEFAULT_PORT"
fi

# 3. Determine Target Port
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

# 4. Run Container
export HOST_PORT=$TARGET_PORT

# Set DOCKER_IMAGE from env var or default
if [ -z "$DOCKER_IMAGE" ]; then
  export DOCKER_IMAGE="tpa-sun-api:${TARGET_ENV}"
fi

COMPOSE_PROJECT_NAME="${APP_NAME}-${TARGET_ENV}-${TARGET_COLOR}"

# Use --env-file explicitly to avoid ambiguity, though standard .env in dir works too
docker compose -f docker-compose.app.yml -p $COMPOSE_PROJECT_NAME up -d

# 5. Health Check
echo "🏥 Health Checking ($TARGET_PORT)..."

for i in {1..5}; do
  # Use 127.0.0.1 explicitly to avoid localhost resolution issues
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

  echo "--- Docker Logs (Last 50 lines) ---"
  docker compose -f docker-compose.app.yml -p $COMPOSE_PROJECT_NAME logs --tail 50
  echo "-----------------------------------"

  echo "Rolling back..."
  docker compose -f docker-compose.app.yml -p $COMPOSE_PROJECT_NAME down
  exit 1
fi

# 6. Switch Nginx Traffic
echo "🔄 Switching Nginx Traffic..."
# Update proxy_pass port in nginx config
sudo sed -i "s/127.0.0.1:[0-9]\{4\}/127.0.0.1:${TARGET_PORT}/g" $NGINX_CONF
sudo nginx -s reload

# 7. Update Current Port File
echo "$TARGET_PORT" > "$CURRENT_PORT_FILE"

# 8. Stop Old Container
if [ "$TARGET_COLOR" == "blue" ]; then
  OLD_COLOR="green"
else
  OLD_COLOR="blue"
fi

OLD_PROJECT_NAME="${APP_NAME}-${TARGET_ENV}-${OLD_COLOR}"
echo "🛑 Stopping old container ($OLD_PROJECT_NAME)..."
# We need to set HOST_PORT for the old container to down it correctly,
# though 'down' mainly needs project name. Setting it to old port to be safe.
HOST_PORT=$CURRENT_PORT docker compose -f docker-compose.app.yml -p $OLD_PROJECT_NAME down

echo "🎉 $TARGET_ENV Deployment Finished!"