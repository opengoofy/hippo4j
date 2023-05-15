#!/bin/bash

export JAVA_HOME
export JAVA="$JAVA_HOME/bin/java"

export SERVER="hippo4j-server"

JAVA_OPT="${JAVA_OPT} -Djava.ext.dirs=${JAVA_HOME}/jre/lib/ext:${JAVA_HOME}/lib/ext"
JAVA_OPT="${JAVA_OPT} -Xloggc:${BASE_DIR}/logs/hippo4j_gc.log -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=100M"

JAVA_OPT="${JAVA_OPT} -Xms1024m -Xmx1024m -Xmn512m"
JAVA_OPT="${JAVA_OPT} -Dhippo4j.standalone=true"
JAVA_OPT="${JAVA_OPT} -XX:-OmitStackTraceInFastThrow -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${BASE_DIR}/logs/java_heapdump.hprof"
JAVA_OPT="${JAVA_OPT} -Dhippo4j.home=${BASE_DIR}"

JAVA_OPT="${JAVA_OPT} ${JAVA_OPT_EXT}"
JAVA_OPT="${JAVA_OPT} --logging.config=${BASE_DIR}/conf/hippo4j-logback.xml"
JAVA_OPT="${JAVA_OPT} --server.max-http-header-size=524288"
JAVA_OPT="${JAVA_OPT} --server.tomcat.basedir=${BASE_DIR}/bin"

if [[ "${DATASOURCE_MODE}" == "mysql" ]]; then
  JAVA_OPT="${JAVA_OPT} --spring.profiles.active=mysql --hippo4j.database.init_enable=false "
  JAVA_OPT="${JAVA_OPT} --spring.datasource.url=jdbc:mysql://${DATASOURCE_HOST}:${DATASOURCE_PORT}/${DATASOURCE_DB}?characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true&serverTimezone=GMT%2B8 "
  JAVA_OPT="${JAVA_OPT} --spring.datasource.username=${DATASOURCE_USERNAME} --spring.datasource.password=${DATASOURCE_PASSWORD} "
elif [[ "${DATASOURCE_MODE}" == "h2" ]]; then
  JAVA_OPT="${JAVA_OPT} --spring.profiles.active=h2 --spring.datasource.url=jdbc:h2:file:${BASE_DIR}/h2_hippo4j;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;MODE=MYSQL"
else
  echo "hippo4j DATASOURCE_MODE is error, value ${DATASOURCE_MODE}, use default h2"
fi

if [ ! -d "${BASE_DIR}/logs" ]; then
  mkdir ${BASE_DIR}/logs
fi

echo "$JAVA ${JAVA_OPT}"

echo "hippo4j is starting with standalone"

if [ ! -f "${BASE_DIR}/logs/start.out" ]; then
  touch "${BASE_DIR}/logs/start.out"
fi

echo "$JAVA ${JAVA_OPT}" > ${BASE_DIR}/logs/start.out 2>&1 &
java -jar hippo4j-server.jar ${JAVA_OPT}
echo "hippo4j is starting，you can check the ${BASE_DIR}/logs/"
