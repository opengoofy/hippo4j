package cn.hippo4j.common.api;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Client close hook execute.
 *
 * @author chen.ma
 * @date 2022/1/6 22:14
 */
public interface ClientCloseHookExecute {

    /**
     * Client close hook function execution.
     *
     * @param req
     */
    void closeHook(ClientCloseHookReq req);

    @Data
    @Accessors(chain = true)
    class ClientCloseHookReq {

        /**
         * appName
         */
        private String appName;

        /**
         * instanceId
         */
        private String instanceId;

        /**
         * groupKey
         */
        private String groupKey;

    }

}
