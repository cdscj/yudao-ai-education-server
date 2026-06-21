# 07 - Framework 设计

## 1. Starter 模块标准结构

```
yudao-spring-boot-starter-{name}/
├── pom.xml
└── src/main/
    ├── java/cn/iocoder/yudao/framework/{name}/
    │   ├── config/
    │   │   └── Yudao{Name}AutoConfiguration.java    # 自动配置类
    │   ├── core/
    │   │   ├── {核心业务类}.java
    │   │   └── util/
    │   │       └── {工具类}.java
    │   └── package-info.java
    └── resources/META-INF/spring/
        └── org.springframework.boot.autoconfigure.AutoConfiguration.imports  # 注册文件
```

**目录结构遵循：**
- `config/` — Spring 自动配置、属性类
- `core/` — 核心功能实现，不依赖 Spring 注解
- `core/util/` — 工具类
- Spring Boot 3.x 使用 `AutoConfiguration.imports`（非旧版 `spring.factories`）

## 2. Auto-Configuration 注册

### 2.1 AutoConfiguration.imports 文件

每个 Starter 在 META-INF 下注册其自动配置类：

```
META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

内容示例（yudao-spring-boot-starter-web）：
```
cn.iocoder.yudao.framework.web.config.YudaoWebAutoConfiguration
cn.iocoder.yudao.framework.web.config.YudaoJacksonAutoConfiguration
cn.iocoder.yudao.framework.web.config.YudaoSwaggerAutoConfiguration
cn.iocoder.yudao.framework.web.config.YudaoApiLogAutoConfiguration
cn.iocoder.yudao.framework.web.config.YudaoXssAutoConfiguration
cn.iocoder.yudao.framework.web.config.YudaoBannerAutoConfiguration
cn.iocoder.yudao.framework.web.config.YudaoApiEncryptAutoConfiguration
```

### 2.2 spring.factories（仅限 EnvironmentPostProcessor）

只有 `EnvironmentPostProcessor` 需要使用旧的 `spring.factories`：

```
META-INF/spring.factories
```
```properties
org.springframework.boot.env.EnvironmentPostProcessor=\
cn.iocoder.yudao.framework.mybatis.config.IdTypeEnvironmentPostProcessor
```

## 3. 自动配置类编写规范

### 3.1 标准模板

```java
@AutoConfiguration                           // Spring Boot 3.x 新注解
@EnableConfigurationProperties(XxxProperties.class)
@ConditionalOnProperty(prefix = "yudao.xxx", name = "enable", havingValue = "true",
                       matchIfMissing = true)
public class YudaoXxxAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public XxxService xxxService() {
        return new XxxServiceImpl();
    }
}
```

### 3.2 配置顺序控制

```java
// before: 在当前类之前加载
@AutoConfiguration(before = MybatisPlusAutoConfiguration.class)

// after: 在当前类之后加载
@AutoConfiguration(after = YudaoRedisAutoConfiguration.class)

// beforeName: 用字符串指定（避免编译依赖）
@AutoConfiguration(beforeName = {
    "com.fhs.trans.config.TransServiceConfig",
    "com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure"
})

// 安全模块需要最先加载
@AutoConfigureOrder(-1)
```

### 3.3 完整的 Web Auto-Configuration 示例

```java
@AutoConfiguration(beforeName = "com.fhs.trans.config.TransServiceConfig")
@EnableConfigurationProperties(WebProperties.class)
public class YudaoWebAutoConfiguration {

    // ========== WebMvc 定制 ==========

    @Bean
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public WebMvcRegistrations webMvcRegistrations(WebProperties webProperties) {
        return new WebMvcRegistrations() {
            @Override
            public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
                return new RequestMappingHandlerMapping() {
                    @Override
                    public void afterPropertiesSet() {
                        super.afterPropertiesSet();
                        // 根据包路径设置 API 前缀
                        setPathPrefixes(Map.of(
                            webProperties.getAdminApi().getPrefix(),    // "/admin-api"
                            webProperties.getAdminApi().getController() // "**.controller.admin.**"
                        ));
                    }
                };
            }
        };
    }

    // ========== 全局异常处理 ==========

    @Bean
    @ConditionalOnMissingBean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

    // ========== 全局响应处理 ==========

    @Bean
    @ConditionalOnMissingBean
    public GlobalResponseBodyHandler globalResponseBodyHandler() {
        return new GlobalResponseBodyHandler();
    }

    // ========== 过滤器 ==========

    @Bean
    @ConditionalOnMissingBean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        return createFilterBean(new CorsFilter(), WebFilterOrderEnum.CORS_FILTER);
    }

    // ========== 条件启用的过滤器 ==========

    @Bean
    @ConditionalOnProperty(prefix = "yudao", name = "demo", havingValue = "true")
    public FilterRegistrationBean<DemoFilter> demoFilter() {
        return createFilterBean(new DemoFilter(), WebFilterOrderEnum.DEMO_FILTER);
    }

    // ========== 工具方法 ==========

    protected static <T extends Filter> FilterRegistrationBean<T> createFilterBean(
            T filter, Integer order) {
        FilterRegistrationBean<T> bean = new FilterRegistrationBean<>(filter);
        bean.setOrder(order);
        return bean;
    }
}
```

## 4. 配置属性类

```java
@ConfigurationProperties(prefix = "yudao.security")
@Data
public class SecurityProperties {

    /** 令牌请求头名称 */
    @NotEmpty(message = "Token 请求头名称不能为空")
    private String tokenHeader = "Authorization";

    /** 令牌请求参数名 */
    private String tokenParameter = "token";

    /** 是否开启 Mock 登录 */
    private Boolean mockEnable = false;

    /** Mock 登录密钥 */
    private String mockSecret = "test";

    /** 免登录的 URL 列表 */
    private List<String> permitAllUrls = Collections.emptyList();

    /** 密码编码器强度 */
    private Integer passwordEncoderLength = 4;
}
```

**规范：**
- `prefix = "yudao.{模块}"` 统一前缀
- 每个属性带 `@Schema` 或 JavaDoc 注释
- 提供合理的默认值
- 必填参数用 `@NotEmpty` / `@NotNull`

## 5. MapperScan 配置

```java
@AutoConfiguration(before = MybatisPlusAutoConfiguration.class)
@MapperScan(
    value = "${yudao.info.base-package}",   // cn.iocoder.yudao
    annotationClass = Mapper.class           // 只扫描标记了 @Mapper 的接口
)
public class YudaoMybatisAutoConfiguration {

    // 配置 MyBatis Plus 拦截器（分页）
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }

    // 配置字段自动填充
    @Bean
    public DefaultDBFieldHandler defaultDBFieldHandler() {
        return new DefaultDBFieldHandler();
    }

    // 条件注册：Oracle/PG 才有序列 KeyGenerator
    @Bean
    @ConditionalOnProperty(prefix = "mybatis-plus.global-config.db-config",
                           name = "id-type", havingValue = "INPUT")
    public IKeyGenerator keyGenerator() {
        return new MybatisPlusKeyGenerator();
    }
}
```

## 6. Redis 配置

```java
@AutoConfiguration(before = RedissonAutoConfigurationV2.class)
public class YudaoRedisAutoConfiguration {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // Key 用 String 序列化
        template.setKeySerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());

        // Value 用自定义 JSON 序列化（解决 LocalDateTime 序列化问题）
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Jackson2JsonRedisSerializer<Object> jsonSerializer =
            new Jackson2JsonRedisSerializer<>(mapper, Object.class);

        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        return template;
    }
}
```

## 7. EnvironmentPostProcessor（环境后置处理）

用于在 Spring 上下文加载前修改环境变量：

```java
public class IdTypeEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment,
                                        SpringApplication application) {
        // 根据数据源 URL 自动判断数据库类型
        String url = environment.getProperty("spring.datasource.dynamic.datasource.master.url");
        if (url == null) { return; }

        String dbType = resolveDbType(url);
        // 将判断结果设置到环境变量
        if (dbType != null) {
            System.setProperty("mybatis-plus.global-config.db-config.id-type",
                               "oracle".equals(dbType) ? "INPUT" : "AUTO");
        }
    }
}
```

注册（必须用 spring.factories）：
```properties
org.springframework.boot.env.EnvironmentPostProcessor=\
cn.iocoder.yudao.framework.mybatis.config.IdTypeEnvironmentPostProcessor
```

## 8. 自定义注解驱动配置

```java
// 1. 定义注解
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@InEnum(value = CommonStatusEnum.class)
public @interface InEnum {
    Class<? extends ArrayValuable<?>> value();
    String message() default "必须在指定范围";
}

// 2. 定义对应的 Validator
public class InEnumValidator implements ConstraintValidator<InEnum, Object> {
    private Set<Object> values;

    @Override
    public void initialize(InEnum annotation) {
        values = Arrays.stream(annotation.value().getEnumConstants())
                       .flatMap(e -> Arrays.stream(((ArrayValuable<?>) e).array()))
                       .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return value == null || values.contains(value);
    }
}

// 3. 使用
@InEnum(value = CommonStatusEnum.class, message = "状态值不正确")
private Integer status;
```

## 9. Filter 设计模式

### 抽象过滤器基类

```java
// 只对 API 请求生效的过滤器基类
public abstract class ApiRequestFilter extends OncePerRequestFilter {
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 只过滤 /admin-api 和 /app-api 请求
        return !request.getRequestURI().startsWith("/admin-api")
            && !request.getRequestURI().startsWith("/app-api");
    }
}
```

### 请求体缓存过滤器

```java
public class CacheRequestBodyFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain chain) {
        // 只缓存 JSON 请求
        if (isJsonRequest(request)) {
            request = new CacheRequestBodyWrapper(request);
        }
        chain.doFilter(request, response);
    }

    // CacheRequestBodyWrapper 内部缓存了 request body
    // 使 getInputStream() 可以重复读取
}
```

## 10. 添加新 Starter 的步骤

1. **创建模块目录**
   ```
   yudao-framework/yudao-spring-boot-starter-xxx/
   ```

2. **编写 POM**
   ```xml
   <parent>yudao-framework</parent>
   <artifactId>yudao-spring-boot-starter-xxx</artifactId>
   <dependencies>
       <dependency><groupId>cn.iocoder.cloud</groupId>
                    <artifactId>yudao-common</artifactId></dependency>
       <dependency><groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-autoconfigure</artifactId></dependency>
   </dependencies>
   ```

3. **创建自动配置类**
   ```java
   @AutoConfiguration
   public class YudaoXxxAutoConfiguration {
       @Bean
       public XxxService xxxService() { ... }
   }
   ```

4. **注册到 AutoConfiguration.imports**
   ```
   META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
   ```

5. **在父 POM 中声明**
   ```xml
   <!-- yudao-framework/pom.xml -->
   <module>yudao-spring-boot-starter-xxx</module>
   ```

6. **在 BOM 中声明版本**
   ```xml
   <!-- yudao-dependencies/pom.xml -->
   <dependency>
       <groupId>cn.iocoder.cloud</groupId>
       <artifactId>yudao-spring-boot-starter-xxx</artifactId>
       <version>${revision}</version>
   </dependency>
   ```
