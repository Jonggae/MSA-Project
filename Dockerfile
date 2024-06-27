FROM openjdk:21-jdk-slim
COPY . /app
WORKDIR /app
RUN ./gradlew build -x test
CMD ["java", "-jar", "build/libs/eureka-0.0.1-SNAPSHOT.jar"]
