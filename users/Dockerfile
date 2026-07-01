###
### build
###
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

COPY ./gradle ./gradle
COPY ./gradlew ./
COPY ./build.gradle.kts ./settings.gradle.kts ./

RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon

COPY ./src ./src

RUN ./gradlew bootJar --no-daemon

###
### run
###
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java","-jar","app.jar"]
