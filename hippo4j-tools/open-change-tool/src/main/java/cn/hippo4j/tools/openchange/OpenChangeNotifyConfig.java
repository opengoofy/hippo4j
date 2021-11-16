package cn.hippo4j.tools.openchange;

import com.dtflys.forest.springboot.annotation.ForestScan;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 项目变更通知配置.
 *
 * @author chen.ma
 * @date 2021/10/31 10:23
 */
@EnableScheduling
@AllArgsConstructor
@ImportAutoConfiguration(OpenChangeNotifyBootstrapProperties.class)
@ForestScan(basePackages = "com.github.dynamic.threadpool.openchange")
public class OpenChangeNotifyConfig {

    private final GitHubRemote gitHubRemote;

    private final OpenChangeNotifyBootstrapProperties bootstrapProperties;

    @Bean
    public OpenChangeTimedTask changeTimedTask() {
        return new OpenChangeTimedTask(gitHubRemote, bootstrapProperties);
    }

}
