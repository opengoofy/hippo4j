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
     * lark at format. openid
     * 当配置openid时,机器人可以@人
     */
    public static final String LARK_AT_FORMAT_OPENID = "<at id='%s'></at>";

    /**
     * lark at format. username
     * 当配置username时,只能蓝色字体展示@username,被@人无@提醒
     */
    public static final String LARK_AT_FORMAT_USERNAME = "<at id=''>%s</at>";

    /**
     * lark openid prefix
     */
    public static final String LARK_OPENID_PREFIX = "ou_";
}
