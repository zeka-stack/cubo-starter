---
published: 2022.04.04
---

# 统一响应封装

本说明以当前 Servlet 源码为准，不适用于 Reactive 的自动包装承诺。业务不复制 Advice，使用既有 RestControllerWrapper / ServletController，直接返回 DTO、集合、IPage 或 void；已有显式 Result 返回保持一致。

## 包装条件与真实实现

ResponseWrapperAdvice.supports 同时要求 RestUtils.zekaClass 和 supportsAdvice。包名前缀默认为 dev.dong4j.zeka；其他根包需要 JVM -DPARENT_PACKAGE_NAME 配置并验证接口。

以下是实现摘录，省略 import 和方法注释，仅用于解释机制：

```java
    public boolean supports(@NotNull MethodParameter returnType, @NotNull Class<? extends HttpMessageConverter<?>> converterType) {
        return RestUtils.zekaClass(returnType) && RestUtils.supportsAdvice(returnType);
    }

    public Object beforeBodyWrite(Object body,
                                  @NotNull MethodParameter returnType,
                                  @NotNull MediaType selectedContentType,
                                  @NotNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @NotNull ServerHttpRequest request,
                                  @NotNull ServerHttpResponse response) {

        OriginalResponse originalResponse = Objects.requireNonNull(returnType.getMethod()).getAnnotation(OriginalResponse.class);
        // 检查是否使用了 @OriginalResponse 注解以及 Content-Type 是否为 JSON
        if (originalResponse == null && selectedContentType.equalsTypeAndSubtype(MediaType.APPLICATION_JSON)) {
            // 没有使用 @OriginalResponse 注解且 Content-Type 为 application/json 时才包装
            return this.wrapper(body);
        }
        // 其他情况直接返回原始结果
        return body;
    }

    private Object wrapper(Object body) {
        if (body instanceof Result) {
            // 已经是 Result 类型，直接返回，避免重复包装
            log.trace("请求响应已使用 Result 包装, 原始响应: [{}]", body);
            return body;
        } else {
            if (body == null) {
                // null 值包装为成功但无数据的结果
                return R.succeed();
            }
            // 非 null 值包装为成功并返回数据的结果
            log.trace("重写请求响应, 使用 Result 包装, 原始响应: [{}]", body);
            return R.succeed(body);
        }
    }
```

非 JSON 或 OriginalResponse 方法返回原值；已经是 Result 不重复包装；null 包装为成功空结果。复杂下载、流和字符串返回还需实际接口验证。

## 注解契约

- ResponseWrapper 是标记注解，没有 value 或 enabled 参数。
- OriginalResponse 是方法级标记注解，没有原因字符串参数。
- RestControllerWrapper 组合 RestController、ResponseWrapper、RequestMapping；value 为路径数组，name 为 Bean 名称。
- RestUtils 没有 isOriginalResponse 方法，跳过判断在 Advice 中完成。

## 响应与异常

Result 的 code 为 Integer，默认成功 2000，包含 success/data/message/traceId/extend；字段出现与否受空值序列化和环境影响，没有统一 timestamp 字段。显式包装使用 R.succeed(data)，不是 Result.success；指定成功消息可用 R.succeed(code, message, data)。

业务异常用 BaseCodes 或 implements LowestExceptionAssert 的枚举，通过 notNull/isTrue/fail 等断言交由统一处理器；不另建 Result / ApiResponse，也不在每个接口 catch 后拼 Map。

## 验证

覆盖正常 DTO、void、已包装 Result、参数错误、业务异常、OriginalResponse、自定义根包和非 JSON 下载。静态文档不能替代当前版本实际应用测试。
