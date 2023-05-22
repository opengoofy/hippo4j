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

ENV DATASOURCE_MODE="h2" \
    DATASOURCE_HOST="127.0.0.1" \
    DATASOURCE_PORT="3306" \
    DATASOURCE_DB="hippo4j_manager" \
    DATASOURCE_USERNAME="root" \
    DATASOURCE_PASSWORD="root" \
    BASE_DIR="/opt/hippo4j"

ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ADD conf/hippo4j-logback.xml ${BASE_DIR}/conf/hippo4j-logback.xml
ADD conf/application.properties ${BASE_DIR}/conf/application.properties
ADD conf/application-h2.properties ${BASE_DIR}/conf/application-h2.properties
ADD target/hippo4j-server.jar ${BASE_DIR}/hippo4j-server.jar
ADD docker-startup.sh ${BASE_DIR}/docker-startup.sh

WORKDIR ${BASE_DIR}
RUN chmod +x docker-startup.sh

ENTRYPOINT  ["./docker-startup.sh"]
 