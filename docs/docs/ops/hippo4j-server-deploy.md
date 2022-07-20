---
sidebar_position: 1
---

# hippo4j server 部署


[RELEASE](https://github.com/longtai-cn/hippo4j/releases) 页面下载对应版本并进行解压。

## 初始化

修改数据库相关信息。

```txt
/conf/application.properties
```

数据库执行 SQL 脚本。

```txt
/conf/hippo4j_manager.sql
```

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
