# 02 - 编码规范与分层架构

## 1. 基础包结构

所有 Java 代码位于统一的基础包下：

```
cn.iocoder.yudao
├── server                         # yudao-server 启动类
├── framework                      # 框架层
│   ├── common/                    # 通用模型
│   │   ├── core/                  #   ArrayValuable 接口
│   │   ├── enums/                 #   通用枚举、RpcConstants
│   │   ├── exception/             #   ErrorCode, ServiceException
│   │   ├── pojo/                  #   CommonResult, PageParam, PageResult
│   │   ├── util/                  #   工具类（JsonUtils, BeanUtils）
│   │   └── validation/            #   自定义校验注解（@InEnum, @Mobile）
│   ├── mybatis/                   # MyBatis 增强
│   ├── web/                       # Web 层增强
│   ├── security/                  # 安全框架
│   └── ...
└── module/                        # 业务模块
    ├── system/                    #   系统管理
    │   ├── api/                   #     对外 Feign 接口
    │   ├── controller/            #     REST 控制器
    │   │   ├── admin/.../vo/      #       管理后台 + VO
    │   │   └── app/.../vo/        #       移动端 + VO
    │   ├── service/               #     业务服务
    │   ├── dal/                   #     数据访问层
    │   │   ├── dataobject/        #       DO 实体
    │   │   └── mysql/             #       Mapper 接口
    │   ├── convert/               #     MapStruct 转换器
    │   └── framework/             #     模块级框架配置
    ├── infra/                     #   基础设施
    ├── member/                    #   会员中心
    └── ...
```

## 2. 分层架构与职责

```
┌────────────────────────────────────────────────────────────┐
│  Controller 层                                              │
│  职责：接收请求、参数校验、权限检查、调用 Service、返回 VO   │
│  关键注解：@Tag @RestController @RequestMapping @Validated   │
│  返回类型：CommonResult<T>                                   │
├────────────────────────────────────────────────────────────┤
│  Service 层                                                 │
│  职责：业务逻辑、事务管理、缓存、异常抛出、操作日志           │
│  模式：Interface + Impl（面向接口编程）                      │
│  关键注解：@Service @Transactional @Cacheable @LogRecord     │
├────────────────────────────────────────────────────────────┤
│  DAO 层（Mapper + DO）                                      │
│  职责：数据访问、查询构建、批量操作                          │
│  Mapper：extends BaseMapperX<T>（增强 Mapper）               │
│  DO：extends BaseDO / TenantBaseDO（基类实体）               │
├────────────────────────────────────────────────────────────┤
│  Convert 层                                                 │
│  职责：对象转换（DO ↔ VO ↔ DTO）                            │
│  工具：Hutool BeanUtils（简单）+ MapStruct（复杂）            │
└────────────────────────────────────────────────────────────┘
```

### 每层铁的规则

1. **Controller 不能直接调用 DAO**，必须通过 Service
2. **Service 不能直接返回 DO 给 Controller**，必须先转为 VO
3. **DAO 不包含业务逻辑**，只负责数据访问
4. **DO 不暴露到模块外部**，跨模块通信必须用 DTO

## 3. 核心基类体系

### 3.1 实体基类

```java
// BaseDO — 所有数据库实体的基类
@Data
@EqualsAndHashCode
public abstract class BaseDO implements Serializable {
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private String creator;        // 创建人 ID

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updater;        // 更新人 ID

    @TableLogic                    // 逻辑删除
    private Boolean deleted;       // 0=未删除, 1=已删除
}

// TenantBaseDO — 多租户实体基类
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class TenantBaseDO extends BaseDO {
    private Long tenantId;         // 租户 ID
}
```

**选择规则：**
- 租户隔离的数据 → `extends TenantBaseDO`
- 全局共享的数据（如菜单、字典） → `extends BaseDO`

### 3.2 Mapper 基类

```java
// BaseMapperX<T> — 增强 Mapper
public interface BaseMapperX<T> extends MPJBaseMapper<T> {

    // 分页查询（自动处理 PAGE_SIZE_NONE = -1 不分页场景）
    default PageResult<T> selectPage(PageParam pageParam, Wrapper<T> queryWrapper) { ... }

    // MyBatis-Plus-Join 分页
    default <D> PageResult<D> selectJoinPage(PageParam pageParam, Class<D> clazz,
                                              MPJLambdaWrapper<T> wrapper) { ... }

    // 单字段查询快捷方法
    default T selectOne(SFunction<T, ?> field, Object value) { ... }
    default List<T> selectList(SFunction<T, ?> field, Object value) { ... }
    default Long selectCount(SFunction<T, ?> field, Object value) { ... }

    // 批量插入（SQL Server 特殊处理）
    default void insertBatch(Collection<T> entities) { ... }
}
```

### 3.3 通用响应模型

```java
@Data
public class CommonResult<T> implements Serializable {
    /** 错误码，0 表示成功 */
    private Integer code;
    /** 错误提示，成功时为空字符串 */
    private String msg;
    /** 返回数据 */
    private T data;

    // ========== 工厂方法 ==========

    public static <T> CommonResult<T> success(T data) {
        CommonResult<T> result = new CommonResult<>();
        result.code = 0;
        result.data = data;
        result.msg = "";
        return result;
    }

    public static <T> CommonResult<T> error(Integer code, String msg) { ... }
    public static <T> CommonResult<T> error(ErrorCode errorCode) { ... }

    // ========== 检查方法 ==========

    public T getCheckedData() {
        checkError();  // 如果 code != 0 则抛出 ServiceException
        return data;
    }
}
```

### 3.4 分页模型

```java
// 分页请求
@Data
public class PageParam {
    public static final Integer PAGE_SIZE_NONE = -1;  // 不分页

    private Integer pageNo = 1;
    private Integer pageSize = 10;
}

// 排序分页请求
@Data
public class SortablePageParam extends PageParam {
    private List<SortingField> sortingFields;   // [{field: "createTime", order: "desc"}]
}

// 分页结果
@Data
public class PageResult<T> {
    private Long total;
    private List<T> list;

    public static <T> PageResult<T> empty() {
        return new PageResult<>(0L, Collections.emptyList());
    }
}
```

## 4. 异常处理体系

### 4.1 异常类型

| 异常类 | 说明 | 使用场景 |
|--------|------|----------|
| `ServiceException` | 业务异常（code=1） | 用户可见的业务错误 |
| `ServerException` | 系统异常（code=2） | 系统内部错误 |
| `ErrorCode` | 错误码对象 | 组合 `Integer code` + `String msg` |

### 4.2 错误码定义

```java
// 全局错误码（0-999）
public interface GlobalErrorCodeConstants {
    ErrorCode SUCCESS = new ErrorCode(0, "成功");
    ErrorCode BAD_REQUEST = new ErrorCode(400, "请求参数不正确");
    ErrorCode UNAUTHORIZED = new ErrorCode(401, "账号未登录");
    ErrorCode FORBIDDEN = new ErrorCode(403, "没有该操作权限");
    ErrorCode NOT_FOUND = new ErrorCode(404, "请求未找到");
    ErrorCode INTERNAL_SERVER_ERROR = new ErrorCode(500, "系统异常");
    ErrorCode DEMO_DENY = new ErrorCode(901, "演示模式下不允许操作");
}

// 模块业务错误码（从 1_000_000_000 开始，每个模块分配区间）
// 规则：{系统编号}-{模块编号}-{错误序号}
// 示例：1-002-004-002 = 系统管理-部门-部门不存在
public interface ErrorCodeConstants {
    ErrorCode DEPT_NOT_FOUND = new ErrorCode(1_002_004_002, "当前部门不存在");
    ErrorCode DEPT_NAME_DUPLICATE = new ErrorCode(1_002_004_000, "已经存在该名字的部门");
}
```

### 4.3 使用方式

```java
// 在 Service 中抛出
throw exception(DEPT_NOT_FOUND);                              // 不带参数
throw exception(ROLE_NAME_DUPLICATE, roleName);               // 带格式化参数
throw invalidParamException("父级部门不能是自己");              // 通用参数错误

// 使用静态导入
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
```

### 4.4 全局异常处理

所有异常由 `GlobalExceptionHandler` (`@RestControllerAdvice`) 统一处理：

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public CommonResult<?> serviceExceptionHandler(ServiceException ex) {
        return CommonResult.error(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResult<?> methodArgumentNotValidExceptionHandler(...) {
        // 提取第一个校验失败的字段错误
        return CommonResult.error(BAD_REQUEST.getCode(), message);
    }

    @ExceptionHandler(Exception.class)
    public CommonResult<?> defaultExceptionHandler(HttpServletRequest req, Exception ex) {
        log.error("[defaultExceptionHandler]", ex);
        return CommonResult.error(INTERNAL_SERVER_ERROR.getCode(),
                                   INTERNAL_SERVER_ERROR.getMsg());
    }
}
```

## 5. 命名约定总结

| 元素 | 命名格式 | 示例 |
|------|----------|------|
| Controller | `{Entity}Controller` | `DeptController` |
| Service 接口 | `{Entity}Service` | `DeptService` |
| Service 实现 | `{Entity}ServiceImpl` | `DeptServiceImpl` |
| Mapper | `{Entity}Mapper` | `DeptMapper` |
| DO 实体 | `{Entity}DO` | `DeptDO` |
| 新增/修改 VO | `{Entity}SaveReqVO` | `DeptSaveReqVO` |
| 响应 VO | `{Entity}RespVO` | `DeptRespVO` |
| 列表查询 VO | `{Entity}ListReqVO` | `DeptListReqVO` |
| 分页查询 VO | `{Entity}PageReqVO` | `RolePageReqVO` |
| 精简 VO | `{Entity}SimpleRespVO` | `DeptSimpleRespVO` |
| Feign 接口 | `{Entity}Api` | `DeptApi` |
| Feign 实现 | `{Entity}ApiImpl` | `DeptApiImpl` |
| Feign DTO | `{Entity}RespDTO` | `DeptRespDTO` |
| 转换器 | `{Entity}Convert` | `UserConvert` |
| 字符串常量 | `UPPER_SNAKE` | `DEPT_CHILDREN_ID_LIST` |
| 错误码常量 | `UPPER_SNAKE` | `DEPT_NOT_FOUND` |

## 6. 关键注解速查

| 注解 | 位置 | 作用 |
|------|------|------|
| `@Tag(name = "管理后台 - 部门")` | Controller | Knife4j 接口分组 |
| `@Operation(summary = "创建部门")` | Controller 方法 | API 文档描述 |
| `@Validated` | Controller 类 | 开启方法级别校验 |
| `@Valid` | Controller 方法参数 | 校验 RequestBody |
| `@PreAuthorize("@ss.hasPermission('system:dept:create')")` | Controller 方法 | 权限控制 |
| `@PermitAll` | Controller 方法 | 免登录访问 |
| `@Transactional(rollbackFor = Exception.class)` | Service 方法 | 事务 |
| `@Cacheable(value = KEY, key = "#id", unless = "#result == null")` | Service 方法 | 缓存读取 |
| `@CacheEvict(value = KEY, key = "#id")` | Service 方法 | 缓存清除 |
| `@LogRecord(type = "SYSTEM", subType = "CREATE", success = "...")` | Service 方法 | 操作日志 |
| `@ApiAccessLog(operateType = EXPORT)` | Controller 方法 | 访问日志/导出 |
| `@DataPermission(enable = false)` | Controller 方法 | 禁用数据权限 |
| `@TableName("system_dept")` | DO | 表名映射 |
| `@TableId` | DO 字段 | 主键 |
| `@TableLogic` | DO 字段 | 逻辑删除标记 |
| `@TableField(fill = FieldFill.INSERT)` | DO 字段 | 自动填充 |
| `@KeySequence("system_dept_seq")` | DO | Oracle/PG 序列名 |
