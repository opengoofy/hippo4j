package cn.hippo4j.core.starter.parser;

import com.google.common.collect.Lists;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author : wh
 * @date : 2022/3/1 07:49
 * @description:
 */
public class PropertiesConfigParser extends AbstractConfigParser {

    @Override
    public Map<Object, Object> doParse(String content) throws IOException {
        Properties properties = new Properties();
        properties.load(new StringReader(content));
        return properties;
    }

    @Override
    public List<ConfigFileTypeEnum> getConfigFileTypes() {
        return Lists.newArrayList(ConfigFileTypeEnum.PROPERTIES);
    }

}
