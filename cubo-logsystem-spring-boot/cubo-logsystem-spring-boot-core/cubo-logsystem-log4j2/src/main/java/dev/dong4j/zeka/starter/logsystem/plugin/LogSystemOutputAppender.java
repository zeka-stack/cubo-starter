package dev.dong4j.zeka.starter.logsystem.plugin;

import dev.dong4j.zeka.kernel.common.util.StringPool;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Description: 默认输出 </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:19
 * @since 1.0.0
 */
@UtilityClass
public class LogSystemOutputAppender {

    /**
     * Append.
     *
     * @param toAppendTo the to append to
     * @since 1.0.0
     */
    public static void append(@NotNull StringBuilder toAppendTo) {
        toAppendTo.append("AT: " + StringPool.NULL_STRING);
    }
}
