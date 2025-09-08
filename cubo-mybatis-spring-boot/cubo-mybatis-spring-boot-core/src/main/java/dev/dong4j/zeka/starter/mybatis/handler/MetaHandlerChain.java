package dev.dong4j.zeka.starter.mybatis.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import dev.dong4j.zeka.kernel.common.util.CollectionUtils;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.apache.ibatis.reflection.MetaObject;

/**
 * 元数据处理器链
 *
 * 该类实现了 MyBatis Plus 的 MetaObjectHandler 接口，用于管理多个元数据处理器。
 * 通过责任链模式，将多个处理器组合在一起，统一处理字段的自动填充。
 *
 * 主要功能：
 * 1. 管理多个 MetaObjectChain 处理器
 * 2. 在数据插入时调用所有处理器的 insertFill 方法
 * 3. 在数据更新时调用所有处理器的 updateFill 方法
 * 4. 提供统一的处理器调用机制
 *
 * 设计优势：
 * - 支持多个处理器的组合使用
 * - 处理器之间相互独立，便于扩展
 * - 统一的调用接口，简化配置
 * - 支持动态添加和移除处理器
 *
 * 使用场景：
 * - 需要同时处理多种字段填充的场景
 * - 时间字段、租户字段、客户端字段等的统一管理
 * - 复杂业务场景下的字段自动填充
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.04.07 20:39
 * @since 1.0.0
 */
@AllArgsConstructor
public class MetaHandlerChain implements MetaObjectHandler {
    /** Chains */
    private final List<MetaObjectChain> chains;

    /**
     * 新增数据执行
     *
     * @param metaObject the meta object
     * @since 1.0.0
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        if (CollectionUtils.isEmpty(this.chains)) {
            return;
        }

        this.chains.stream().filter(Objects::nonNull).forEach(c -> c.insertFill(metaObject, c));
    }

    /**
     * 更新数据执行
     *
     * @param metaObject the meta object
     * @since 1.0.0
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        if (CollectionUtils.isEmpty(this.chains)) {
            return;
        }

        this.chains.stream().filter(Objects::nonNull).forEach(c -> c.updateFill(metaObject, c));
    }

}
