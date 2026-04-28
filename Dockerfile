FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY consent-command-service/pom.xml .
RUN mvn dependency:go-offline -B

COPY consent-command-service/src ./src

RUN mvn package -DskipTests -B

FROM eclipse-temurin:21-jre-jammy

RUN groupadd -r appuser && useradd -r -g appuser -m appuser

WORKDIR /app

COPY --from=build /app/target/consent-command-service-0.0.1-SNAPSHOT.jar app.jar

COPY opa/policies /opa/policies

COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

RUN chown appuser:appuser app.jar /entrypoint.sh

USER appuser

EXPOSE 8080

ENV JAVA_OPTS=

ENTRYPOINT ["/entrypoint.sh"]
