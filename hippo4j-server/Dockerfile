# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

FROM openjdk:8-jre-slim
MAINTAINER lijianxin

ENV MYSQL_HOST="127.0.0.1" \
    MYSQL_PORT="3306" \
    MYSQL_DB="hippo4j_manager" \
    MYSQL_USERNAME="root" \
    MYSQL_PASSWORD="root" \
    BASE_DIR="/opt/hippo4j"

ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ADD conf/hippo4j-logback.xml ${BASE_DIR}/logback.xml
ADD target/hippo4j-server.jar ${BASE_DIR}/hippo4j-server.jar

WORKDIR ${BASE_DIR}

ENTRYPOINT ["sh","-c","java -jar \
 -Xloggc:${BASE_DIR}/hippo4j_gc.log -verbose:gc -XX:+PrintGCDetails \
      -XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps -XX:+UseGCLogFileRotation \
      -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=100M \
      -XX:-OmitStackTraceInFastThrow -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${BASE_DIR}/java_heapdump.hprof \
      -Xms1024m -Xmx1024m -Xmn512m \
      -Dhippo4j.standalone=true -Dhippo4j.home=${BASE_DIR} \
 hippo4j-server.jar \
 --server.max-http-header-size=524288 --server.tomcat.basedir=${BASE_DIR}/tomcat/ \
     --logging.config=${BASE_DIR}/logback.xml \
     --spring.datasource.url=\"jdbc:mysql://$MYSQL_HOST:$MYSQL_PORT/$MYSQL_DB?characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true&serverTimezone=GMT%2B8\" \
     --spring.datasource.username=$MYSQL_USERNAME --spring.datasource.password=$MYSQL_PASSWORD \
 "]