# 09 - 配置与 DevOps

## 1. 多环境 Profile 设计

```
yudao-server/src/main/resources/
├── application.yaml              # 公共配置（所有环境共享）
├── application-local.yaml        # 本地开发环境（默认激活）
├── application-dev.yaml          # 远程开发服务器
├── application-prod.yaml         # 生产环境
├── logback-spring.xml            # 日志配置
└── .env.example                  # 环境变量模板
```

### 1.1 application.yaml — 公共配置

```yaml
# ========== Spring 基础 ==========
spring:
  application:
    name: yudao-server
  profiles:
    active: local                  # 默认使用 local profile
  main:
    allow-circular-references: true
  servlet:
    multipart:
      max-file-size: 100MB
      max-request-size: 100MB

# ========== 云基础设施（默认禁用） ==========
spring:
  cloud:
    nacos:
      discovery:
        enabled: false            # 单机模式不使用 Nacos
      config:
        enabled: false

# ========== MyBatis Plus ==========
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
  global-config:
    db-config:
      id-type: NONE               # 自动检测
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

# ========== 项目自定义配置 ==========
yudao:
  info:
    version: 1.0.0
    base-package: cn.iocoder.yudao
  security:
    mock-enable: false            # 默认关闭 Mock 登录
  captcha:
    enable: true
  access-log:
    enable: true
  xss:
    enable: true
  tenant:
    enable: true
```

### 1.2 application-local.yaml — 本地开发

```yaml
server:
  port: 48080

# ========== 数据源 ==========
spring:
  datasource:
    dynamic:
      primary: master
      datasource:
        master:
          url: jdbc:mysql://127.0.0.1:3306/ruoyi-vue-pro?useSSL=false&serverTimezone=Asia/Shanghai
          username: root
          password: ${DB_PASSWORD:root}
        slave:
          url: jdbc:mysql://127.0.0.1:3306/ruoyi-vue-pro-slave
          username: root
          password: ${DB_PASSWORD:root}
          lazy: true

# ========== Redis ==========
  data:
    redis:
      host: 127.0.0.1
      port: 6379
      database: 0
      password:                  # 本地无密码

# ========== 日志 ==========
logging:
  level:
    cn.iocoder.yudao.module.system.dal.mysql: DEBUG
    cn.iocoder.yudao.module.infra.dal.mysql: DEBUG

# ========== Yudao 本地配置 ==========
yudao:
  security:
    mock-enable: true            # 开启 Mock 登录，跳过认证
  captcha:
    enable: false                # 关闭验证码
  access-log:
    enable: false                # 关闭访问日志
  xss:
    enable: false                # 关闭 XSS 过滤

# ========== XXL-Job（本地默认禁用） ==========
xxl:
  job:
    enabled: false
```

### 1.3 环境变量管理

```bash
# .env.example — 敏感信息模板
DB_PASSWORD=root
REDIS_PASSWORD=
OPENAI_API_KEY=sk-xxx
DEEPSEEK_API_KEY=sk-xxx
ANTHROPIC_API_KEY=sk-xxx
ADMIN_UI_URL=http://localhost:48080
AES_ENCRYPT_KEY=xxx
```

在配置中引用：`${DB_PASSWORD:root}`（优先从环境变量读取，fallback 为 `root`）

## 2. 自动配置排除

当不需要某些自动配置时，在主配置中排除：

```yaml
spring:
  autoconfigure:
    exclude:
      # 不需要的向量存储
      - org.springframework.ai.vectorstore.qdrant.QdrantVectorStoreAutoConfiguration
      - org.springframework.ai.vectorstore.milvus.MilvusVectorStoreAutoConfiguration
      # 单体模式不需要 Feign
      - org.springframework.cloud.openfeign.FeignAutoConfiguration
```

或在启动类上：

```java
@SpringBootApplication(
    exclude = {
        FeignAutoConfiguration.class,
        QdrantVectorStoreAutoConfiguration.class
    }
)
```

## 3. Maven 模块自动排除

在单体模式下，通过 POM 依赖排除来控制模块行为：

```xml
<!-- yudao-server/pom.xml -->
<dependency>
    <groupId>cn.iocoder.cloud</groupId>
    <artifactId>yudao-spring-boot-starter-rpc</artifactId>
    <exclusions>
        <!-- 单体模式：排除 Feign -->
        <exclusion>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

## 4. 日志配置

```xml
<!-- logback-spring.xml -->
<configuration>
    <!-- 引入 Spring Boot 默认日志配置 -->
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>

    <!-- 控制台输出 -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- 按模块控制日志级别 -->
    <logger name="cn.iocoder.yudao.module.system" level="DEBUG"/>
    <logger name="cn.iocoder.yudao.module.infra" level="DEBUG"/>
    <logger name="cn.iocoder.yudao.framework" level="INFO"/>

    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
    </root>
</configuration>
```

## 5. Lombok 配置

项目根目录的 `lombok.config`：

```properties
# 全局配置
config.stopBubbling = true
lombok.chain = true                              # 所有 @Setter 返回 this
lombok.toString.callSuper = CALL                 # toString 包含父类字段
lombok.equalsAndHashCode.callSuper = CALL        # equals/hashCode 包含父类字段
lombok.addLombokGeneratedAnnotation = true       # JaCoCo 忽略 Lombok 生成的方法
```

## 6. Docker Compose 本地基础设施

```yaml
# sql/tools/docker-compose.yaml — 开发依赖服务
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    ports:
      - "3306:3306"
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: ruoyi-vue-pro

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"

  nacos:
    image: nacos/nacos-server:v2.3.0
    ports:
      - "8848:8848"
      - "9848:9848"

  rocketmq:
    image: apache/rocketmq:5.1.0
    ports:
      - "9876:9876"
      - "10911:10911"
```

启动：
```bash
docker-compose -f sql/tools/docker-compose.yaml up -d
```

## 7. 单体模式启动流程

```bash
# 1. 启动基础设施
docker-compose -f sql/tools/docker-compose.yaml up -d mysql redis

# 2. 创建数据库
mysql -u root -proot -e "CREATE DATABASE IF NOT EXISTS \`ruoyi-vue-pro\` DEFAULT CHARACTER SET utf8mb4;"

# 3. 导入 DDL
mysql -u root -proot ruoyi-vue-pro < sql/mysql/ruoyi-vue-pro.sql

# 4. 编译（跳过测试）
mvn clean install -DskipTests

# 5. 启动应用
cd yudao-server && mvn spring-boot:run

# 应用运行在：http://localhost:48080
# API 文档：http://localhost:48080/swagger-ui
# Druid 监控：http://localhost:48080/druid/
```

## 8. 微服务模式启动流程

```bash
# 1. 启动全部基础设施
docker-compose -f sql/tools/docker-compose.yaml up -d

# 2. 修改配置
#    - application.yaml: nacos.discovery.enabled → true
#    - application.yaml: nacos.config.enabled → true
#    - security.mock-enable → false

# 3. 启动 Nacos（如果容器化）或在本地启动

# 4. 按顺序启动服务
#    先启动 gateway，再启动各业务模块
cd yudao-gateway && mvn spring-boot:run        # 48080
cd yudao-module-system/yudao-module-system-server && mvn spring-boot:run   # 48081
cd yudao-module-infra/yudao-module-infra-server && mvn spring-boot:run     # 48082
# ... 按需启动其他模块
```

## 9. CI/CD (GitHub Actions)

```yaml
# .github/workflows/maven.yml
name: Java CI with Maven

on:
  push:
    branches: [ master ]

jobs:
  build:
    runs-on: ubuntu-latest
    strategy:
      matrix:
        java: [17]

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK ${{ matrix.java }}
        uses: actions/setup-java@v4
        with:
          java-version: ${{ matrix.java }}
          distribution: 'temurin'
          cache: maven

      - name: Build with Maven
        run: mvn -B package --file pom.xml -Dmaven.test.skip=true
```

## 10. 打包部署

```bash
# 单体 fat jar
mvn clean package -pl yudao-server -am -DskipTests
java -jar yudao-server/target/yudao-server.jar --spring.profiles.active=prod

# 各微服务独立 jar
mvn clean package -pl yudao-gateway -am -DskipTests
mvn clean package -pl yudao-module-system/yudao-module-system-server -am -DskipTests
# ...

# Docker 构建
docker build -t yudao-server:latest -f script/docker/Dockerfile .
```

## 11. 配置优先级

```
1. 命令行参数                    --server.port=8080
2. 环境变量                      DB_PASSWORD=xxx
3. application-{profile}.yaml   application-local.yaml
4. application.yaml              公共配置
5. @PropertySource               自定义配置文件
6. 默认值                        ${DB_PASSWORD:root}
```

## 12. 常用配置开关

| 配置项 | local (本地) | dev (开发) | prod (生产) |
|--------|-------------|-----------|-------------|
| `yudao.security.mock-enable` | true | false | false |
| `yudao.captcha.enable` | false | true | true |
| `yudao.access-log.enable` | false | true | true |
| `yudao.xss.enable` | false | true | true |
| `yudao.tenant.enable` | true | true | true |
| `yudao.demo` | false | false | false |
| `spring.cloud.nacos.discovery.enabled` | false | true | true |
| `spring.cloud.nacos.config.enabled` | false | true | true |
| `xxl.job.enabled` | false | true | true |
| `spring.datasource.dynamic.datasource.slave.lazy` | true | false | false |
| `logging.level.**.dal.mysql` | DEBUG | INFO | WARN |
| `server.port` | 48080 | 48080 | 8080 |

## 13. 添加新 Profile 的步骤

1. 创建 `application-{profile}.yaml`
2. 覆盖环境相关配置（数据源、Redis、日志级别等）
3. 在环境变量或启动参数中激活：`--spring.profiles.active={profile}`
4. 敏感信息使用环境变量：`${VAR_NAME:default}`
