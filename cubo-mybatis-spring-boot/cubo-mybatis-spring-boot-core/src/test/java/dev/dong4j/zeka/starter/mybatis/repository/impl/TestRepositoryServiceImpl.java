package dev.dong4j.zeka.starter.mybatis.repository.impl;

import dev.dong4j.zeka.starter.mybatis.converter.TestServiceConverter;
import dev.dong4j.zeka.starter.mybatis.dao.TestDao;
import dev.dong4j.zeka.starter.mybatis.entity.dto.TestDTO;
import dev.dong4j.zeka.starter.mybatis.entity.po.Test;
import dev.dong4j.zeka.starter.mybatis.repository.TestRepositoryService;
import dev.dong4j.zeka.starter.mybatis.service.impl.ExchangeServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.03.15 19:31
 * @since 1.8.0
 */
@Service
public class TestRepositoryServiceImpl extends ExchangeServiceImpl<TestDao, Test, TestDTO, TestServiceConverter>
    implements TestRepositoryService {
}
