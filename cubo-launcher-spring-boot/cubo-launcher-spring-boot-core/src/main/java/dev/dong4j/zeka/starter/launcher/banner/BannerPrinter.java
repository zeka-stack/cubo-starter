package dev.dong4j.zeka.starter.launcher.banner;

import dev.dong4j.zeka.kernel.common.util.ConfigKit;
import dev.dong4j.zeka.kernel.common.util.Tools;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * <p>Description: banner 文件查找顺序</p>
 * 1. banner.txt
 * 2. default.banner
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.26 21:45
 * @since 1.0.0
 */
@Slf4j
public class BannerPrinter {
    /** DEFAULT_BANNER */
    private static final Banner DEFAULT_BANNER = new ZekaBanner();

    /**
     * 输出 banner 信息.
     * 优先加载自有模块中的 banner.txt 文件内容, 如果不存在, 则使用 default.banner 文件内容输出
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
     * Print *
     *
     * @param inputStream input stream
     * @param profile     profile
     * @param version     version
     * @param appName     app name
     * @since 1.0.0
     */
    private static void print(InputStream inputStream, String profile, String version, String appName) {
        DEFAULT_BANNER.printBanner(inputStream, profile, version, appName);
    }
}
