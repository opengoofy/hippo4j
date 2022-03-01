package cn.hippo4j.core.starter.refresher.config.impl;

import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.core.starter.refresher.config.ConfigParser;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ByteArrayResource;

import java.util.Map;

/**
 * Yml序列化配置
 *
 * @author serenity SerenitySir@outlook.com
 * @since 2022/2/28
 */
public class YmlConfigParser implements ConfigParser {

    @Override
    public Map<Object, Object> parseConfig(String content) {
        Map<Object, Object> resultMap = Maps.newHashMap();
        if (StringUtil.isBlank(content)) {
            return resultMap;
        }

        YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
        yamlPropertiesFactoryBean.setResources(new ByteArrayResource(content.getBytes()));
        resultMap = yamlPropertiesFactoryBean.getObject();

        return resultMap;
    }
}
