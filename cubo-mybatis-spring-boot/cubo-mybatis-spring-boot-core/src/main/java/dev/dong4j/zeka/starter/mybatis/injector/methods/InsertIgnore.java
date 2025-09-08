package dev.dong4j.zeka.starter.mybatis.injector.methods;

import dev.dong4j.zeka.starter.mybatis.injector.MybatisSqlMethod;
import java.io.Serial;

/**
 * INSERT IGNORE 方法注入器
 *
 * 该类用于向 Mapper 接口注入 INSERT IGNORE 方法，实现插入时忽略重复数据的功能。
 * 继承自 AbstractInsertMethod，提供了 MySQL 的 INSERT IGNORE 语法支持。
 *
 * 主要功能：
 * 1. 注入 insertIgnore 方法到 BaseDao 接口
 * 2. 生成 INSERT IGNORE SQL 语句
 * 3. 当插入数据与现有数据冲突时，忽略当前插入操作
 * 4. 支持选择性字段插入，只插入非空字段
 *
 * 使用场景：
 * - 批量插入时避免重复数据导致的异常
 * - 幂等性操作，重复执行不会产生副作用
 * - 数据同步场景，避免重复数据的插入
 *
 * 注意：该功能依赖于表的主键或唯一索引约束
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 14:52
 * @since 1.0.0
 */
public class InsertIgnore extends AbstractInsertMethod {

    @Serial
    private static final long serialVersionUID = 7334973787192990572L;

    /**
     * 构造方法
     *
     * 创建 InsertIgnore 方法注入器实例，使用 INSERT_IGNORE_ONE 作为 SQL 方法类型。
     * 该构造方法会调用父类的构造方法，完成方法注入的初始化工作。
     *
     * @since 1.0.0
     */
    public InsertIgnore() {
        super(MybatisSqlMethod.INSERT_IGNORE_ONE);
    }
}
