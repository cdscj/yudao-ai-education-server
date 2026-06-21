# 03 - Controller 层模式

## 1. Controller 标准模板

```java
@Tag(name = "管理后台 - 部门")
@RestController
@RequestMapping("/system/dept")
@Validated
public class DeptController {

    @Resource
    private DeptService deptService;

    // ========== 创建 ==========

    @PostMapping("create")
    @Operation(summary = "创建部门")
    @PreAuthorize("@ss.hasPermission('system:dept:create')")
    public CommonResult<Long> createDept(@Valid @RequestBody DeptSaveReqVO createReqVO) {
        Long deptId = deptService.createDept(createReqVO);
        return success(deptId);
    }

    // ========== 更新 ==========

    @PutMapping("update")
    @Operation(summary = "更新部门")
    @PreAuthorize("@ss.hasPermission('system:dept:update')")
    public CommonResult<Boolean> updateDept(@Valid @RequestBody DeptSaveReqVO updateReqVO) {
        deptService.updateDept(updateReqVO);
        return success(true);
    }

    // ========== 删除 ==========

    @DeleteMapping("delete")
    @Operation(summary = "删除部门")
    @Parameter(name = "id", description = "部门编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:dept:delete')")
    public CommonResult<Boolean> deleteDept(@RequestParam("id") Long id) {
        deptService.deleteDept(id);
        return success(true);
    }

    // 批量删除
    @DeleteMapping("/delete-list")
    @Operation(summary = "删除部门列表")
    @PreAuthorize("@ss.hasPermission('system:dept:delete')")
    public CommonResult<Boolean> deleteDeptList(@RequestParam("ids") List<Long> ids) {
        deptService.deleteDeptList(ids);
        return success(true);
    }

    // ========== 查询单个 ==========

    @GetMapping("/get")
    @Operation(summary = "获得部门信息")
    @PreAuthorize("@ss.hasPermission('system:dept:query')")
    public CommonResult<DeptRespVO> getDept(@RequestParam("id") Long id) {
        DeptDO dept = deptService.getDept(id);
        return success(BeanUtils.toBean(dept, DeptRespVO.class));
    }

    // ========== 查询列表 ==========

    @GetMapping("/list")
    @Operation(summary = "获取部门列表")
    @PreAuthorize("@ss.hasPermission('system:dept:query')")
    public CommonResult<List<DeptRespVO>> getDeptList(@Valid DeptListReqVO reqVO) {
        List<DeptDO> list = deptService.getDeptList(reqVO);
        return success(BeanUtils.toBean(list, DeptRespVO.class));
    }

    // ========== 精简列表（下拉框用） ==========

    @GetMapping(value = {"/list-all-simple", "/simple-list"})
    @Operation(summary = "获取部门精简信息列表")
    public CommonResult<List<DeptSimpleRespVO>> getSimpleDeptList() {
        List<DeptDO> list = deptService.getDeptList(new DeptListReqVO());
        return success(BeanUtils.toBean(list, DeptSimpleRespVO.class));
    }
}
```

## 2. 标准 CRUD 端点清单

| 操作 | HTTP 方法 | URL | 参数 | 返回值 |
|------|-----------|-----|------|--------|
| 新增 | POST | `/create` | `@Valid @RequestBody SaveReqVO` | `CommonResult<Long>` (ID) |
| 修改 | PUT | `/update` | `@Valid @RequestBody SaveReqVO` | `CommonResult<Boolean>` |
| 删除 | DELETE | `/delete` | `@RequestParam("id") Long` | `CommonResult<Boolean>` |
| 批量删除 | DELETE | `/delete-list` | `@RequestParam("ids") List<Long>` | `CommonResult<Boolean>` |
| 查询单个 | GET | `/get` | `@RequestParam("id") Long` | `CommonResult<RespVO>` |
| 分页查询 | GET | `/page` | `@Valid PageReqVO`（无 @RequestBody） | `CommonResult<PageResult<RespVO>>` |
| 列表查询 | GET | `/list` | Query VO（无 @RequestBody） | `CommonResult<List<RespVO>>` |
| 精简列表 | GET | `/list-all-simple` 或 `/simple-list` | 无 | `CommonResult<List<SimpleRespVO>>` |
| 导出 | GET | `/export` | Query VO | void（直接写入 HttpServletResponse） |

## 3. VO 设计规范

### 3.1 SaveReqVO（新增/修改共享）

```java
@Schema(description = "管理后台 - 部门新增/修改 Request VO")
@Data
public class DeptSaveReqVO {

    @Schema(description = "部门编号", example = "1024")
    private Long id;                        // 修改时传入，新增时不传

    @Schema(description = "部门名称", requiredMode = REQUIRED, example = "芋道")
    @NotBlank(message = "部门名称不能为空")
    @Size(max = 30, message = "部门名称长度不能超过 30 个字符")
    private String name;

    @Schema(description = "父部门 ID", example = "2048")
    @NotNull(message = "父部门 ID 不能为空")
    private Long parentId;

    @Schema(description = "显示顺序", example = "1024")
    @NotNull(message = "显示顺序不能为空")
    private Integer sort;

    @Schema(description = "负责人的用户编号", example = "2048")
    private Long leaderUserId;

    @Schema(description = "联系电话", example = "15601691000")
    @Size(max = 11, message = "联系电话长度不能超过11个字符")
    private String phone;

    @Schema(description = "邮箱", example = "yudao@iocoder.cn")
    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱长度不能超过 50 个字符")
    private String email;

    @Schema(description = "状态", requiredMode = REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    @InEnum(value = CommonStatusEnum.class, message = "状态必须是 {value}")
    private Integer status;
}
```

**设计原则：**
- 新增和修改共用同一个 VO，通过 `id` 是否为空来区分
- 所有必填字段加 `@NotNull` / `@NotBlank`
- 枚举值校验使用 `@InEnum`
- 每个字段都加 `@Schema` 描述
- 使用 `requiredMode = REQUIRED` 标记必填字段

### 3.2 RespVO（完整响应）

```java
@Schema(description = "管理后台 - 部门信息 Response VO")
@Data
public class DeptRespVO {

    @Schema(description = "部门编号", example = "1024")
    private Long id;

    @Schema(description = "部门名称", example = "芋道")
    private String name;

    @Schema(description = "父部门 ID", example = "2048")
    private Long parentId;

    @Schema(description = "显示顺序", example = "1024")
    private Integer sort;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "创建时间", example = "时间戳格式")
    private LocalDateTime createTime;
}
```

### 3.3 PageReqVO（分页查询）

```java
@Schema(description = "管理后台 - 角色分页 Request VO")
@Data
public class RolePageReqVO extends PageParam {

    @Schema(description = "角色名称", example = "系统管理员")
    private String name;

    @Schema(description = "角色编码", example = "admin")
    private String code;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;  // 数组：时间范围 [开始, 结束]
}
```

### 3.4 SimpleRespVO（精简信息，下拉框用）

```java
@Schema(description = "管理后台 - 部门精简信息 Response VO")
@Data
public class DeptSimpleRespVO {

    @Schema(description = "部门编号", example = "1024")
    private Long id;

    @Schema(description = "部门名称", example = "芋道")
    private String name;

    @Schema(description = "父部门 ID", example = "2048")
    private Long parentId;
}
```

## 4. 参数校验模式

### 4.1 字段级校验（注解）

```java
@NotBlank(message = "名称不能为空")
@Size(min = 2, max = 30, message = "名称长度为 2-30 个字符")
@Email(message = "邮箱格式不正确")
@Pattern(regexp = "^[a-zA-Z0-9]+$", message = "只允许字母和数字")
@InEnum(value = CommonStatusEnum.class, message = "状态值不正确")
@Mobile  // 自定义：手机号校验
@NotNull(message = "ID 不能为空")
```

### 4.2 方法级校验（@AssertTrue）

当校验涉及多个字段时，使用 `@AssertTrue` 方法：

```java
@Schema(description = "管理后台 - 用户新增/修改 Request VO")
@Data
public class UserSaveReqVO {

    @Schema(description = "登录账号", example = "admin")
    @NotBlank(message = "登录账号不能为空")
    private String username;

    @Schema(description = "用户昵称", example = "管理员")
    @NotBlank(message = "用户昵称不能为空")
    private String nickname;

    // 条件校验：新增时必须填密码
    @AssertTrue(message = "新增用户时必须设置密码")
    public boolean isPasswordValid() {
        return id != null || StrUtil.isNotBlank(password);
    }
}
```

## 5. 权限控制模式

```java
// 方法级权限
@PreAuthorize("@ss.hasPermission('system:dept:create')")
@PreAuthorize("@ss.hasAnyPermissions('system:dept:create', 'system:dept:update')")
@PreAuthorize("@ss.hasRole('super_admin')")
@PreAuthorize("@ss.hasAnyRoles('super_admin', 'dept_admin')")

// 免登录
@PermitAll

// 禁用数据权限（如导出、公开查询）
@GetMapping("/export")
@DataPermission(enable = false)
public void export(...) { ... }
```

## 6. 导出模式

```java
@GetMapping("/export")
@Operation(summary = "导出部门")
@PreAuthorize("@ss.hasPermission('system:dept:export')")
@ApiAccessLog(operateType = EXPORT)
public void export(HttpServletResponse response, DeptListReqVO reqVO) throws IOException {
    List<DeptDO> list = deptService.getDeptList(reqVO);
    // 写入 Excel
    ExcelUtils.write(response, "部门列表.xlsx", "部门", DeptRespVO.class,
                     BeanUtils.toBean(list, DeptRespVO.class));
}
```

## 7. 文件上传/下载模式

```java
@PostMapping("/import")
@Operation(summary = "导入用户")
@PreAuthorize("@ss.hasPermission('system:user:import')")
public CommonResult<UserImportRespVO> importExcel(@RequestParam("file") MultipartFile file,
                                                   @RequestParam("updateSupport") Boolean updateSupport)
        throws Exception {
    List<UserImportVO> importList = ExcelUtils.read(file, UserImportVO.class);
    UserImportRespVO respVO = userService.importUsers(importList, updateSupport);
    return success(respVO);
}
```

## 8. Controller 注解清单

```java
@Tag(name = "管理后台 - 部门")                  // API 分组名称
@RestController                                  // REST 控制器
@RequestMapping("/system/dept")                   // 请求路径前缀
@Validated                                       // 开启 @Valid 校验
public class DeptController {

    @Resource                                    // 注入 Service
    private DeptService deptService;

    @PostMapping("create")                       // POST 映射
    @PutMapping("update")                        // PUT 映射
    @DeleteMapping("delete")                     // DELETE 映射
    @GetMapping("/get")                          // GET 映射

    @Operation(summary = "创建部门")              // API 文档摘要
    @Parameter(name = "id", required = true)     // 参数文档

    @Valid @RequestBody                          // 校验 + JSON 请求体
    @RequestParam("id")                          // URL 查询参数
    @RequestParam(value = "ids")                // 数组参数

    @PreAuthorize("@ss.hasPermission('xxx')")    // 权限控制
    @PermitAll                                    // 免登录
    @ApiAccessLog(operateType = EXPORT)           // 访问日志 + 导出标记
    @DataPermission(enable = false)              // 禁用数据权限过滤
}
```
