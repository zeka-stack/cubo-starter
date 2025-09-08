package dev.dong4j.zeka.starter.launcher.banner;

import java.io.InputStream;

/**
 * Banner 接口，用于自定义应用启动时的 Banner 输出
 *
 * 该接口替代了 SpringBoot 默认的 banner 输出机制，提供了更灵活的自定义能力。
 * 实现此接口可以根据不同的需求定制应用启动时的视觉效果和信息展示。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.26 21:45
 * @since 1.0.0
 */
public interface Banner {

    /**
     * 打印 Banner 信息
     *
     * 根据提供的参数打印自定义的 Banner 内容，可以包含应用名称、版本号和环境信息等。
     *
     * @param inputStream Banner 内容的输入流，通常从文件中读取
     * @param profile     当前激活的环境配置（如：dev、test、prod）
     * @param version     当前应用的版本号
     * @param appName     应用名称
     * @since 1.0.0
     */
    void printBanner(InputStream inputStream, String profile, String version, String appName);
}
