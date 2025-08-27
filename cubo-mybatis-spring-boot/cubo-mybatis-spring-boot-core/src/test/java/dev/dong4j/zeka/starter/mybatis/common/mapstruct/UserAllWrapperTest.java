package dev.dong4j.zeka.starter.mybatis.common.mapstruct;

import dev.dong4j.zeka.kernel.common.enums.DeletedEnum;
import dev.dong4j.zeka.kernel.common.enums.EnabledEnum;
import dev.dong4j.zeka.kernel.common.exception.LowestException;
import dev.dong4j.zeka.kernel.common.util.EnumUtils;
import dev.dong4j.zeka.starter.mybatis.common.entity.dto.UserDTO;
import dev.dong4j.zeka.starter.mybatis.common.entity.enums.GenderEnum;
import dev.dong4j.zeka.starter.mybatis.common.entity.po.User;
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
 * @date 2020.01.26 20:16
 * @since 1.0.0
 */
@Slf4j
class UserAllWrapperTest {

    /**
     * dto -> po
     * 最终调用 GenderEnum fromValue(Integer value) 来转换枚举关系
     *
     * @since 1.0.0
     */
    @Test
    void testDtoToPo() {
        UserDTO dto = UserDTO.builder().id(1L).gender(1).deleted(Boolean.FALSE).enable(Boolean.TRUE).build();
        User po = UserAllWrapper.INSTANCE.po(dto);
        log.info("po = [{}]", po);
        Assertions.assertEquals(po.getGender(),
            EnumUtils.of(GenderEnum.class, g -> g.getValue().equals(dto.getGender())).orElseThrow(LowestException::new));
        Assertions.assertEquals(po.getEnable(),
            EnumUtils.of(EnabledEnum.class, g -> g.getValue().equals(dto.getEnable())).orElseThrow(LowestException::new));
        Assertions.assertEquals(po.getDeleted(), EnumUtils.of(DeletedEnum.class, g -> g.getValue().equals(dto.getDeleted())).orElseThrow(LowestException::new));
    }

    /**
     * Test dto to vo
     *
     * @since 1.0.0
     */
    @Test
    void testDtoToVo() {
        UserDTO dto = UserDTO.builder().id(1L).gender(1).enable(Boolean.TRUE).deleted(Boolean.FALSE).build();
        UserVO vo = UserAllWrapper.INSTANCE.vo(dto);
        log.info("vo = [{}]", vo);
        Assertions.assertEquals(vo.getGender(),
            EnumUtils.of(GenderEnum.class, g -> g.getValue().equals(dto.getGender())).orElseThrow(LowestException::new).getDesc());
        Assertions.assertEquals(vo.getEnable(),
            EnumUtils.of(EnabledEnum.class, g -> g.getValue().equals(dto.getEnable())).orElseThrow(LowestException::new).getDesc());
        Assertions.assertEquals(vo.getDeleted(), EnumUtils.of(DeletedEnum.class, g -> g.getValue().equals(dto.getDeleted())).orElseThrow(LowestException::new).getDesc());
    }

    /**
     * Test po to vo
     *
     * @since 1.0.0
     */
    @Test
    void testPoToVo() {
        User po = new User().setGender(GenderEnum.MAN);
        UserVO vo = UserAllWrapper.INSTANCE.vo(po);
        log.info("vo = [{}]", vo);
        Assertions.assertEquals(vo.getGender(), po.getGender().getDesc());
    }

    /**
     * Test po to dto
     *
     * @since 1.0.0
     */
    @Test
    void testPoToDto() {
        User po = new User().setGender(GenderEnum.MAN);
        UserDTO dto = UserAllWrapper.INSTANCE.dto(po);
        log.info("dto = [{}]", dto);
        Assertions.assertEquals(dto.getGender(), po.getGender().getValue());
    }

    /**
     * Test vo to po
     *
     * @since 1.0.0
     */
    @Test
    void testVoToPo() {
        UserVO vo = new UserVO().setGender("男");
        User po = UserAllWrapper.INSTANCE.po(vo);
        log.info("po = [{}]", po);
        Assertions.assertEquals(po.getGender(), EnumUtils.of(GenderEnum.class, g -> g.getDesc().equals(vo.getGender())).orElseThrow(LowestException::new));
    }

    /**
     * Test vo to dto
     *
     * @since 1.0.0
     */
    @Test
    void testVoToDto() {
        UserVO vo = new UserVO().setGender("男");
        UserDTO dto = UserAllWrapper.INSTANCE.dto(vo);
        log.info("dto = [{}]", dto);
        Assertions.assertEquals(dto.getGender(), EnumUtils.of(GenderEnum.class, g -> g.getDesc().equals(vo.getGender())).orElseThrow(LowestException::new).getValue());
    }

}
