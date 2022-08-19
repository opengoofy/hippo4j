FROM openjdk:8-jre-slim
MAINTAINER lijianxin

ENV PARAMS=""

ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ADD conf/hippo4j-logback.xml /opt/hippo4j/logback.xml
ADD target/hippo4j-server.jar /opt/hippo4j/hippo4j-server.jar

WORKDIR /opt/hippo4j

ENTRYPOINT ["sh","-c","java -jar -Dhippo4j.home=/opt/hippo4j $JAVA_OPTS /opt/hippo4j/hippo4j-server.jar --server.tomcat.basedir=/opt/tomcat --logging.config=/opt/hippo4j/logback.xml $PARAMS"]