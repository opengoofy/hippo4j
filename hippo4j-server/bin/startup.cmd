@echo off

if not exist "%JAVA_HOME%\bin\java.exe" echo Please set the JAVA_HOME variable in your environment, We need java(x64)! jdk8 or later is better! & EXIT /B 1
set "JAVA=%JAVA_HOME%\bin\java.exe"

setlocal enabledelayedexpansion

set BASE_DIR=%~dp0
rem added double quotation marks to avoid the issue caused by the folder names containing spaces.
rem removed the last 5 chars(which means \bin\) to get the base DIR.
set BASE_DIR="%BASE_DIR:~0,-5%"

set CUSTOM_SEARCH_LOCATIONS=%BASE_DIR%/conf/application.properties

set SERVER=hippo4j-server

echo "hippo4j is starting with standalone"
set "HIPPO4J_JVM_OPTS=-Xms1024m -Xmx1024m -Xmn512m -Dhippo4j.standalone=true -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=320m -XX:-OmitStackTraceInFastThrow -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=%BASE_DIR%\logs\java_heapdump.hprof -XX:-UseLargePages"

set "HIPPO4J_OPTS=%HIPPO4J_OPTS% -Dhippo4j.home=%BASE_DIR%"
set "HIPPO4J_OPTS=%HIPPO4J_OPTS% -jar %BASE_DIR%\target\%SERVER%.jar"


rem set hippo4j spring config location
set "HIPPO4J_CONFIG_OPTS=%HIPPO4J_CONFIG_OPTS% --spring.config.location=%CUSTOM_SEARCH_LOCATIONS%"
set "HIPPO4J_CONFIG_OPTS=%HIPPO4J_CONFIG_OPTS% --server.tomcat.basedir=%BASE_DIR%/bin"


rem set hippo4j logback file location
set "HIPPO4J_LOGBACK_OPTS=--logging.config=%BASE_DIR%/conf/hippo4j-logback.xml"

set COMMAND="%JAVA%" %HIPPO4J_JVM_OPTS% %HIPPO4J_OPTS% %HIPPO4J_CONFIG_OPTS% %HIPPO4J_LOGBACK_OPTS% hippo4j.hippo4j %*

rem start hippo4j command
%COMMAND%
