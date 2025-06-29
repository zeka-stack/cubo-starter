package dev.dong4j.zeka.starter.logsystem.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.4
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.26 13:51
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
public class LogFile {
    /** 日志文件名, 此配置只对 log4j2-flie.xml 有效. */
    private String name;
    /** 日志保存路径, 此配置只对 log4j2-flie.xml 有效. */
    private String path;
    /** 启动应用时清理历史日志, 此配置只对 log4j2-flie.xml 有效. */
    private boolean cleanHistoryOnStart = false;
    /** 历史日志最大保留时间(天), 此配置只对 log4j2-flie.xml 有效. */
    private Integer maxHistory = 90;
    /** 日志文件最大容量, 此配置只对 log4j2-flie.xml 有效. */
    private String maxSize = "50MB";
    /** 日志总数量, 此配置只对 log4j2-flie.xml 有效. */
    private Integer totalSizeCap = 50;

    /**
     * Log file
     *
     * @param name name
     * @param path path
     * @since 1.4.0
     */
    @Contract(pure = true)
    public LogFile(String name, String path) {
        this.name = name;
        this.path = path;
    }
}
