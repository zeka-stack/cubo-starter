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
 * <p>Description: 通过 SPI 加载 mybatis 默认配置</p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 11:19
 * @since 1.0.0
 */
@AutoService(LauncherInitiation.class)
public class MybatisLauncherInitiation implements LauncherInitiation {
    /**
     * Launcher *
     *
     * @param env           env
     * @param appName       app name
     * @param isLocalLaunch is local launch
     * @return the map
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
     * Gets order *
     *
     * @return the order
     * @since 1.0.0
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 200;
    }

    /**
     * Gets name *
     *
     * @return the name
     * @since 1.0.0
     */
    @Override
    public String getName() {
        return "cubo-mybatis-spring-boot-starter";
    }
}
