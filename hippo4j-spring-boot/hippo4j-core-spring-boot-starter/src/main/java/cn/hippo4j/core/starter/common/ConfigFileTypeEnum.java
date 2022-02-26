package cn.hippo4j.core.starter.common;

/**
 * Config file type enum.
 *
 * @author chen.ma
 * @date 2022/2/26 18:12
 */
public enum ConfigFileTypeEnum {

    /**
     * PROPERTIES
     */
    PROPERTIES {
        @Override
        public String type() {
            return "properties";
        }
    },

    /***
     * YML
     */
    YML {
        @Override
        public String type() {
            return "yml";
        }
    },

    /***
     * YAML
     */
    YAML {
        @Override
        public String type() {
            return "yaml";
        }
    };

    /**
     * Type.
     *
     * @return
     */
    public abstract String type();

}
