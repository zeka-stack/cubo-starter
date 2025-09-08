# Cubo OpenAPI Spring Boot

## 概述

`cubo-openapi-spring-boot` 是 Cubo Starter 项目的 OpenAPI 文档模块，提供了完整的 API 文档生成和管理功能。该模块支持多种 API 文档工具，包括
Knife4j（Swagger UI 增强版）和 Dubbo 接口文档，为开发者提供了便捷的 API 文档管理和调试工具。

## 主要功能

### 1. API 文档生成

- 基于 OpenAPI 3.0 规范的 API 文档自动生成
- 支持 Swagger 注解和 Spring Boot 自动配置
- 提供丰富的 API 元数据支持

### 2. 多文档工具支持

- **Knife4j**: 增强版的 Swagger UI，提供更好的用户体验
- **Dubbo**: 支持 Dubbo 服务的接口文档生成
- 统一的配置管理和切换机制

### 3. 文档增强功能

- 支持 API 分组和标签管理
- 提供接口测试和调试功能
- 支持多种认证方式

### 4. 自定义配置

- 灵活的文档配置选项
- 支持自定义文档信息和样式
- 提供扩展点用于功能定制

## 模块结构

```
cubo-openapi-spring-boot/
├── cubo-openapi-spring-boot-autoconfigure/    # 自动配置模块
├── cubo-openapi-spring-boot-core/             # 核心功能模块
│   ├── cubo-openapi-common/                   # 通用组件
│   ├── cubo-openapi-dubbo/                    # Dubbo 文档支持
│   └── cubo-openapi-knife4j/                  # Knife4j 支持
└── cubo-openapi-spring-boot-starter/          # Starter 模块
    ├── cubo-openapi-dubbo-spring-boot-starter/
    └── cubo-openapi-knife4j-spring-boot-starter/
```

### 子模块说明

#### cubo-openapi-spring-boot-autoconfigure

- **OpenAPIAutoConfiguration**: OpenAPI 主自动配置类
- **Knife4jAutoConfiguration**: Knife4j 自动配置类

#### cubo-openapi-spring-boot-core

##### cubo-openapi-common

- 提供通用的 OpenAPI 配置和工具类
- 定义文档生成的核心接口

##### cubo-openapi-knife4j

- 基于 Knife4j 的文档实现
- 提供增强的 Swagger UI 功能
- 支持多种主题和样式

##### cubo-openapi-dubbo

- 支持 Dubbo 服务的接口文档生成
- 提供 Dubbo 特有的文档功能

## 核心特性

### 1. Knife4j 集成

#### 自动配置

```java
@AutoConfiguration
@ConditionalOnClass(OpenApiExtension.class)
@ConditionalOnProperty(prefix = "knife4j", name = "enable", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(Knife4jProperties.class)
public class Knife4jAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public OpenApiExtension knife4jOpenApiExtension() {
        return new Knife4jOpenApiExtension();
    }
}
```

#### 配置属性

```yaml
knife4j:
  enable: true
  openapi:
    title: "API 文档"
    description: "系统 API 接口文档"
    version: "1.0.0"
    contact:
      name: "开发团队"
      email: "dev@example.com"
      url: "https://example.com"
    license:
      name: "MIT"
      url: "https://opensource.org/licenses/MIT"
  setting:
    language: "zh_cn"
    enable-version: true
    enable-swagger-models: true
    enable-document-scan: true
    enable-reload-cache-parameter: true
    enable-after-script: true
    enable-filter-multipart-api-method-type: POST
    enable-filter-multipart-apis: true
    enable-request-cache: true
    enable-host-text: "localhost:8080"
    enable-home-custom: true
    home-custom-path: "classpath:markdown/home.md"
    enable-search: true
    enable-footer: true
    enable-footer-custom: true
    footer-custom-content: "Copyright © 2024"
    enable-dynamic-parameter: true
    enable-request-cache: true
    enable-filter-multipart-apis: true
    enable-filter-multipart-api-method-type: POST
```

### 2. OpenAPI 配置

#### 基本信息配置

```java
@Configuration
@EnableOpenApi
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("系统 API 文档")
                .description("基于 Spring Boot 的系统 API 接口文档")
                .version("1.0.0")
                .contact(new Contact()
                    .name("开发团队")
                    .email("dev@example.com")
                    .url("https://example.com"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(Arrays.asList(
                new Server().url("http://localhost:8080").description("开发环境"),
                new Server().url("https://api.example.com").description("生产环境")
            ));
    }
}
```

### 3. API 文档注解

#### Controller 注解

```java
@RestController
@RequestMapping("/api/users")
@Api(tags = "用户管理")
@ApiResponses({
    @ApiResponse(code = 200, message = "操作成功"),
    @ApiResponse(code = 400, message = "请求参数错误"),
    @ApiResponse(code = 500, message = "服务器内部错误")
})
public class UserController {

    @GetMapping("/{id}")
    @ApiOperation(value = "根据ID获取用户", notes = "通过用户ID获取用户详细信息")
    @ApiImplicitParam(name = "id", value = "用户ID", required = true,
                     dataType = "Long", paramType = "path")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        // 实现逻辑
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    @ApiOperation(value = "创建用户", notes = "创建新用户")
    public ResponseEntity<User> createUser(@RequestBody @Valid UserCreateRequest request) {
        // 实现逻辑
        return ResponseEntity.ok(userService.createUser(request));
    }
}
```

#### 实体类注解

```java
@Data
@ApiModel(description = "用户信息")
public class User {

    @ApiModelProperty(value = "用户ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "用户名", required = true, example = "john_doe")
    private String username;

    @ApiModelProperty(value = "邮箱地址", required = true, example = "john@example.com")
    private String email;

    @ApiModelProperty(value = "创建时间", example = "2024-01-01T00:00:00Z")
    private LocalDateTime createTime;
}
```

### 4. 分组管理

#### API 分组配置

```java
@Configuration
public class ApiGroupConfig {

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
            .group("用户管理")
            .pathsToMatch("/api/users/**")
            .build();
    }

    @Bean
    public GroupedOpenApi orderApi() {
        return GroupedOpenApi.builder()
            .group("订单管理")
            .pathsToMatch("/api/orders/**")
            .build();
    }
}
```

## 配置属性

### OpenAPIProperties

| 属性名                                | 类型      | 默认值           | 说明              |
|------------------------------------|---------|---------------|-----------------|
| `zeka-stack.openapi.enabled`       | boolean | true          | 是否启用 OpenAPI 功能 |
| `zeka-stack.openapi.title`         | String  | "API 文档"      | 文档标题            |
| `zeka-stack.openapi.description`   | String  | "系统 API 接口文档" | 文档描述            |
| `zeka-stack.openapi.version`       | String  | "1.0.0"       | API 版本          |
| `zeka-stack.openapi.contact.name`  | String  | -             | 联系人姓名           |
| `zeka-stack.openapi.contact.email` | String  | -             | 联系人邮箱           |
| `zeka-stack.openapi.contact.url`   | String  | -             | 联系人网址           |

### Knife4jProperties

| 属性名                                     | 类型      | 默认值     | 说明           |
|-----------------------------------------|---------|---------|--------------|
| `knife4j.enable`                        | boolean | true    | 是否启用 Knife4j |
| `knife4j.setting.language`              | String  | "zh_cn" | 界面语言         |
| `knife4j.setting.enable-version`        | boolean | true    | 是否显示版本信息     |
| `knife4j.setting.enable-swagger-models` | boolean | true    | 是否显示模型信息     |
| `knife4j.setting.enable-search`         | boolean | true    | 是否启用搜索功能     |
| `knife4j.setting.enable-footer`         | boolean | true    | 是否显示页脚       |

## 使用方式

### 1. 引入依赖

**使用 Knife4j**

```xml
<dependency>
    <groupId>dev.dong4j</groupId>
    <artifactId>cubo-openapi-knife4j-spring-boot-starter</artifactId>
</dependency>
```

**使用 Dubbo 文档**

```xml
<dependency>
    <groupId>dev.dong4j</groupId>
    <artifactId>cubo-openapi-dubbo-spring-boot-starter</artifactId>
</dependency>
```

### 2. 配置启用

```yaml
zeka-stack:
  openapi:
    enabled: true
    title: "系统 API 文档"
    description: "基于 Spring Boot 的系统 API 接口文档"
    version: "1.0.0"
    contact:
      name: "开发团队"
      email: "dev@example.com"
      url: "https://example.com"

knife4j:
  enable: true
  setting:
    language: "zh_cn"
    enable-version: true
    enable-swagger-models: true
    enable-search: true
    enable-footer: true
```

### 3. 访问文档

- **Knife4j UI**: http://localhost:8080/doc.html
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## 高级功能

### 1. 自定义主题

```java
@Configuration
public class CustomThemeConfig {

    @Bean
    public Knife4jOpenApiCustomizer knife4jOpenApiCustomizer() {
        return new Knife4jOpenApiCustomizer() {
            @Override
            public void customise(OpenAPI openApi) {
                openApi.addTagsItem(new Tag().name("用户管理").description("用户相关接口"));
                openApi.addTagsItem(new Tag().name("订单管理").description("订单相关接口"));
            }
        };
    }
}
```

### 2. 安全配置

```java
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityScheme securityScheme() {
        return new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .name("Authorization");
    }

    @Bean
    public SecurityRequirement securityRequirement() {
        return new SecurityRequirement().addList("Authorization");
    }
}
```

### 3. 自定义文档页面

```java
@Controller
public class CustomDocController {

    @GetMapping("/custom-doc")
    public String customDoc() {
        return "custom-doc";
    }
}
```

### 4. API 版本管理

```java
@Configuration
public class VersionConfig {

    @Bean
    public GroupedOpenApi v1Api() {
        return GroupedOpenApi.builder()
            .group("v1")
            .pathsToMatch("/api/v1/**")
            .build();
    }

    @Bean
    public GroupedOpenApi v2Api() {
        return GroupedOpenApi.builder()
            .group("v2")
            .pathsToMatch("/api/v2/**")
            .build();
    }
}
```

## 最佳实践

### 1. 文档规范

- 使用清晰的 API 描述和说明
- 提供完整的请求和响应示例
- 标注必要的参数验证规则
- 保持文档的及时更新

### 2. 分组管理

- 按功能模块进行 API 分组
- 使用有意义的组名和描述
- 合理组织 API 的层次结构

### 3. 安全考虑

- 配置适当的认证和授权
- 避免在文档中暴露敏感信息
- 使用 HTTPS 访问文档页面

### 4. 性能优化

- 合理配置文档缓存
- 避免生成过大的文档文件
- 使用分组减少单页加载内容

## 注意事项

1. **性能影响**: 文档生成会影响应用启动时间
2. **安全风险**: 生产环境需要限制文档访问权限
3. **版本兼容**: 确保与 Spring Boot 版本的兼容性
4. **内存使用**: 大型项目的文档可能占用较多内存

## 相关链接

- [OpenAPI 规范](https://swagger.io/specification/)
- [Knife4j 官方文档](https://doc.xiaominfo.com/)
- [Spring Boot OpenAPI 支持](https://springdoc.org/)
- [Swagger 注解参考](https://swagger.io/docs/open-source-tools/swagger-ui/usage/configuration/)
