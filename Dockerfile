# ---- Stage 1: Build ----
FROM eclipse-temurin:25-jdk-alpine as builder
WORKDIR /build

# Install Maven
RUN apk add --no-cache maven

# Download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Build application
COPY src ./src
RUN mvn clean package -DskipTests

# ---- Stage 2: Runtime ----
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Copy JAR from builder
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
