FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY tcc-security-starter /tmp/tcc-security-starter
RUN cd /tmp/tcc-security-starter && mvn install -DskipTests -B

COPY consent-command-service/consent-command-service/pom.xml .
RUN mvn dependency:go-offline -B

COPY consent-command-service/consent-command-service/src ./src

RUN mvn package -DskipTests -B

FROM eclipse-temurin:21-jre-jammy

RUN groupadd -r appuser && useradd -r -g appuser -m appuser

WORKDIR /app

COPY --from=build /app/target/consent-command-service-0.0.1-SNAPSHOT.jar app.jar

COPY consent-command-service/opa/policies /opa/policies

COPY consent-command-service/entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

RUN chown appuser:appuser app.jar /entrypoint.sh

EXPOSE 8080

ENV JAVA_OPTS=

ENTRYPOINT ["/entrypoint.sh"]
