package io.dynamic.threadpool.server.toolkit;

import cn.hutool.crypto.digest.DigestUtil;
import io.dynamic.threadpool.server.model.ConfigAllInfo;


/**
 * Md5 配置组件
 *
 * @author chen.ma
 * @date 2021/6/21 18:32
 */
public class Md5ConfigUtil {

    /**
     * 获取 ThreadPool 相关内容 Md5 值
     *
     * @param config
     * @return
     */
    public String getTpContentMd5(ConfigAllInfo config) {
        StringBuilder stringBuilder = new StringBuilder();
        String targetStr = stringBuilder.append(config.getCoreSize())
                .append(config.getMaxSize())
                .append(config.getQueueType())
                .append(config.getCapacity())
                .append(config.getKeepAliveTime())
                .append(config.getIsAlarm())
                .append(config.getCapacityAlarm())
                .append(config.getLivenessAlarm())
                .toString();
        return DigestUtil.md5Hex(targetStr);
    }
}
