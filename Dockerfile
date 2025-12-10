# 1. Builder Stage (Eclipse Temurin Java 25)
FROM eclipse-temurin:25-jdk AS builder

WORKDIR /app

# Gradle 캐싱을 위해 설정 파일만 먼저 복사
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties .
# 서브 모듈이 있다면 해당 build.gradle들도 복사 필요 (구조에 따라 수정)
# COPY core/build.gradle ./core/
# ...

# 실행 권한 부여
RUN chmod +x ./gradlew

# 의존성만 다운로드 (소스 변경 시 캐시 활용)
RUN ./gradlew dependencies --no-daemon || return 0

# 전체 소스 복사 및 빌드
COPY . .
# 테스트 제외하고 bootJar 빌드 (빠른 빌드를 위함)
RUN ./gradlew clean bootJar -x test --no-daemon

# 2. Runtime Stage
FROM eclipse-temurin:25-jre

WORKDIR /app

# 빌드 결과물 복사 (경로는 프로젝트 구조에 따라 다를 수 있음)
# find 명령어로 jar를 찾아서 app.jar로 복사하는 트릭 사용
COPY --from=builder /app/core/core-api/build/libs/*.jar app.jar

# 시간대 설정 (KST)
ENV TZ=Asia/Seoul

EXPOSE 8080

CMD ["java", "-jar", "-Djava.security.egd=file:/dev/./urandom", "-Dspring.profiles.active=dev", "app.jar"]