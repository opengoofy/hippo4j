package cn.hippo4j.common.notify;

import cn.hippo4j.common.notify.request.AlarmNotifyRequest;
import cn.hippo4j.common.notify.request.ChangeParameterNotifyRequest;

/**
 * Send message service.
 *
 * @author chen.ma
 * @date 2022/2/22 19:56
 */
public interface HippoSendMessageService {

    /**
     * Send alarm message.
     *
     * @param typeEnum
     * @param alarmNotifyRequest
     */
    void sendAlarmMessage(NotifyTypeEnum typeEnum, AlarmNotifyRequest alarmNotifyRequest);

    /**
     * Send change message.
     *
     * @param changeParameterNotifyRequest
     */
    void sendChangeMessage(ChangeParameterNotifyRequest changeParameterNotifyRequest);

}
