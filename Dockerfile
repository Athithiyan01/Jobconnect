# Use an official Maven image to build the app
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy project files
COPY JobConnect/pom.xml JobConnect/pom.xml
COPY JobConnect/src JobConnect/src

# Build the JAR
RUN mvn -f JobConnect/pom.xml clean package -DskipTests

# Use a smaller JDK image for runtime
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copy the JAR from build stage
COPY --from=build /app/JobConnect/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
