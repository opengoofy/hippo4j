package cn.hippo4j.core.springboot.starter.support;

import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.core.springboot.starter.config.AdapterExecutorProperties;
import cn.hippo4j.core.springboot.starter.config.BootstrapCoreProperties;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.List;
import java.util.Map;

import static cn.hippo4j.common.constant.Constants.IDENTIFY_SLICER_SYMBOL;

/**
 * Thread-pool adapter register.
 */
@Slf4j
@AllArgsConstructor
public class ThreadPoolAdapterRegister implements ApplicationRunner {

    private final BootstrapCoreProperties bootstrapCoreProperties;

    public static final Map<String, AdapterExecutorProperties> ADAPTER_EXECUTORS_MAP = Maps.newConcurrentMap();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<AdapterExecutorProperties> adapterExecutors;
        if (CollectionUtil.isEmpty(adapterExecutors = bootstrapCoreProperties.getAdapterExecutors())) {
            return;
        }
        for (AdapterExecutorProperties each : adapterExecutors) {
            String buildKey = each.getMark() + IDENTIFY_SLICER_SYMBOL + each.getThreadPoolKey();
            ADAPTER_EXECUTORS_MAP.put(buildKey, each);
        }
    }
}
