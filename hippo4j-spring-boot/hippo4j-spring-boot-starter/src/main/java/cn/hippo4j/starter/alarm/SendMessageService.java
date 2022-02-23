package cn.hippo4j.starter.alarm;

import cn.hippo4j.common.model.PoolParameterInfo;
import cn.hippo4j.common.notify.NotifyTypeEnum;
import cn.hippo4j.core.executor.DynamicThreadPoolExecutor;

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
    void sendAlarmMessage(NotifyTypeEnum typeEnum, DynamicThreadPoolExecutor threadPoolExecutor);

    /**
     * Send change message.
     *
     * @param parameter
     */
    void sendChangeMessage(PoolParameterInfo parameter);

}
