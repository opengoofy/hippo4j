package cn.hippo4j.config.verify;

import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.config.service.biz.ConfigChangeVerifyService;
import com.google.common.collect.Maps;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * config change verify service choose
 */
@Component
public class ConfigChangeVerifyServiceChoose implements CommandLineRunner {

    /**
     * Storage config change verify service container.
     */
    private Map<Integer, ConfigChangeVerifyService> configChangeVerifyServiceChooseMap = Maps.newHashMap();

    /**
     * Choose by type.
     *
     * @param type {@link cn.hippo4j.common.constant.ConfigChangeTypeConstants}
     * @return
     */
    public ConfigChangeVerifyService choose(Integer type) {
        ConfigChangeVerifyService verifyService = configChangeVerifyServiceChooseMap.get(type);
        return verifyService;
    }

    @Override
    public void run(String... args) throws Exception {
        Map<String, ConfigChangeVerifyService> configChangeVerifyServiceMap =
                ApplicationContextHolder.getBeansOfType(ConfigChangeVerifyService.class);
        configChangeVerifyServiceMap.values().forEach(each -> configChangeVerifyServiceChooseMap.put(each.type(), each));
    }
}
