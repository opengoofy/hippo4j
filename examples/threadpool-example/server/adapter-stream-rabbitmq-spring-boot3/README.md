## spring-boot3 仅支持jdk17以上版本
## 启动项添加JVM配置参数：
``` 
--add-opens java.base/java.util.concurrent=ALL-UNNAMED
```