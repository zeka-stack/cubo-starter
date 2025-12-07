---
published: 2022.04.11
---

# 敏感字段加解密

## 概述

敏感字段加解密是 `cubo-mybatis-spring-boot` 模块的重要安全特性之一，它通过 `SensitiveFieldEncryptIntercepter` 和
`SensitiveFieldDecryptIntercepter` 实现了对敏感字段的自动加密和解密。该功能在数据写入数据库前自动加密敏感字段，在数据查询时自动解密，对业务代码完全透明，有效保护了敏感数据的安全。

## 设计目标

### 1. 数据安全

- **自动加密**：数据写入前自动加密敏感字段
- **自动解密**：数据查询时自动解密敏感字段
- **透明处理**：对业务代码完全透明，无需关心加解密逻辑

### 2. 灵活配置

- **注解驱动**：通过 `@SensitiveField` 注解标记敏感字段
- **可配置密钥**：支持通过配置设置加密密钥
- **可启用/禁用**：支持通过配置启用/禁用功能

### 3. 性能优化

- **按需处理**：只对标记的字段进行处理
- **高效算法**：使用 AES 加密算法，性能优异
- **最小开销**：加解密操作对性能影响很小

## 核心组件

### 1. @SensitiveField 注解

`@SensitiveField` 注解用于标记需要加密的敏感字段：

```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SensitiveField {
    /**
     * 字段描述
     */
    String value() default "";
}
```

#### 使用示例

```java
@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    @SensitiveField("手机号")
    private String phone;

    @SensitiveField("邮箱")
    private String email;

    @SensitiveField("身份证号")
    private String idCard;
}
```

### 2. SensitiveFieldEncryptIntercepter（加密拦截器）

`SensitiveFieldEncryptIntercepter` 用于在数据写入数据库前自动加密敏感字段。

#### 工作原理

```java
@Intercepts({
    @Signature(
        type = Executor.class,
        method = "update",
        args = {MappedStatement.class, Object.class}
    )
})
public class SensitiveFieldEncryptIntercepter implements Interceptor {

    private final String sensitiveKey;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();

        // 只处理 INSERT 和 UPDATE 操作
        if (SqlCommandType.INSERT.equals(sqlCommandType) ||
            SqlCommandType.UPDATE.equals(sqlCommandType)) {

            Object parameter = invocation.getArgs()[1];

            // 加密敏感字段
            encryptSensitiveFields(parameter, sqlCommandType);
        }

        return invocation.proceed();
    }

    private void encryptField(Field[] declaredFields, Object parameter,
                             SqlCommandType sqlCommandType) {
        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(SensitiveField.class)) {
                // 获取字段值
                Object fieldValue = ReflectionUtils.getFieldValue(parameter, field.getName());

                if (!StringUtils.isEmpty(fieldValue)) {
                    // 使用 AES 加密
                    byte[] encrypt = AesUtils.encrypt(String.valueOf(fieldValue), this.sensitiveKey);
                    // Base64 编码
                    String encryptStr = Base64Utils.encodeToString(encrypt);
                    // 设置加密后的值
                    ReflectionUtils.setFieldValue(parameter, field.getName(), encryptStr);
                }
            }
        }
    }
}
```

#### 加密流程

```
数据写入请求
    ↓
加密拦截器拦截
    ↓
识别 @SensitiveField 注解的字段
    ↓
获取字段原始值
    ↓
AES 加密
    ↓
Base64 编码
    ↓
设置加密后的值
    ↓
继续执行 SQL
```

### 3. SensitiveFieldDecryptIntercepter（解密拦截器）

`SensitiveFieldDecryptIntercepter` 用于在数据查询时自动解密敏感字段。

#### 工作原理

```java
@Intercepts({
    @Signature(
        type = Executor.class,
        method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}
    )
})
public class SensitiveFieldDecryptIntercepter implements Interceptor {

    private final String sensitiveKey;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 执行查询
        Object result = invocation.proceed();

        // 解密查询结果
        if (result != null) {
            decryptSensitiveFields(result);
        }

        return result;
    }

    private void process(Object o) {
        // 递归处理对象的所有字段
        ReflectionUtils.doWithFields(o.getClass(), field -> {
            Object fieldValue = ReflectionUtils.getFieldValue(o, field.getName());
            if (fieldValue != null) {
                this.process(fieldValue); // 递归处理嵌套对象
            }
        }, field -> o instanceof BaseDTO &&
                   field.getAnnotation(SensitiveBody.class) != null);

        // 处理标记为 @SensitiveField 的字段
        ReflectionUtils.doWithFields(o.getClass(), field -> {
            Object fieldValue = ReflectionUtils.getFieldValue(o, field.getName());
            if (!StringUtils.isEmpty(fieldValue)) {
                try {
                    // Base64 解码
                    byte[] decoded = Base64Utils.decodeFromString(String.valueOf(fieldValue));
                    // AES 解密
                    String decrypt = AesUtils.decryptToStr(decoded, this.sensitiveKey);
                    // 设置解密后的值
                    ReflectionUtils.setFieldValue(o, field.getName(), decrypt);
                } catch (Exception e) {
                    log.debug("敏感字段解密异常, fieldValue={}, exception={}",
                        fieldValue, e.getMessage());
                }
            }
        }, field -> field.getAnnotation(SensitiveField.class) != null);
    }
}
```

#### 解密流程

```
查询结果返回
    ↓
解密拦截器拦截
    ↓
识别 @SensitiveField 注解的字段
    ↓
获取字段加密值
    ↓
Base64 解码
    ↓
AES 解密
    ↓
设置解密后的值
    ↓
返回结果
```

## 加密算法

### 1. AES 加密

框架使用 AES（Advanced Encryption Standard）加密算法，这是一种对称加密算法，具有以下特点：

- **安全性高**：AES 是当前最安全的加密算法之一
- **性能优异**：加密解密速度快，适合大量数据加密
- **标准化**：被广泛采用，兼容性好

### 2. Base64 编码

加密后的字节数组使用 Base64 编码转换为字符串，便于存储到数据库：

- **可存储性**：Base64 编码后的字符串可以安全存储到数据库
- **可读性**：编码后的字符串不包含特殊字符
- **标准化**：Base64 是标准编码方式

### 3. 加密流程

```
原始数据: "13800138000"
    ↓
AES 加密: [字节数组]
    ↓
Base64 编码: "U2FsdGVkX1..."
    ↓
存储到数据库
```

### 4. 解密流程

```
从数据库读取: "U2FsdGVkX1..."
    ↓
Base64 解码: [字节数组]
    ↓
AES 解密: "13800138000"
    ↓
返回给业务代码
```

## 配置说明

### 1. 基本配置

```yaml
zeka-stack:
  mybatis:
    enabled: true
    # 启用敏感字段加解密
    enable-sensitive: true
    # 加密密钥（必须配置）
    sensitive-key: "your-secret-key-32-chars"
```

### 2. 密钥配置

**重要**：加密密钥必须妥善保管，建议：

- 使用环境变量配置密钥
- 使用配置中心管理密钥
- 定期轮换密钥
- 不同环境使用不同密钥

```yaml
# 使用环境变量
zeka-stack:
  mybatis:
    sensitive-key: ${MYBATIS_SENSITIVE_KEY}

# 或使用配置中心
# 从 Nacos、Apollo 等配置中心读取
```

### 3. 禁用加解密

```yaml
zeka-stack:
  mybatis:
    enable-sensitive: false  # 禁用敏感字段加解密
```

## 使用示例

### 1. 基本使用

```java
@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    @SensitiveField("手机号")
    private String phone;

    @SensitiveField("邮箱")
    private String email;

    @SensitiveField("身份证号")
    private String idCard;
}

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public void createUser(User user) {
        // 写入时自动加密敏感字段
        // user.getPhone() = "13800138000"
        // 存储到数据库时会被加密为 "U2FsdGVkX1..."
        userMapper.insert(user);
    }

    public User getUser(Long id) {
        // 查询时自动解密敏感字段
        // 数据库中的 "U2FsdGVkX1..." 会被解密为 "13800138000"
        User user = userMapper.selectById(id);
        // user.getPhone() = "13800138000"（已解密）
        return user;
    }
}
```

### 2. 嵌套对象支持

```java
@Data
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id;

    @SensitiveField("收货人手机号")
    private String receiverPhone;

    @Valid
    private User user;  // 嵌套对象
}

@Data
public class User {

    @SensitiveField("手机号")
    private String phone;
}
```

### 3. 批量操作支持

```java
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public void batchCreateUsers(List<User> users) {
        // 批量插入时，每个对象的敏感字段都会自动加密
        userMapper.insertBatch(users);
    }

    public List<User> getUsers(List<Long> ids) {
        // 批量查询时，每个对象的敏感字段都会自动解密
        return userMapper.selectBatchIds(ids);
    }
}
```

## 设计优势

### 1. 透明处理

- **零侵入**：业务代码无需关心加解密逻辑
- **自动处理**：加解密过程完全自动化
- **易于使用**：只需添加注解即可

### 2. 安全性

- **强加密算法**：使用 AES 加密算法
- **密钥管理**：支持灵活的密钥配置
- **数据保护**：有效保护敏感数据安全

### 3. 性能

- **按需处理**：只对标记的字段进行处理
- **高效算法**：AES 加密性能优异
- **最小开销**：加解密操作对性能影响很小

### 4. 灵活性

- **可配置**：支持启用/禁用功能
- **可扩展**：支持自定义加密算法
- **兼容性**：与 MyBatis Plus 完全兼容

## 注意事项

### 1. 密钥管理

- **密钥安全**：密钥必须妥善保管，不能泄露
- **密钥长度**：建议使用 32 字符的密钥
- **密钥轮换**：定期轮换密钥，提高安全性

### 2. 数据迁移

- **已加密数据**：如果数据库中已有加密数据，需要确保密钥一致
- **数据解密**：迁移时需要先解密数据
- **密钥变更**：密钥变更需要重新加密所有数据

### 3. 性能考虑

- 加解密会带来一定的性能开销
- 对于高频接口，需要评估性能影响
- 建议只对真正的敏感字段使用

### 4. 兼容性

- 与 MyBatis Plus 完全兼容
- 支持所有标准的 MyBatis Plus 操作
- 支持嵌套对象和集合类型

## 最佳实践

### 1. 字段选择

- **敏感字段**：只对真正的敏感字段使用加密
- **非敏感字段**：不要对非敏感字段使用加密
- **性能考虑**：避免对高频访问的字段使用加密

### 2. 密钥管理

- **环境变量**：使用环境变量配置密钥
- **配置中心**：使用配置中心管理密钥
- **密钥轮换**：定期轮换密钥

### 3. 数据备份

- **加密数据**：备份时注意加密数据的处理
- **密钥备份**：妥善备份加密密钥
- **恢复测试**：定期测试数据恢复流程

## 总结

敏感字段加解密通过拦截器机制实现了对敏感字段的自动加密和解密，有效保护了敏感数据的安全。这种设计不仅提供了强大的数据保护能力，还保持了良好的性能和易用性，是构建安全数据访问层的重要基础。

