package cn.hippo4j.auth.model.biz.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * User req dto.
 *
 * @author chen.ma
 * @date 2021/11/11 20:30
 */
@Data
@Accessors(chain = true)
public class UserReqDTO extends Page {

    /**
     * userName
     */
    private String userName;

    /**
     * password
     */
    private String password;

    /**
     * role
     */
    private String role;

}
