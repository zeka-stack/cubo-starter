package dev.dong4j.zeka.starter.mybatis.common.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import dev.dong4j.zeka.kernel.common.enums.EnabledEnum;
import dev.dong4j.zeka.starter.mybatis.base.BaseExtendPO;
import dev.dong4j.zeka.starter.mybatis.common.entity.enums.GenderEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>Description: 用户信息表 实体类  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:10
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("user")
public class User extends BaseExtendPO<Long, User> {

    /** GENDER */
    public static final String GENDER = "gender";
    /** ENABLE */
    public static final String ENABLE = "enable";
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    /** 性别 */
    private GenderEnum gender;
    /** Enable */
    private EnabledEnum enable;
}
