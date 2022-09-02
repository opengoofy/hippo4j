package cn.hippo4j.example.config.etcd.config;

import java.util.concurrent.ThreadPoolExecutor;

import cn.hippo4j.core.executor.DynamicThreadPool;
import cn.hippo4j.core.executor.support.ThreadPoolBuilder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *@author : wh
 *@date : 2022/9/2 19:26
 *@description:
 */
@Configuration
public class ThreadPoolConfig {


	@Bean
	@DynamicThreadPool
	public ThreadPoolExecutor messageConsumeDynamicExecutor() {
		String threadPoolId = "message-consume";
		ThreadPoolExecutor messageConsumeDynamicExecutor = ThreadPoolBuilder.builder()
				.threadFactory(threadPoolId)
				.threadPoolId(threadPoolId)
				.dynamicPool()
				.build();
		return messageConsumeDynamicExecutor;
	}
	
	
	
	
}
