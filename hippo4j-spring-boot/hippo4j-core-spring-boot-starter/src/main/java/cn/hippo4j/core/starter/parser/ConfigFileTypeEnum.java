package cn.hippo4j.core.starter.parser;

import lombok.Getter;

/**
 * @author : wh
 * @date : 2022/3/1 07:47
 * @description:
 */
@Getter
public enum ConfigFileTypeEnum {

    /**
     * properties
     */
    PROPERTIES("properties"),

    /**
     * xml
     */
    XML("xml"),

    /**
     * json
     */
    JSON("json"),

    /**
     * yml
     */
    YML("yml"),

    /**
     * yaml
     */
    YAML("yaml"),

    /**
     * txt
     */
    TXT("txt");

    private final String value;

    ConfigFileTypeEnum(String value) {
        this.value = value;
    }

    public static ConfigFileTypeEnum of(String value) {
        for (ConfigFileTypeEnum typeEnum : ConfigFileTypeEnum.values()) {
            if (typeEnum.value.equals(value)) {
                return typeEnum;
            }
        }
        return PROPERTIES;
    }

}
