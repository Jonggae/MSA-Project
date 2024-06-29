# Base image with Java 21
FROM openjdk:21-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the project files
COPY . .

# Use the Gradle Wrapper to build the project
RUN ./gradlew build -x test

# Copy the built jar file to the working directory
RUN cp build/libs/api-gateway-0.0.1-SNAPSHOT.jar app.jar

# Set the entry point for the container
CMD ["java", "-jar", "app.jar"]
