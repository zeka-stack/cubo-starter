package dev.dong4j.zeka.starter.mybatis.support;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

/**
 * 基础实体包装器抽象类
 *
 * 该抽象类提供了实体对象和视图对象之间的转换功能，主要用于将
 * 数据库实体对象转换为前端展示的视图对象。
 *
 * 主要功能：
 * 1. 分页数据转换：将分页的实体对象转换为分页的视图对象
 * 2. 列表数据转换：将实体对象列表转换为视图对象列表
 * 3. 单个对象转换：提供抽象方法供子类实现具体转换逻辑
 *
 * 使用方式：
 * - 继承该抽象类并实现 entityVO 方法
 * - 定义具体的实体到视图的转换逻辑
 * - 利用提供的批量转换方法处理集合数据
 *
 * 注意：该类已标记为废弃，建议使用更现代的转换方式：
 * - 使用 MapStruct 进行对象映射
 * - 使用 ViewConverter 或 ServiceConverter 接口
 * - 采用更灵活的转换机制
 *
 * @param <E> 实体对象类型
 * @param <V> 视图对象类型
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:21
 * @since 1.0.0
 * @deprecated 建议使用 MapStruct 或其他现代转换工具
 */
@Deprecated
public abstract class BaseEntityWrapper<E, V> {

    /**
     * 分页实体类集合包装
     *
     * @param pages pages
     * @return page page
     * @since 1.0.0
     */
    public IPage<V> pageVO(@NotNull IPage<E> pages) {
        List<V> records = listVO(pages.getRecords());
        IPage<V> pageVo = new Page<>(pages.getCurrent(), pages.getSize(), pages.getTotal());
        pageVo.setRecords(records);
        return pageVo;
    }

    /**
     * 实体类集合包装
     *
     * @param list list
     * @return list list
     * @since 1.0.0
     */
    public List<V> listVO(@NotNull List<E> list) {
        return list.stream().map(this::entityVO).collect(Collectors.toList());
    }

    /**
     * 单个实体类包装
     *
     * @param entity entity
     * @return v v
     * @since 1.0.0
     */
    public abstract V entityVO(E entity);

}
