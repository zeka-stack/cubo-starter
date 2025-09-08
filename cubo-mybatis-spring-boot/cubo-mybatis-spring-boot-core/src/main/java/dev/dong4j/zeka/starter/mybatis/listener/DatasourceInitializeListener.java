package dev.dong4j.zeka.starter.mybatis.listener;

import dev.dong4j.zeka.kernel.common.ZekaApplicationListener;
import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import dev.dong4j.zeka.kernel.common.util.StringPool;
import dev.dong4j.zeka.kernel.common.util.StringUtils;
import dev.dong4j.zeka.processor.annotation.AutoListener;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * 数据源初始化监听器
 *
 * 该监听器在应用上下文初始化完成后检查数据源配置的完整性。
 * 如果检测到缺少必要的数据源配置，会自动排除相关的自动配置类，
 * 避免因配置不完整导致的应用启动失败。
 *
 * 主要功能：
 * 1. 检查 spring.datasource.url 配置是否存在
 * 2. 如果配置缺失，自动排除数据源相关的自动配置类
 * 3. 记录警告日志，提醒开发者处理配置问题
 * 4. 确保应用能够正常启动，即使数据源配置不完整
 *
 * 排除的自动配置类：
 * - DataSourceAutoConfiguration：Spring Boot 数据源自动配置
 * - MybatisPlusAutoConfiguration：MyBatis Plus 自动配置
 * - DruidDataSourceAutoConfigure：Druid 数据源自动配置
 * - MybatisAutoConfiguration：自定义 MyBatis 自动配置
 *
 * 注意：该监听器目前标记为暂时不使用，可根据需要启用
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.19 18:46
 * @since 1.0.0
 */
@Slf4j
@AutoListener
public class DatasourceInitializeListener implements ZekaApplicationListener {
    /** DATASOURCEAUTOCONFIGURATION */
    public static final String DATASOURCE_AUTOCONFIGURATION = "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration";
    /** MYBATISPLUS_AUTOCONFIGURATION */
    public static final String MYBATISPLUS_AUTOCONFIGURATION = "com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration";
    /** DRUIDDATASOURCE_AUTOCONFIGURE */
    public static final String DRUIDDATASOURCE_AUTOCONFIGURE = "com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure";
    /** MYBATIS_AUTOCONFIGURATION */
    public static final String MYBATIS_AUTOCONFIGURATION = "dev.dong4j.zeka.starter.mybatis.autoconfigure.MybatisAutoConfiguration";
    /** Inited */
    private static boolean inited = false;

    /**
     * 获取执行顺序
     *
     * 该方法返回监听器的执行顺序，设置为最低优先级（LOWEST_PRECEDENCE），
     * 确保在所有其他配置处理完成后再执行数据源配置检查。
     *
     * @return int 执行顺序值，数值越大优先级越低
     * @since 1.0.0
     */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    /**
     * 处理应用上下文初始化事件
     *
     * 该方法在应用上下文初始化完成后被调用，执行数据源配置检查逻辑。
     *
     * 处理流程：
     * 1. 检查是否已经初始化过，避免重复执行
     * 2. 从环境配置中获取数据源 URL 配置
     * 3. 如果配置缺失，记录错误日志并排除相关自动配置类
     * 4. 通过系统属性设置排除的自动配置类列表
     * 5. 标记为已初始化，防止重复处理
     *
     * @param event 应用上下文初始化事件
     * @since 1.0.0
     */
    @Override
    public void onApplicationContextInitializedEvent(@NotNull ApplicationContextInitializedEvent event) {
        if (!inited) {
            ZekaApplicationListener.Runner.executeAtLast(this.key(event, this.getClass()), () -> {
                ConfigurableEnvironment environment = event.getApplicationContext().getEnvironment();
                String datasourceUrl = environment.getProperty("spring.datasource.url");

                if (StringUtils.isBlank(datasourceUrl)) {
                    log.error("未检测到 JDBC 配置, 但是引入了 JDBC 相关依赖包, 将会禁用自动配置. 请根据业务处理此错误!");
                    String property = System.getProperty(ConfigKey.SpringConfigKey.AUTOCONFIGURE_EXCLUDE);

                    String value;
                    if (StringUtils.isBlank(property)) {
                        value = String.join(StringPool.COMMA,
                            DATASOURCE_AUTOCONFIGURATION,
                            MYBATISPLUS_AUTOCONFIGURATION,
                            DRUIDDATASOURCE_AUTOCONFIGURE,
                            MYBATIS_AUTOCONFIGURATION);

                    } else {
                        value = String.join(StringPool.COMMA,
                            property,
                            DATASOURCE_AUTOCONFIGURATION,
                            MYBATISPLUS_AUTOCONFIGURATION,
                            DRUIDDATASOURCE_AUTOCONFIGURE,
                            MYBATIS_AUTOCONFIGURATION);
                    }
                    System.setProperty(ConfigKey.SpringConfigKey.AUTOCONFIGURE_EXCLUDE, value);
                }
                inited = true;
            });
        }
    }
}
