package cn.hippo4j.config.monitor;

import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.common.monitor.Message;
import cn.hippo4j.common.monitor.MessageTypeEnum;
import com.google.common.collect.Maps;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Query monitor execute choose.
 *
 * @author chen.ma
 * @date 2021/12/10 20:12
 */
@Component
public class QueryMonitorExecuteChoose implements CommandLineRunner {

    /**
     * Storage monitoring data execution container.
     */
    private Map<String, AbstractMonitorDataExecuteStrategy> monitorDataExecuteStrategyChooseMap = Maps.newHashMap();

    /**
     * Choose by {@link cn.hippo4j.common.monitor.MessageTypeEnum}.
     *
     * @param messageTypeEnum {@link cn.hippo4j.common.monitor.MessageTypeEnum}
     * @return
     */
    public AbstractMonitorDataExecuteStrategy choose(MessageTypeEnum messageTypeEnum) {
        return choose(messageTypeEnum.name());
    }

    /**
     * Choose by mark type.
     *
     * @param markType {@link cn.hippo4j.common.monitor.MessageTypeEnum#name()}
     * @return
     */
    public AbstractMonitorDataExecuteStrategy choose(String markType) {
        AbstractMonitorDataExecuteStrategy executeStrategy = monitorDataExecuteStrategyChooseMap.get(markType);
        if (executeStrategy == null) {
            executeStrategy = monitorDataExecuteStrategyChooseMap.get(MessageTypeEnum.DEFAULT.name());
        }
        return executeStrategy;
    }

    /**
     * Choose and execute.
     *
     * @param message {@link Message}
     */
    public void chooseAndExecute(Message message) {
        MessageTypeEnum messageType = message.getMessageType();
        AbstractMonitorDataExecuteStrategy executeStrategy = choose(messageType);
        executeStrategy.execute(message);
    }

    @Override
    public void run(String... args) throws Exception {
        Map<String, AbstractMonitorDataExecuteStrategy> monitorDataExecuteStrategyMap =
                ApplicationContextHolder.getBeansOfType(AbstractMonitorDataExecuteStrategy.class);

        monitorDataExecuteStrategyMap.values().forEach(each -> monitorDataExecuteStrategyChooseMap.put(each.mark(), each));
    }

}
