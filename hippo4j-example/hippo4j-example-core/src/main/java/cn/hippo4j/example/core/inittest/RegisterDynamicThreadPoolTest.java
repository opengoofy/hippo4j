package cn.hippo4j.example.core.inittest;

import cn.hippo4j.common.model.register.DynamicThreadPoolRegisterParameter;
import cn.hippo4j.common.model.register.DynamicThreadPoolRegisterWrapper;
import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.core.executor.support.DynamicThreadPoolService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Register dynamic thread-pool test
 */
@Slf4j
@Component
@AllArgsConstructor
public class RegisterDynamicThreadPoolTest {

    private final DynamicThreadPoolService dynamicThreadPoolService;

    @PostConstruct
    public void registerDynamicThreadPool() {
        String threadPoolId = "register-dynamic-thread-pool";
        DynamicThreadPoolRegisterParameter parameterInfo = new DynamicThreadPoolRegisterParameter();
        parameterInfo.setThreadPoolId(threadPoolId);
        parameterInfo.setCorePoolSize(2);
        parameterInfo.setMaximumPoolSize(14);
        parameterInfo.setQueueType(9);
        parameterInfo.setCapacity(110);
        parameterInfo.setKeepAliveTime(110);
        parameterInfo.setRejectedType(2);
        parameterInfo.setIsAlarm(0);
        parameterInfo.setCapacityAlarm(90);
        parameterInfo.setLivenessAlarm(90);
        parameterInfo.setAllowCoreThreadTimeOut(0);
        DynamicThreadPoolRegisterWrapper registerWrapper = DynamicThreadPoolRegisterWrapper.builder()
                .dynamicThreadPoolRegisterParameter(parameterInfo)
                .build();
        ThreadPoolExecutor dynamicThreadPool = dynamicThreadPoolService.registerDynamicThreadPool(registerWrapper);
        log.info("Dynamic registration thread pool parameter details: {}", JSONUtil.toJSONString(dynamicThreadPool));
    }
}
