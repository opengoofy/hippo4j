package cn.hippo4j.config.monitor;

import cn.hippo4j.common.monitor.MessageTypeEnum;
import cn.hippo4j.common.monitor.RuntimeMessage;
import cn.hippo4j.config.service.biz.HisRunDataService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Runtime data resolver.
 *
 * @author chen.ma
 * @date 2021/12/10 20:18
 */
@Slf4j
@Component
@AllArgsConstructor
public class RuntimeDataResolver extends AbstractMonitorDataExecuteStrategy<RuntimeMessage> {

    private final HisRunDataService hisRunDataService;

    @Override
    public String mark() {
        return MessageTypeEnum.RUNTIME.name();
    }

    @Override
    public void execute(RuntimeMessage message) {
        hisRunDataService.save(message);
    }

}
