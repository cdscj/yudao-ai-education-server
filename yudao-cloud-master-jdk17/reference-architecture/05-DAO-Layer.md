# 05 - DAO 层模式

## 1. DO 实体设计

### 1.1 标准 DO

```java
@TableName("system_dept")
@KeySequence("system_dept_seq")       // Oracle/PostgreSQL 序列名
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DeptDO extends TenantBaseDO {

    public static final Long PARENT_ID_ROOT = 0L;   // 业务常量

    @TableId
    private Long id;

    @TableField("name")                // 字段名（可不写，默认驼峰转下划线）
    private String name;

    private Long parentId;
    private Integer sort;
    private Long leaderUserId;
    private String phone;
    private String email;
    private Integer status;            // CommonStatusEnum: 0=开启, 1=关闭
}
```

### 1.2 包含 JSON 列的 DO

```java
@TableName(value = "system_role", autoResultMap = true)  // ← 必须加 autoResultMap
@KeySequence("system_role_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RoleDO extends TenantBaseDO {

    @TableId
    private Long id;
    private String name;
    private String code;
    private Integer sort;
    private Integer status;
    private String remark;

    @TableField(typeHandler = JacksonTypeHandler.class)  // ← JSON 列
    private Set<Long> dataScopeDeptIds;
}
```

### 1.3 使用 Builder 模式的 DO

```java
@TableName("system_users")
@KeySequence("system_users_seq")
@Data
@Builder                                    // ← 可选
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AdminUserDO extends TenantBaseDO {

    @TableId
    private Long id;
    private String username;
    private String password;                // BCrypt 加密
    private String nickname;
    private String email;
    private String mobile;
    private Integer sex;
    private String avatar;
    private Integer status;
    private String remark;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Set<Long> postIds;              // JSON 列
}
```

### 1.4 基类字段自动填充

框架通过 `DefaultDBFieldHandler` 实现自动填充，无需在每个 DO 中处理：

```java
@Component
public class DefaultDBFieldHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        // 自动填充 createTime, updateTime, creator, updater
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "creator", String.class, getLoginUserId());
        this.strictInsertFill(metaObject, "updater", String.class, getLoginUserId());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.strictUpdateFill(metaObject, "updater", String.class, getLoginUserId());
    }
}
```

**所以 DO 中不需要手动设置 `createTime`、`updateTime`、`creator`、`updater`、`deleted` 字段。**

## 2. Mapper 设计

### 2.1 标准 Mapper

```java
@Mapper
public interface DeptMapper extends BaseMapperX<DeptDO> {

    // ========== 条件查询（使用 LambdaQueryWrapperX 的 xxxIfPresent 方法） ==========

    default List<DeptDO> selectList(DeptListReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<DeptDO>()
                .likeIfPresent(DeptDO::getName, reqVO.getName())
                .eqIfPresent(DeptDO::getStatus, reqVO.getStatus()));
    }

    // ========== 单字段快捷查询 ==========

    default DeptDO selectByParentIdAndName(Long parentId, String name) {
        return selectOne(DeptDO::getParentId, parentId, DeptDO::getName, name);
    }

    default Long selectCountByParentId(Long parentId) {
        return selectCount(DeptDO::getParentId, parentId);
    }
}
```

### 2.2 分页查询 Mapper

```java
@Mapper
public interface RoleMapper extends BaseMapperX<RoleDO> {

    default PageResult<RoleDO> selectPage(RolePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<RoleDO>()
                .likeIfPresent(RoleDO::getName, reqVO.getName())
                .likeIfPresent(RoleDO::getCode, reqVO.getCode())
                .eqIfPresent(RoleDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(BaseDO::getCreateTime, reqVO.getCreateTime())
                .orderByAsc(RoleDO::getSort));
    }

    default RoleDO selectByName(String name) {
        return selectOne(RoleDO::getName, name);
    }

    default RoleDO selectByCode(String code) {
        return selectOne(RoleDO::getCode, code);
    }
}
```

### 2.3 Join 查询 Mapper

```java
@Mapper
public interface UserMapper extends BaseMapperX<AdminUserDO> {

    default PageResult<AdminUserDO> selectPage(UserPageReqVO reqVO, Collection<Long> deptIds) {
        // 使用 MPJLambdaWrapperX 进行多表关联
        MPJLambdaWrapperX<AdminUserDO> query = new MPJLambdaWrapperX<AdminUserDO>()
                .likeIfPresent(AdminUserDO::getUsername, reqVO.getUsername())
                .likeIfPresent(AdminUserDO::getMobile, reqVO.getMobile())
                .eqIfPresent(AdminUserDO::getStatus, reqVO.getStatus())
                .inIfPresent(AdminUserDO::getDeptId, deptIds)      // ← 外部计算的条件
                .betweenIfPresent(AdminUserDO::getCreateTime, reqVO.getCreateTime());

        // leftJoin 关联部门表
        if (StrUtil.isNotBlank(reqVO.getDeptName())) {
            query.leftJoin(DeptDO.class, DeptDO::getId, AdminUserDO::getDeptId)
                 .like(DeptDO::getName, reqVO.getDeptName());
        }

        return selectJoinPage(reqVO, AdminUserDO.class, query);
    }
}
```

## 3. QueryWrapper 增强体系

### 3.1 LambdaQueryWrapperX（单表查询）

```java
// 核心特性：xxxIfPresent 方法 — 值非空才添加条件
new LambdaQueryWrapperX<DeptDO>()
    .likeIfPresent(DeptDO::getName, name)           // name != null && name != "" 才生效
    .eqIfPresent(DeptDO::getStatus, status)          // status != null 才生效
    .inIfPresent(DeptDO::getId, ids)                // ids != null && !ids.isEmpty()
    .betweenIfPresent(BaseDO::getCreateTime, times)  // times != null && times.length == 2
    .orderByAsc(DeptDO::getSort)                    // 排序（总是生效）
    .orderByDesc(DeptDO::getId);
```

**所有 `xxxIfPresent` 方法列表：**
| 方法 | 触发条件 |
|------|----------|
| `likeIfPresent` | `StrUtil.isNotBlank(value)` |
| `eqIfPresent` | `value != null` |
| `neIfPresent` | `value != null` |
| `gtIfPresent` / `geIfPresent` / `ltIfPresent` / `leIfPresent` | `value != null` |
| `betweenIfPresent` | `values != null && values.length == 2` |
| `inIfPresent` | `CollUtil.isNotEmpty(values)` |

### 3.2 MPJLambdaWrapperX（多表 Join 查询）

```java
new MPJLambdaWrapperX<UserDO>()
    // 单表条件（同 LambdaQueryWrapperX）
    .eqIfPresent(UserDO::getStatus, reqVO.getStatus())
    // Join 支持
    .leftJoin(DeptDO.class, DeptDO::getId, UserDO::getDeptId)
    .leftJoin(RoleDO.class, RoleDO::getId, UserRoleDO::getRoleId,
              wrapper -> wrapper.eq(RoleDO::getStatus, CommonStatusEnum.ENABLE.getStatus()))
    // Join 后的条件
    .like(DeptDO::getName, reqVO.getDeptName())
    // 关联查询字段
    .selectAll(UserDO.class)
    .selectAs(DeptDO::getName, UserRespVO::getDeptName);
```

### 3.3 传统 QueryWrapperX（字符串列名）

```java
// 当无法使用 Lambda 表达式时使用
new QueryWrapperX<DeptDO>()
    .eqIfPresent("name", name)
    .likeIfPresent("description", desc);
```

## 4. BaseMapperX 提供的方法

```java
public interface BaseMapperX<T> extends MPJBaseMapper<T> {

    // ========== 分页 ==========
    PageResult<T> selectPage(PageParam pageParam, Wrapper<T> queryWrapper);
    <D> PageResult<D> selectJoinPage(PageParam pageParam, Class<D> resultClass,
                                      MPJLambdaWrapper<T> wrapper);

    // ========== 单结果查询 ==========
    T selectOne(SFunction<T, ?> field, Object value);
    T selectOne(SFunction<T, ?> field1, Object value1,
                SFunction<T, ?> field2, Object value2);     // 两条件版本
    T selectFirstOne(SFunction<T, ?> field, Object value);  // 取第一条（防止多结果报错）

    // ========== 列表查询 ==========
    List<T> selectList(SFunction<T, ?> field, Object value);
    List<T> selectList();

    // ========== 计数 ==========
    Long selectCount(SFunction<T, ?> field, Object value);

    // ========== 批量操作 ==========
    void insertBatch(Collection<T> entities);          // SQL Server 特殊处理
    void updateBatch(Collection<T> entities);
    void deleteBatch(Collection<Long> ids);            // 物理删除
    void deleteById(Long id);                          // 逻辑删除
}
```

## 5. 自动 ID 类型检测

框架通过 `IdTypeEnvironmentPostProcessor` 自动检测数据库类型并设置主键策略：

| 数据库 | ID 策略 | 说明 |
|--------|---------|------|
| Oracle, PostgreSQL, Kingbase, DB2, H2 | `INPUT` | 使用序列，需配合 `@KeySequence` |
| MySQL, DM（达梦） | `AUTO` | 自增主键 |

### 配置示例

```yaml
mybatis-plus:
  global-config:
    db-config:
      id-type: NONE              # 设置为 NONE，由框架自动检测
      logic-delete-field: deleted
      logic-delete-value: 1      # 已删除
      logic-not-delete-value: 0  # 未删除
```

## 6. 类型处理器

### JSON 列类型处理器

```java
// DO 中使用
@TableField(typeHandler = JacksonTypeHandler.class)
private Set<Long> postIds;              // Java Set ↔ MySQL JSON
```

### 加密字段类型处理器

```java
// DO 中使用
@TableField(typeHandler = EncryptTypeHandler.class)
private String secretKey;               // 数据库存储加密值，Java 中自动解密
```

### 集合类型处理器

```java
@TableField(typeHandler = LongListTypeHandler.class)
private List<Long> tagIds;              // [1,2,3] ↔ "1,2,3"

@TableField(typeHandler = StringListTypeHandler.class)
private List<String> tags;              // ["a","b"] ↔ "a,b"
```

## 7. 逻辑删除

项目中所有表都使用逻辑删除，配置如下：

```yaml
mybatis-plus:
  global-config:
    db-config:
      logic-delete-field: deleted   # 逻辑删除字段名
      logic-delete-value: 1         # 已删除 = 1
      logic-not-delete-value: 0     # 未删除 = 0
```

在 `BaseDO` 中：

```java
@TableLogic
private Boolean deleted;
```

**效果：**
- `mapper.deleteById(id)` 实际执行 `UPDATE ... SET deleted = 1 WHERE id = ?`
- `mapper.selectList(...)` 自动添加 `WHERE deleted = 0`

## 8. 多数据源配置

本项目使用 Dynamic Datasource：

```yaml
spring:
  datasource:
    dynamic:
      primary: master
      datasource:
        master:
          url: jdbc:mysql://127.0.0.1:3306/ruoyi-vue-pro
          username: root
          password: ${DB_PASSWORD:root}
        slave:
          url: jdbc:mysql://127.0.0.1:3306/ruoyi-vue-pro-slave
          username: root
          password: ${DB_PASSWORD:root}
          lazy: true              # 从库懒加载
```

切换数据源：

```java
@DS("slave")                       // 方法级切换
public List<UserDO> selectList() { ... }
```

## 9. MyBatis Plus Join 配置

```java
// 开启 MyBatis Plus Join（全局配置在 YudaoMybatisAutoConfiguration 中）
// 默认已配置，无需额外操作

// Mapper 继承 BaseMapperX<T>（已 extends MPJBaseMapper<T>）
// 可直接使用 selectJoinPage, selectJoinList 等方法
```
