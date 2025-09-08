package dev.dong4j.zeka.starter.mybatis.autoconfigure;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.IllegalSQLInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import dev.dong4j.zeka.kernel.autoconfigure.condition.ConditionalOnEnabled;
import dev.dong4j.zeka.kernel.common.constant.App;
import dev.dong4j.zeka.kernel.common.constant.ConfigDefaultValue;
import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import dev.dong4j.zeka.kernel.common.enums.LibraryEnum;
import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import dev.dong4j.zeka.kernel.common.start.ZekaComponentBean;
import dev.dong4j.zeka.starter.mybatis.handler.ClientIdMetIdaObjectHandler;
import dev.dong4j.zeka.starter.mybatis.handler.GeneralEnumTypeHandler;
import dev.dong4j.zeka.starter.mybatis.handler.MetaHandlerChain;
import dev.dong4j.zeka.starter.mybatis.handler.MetaObjectChain;
import dev.dong4j.zeka.starter.mybatis.handler.SerializableIdTypeHandler;
import dev.dong4j.zeka.starter.mybatis.handler.SqlExecuteTimeoutHandler;
import dev.dong4j.zeka.starter.mybatis.handler.TenantIdMetaObjectHandler;
import dev.dong4j.zeka.starter.mybatis.handler.TimeMetaObjectHandler;
import dev.dong4j.zeka.starter.mybatis.injector.MybatisSqlInjector;
import dev.dong4j.zeka.starter.mybatis.plugins.PerformanceInterceptor;
import dev.dong4j.zeka.starter.mybatis.plugins.SensitiveFieldDecryptIntercepter;
import dev.dong4j.zeka.starter.mybatis.plugins.SensitiveFieldEncryptIntercepter;
import dev.dong4j.zeka.starter.mybatis.util.SqlUtils;
import java.io.Serializable;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

/**
 * MyBatis Plus 自动配置类
 *
 * 该配置类负责自动配置 MyBatis Plus 相关组件，包括：
 * 1. SQL 拦截器配置（非法 SQL 拦截、攻击拦截、分页拦截等）
 * 2. 性能监控插件配置
 * 3. 敏感字段加解密插件配置
 * 4. 元数据处理器配置（自动填充创建时间、更新时间等）
 * 5. 类型处理器配置（枚举类型、ID 类型等）
 * 6. SQL 注入器配置
 *
 * 注意：
 * - 部分插件仅在非生产环境下生效
 * - 支持通过配置属性动态开启/关闭功能
 * - 与 P6spy 插件互斥，避免重复功能
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.06.29 16:49
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration
@ConditionalOnEnabled(value = MybatisProperties.PREFIX)
@ConditionalOnClass(MybatisPlusAutoConfiguration.class)
@EnableConfigurationProperties(MybatisProperties.class)
public class MybatisAutoConfiguration implements ZekaAutoConfiguration {

    public MybatisAutoConfiguration() {
        log.info("启动自动配置: [{}]", this.getClass());
    }

    /**
     * 创建非法 SQL 语句拦截器
     *
     * 该拦截器用于在开发和测试环境中检测和拦截可能存在性能问题的 SQL 语句。
     * 主要检测规则包括：
     *
     * 1. 索引使用检查：
     *    - 必须使用到索引，包含 left join 连接字段，符合索引最左原则
     *    - 防止因动态 SQL bug 导致全表更新等危险操作
     *    - 确保 SQL 性能基本可控
     *
     * 2. SQL 复杂度控制：
     *    - 推荐单表执行，减少复杂的 join 操作
     *    - 提高查询条件的简单性和可维护性
     *    - 为分库分表等扩展性需求做准备
     *    - 提高缓存利用率
     *
     * 3. 危险语法检测：
     *    - 在字段上使用函数
     *    - where 条件为空
     *    - 使用 != 操作符
     *    - 使用 not 关键字
     *    - 使用 or 关键字
     *    - 使用子查询
     *
     * 注意：该拦截器仅在非生产环境下生效，且需要通过配置开启
     *
     * @return IllegalSQLInnerInterceptor 非法 SQL 拦截器实例
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(IllegalSQLInnerInterceptor.class)
    @Profile(value = {App.ENV_NOT_PROD})
    @ConditionalOnProperty(value = ConfigKey.MYBATIS_ENABLE_ILLEGAL_SQL_INTERCEPTOR,
        havingValue = ConfigDefaultValue.TRUE_STRING)
    public IllegalSQLInnerInterceptor illegalSqlInterceptor() {
        return new IllegalSQLInnerInterceptor();
    }

    /**
     * 创建 SQL 攻击拦截器
     *
     * 该拦截器用于防止恶意的 SQL 攻击操作，主要功能包括：
     * - 拦截全表 update 操作
     * - 拦截全表 delete 操作
     * - 防止无 where 条件的危险操作
     *
     * 注意：该插件仅在非生产环境下生效，生产环境建议关闭以提高性能
     *
     * @return BlockAttackInnerInterceptor SQL 攻击拦截器实例
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(BlockAttackInnerInterceptor.class)
    @Profile(value = {App.ENV_NOT_PROD})
    @ConditionalOnProperty(value = ConfigKey.MYBATIS_ENABLE_SQL_EXPLAIN_INTERCEPTOR,
        havingValue = ConfigDefaultValue.TRUE_STRING,
        matchIfMissing = true)
    public BlockAttackInnerInterceptor sqlExplainInterceptor() {
        return new BlockAttackInnerInterceptor();
    }

    /**
     * 创建分页拦截器
     *
     * 该拦截器提供分页查询功能，主要特性包括：
     * - 自动识别数据库方言，无需手动配置
     * - 支持多种数据库（MySQL、PostgreSQL、Oracle 等）
     * - 可配置单页最大查询数量，防止大数据量查询影响性能
     * - 自动优化 count 查询语句
     *
     * @param mybatisProperties MyBatis 配置属性，用于获取分页限制等配置
     * @return PaginationInnerInterceptor 分页拦截器实例
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(PaginationInnerInterceptor.class)
    public PaginationInnerInterceptor paginationInterceptor(@NotNull MybatisProperties mybatisProperties) {
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor();
        // 设置默认最大分页数 (zeka-stack.mybatis.single-page-limit)
        paginationInterceptor.setMaxLimit(mybatisProperties.getSinglePageLimit());
        return paginationInterceptor;
    }

    /**
     * 创建 MyBatis Plus 拦截器链
     *
     * 该方法将所有已配置的内部拦截器组装成一个拦截器链，统一管理。
     * 拦截器的执行顺序按照添加顺序进行，常见的拦截器包括：
     * - 分页拦截器
     * - SQL 攻击拦截器
     * - 非法 SQL 拦截器
     * - 性能监控拦截器等
     *
     * @param interceptors 所有已配置的内部拦截器列表
     * @return MybatisPlusInterceptor MyBatis Plus 拦截器链实例
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnBean(InnerInterceptor.class)
    public MybatisPlusInterceptor mybatisPlusInterceptor(List<InnerInterceptor> interceptors) {
        MybatisPlusInterceptor plusInterceptor = new MybatisPlusInterceptor();
        interceptors.forEach(plusInterceptor::addInnerInterceptor);
        return plusInterceptor;
    }

    /**
     * 创建自定义 SQL 注入器
     *
     * 该注入器用于扩展 MyBatis Plus 的默认 SQL 方法，提供额外的数据库操作方法。
     * 主要功能包括：
     * - 注入 insertIgnore 方法（MySQL 的 INSERT IGNORE 语法）
     * - 注入 replace 方法（MySQL 的 REPLACE INTO 语法）
     * - 支持批量操作的扩展方法
     *
     * @return ISqlInjector 自定义 SQL 注入器实例
     * @since 1.0.0
     */
    @Bean
    public ISqlInjector sqlInjector() {
        return new MybatisSqlInjector();
    }

    /**
     * 创建 SQL 性能监控拦截器
     *
     * 该拦截器用于监控 SQL 执行性能，主要功能包括：
     * - 记录 SQL 执行时间
     * - 格式化输出 SQL 语句（可配置）
     * - 设置 SQL 执行超时阈值
     * - 限制 SQL 输出长度，避免日志过长
     *
     * 注意：
     * - 仅在非生产环境下生效，生产环境建议关闭
     * - 当类路径中不存在 P6spy 驱动时才会创建此插件
     * - 与 P6spy 功能互斥，避免重复监控
     *
     * @param mybatisProperties MyBatis 配置属性，包含性能监控相关配置
     * @return PerformanceInterceptor 性能监控拦截器实例
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(PerformanceInterceptor.class)
    @ConditionalOnMissingClass("com.p6spy.engine.spy.P6SpyDriver")
    @Profile(value = {App.ENV_NOT_PROD})
    @ConditionalOnProperty(
        value = ConfigKey.MybatisConfigKey.MYBATIS_ENABLE_LOG,
        havingValue = ConfigDefaultValue.TRUE_STRING
    )
    public PerformanceInterceptor performanceInterceptor(MybatisProperties mybatisProperties) {
        PerformanceInterceptor performanceInterceptor = new PerformanceInterceptor();
        performanceInterceptor.setFormat(mybatisProperties.isSqlFormat());
        performanceInterceptor.setMaxTime(mybatisProperties.getPerformmaxTime());
        performanceInterceptor.setMaxLength(mybatisProperties.getMaxLength());
        return performanceInterceptor;
    }

    /**
     * 创建 SQL 执行超时处理器
     *
     * 该处理器用于监听和处理 SQL 执行超时事件，主要功能包括：
     * - 异步监听 SQL 执行超时事件
     * - 将超时的 SQL 语句记录到单独的 sql.log 文件中
     * - 便于后续分析和优化慢查询
     *
     * 注意：需要通过配置属性开启该功能
     *
     * @return SqlExecuteTimeoutHandler SQL 执行超时处理器实例
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnProperty(
        value = ConfigKey.MybatisConfigKey.APPEND_SQL_FILE,
        havingValue = ConfigDefaultValue.TRUE_STRING
    )
    @ConditionalOnMissingBean
    public SqlExecuteTimeoutHandler sqlExecuteTimeoutHandler() {
        return new SqlExecuteTimeoutHandler();
    }

    /**
     * 创建敏感字段解密拦截器
     *
     * 该拦截器用于在查询结果返回时自动解密敏感字段，主要功能包括：
     * - 自动识别标记为敏感的字段
     * - 使用配置的密钥对敏感字段进行解密
     * - 支持多种加密算法
     * - 透明化处理，业务代码无需关心加解密逻辑
     *
     * 注意：需要配置敏感字段加密功能才会生效
     *
     * @param mybatisProperties MyBatis 配置属性，包含敏感字段加密密钥等配置
     * @return SensitiveFieldDecryptIntercepter 敏感字段解密拦截器实例
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(SensitiveFieldDecryptIntercepter.class)
    @ConditionalOnProperty(
        value = ConfigKey.MybatisConfigKey.MYBATIS_ENABLE_SENSITIVE,
        havingValue = ConfigDefaultValue.TRUE_STRING,
        matchIfMissing = true
    )
    public SensitiveFieldDecryptIntercepter sensitiveFieldDecryptIntercepter(@NotNull MybatisProperties mybatisProperties) {
        return new SensitiveFieldDecryptIntercepter(mybatisProperties.getSensitiveKey());
    }

    /**
     * 创建敏感字段加密拦截器
     *
     * 该拦截器用于在数据写入数据库前自动加密敏感字段，主要功能包括：
     * - 自动识别标记为敏感的字段
     * - 使用配置的密钥对敏感字段进行加密
     * - 支持多种加密算法
     * - 透明化处理，业务代码无需关心加解密逻辑
     * - 与解密拦截器配合使用，实现完整的敏感数据保护
     *
     * 注意：需要配置敏感字段加密功能才会生效
     *
     * @param mybatisProperties MyBatis 配置属性，包含敏感字段加密密钥等配置
     * @return SensitiveFieldEncryptIntercepter 敏感字段加密拦截器实例
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(SensitiveFieldEncryptIntercepter.class)
    @ConditionalOnProperty(
        value = ConfigKey.MybatisConfigKey.MYBATIS_ENABLE_SENSITIVE,
        havingValue = ConfigDefaultValue.TRUE_STRING,
        matchIfMissing = true
    )
    public SensitiveFieldEncryptIntercepter sensitiveFieldEncryptIntercepter(@NotNull MybatisProperties mybatisProperties) {
        SqlUtils.setSensitiveKey(mybatisProperties.getSensitiveKey());
        return new SensitiveFieldEncryptIntercepter(mybatisProperties.getSensitiveKey());
    }

    /**
     * 创建枚举类型处理器配置定制器
     *
     * 该定制器用于配置自定义的枚举类型处理器，替换 MyBatis 默认的枚举处理器。
     * 主要功能包括：
     * - 使用 GeneralEnumTypeHandler 代替默认的 EnumTypeHandler
     * - 支持 EntityEnum 接口的枚举类型自动转换
     * - 数据库存储枚举的 value 值，程序中使用枚举对象
     * - 提供更灵活的枚举类型映射机制
     *
     * @return ConfigurationCustomizer MyBatis 配置定制器
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(MybatisEnumTypeHandler.class)
    public ConfigurationCustomizer configurationCustomizer() {
        // 通用枚举转换器
        return configuration -> configuration.setDefaultEnumTypeHandler(GeneralEnumTypeHandler.class);
    }

    /**
     * 创建 ID 类型处理器配置定制器
     *
     * 该定制器用于注册自定义的 ID 类型处理器，主要功能包括：
     * - 注册 SerializableIdTypeHandler 处理器
     * - 支持 Serializable 类型的 ID 字段自动转换
     * - 处理不同数据库中 ID 类型的差异
     * - 提供统一的 ID 类型映射机制
     *
     * @return ConfigurationCustomizer MyBatis 配置定制器
     * @since 1.0.0
     */
    @Bean
    public ConfigurationCustomizer idTypeHandlerCustomizer() {
        // id 转换器
        return configuration -> configuration.getTypeHandlerRegistry().register(new SerializableIdTypeHandler(Serializable.class));
    }

    /**
     * 创建 MyBatis 组件 Bean 提供者
     *
     * 该方法用于处理 P6spy 与性能拦截器的冲突问题。
     * 当检测到类路径中存在 P6spy 但未正确配置时，会发出警告并返回 null。
     *
     * 主要功能：
     * - 检测 P6spy 驱动的存在性
     * - 避免与 PerformanceInterceptor 功能重复
     * - 提供配置建议和警告信息
     *
     * @return ObjectProvider<ZekaComponentBean> 组件 Bean 提供者，如果存在冲突则返回 null
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnProperty(
        name = ConfigKey.DruidConfigKey.DRIVER_CLASS,
        havingValue = "com.mysql.cj.jdbc.Driver",
        matchIfMissing = true
    )
    @ConditionalOnClass(name = "com.p6spy.engine.spy.P6SpyDriver")
    public ObjectProvider<ZekaComponentBean> mybatisZekaComponentBean() {
        log.warn("classpath 存在 p6spy 但是未使用, 忽略加载 PerformanceInterceptor, "
            + "如不使用 p6spy 请删除相关依赖, 否则请正确配置 p6spy");
        return null;
    }

    /**
     * 元数据对象处理器自动配置类
     *
     * 该内部配置类负责配置 MyBatis Plus 的元数据自动填充功能，包括：
     * 1. 创建元数据处理器链，统一管理所有元数据处理器
     * 2. 配置时间字段自动填充处理器（创建时间、更新时间）
     * 3. 配置租户 ID 自动填充处理器（多租户场景）
     * 4. 配置客户端 ID 自动填充处理器（多客户端场景）
     *
     * 这些处理器会在数据插入和更新时自动填充相应的字段值，
     * 减少业务代码中的重复操作，确保数据的一致性和完整性。
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2021.04.07 21:01
     * @since 1.0.0
     */
    @AutoConfiguration
    static class MetaObjectAutoConfiguration implements ZekaAutoConfiguration {
        /**
         * 创建元数据处理器链
         *
         * 该方法将所有已配置的元数据处理器组装成一个处理器链，统一管理。
         * 处理器链会在数据插入和更新时按顺序执行，自动填充相关字段。
         *
         * 主要功能：
         * - 统一管理所有元数据处理器
         * - 按顺序执行处理器链
         * - 支持插入和更新时的字段自动填充
         *
         * @param chains 所有已配置的元数据处理器列表
         * @return MetaObjectHandler 元数据处理器链实例
         * @since 1.0.0
         */
        @Bean
        @ConditionalOnMissingBean(MetaHandlerChain.class)
        public MetaObjectHandler metaHandlerChain(List<MetaObjectChain> chains) {
            return new MetaHandlerChain(chains);
        }

        /**
         * 创建时间字段元数据处理器
         *
         * 该处理器负责自动填充时间相关字段，主要功能包括：
         * - 插入时自动设置创建时间（create_time）
         * - 更新时自动设置更新时间（update_time）
         * - 使用当前系统时间作为默认值
         * - 支持 Date 类型的时间字段
         *
         * @return MetaObjectChain 时间字段元数据处理器实例
         * @since 1.0.0
         */
        @Bean
        @ConditionalOnMissingBean(name = "timeMetaObjectHandler")
        public MetaObjectChain timeMetaObjectHandler() {
            return new TimeMetaObjectHandler();
        }

        /**
         * 创建租户 ID 元数据处理器
         *
         * 该处理器负责在多租户场景下自动填充租户 ID 字段，主要功能包括：
         * - 插入时自动设置租户 ID（tenant_id）
         * - 从当前上下文中获取租户信息
         * - 支持多租户数据隔离
         * - 确保数据归属的正确性
         *
         * @return MetaObjectChain 租户 ID 元数据处理器实例
         * @since 1.0.0
         */
        @Bean
        @ConditionalOnMissingBean(name = "tenantMetaObjectHandler")
        public MetaObjectChain tenantMetaObjectHandler() {
            return new TenantIdMetaObjectHandler();
        }

        /**
         * 创建客户端 ID 元数据处理器
         *
         * 该处理器负责在多客户端场景下自动填充客户端 ID 字段，主要功能包括：
         * - 插入时自动设置客户端 ID（client_id）
         * - 从当前上下文中获取客户端信息
         * - 支持多客户端数据标识
         * - 便于数据来源追踪和统计
         *
         * @return MetaObjectChain 客户端 ID 元数据处理器实例
         * @since 1.0.0
         */
        @Bean
        @ConditionalOnMissingBean(name = "clientMetaObjectHandler")
        public MetaObjectChain clientMetaObjectHandler() {
            return new ClientIdMetIdaObjectHandler();
        }

    }

    /**
     * 获取库类型标识
     *
     * 该方法返回当前配置类所属的库类型，用于框架内部的组件识别和管理。
     * 返回 DRUID 表示该配置类主要用于 Druid 数据源相关的 MyBatis 配置。
     *
     * @return LibraryEnum 库类型枚举，返回 DRUID
     * @since 1.0.0
     */
    @Override
    public LibraryEnum getLibraryType() {
        return LibraryEnum.DRUID;
    }
}
