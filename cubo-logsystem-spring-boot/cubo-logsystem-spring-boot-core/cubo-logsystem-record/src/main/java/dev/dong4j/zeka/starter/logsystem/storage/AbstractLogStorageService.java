package dev.dong4j.zeka.starter.logsystem.storage;

import dev.dong4j.zeka.starter.logsystem.entity.ApiLog;
import dev.dong4j.zeka.starter.logsystem.entity.ErrorLog;
import dev.dong4j.zeka.starter.logsystem.entity.SystemLog;

/**
 * <p>Description: 日志存储适配器 </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 21:17
 * @since 1.0.0
 */
public abstract class AbstractLogStorageService implements LogStorageService {
    /**
     * Save usual log boolean
     *
     * @param log log
     * @return the boolean
     * @since 1.0.0
     */
    @Override
    public Boolean save(SystemLog log) {
        return true;
    }

    /**
     * Save api log boolean
     *
     * @param log log
     * @return the boolean
     * @since 1.0.0
     */
    @Override
    public Boolean save(ApiLog log) {
        return true;
    }

    /**
     * Save error log boolean
     *
     * @param log log
     * @return the boolean
     * @since 1.0.0
     */
    @Override
    public Boolean save(ErrorLog log) {
        return true;
    }
}
