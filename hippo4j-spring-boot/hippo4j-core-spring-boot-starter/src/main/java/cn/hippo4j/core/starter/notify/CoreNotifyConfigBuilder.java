package cn.hippo4j.core.starter.notify;

import cn.hippo4j.common.notify.AlarmControlHandler;
import cn.hippo4j.common.api.NotifyConfigBuilder;
import cn.hippo4j.common.notify.NotifyConfigDTO;
import cn.hippo4j.core.starter.config.BootstrapCoreProperties;
import cn.hippo4j.core.starter.config.ExecutorProperties;
import cn.hippo4j.core.starter.config.NotifyPlatformProperties;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Core notify config builder.
 *
 * @author chen.ma
 * @date 2022/2/25 00:24
 */
@AllArgsConstructor
public class CoreNotifyConfigBuilder implements NotifyConfigBuilder {

    private final AlarmControlHandler alarmControlHandler;

    private final BootstrapCoreProperties bootstrapCoreProperties;

    @Override
    public Map<String, List<NotifyConfigDTO>> buildNotify() {
        Map<String, List<NotifyConfigDTO>> resultMap = Maps.newHashMap();

        List<ExecutorProperties> executors = bootstrapCoreProperties.getExecutors();
        if (null !=executors) {
            for (ExecutorProperties executor : executors) {
                resultMap.putAll(buildSingleNotifyConfig(executor));
            }
        }
        return resultMap;
    }

    /**
     * Build single notify config.
     *
     * @param executor
     * @return
     */
    public Map<String, List<NotifyConfigDTO>> buildSingleNotifyConfig(ExecutorProperties executor) {
        Map<String, List<NotifyConfigDTO>> resultMap = Maps.newHashMap();
        String threadPoolId = executor.getThreadPoolId();
        String alarmBuildKey = threadPoolId + "+ALARM";
        List<NotifyConfigDTO> alarmNotifyConfigs = Lists.newArrayList();

        List<NotifyPlatformProperties> notifyPlatforms = bootstrapCoreProperties.getNotifyPlatforms();
        for (NotifyPlatformProperties platformProperties : notifyPlatforms) {
            NotifyConfigDTO notifyConfig = new NotifyConfigDTO();
            notifyConfig.setPlatform(platformProperties.getPlatform());
            notifyConfig.setThreadPoolId(threadPoolId);
            notifyConfig.setType("ALARM");
            notifyConfig.setSecretKey(platformProperties.getSecretKey());
            notifyConfig.setInterval(executor.getNotify().getInterval());
            Map<String, String> receives = executor.getNotify().getReceives();
            String receive = receives.get(platformProperties.getPlatform());
            if (StrUtil.isBlank(receive)) {
                receive = platformProperties.getReceives();
            }
            notifyConfig.setReceives(receive);
            alarmNotifyConfigs.add(notifyConfig);
        }

        resultMap.put(alarmBuildKey, alarmNotifyConfigs);

        String changeBuildKey = threadPoolId + "+CONFIG";
        List<NotifyConfigDTO> changeNotifyConfigs = Lists.newArrayList();

        for (NotifyPlatformProperties platformProperties : notifyPlatforms) {
            NotifyConfigDTO notifyConfig = new NotifyConfigDTO();
            notifyConfig.setPlatform(platformProperties.getPlatform());
            notifyConfig.setThreadPoolId(threadPoolId);
            notifyConfig.setType("CONFIG");
            notifyConfig.setSecretKey(platformProperties.getSecretKey());

            Map<String, String> receives = executor.getNotify().getReceives();
            String receive = receives.get(platformProperties.getPlatform());
            if (StrUtil.isBlank(receive)) {
                receive = platformProperties.getReceives();
            }
            notifyConfig.setReceives(receive);
            changeNotifyConfigs.add(notifyConfig);
        }

        resultMap.put(changeBuildKey, changeNotifyConfigs);

        resultMap.forEach(
                (key, val) -> val.stream()
                        .filter(each -> StrUtil.equals("ALARM", each.getType()))
                        .forEach(each -> alarmControlHandler.initCacheAndLock(each.getThreadPoolId(), each.getPlatform(), each.getInterval()))
        );

        return resultMap;
    }

}
