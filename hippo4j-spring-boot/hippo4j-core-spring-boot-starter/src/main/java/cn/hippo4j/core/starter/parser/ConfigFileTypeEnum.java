package cn.hippo4j.core.starter.parser;

import lombok.Getter;

/**
 * @author : wh
 * @date : 2022/3/1 07:47
 * @description:
 */
@Getter
public enum ConfigFileTypeEnum {

    PROPERTIES("properties"),
    XML("xml"),
    JSON("json"),
    YML("yml"),
    YAML("yaml"),
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
