package cn.hippo4j.config.monitor;

import cn.hippo4j.common.monitor.Message;
import cn.hippo4j.common.monitor.MessageTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Default monitor data resolver.
 *
 * @author chen.ma
 * @date 2021/12/10 21:47
 */
@Slf4j
@Component
public class DefaultMonitorDataResolver extends AbstractMonitorDataExecuteStrategy<Message> {

    @Override
    public String mark() {
        return MessageTypeEnum.DEFAULT.name();
    }

    @Override
    public void execute(Message message) {
        log.warn("There is no suitable monitoring data storage actuator.");
    }

}
