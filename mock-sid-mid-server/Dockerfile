ARG IMAGE=eclipse-temurin:21
FROM ${IMAGE}

WORKDIR /mock-sid-mid

ARG JAR_FILE=/target/cdoc2-sid-mid-mock-1.0.0-SNAPSHOT.jar

COPY ${JAR_FILE} /mock-sid-mid/mock.jar

CMD exec java ${JAVA_OPTS} -jar /mock-sid-mid/mock.jar

