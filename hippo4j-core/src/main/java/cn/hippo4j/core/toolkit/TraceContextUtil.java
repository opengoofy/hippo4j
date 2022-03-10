package cn.hippo4j.core.toolkit;

import org.slf4j.MDC;

import static cn.hippo4j.common.constant.Constants.EXECUTE_TIMEOUT_TRACE;

/**
 * MD util.
 *
 * @author chen.ma
 * @date 2022/3/3 08:30
 */
public class TraceContextUtil {

    /**
     * Execute timeout trace key.
     */
    private static String EXECUTE_TIMEOUT_TRACE_KEY = EXECUTE_TIMEOUT_TRACE;

    /**
     * Get and remove.
     *
     * @return
     */
    public static String getAndRemove() {
        String val = MDC.get(EXECUTE_TIMEOUT_TRACE_KEY);
        MDC.remove(EXECUTE_TIMEOUT_TRACE_KEY);
        return val;
    }

    /**
     * Set execute timeout trace key.
     *
     * @param key
     */
    public static void setExecuteTimeoutTraceKey(String key) {
        EXECUTE_TIMEOUT_TRACE_KEY = key;
    }

}
