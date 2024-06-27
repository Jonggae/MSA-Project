FROM openjdk:21-jdk-slim
COPY . /app
WORKDIR /app
RUN ./gradlew build -x test
EXPOSE 8085
CMD ["java", "-jar", "build/libs/api-gateway-0.0.1-SNAPSHOT.jar"]
