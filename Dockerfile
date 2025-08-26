# Use official OpenJDK 21 slim image
FROM openjdk:21-jdk-slim

# Install tools (for mvnw if needed)
RUN apt-get update && apt-get install -y dos2unix && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy everything into container
COPY . .

# Fix mvnw line endings + make it executable
RUN dos2unix mvnw && chmod +x mvnw

# Build the project (skip tests for faster deploys)
RUN ./mvnw clean package -DskipTests -f JobConnect/pom.xml

# Expose Spring Boot default port
EXPOSE 8080

# Run the JAR
CMD ["java", "-jar", "JobConnect/target/*.jar"]
