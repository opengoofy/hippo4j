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

    public static String getPoolContent(PoolParameter parameter) {
        PoolParameterInfo poolInfo = new PoolParameterInfo();
        poolInfo.setTenantId(parameter.getTenantId())
                .setItemId(parameter.getItemId())
                .setTpId(parameter.getTpId())
                .setCoreSize(parameter.getCoreSize())
                .setMaxSize(parameter.getMaxSize())
                .setQueueType(parameter.getQueueType())
                .setCapacity(parameter.getCapacity())
                .setKeepAliveTime(parameter.getKeepAliveTime())
                .setIsAlarm(parameter.getIsAlarm())
                .setCapacityAlarm(parameter.getCapacityAlarm())
                .setLivenessAlarm(parameter.getLivenessAlarm())
                .setRejectedType(parameter.getRejectedType());
        return JSON.toJSONString(poolInfo);
    }

    public static String getGroupKey(PoolParameter parameter) {
        StringBuilder stringBuilder = new StringBuilder();
        String resultStr = stringBuilder.append(parameter.getTpId())
                .append(Constants.GROUP_KEY_DELIMITER)
                .append(parameter.getItemId())
                .append(Constants.GROUP_KEY_DELIMITER)
                .append(parameter.getTenantId())
                .toString();
        return resultStr;
    }
}
