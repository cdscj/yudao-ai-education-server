# 06 - Feign RPC 模式

## 1. 整体架构

```
Module A (调用方)                           Module B (提供方)
┌──────────────────┐                      ┌──────────────────────┐
│  ServiceImpl     │                      │  DeptApiImpl         │
│    │             │     Feign 调用        │    │                 │
│    ├── DeptApi ──┼─────────────────────▶│    ├── implements    │
│    │  (接口)     │                      │    │     DeptApi      │
│    │             │                      │    │                 │
│    │  引用 -api  │                      │    └── DeptService   │
│    │  模块依赖   │                      │                      │
└──────────────────┘                      └──────────────────────┘

DeptApi 定义在 yudao-module-system-api 中  ← 双方共享的契约
DeptApiImpl 实现在 yudao-module-system-server 中  ← 只有提供方有实现
```

**核心原则：** API 模块是服务契约，不包含业务逻辑。Server 模块是唯一实现。调用方只依赖 API 模块。

## 2. API 模块设计

### 2.1 模块依赖

```xml
<!-- yudao-module-system-api/pom.xml -->
<parent>yudao</parent>
<artifactId>yudao-module-system-api</artifactId>

<dependencies>
    <!-- 只依赖 common，不依赖任何 server 模块 -->
    <dependency>
        <groupId>cn.iocoder.cloud</groupId>
        <artifactId>yudao-common</artifactId>
    </dependency>

    <!-- 如果需要引用其他模块的 DTO -->
    <dependency>
        <groupId>cn.iocoder.cloud</groupId>
        <artifactId>yudao-module-infra-api</artifactId>
    </dependency>
</dependencies>
```

### 2.2 服务常量定义

```java
// cn.iocoder.yudao.module.system.enums.ApiConstants
public class ApiConstants {
    /** 服务名 — 必须与 spring.application.name 一致 */
    public static final String NAME = "system-server";

    /** RPC API 前缀 */
    public static final String PREFIX = RpcConstants.RPC_API_PREFIX + "/system";   // "/rpc-api/system"

    /** 版本号 */
    public static final String VERSION = "1.0.0";
}

// 全局 RPC 常量（在 yudao-common 中）
public interface RpcConstants {
    String RPC_API_PREFIX = "/rpc-api";

    // ========== 服务名 ==========
    String SYSTEM_NAME = "system-server";
    String INFRA_NAME = "infra-server";
    String BPM_NAME = "bpm-server";
    // ...
}
```

### 2.3 Feign 接口定义

```java
// cn.iocoder.yudao.module.system.api.dept.DeptApi
@FeignClient(name = ApiConstants.NAME)                    // 指向目标服务
@Tag(name = "RPC 服务 - 部门")
public interface DeptApi {

    String PREFIX = ApiConstants.PREFIX + "/dept";        // "/rpc-api/system/dept"

    // ========== 单个查询 ==========

    @GetMapping(PREFIX + "/get")
    @Operation(summary = "获得部门信息")
    CommonResult<DeptRespDTO> getDept(@RequestParam("id") Long id);

    // ========== 批量查询 ==========

    @GetMapping(PREFIX + "/list")
    @Operation(summary = "获得部门列表")
    CommonResult<List<DeptRespDTO>> getDeptList(@RequestParam("ids") Collection<Long> ids);

    // ========== 校验 ==========

    @GetMapping(PREFIX + "/valid")
    @Operation(summary = "校验部门是否有效")
    CommonResult<Boolean> validateDeptList(@RequestParam("ids") Collection<Long> ids);

    // ========== 默认方法 —— 调用方不需要关注 RPC 细节 ==========

    /**
     * 获取部门 Map（key: 部门 ID, value: 部门名称）
     * 这是一个 default 方法，内部调用 Feign 接口
     */
    default Map<Long, DeptRespDTO> getDeptMap(Collection<Long> ids) {
        List<DeptRespDTO> list = getDeptList(ids).getCheckedData();
        return CollectionUtils.convertMap(list, DeptRespDTO::getId);
    }
}
```

**设计要点：**
1. 所有方法返回 `CommonResult<T>`，调用方用 `getCheckedData()` 解包
2. 参数简单类型用 `@RequestParam`，复杂对象用 `@RequestBody`
3. `default` 方法提供便捷操作，封装 RPC 调用细节
4. 路径前缀统一在 `ApiConstants.PREFIX` 中管理

### 2.4 跨服务 DTO

```java
// DeptRespDTO.java — 跨服务 DTO
@Data
public class DeptRespDTO {
    private Long id;
    private String name;
    private Long parentId;
    private Integer status;
    // 只包含其他服务需要的字段，不暴露内部实现细节
}

// AdminUserRespDTO.java — 支持 Easy-Trans 的 DTO
@Data
public class AdminUserRespDTO implements VO {   // 实现 Easy-Trans 的 VO 接口
    private Long id;
    private String nickname;

    // Easy-Trans：被其他模块引用时的自动翻译标记
    @AutoTrans(namespace = ApiConstants.PREFIX + "/user", fields = {"nickname"})
    public interface AutoTransAdminUserRespDTO {
        Long getId();
        String getNickname();
    }
}
```

## 3. Server 模块实现（Feign 接口实现）

```java
// DeptApiImpl.java — 在 server 模块中实现
@RestController    // ← 用 @RestController，不是 @FeignClient
@Validated
public class DeptApiImpl implements DeptApi {

    @Resource
    private DeptService deptService;

    @Override
    public CommonResult<DeptRespDTO> getDept(Long id) {
        DeptDO dept = deptService.getDept(id);
        return success(BeanUtils.toBean(dept, DeptRespDTO.class));
    }

    @Override
    public CommonResult<List<DeptRespDTO>> getDeptList(Collection<Long> ids) {
        List<DeptDO> list = deptService.getDeptList(ids);
        return success(BeanUtils.toBean(list, DeptRespDTO.class));
    }

    @Override
    public CommonResult<Boolean> validateDeptList(Collection<Long> ids) {
        deptService.validateDeptList(ids);
        return success(true);
    }
}
```

**关键点：**
- 实现类用 `@RestController` 注解
- 实现类放在 server 模块的 `api/` 包下
- 不包含业务逻辑，只做 DO→DTO 转换后委托给 Service

## 4. 调用模式

### 4.1 在 Service 中调用

```java
@Service
public class UserServiceImpl implements UserService {

    // 直接注入 Feign 接口（Spring Cloud 自动生成代理）
    @Resource
    private DeptApi deptApi;

    // 或者注入 PermissionApi
    @Resource
    private PermissionApi permissionApi;

    public UserRespVO getUserDetail(Long userId) {
        AdminUserDO user = userMapper.selectById(userId);

        // 通过 RPC 获取部门信息
        DeptRespDTO dept = deptApi.getDept(user.getDeptId()).getCheckedData();

        // 通过 RPC 获取权限
        Set<String> permissions = permissionApi.getUserPermissions(userId)
                                                .getCheckedData();

        // 组装响应
        return UserConvert.INSTANCE.convert(user, dept, permissions);
    }
}
```

### 4.2 调用约定

```java
// 1. 始终调用 getCheckedData() 来解包
DeptRespDTO dept = deptApi.getDept(id).getCheckedData();

// 2. null 安全调用
DeptRespDTO dept = deptApi.getDept(id);      // 可能返回 null
if (dept != null) {
    dept = dept.getCheckedData();            // 如果 Feign 调用失败，这里抛异常
}

// 3. 批量调用优化（使用 default 方法）
Map<Long, DeptRespDTO> deptMap = deptApi.getDeptMap(deptIds);
```

## 5. Feign 调用上下文传递

### 5.1 LoginUser 传递

框架通过 `LoginUserRequestInterceptor` 自动将当前用户信息传递给下游服务：

```java
// yudao-framework/yudao-spring-boot-starter-security/.../core/rpc/LoginUserRequestInterceptor.java
public class LoginUserRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        // 将当前登录用户序列化到请求头
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser != null) {
            template.header(SecurityProperties.LOGIN_USER_HEADER, encode(loginUser));
        }
    }
}
```

下游服务的 `TokenAuthenticationFilter` 从 `login-user` 请求头反序列化出 `LoginUser`，无需再次认证。

### 5.2 租户上下文传递

类似地，`TenantRequestInterceptor` 传递 `tenant-id` 请求头。

## 6. 单体模式下的行为

在单体模式下（`yudao-server` 本地开发默认模式）：

1. `yudao-server/pom.xml` 排除了 `spring-cloud-starter-openfeign`
2. `FeignAutoConfiguration` 被排除
3. Feign 接口的 `@FeignClient` 注解被忽略
4. `DeptApiImpl` 作为本地 Spring Bean 被直接注入
5. **所有调用变成进程内调用，无网络开销**

```xml
<!-- yudao-server/pom.xml 中的排除 -->
<dependency>
    <groupId>cn.iocoder.cloud</groupId>
    <artifactId>yudao-spring-boot-starter-rpc</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

## 7. Easy-Trans 跨服务数据翻译

当 A 模块的 VO 需要显示 B 模块的数据时（如订单列表显示用户昵称），使用 Easy-Trans：

```java
// ========== 在 system-api 中定义 ==========

@Data
public class AdminUserRespDTO implements VO {
    private Long id;
    private String nickname;
}

// AdminUserApi.java — 提供翻译数据源
@FeignTransfer(type = AdminUserRespDTO.class)
public interface AdminUserApi {
    @GetMapping(PREFIX + "/get")
    CommonResult<AdminUserRespDTO> getUser(@RequestParam("id") Long id);

    @GetMapping(PREFIX + "/list")
    CommonResult<List<AdminUserRespDTO>> getUsers(@RequestParam("ids") List<Long> ids);
}

// ========== 在 trade-server 中使用 ==========

@Data
public class OrderRespVO {
    private Long id;
    private String orderNo;

    @Trans(type = AdminUserRespDTO.class, fields = "nickname")
    private Long userId;

    // Easy-Trans 自动将 userId 翻译为 nickname 并设置到此字段
    private String userNickname;
}
```

## 8. 错误码跨模块共享

错误码定义在 API 模块中（因为错误码是契约的一部分）：

```java
// yudao-module-system-api/.../enums/ErrorCodeConstants.java
public interface ErrorCodeConstants {
    // 系统管理模块：1-002-xxx-xxx
    // ├── AUTH 模块：1-002-000-xxx
    // ├── 菜单模块：1-002-001-xxx
    // ├── 角色模块：1-002-002-xxx
    // ├── 用户模块：1-002-003-xxx
    // └── 部门模块：1-002-004-xxx

    ErrorCode DEPT_NOT_FOUND = new ErrorCode(1_002_004_002, "当前部门不存在");
    ErrorCode DEPT_NAME_DUPLICATE = new ErrorCode(1_002_004_000, "已经存在该名字的部门");
    ErrorCode DEPT_PARENT_ERROR = new ErrorCode(1_002_004_003, "不能设置自己为父部门");
}
```

调用方模块可以直接引用和检查错误码：

```java
// 在其他模块中
try {
    deptApi.getDept(deptId);
} catch (ServiceException e) {
    if (ErrorCodeConstants.DEPT_NOT_FOUND.getCode().equals(e.getCode())) {
        // 处理部门不存在的情况
    }
}
```

## 9. CommonApi 模式（框架级 RPC）

框架自身也暴露 RPC 接口供业务模块使用：

```java
// yudao-common/.../common/api/ApiAccessLogCommonApi.java
@FeignClient(name = RpcConstants.INFRA_NAME)
public interface ApiAccessLogCommonApi {
    @PostMapping(RpcConstants.INFRA_PREFIX + "/api-access-log/create")
    CommonResult<Boolean> createApiAccessLog(@Valid @RequestBody ApiAccessLogCreateReqDTO dto);
}

// 业务模块直接注入使用
@Resource
private ApiAccessLogCommonApi apiAccessLogCommonApi;
```
