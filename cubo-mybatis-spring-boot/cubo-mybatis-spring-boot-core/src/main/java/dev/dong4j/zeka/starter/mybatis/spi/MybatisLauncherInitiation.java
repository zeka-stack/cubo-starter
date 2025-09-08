package dev.dong4j.zeka.starter.mybatis.spi;

import dev.dong4j.zeka.kernel.common.constant.ConfigDefaultValue;
import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import dev.dong4j.zeka.kernel.common.start.LauncherInitiation;
import dev.dong4j.zeka.kernel.common.support.ChainMap;
import dev.dong4j.zeka.processor.annotation.AutoService;
import java.util.Map;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * MyBatis 启动初始化器
 *
 * 该类通过 SPI 机制自动加载 MyBatis Plus 的默认配置，在应用启动时
 * 为 MyBatis Plus 提供合理的默认配置值。
 *
 * 主要功能：
 * 1. 配置 Mapper XML 文件的扫描路径
 * 2. 配置 MyBatis 的基本行为参数
 * 3. 配置逻辑删除的默认值
 * 4. 配置主键生成策略
 * 5. 配置日志实现类
 *
 * 配置项包括：
 * - Mapper 文件位置：支持 classpath 和 jar 包中的 XML 文件
 * - 缓存配置：启用二级缓存
 * - 命名策略：下划线转驼峰命名
 * - 逻辑删除：设置删除和未删除的标识值
 * - 主键策略：使用数据库自增主键
 * - 日志配置：使用自定义的无日志输出实现
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 11:19
 * @since 1.0.0
 */
@AutoService(LauncherInitiation.class)
public class MybatisLauncherInitiation implements LauncherInitiation {
    /**
     * 设置默认属性
     *
     * 该方法在应用启动时被调用，用于设置 MyBatis Plus 的默认配置。
     *
     * 配置的主要参数：
     * 1. Mapper XML 文件扫描路径，支持多级目录和 jar 包
     * 2. MyBatis 核心配置：缓存、命名转换、空值处理等
     * 3. 逻辑删除配置：已删除值为 1，未删除值为 0
     * 4. 主键生成策略：使用数据库自增
     * 5. 日志配置：使用自定义的无输出日志实现
     *
     * @param env Spring 环境配置对象
     * @param appName 应用名称
     * @param isLocalLaunch 是否为本地启动
     * @return Map<String, Object> 默认配置属性映射
     * @see dev.dong4j.zeka.starter.mybatis.logger.NoLogOutImpl
     * @since 1.0.0
     */
    @Override
    public Map<String, Object> setDefaultProperties(ConfigurableEnvironment env,
                                                    String appName,
                                                    boolean isLocalLaunch) {
        return ChainMap.build(8)
            // 支持 mappers 和里面的子目录, 包括 jar 中的 xml 文件
            .put(ConfigKey.MybatisConfigKey.MAPPER_LOCATIONS, "classpath*:/mappers/**/*.xml")
            .put(ConfigKey.MybatisConfigKey.CONFIGURATION_CALL_SETTERS_ON_NULLS, ConfigDefaultValue.TRUE)
            .put(ConfigKey.MybatisConfigKey.CONFIGURATION_LOG_IMPL, "dev.dong4j.zeka.starter.mybatis.logger.NoLogOutImpl")
            .put(ConfigKey.MybatisConfigKey.CONFIGURATION_CACHE_ENABLED, ConfigDefaultValue.TRUE)
            .put(ConfigKey.MybatisConfigKey.CONFIGURATION_MAP_UNDERSCORE_TO_CAMEL_CASE, ConfigDefaultValue.TRUE)
            // 逻辑删除配置, 逻辑已删除值, 逻辑未删除值(默认为 0)
            .put(ConfigKey.MybatisConfigKey.GLOBAL_LOGIC_DELETE_VALUE, 1)
            .put(ConfigKey.MybatisConfigKey.GLOBAL_LOGIC_NOT_DELETE_VALUE, 0)
            // 主键类型, 设置为自增, 要求 DDL 使用 auto_increment
            .put(ConfigKey.MybatisConfigKey.GLOBAL_ID_TYPE, "auto")
            .put(ConfigKey.MybatisConfigKey.GLOBAL_LOGIC_BANNER, "false");

    }

    /**
     * 获取执行顺序
     *
     * 该方法返回初始化器的执行顺序，数值越小优先级越高。
     * 设置为 HIGHEST_PRECEDENCE + 200，确保在大部分配置之前执行，
     * 但在核心框架配置之后执行。
     *
     * @return int 执行顺序值
     * @since 1.0.0
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 200;
    }

    /**
     * 获取初始化器名称
     *
     * 该方法返回初始化器的唯一标识名称，用于日志记录和调试。
     * 名称对应当前 starter 模块的名称。
     *
     * @return String 初始化器名称
     * @since 1.0.0
     */
    @Override
    public String getName() {
        return "cubo-mybatis-spring-boot-starter";
    }
}
