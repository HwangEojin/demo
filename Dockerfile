# 1단계: Java 21 및 Maven을 사용하여 애플리케이션 빌드
FROM maven:3.8.8-eclipse-temurin-21 AS build

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn package -DskipTests

# 2단계: 빌드된 .war 파일을 Tomcat 10.1 서버에 배포
FROM tomcat:10.1-jdk21-temurin

# 기존 기본 ROOT 디렉터리 및 파일 제거 후 루트 경로로 배포
RUN rm -rf /usr/local/tomcat/webapps/ROOT /usr/local/tomcat/webapps/ROOT.war
COPY --from=build /app/target/baseline.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080