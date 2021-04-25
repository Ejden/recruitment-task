FROM gradle:latest as builder
COPY build.gradle.kts .
COPY src ./src
RUN gradle clean build --scan

FROM openjdk:11-alpine
COPY --from=builder home/gradle/build/libs/recruitment-task-0.0.1-SNAPSHOT.jar /recruitment-task.jar
CMD [ "java", "-jar", "/recruitment-task.jar" ]