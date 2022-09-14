---
sidebar_position: 2
---

# hippo4j server docker 构建

## 镜像构建

可以通过以下命令快速构建 Hippo4J Server：

方式一：

```shell
# 进入到 hippo4j-server 工程路径下
mvn clean package -Dskip.spotless.apply=true
# 默认打包是打包的 tag 是 latest
docker build -t hippo4j/hippo4j-server ../hippo4j-server
```

方式二：
通过 `maven docker plugin`

```shell
# 进入到 hippo4j-server 工程路径下
mvn clean package -DskipTests -Dskip.spotless.apply=true docker:build
```

## 镜像启动

- 下载镜像

```shell
docker pull hippo4j/hippo4j-server
```

- 创建容器并运行

```shell
docker run -d -p 6691:6691 --name hippo4j-server hippo4j/hippo4j-server

# 如需个性化参数指定，请参考下述命令

/**
 * 暂时只暴露以下参数
 *
 * MYSQL_HOST、MYSQL_PORT、MYSQL_DB、MYSQL_USERNAME、MYSQL_PASSWORD
 * MYSQL_HOST 需要使用本地 IP，不能使用 127.0.0.1
 */
docker run -d -p 6691:6691 --name hippo4j-server \
-e MYSQL_HOST=xxx.xxx.x.x \
-e MYSQL_PORT=3306 \
-e MYSQL_DB=hippo4j_manager \
-e MYSQL_USERNAME=root \
-e MYSQL_PASSWORD=root \
hippo4j/hippo4j-server 
```
