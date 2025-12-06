# Spring Boot 3.x 升级指南

## 概述

本文档描述了将 `cubo-rest-spring-boot` 模块从 Spring Boot 2.x 升级到 Spring Boot 3.x 时的主要变更。

## 主要变更

### 1. OkHttp3ClientHttpRequestFactory 被删除

**问题描述：**
在 Spring Framework 6.1 中，`OkHttp3ClientHttpRequestFactory` 类已被删除。这导致在 Spring Boot 3.x 中使用 OkHttp 作为 RestTemplate 的 HTTP
客户端时出现编译错误。

**解决方案：**
创建了自定义的 `OkHttpClientHttpRequestFactory` 类来替代已废弃的 `OkHttp3ClientHttpRequestFactory`。

### 2. 新增文件

- `OkHttpClientHttpRequestFactory.java` - 自定义的 OkHttp 客户端工厂实现

### 3. 修改文件

- `RestTemplateAutoConfiguration.java` - 更新了 `clientHttpRequestFactory` 方法，使用自定义的工厂类

## 技术细节

### OkHttpClientHttpRequestFactory 特性

1. **完全兼容 Spring Boot 3.x** - 实现了 `ClientHttpRequestFactory` 接口
2. **支持 SSL 配置** - 可以配置忽略 HTTPS 证书验证
3. **超时配置** - 支持连接、读取、写入超时设置
4. **连接池支持** - 可配置最大空闲连接数和保活时间
5. **请求头处理** - 正确处理 HTTP 请求头
6. **响应处理** - 完整实现 `ClientHttpResponse` 接口

### 使用方式

```java
@Bean
@ConditionalOnClass(OkHttpClient.class)
public ClientHttpRequestFactory clientHttpRequestFactory(RestProperties restProperties) {
    // 创建支持SSL的OkHttpClient实例
    OkHttpClient okHttpClient = this.getUnsafeOkHttpClient()
        .newBuilder()
        .connectTimeout(restProperties.getConnectTimeout(), TimeUnit.MILLISECONDS)
        .readTimeout(restProperties.getReadTimeout(), TimeUnit.MILLISECONDS)
        .writeTimeout(restProperties.getWriteTimeout(), TimeUnit.MILLISECONDS)
        .build();

    // 使用自定义的OkHttpClientHttpRequestFactory
    return new OkHttpClientHttpRequestFactory(okHttpClient);
}
```

## 注意事项

1. **性能影响** - 自定义实现可能在某些场景下性能略低于原生实现
2. **功能完整性** - 确保所有必要的 HTTP 功能都已实现
3. **测试覆盖** - 建议增加单元测试来验证自定义实现的正确性

## 未来改进

1. **监控和指标** - 可以添加请求/响应监控
2. **连接池优化** - 进一步优化 OkHttp 连接池配置
3. **WebClient 迁移** - 考虑逐步迁移到 Spring WebFlux 的 WebClient

## 相关链接

- [Spring Boot 3.x 迁移指南](https://docs.spring.io/spring-boot/docs/3.0.0/reference/html/migration.html)
- [Spring Framework 6.x 变更日志](https://github.com/spring-projects/spring-framework/releases)
- [OkHttp 官方文档](https://square.github.io/okhttp/)
