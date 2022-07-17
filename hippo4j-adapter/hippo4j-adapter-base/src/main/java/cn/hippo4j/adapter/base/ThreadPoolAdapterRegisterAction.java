package cn.hippo4j.adapter.base;


import java.util.Map;

/**
 * Provide registration for each adaptation
 */
public interface ThreadPoolAdapterRegisterAction {

    void adapterRegister(Map<String, ThreadPoolAdapter> threadPoolAdapterMap);
}
