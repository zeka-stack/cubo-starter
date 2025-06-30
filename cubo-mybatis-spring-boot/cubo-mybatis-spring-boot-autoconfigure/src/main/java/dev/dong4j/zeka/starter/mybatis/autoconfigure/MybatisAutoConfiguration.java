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
import dev.dong4j.zeka.kernel.autoconfigure.ZekaProperties;
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
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 组件自动装配类
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.06.29 16:49
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(
    prefix = MybatisProperties.PREFIX,
    name = ZekaProperties.ENABLED,
    havingValue = ZekaProperties.ON,
    matchIfMissing = true)
@ConditionalOnClass(MybatisPlusAutoConfiguration.class)
@EnableConfigurationProperties(MybatisProperties.class)
public class MybatisAutoConfiguration implements ZekaAutoConfiguration {

    public MybatisAutoConfiguration() {
        log.info("启动自动配置: [{}]", this.getClass());
    }

    /**
     * 非法 SQL 语句拦截器: SQL 严格模式
     * 1.必须使用到索引,包含 left jion 连接字段,符合索引最左原则
     * 必须使用索引好处:
     * 1.1 如果因为动态 SQL,bug 导致 update 的 where 条件没有带上,全表更新上万条数据
     * 1.2 如果检查到使用了索引,SQL 性能基本不会太差
     * <p>
     * 2.SQL 尽量单表执行,有查询 left jion 的语句,必须在注释里面允许该 SQL 运行,否则会被拦截,有 left jion 的语句,如果不能拆成单表执行的 SQL,请 leader 商量再做决定
     * <a href="http://gaoxianglong.github.io/shark/">...</a>
     * SQL 尽量单表执行的好处:
     * 2.1 查询条件简单、易于开理解和维护;
     * 2.2 扩展性极强;  (可为分库分表做准备)
     * 2.3 缓存利用率高;
     * <p>
     * 2.在字段上使用函数
     * 3.where 条件为空
     * 4.where 条件使用了: !=
     * 5.where 条件使用了: not 关键字
     * 6.where 条件使用了: or 关键字
     * 7.where 条件使用了: 子查询
     *
     * @return the illegal sql interceptor
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
     * SQL执行分析插件, 拦截一些整表操作,在生产环境最好关闭.
     *
     * @return the sql explain interceptor
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
     * 分页插件, 不需要设置方言, mybatis-plus 自动判断
     *
     * @param mybatisProperties mybatis properties
     * @return the pagination interceptor
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
     * Mybatis plus interceptor
     *
     * @param interceptors interceptors
     * @return the mybatis plus interceptor
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
     * sql 注入
     *
     * @return the sql injector
     * @since 1.0.0
     */
    @Bean
    public ISqlInjector sqlInjector() {
        return new MybatisSqlInjector();
    }

    /**
     * mybatis-plus SQL执行效率插件 (生产环境最好关闭)
     * 不存在 com.p6spy.engine.spy.P6SpyDriver 则使用此插件.
     *
     * @param mybatisProperties 配置类
     * @return the performance interceptor
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(PerformanceInterceptor.class)
    @ConditionalOnMissingClass("com.p6spy.engine.spy.P6SpyDriver")
    @Profile(value = {App.ENV_NOT_PROD})
    @ConditionalOnProperty(
        value = ConfigKey.MybatisConfigKey.MYBATIS_ENABLE_LOG,
        havingValue = ConfigDefaultValue.TRUE_STRING,
        matchIfMissing = true
    )
    public PerformanceInterceptor performanceInterceptor(MybatisProperties mybatisProperties) {
        PerformanceInterceptor performanceInterceptor = new PerformanceInterceptor();
        performanceInterceptor.setFormat(mybatisProperties.isSqlFormat());
        performanceInterceptor.setMaxTime(mybatisProperties.getPerformmaxTime());
        return performanceInterceptor;
    }

    /**
     * 脱敏插件-解码
     *
     * @param mybatisProperties mybatis properties
     * @return the sensitive field decrypt intercepter
     * @since 1.5.0
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
     * 脱敏插件-编码
     *
     * @param mybatisProperties mybatis properties
     * @return the sensitive field encrypt intercepter
     * @since 1.5.0
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
     * 使用 MybatisEnumTypeHandler 代替默认的 EnumTypeHandler, 实现 EntityEnum 子类的类型转换(数据库存 value, 返回 Entity)
     *
     * @return the configuration customizer
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(MybatisEnumTypeHandler.class)
    public ConfigurationCustomizer configurationCustomizer() {
        // 通用枚举转换器
        return configuration -> configuration.setDefaultEnumTypeHandler(GeneralEnumTypeHandler.class);
    }

    /**
     * id 类型转换
     *
     * @return the configuration customizer
     * @since 1.0.0
     */
    @Bean
    public ConfigurationCustomizer idTypeHandlerCustomizer() {
        // id 转换器
        return configuration -> configuration.getTypeHandlerRegistry().register(new SerializableIdTypeHandler(Serializable.class));
    }

    /**
     * Mybatis component bean
     *
     * @return the object provider
     * @since 1.7.1
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
     * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2021.04.07 21:01
     * @since 1.8.0
     */
    @Configuration(proxyBeanMethods = false)
    static class MetaObjectAutoConfiguration implements ZekaAutoConfiguration {
        /**
         * 自动创建时间和更新时间
         *
         * @param chains chains
         * @return global config
         * @since 1.0.0
         */
        @Bean
        @ConditionalOnMissingBean(MetaHandlerChain.class)
        public MetaObjectHandler metaHandlerChain(List<MetaObjectChain> chains) {
            return new MetaHandlerChain(chains);
        }

        /**
         * Time meta handler
         *
         * @return the meta object chain
         * @since 1.8.0
         */
        @Bean
        @ConditionalOnMissingBean(name = "timeMetaObjectHandler")
        public MetaObjectChain timeMetaObjectHandler() {
            return new TimeMetaObjectHandler();
        }

        /**
         * Tenant meta handler
         *
         * @return the meta object chain
         * @since 1.8.0
         */
        @Bean
        @ConditionalOnMissingBean(name = "tenantMetaObjectHandler")
        public MetaObjectChain tenantMetaObjectHandler() {
            return new TenantIdMetaObjectHandler();
        }

        /**
         * Client id meta object handler
         *
         * @return the meta object chain
         * @since 1.8.0
         */
        @Bean
        @ConditionalOnMissingBean(name = "clientMetaObjectHandler")
        public MetaObjectChain clientMetaObjectHandler() {
            return new ClientIdMetIdaObjectHandler();
        }

    }

    /**
     * Gets library type *
     *
     * @return the library type
     * @since 1.0.0
     */
    @Override
    public LibraryEnum getLibraryType() {
        return LibraryEnum.DRUID;
    }
}
