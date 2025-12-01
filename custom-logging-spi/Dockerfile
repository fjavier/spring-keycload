FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /build
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package

FROM quay.io/keycloak/keycloak:25.0

COPY target/*.jar /opt/keycloak/providers/

RUN /opt/keycloak/bin/kc.sh build

ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
CMD ["start-dev"]
