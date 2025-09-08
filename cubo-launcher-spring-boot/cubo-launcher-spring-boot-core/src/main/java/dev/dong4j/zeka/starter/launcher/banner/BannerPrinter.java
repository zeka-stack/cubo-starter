package dev.dong4j.zeka.starter.launcher.banner;

import dev.dong4j.zeka.kernel.common.util.ConfigKit;
import dev.dong4j.zeka.kernel.common.util.Tools;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import lombok.extern.slf4j.Slf4j;

/**
 * Banner 打印工具类，负责加载和显示应用启动 Banner
 *
 * 该类会按照以下顺序查找 Banner 文件：
 * 1. 优先查找项目根目录下的 banner.txt 文件
 * 2. 如果不存在，则使用内置的 banner/default.banner 文件
 *
 * 加载到 Banner 文件后，会将其内容与应用信息（版本、名称、环境）一起显示。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.26 21:45
 * @since 1.0.0
 */
@Slf4j
public class BannerPrinter {
    /** DEFAULT_BANNER */
    private static final Banner DEFAULT_BANNER = new ZekaBanner();

    /**
     * 输出 Banner 信息
     *
     * 该方法会按照优先级查找并加载 Banner 文件：
     * 1. 首先尝试加载项目中的自定义 banner.txt 文件
     * 2. 如果未找到，则加载内置的 banner/default.banner 文件
     * 3. 获取应用版本、名称和环境信息
     * 4. 调用 Banner 实现类显示内容
     *
     * @since 1.0.0
     */
    public void print() {
        InputStream inputStream;
        // 如果自定义 banner, 则优先加载
        inputStream = this.getClass().getClassLoader().getResourceAsStream("banner.txt");
        if (inputStream == null) {
            URL url = this.getClass().getClassLoader().getResource("banner/default.banner");
            if (url != null) {
                try {
                    inputStream = url.openStream();
                } catch (IOException ignored) {
                }
            }
        }

        String version = ConfigKit.getFrameworkVersion();
        String appName = ConfigKit.getAppName();
        String profile = ConfigKit.getProfile();
        version = Tools.isBlank(version)
            ? "JDK (version: " + System.getProperty("java.version") + ")"
            : "Framework (version: " + version + ")";
        print(inputStream, profile, version, appName);
    }

    /**
     * 打印 Banner 内容
     *
     * 调用默认的 Banner 实现类来打印 Banner 内容，
     * 将加载的 Banner 文件内容与应用信息一起显示。
     *
     * @param inputStream Banner 文件的输入流
     * @param profile     当前激活的环境配置
     * @param version     应用版本信息
     * @param appName     应用名称
     * @since 1.0.0
     */
    private static void print(InputStream inputStream, String profile, String version, String appName) {
        DEFAULT_BANNER.printBanner(inputStream, profile, version, appName);
    }
}
