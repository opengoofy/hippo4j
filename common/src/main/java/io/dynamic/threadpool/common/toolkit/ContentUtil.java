package io.dynamic.threadpool.common.toolkit;

import io.dynamic.threadpool.common.model.PoolParameter;

/**
 * Content Util.
 *
 * @author chen.ma
 * @date 2021/6/24 16:13
 */
public class ContentUtil {

    public static String getPoolContent(PoolParameter parameter) {
        StringBuilder stringBuilder = new StringBuilder();
        String targetStr = stringBuilder.append(parameter.getCoreSize())
                .append(parameter.getMaxSize())
                .append(parameter.getQueueType())
                .append(parameter.getCapacity())
                .append(parameter.getKeepAliveTime())
                .append(parameter.getIsAlarm())
                .append(parameter.getCapacityAlarm())
                .append(parameter.getLivenessAlarm())
                .toString();
        return targetStr;
    }
}
