# Stage 1: build
FROM maven:3.9.12-eclipse-temurin-21 AS build
WORKDIR /backend
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: runtime
FROM amazoncorretto:21
WORKDIR /backend
COPY --from=build /backend/target/pulse-0.0.1-SNAPSHOT.jar pulse.jar
ENTRYPOINT ["java", "-jar", "pulse.jar"]