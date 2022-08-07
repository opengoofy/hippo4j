package cn.hippo4j.example.server;

import cn.hippo4j.core.executor.support.ThreadPoolBuilder;
import cn.hippo4j.common.model.register.DynamicThreadPoolRegisterParameter;
import cn.hippo4j.common.model.register.DynamicThreadPoolRegisterWrapper;
import cn.hippo4j.core.executor.support.DynamicThreadPoolService;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Register dynamic thread-pool test
 */
@Component
@AllArgsConstructor
public class RegisterDynamicThreadPoolTest implements ApplicationRunner {

    private final DynamicThreadPoolService dynamicThreadPoolService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
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
        ThreadPoolExecutor threadPoolExecutor = ThreadPoolBuilder.builder()
                .threadPoolId(threadPoolId)
                .threadFactory(threadPoolId)
                .dynamicPool()
                .build();
        DynamicThreadPoolRegisterWrapper registerWrapper = DynamicThreadPoolRegisterWrapper.builder()
                .dynamicThreadPoolRegisterParameter(parameterInfo)
                .dynamicThreadPoolExecutor(threadPoolExecutor)
                .build();
        dynamicThreadPoolService.registerDynamicThreadPool(registerWrapper);
    }
}
