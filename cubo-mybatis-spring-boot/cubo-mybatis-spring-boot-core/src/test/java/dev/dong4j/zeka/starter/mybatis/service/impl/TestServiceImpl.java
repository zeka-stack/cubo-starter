package dev.dong4j.zeka.starter.mybatis.service.impl;

import dev.dong4j.zeka.kernel.common.base.CrudDelegateImpl;
import dev.dong4j.zeka.starter.mybatis.entity.dto.TestDTO;
import dev.dong4j.zeka.starter.mybatis.repository.TestRepositoryService;
import dev.dong4j.zeka.starter.mybatis.service.TestService;
import org.springframework.stereotype.Service;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.03.16 22:58
 * @since 1.8.0
 */
@Service
public class TestServiceImpl extends CrudDelegateImpl<TestRepositoryService, TestDTO> implements TestService {
}
