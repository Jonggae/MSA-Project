# Use a base image with Java and Gradle
FROM gradle:7.4.2-jdk11 AS build

# Set the working directory
WORKDIR /home/gradle/project

# Copy the project files
COPY . .

# Build the project
RUN gradle build --no-daemon

# Use an official OpenJDK runtime as a parent image
FROM openjdk:11-jre-slim

# Set the working directory
WORKDIR /app

# Copy the jar file from the build stage
COPY --from=build /home/gradle/project/build/libs/*.jar app.jar

# Expose the port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
