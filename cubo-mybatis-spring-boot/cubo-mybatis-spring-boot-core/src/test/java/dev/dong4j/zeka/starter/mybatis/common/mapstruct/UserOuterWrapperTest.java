package dev.dong4j.zeka.starter.mybatis.common.mapstruct;

import dev.dong4j.zeka.kernel.common.exception.LowestException;
import dev.dong4j.zeka.kernel.common.util.EnumUtils;
import dev.dong4j.zeka.starter.mybatis.common.entity.dto.UserDTO;
import dev.dong4j.zeka.starter.mybatis.common.entity.enums.GenderEnum;
import dev.dong4j.zeka.starter.mybatis.common.entity.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:23
 * @since 1.0.0
 */
@Slf4j
class UserOuterWrapperTest {
    /**
     * Test vo to dto
     *
     * @since 1.0.0
     */
    @Test
    void testVoToDto() {
        UserVO vo = new UserVO().setGender("男");
        UserDTO dto = UserOuterWrapper.INSTANCE.to(vo);
        log.info("dto = [{}]", dto);
        Assertions.assertEquals(dto.getGender(),
            EnumUtils.of(GenderEnum.class, g -> g.getDesc().equals(vo.getGender())).orElseThrow(LowestException::new).getValue());
    }

    /**
     * Test dto to vo
     *
     * @since 1.0.0
     */
    @Test
    void testDtoToVo() {
        UserDTO dto = new UserDTO().setGender(1);
        UserVO vo = UserOuterWrapper.INSTANCE.from(dto);
        log.info("vo = [{}]", vo);
        Assertions.assertEquals(vo.getGender(),
            EnumUtils.of(GenderEnum.class, g -> g.getValue().equals(dto.getGender())).orElseThrow(LowestException::new).getDesc());
    }

}
