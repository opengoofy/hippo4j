package cn.hippo4j.core.starter.parser;

/**
 * @author : wh
 * @date : 2022/3/1 07:50
 * @description:
 */
public abstract class AbstractConfigParser implements ConfigParser {

    @Override
    public boolean supports(ConfigFileTypeEnum type) {
        return getConfigFileTypes().contains(type);
    }

}
