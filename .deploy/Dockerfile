FROM maven:3.8.6-eclipse-temurin-17-alpine as build

WORKDIR /app
COPY ./src ./src
COPY ./pom.xml .
ENV DOCKER_HOST=tcp://172.17.0.1:2376
RUN mvn clean package

RUN jar -xf target/*.jar

FROM eclipse-temurin:17-jre-alpine

RUN apk --update --no-cache add curl

COPY --from=build /app/BOOT-INF/lib /app/lib
COPY --from=build /app/META-INF /app/META-INF
COPY --from=build /app/BOOT-INF/classes /app
COPY --from=build /app/org/springframework/boot/loader/ /app/org/springframework/boot/loader/

ENV JAVA_OPTS=""

ENTRYPOINT java -cp app:app/lib/* $JAVA_OPTS org.springframework.boot.loader.JarLauncher
