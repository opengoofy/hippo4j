---
sidebar_position: 2
---

# hippo4j server docker 构建

可以通过以下命令快速构建 Hippo4J Server：

方式一：

```shell
# 进入到 hippo4j-server 工程路径下
mvn clean package
# 默认打包是打包的 tag 是 latest
docker build -t hippo4j-server ../hippo4j-server
```

方式二：
通过 `maven docker plugin`

```shell
# 进入到 hippo4j-server 工程路径下
mvn clean package -DskipTests docker:build
```

## Docker 镜像方式搭建 Hippo4J Server

- 下载镜像

```shell
# Docker地址：https://hub.docker.com/r/xxxx/hippo4j-server/(建议指定版本号)
docker pull hippo4j-server
```

- 创建容器并运行

```shell
docker run -p 6691:6691 --name hippo4j-server -d hippo4j-server:{指定版本}

/**
* 暂时只暴露以下参数
* MYSQL_HOST、MYSQL_PORT、MYSQL_DB、MYSQL_USERNAME、MYSQL_PASSWORD
*/
docker run -d -p 6691:6691 --name hippo4j-server \
-e MYSQL_HOST=127.0.0.1 \
-e MYSQL_PORT=3306 \
-e MYSQL_DB=hippo4j_manager \
-e MYSQL_USERNAME=root \
-e MYSQL_PASSWORD=root \
hippo4j/hippo4j-server 
```
