package dev.dong4j.zeka.starter.mybatis.support;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import dev.dong4j.zeka.kernel.common.api.BaseCodes;
import dev.dong4j.zeka.kernel.common.base.BaseQuery;
import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import dev.dong4j.zeka.kernel.common.mybatis.support.Page;
import dev.dong4j.zeka.kernel.common.util.ConfigKit;
import dev.dong4j.zeka.kernel.common.util.Tools;
import java.util.Arrays;
import java.util.Map;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Description: 分页工具 </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.24 15:33
 * @since 1.0.0
 */
@Slf4j
@UtilityClass
public class Condition {

    /**
     * 转化成mybatis plus中的Page
     *
     * @param <T>   the type parameter
     * @param query the query
     * @return page page
     * @since 1.0.0
     */
    @NotNull
    public static <T> IPage<T> getPage(@NotNull BaseQuery<?> query) {
        Page<T> page = new Page<>(
            Tools.toLong(query.getPage(), ConfigKit.getLongValue(ConfigKey.MybatisConfigKey.PAGE, 1L)),
            Tools.toLong(query.getLimit(), ConfigKit.getLongValue(ConfigKey.MybatisConfigKey.LIMIT, 10L)));

        checkTime(query);

        Arrays.stream(Tools.toStrArray(SqlKeyword.filter(query.getAscs()))).forEach(o -> page.addOrder(OrderItem.asc(o)));
        Arrays.stream(Tools.toStrArray(SqlKeyword.filter(query.getDescs()))).forEach(o -> page.addOrder(OrderItem.desc(o)));
        return page;
    }

    /**
     * 时间检查: 开始时间不能大于结束时间
     *
     * @param query query
     * @since 2024.2.0
     */
    public static void checkTime(@NotNull BaseQuery<?> query) {
        if (query.getStartTime() != null && query.getEndTime() != null) {
            BaseCodes.PARAM_VERIFY_ERROR.isTrue(query.getStartTime().before(query.getEndTime()), "开始时间必须在结束时间之前");
        }
    }

    /**
     * 获取 mybatis plus 中的 QueryWrapper
     *
     * @param <T>    the type parameter
     * @param entity the entity
     * @return query wrapper
     * @since 1.0.0
     */
    @NotNull
    @Contract("_ -> new")
    public static <T> QueryWrapper<T> getQueryWrapper(T entity) {
        return new QueryWrapper<>(entity);
    }

    /**
     * 获取 mybatis plus中的 QueryWrapper
     *
     * @param <T>   the type parameter
     * @param query the query
     * @param clazz the clazz
     * @return query wrapper
     * @since 1.0.0
     */
    public static <T> @NotNull QueryWrapper<T> getQueryWrapper(@NotNull Map<String, Object> query, Class<T> clazz) {
        query.remove("page");
        query.remove("limit");
        query.remove("descs");
        query.remove("ascs");
        QueryWrapper<T> qw = new QueryWrapper<>();
        qw.setEntity(Tools.newInstance(clazz));
        SqlKeyword.buildCondition(query, qw);
        return qw;
    }

}
