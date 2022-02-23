package cn.hippo4j.common.notify.request;

import lombok.Data;

/**
 * Robot alarm notify request.
 *
 * @author chen.ma
 * @date 2022/2/22 21:50
 */
@Data
public class RobotAlarmNotifyRequest extends AlarmNotifyRequest {

    /**
     * secretKey
     */
    private String secretKey;

}
