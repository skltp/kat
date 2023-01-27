FROM openjdk:11-oraclelinux8

ENV APP_NAME=kat \
    BASE_DIR=/opt/kat/ \
    USER=ind-app \
    LOG_DIR=/var/log/ \
    JARFILE=target/kat-application-*.jar

ADD $JARFILE ${BASE_DIR}/${APP_NAME}.jar

RUN mkdir -p ${LOG_DIR} \
  && useradd -Ms /bin/bash -b ${BASE_DIR} -u 556559423 ${USER} \
  && chown ${USER}:${USER} -R ${BASE_DIR} ${LOG_DIR}

WORKDIR ${BASE_DIR}
USER ${USER}
CMD java -jar ${APP_NAME}.jar