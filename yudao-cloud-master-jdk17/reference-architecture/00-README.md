# 芋道云平台 (Yudao Cloud) 架构参考手册

> 从芋道云平台项目中提取的 Spring Cloud Alibaba 微服务快速开发架构模式与代码规范。
> 适用于 JDK 17 + Spring Boot 3.x + Spring Cloud 2025.x 技术栈。

## 文档导航

| 文档 | 内容 | 适用场景 |
|------|------|----------|
| [01-项目结构与模块设计](01-Project-Structure.md) | Maven 多模块、BOM 版本管理、api/server 分离、单体/微服务双模式 | 新项目搭建、模块拆分 |
| [02-编码规范与分层架构](02-Coding-Conventions.md) | 包结构、命名约定、基类体系、分层职责 | 日常开发、代码审查 |
| [03-Controller 层模式](03-Controller-Layer.md) | REST API 设计、VO 设计、参数校验、权限注解、响应格式 | 编写 API 接口 |
| [04-Service 层模式](04-Service-Layer.md) | 服务接口/实现、事务管理、缓存注解、异常处理、操作日志 | 编写业务逻辑 |
| [05-DAO 层模式](05-DAO-Layer.md) | MyBatis-Plus 增强、查询包装器、DO 设计、字段自动填充 | 数据库访问 |
| [06-Feign-RPC 模式](06-Feign-RPC-Patterns.md) | Feign 接口定义、DTO 设计、RPC 实现、跨服务数据翻译 | 微服务间通信 |
| [07-Framework 设计](07-Framework-Design.md) | Spring Boot Starter 设计、自动配置、配置属性、扩展点 | 编写框架组件 |
| [08-测试模式](08-Testing-Patterns.md) | 测试基类、Mock 策略、测试数据生成、断言工具 | 编写单元测试 |
| [09-配置与 DevOps](09-Configuration-DevOps.md) | 多环境配置、Profile 管理、CI/CD、Docker | 部署运维 |

## 核心技术栈

| 层次 | 技术 | 版本 |
|------|------|------|
| 语言 | Java | 17 |
| 框架 | Spring Boot | 3.5.14 |
| 微服务 | Spring Cloud + Alibaba | 2025.0.1 / 2025.0.0.0 |
| ORM | MyBatis Plus + MyBatis Plus Join | 3.5.16 |
| 连接池 | Druid | 1.2.28 |
| 多数据源 | Dynamic Datasource | 4.5.0 |
| 缓存 | Redis + Redisson | 4.3.1 |
| 安全 | Spring Security + OAuth2 | — |
| API 文档 | SpringDoc + Knife4j | 2.8.17 / 4.5.0 |
| 对象转换 | MapStruct | 1.6.3 |
| 工具库 | Hutool | 5.8.44 / 6.0.0-M22 |
| 工作流 | Flowable | 7.2.0 |
| 分布式锁 | Lock4j | 2.2.7 |
| 任务调度 | XXL-Job | 2.4.0 |

## 核心设计理念

### 1. 空壳容器模式
`yudao-server` 本身不包含业务代码，仅通过 Maven 依赖引入各业务模块。单体部署时所有模块在同一个 JVM 中运行，微服务部署时每个模块独立部署。

### 2. API/Server 分离
每个业务模块分为 `-api`（Feign 接口 + DTO）和 `-server`（控制器 + 服务 + DAO）两个子模块。其他模块只需依赖 `-api` 即可调用该模块的服务。

### 3. BOM 驱动版本管理
所有依赖版本集中在 `yudao-dependencies` BOM 中管理，是版本号的唯一真相来源。各模块 POM 只声明依赖，不指定版本。

### 4. 框架即 Starter
通用技术能力封装为 `yudao-spring-boot-starter-*` 模块，通过 Spring Boot 3.x 的 `AutoConfiguration.imports` 机制自动装配，开箱即用。

### 5. 约定优于配置
- 包路径决定 API 前缀（`**.controller.admin.**` → `/admin-api`）
- 实体继承决定多租户行为（`TenantBaseDO` vs `BaseDO`）
- 接口继承决定 CRUD 能力（`BaseMapperX<T>`）

## 快速上手

如果你想基于这套架构开发新项目，推荐按以下顺序阅读：

1. **搭建项目骨架** → [01-项目结构与模块设计](01-Project-Structure.md)
2. **理解分层规范** → [02-编码规范与分层架构](02-Coding-Conventions.md)
3. **编写第一个 CRUD** → [03-Controller](03-Controller-Layer.md) → [04-Service](04-Service-Layer.md) → [05-DAO](05-DAO-Layer.md)
4. **添加微服务调用** → [06-Feign-RPC 模式](06-Feign-RPC-Patterns.md)
5. **编写测试** → [08-测试模式](08-Testing-Patterns.md)
6. **自定义框架组件** → [07-Framework 设计](07-Framework-Design.md)
