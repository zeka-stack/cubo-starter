# 字典组件使用指南

## 概述

本文档说明如何在业务项目中使用 `cubo-dict-spring-boot` 字典组件。

## 1. 引入依赖

在业务项目的 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>dev.dong4j</groupId>
    <artifactId>cubo-dict-spring-boot-starter</artifactId>
</dependency>
```

## 2. 配置启用

在 `application.yml` 中配置字典组件：

```yaml
zeka-stack:
  dict:
    enabled: true
    enable-cache: true
    preload-cache: true
    cache-type: MEMORY
    cache-expire-time: 3600
```

## 3. 数据库初始化

执行 `db/init.sql` 脚本初始化数据库表结构和示例数据。

## 4. 在业务代码中使用

### 4.1 创建字典服务实现

由于字典服务需要Mapper依赖，需要在业务项目中创建实现类：

```java
@Service
@AllArgsConstructor
public class BusinessDictionaryServiceImpl implements DictionaryService {

    private final DictionaryTypeMapper dictionaryTypeMapper;
    private final DictionaryValueMapper dictionaryValueMapper;
    private final DictionaryCache dictionaryCache;
    private final DictProperties properties;

    // 实现DictionaryService接口的所有方法
    // 可以直接复制DictionaryServiceImpl的实现
}
```

### 4.2 在业务Service中使用

```java
@Service
@AllArgsConstructor
public class UserService {

    private final DictionaryService dictionaryService;

    public void createUser(User user) {
        // 获取性别选项
        List<DictionaryOption> genderOptions = dictionaryService.getDictionaryOptions("gender");

        // 验证性别编码
        if (!isValidGenderCode(user.getGenderCode())) {
            throw new IllegalArgumentException("无效的性别编码");
        }

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

    private boolean isValidGenderCode(String genderCode) {
        List<DictionaryValue> values = dictionaryService.getDictionaryValues("gender");
        return values.stream()
                .anyMatch(v -> v.getCode().equals(genderCode));
    }
}
```

### 4.3 前端接口调用

```javascript
// 获取性别选项
fetch('/api/dictionary/options/gender')
  .then(response => response.json())
  .then(data => {
    console.log('性别选项:', data.data);
    // 用于填充下拉框
  });

// 获取所有字典选项
fetch('/api/dictionary/options')
  .then(response => response.json())
  .then(data => {
    console.log('所有字典选项:', data.data);
    // 用于前端初始化
  });
```

## 5. 自定义字典数据

### 5.1 添加新的字典类型

```java
@Service
@AllArgsConstructor
public class DictionaryManagementService {

    private final DictionaryService dictionaryService;

    public void addCustomDictionaryType() {
        DictionaryType type = new DictionaryType();
        type.setCode("user_status");
        type.setName("用户状态");
        type.setDescription("用户账户状态");
        type.setState(DictionaryTypeState.ENABLED);
        type.setOrder(1);
        type.setTenantId("default");
        type.setClientId("default");

        dictionaryService.saveDictionaryType(type);
    }

    public void addCustomDictionaryValues() {
        // 添加用户状态字典值
        String[][] statusData = {
            {"0", "未激活", "用户未激活状态"},
            {"1", "正常", "用户正常状态"},
            {"2", "禁用", "用户被禁用状态"},
            {"3", "锁定", "用户被锁定状态"}
        };

        for (String[] data : statusData) {
            DictionaryValue value = new DictionaryValue();
            value.setTypeCode("user_status");
            value.setCode(data[0]);
            value.setName(data[1]);
            value.setDescription(data[2]);
            value.setState(DictionaryValueState.ENABLED);
            value.setOrder(Integer.parseInt(data[0]) + 1);
            value.setTenantId("default");
            value.setClientId("default");

            dictionaryService.saveDictionaryValue(value);
        }
    }
}
```

## 6. 缓存管理

### 6.1 手动刷新缓存

```java
@Service
@AllArgsConstructor
public class CacheManagementService {

    private final DictionaryService dictionaryService;

    public void refreshDictionaryCache() {
        // 刷新指定字典类型缓存
        dictionaryService.refreshCache("gender");

        // 刷新所有缓存
        dictionaryService.refreshAllCache();
    }

    public void clearDictionaryCache() {
        // 清除指定字典类型缓存
        dictionaryService.clearCache("gender");

        // 清除所有缓存
        dictionaryService.clearAllCache();
    }
}

// 自定义字典事件监听器
@Component
public class CustomDictionaryEventListener {

    @EventListener
    @Async
    public void handleDictionaryUpdateEvent(DictionaryUpdateEvent event) {
        // 处理字典更新事件
        log.info("收到字典更新事件: typeCode={}, operationType={}, description={}",
                event.getTypeCode(), event.getOperationType(), event.getDescription());

        // 可以在这里添加自定义逻辑，比如：
        // - 发送通知给其他系统
        // - 更新相关缓存
        // - 记录审计日志
        // - 触发其他业务逻辑
    }
}
```

### 6.2 定时刷新缓存

```java
@Component
@AllArgsConstructor
public class DictionaryCacheScheduler {

    private final DictionaryService dictionaryService;

    @Scheduled(fixedRate = 300000) // 每5分钟执行一次
    public void refreshCache() {
        dictionaryService.refreshAllCache();
    }
}
```

## 7. 配置说明

| 配置项                                 | 说明        | 默认值    |
|-------------------------------------|-----------|--------|
| `zeka-stack.dict.enabled`           | 是否启用字典功能  | true   |
| `zeka-stack.dict.enable-cache`      | 是否启用缓存    | true   |
| `zeka-stack.dict.preload-cache`     | 是否启动时预热缓存 | true   |
| `zeka-stack.dict.cache-type`        | 缓存类型      | MEMORY |
| `zeka-stack.dict.cache-expire-time` | 缓存过期时间（秒） | 3600   |

## 8. 注意事项

1. **依赖注入**：DictionaryService需要在业务项目中实现，因为需要Mapper依赖
2. **多租户支持**：确保tenantId和clientId正确设置
3. **缓存一致性**：字典数据更新后会自动清除相关缓存
4. **性能优化**：建议启用缓存预热，提高首次访问性能
5. **数据安全**：字典数据支持逻辑删除，不会物理删除数据

## 9. 常见问题

### Q: 如何添加新的字典类型？

A: 通过DictionaryService的saveDictionaryType方法添加字典类型，然后添加对应的字典值。

### Q: 缓存不生效怎么办？

A: 检查配置是否正确，确保cache-type不是none，并且enable-cache为true。

### Q: 如何自定义缓存实现？

A: 实现DictionaryCache接口，并通过@ConditionalOnProperty注解配置条件。

### Q: 多租户环境下如何使用？

A: 确保在创建字典数据时正确设置tenantId和clientId，查询时会自动过滤。
