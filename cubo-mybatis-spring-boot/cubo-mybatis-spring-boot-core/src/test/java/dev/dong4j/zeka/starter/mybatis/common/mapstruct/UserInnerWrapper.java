package dev.dong4j.zeka.starter.mybatis.common.mapstruct;

import dev.dong4j.zeka.kernel.common.enums.SerializeEnum;
import dev.dong4j.zeka.kernel.common.mapstruct.Converter;
import dev.dong4j.zeka.kernel.common.mapstruct.DeletedEnumConverter;
import dev.dong4j.zeka.kernel.common.mapstruct.EnabledEnumConverter;
import dev.dong4j.zeka.kernel.common.mapstruct.EntityEnumConverter;
import dev.dong4j.zeka.starter.mybatis.common.entity.dto.UserDTO;
import dev.dong4j.zeka.starter.mybatis.common.entity.enums.GenderEnum;
import dev.dong4j.zeka.starter.mybatis.common.entity.po.User;
import java.io.Serializable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:09
 * @since 1.0.0
 */
@Mapper(uses = {
    UserInnerWrapper.GenderEnumConverter.class,
    DeletedEnumConverter.class,
    EnabledEnumConverter.class}
)
public interface UserInnerWrapper extends Converter<User, UserDTO> {

    /**
     * po -> dto: UserInnerWrapper.INSTANCE.to(po);
     * dto -> po: UserInnerWrapper.INSTANCE.from(dto);
     */
    UserInnerWrapper INSTANCE = Mappers.getMapper(UserInnerWrapper.class);

    /**
     * 正向转化 source -> tageter
     * 自动匹配 {@link EntityEnumConverter#toValue(SerializeEnum)
     *
     * @param s the s
     * @return the t
     * @since 1.0.0
     */
    @Override
    @Mapping(target = "gender", qualifiedByName = {"GenderEnumConverter"})
    @Mapping(target = "enable", qualifiedByName = {"EnabledEnumConverter"})
    @Mapping(target = "deleted", qualifiedByName = {"DeletedEnumConverter"})
    UserDTO to(User s);

    /**
     * 逆向转化 tageter -> source
     * 自动匹配 {@link EntityEnumConverter#fromValue(Serializable)} (EntityEnum)}
     *
     * @param t the t
     * @return the s
     * @since 1.0.0
     */
    @Override
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "gender", qualifiedByName = {"GenderEnumConverter"})
    @Mapping(target = "enable", qualifiedByName = {"EnabledEnumConverter"})
    @Mapping(target = "deleted", qualifiedByName = {"DeletedEnumConverter"})
    User from(UserDTO t);

    /**
     * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.01.27 18:09
     * @since 1.0.0
     */
    @Named("GenderEnumConverter")
    class GenderEnumConverter extends EntityEnumConverter<GenderEnum, Integer> {
        /**
         * Gender enum converter
         *
         * @since 1.9.0
         */
        public GenderEnumConverter() {
            super(GenderEnum.class);
        }
    }
}
