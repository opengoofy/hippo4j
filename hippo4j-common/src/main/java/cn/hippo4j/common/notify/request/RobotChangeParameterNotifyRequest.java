package cn.hippo4j.common.notify.request;

import lombok.Data;

/**
 * Robot change parameter notify request.
 *
 * @author chen.ma
 * @date 2022/2/22 21:03
 */
@Data
public class RobotChangeParameterNotifyRequest extends ChangeParameterNotifyRequest {

    /**
     * secretKey
     */
    private String secretKey;

}
