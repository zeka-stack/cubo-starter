package dev.dong4j.zeka.starter.mybatis.repository;

import dev.dong4j.zeka.kernel.common.base.IRepositoryService;
import dev.dong4j.zeka.starter.mybatis.entity.dto.TestDTO;
import dev.dong4j.zeka.starter.mybatis.entity.po.Test;
import dev.dong4j.zeka.starter.mybatis.service.IExchangeService;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.03.15 19:31
 * @since 1.8.0
 */
public interface TestRepositoryService extends IRepositoryService<TestDTO>, IExchangeService<Test, TestDTO> {
}
