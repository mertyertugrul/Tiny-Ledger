FROM gradle:8-jdk17 AS build
LABEL authors="merty.ertugrul"
WORKDIR /app

COPY gradle gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .

COPY src src
RUN ./gradlew clean build --no-daemon


FROM amazoncorretto:17-alpine
WORKDIR /app

COPY --from=build /app/target/tiny-ledger-0.0.1-SNAPSHOT.jar /app/tiny-ledger.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","tiny-ledger.jar"]