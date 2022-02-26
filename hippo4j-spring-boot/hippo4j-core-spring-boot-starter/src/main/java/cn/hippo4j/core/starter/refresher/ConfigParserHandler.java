package cn.hippo4j.core.starter.refresher;

import cn.hippo4j.common.toolkit.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ByteArrayResource;

import java.util.List;
import java.util.Map;

/**
 * Config parser service.
 *
 * @author chen.ma
 * @date 2022/2/26 17:33
 */
public class ConfigParserHandler {

    private final List<String> yamlList = Lists.newArrayList("yaml", "yml");

    /**
     * Parse config.
     *
     * @param content
     * @param configFileType
     * @return
     */
    public Map<Object, Object> parseConfig(String content, String configFileType) {
        Map<Object, Object> resultMap = Maps.newHashMap();
        if (StringUtil.isBlank(content)) {
            return resultMap;
        }

        if (yamlList.contains(configFileType)) {
            YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
            yamlPropertiesFactoryBean.setResources(new ByteArrayResource(content.getBytes()));
            resultMap = yamlPropertiesFactoryBean.getObject();
        }

        return resultMap;
    }

}
