package cn.hippo4j.starter.alarm.lark;

/**
 * lark alarm constants.
 *
 * @author imyzt
 * @date 2021-11-23 19:29
 */
public class LarkAlarmConstants {

    /**
     * lark bot url
     */
    public static final String LARK_BOT_URL = "https://open.feishu.cn/open-apis/bot/v2/hook/";

    /**
     * lark 报警 json文件路径
     */
    public static final String ALARM_JSON_PATH = "classpath:properties/lark/alarm.json";

    /**
     * lark 配置变更通知 json文件路径
     */
    public static final String NOTICE_JSON_PATH = "classpath:properties/lark/notice.json";

    /**
     * lark at format
     */
    public static final String LARK_AT_FORMAT = "<at email=''>%s</at>";
}
