---
sidebar_position: 1
---

# 源码包部署

[RELEASE](https://github.com/opengoofy/hippo4j/releases) 页面下载对应版本并进行解压。

## 初始化

修改数据库相关信息。

```txt
/conf/application.properties
```

如果是新运行 Hippo4j，数据库执行下述 SQL 脚本即可。

```txt
/conf/hippo4j_manager.sql
```

如果是对已运行 Hippo4j 升级，请查看 `/conf/sql-upgrade` 目录下，是否有目标版本对应的升级脚本。

## 直接运行

Mac Linux 启动执行。

```txt
sh ./bin/startup.sh
```

Windows 启动执行。

```txt
bin/startup.cmd
```

## 访问控制台

启动成功后，访问链接。用户名密码：admin 123456

```txt
localhost:6691/index.html
```
