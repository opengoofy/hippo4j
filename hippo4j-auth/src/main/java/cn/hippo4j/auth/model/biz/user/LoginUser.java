package cn.hippo4j.auth.model.biz.user;

import lombok.Data;

/**
 * Login user.
 *
 * @author chen.ma
 * @date 2021/11/9 22:41
 */
@Data
public class LoginUser {

    /**
     * username
     */
    private String username;

    /**
     * password
     */
    private String password;

    /**
     * rememberMe
     */
    private Integer rememberMe;

}
