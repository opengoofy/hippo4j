package cn.hippo4j.common.toolkit;

import org.slf4j.MDC;

/**
 * MD util.
 *
 * @author chen.ma
 * @date 2022/3/3 08:30
 */
public class MDCUtil {

    /**
     * Get and remove.
     *
     * @param key
     * @return
     */
    public static String getAndRemove(String key) {
        String val = MDC.get(key);
        MDC.remove(key);
        return val;
    }

}
