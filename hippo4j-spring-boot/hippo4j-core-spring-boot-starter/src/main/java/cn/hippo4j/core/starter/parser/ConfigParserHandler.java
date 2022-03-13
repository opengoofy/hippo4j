package cn.hippo4j.core.starter.parser;

import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @author : wh
 * @date : 2022/3/1 08:02
 * @description:
 */
public class ConfigParserHandler {

    private static final List<ConfigParser> PARSERS = Lists.newArrayList();

    private ConfigParserHandler() {
        ServiceLoader<ConfigParser> loader = ServiceLoader.load(ConfigParser.class);
        for (ConfigParser configParser : loader) {
            PARSERS.add(configParser);
        }

        PARSERS.add(new PropertiesConfigParser());
        PARSERS.add(new YamlConfigParser());
    }

    public Map<Object, Object> parseConfig(String content, ConfigFileTypeEnum type) throws IOException {
        for (ConfigParser parser : PARSERS) {
            if (parser.supports(type)) {
                return parser.doParse(content);
            }
        }

        return Collections.emptyMap();
    }

    public static ConfigParserHandler getInstance() {
        return ConfigParserHandlerHolder.INSTANCE;
    }

    private static class ConfigParserHandlerHolder {
        private static final ConfigParserHandler INSTANCE = new ConfigParserHandler();
    }

}
