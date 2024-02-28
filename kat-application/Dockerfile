FROM eclipse-temurin:11-jre-alpine

ENV BASE_DIR=/opt/kat/ \
    USER=ind-app \
    LOG_DIR=/var/log/

ARG JARFILE=target/kat-application-*.jar
ARG APP_NAME=kat

ADD $JARFILE ${BASE_DIR}/${APP_NAME}.jar

RUN mkdir -p ${LOG_DIR} \
  && adduser -HD -h ${BASE_DIR} -u 1000 ${USER} \
  && chown ${USER}:${USER} -R ${BASE_DIR} ${LOG_DIR}

WORKDIR ${BASE_DIR}
USER ${USER}
CMD java -XX:MaxRAMPercentage=75 ${JAVA_OPTS} -jar ${APP_NAME}.jar