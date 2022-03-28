package cn.hippo4j.config.monitor;

import cn.hippo4j.common.monitor.Message;

/**
 * Abstract monitor data execute strategy.
 *
 * @author chen.ma
 * @date 2021/12/10 20:14
 */
public abstract class AbstractMonitorDataExecuteStrategy<T extends Message> {

    /**
     * Mark.
     *
     * @return
     */
    public abstract String mark();

    /**
     * Execute.
     *
     * @param message
     */
    public abstract void execute(T message);

}
