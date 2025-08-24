# Stage 1: Build
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM openjdk:17.0.1-jdk-slim
WORKDIR /app
COPY --from=build /app/target/Trash2Cash-0.0.1-SNAPSHOT.jar Trash2Cash.jar
EXPOSE 8089
ENTRYPOINT ["java","-jar","Trash2Cash.jar"]

