package cn.hippo4j.starter.monitor.send;

import cn.hippo4j.common.monitor.Message;
import cn.hippo4j.common.monitor.MessageWrapper;
import cn.hippo4j.common.toolkit.MessageConvert;
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
public class HttpConnectSender implements MessageSender {

    private final HttpAgent httpAgent;

    @Override
    public void send(Message message) {
        try {
            MessageWrapper messageWrapper = MessageConvert.convert(message);
            httpAgent.httpPost(MONITOR_PATH, messageWrapper);
        } catch (Throwable ex) {
            log.error("Failed to push dynamic thread pool runtime data.", ex);
        }
    }

}
