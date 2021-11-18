package cn.hippo4j.starter.alarm;

import cn.hippo4j.common.model.PoolParameterInfo;
import cn.hippo4j.starter.core.DynamicThreadPoolExecutor;

/**
 * Send msg.
 *
 * @author chen.ma
 * @date 2021/8/15 15:31
 */
public interface SendMessageService {

    /**
     * Send alarm message.
     *
     * @param typeEnum
     * @param threadPoolExecutor
     */
    void sendAlarmMessage(MessageTypeEnum typeEnum, DynamicThreadPoolExecutor threadPoolExecutor);

    /**
     * Send change message.
     *
     * @param parameter
     */
    void sendChangeMessage(PoolParameterInfo parameter);

}
