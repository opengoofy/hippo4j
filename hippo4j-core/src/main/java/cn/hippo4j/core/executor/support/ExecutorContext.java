package cn.hippo4j.core.executor.support;

import org.slf4j.MDC;

import static cn.hippo4j.common.constant.Constants.EXECUTE_TIMEOUT_TRACE;

public class ExecutorContext {

    public static void putExecuteTimeoutTrace(String executeTimeoutTrace) {
        MDC.put(EXECUTE_TIMEOUT_TRACE, executeTimeoutTrace);
    }

}
