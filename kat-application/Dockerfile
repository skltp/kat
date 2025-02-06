FROM eclipse-temurin:17-jre-alpine

ENV BASE_DIR=/opt/kat/ \
    USER=ind-app \
    LOG_DIR=/tmp/log/kat \
    APP_NAME=kat

ARG JARFILE=target/kat-application-*.jar

ADD $JARFILE ${BASE_DIR}/${APP_NAME}.jar

RUN adduser -HD -h ${BASE_DIR} -u 1000 ${USER}

WORKDIR ${BASE_DIR}
USER ${USER}
CMD java -XX:MaxRAMPercentage=75 ${JAVA_OPTS} -jar ${APP_NAME}.jar