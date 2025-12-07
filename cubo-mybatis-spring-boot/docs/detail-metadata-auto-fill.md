---
published: 2022.04.11
---

# 元数据自动填充

## 概述

元数据自动填充是 `cubo-mybatis-spring-boot` 模块的重要特性之一，它通过 `MetaObjectHandler`
和责任链模式实现了对数据表元数据字段的自动填充。该功能可以在数据插入和更新时自动填充创建时间、更新时间、租户 ID、客户端 ID
等字段，减少业务代码中的重复操作，确保数据的一致性和完整性。

## 设计目标

### 1. 自动化填充

- **时间字段**：自动填充创建时间、更新时间
- **租户字段**：自动填充租户 ID（多租户场景）
- **客户端字段**：自动填充客户端 ID（多客户端场景）
- **扩展支持**：支持自定义字段填充

### 2. 责任链模式

- **灵活组合**：多个处理器可以灵活组合
- **独立处理**：每个处理器独立处理特定字段
- **易于扩展**：支持自定义处理器

### 3. 零侵入

- **注解驱动**：通过 `@TableField` 注解标记需要填充的字段
- **自动处理**：填充过程完全自动化
- **业务透明**：对业务代码完全透明

## 核心组件

### 1. MetaObjectChain 接口

`MetaObjectChain` 定义了元数据自动填充的责任链接口：

```java
public interface MetaObjectChain {

    /**
     * 插入数据时的字段自动填充
     */
    default void insertFill(MetaObject metaObject, MetaObjectChain chain) {
        // 子类实现具体填充逻辑
    }

    /**
     * 更新数据时的字段自动填充
     */
    default void updateFill(MetaObject metaObject, MetaObjectChain chain) {
        // 子类实现具体填充逻辑
    }

    /**
     * 根据字段名设置字段值
     */
    default void setFieldValByName(String fieldName, Object fieldVal, MetaObject metaObject) {
        // 通用字段值设置方法
    }
}
```

### 2. MetaHandlerChain（处理器链）

`MetaHandlerChain` 实现了 MyBatis Plus 的 `MetaObjectHandler` 接口，管理多个 `MetaObjectChain` 处理器：

```java
@AllArgsConstructor
public class MetaHandlerChain implements MetaObjectHandler {

    private final List<MetaObjectChain> chains;

    @Override
    public void insertFill(MetaObject metaObject) {
        // 按顺序调用所有处理器的 insertFill 方法
        for (MetaObjectChain chain : chains) {
            chain.insertFill(metaObject, getNextChain(chain));
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 按顺序调用所有处理器的 updateFill 方法
        for (MetaObjectChain chain : chains) {
            chain.updateFill(metaObject, getNextChain(chain));
        }
    }
}
```

### 3. TimeMetaObjectHandler（时间字段处理器）

`TimeMetaObjectHandler` 负责自动填充时间相关字段：

```java
@Slf4j
public class TimeMetaObjectHandler implements MetaObjectChain {

    @Override
    public void insertFill(MetaObject metaObject, MetaObjectChain chain) {
        // 插入时设置创建时间和更新时间
        this.setFieldValByName("createTime", new Date(), metaObject);
        this.setFieldValByName("updateTime", new Date(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject, MetaObjectChain chain) {
        // 更新时只设置更新时间
        this.setFieldValByName("updateTime", new Date(), metaObject);
    }
}
```

### 4. TenantIdMetaObjectHandler（租户 ID 处理器）

`TenantIdMetaObjectHandler` 负责自动填充租户 ID 字段：

```java
public class TenantIdMetaObjectHandler extends AbstractDataIdMetaObjectHandler {

    @Override
    protected void setFieldValue(MetaObject metaObject, ExpandIds expandIds) {
        // 从上下文获取租户 ID
        String tenantId = expandIds.getTenantId();
        if (StringUtils.isNotBlank(tenantId)) {
            this.setFieldValByName("tenantId", tenantId, metaObject);
        }
    }
}
```

### 5. ClientIdMetaObjectHandler（客户端 ID 处理器）

`ClientIdMetaObjectHandler` 负责自动填充客户端 ID 字段：

```java
public class ClientIdMetIdaObjectHandler extends AbstractDataIdMetaObjectHandler {

    @Override
    protected void setFieldValue(MetaObject metaObject, ExpandIds expandIds) {
        // 从上下文获取客户端 ID
        String clientId = expandIds.getClientId();
        if (StringUtils.isNotBlank(clientId)) {
            this.setFieldValByName("clientId", clientId, metaObject);
        }
    }
}
```

## 填充机制

### 1. 填充时机

#### 插入时填充

```java
@Override
public void insertFill(MetaObject metaObject) {
    // 所有处理器的 insertFill 方法会被调用
    // 按配置顺序执行
    for (MetaObjectChain chain : chains) {
        chain.insertFill(metaObject, getNextChain(chain));
    }
}
```

#### 更新时填充

```java
@Override
public void updateFill(MetaObject metaObject) {
    // 所有处理器的 updateFill 方法会被调用
    // 按配置顺序执行
    for (MetaObjectChain chain : chains) {
        chain.updateFill(metaObject, getNextChain(chain));
    }
}
```

### 2. 填充策略

#### FieldFill.INSERT

只在插入时填充：

```java
@TableField(value = "create_time", fill = FieldFill.INSERT)
private Date createTime;
```

#### FieldFill.UPDATE

只在更新时填充：

```java
@TableField(value = "update_time", fill = FieldFill.UPDATE)
private Date updateTime;
```

#### FieldFill.INSERT_UPDATE

插入和更新时都填充：

```java
@TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
private Date updateTime;
```

### 3. 填充流程

```
数据插入/更新请求
    ↓
MyBatis Plus 拦截
    ↓
检查字段的 fill 属性
    ↓
调用 MetaHandlerChain
    ↓
按顺序执行处理器链
    ↓
TimeMetaObjectHandler 填充时间
    ↓
TenantIdMetaObjectHandler 填充租户 ID
    ↓
ClientIdMetaObjectHandler 填充客户端 ID
    ↓
执行 SQL
```

## 使用示例

### 1. 时间字段自动填充

```java
@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public void createUser(User user) {
        // createTime 和 updateTime 会自动填充
        // 无需手动设置
        userMapper.insert(user);
    }

    public void updateUser(User user) {
        // updateTime 会自动更新
        // createTime 保持不变
        userMapper.updateById(user);
    }
}
```

### 2. 租户 ID 自动填充

```java
@Data
@TableName("order")
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    @TableField(value = "tenant_id", fill = FieldFill.INSERT)
    private String tenantId;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;
}

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    public void createOrder(Order order) {
        // tenantId 和 createTime 会自动填充
        // 从 ExpandIdsContext 中获取租户 ID
        orderMapper.insert(order);
    }
}
```

### 3. 客户端 ID 自动填充

```java
@Data
@TableName("product")
public class Product {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    @TableField(value = "client_id", fill = FieldFill.INSERT)
    private String clientId;
}

@Service
public class ProductService {

    @Autowired
    private ProductMapper productMapper;

    public void createProduct(Product product) {
        // clientId 会自动填充
        // 从 ExpandIdsContext 中获取客户端 ID
        productMapper.insert(product);
    }
}
```

### 4. 自定义处理器

```java
@Component
public class CustomMetaObjectHandler implements MetaObjectChain {

    @Override
    public void insertFill(MetaObject metaObject, MetaObjectChain chain) {
        // 自动填充创建人
        String currentUser = getCurrentUser();
        this.setFieldValByName("createBy", currentUser, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject, MetaObjectChain chain) {
        // 自动填充更新人
        String currentUser = getCurrentUser();
        this.setFieldValByName("updateBy", currentUser, metaObject);
    }

    private String getCurrentUser() {
        // 从安全上下文获取当前用户
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
```

## 配置说明

### 1. 自动配置

框架会自动配置所有处理器：

```java
@AutoConfiguration
static class MetaObjectAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(MetaHandlerChain.class)
    public MetaObjectHandler metaHandlerChain(List<MetaObjectChain> chains) {
        return new MetaHandlerChain(chains);
    }

    @Bean
    @ConditionalOnMissingBean(name = "timeMetaObjectHandler")
    public MetaObjectChain timeMetaObjectHandler() {
        return new TimeMetaObjectHandler();
    }

    @Bean
    @ConditionalOnMissingBean(name = "tenantMetaObjectHandler")
    public MetaObjectChain tenantMetaObjectHandler() {
        return new TenantIdMetaObjectHandler();
    }

    @Bean
    @ConditionalOnMissingBean(name = "clientMetaObjectHandler")
    public MetaObjectChain clientMetaObjectHandler() {
        return new ClientIdMetIdaObjectHandler();
    }
}
```

### 2. 自定义配置

可以通过自定义 Bean 覆盖默认配置：

```java
@Configuration
public class CustomMetaObjectConfig {

    @Bean
    public MetaObjectChain customTimeMetaObjectHandler() {
        // 自定义时间处理器
        return new CustomTimeMetaObjectHandler();
    }
}
```

## 设计优势

### 1. 责任链模式

- **灵活组合**：多个处理器可以灵活组合
- **独立处理**：每个处理器独立处理特定字段
- **易于扩展**：支持自定义处理器

### 2. 零侵入

- **注解驱动**：通过注解标记需要填充的字段
- **自动处理**：填充过程完全自动化
- **业务透明**：对业务代码完全透明

### 3. 一致性

- **统一管理**：所有元数据字段统一管理
- **确保完整**：确保数据的一致性和完整性
- **减少错误**：减少手动填充导致的错误

### 4. 可扩展性

- **易于扩展**：支持自定义处理器
- **灵活配置**：支持通过配置调整行为
- **兼容性好**：与 MyBatis Plus 完全兼容

## 注意事项

### 1. 字段命名

- 时间字段建议使用 `createTime` 和 `updateTime`
- 租户字段建议使用 `tenantId`
- 客户端字段建议使用 `clientId`

### 2. 上下文获取

- 租户 ID 和客户端 ID 需要从 `ExpandIdsContext` 中获取
- 确保在请求处理前设置上下文信息
- 使用线程局部变量保证线程安全

### 3. 性能考虑

- 自动填充会带来一定的性能开销
- 对于高频接口，需要评估性能影响
- 建议只对必要的字段使用自动填充

## 最佳实践

### 1. 字段设计

- **统一命名**：使用统一的字段命名规范
- **合理使用**：只对必要的字段使用自动填充
- **避免冗余**：避免重复填充相同字段

### 2. 处理器设计

- **单一职责**：每个处理器只处理特定字段
- **独立实现**：处理器之间相互独立
- **易于测试**：处理器应该易于单元测试

### 3. 上下文管理

- **及时设置**：在请求处理前设置上下文信息
- **及时清理**：请求处理后及时清理上下文
- **线程安全**：确保上下文管理的线程安全

## 总结

元数据自动填充通过责任链模式实现了对数据表元数据字段的自动填充，减少了业务代码中的重复操作，确保了数据的一致性和完整性。这种设计不仅提供了强大的自动填充能力，还保持了良好的扩展性和易用性，是构建高质量数据访问层的重要基础。

