package dev.dong4j.zeka.starter.mybatis.injector.methods;

import dev.dong4j.zeka.starter.mybatis.injector.MybatisSqlMethod;
import java.io.Serial;

/**
 * REPLACE INTO 方法注入器
 *
 * 该类用于向 Mapper 接口注入 REPLACE INTO 方法，实现替换插入功能。
 * 继承自 AbstractInsertMethod，提供了 MySQL 的 REPLACE INTO 语法支持。
 *
 * 主要功能：
 * 1. 注入 replace 方法到 BaseDao 接口
 * 2. 生成 REPLACE INTO SQL 语句
 * 3. 如果数据不存在则插入，如果存在则先删除再插入
 * 4. 支持选择性字段插入，只插入非空字段
 *
 * 工作原理：
 * - 如果数据不存在：执行插入操作
 * - 如果数据已存在（基于主键或唯一索引）：先删除旧数据，再插入新数据
 *
 * 使用场景：
 * - 数据更新插入（upsert）操作
 * - 配置数据的覆盖更新
 * - 缓存数据的刷新操作
 *
 * 注意：该操作要求表必须有主键或唯一索引约束
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 14:53
 * @since 1.0.0
 */
public class Replace extends AbstractInsertMethod {

    @Serial
    private static final long serialVersionUID = 2783717166289508211L;

    /**
     * 构造方法
     *
     * 创建 Replace 方法注入器实例，使用 REPLACE_ONE 作为 SQL 方法类型。
     * 该构造方法会调用父类的构造方法，完成方法注入的初始化工作。
     *
     * @since 1.0.0
     */
    public Replace() {
        super(MybatisSqlMethod.REPLACE_ONE);
    }
}

