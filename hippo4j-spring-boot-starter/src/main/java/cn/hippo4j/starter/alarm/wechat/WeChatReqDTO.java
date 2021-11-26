package cn.hippo4j.starter.alarm.wechat;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * WeChat req dto.
 *
 * @author chen.ma
 * @date 2021/11/26 20:15
 */
@Data
@Accessors(chain = true)
public class WeChatReqDTO {

    /**
     * msgType
     */
    private String msgtype;

    /**
     * markdown
     */
    private Markdown markdown;

    @Data
    public static class Markdown {

        /**
         * content
         */
        private String content;

    }

}
