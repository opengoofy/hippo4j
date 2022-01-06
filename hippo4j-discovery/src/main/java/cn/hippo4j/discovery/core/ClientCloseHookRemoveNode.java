package cn.hippo4j.discovery.core;

import cn.hippo4j.common.api.ClientCloseHookExecute;
import cn.hippo4j.common.model.InstanceInfo;
import cn.hippo4j.common.toolkit.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Client close hook remove node.
 *
 * @author chen.ma
 * @date 2022/1/6 22:24
 */
@Slf4j
@Component
@AllArgsConstructor
public class ClientCloseHookRemoveNode implements ClientCloseHookExecute {

    private final InstanceRegistry instanceRegistry;

    @Override
    public void closeHook(ClientCloseHookReq req) {
        log.info(
                "Remove Node, Execute client hook function. Req :: {}",
                JSONUtil.toJSONString(req)
        );

        try {
            InstanceInfo instanceInfo = new InstanceInfo();
            instanceInfo.setAppName(req.getAppName()).setInstanceId(req.getInstanceId());
            instanceRegistry.remove(instanceInfo);
        } catch (Exception ex) {
            log.error("Failed to delete node hook.", ex);
        }
    }

}
