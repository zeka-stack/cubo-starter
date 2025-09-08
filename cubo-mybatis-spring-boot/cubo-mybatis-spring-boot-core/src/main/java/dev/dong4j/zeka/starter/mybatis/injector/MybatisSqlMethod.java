package dev.dong4j.zeka.starter.mybatis.injector;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * MyBatis 扩展 SQL 方法枚举
 *
 * 该枚举定义了自定义的 SQL 方法，用于扩展 MyBatis Plus 的默认功能。
 * 每个枚举值包含方法名、描述和对应的 SQL 模板。
 *
 * 主要功能：
 * 1. 定义 INSERT IGNORE 方法，支持插入时忽略重复数据
 * 2. 定义 REPLACE INTO 方法，支持替换插入操作
 * 3. 提供 SQL 模板，用于动态生成具体的 SQL 语句
 *
 * 枚举属性：
 * - method：方法名称，对应 Mapper 接口中的方法名
 * - desc：方法描述，说明方法的功能和用途
 * - sql：SQL 模板，使用占位符支持动态表名和字段
 *
 * 使用场景：
 * - 配合 MybatisSqlInjector 注入自定义方法
 * - 支持 MySQL 特有的 INSERT IGNORE 和 REPLACE INTO 语法
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 14:52
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum MybatisSqlMethod {

    /** 插入如果中已经存在相同的记录,则忽略当前新数据 */
    INSERT_IGNORE_ONE("insertIgnore", "插入一条数据 (选择字段插入) ", "<script>\nINSERT IGNORE INTO %s %s VALUES %s\n</script>"),

    /** 表示插入替换数据,需求表中有PrimaryKey,或者unique索引,如果数据库已经存在数据,则用新数据替换,如果没有数据效果则和insert into一样 */
    REPLACE_ONE("replace", "插入一条数据 (选择字段插入) ", "<script>\nREPLACE INTO %s %s VALUES %s\n</script>");

    /** Method */
    private final String method;
    /** Desc */
    private final String desc;
    /** Sql */
    private final String sql;
}
