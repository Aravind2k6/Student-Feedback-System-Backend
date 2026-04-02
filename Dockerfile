# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the pom.xml and source code
COPY pom.xml .
# Download dependencies first (layered for caching)
RUN mvn dependency:go-offline

COPY src ./src
# Build the application
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=build /app/target/student-feedback-backend-1.0.0.jar app.jar

# Expose port (Render sets PORT env variable, defaulting to 8080 if not set, Spring Boot uses server.port)
EXPOSE 8080

# Run the app dynamically binding the port Render gives us, or defaulting to 8080
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]
