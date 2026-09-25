ARG IMAGE=openjdk:17-alpine
FROM ${IMAGE}

# the gatling .jar file is provided at runtime
ARG JAR_FILE=gatling.jar

COPY ${JAR_FILE} /gatling/gatling-tests.jar

CMD java ${JAVA_OPTS} -cp /gatling/gatling-tests.jar ${MAIN_CLASS} ${MAIN_CLASS_ARGS}

