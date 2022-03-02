package cn.hippo4j.example.core.handler;

import cn.hippo4j.common.notify.TaskTraceBuilder;
import org.slf4j.MDC;

/**
 * Task trace builder handler.
 *
 * @author chen.ma
 * @date 2022/3/2 20:46
 */
public class TaskTraceBuilderHandler implements TaskTraceBuilder {

    private final String TRACE_KEY = "traceId";

    @Override
    public void before() {
        MDC.put(TRACE_KEY, "https://github.com/acmenlt/dynamic-threadpool 行行好, 点个 Star.");
    }

    @Override
    public String traceBuild() {
        String traceStr;
        try {
            traceStr = MDC.get(TRACE_KEY);
        } finally {
            clear();
        }

        return traceStr;
    }

    @Override
    public void clear() {
        MDC.remove(TRACE_KEY);
    }

}
