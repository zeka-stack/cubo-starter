# Cubo Dict Spring Boot

## 概述

`cubo-dict-spring-boot` 是 Zeka Stack 项目中的通用字典组件，提供了完整的字典数据管理功能。该组件支持字典类型和字典值的增删改查，提供了缓存机制和前端接口，大大简化了字典数据的使用。

## 主要功能

### 1. 字典管理

- 字典类型的增删改查
- 字典值的增删改查
- 支持多租户和客户端隔离

### 2. 缓存支持

- 内存缓存（默认）
- 无操作缓存（禁用缓存）
- 支持缓存预热
- 延迟双删策略保证数据一致性
- Spring Event事件驱动缓存更新

### 3. 前端接口

- 提供字典选项接口供前端下拉框使用
- 支持单个字典类型查询
- 支持批量获取所有字典选项

### 4. 配置灵活

- 支持启用/禁用缓存
- 支持缓存预热配置
- 支持缓存过期时间配置

## 模块结构

```
cubo-dict-spring-boot/
├── cubo-dict-spring-boot-core/             # 核心功能模块
│   ├── cache/                              # 缓存相关
│   ├── controller/                         # 控制器
│   ├── entity/                             # 实体类
│   ├── service/                            # 服务层
│   └── db/                                 # 数据库初始化脚本
├── cubo-dict-spring-boot-autoconfigure/    # 自动配置模块
└── cubo-dict-spring-boot-starter/          # 启动器模块
```

## 快速开始

### 1. 引入依赖

```xml
<dependency>
    <groupId>dev.dong4j</groupId>
    <artifactId>cubo-dict-spring-boot-starter</artifactId>
</dependency>
```

### 2. 配置启用

```yaml
zeka-stack:
  dict:
    enabled: true
    enable-cache: true
    preload-cache: true
    cache-type: memory
    cache-expire-time: 3600
    table-prefix: sys_
    cache-refresh-delay: 100
    enable-consistency-check: true
    consistency-check-interval: 300
```

### 3. 数据库初始化

组件提供了数据库初始化脚本 `db/init.sql`，包含：

- 字典类型表 `sys_dictionary_type`
- 字典值表 `sys_dictionary_value`
- 示例数据（性别字典）

### 4. 使用示例

#### 4.1 在Service中使用

```java
@Service
public class UserService {

    @Autowired
    private DictionaryService dictionaryService;

    public void createUser(User user) {
        // 获取性别选项
        List<DictionaryOption> genderOptions = dictionaryService.getDictionaryOptions("gender");
        // 业务逻辑...
    }

    public String getGenderName(String genderCode) {
        List<DictionaryValue> values = dictionaryService.getDictionaryValues("gender");
        return values.stream()
                .filter(v -> v.getCode().equals(genderCode))
                .findFirst()
                .map(DictionaryValue::getName)
                .orElse("未知");
    }
}
```

#### 4.2 前端接口调用

```javascript
// 获取性别选项
GET /api/dictionary/options/gender

// 响应示例
{
  "code": "success",
  "data": [
    {
      "value": "0",
      "label": "未知",
      "description": "性别未知",
      "disabled": false,
      "sortOrder": 1
    },
    {
      "value": "1",
      "label": "男",
      "description": "男性",
      "disabled": false,
      "sortOrder": 2
    },
    {
      "value": "2",
      "label": "女",
      "description": "女性",
      "disabled": false,
      "sortOrder": 3
    }
  ]
}

// 获取所有字典选项
GET /api/dictionary/options

// 刷新缓存
POST /api/dictionary/refresh/gender
POST /api/dictionary/refresh
```

## 配置说明

| 属性名                                   | 类型                  | 默认值    | 说明                |
|---------------------------------------|---------------------|--------|-------------------|
| `zeka-stack.dict.enabled`             | boolean             | true   | 是否启用字典功能          |
| `zeka-stack.dict.enable-cache`        | boolean             | true   | 是否启用缓存            |
| `zeka-stack.dict.preload-cache`       | boolean             | true   | 是否启动时预热缓存         |
| `zeka-stack.dict.cache-type`          | DictionaryCacheType | MEMORY | 缓存类型：MEMORY, NONE |
| `zeka-stack.dict.cache-expire-time`   | long                | 3600   | 缓存过期时间（秒）         |
| `zeka-stack.dict.cache-refresh-delay` | long                | 100    | 缓存刷新延迟时间（毫秒）      |

## 缓存策略

### 1. 缓存更新策略

- **读操作**：先查缓存，缓存未命中则查数据库并更新缓存
- **写操作**：先更新数据库，再删除缓存（延迟双删）
- **最终一致性**：允许短暂的数据不一致，通过 TTL 和主动刷新保证最终一致
- **事件驱动**：通过Spring Event机制发布字典更新事件，支持异步缓存更新

### 2. 缓存类型

- **MEMORY**：基于 ConcurrentHashMap 的内存缓存，支持过期清理
- **NONE**：无操作缓存，禁用缓存功能

## 扩展性

### 1. 自定义缓存实现

```java
@Component
@ConditionalOnProperty(prefix = "zeka-stack.dict", name = "cache-type", havingValue = "redis")
public class RedisDictionaryCache implements DictionaryCache {
    // 实现Redis缓存逻辑
}
```

### 2. 自定义字典数据加载器

```java
@Component
public class CustomDictionaryDataLoader implements DictionaryDataLoader {
    @Override
    public void loadInitialData() {
        // 加载自定义字典数据
    }
}
```

### 3. 自定义字典事件监听器

```java
@Component
public class CustomDictionaryEventListener {

    @EventListener
    @Async
    public void handleDictionaryUpdateEvent(DictionaryUpdateEvent event) {
        // 处理字典更新事件
        log.info("收到字典更新事件: typeCode={}, operationType={}",
                event.getTypeCode(), event.getOperationType());

        // 可以在这里添加自定义逻辑，比如：
        // - 发送通知
        // - 更新其他系统
        // - 记录审计日志
    }
}
```

## 注意事项

1. **数据一致性**：字典数据采用最终一致性策略，允许短暂的数据不一致
2. **缓存预热**：建议在生产环境启用缓存预热，提高首次访问性能
3. **多租户支持**：组件支持多租户和客户端隔离，确保数据安全
4. **性能优化**：大量字典数据时建议调整缓存过期时间和清理间隔

## 相关链接

- [Zeka Stack 框架文档](../../README.md)
- [Spring Boot 自动配置](https://docs.spring.io/spring-boot/docs/current/reference/html/auto-configuration-classes.html)
- [MyBatis Plus 文档](https://baomidou.com/)
