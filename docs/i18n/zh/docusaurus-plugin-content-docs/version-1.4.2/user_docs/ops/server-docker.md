---
sidebar_position: 2
---

# Docker部署

## 镜像启动

Docker 镜像默认使用内置 H2 数据库，数据持久化到 Docker 容器存储卷中。

```shell
docker run -d -p 6691:6691 --name hippo4j-server hippo4j/hippo4j-server
```

或者，底层存储数据库切换为 MySQL。`DATASOURCE_HOST` 需要切换为本地 IP，不能使用 `127.0.0.1` 或 `localhost`。

```shell
docker run -d -p 6691:6691 --name hippo4j-server \
-e DATASOURCE_MODE=mysql \
-e DATASOURCE_HOST=xxx.xxx.xxx.xxx \
-e DATASOURCE_PORT=3306 \
-e DATASOURCE_DB=hippo4j_manager \
-e DATASOURCE_USERNAME=root \
-e DATASOURCE_PASSWORD=root \
hippo4j/hippo4j-server
```

访问 Server 控制台，路径 `http://localhost:6691/index.html` ，默认用户名密码：admin / 123456

## 镜像构建

如果想要自定义镜像，可以通过以下命令快速构建 Hippo4j Server：

方式一：

```shell
# 进入到 hippo4j-server/hippo4j-bootstrap 工程路径下
mvn clean package -Dskip.spotless.apply=true
# 默认打包是打包的 tag 是 latest
docker build -t hippo4j/hippo4j-server ../hippo4j-bootstrap
```

方式二：

通过 `maven docker plugin`

```shell
# 进入到 hippo4j-server 工程路径下
mvn clean package -DskipTests -Dskip.spotless.apply=true docker:build
```
