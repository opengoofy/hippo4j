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

    /**
     * Supports.
     *
     * @param type
     * @return
     */
    boolean supports(ConfigFileTypeEnum type);

    /**
     * Do parse.
     *
     * @param content
     * @return
     * @throws IOException
     */
    Map<Object, Object> doParse(String content) throws IOException;

    /**
     * Get config file types.
     *
     * @return
     */
    List<ConfigFileTypeEnum> getConfigFileTypes();

}
