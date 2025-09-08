package dev.dong4j.zeka.starter.launcher.banner;

import dev.dong4j.zeka.kernel.common.util.ConfigKit;
import dev.dong4j.zeka.kernel.common.util.DateUtils;
import dev.dong4j.zeka.kernel.common.util.StartUtils;
import dev.dong4j.zeka.kernel.common.util.StringPool;
import dev.dong4j.zeka.kernel.common.util.StringUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.boot.SpringBootVersion;

/**
 * Zeka 框架的 Banner 实现类，负责格式化和输出自定义 Banner
 *
 * 该类实现了 Banner 接口，提供了具体的 Banner 打印逻辑：
 * 1. 读取并格式化 Banner 文件内容
 * 2. 添加应用信息（名称、版本、环境）
 * 3. 使用日志系统输出美观的 Banner
 * 4. 支持不同启动模式下的信息展示
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 00:29
 * @since 1.0.0
 */
@Slf4j
public class ZekaBanner implements Banner {

    /**
     * 打印 Banner 信息
     *
     * 读取输入流中的 Banner 内容，并添加版本、环境和应用名称等信息，
     * 通过格式化处理后输出美观的启动 Banner。
     *
     * @param inputStream Banner 文件的输入流
     * @param profile     当前激活的环境配置
     * @param version     应用版本信息
     * @param appName     应用名称
     * @since 1.0.0
     */
    @Override
    public void printBanner(InputStream inputStream, String profile, String version, String appName) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String blankCharacter = "   ";
            String info = blankCharacter + "::: Spring Boot (version:{}) :::        ::: {} :::        ::: spring.profiles.active = {} :::";
            String versionInfo = StringUtils.format(info, SpringBootVersion.getVersion(), version, profile);
            if (profile.startsWith(StringPool.LEFT_SQ_BRACKET)) {
                versionInfo = blankCharacter + profile;
            }
            int length = versionInfo.length();
            StringBuilder str = new StringBuilder(blankCharacter);
            for (int i = 0; i < length - blankCharacter.length(); i++) {
                str.append("-");
            }

            this.printLine(appName, br, versionInfo, str);

        } catch (IOException ignored) {
        }
    }

    /**
     * 打印 Banner 的具体行内容
     *
     * 该方法负责：
     * 1. 格式化并居中显示 Banner 内容
     * 2. 添加应用信息和启动时间
     * 3. 添加环境和版本信息
     * 4. 使用特定的日志标记输出，便于日志系统特殊处理
     *
     * @param appName     应用名称
     * @param br          Banner 内容的缓冲读取器
     * @param versionInfo 版本信息字符串
     * @param str         用于生成下划线的 StringBuilder
     * @throws IOException 读取 Banner 内容时可能抛出的异常
     * @since 1.0.0
     */
    private void printLine(String appName,
                           @NotNull BufferedReader br,
                           String versionInfo,
                           @NotNull StringBuilder str) throws IOException {
        // 日志配置文件: MarkerPatternSelector.PatternMatch.key = banner
        Marker bannerMarker = MarkerFactory.getMarker("banner");

        String underline = str.toString();
        String devModel = "Local Development Model";
        if (ConfigKit.notLocalLaunch()) {
            devModel = "Started By Shell";
        }
        String startline = "::: "
            + appName
            + "("
            + ConfigKit.getAppVersion()
            + ")"
            + " starting time: "
            + DateUtils.formatDateTime(new Date())
            + " start.model: " + devModel + " :::";

        startline = StringUtils.center(startline, underline.length(), StringPool.SPACE);
        int maxlength = 0;
        log.info(bannerMarker, "");
        log.info(bannerMarker, "");
        log.info(bannerMarker, startline);
        log.info(bannerMarker, "");

        // 循环输出 banner 内容
        String line;
        while ((line = br.readLine()) != null) {
            maxlength = Math.max(maxlength, line.length());
            log.info(bannerMarker, StringUtils.center(line, underline.length(), StringPool.SPACE));
        }

        log.info(bannerMarker, StartUtils.showInfo(maxlength, str.toString()));
        log.info(bannerMarker, StartUtils.showInfo(maxlength, versionInfo));
        log.info(bannerMarker, StartUtils.showInfo(maxlength, str.toString()));
        log.info(bannerMarker, "");
    }
}
