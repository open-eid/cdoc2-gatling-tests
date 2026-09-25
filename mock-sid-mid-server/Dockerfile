# this file is used to build a docker image with cdoc Gatling tests

# for RIA infra
#FROM nexus.riaint.ee:8500/library/openjdk:21-alpine

# for non-RIA infra
FROM eclipse-temurin:21

WORKDIR /mock-sid-mid

ARG JAR_FILE=/target/cdoc2-sid-mid-mock-1.0.0-SNAPSHOT.jar

COPY ${JAR_FILE} /mock-sid-mid/mock.jar

CMD exec java ${JAVA_OPTS} -jar /mock-sid-mid/mock.jar

