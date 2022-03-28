package cn.hippo4j.starter.notify;

import cn.hippo4j.common.notify.AlarmControlHandler;
import cn.hippo4j.common.api.NotifyConfigBuilder;
import cn.hippo4j.common.notify.NotifyConfigDTO;
import cn.hippo4j.common.notify.ThreadPoolNotifyDTO;
import cn.hippo4j.common.notify.request.ThreadPoolNotifyRequest;
import cn.hippo4j.common.toolkit.GroupKey;
import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.common.web.base.Result;
import cn.hippo4j.core.executor.manage.GlobalThreadPoolManage;
import cn.hippo4j.starter.config.BootstrapProperties;
import cn.hippo4j.starter.remote.HttpAgent;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import static cn.hippo4j.common.constant.Constants.BASE_PATH;

/**
 * Server notify config builder.
 *
 * @author chen.ma
 * @date 2022/2/24 19:57
 */
@Slf4j
@AllArgsConstructor
public class ServerNotifyConfigBuilder implements NotifyConfigBuilder {

    private final HttpAgent httpAgent;

    private final BootstrapProperties properties;

    private final AlarmControlHandler alarmControlHandler;

    @Override
    public Map<String, List<NotifyConfigDTO>> buildNotify() {
        Map<String, List<NotifyConfigDTO>> resultMap = Maps.newHashMap();
        List<String> threadPoolIds = GlobalThreadPoolManage.listThreadPoolId();
        if (CollUtil.isEmpty(threadPoolIds)) {
            log.warn("The client does not have a dynamic thread pool instance configured.");
            return resultMap;
        }

        List<String> groupKeys = Lists.newArrayList();
        threadPoolIds.forEach(each -> {
            String groupKey = GroupKey.getKeyTenant(each, properties.getItemId(), properties.getNamespace());
            groupKeys.add(groupKey);
        });

        Result result = null;
        try {
            result = httpAgent.httpPostByDiscovery(BASE_PATH + "/notify/list/config", new ThreadPoolNotifyRequest(groupKeys));
        } catch (Throwable ex) {
            log.error("Get dynamic thread pool notify configuration error. message :: {}", ex.getMessage());
        }

        if (result != null && result.isSuccess() && result.getData() != null) {
            String resultDataStr = JSONUtil.toJSONString(result.getData());
            List<ThreadPoolNotifyDTO> resultData = JSONUtil.parseArray(resultDataStr, ThreadPoolNotifyDTO.class);
            resultData.forEach(each -> resultMap.put(each.getNotifyKey(), each.getNotifyList()));

            resultMap.forEach((key, val) ->
                    val.stream().filter(each -> StrUtil.equals("ALARM", each.getType()))
                            .forEach(each -> alarmControlHandler.initCacheAndLock(each.getTpId(), each.getPlatform(), each.getInterval()))
            );
        }

        return resultMap;
    }

}
