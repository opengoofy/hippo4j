package cn.hippo4j.core.starter.parser;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author : wh
 * @date : 2022/3/1 07:57
 * @description:
 */
public class YamlConfigParser extends AbstractConfigParser {

    @Override
    public Map<Object, Object> doParse(String content) {
        if (StringUtils.isEmpty(content)) {
            return Maps.newHashMapWithExpectedSize(0);
        }

        YamlPropertiesFactoryBean bean = new YamlPropertiesFactoryBean();
        bean.setResources(new ByteArrayResource(content.getBytes()));

        return bean.getObject();
    }

    @Override
    public List<ConfigFileTypeEnum> getConfigFileTypes() {
        return Lists.newArrayList(ConfigFileTypeEnum.YML, ConfigFileTypeEnum.YAML);
    }

}
