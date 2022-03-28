package cn.hippo4j.config.service.handler;

import cn.hippo4j.common.api.ClientCloseHookExecute;
import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.config.service.ConfigCacheService;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Client close hook remove config cache.
 *
 * @author chen.ma
 * @date 2022/1/6 22:20
 */
@Slf4j
@Component
public class ClientCloseHookRemoveConfigCache implements ClientCloseHookExecute {

    @Override
    public void closeHook(ClientCloseHookReq req) {
        log.info(
                "Remove Config Cache, Execute client hook function. Req :: {}",
                JSONUtil.toJSONString(req)
        );

        try {
            String groupKey = req.getGroupKey();
            if (StrUtil.isNotBlank(groupKey)) {
                ConfigCacheService.removeConfigCache(groupKey);
            }
        } catch (Exception ex) {
            log.error("Failed to remove config cache hook.", ex);
        }
    }

}
