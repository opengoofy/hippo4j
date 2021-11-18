package cn.hippo4j.starter.alarm;

import cn.hippo4j.common.model.PoolParameterInfo;
import cn.hippo4j.starter.core.DynamicThreadPoolExecutor;

/**
 * Send message handler.
 *
 * @author chen.ma
 * @date 2021/8/15 15:44
 */
public interface SendMessageHandler {

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
     * @param threadPoolExecutor
     */
    void sendAlarmMessage(NotifyDTO notifyConfig, DynamicThreadPoolExecutor threadPoolExecutor);

    /**
     * Send change message.
     *
     * @param notifyConfig
     * @param parameter
     */
    void sendChangeMessage(NotifyDTO notifyConfig, PoolParameterInfo parameter);

}
