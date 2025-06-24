package dev.dong4j.zeka.starter.launcher.spi;

import com.google.common.collect.Maps;
import dev.dong4j.zeka.kernel.common.constant.ConfigDefaultValue;
import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import dev.dong4j.zeka.kernel.common.enums.SerializeEnum;
import dev.dong4j.zeka.kernel.common.enums.ZekaEnv;
import dev.dong4j.zeka.kernel.common.start.LauncherInitiation;
import dev.dong4j.zeka.kernel.common.support.ChainMap;
import dev.dong4j.zeka.kernel.common.util.ConfigKit;
import dev.dong4j.zeka.kernel.common.util.FileUtils;
import dev.dong4j.zeka.kernel.common.util.StringUtils;
import dev.dong4j.zeka.processor.annotation.AutoService;
import dev.dong4j.zeka.starter.launcher.constant.Launcher;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.01.27 12:23
 * @since 1.0.0
 */
@Slf4j
@AutoService(LauncherInitiation.class)
public class SubLauncherInitiation implements LauncherInitiation {
    /** SPRING_PROFILE_ACTIVE_FILE */
    private static final String SPRING_PROFILE_ACTIVE_FILE = "spring.profiles.active";

    /**
     * 检查枚举的 value 是否重复
     *
     * @param appName app name
     * @since 2022.1.1
     */
    @SneakyThrows
    @Override
    @SuppressWarnings(value = {"rawtypes"})
    public void advance(String appName) {
        // 构建 Reflections 扫描器，仅扫描指定包下的子类
        Reflections reflections = new Reflections(
            new ConfigurationBuilder()
                .forPackages(ConfigDefaultValue.BASE_PACKAGES)
                .setScanners(new SubTypesScanner(false))
                .setExpandSuperTypes(false)
        );

        Set<Class<? extends SerializeEnum>> enumClasses = reflections.getSubTypesOf(SerializeEnum.class);
        if (enumClasses == null || enumClasses.isEmpty()) {
            return;
        }

        for (Class<? extends SerializeEnum> enumClass : enumClasses) {
            if (!enumClass.isEnum()) {
                continue;
            }

            try {
                Method getValueMethod = enumClass.getMethod(SerializeEnum.VALUE_METHOD_NAME);
                Object[] enumConstants = enumClass.getEnumConstants();

                if (enumConstants == null || enumConstants.length <= 1) {
                    continue;
                }

                Map<Object, Object> valueMap = Maps.newHashMapWithExpectedSize(64);
                for (Object constant : enumConstants) {
                    Object value = getValueMethod.invoke(constant);
                    Object existing = valueMap.putIfAbsent(value, constant);
                    if (existing != null) {
                        throw new IllegalArgumentException(String.format(
                            "存在相同的枚举 value: [%s: %s.value = %s.value]",
                            enumClass.getName(), constant, existing
                        ));
                    }
                }
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("处理枚举类失败: " + enumClass.getName(), e);
            }
        }
    }

    /**
     * Launcher map
     *
     * @param env           env
     * @param appName       app name
     * @param isLocalLaunch is local launch
     * @return the map
     * @since 1.0.0
     */
    @Override
    public Map<String, Object> launcher(ConfigurableEnvironment env, String appName, boolean isLocalLaunch) {
        ChainMap chainMap = ChainMap.build(8)
            // Spring Boot 2.1 需要设定, 存在相同的 bean name 时, 后一个覆盖前一个, 主要用于覆写默认 bean
            .put(ConfigKey.SpringConfigKey.MAIN_ALLOW_BEAN_DEFINITION_OVERRIDING, ConfigDefaultValue.TRUE)
            // 启动后新增一个 app.pid 文本文件, 写入当前应用的 PID
            .put(ConfigKey.SpringConfigKey.PID_FILE, ConfigDefaultValue.PROP_PID_FILE)
            // 配置加密密钥
            .put(ConfigKey.JASYPT_ENCRYPTOR_PASSWORD, ConfigDefaultValue.DEFAULT_ENCRYPTOR_PASSWORD)
            .put(ConfigKey.WIKI, ConfigDefaultValue.WIKI);

        // 本地开发时, 读取 arco-maven-plugin/profile/spring.profiles.active
        if (isLocalLaunch) {
            String targetPath = getTargetDir();
            String finalActiveFilePath = FileUtils.appendPath(targetPath, "arco-maven-plugin", "profile", SPRING_PROFILE_ACTIVE_FILE);

            String currentProfileActive = ZekaEnv.LOCAL.getName();
            try {
                String active = StringUtils.trimAllWhitespace(FileUtils.readToString(new File(finalActiveFilePath)));
                if (StringUtils.isBlank(active)) {
                    active = ZekaEnv.LOCAL.getName();
                }
                currentProfileActive = active;
            } catch (Exception e) {
                log.debug("未监测到 target/arco-maven-plugin/profile/spring.profiles.active 文件, 将自动设置为 [local].");
                System.setProperty("profile.active", currentProfileActive);
            }

            System.setProperty(ConfigKey.SpringConfigKey.PROFILE_ACTIVE, currentProfileActive);
        }

        return chainMap;
    }

    /**
     * 本地开发时获取 target 目录
     *
     * @return the string
     * @since 1.0.0
     */
    private static @NotNull String getTargetDir() {
        File classPath = new File(ConfigKit.getConfigPath());
        return classPath.getParentFile().getPath();
    }

    /**
     * Gets name *
     *
     * @return the name
     * @since 1.0.0
     */
    @Override
    public String getName() {
        return Launcher.MODULE_NAME;
    }

    /**
     * Gets order *
     *
     * @return the order
     * @since 1.0.0
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
