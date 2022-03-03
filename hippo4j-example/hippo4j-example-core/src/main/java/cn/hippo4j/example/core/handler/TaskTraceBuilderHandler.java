package cn.hippo4j.example.core.handler;

import cn.hippo4j.common.toolkit.StringUtil;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

import static cn.hippo4j.common.constant.Constants.EXECUTE_TIMEOUT_TRACE;

/**
 * Task trace builder handler.
 *
 * @author chen.ma
 * @date 2022/3/2 20:46
 */
public final class TaskTraceBuilderHandler implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        String executeTimeoutTrace = MDC.get(EXECUTE_TIMEOUT_TRACE);

        Runnable taskRun = () -> {
            if (StringUtil.isNotBlank(executeTimeoutTrace)) {
                MDC.put(EXECUTE_TIMEOUT_TRACE, executeTimeoutTrace);
            }
            runnable.run();
            // 此处不用进行清理操作, 统一在线程任务执行后清理
        };

        return taskRun;
    }

}
