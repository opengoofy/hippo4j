package cn.hippo4j.common.notify;

import cn.hippo4j.common.notify.request.AlarmNotifyRequest;
import cn.hippo4j.common.notify.request.ChangeParameterNotifyRequest;

/**
 * Base send message service impl.
 *
 * @author chen.ma
 * @date 2022/2/22 21:32
 */
public class BaseSendMessageServiceImpl implements SendMessageService {

    @Override
    public void sendAlarmMessage(NotifyTypeEnum typeEnum, AlarmNotifyRequest alarmNotifyRequest) {

    }

    @Override
    public void sendChangeMessage(ChangeParameterNotifyRequest changeParameterNotifyRequest) {

    }

}
