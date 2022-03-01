package cn.hippo4j.core.starter.parser;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author : wh
 * @date : 2022/3/1 07:47
 * @description:
 */
public interface ConfigParser {

    boolean supports(ConfigFileTypeEnum type);

    Map<Object, Object> doParse(String content) throws IOException;

    List<ConfigFileTypeEnum> getConfigFileTypes();

}
