package cn.hippo4j.starter.monitor;

import cn.hippo4j.common.monitor.Message;
import cn.hippo4j.starter.remote.HttpAgent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static cn.hippo4j.common.constant.Constants.MONITOR_PATH;

/**
 * Http mvc sender.
 *
 * @author chen.ma
 * @date 2021/12/7 20:53
 */
@Slf4j
@AllArgsConstructor
public class HttpMvcSender implements MessageSender {

    private final HttpAgent httpAgent;

    @Override
    public void send(Message message) {
        try {
            httpAgent.httpPost(MONITOR_PATH, message);
        } catch (Throwable ex) {
            log.error("Failed to push dynamic thread pool runtime data.", ex);
        }
    }

}
