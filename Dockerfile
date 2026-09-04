FROM maven:3.8.5-eclipse-temurin-17 AS build

COPY pom.xml /usr/src/app/pom.xml
RUN mvn -f /usr/src/app/pom.xml dependency:go-offline

COPY src /usr/src/app/src
RUN mvn -f /usr/src/app/pom.xml clean package

#--
FROM eclipse-temurin:17-jre

EXPOSE 8080

ENV APP_HOME="/usr/share/proxx-game"
ENV LANG=C.UTF-8

RUN mkdir -p $APP_HOME

COPY --from=build /usr/src/app/target/proxx-game-1.0-jar-with-dependencies.jar $APP_HOME/app.jar

ENTRYPOINT java $JAVA_OPTS -jar $APP_HOME/app.jar