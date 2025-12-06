# OkHttp 连接池配置指南

## 概述

本文档说明了如何在 `cubo-rest-spring-boot` 中配置 OkHttp 连接池参数，以优化 HTTP 客户端的性能和资源使用。

## 连接池特性

### ✅ **默认支持**

- **连接复用** - 自动复用 HTTP/HTTPS 连接
- **连接池管理** - 可配置最大空闲连接数和保活时间
- **自动清理** - 自动清理过期和无效连接
- **性能优化** - 减少连接建立开销，提高请求响应速度

### 📊 **默认配置**

```yaml
zeka-stack:
  rest:
    connection-pool:
      max-idle-connections: 5      # 最大空闲连接数
      keep-alive-duration: 5      # 连接保活时间（分钟）
```

## 配置选项

### 1. 基础超时配置

```yaml
zeka-stack:
  rest:
    connect-timeout: 3000         # 连接超时（毫秒）
    read-timeout: 5000            # 读取超时（毫秒）
    write-timeout: 5000           # 写入超时（毫秒）
```

### 2. 连接池配置

```yaml
zeka-stack:
  rest:
    connection-pool:
      max-idle-connections: 10    # 最大空闲连接数
      keep-alive-duration: 10    # 连接保活时间（分钟）
```

### 3. 完整配置示例

```yaml
server:
  port: 8080

zeka-stack:
  rest:
    # 超时配置
    connect-timeout: 5000
    read-timeout: 10000
    write-timeout: 10000

    # 连接池配置
    connection-pool:
      max-idle-connections: 20
      keep-alive-duration: 10

    # 其他配置
    enable-http2: true
    enable-container-log: false
```

## 性能调优建议

### 🚀 **高并发场景**

```yaml
zeka-stack:
  rest:
    connection-pool:
      max-idle-connections: 50    # 增加空闲连接数
      keep-alive-duration: 15    # 延长保活时间
```

### 💾 **资源受限环境**

```yaml
zeka-stack:
  rest:
    connection-pool:
      max-idle-connections: 3     # 减少空闲连接数
      keep-alive-duration: 3     # 缩短保活时间
```

### 🔄 **微服务环境**

```yaml
zeka-stack:
  rest:
    connection-pool:
      max-idle-connections: 10    # 适中的连接数
      keep-alive-duration: 8     # 适中的保活时间
```

## 技术原理

### 连接池工作流程

1. **连接建立** - 首次请求时建立新连接
2. **连接复用** - 后续请求复用已有连接
3. **连接保活** - 保持连接活跃状态
4. **连接清理** - 自动清理过期连接

### 性能优势

- **减少延迟** - 避免重复的 TCP 握手
- **提高吞吐量** - 并发请求处理能力增强
- **资源优化** - 减少系统资源消耗
- **稳定性提升** - 减少连接失败率

## 监控和调试

### 日志配置

```yaml
logging:
  level:
    dev.dong4j.zeka.starter.rest: DEBUG
```

### 连接池状态

可以通过日志查看连接池配置信息：

```
INFO  - 创建OkHttpClient，连接池配置: maxIdleConnections=20, keepAliveDuration=10 MINUTES
```

## 注意事项

### ⚠️ **配置建议**

1. **maxIdleConnections** 不宜过大，建议 5-50 之间
2. **keepAliveDuration** 不宜过长，建议 3-15 分钟
3. 根据实际负载情况调整参数
4. 监控连接池使用情况

### 🔍 **常见问题**

- **连接泄漏** - 检查是否正确关闭响应
- **性能下降** - 调整连接池参数
- **内存占用** - 监控连接池大小

## 相关链接

- [OkHttp 官方文档](https://square.github.io/okhttp/)
- [Spring Boot 配置指南](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-external-config)
- [HTTP 连接池最佳实践](https://tools.ietf.org/html/rfc7230#section-6.3)
