package cn.hippo4j.common.api;

/**
 * Thread pool dynamic refresh.
 *
 * @author chen.ma
 * @date 2022/2/26 12:26
 */
public interface ThreadPoolDynamicRefresh {

    /**
     * Dynamic refresh.
     *
     * @param content
     */
    void dynamicRefresh(String content);

}
