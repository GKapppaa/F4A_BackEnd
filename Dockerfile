FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /workspace/target/web-0.0.1-SNAPSHOT.jar app.jar

ENV PORT=8081
EXPOSE 8081

ENTRYPOINT ["sh", "-c", "exec java -Dserver.port=${PORT:-8081} -jar /app/app.jar"]
