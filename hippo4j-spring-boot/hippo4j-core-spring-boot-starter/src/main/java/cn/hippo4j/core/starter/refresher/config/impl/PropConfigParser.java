package cn.hippo4j.core.starter.refresher.config.impl;

import cn.hippo4j.core.starter.refresher.config.ConfigParser;
import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import org.springframework.util.StringUtils;

import java.io.StringReader;
import java.util.Map;
import java.util.Properties;

/**
 * Properties序列化配置
 *
 * @author serenity SerenitySir@outlook.com
 * @since 2022/2/28
 */
public class PropConfigParser implements ConfigParser {

    @SneakyThrows
    @Override
    public Map<Object, Object> parseConfig(String content) {
        if (!StringUtils.hasText(content)){
            return Maps.newHashMap();
        }

        Properties properties = new Properties();
        properties.load(new StringReader(content));
        return properties;
    }

    public static void main(String[] args) {
        ConfigParser propConfigParser = new PropConfigParser();
        Map<Object, Object> map = propConfigParser.parseConfig("db.aa=11\ndb.bb=22");
        System.out.println(map.toString());
    }
}
