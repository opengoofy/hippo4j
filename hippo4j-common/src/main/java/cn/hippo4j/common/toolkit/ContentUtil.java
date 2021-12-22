package cn.hippo4j.common.toolkit;

import cn.hippo4j.common.constant.Constants;
import cn.hippo4j.common.model.PoolParameter;
import cn.hippo4j.common.model.PoolParameterInfo;

/**
 * Content util.
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
                .setAllowCoreThreadTimeOut(parameter.getAllowCoreThreadTimeOut())
                .setRejectedType(parameter.getRejectedType());
        return JSONUtil.toJSONString(poolInfo);
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

    public static String getGroupKey(String... parameters) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < parameters.length; i++) {
            stringBuilder.append(parameters[i]);

            if (i < parameters.length - 1) {
                stringBuilder.append(Constants.GROUP_KEY_DELIMITER);
            }
        }

        return stringBuilder.toString();
    }

}
