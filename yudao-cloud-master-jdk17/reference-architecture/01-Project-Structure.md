# 01 - 项目结构与模块设计

## 1. 整体模块层次

```
project-root/
├── pom.xml                          # 根 POM：聚合 + pluginManagement + 仓库
├── project-dependencies/            # BOM：统一版本管理（唯一真相来源）
│   └── pom.xml
├── project-framework/               # 框架层：Spring Boot Starters
│   ├── project-common/              #   - 通用模型（异常、枚举、工具类）
│   ├── project-spring-boot-starter-web/
│   ├── project-spring-boot-starter-mybatis/
│   ├── project-spring-boot-starter-redis/
│   ├── project-spring-boot-starter-security/
│   ├── project-spring-boot-starter-mq/
│   ├── project-spring-boot-starter-job/
│   ├── project-spring-boot-starter-rpc/
│   ├── project-spring-boot-starter-protection/
│   ├── project-spring-boot-starter-test/
│   └── ...
├── project-gateway/                 # Spring Cloud Gateway（微服务入口，WebFlux）
├── project-server/                  # 空壳容器：组装所有模块为单体应用
├── project-module-system/           # 业务模块：系统管理
│   ├── project-module-system-api/   #   Feign 接口 + DTO（对外契约）
│   └── project-module-system-server/#   控制器 + 服务 + DAO（内部实现）
├── project-module-xxx/              # 其他业务模块...
└── project-ui/                      # 前端（独立 Git 仓库）
```

## 2. 根 POM 设计

### 2.1 ci-friendly 版本号

```xml
<groupId>cn.iocoder.cloud</groupId>
<artifactId>yudao</artifactId>
<version>${revision}</version>
<packaging>pom</packaging>

<properties>
    <revision>2026.03-SNAPSHOT</revision>
</properties>
```

所有子模块统一使用 `${revision}`，通过 `flatten-maven-plugin` 在发布时替换为实际版本号。

### 2.2 依赖管理：BOM 是唯一真相来源

根 POM 的 `<dependencyManagement>` 只导入一个 BOM：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>cn.iocoder.cloud</groupId>
            <artifactId>yudao-dependencies</artifactId>
            <version>${revision}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 2.3 编译器配置

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <parameters>true</parameters>  <!-- Spring Boot 3.2+ 参数名发现 -->
        <annotationProcessorPaths>
            <path>...lombok</path>
            <path>...lombok-mapstruct-binding</path>
            <path>...mapstruct-processor</path>
            <path>...spring-boot-configuration-processor</path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

## 3. BOM 设计（dependencies/pom.xml）

### 3.1 导入上游 BOM

```xml
<dependencyManagement>
    <dependencies>
        <!-- 1. 先导入 Spring 系 BOM -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>${spring.boot.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>${spring.cloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-alibaba-dependencies</artifactId>
            <version>${spring.cloud.alibaba.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>

        <!-- 2. 然后声明本项目内部 Starter 的版本 -->
        <dependency>
            <groupId>cn.iocoder.cloud</groupId>
            <artifactId>yudao-spring-boot-starter-web</artifactId>
            <version>${revision}</version>
        </dependency>
        <!-- ... 更多内部依赖 ... -->

        <!-- 3. 最后覆盖/补充第三方依赖版本 -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
            <version>${mybatis-plus.version}</version>
        </dependency>
        <!-- ... 更多第三方依赖 ... -->
    </dependencies>
</dependencyManagement>
```

### 3.2 版本属性集中管理

```xml
<properties>
    <!-- Spring -->
    <spring.boot.version>3.5.14</spring.boot.version>
    <spring.cloud.version>2025.0.1</spring.cloud.version>
    <spring.cloud.alibaba.version>2025.0.0.0</spring.cloud.alibaba.version>

    <!-- ORM -->
    <mybatis-plus.version>3.5.16</mybatis-plus.version>
    <druid.version>1.2.28</druid.version>
    <dynamic-datasource.version>4.5.0</dynamic-datasource.version>

    <!-- Cache -->
    <redisson.version>4.3.1</redisson.version>

    <!-- Tools -->
    <mapstruct.version>1.6.3</mapstruct.version>
    <lombok.version>1.18.46</lombok.version>
    <hutool-5.version>5.8.44</hutool-5.version>
    <hutool-6.version>6.0.0-M22</hutool-6.version>

    <!-- Docs -->
    <springdoc.version>2.8.17</springdoc.version>
    <knife4j.version>4.5.0</knife4j.version>
</properties>
```

**原则：** 每个第三方依赖都要显式声明版本，不要依赖传递。版本号必须在 BOM 中定义。

## 4. API/Server 分离模式

这是本架构最核心的设计模式。

### 4.1 概念

```
project-module-system-api     ← 对外契约（被其他模块依赖）
    ├── api/DeptApi.java          Feign 接口定义
    ├── dto/DeptRespDTO.java      跨服务 DTO
    └── enums/                     错误码、常量、枚举

project-module-system-server  ← 内部实现（不被其他模块直接依赖）
    ├── controller/admin/          REST 控制器
    ├── service/                   业务逻辑
    ├── dal/                       数据访问
    ├── convert/                   对象转换
    └── api/                       Feign 接口实现（DeptApiImpl）
```

### 4.2 依赖关系

```
Module-A-server  ──依赖──▶  Module-B-api   （只依赖 API 契约）
Module-B-server  ──实现──▶  Module-B-api   （实现 API 接口）
Gateway          ──依赖──▶  Module-*-api   （网关也需要校验 Token）
```

### 4.3 API 模块内容

**只包含接口、DTO 和常量，不包含业务逻辑：**

```java
// ApiConstants.java - 服务标识
public class ApiConstants {
    public static final String NAME = "system-server";   // 对应 spring.application.name
    public static final String PREFIX = "/rpc-api/system";
}

// DeptApi.java - Feign 接口
@FeignClient(name = ApiConstants.NAME)
public interface DeptApi {
    @GetMapping(ApiConstants.PREFIX + "/dept/get")
    CommonResult<DeptRespDTO> getDept(@RequestParam("id") Long id);

    @GetMapping(ApiConstants.PREFIX + "/dept/list")
    CommonResult<List<DeptRespDTO>> getDeptList(@RequestParam("ids") Collection<Long> ids);
}

// DeptRespDTO.java - 跨服务 DTO（只包含其他服务需要的字段）
@Data
public class DeptRespDTO {
    private Long id;
    private String name;
    private Long parentId;
    private Integer status;
}

// ErrorCodeConstants.java - 错误码（定义在本模块的 API 中）
public interface ErrorCodeConstants {
    ErrorCode DEPT_NOT_FOUND = new ErrorCode(1_002_004_002, "当前部门不存在");
    ErrorCode DEPT_NAME_DUPLICATE = new ErrorCode(1_002_004_000, "已经存在该名字的部门");
}
```

### 4.4 Server 模块实现

```java
// DeptApiImpl.java - 实现 Feign 接口
@RestController  // ← 注意：用 @RestController，不是 @FeignClient
@Validated
public class DeptApiImpl implements DeptApi {

    @Resource
    private DeptService deptService;

    @Override
    public CommonResult<DeptRespDTO> getDept(Long id) {
        DeptDO dept = deptService.getDept(id);
        return success(BeanUtils.toBean(dept, DeptRespDTO.class));
    }
}
```

## 5. 空壳容器模式（yudao-server）

### 5.1 核心理念

`yudao-server` 本身不写任何业务代码，它只是一个「壳」，通过 Maven 依赖将各业务模块组装成一个可运行的 Spring Boot 应用。

### 5.2 POM 依赖

```xml
<dependencies>
    <!-- 基础模块：默认包含 -->
    <dependency>
        <groupId>cn.iocoder.cloud</groupId>
        <artifactId>yudao-module-system-server</artifactId>
    </dependency>
    <dependency>
        <groupId>cn.iocoder.cloud</groupId>
        <artifactId>yudao-module-infra-server</artifactId>
    </dependency>

    <!-- 可选模块：按需取消注释 -->
    <!--
    <dependency>
        <groupId>cn.iocoder.cloud</groupId>
        <artifactId>yudao-module-bpm-server</artifactId>
    </dependency>
    <dependency>
        <groupId>cn.iocoder.cloud</groupId>
        <artifactId>yudao-module-pay-server</artifactId>
    </dependency>
    -->
</dependencies>
```

### 5.3 启动类

```java
@SpringBootApplication(scanBasePackages = {
    "${yudao.info.base-package}.server",   // cn.iocoder.yudao.server
    "${yudao.info.base-package}.module"    // cn.iocoder.yudao.module
})
public class YudaoServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(YudaoServerApplication.class, args);
    }
}
```

**关键设计：** 通过 `scanBasePackages` 动态扫描所有模块的包，无需显式列举。当新增模块时，只需添加 Maven 依赖即可自动发现。

### 5.4 单体 vs 微服务切换

| 模式 | 配置 | Feign 行为 |
|------|------|------------|
| 单体 | `spring.cloud.nacos.discovery.enabled: false` | Feign 接口直接调用本地 Bean（排除 `spring-cloud-starter-openfeign`） |
| 微服务 | Nacos 启用 | Feign 通过 HTTP 远程调用 |

单体模式下，`yudao-server` 的 POM 排除了 `spring-cloud-starter-openfeign`，所有 Feign 接口的实现类作为本地 Spring Bean 直接注入。

## 6. 模块命名约定

| 类型 | 命名格式 | 示例 |
|------|----------|------|
| 框架根 | `project-framework` | `yudao-framework` |
| 框架 Starter | `project-spring-boot-starter-{功能}` | `yudao-spring-boot-starter-mybatis` |
| 通用模块 | `project-common` | `yudao-common` |
| 网关 | `project-gateway` | `yudao-gateway` |
| 单体容器 | `project-server` | `yudao-server` |
| 业务 API | `project-module-{业务}-api` | `yudao-module-system-api` |
| 业务 Server | `project-module-{业务}-server` | `yudao-module-system-server` |
| BOM | `project-dependencies` | `yudao-dependencies` |

## 7. 新模块快速搭建模板

### Step 1：创建目录结构

```
project-module-order/
├── project-module-order-api/
│   ├── pom.xml
│   └── src/main/java/cn/iocoder/yudao/module/order/
│       ├── api/
│       │   └── OrderApi.java
│       ├── dto/
│       │   └── OrderRespDTO.java
│       └── enums/
│           ├── ApiConstants.java
│           └── ErrorCodeConstants.java
└── project-module-order-server/
    ├── pom.xml
    └── src/main/java/cn/iocoder/yudao/module/order/
        ├── controller/admin/order/
        │   └── vo/
        │       ├── OrderSaveReqVO.java
        │       ├── OrderRespVO.java
        │       └── OrderPageReqVO.java
        ├── service/order/
        │   ├── OrderService.java
        │   └── OrderServiceImpl.java
        ├── dal/dataobject/order/
        │   └── OrderDO.java
        ├── dal/mysql/order/
        │   └── OrderMapper.java
        ├── convert/
        │   └── OrderConvert.java
        └── api/
            └── OrderApiImpl.java
```

### Step 2：编写 API 模块 POM

```xml
<parent>
    <groupId>cn.iocoder.cloud</groupId>
    <artifactId>yudao</artifactId>
    <version>${revision}</version>
    <relativePath>../../pom.xml</relativePath>
</parent>
<artifactId>yudao-module-order-api</artifactId>
<dependencies>
    <dependency>
        <groupId>cn.iocoder.cloud</groupId>
        <artifactId>yudao-common</artifactId>
    </dependency>
</dependencies>
```

### Step 3：编写 Server 模块 POM

```xml
<parent>
    <groupId>cn.iocoder.cloud</groupId>
    <artifactId>yudao</artifactId>
    <version>${revision}</version>
    <relativePath>../../pom.xml</relativePath>
</parent>
<artifactId>yudao-module-order-server</artifactId>
<dependencies>
    <dependency>
        <groupId>cn.iocoder.cloud</groupId>
        <artifactId>yudao-module-order-api</artifactId>
    </dependency>
    <!-- 框架 Starter -->
    <dependency>
        <groupId>cn.iocoder.cloud</groupId>
        <artifactId>yudao-spring-boot-starter-mybatis</artifactId>
    </dependency>
    <dependency>
        <groupId>cn.iocoder.cloud</groupId>
        <artifactId>yudao-spring-boot-starter-security</artifactId>
    </dependency>
    <!-- 依赖其他模块的 API -->
    <dependency>
        <groupId>cn.iocoder.cloud</groupId>
        <artifactId>yudao-module-system-api</artifactId>
    </dependency>
</dependencies>
```

### Step 4：注册到 Server

在 `yudao-server/pom.xml` 中取消注释：
```xml
<dependency>
    <groupId>cn.iocoder.cloud</groupId>
    <artifactId>yudao-module-order-server</artifactId>
</dependency>
```
