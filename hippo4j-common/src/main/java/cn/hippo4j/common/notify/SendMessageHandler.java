package cn.hippo4j.common.notify;

import cn.hippo4j.common.notify.request.base.NotifyRequest;

/**
 * Send message handler.
 *
 * @author chen.ma
 * @date 2021/8/15 15:44
 */
public interface SendMessageHandler<T extends NotifyRequest, R extends NotifyRequest> {

    /**
     * Get type.
     *
     * @return
     */
    String getType();

    /**
     * Send alarm message.
     *
     * @param notifyConfig
     * @param alarmNotifyRequest
     */
    void sendAlarmMessage(NotifyConfigDTO notifyConfig, T alarmNotifyRequest);

    /**
     * Send change message.
     *
     * @param notifyConfig
     * @param changeParameterNotifyRequest
     */
    void sendChangeMessage(NotifyConfigDTO notifyConfig, R changeParameterNotifyRequest);

}
