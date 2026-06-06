# Auth 模块用户登录注册接口实现计划

## 一、需求分析

根据 OpenAPI 接口文档，需要在 auth 模块中实现以下用户端接口：

| 接口路径 | 方法 | 说明 | 是否需要认证 |
|----------|------|------|-------------|
| `/user/register` | POST | 用户注册 | 否 `@PermitAll` |
| `/user/login` | POST | 用户登录 | 否 `@PermitAll` |
| `/user/logout` | POST | 用户登出 | 是（Header: Authorization） |
| `/user/updateUserInfo` | PUT | 更新用户信息 | 是 |
| `/user/updateUserPassword` | PATCH | 修改密码 | 是（Header: Authorization） |
| `/user/updateUserAvatar` | PATCH | 更新头像 | 是 |
| `/user/resetUserPassword` | PATCH | 重置密码（忘记密码） | 否 `@PermitAll` |

## 二、现有 Auth 模块问题分析

当前 auth 模块存在以下问题需要修复：

1. **VO 字段与实现不匹配**：`AppAuthLoginReqVO` 定义 `email` 字段，但 `MusicAuthServiceImpl.login0()` 调用 `reqVO.getMobile()`，代码无法编译
2. **接口与实现不一致**：`MusicAuthService` 接口只定义 `login()`，但实现类有多个未声明的方法
3. **缺失依赖类**：`MusicAuthServiceImpl` 引用 `MemberUserService`、`MemberUserDO`、`AuthConvert` 等未定义类型
4. **基础包路径错误**：`yudao.info.base-package` 配置为 `cn.iocoder.yudao.module.member`，应为 `cn.iocoder.yudao.module.auth`
5. **旧版代码残留**：根目录 `src/` 下有旧版启动类和 application.properties

## 三、实现步骤

### 步骤 1：清理旧版残留代码

- 删除根目录 `d:\java\giteecode\yudao-cloud-master-jdk17\yudao-cloud-master-jdk17\yudao-module-auth\src\` 下的旧版启动类和配置文件

### 步骤 2：修复配置文件

- 修改 `application.yaml` 中 `yudao.info.base-package` 从 `cn.iocoder.yudao.module.member` 改为 `cn.iocoder.yudao.module.auth`

### 步骤 3：完善 DO 层（数据对象）

- 保留现有的 `TbUserDO.java`（位于 `dal/dataobject/`），已经映射 `tb_user` 表
- 如有需要，补充 MyBatis Plus 相关注解（如继承合适的 BaseDO、添加 `@KeySequence` 等）

### 步骤 4：创建 Mapper 层

- 新建 `dal/mysql/TbUserMapper.java`
- 继承 `BaseMapperX<TbUserDO>`，使用 `@Mapper` 注解
- 参考 member 模块的 `MemberUserMapper` 风格
- 实现基础查询方法：`selectByUsername()`, `selectByEmail()`, `selectByPhone()`

### 步骤 5：创建 Convert 层（MapStruct 对象转换）

- 新建 `convert/UserConvert.java`
- 使用 `@Mapper` 注解 + `Mappers.getMapper()` 单例模式
- 实现 DO ↔ VO 转换方法

### 步骤 6：完善 VO 层（请求/响应对象）

在 `controller/app/auth/vo/` 下创建/修改以下 VO：

| 文件名 | 说明 | 关键字段 | 校验 |
|--------|------|----------|------|
| `AppAuthLoginReqVO.java` | 修改现有 | username/email, password | `@NotEmpty`, `@Length` |
| `AppAuthLoginRespVO.java` | 保留现有 | userId, accessToken, refreshToken, expiresTime | - |
| `AppAuthRegisterReqVO.java` | 新建 | username, password, email, phone | `@NotEmpty`, `@Length`, `@Email` |
| `AppAuthResetPasswordReqVO.java` | 新建 | email/phone, newPassword, code(验证码) | `@NotEmpty`, `@Length` |
| `AppAuthUpdatePasswordReqVO.java` | 新建 | oldPassword, newPassword | `@NotEmpty`, `@Length` |
| `AppAuthUpdateInfoReqVO.java` | 新建 | username, email, phone, userAvatar, introduction | 按需校验 |
| `UserDTO.java` | 新建 | 映射接口文档 | - |
| `UserRegisterDTO.java` | 新建 | 映射接口文档 | - |
| `UserLoginDTO.java` | 新建 | 映射接口文档 | - |
| `UserPasswordDTO.java` | 新建 | 映射接口文档 | - |
| `UserResetPasswordDTO.java` | 新建 | 映射接口文档 | - |

### 步骤 7：重构 Service 层

**重命名并重构服务接口和实现**：
- 将 `MusicAuthService` 重命名为 `UserAuthService`
- 将 `MusicAuthServiceImpl` 重命名为 `UserAuthServiceImpl`
- 方法定义遵循 member 模块 `MemberAuthService` 的风格

**UserAuthService 接口方法**：
```java
AppAuthLoginRespVO login(@Valid AppAuthLoginReqVO reqVO);
AppAuthLoginRespVO register(@Valid AppAuthRegisterReqVO reqVO);
void logout(String token);
void updateUserPassword(Long userId, AppAuthUpdatePasswordReqVO reqVO);
void resetUserPassword(AppAuthResetPasswordReqVO reqVO);
```

**UserAuthServiceImpl 实现要点**：
- 注册：校验用户名/邮箱唯一性 → BCrypt 加密密码 → 保存用户 → 创建 Token → 返回
- 登录：查询用户 → BCrypt 密码匹配 → 创建 Token → 返回
- 登出：移除 Token
- 修改密码：验证旧密码 → 加密新密码 → 更新
- 重置密码：验证码校验 → 加密新密码 → 更新

### 步骤 8：创建 Controller 层

**新建 `controller/app/user/AppUserController.java`**：
- 路径：`/user`
- 参考 member 模块 `AppMemberUserController` 风格
- 公开接口使用 `@PermitAll`
- 需认证接口通过 `SecurityFrameworkUtils.getLoginUserId()` 获取当前用户

```java
@Tag(name = "用户 APP - 用户")
@RestController
@RequestMapping("/user")
@Validated
@Slf4j
public class AppUserController {
    @Resource private UserAuthService userAuthService;
    
    @PostMapping("/register")
    @Operation(summary = "用户注册")
    @PermitAll
    public CommonResult<AppAuthLoginRespVO> register(@RequestBody @Valid AppAuthRegisterReqVO reqVO);
    
    @PostMapping("/login")
    @Operation(summary = "用户登录")
    @PermitAll
    public CommonResult<AppAuthLoginRespVO> login(@RequestBody @Valid AppAuthLoginReqVO reqVO);
    
    @PostMapping("/logout")
    @Operation(summary = "用户登出")
    @PermitAll  // 或用 @PreAuthorize 验证登录状态
    public CommonResult<Boolean> logout(HttpServletRequest request);
    
    @PutMapping("/updateUserInfo")
    @Operation(summary = "更新用户信息")
    public CommonResult<Boolean> updateUserInfo(@RequestBody @Valid AppAuthUpdateInfoReqVO reqVO);
    
    @PatchMapping("/updateUserPassword")
    @Operation(summary = "修改密码")
    public CommonResult<Boolean> updateUserPassword(@RequestBody @Valid AppAuthUpdatePasswordReqVO reqVO);
    
    @PatchMapping("/resetUserPassword")
    @Operation(summary = "重置密码")
    @PermitAll
    public CommonResult<Boolean> resetUserPassword(@RequestBody @Valid AppAuthResetPasswordReqVO reqVO);
}
```

### 步骤 9：创建安全配置

- 新建 `framework/security/config/SecurityConfiguration.java`
- 参考 member 模块的 `SecurityConfiguration`
- 放行公开接口路径

### 步骤 10：创建错误码

- 在 `enums/ErrorCodeConstants.java` 中定义 auth 模块错误码
- 格式参考：`AUTH_USER_NOT_EXISTS`, `AUTH_PASSWORD_ERROR`, `AUTH_USER_EXISTS` 等

### 步骤 11：验证和修复编译问题

- 确保所有 import 正确
- 确保所有依赖类型存在
- 运行 `mvn compile` 验证编译通过

## 四、新增/修改文件清单

```
yudao-module-auth/
├── src/                                         → 【删除】旧版遗留代码
├── yudao-module-auth-server/
│   └── src/main/java/cn/iocoder/yudao/module/auth/
│       ├── controller/app/
│       │   ├── auth/
│       │   │   └── AppAuthController.java       → 【删除】合并到 AppUserController
│       │   └── user/
│       │       ├── AppUserController.java       → 【新建】
│       │       └── vo/
│       │           ├── AppAuthLoginReqVO.java   → 【修改】
│       │           ├── AppAuthLoginRespVO.java  → 【保留】
│       │           ├── AppAuthRegisterReqVO.java → 【新建】
│       │           ├── AppAuthResetPasswordReqVO.java → 【新建】
│       │           ├── AppAuthUpdatePasswordReqVO.java → 【新建】
│       │           └── AppAuthUpdateInfoReqVO.java → 【新建】
│       ├── convert/
│       │   └── UserConvert.java                 → 【新建】
│       ├── dal/
│       │   ├── dataobject/
│       │   │   └── TbUserDO.java                → 【修改/完善】
│       │   └── mysql/
│       │       └── TbUserMapper.java            → 【新建】
│       ├── enums/
│       │   └── ErrorCodeConstants.java          → 【新建】
│       ├── framework/
│       │   └── security/
│       │       └── config/
│       │           └── SecurityConfiguration.java → 【新建】
│       └── service/
│           ├── UserAuthService.java             → 【新建】（替代 MusicAuthService）
│           └── impl/
│               └── UserAuthServiceImpl.java     → 【新建】（替代 MusicAuthServiceImpl）
└── yudao-module-auth-server/src/main/resources/
    └── application.yaml                         → 【修改】base-package
```

## 五、代码规范要点（模仿 member 模块）

1. **返回类型**：统一使用 `CommonResult<T>`，通过静态导入 `success()` 返回
2. **校验**：使用 Jakarta Validation（`@NotEmpty`, `@NotNull`, `@Length`, `@Email` 等）
3. **密码加密**：使用 `BCryptPasswordEncoder`
4. **Token 创建**：通过 `OAuth2TokenCommonApi` 创建 OAuth2 Token
5. **日志**：使用 Lombok `@Slf4j`
6. **注入**：使用 `@Resource` 注入依赖
7. **异常**：使用 `exception(ERROR_CODE)` 静态导入抛出业务异常
8. **Mapper**：继承 `BaseMapperX<T>`，使用 default 方法避免 XML
9. **Convert**：使用 MapStruct `@Mapper` + `Mappers.getMapper()` 单例模式
10. **Controller**：使用 `@Tag` + `@Operation` + `@PermitAll` / `@PreAuthorize`
