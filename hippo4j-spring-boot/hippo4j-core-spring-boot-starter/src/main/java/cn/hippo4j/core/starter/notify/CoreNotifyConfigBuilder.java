package cn.hippo4j.core.starter.notify;

import cn.hippo4j.common.api.NotifyConfigBuilder;
import cn.hippo4j.common.notify.AlarmControlHandler;
import cn.hippo4j.common.notify.NotifyConfigDTO;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.core.starter.config.BootstrapCoreProperties;
import cn.hippo4j.core.starter.config.ExecutorProperties;
import cn.hippo4j.core.starter.config.NotifyPlatformProperties;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        if (null != executors) {
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
            notifyConfig.setTpId(threadPoolId);
            notifyConfig.setType("ALARM");
            notifyConfig.setSecret(platformProperties.getSecret());
            notifyConfig.setSecretKey(getToken(platformProperties));
            int interval = Optional.ofNullable(executor.getNotify())
                    .map(each -> each.getInterval())
                    .orElseGet(() -> bootstrapCoreProperties.getAlarmInterval() != null ? bootstrapCoreProperties.getAlarmInterval() : 5);
            notifyConfig.setInterval(interval);
            notifyConfig.setReceives(buildReceive(executor, platformProperties));
            alarmNotifyConfigs.add(notifyConfig);
        }
        resultMap.put(alarmBuildKey, alarmNotifyConfigs);

        String changeBuildKey = threadPoolId + "+CONFIG";
        List<NotifyConfigDTO> changeNotifyConfigs = Lists.newArrayList();

        for (NotifyPlatformProperties platformProperties : notifyPlatforms) {
            NotifyConfigDTO notifyConfig = new NotifyConfigDTO();
            notifyConfig.setPlatform(platformProperties.getPlatform());
            notifyConfig.setTpId(threadPoolId);
            notifyConfig.setType("CONFIG");
            notifyConfig.setSecretKey(getToken(platformProperties));
            notifyConfig.setSecret(platformProperties.getSecret());
            notifyConfig.setReceives(buildReceive(executor, platformProperties));
            changeNotifyConfigs.add(notifyConfig);
        }
        resultMap.put(changeBuildKey, changeNotifyConfigs);

        resultMap.forEach(
                (key, val) -> val.stream()
                        .filter(each -> StrUtil.equals("ALARM", each.getType()))
                        .forEach(each -> alarmControlHandler.initCacheAndLock(each.getTpId(), each.getPlatform(), each.getInterval()))
        );

        return resultMap;
    }

    private String buildReceive(ExecutorProperties executor, NotifyPlatformProperties platformProperties) {
        String receive;
        if (executor.getNotify() != null) {
            receive = executor.getNotify().getReceive();
            if (StrUtil.isBlank(receive)) {
                receive = bootstrapCoreProperties.getReceive();
                if (StrUtil.isBlank(receive)) {
                    Map<String, String> receives = executor.receives();
                    receive = receives.get(platformProperties.getPlatform());
                }
            }
        } else {
            receive = bootstrapCoreProperties.getReceive();
            if (StrUtil.isBlank(receive)) {
                Map<String, String> receives = executor.receives();
                receive = receives.get(platformProperties.getPlatform());
            }
        }

        return receive;
    }

    private String getToken(NotifyPlatformProperties platformProperties) {
        return StringUtil.isNotBlank(platformProperties.getToken()) ? platformProperties.getToken() : platformProperties.getSecretKey();
    }

}
