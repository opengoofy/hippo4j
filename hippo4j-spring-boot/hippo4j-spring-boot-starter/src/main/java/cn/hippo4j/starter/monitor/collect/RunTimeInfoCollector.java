package cn.hippo4j.starter.monitor.collect;

import cn.hippo4j.common.model.PoolRunStateInfo;
import cn.hippo4j.common.monitor.AbstractMessage;
import cn.hippo4j.common.monitor.Message;
import cn.hippo4j.common.monitor.MessageTypeEnum;
import cn.hippo4j.common.monitor.RuntimeMessage;
import cn.hippo4j.starter.config.BootstrapProperties;
import cn.hippo4j.starter.core.GlobalThreadPoolManage;
import cn.hippo4j.starter.handler.AbstractThreadPoolRuntime;
import cn.hutool.core.bean.BeanUtil;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;

import java.util.List;

import static cn.hippo4j.starter.toolkit.IdentifyUtil.getThreadPoolIdentify;

/**
 * Thread pool runtime data collection.
 *
 * @author chen.ma
 * @date 2021/12/16 19:46
 */
@AllArgsConstructor
public class RunTimeInfoCollector extends AbstractThreadPoolRuntime implements Collector {

    private final BootstrapProperties properties;

    @Override
    public Message collectMessage() {
        AbstractMessage message = new RuntimeMessage();

        List<Message> runtimeMessages = Lists.newArrayList();
        List<String> listThreadPoolId = GlobalThreadPoolManage.listThreadPoolId();
        for (String each : listThreadPoolId) {
            PoolRunStateInfo poolRunState = getPoolRunState(each);
            RuntimeMessage runtimeMessage = BeanUtil.toBean(poolRunState, RuntimeMessage.class);
            runtimeMessage.setGroupKey(getThreadPoolIdentify(each, properties));
            runtimeMessages.add(runtimeMessage);
        }

        message.setMessageType(MessageTypeEnum.RUNTIME);
        message.setMessages(runtimeMessages);

        return message;
    }

    @Override
    protected PoolRunStateInfo supplement(PoolRunStateInfo basePoolRunStateInfo) {
        return basePoolRunStateInfo;
    }

}
