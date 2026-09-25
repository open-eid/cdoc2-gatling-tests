ARG IMAGE=eclipse-temurin:21
FROM ${IMAGE}

WORKDIR /gatling

ARG JAR_FILE=/target/cdoc2-auth-server-test-1.0.0-SNAPSHOT.jar

COPY ${JAR_FILE} /gatling/gatling-tests.jar
COPY logback.xml /gatling/logback.xml

CMD java ${JAVA_OPTS} -cp /gatling/gatling-tests.jar ${MAIN_CLASS} ${MAIN_CLASS_ARGS}

