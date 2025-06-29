package dev.dong4j.zeka.starter.rest.support;

import dev.dong4j.zeka.kernel.auth.CurrentUser;
import dev.dong4j.zeka.kernel.auth.util.AuthUtils;
import dev.dong4j.zeka.kernel.auth.util.JwtUtils;
import dev.dong4j.zeka.kernel.common.util.StringUtils;
import javax.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.09.11 13:06
 * @since 1.6.0
 */
public interface CurrentUserService {

    /**
     * 通过 token 获取用户信息
     *
     * @param token token
     * @return the current user
     * @since 1.6.0
     */
    default CurrentUser getCurrentUser(String token) {
        return JwtUtils.PlayGround.getUser(token);
    }

    /**
     * 从 request 获取用户信息
     *
     * @param request request
     * @return the current user
     * @since 1.6.0
     */
    default CurrentUser getCurrentUser(@NotNull HttpServletRequest request) {
        String token = AuthUtils.getToken(request);

        if (StringUtils.isNotBlank(token)) {
            return this.getCurrentUser(token);
        }
        return null;
    }
}
