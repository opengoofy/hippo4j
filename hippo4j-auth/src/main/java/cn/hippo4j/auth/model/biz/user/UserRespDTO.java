package cn.hippo4j.auth.model.biz.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * User resp dto.
 *
 * @author chen.ma
 * @date 2021/10/30 21:51
 */
@Data
public class UserRespDTO {

    /**
     * userName
     */
    private String userName;

    /**
     * role
     */
    private String role;

    /**
     * gmtCreate
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtCreate;

    /**
     * gmtModified
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtModified;

}
