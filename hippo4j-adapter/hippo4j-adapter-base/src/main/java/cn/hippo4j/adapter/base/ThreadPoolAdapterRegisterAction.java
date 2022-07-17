package cn.hippo4j.adapter.base;


import java.util.Map;

/**
 * Provide registration for each adaptation
 */
public interface ThreadPoolAdapterRegisterAction {

    /**
     * adapterRegister
     * @param threadPoolAdapterMap
     * @return
     */
    void adapterRegister(Map<String, ThreadPoolAdapter> threadPoolAdapterMap);
}
