---
published: 2022.04.04
---

# XSS 防护

## 概述

XSS（Cross-Site Scripting，跨站脚本攻击）防护是 `cubo-rest-spring-boot` 模块的重要安全特性之一。该模块通过多层防护机制，在请求入口对潜在的恶意脚本进行过滤和转义，有效防止
XSS 攻击，保护应用和用户数据的安全。

## 设计目标

### 1. 多层防护

- **Filter 层防护**：在请求入口进行过滤
- **Request Wrapper 层防护**：包装请求对象，拦截所有参数获取
- **HTML 过滤器层防护**：专业的 HTML 过滤功能

### 2. 零侵入

- **自动启用**：引入依赖即可使用，无需额外配置
- **透明处理**：对业务代码完全透明，不影响正常业务逻辑
- **灵活控制**：支持通过配置排除特定路径

### 3. 性能优化

- **智能过滤**：只对需要过滤的请求进行处理
- **缓存优化**：避免重复过滤操作
- **最小开销**：过滤操作对性能影响很小

## 核心组件

### 1. XssFilter

`XssFilter` 是 XSS 防护的第一道防线，它在 Filter 层对请求进行拦截和处理。

#### 工作原理

```java
@AllArgsConstructor
public class XssFilter implements Filter {

    private final List<String> excludePatterns;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                        FilterChain chain) throws IOException, ServletException {
        String path = ((HttpServletRequest) request).getServletPath();

        // 检查是否在排除列表中
        if (this.excludePatterns.stream().anyMatch(path::contains)) {
            chain.doFilter(request, response);
        } else {
            // 包装请求对象，进行 XSS 过滤
            if (request instanceof CacheRequestEnhanceWrapper) {
                chain.doFilter(new XssHttpServletRequestWrapper(
                    ((CacheRequestEnhanceWrapper) request).getCachingRequestWrapper()),
                    response);
            } else {
                chain.doFilter(new XssHttpServletRequestWrapper(
                    new ContentCachingRequestWrapper((HttpServletRequest) request)),
                    response);
            }
        }
    }
}
```

#### 过滤流程

```
请求进入
    ↓
检查路径是否在排除列表
    ↓
是 → 直接放行
    ↓
否 → 包装请求对象
    ↓
XssHttpServletRequestWrapper 处理
    ↓
继续过滤器链
```

### 2. XssHttpServletRequestWrapper

`XssHttpServletRequestWrapper` 是 XSS 防护的核心实现，它包装了原始的 `HttpServletRequest`，拦截所有参数获取方法，对参数值进行 XSS 过滤。

#### 核心方法

```java
@Slf4j
public class XssHttpServletRequestWrapper extends CacheRequestEnhanceWrapper {

    private static final HtmlFilter HTML_FILTER = new HtmlFilter();
    private static final String SQL_KEY = "and|exec|insert|select|delete|update|count|*|%|chr|mid|master|truncate|char|declare|;|or|-|+";
    private static final Set<String> NOT_ALLOWED_KEY_WORDS = Sets.newHashSet();
    private static final String REPLACED_STRING = "INVALID";

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(xssEncode(name));
        if (StringUtils.isNotBlank(value)) {
            value = xssEncode(value);
        }
        return value;
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(xssEncode(name));
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                values[i] = xssEncode(values[i]);
            }
        }
        return values;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> parameterMap = super.getParameterMap();
        Map<String, String[]> filteredMap = new HashMap<>();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = xssEncode(entry.getKey());
            String[] values = entry.getValue();
            if (values != null) {
                for (int i = 0; i < values.length; i++) {
                    values[i] = xssEncode(values[i]);
                }
            }
            filteredMap.put(key, values);
        }
        return filteredMap;
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(xssEncode(name));
        if (StringUtils.isNotBlank(value)) {
            value = xssEncode(value);
        }
        return value;
    }

    @Override
    public ServletInputStream getInputStream() {
        // 对请求体进行 XSS 过滤
        String requestStr = WebUtils.getRequestStr(this.cachingRequestWrapper, this.body);
        if (StringUtils.isBlank(requestStr)) {
            return super.getInputStream();
        }

        // 只对 JSON 类型的请求体进行过滤
        if (BasicConstant.JSON.equals(MediaType.valueOf(
            super.getHeader(HttpHeaders.CONTENT_TYPE)).getSubtype())) {
            requestStr = xssEncode(requestStr);
            return WebUtils.getCacheInputStream(requestStr.getBytes(Charsets.UTF_8));
        }

        return super.getInputStream();
    }

    private static String xssEncode(String input) {
        if (StringUtils.isBlank(input)) {
            return input;
        }
        // 先过滤 SQL 关键字
        String cleaned = cleanSqlKeyWords(input);
        // 再使用 HTML 过滤器过滤
        return HTML_FILTER.filter(cleaned);
    }

    private static String cleanSqlKeyWords(String input) {
        String lowerInput = input.toLowerCase();
        for (String keyword : NOT_ALLOWED_KEY_WORDS) {
            if (lowerInput.contains(keyword)) {
                return REPLACED_STRING;
            }
        }
        return input;
    }
}
```

### 3. HtmlFilter

`HtmlFilter` 是专业的 HTML 过滤工具，基于 Cal Hendersen 的 PHP 版本移植而来，提供了强大的 HTML 过滤能力。

#### 核心功能

- **HTML 标签过滤**：移除或转义危险的 HTML 标签
- **脚本过滤**：移除 `<script>`、`<iframe>` 等危险标签
- **事件属性过滤**：移除 `onclick`、`onerror` 等事件属性
- **JavaScript 过滤**：移除 `javascript:` 协议链接

#### 过滤规则

```java
public final class HtmlFilter {

    /**
     * 过滤 HTML 内容，移除潜在的 XSS 攻击代码
     *
     * @param input 原始 HTML 内容
     * @return 过滤后的安全内容
     */
    public String filter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        // 执行 HTML 过滤逻辑
        // 移除危险标签、事件属性、JavaScript 代码等
        return filteredContent;
    }
}
```

## 防护机制

### 1. 多层防护策略

#### 第一层：路径过滤

在 `XssFilter` 中检查请求路径，排除不需要过滤的路径：

```java
if (this.excludePatterns.stream().anyMatch(path::contains)) {
    chain.doFilter(request, response);
}
```

#### 第二层：参数过滤

在 `XssHttpServletRequestWrapper` 中拦截所有参数获取方法：

- `getParameter(String name)`：单个参数
- `getParameterValues(String name)`：多个参数值
- `getParameterMap()`：所有参数
- `getHeader(String name)`：请求头

#### 第三层：请求体过滤

对请求体（特别是 JSON 格式）进行过滤：

```java
@Override
public ServletInputStream getInputStream() {
    // 只对 JSON 类型的请求体进行过滤
    if (BasicConstant.JSON.equals(MediaType.valueOf(
        super.getHeader(HttpHeaders.CONTENT_TYPE)).getSubtype())) {
        String requestStr = WebUtils.getRequestStr(...);
        requestStr = xssEncode(requestStr);
        return WebUtils.getCacheInputStream(requestStr.getBytes(Charsets.UTF_8));
    }
    return super.getInputStream();
}
```

### 2. SQL 关键字过滤

除了 XSS 防护，还提供了 SQL 关键字过滤，防止 SQL 注入攻击：

```java
private static final String SQL_KEY = "and|exec|insert|select|delete|update|count|*|%|chr|mid|master|truncate|char|declare|;|or|-|+";

private static String cleanSqlKeyWords(String input) {
    String lowerInput = input.toLowerCase();
    for (String keyword : NOT_ALLOWED_KEY_WORDS) {
        if (lowerInput.contains(keyword)) {
            return REPLACED_STRING; // 替换为 "INVALID"
        }
    }
    return input;
}
```

## 配置说明

### 1. 基本配置

```yaml
zeka-stack:
  rest:
    xss:
      enable-xss-filter: true  # 是否启用 XSS 过滤器
      exclude-patterns:        # 排除的路径模式
        - /api/files/upload
        - /api/images
```

### 2. 排除路径配置

可以通过配置排除特定路径，这些路径不会被 XSS 过滤器处理：

```yaml
zeka-stack:
  rest:
    xss:
      exclude-patterns:
        - /api/files/upload    # 文件上传接口
        - /api/images          # 图片接口
        - /api/rich-text       # 富文本接口
```

### 3. 禁用 XSS 过滤

```yaml
zeka-stack:
  rest:
    xss:
      enable-xss-filter: false  # 禁用 XSS 过滤器
```

## 使用示例

### 1. 基本使用

引入依赖后，XSS 防护会自动启用，无需额外配置：

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @PostMapping
    public User createUser(@RequestBody UserCreateRequest request) {
        // 请求参数会自动进行 XSS 过滤
        // 例如：request.getUsername() 返回的值已经被过滤
        return userService.createUser(request);
    }
}
```

### 2. 排除特定路径

对于需要保留原始 HTML 内容的接口（如富文本编辑器），可以配置排除：

```yaml
zeka-stack:
  rest:
    xss:
      exclude-patterns:
        - /api/rich-text/save    # 富文本保存接口
```

### 3. 获取原始值

如果需要获取未过滤的原始值，可以通过 `@OriginalRequest` 注解（如果支持）或直接访问原始请求：

```java
@PostMapping("/rich-text")
public void saveRichText(@RequestBody String content, HttpServletRequest request) {
    // 对于排除路径，可以直接获取原始内容
    // 但需要注意安全处理
}
```

## 过滤示例

### 1. 脚本标签过滤

**输入**：

```html
<script>alert('XSS')</script>
```

**输出**：

```html
（被完全移除）
```

### 2. 事件属性过滤

**输入**：

```html
<img src="image.jpg" onclick="alert('XSS')" />
```

**输出**：

```html
<img src="image.jpg" />
```

### 3. JavaScript 协议过滤

**输入**：

```html
<a href="javascript:alert('XSS')">链接</a>
```

**输出**：

```html
<a href="">链接</a>
```

### 4. SQL 关键字过滤

**输入**：

```
admin' OR '1'='1
```

**输出**：

```
INVALID
```

## 设计优势

### 1. 多层防护

- **Filter 层**：在请求入口进行拦截
- **Wrapper 层**：拦截所有参数获取
- **HTML 过滤层**：专业的 HTML 过滤

### 2. 零侵入

- **自动启用**：引入依赖即可使用
- **透明处理**：对业务代码完全透明
- **无需修改**：不需要修改现有代码

### 3. 性能优化

- **智能过滤**：只对需要过滤的请求处理
- **路径排除**：支持排除特定路径
- **最小开销**：过滤操作性能开销很小

### 4. 灵活配置

- **可配置开关**：支持启用/禁用
- **路径排除**：支持排除特定路径
- **自定义规则**：支持扩展过滤规则

## 注意事项

### 1. 性能考虑

- XSS 过滤会带来一定的性能开销，但影响很小
- 对于高频接口，可以考虑排除过滤
- 生产环境建议启用 XSS 过滤

### 2. 兼容性

- 与 Spring MVC 完全兼容
- 不影响正常的业务逻辑
- 支持所有标准的请求处理

### 3. 安全性

- XSS 过滤是安全的重要防线，但不是唯一防线
- 还需要配合其他安全措施（如 CSP、输入验证等）
- 对于富文本内容，需要特殊处理

### 4. 富文本处理

对于需要保留 HTML 内容的场景（如富文本编辑器），建议：

- 配置路径排除
- 使用专业的 HTML 清理库（如 JSoup）
- 白名单方式允许特定标签和属性

## 最佳实践

### 1. 启用 XSS 过滤

- 生产环境必须启用 XSS 过滤
- 开发环境也建议启用，及早发现问题

### 2. 合理配置排除

- 只对真正需要保留 HTML 的接口配置排除
- 排除的接口需要额外的安全处理
- 定期审查排除列表

### 3. 配合其他安全措施

- 使用 HTTPS 传输
- 设置 CSP（Content Security Policy）头
- 进行输入验证
- 输出转义

### 4. 监控和日志

- 记录被过滤的请求
- 监控过滤统计信息
- 定期审查安全日志

## 总结

XSS 防护通过多层防护机制实现了对 XSS 攻击的有效防护，确保了应用和用户数据的安全。这种设计不仅提供了强大的安全防护能力，还保持了良好的性能和易用性，是构建安全
REST API 的重要基础。

