package io.dynamic.threadpool.common.toolkit;

import com.alibaba.fastjson.JSON;
import io.dynamic.threadpool.common.constant.Constants;
import io.dynamic.threadpool.common.model.PoolParameter;
import io.dynamic.threadpool.common.model.PoolParameterInfo;

/**
 * Content Util.
 *
 * @author chen.ma
 * @date 2021/6/24 16:13
 */
public class ContentUtil {

    /*public static String getPoolContent(PoolParameter parameter) {
        StringBuilder stringBuilder = new StringBuilder();
        String resultStr = stringBuilder.append(parameter.getCoreSize())
                .append(parameter.getMaxSize())
                .append(parameter.getQueueType())
                .append(parameter.getCapacity())
                .append(parameter.getKeepAliveTime())
                .append(parameter.getIsAlarm())
                .append(parameter.getCapacityAlarm())
                .append(parameter.getLivenessAlarm())
                .toString();
        return resultStr;
    }*/

    public static String getPoolContent(PoolParameter parameter) {
        PoolParameterInfo poolInfo = new PoolParameterInfo();
        poolInfo.setNamespace(parameter.getNamespace());
        poolInfo.setItemId(parameter.getItemId());
        poolInfo.setTpId(parameter.getTpId());
        poolInfo.setCoreSize(parameter.getCoreSize());
        poolInfo.setMaxSize(parameter.getMaxSize());
        poolInfo.setQueueType(parameter.getQueueType());
        poolInfo.setCapacity(parameter.getCapacity());
        poolInfo.setKeepAliveTime(parameter.getKeepAliveTime());
        poolInfo.setIsAlarm(parameter.getIsAlarm());
        poolInfo.setCapacityAlarm(parameter.getCapacityAlarm());
        poolInfo.setLivenessAlarm(parameter.getLivenessAlarm());
        return JSON.toJSONString(poolInfo);
    }

    public static String getGroupKey(PoolParameter parameter) {
        StringBuilder stringBuilder = new StringBuilder();
        String resultStr = stringBuilder.append(parameter.getTpId())
                .append(Constants.GROUP_KEY_DELIMITER)
                .append(parameter.getItemId())
                .append(Constants.GROUP_KEY_DELIMITER)
                .append(parameter.getNamespace())
                .toString();
        return resultStr;
    }
}
