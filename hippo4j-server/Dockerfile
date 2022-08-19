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

ENV PARAMS=""

ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ADD conf/hippo4j-logback.xml /opt/hippo4j/logback.xml
ADD target/hippo4j-server.jar /opt/hippo4j/hippo4j-server.jar

WORKDIR /opt/hippo4j

ENTRYPOINT ["sh","-c","java -jar -Dhippo4j.home=/opt/hippo4j $JAVA_OPTS /opt/hippo4j/hippo4j-server.jar --server.tomcat.basedir=/opt/tomcat --logging.config=/opt/hippo4j/logback.xml $PARAMS"]