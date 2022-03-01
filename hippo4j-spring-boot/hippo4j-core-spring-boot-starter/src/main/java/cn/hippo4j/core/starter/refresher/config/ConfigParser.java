package cn.hippo4j.core.starter.refresher.config;

import java.util.Map;

/**
 * 配置序列化接口
 *
 * @author serenity SerenitySir@outlook.com
 * @since 2022/2/28
 */
public interface ConfigParser {
    /**
     * 对配置进行序列化
     * @param content 配置的字符串形式
     * @return 序列化后的k,v
     */
    Map<Object, Object> parseConfig(String content);
}
