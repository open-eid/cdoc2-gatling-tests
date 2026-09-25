# this file is used to build a docker image with cdoc Gatling tests

# for RIA infra
#FROM nexus.riaint.ee:8500/library/openjdk:21-alpine

# for non-RIA infra
FROM eclipse-temurin:21

WORKDIR /gatling

ARG JAR_FILE=/target/cdoc2-auth-server-test-1.0.0-SNAPSHOT.jar

COPY ${JAR_FILE} /gatling/gatling-tests.jar
COPY logback.xml /gatling/logback.xml

CMD java ${JAVA_OPTS} -cp /gatling/gatling-tests.jar ${MAIN_CLASS} ${MAIN_CLASS_ARGS}

