package dev.dong4j.zeka.starter.mybatis.provider.impl;

import dev.dong4j.zeka.kernel.common.base.CrudDelegateImpl;
import dev.dong4j.zeka.starter.mybatis.entity.dto.TestDTO;
import dev.dong4j.zeka.starter.mybatis.provider.TestProvider;
import dev.dong4j.zeka.starter.mybatis.repository.TestRepositoryService;
import org.springframework.stereotype.Component;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.03.16 09:48
 * @since 1.0.0
 */
@Component
public class TestProviderImpl extends CrudDelegateImpl<TestRepositoryService, TestDTO> implements TestProvider {

}
