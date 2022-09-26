---
sidebar_position: 2
---

# Docker 部署

## 镜像启动

Docker 镜像默认使用内置 H2 数据库，数据持久化到 Docker 容器存储卷中。

```shell
docker run -d -p 6691:6691 --name hippo4j-server hippo4j/hippo4j-server
```

访问 Server 控制台，路径 `http://localhost:6691/index.html`，默认用户名密码：admin / 123456

## 镜像构建

如果想要自定义镜像，可以通过以下命令快速构建 Hippo4J Server：

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
