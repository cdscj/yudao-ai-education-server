# 04 - Service 层模式

## 1. Service 标准模板（接口 + 实现）

### 1.1 Service 接口

```java
public interface DeptService {

    /** 创建部门 */
    Long createDept(DeptSaveReqVO createReqVO);

    /** 更新部门 */
    void updateDept(DeptSaveReqVO updateReqVO);

    /** 删除部门 */
    void deleteDept(Long id);

    /** 获得部门信息 */
    DeptDO getDept(Long id);

    /** 获得部门列表 */
    List<DeptDO> getDeptList(DeptListReqVO reqVO);

    /** 获得子部门列表 */
    List<DeptDO> getChildDeptList(Long parentId);

    /** 校验部门是否存在 */
    void validateDeptList(Collection<Long> ids);
}
```

### 1.2 Service 实现

```java
@Service
@Validated
@Slf4j
public class DeptServiceImpl implements DeptService {

    @Resource
    private DeptMapper deptMapper;

    // ========== 创建 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = RedisKeyConstants.DEPT_CHILDREN_ID_LIST, allEntries = true)
    public Long createDept(DeptSaveReqVO createReqVO) {
        // 1. 校验业务规则
        validateParentDept(null, createReqVO.getParentId());
        validateDeptNameUnique(null, createReqVO.getParentId(), createReqVO.getName());

        // 2. 插入数据
        DeptDO dept = BeanUtils.toBean(createReqVO, DeptDO.class);
        deptMapper.insert(dept);
        return dept.getId();
    }

    // ========== 更新 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = RedisKeyConstants.DEPT_CHILDREN_ID_LIST, allEntries = true)
    public void updateDept(DeptSaveReqVO updateReqVO) {
        // 1. 校验存在性
        DeptDO oldDept = validateDeptExists(updateReqVO.getId());
        // 2. 校验业务规则
        validateParentDept(updateReqVO.getId(), updateReqVO.getParentId());
        validateDeptNameUnique(updateReqVO.getId(), updateReqVO.getParentId(), updateReqVO.getName());
        // 3. 更新
        DeptDO updateObj = BeanUtils.toBean(updateReqVO, DeptDO.class);
        deptMapper.updateById(updateObj);
    }

    // ========== 删除 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = RedisKeyConstants.DEPT_CHILDREN_ID_LIST, allEntries = true)
    public void deleteDept(Long id) {
        // 1. 校验存在性
        validateDeptExists(id);
        // 2. 校验是否有关联数据（子部门、用户）
        if (deptMapper.selectCountByParentId(id) > 0) {
            throw exception(DEPT_EXITS_CHILDREN);
        }
        // 3. 删除
        deptMapper.deleteById(id);
    }

    // ========== 查询 ==========

    @Override
    public DeptDO getDept(Long id) {
        return validateDeptExists(id);
    }

    @Override
    public List<DeptDO> getDeptList(DeptListReqVO reqVO) {
        return deptMapper.selectList(reqVO);
    }

    @Override
    public List<DeptDO> getChildDeptList(Long parentId) {
        return deptMapper.selectListByParentId(parentId);
    }

    // ========== 业务校验方法 ==========

    /**
     * 校验部门是否存在
     * @return 存在的部门对象
     */
    @VisibleForTesting
    DeptDO validateDeptExists(Long id) {
        if (id == null) { return null; }
        DeptDO dept = deptMapper.selectById(id);
        if (dept == null) {
            throw exception(DEPT_NOT_FOUND);
        }
        return dept;
    }

    /**
     * 校验父部门是否合法
     */
    @VisibleForTesting
    void validateParentDept(Long id, Long parentId) {
        if (parentId == null || DeptDO.PARENT_ID_ROOT.equals(parentId)) {
            return;  // 根部门，不需要校验
        }
        // 1. 不能设置自己为父部门
        if (Objects.equals(id, parentId)) {
            throw exception(DEPT_PARENT_ERROR);
        }
        // 2. 父部门必须存在
        DeptDO parentDept = deptMapper.selectById(parentId);
        if (parentDept == null) {
            throw exception(DEPT_PARENT_NOT_EXITS);
        }
        // 3. 父部门必须为启用状态
        if (!CommonStatusEnum.ENABLE.getStatus().equals(parentDept.getStatus())) {
            throw exception(DEPT_PARENT_NOT_ENABLE);
        }
    }

    /**
     * 校验同级部门名称唯一
     */
    @VisibleForTesting
    void validateDeptNameUnique(Long id, Long parentId, String name) {
        DeptDO dept = deptMapper.selectByParentIdAndName(parentId, name);
        if (dept == null) { return; }
        if (id == null || !id.equals(dept.getId())) {
            throw exception(DEPT_NAME_DUPLICATE);
        }
    }
}
```

## 2. 事务管理

```java
// 标准事务声明：所有异常都回滚
@Transactional(rollbackFor = Exception.class)

// 只读事务（查询方法可选）
@Transactional(readOnly = true)

// 注意：
// 1. 事务注解放在 Service 实现类的方法上，不要放在接口上
// 2. 外部不能直接调用加了 @Transactional 的同类的其他方法（AOP 代理问题）
//    解决方案：通过 SpringUtil.getBean(getClass()) 获取代理对象再调用
```

### 自调用事务解决方案

```java
@Service
public class RoleServiceImpl implements RoleService {
    // 当需要调用同类中的事务方法时，通过代理调用
    private RoleService getSelf() {
        return SpringUtil.getBean(getClass());
    }

    public void batchProcess(List<Long> ids) {
        for (Long id : ids) {
            getSelf().doTransactionalWork(id);  // 通过代理调用
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void doTransactionalWork(Long id) {
        // 事务操作...
    }
}
```

## 3. 缓存模式

### 3.1 Cacheable（读取缓存）

```java
@Cacheable(value = RedisKeyConstants.ROLE, key = "#id", unless = "#result == null")
public RoleDO getRole(Long id) {
    return validateRoleExists(id);
}

// value 指向 RedisKeyConstants 中定义的常量
// key 用 SpEL 表达式
// unless = "#result == null" 表示 null 值不缓存
```

### 3.2 CacheEvict（清除缓存）

```java
@CacheEvict(value = RedisKeyConstants.ROLE, key = "#id")
public void updateRole(RoleSaveReqVO updateReqVO) { ... }

// allEntries = true 清除该缓存空间下的所有条目
@CacheEvict(cacheNames = RedisKeyConstants.DEPT_CHILDREN_ID_LIST, allEntries = true)
public Long createDept(DeptSaveReqVO createReqVO) { ... }

// condition = "#result != null" 仅当结果不为 null 时才清除
@CacheEvict(value = RedisKeyConstants.ROLE, key = "#id", condition = "#result != null")
```

### 3.3 RedisKeyConstants 定义

```java
public interface RedisKeyConstants {
    // 格式：模块_实体_用途
    String DEPT_CHILDREN_ID_LIST = "dept_children_ids";
    String ROLE = "role";
    String USER_ROLE_ID_LIST = "user_role_ids";
    String MENU_ROLE_ID_LIST = "menu_role_ids";
    String OAUTH2_ACCESS_TOKEN = "oauth2_access_token:%s";    // 带占位符
    String OAUTH_CLIENT = "oauth_client";
}
```

## 4. 异常抛出

```java
// 导入静态方法
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.invalidParamException;

// 方式 1：使用预定义 ErrorCode
throw exception(DEPT_NOT_FOUND);
throw exception(ROLE_NAME_DUPLICATE, roleName);       // 带占位符参数

// 方式 2：通用参数校验异常
throw invalidParamException("父级部门【{}】不能是自己", id);
```

## 5. 操作日志

```java
@Service
public class RoleServiceImpl implements RoleService {

    // 创建角色时记录操作日志
    @LogRecord(
        type = LogRecordConstants.SYSTEM_ROLE_TYPE,
        subType = LogRecordConstants.SYSTEM_ROLE_CREATE_SUB_TYPE,
        bizNo = "{{#role.id}}",
        success = LogRecordConstants.SYSTEM_ROLE_CREATE_SUCCESS
    )
    @Transactional(rollbackFor = Exception.class)
    public Long createRole(RoleSaveReqVO createReqVO) {
        // 存入上下文变量供日志使用
        LogRecordContext.putVariable("roleName", createReqVO.getName());
        // ... 业务逻辑
    }

    // 对比更新前后差异
    @LogRecord(
        type = SYSTEM_ROLE_TYPE,
        subType = SYSTEM_ROLE_UPDATE_SUB_TYPE,
        bizNo = "{{#updateReqVO.id}}",
        success = SYSTEM_ROLE_UPDATE_SUCCESS
    )
    public void updateRole(@DiffLogField RoleSaveReqVO updateReqVO) {
        // DiffParseFunction 会自动计算新旧值差异
        LogRecordContext.putVariable(DiffParseFunction.OLD_OBJECT, oldRole);
        // ...
    }
}
```

### 日志相关常量

```java
public interface LogRecordConstants {
    String SYSTEM_ROLE_TYPE = "SYSTEM_ROLE_TYPE";
    String SYSTEM_ROLE_CREATE_SUB_TYPE = "SYSTEM_ROLE_CREATE";
    String SYSTEM_ROLE_CREATE_SUCCESS = "【{{#roleName}}】角色创建成功";
    String SYSTEM_ROLE_UPDATE_SUB_TYPE = "SYSTEM_ROLE_UPDATE";
    String SYSTEM_ROLE_UPDATE_SUCCESS = "{{#roleName}}：{_DIFF{#updateReqVO}}";
}
```

## 6. 依赖注入选择

```java
@Service
public class MenuServiceImpl implements MenuService {

    @Resource
    private MenuMapper menuMapper;

    @Resource
    private PermissionService permissionService;

    // @Lazy 打破循环依赖
    @Resource
    @Lazy
    private TenantService tenantService;
}
```

| 注解 | 说明 |
|------|------|
| `@Resource` | 项目统一使用，按名称注入 |
| `@Lazy` | 延迟注入，打破循环依赖 |
| `@Autowired` | 不推荐，项目不使用 |

## 7. 完整 CRUD 校验模式

一个完整的 Service 包含以下校验层次：

```
1. 存在性校验  ── validateXxxExists(id)    →  数据必须存在
2. 唯一性校验  ── validateXxxUnique(...)    →  名称/编码不重复
3. 关联性校验  ── validateParentXxx(...)    →  父级引用合法
4. 状态校验    ── validateXxxEnabled(...)   →  数据处于可用状态
5. 级联校验    ── validateNoChildren(id)    →  无子数据才能删除
6. 业务规则    ── 具体业务场景              →  业务相关的约束
```

**校验方法遵循固定模式：**

1. `@VisibleForTesting` 注解（方便在测试中直接调用）
2. 不满足条件时抛出 `ServiceException`
3. 满足条件时返回有用的数据（如 `validateDeptExists` 返回部门对象）
4. 方法名以 `validate` 开头

## 8. Service 层反模式（避免事项）

❌ **不要在 Service 中直接操作 HttpServletRequest/HttpServletResponse**
❌ **不要返回 `CommonResult<T>`**（那是 Controller 层的职责）
❌ **不要将 DO 直接暴露给 Controller**（应在 Controller 层转换为 VO）
❌ **不要在 Service 接口上定义过多 default 方法**
❌ **不要在循环中调用 `@Transactional` 方法**（用自代理解决）
❌ **不要 catch 异常后吞掉**（要么处理，要么继续抛出 `ServiceException`）
