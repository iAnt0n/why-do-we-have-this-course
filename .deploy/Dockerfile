FROM maven:3.8.6-eclipse-temurin-17-alpine as build

WORKDIR /app
COPY ./src ./src
COPY ./pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn clean package

RUN jar -xf target/*.jar

FROM eclipse-temurin:17-jre-alpine

COPY --from=build /app/BOOT-INF/lib /app/lib
COPY --from=build /app/META-INF /app/META-INF
COPY --from=build /app/BOOT-INF/classes /app

ENTRYPOINT ["java", "-cp", "app:app/lib/*", "ru.itmo.lab1.Lab1Application"]
