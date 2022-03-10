package cn.hippo4j.common.notify.platform;

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

    /**
     * Trace 信息
     */
    public static final String LARK_ALARM_TIMOUT_TRACE_REPLACE_TXT = ",{\"is_short\":true,\"text\":{\"content\":\"** 链路信息：** %s\",\"tag\":\"lark_md\"}}";

    /**
     * 替换任务超时模板
     */
    public static final String LARK_ALARM_TIMOUT_REPLACE_TXT =
            "{\"fields\":[{\"is_short\":true,\"text\":{\"content\":\"** 任务执行时间：** %s\",\"tag\":\"lark_md\"}},{\"is_short\":true,\"text\":{\"content\":\"** 超时时间：** %s\",\"tag\":\"lark_md\"}}" +
                    LARK_ALARM_TIMOUT_TRACE_REPLACE_TXT +
                    "],\"tag\":\"div\"},{\"tag\":\"hr\"},";

    /**
     * lark alarm json str
     */
    public static final String LARK_ALARM_JSON_STR = "{\"msg_type\":\"interactive\",\"card\":{\"config\":{\"wide_screen_mode\":true},\"header\":{\"template\":\"red\",\"title\":{\"content\":\"[\uD83D\uDD25警报] %s 动态线程池运行告警（%s）\",\"tag\":\"plain_text\"}},\"elements\":[{\"fields\":[{\"is_short\":true,\"text\":{\"content\":\"** 线程池ID：** %s\",\"tag\":\"lark_md\"}},{\"is_short\":true,\"text\":{\"content\":\"** 应用名称：** %s\",\"tag\":\"lark_md\"}},{\"is_short\":true,\"text\":{\"content\":\"** 应用实例：** %s\",\"tag\":\"lark_md\"}}],\"tag\":\"div\"},{\"tag\":\"hr\"},{\"fields\":[{\"is_short\":true,\"text\":{\"content\":\"** 核心线程数：** %s\",\"tag\":\"lark_md\"}},{\"is_short\":true,\"text\":{\"content\":\"** 最大线程数：** %s\",\"tag\":\"lark_md\"}},{\"is_short\":true,\"text\":{\"content\":\"** 当前线程数：** %s\",\"tag\":\"lark_md\"}},{\"is_short\":true,\"text\":{\"content\":\"** 活跃线程数：** %s\",\"tag\":\"lark_md\"}},{\"is_short\":true,\"text\":{\"content\":\"** 最大任务数：** %s\",\"tag\":\"lark_md\"}},{\"is_short\":true,\"text\":{\"content\":\"** 线程池任务总量：** %s\",\"tag\":\"lark_md\"}}],\"tag\":\"div\"},{\"tag\":\"hr\"},{\"fields\":[{\"is_short\":true,\"text\":{\"content\":\"** 队列类型：** %s\",\"tag\":\"lark_md\"}},{\"is_short\":true,\"text\":{\"content\":\"** 队列容量：** %s\",\"tag\":\"lark_md\"}},{\"is_short\":true,\"text\":{\"content\":\"** 队列元素个数：** %s\",\"tag\":\"lark_md\"}},{\"is_short\":true,\"text\":{\"content\":\"** 队列剩余个数：** %s\",\"tag\":\"lark_md\"}}],\"tag\":\"div\"},{\"tag\":\"hr\"}," + LARK_ALARM_TIMOUT_REPLACE_TXT + "{\"fields\":[{\"is_short\":true,\"text\":{\"content\":\"** 拒绝策略：** %s\",\"tag\":\"lark_md\"}},{\"is_short\":true,\"text\":{\"content\":\"** 拒绝策略执行次数：** %s\",\"tag\":\"lark_md\"}},{\"is_short\":true,\"text\":{\"content\":\"** OWNER：** %s\",\"tag\":\"lark_md\"}},{\"is_short\":true,\"text\":{\"content\":\"** 播报时间： ** %s\",\"tag\":\"lark_md\"}}],\"tag\":\"div\"},{\"tag\":\"hr\"},{\"tag\":\"note\",\"elements\":[{\"tag\":\"plain_text\",\"content\":\"提示： %s 分钟内此线程池不会重复告警（可配置）\"}]}]}}";

    /**
     * lark notice json str
     */
    public static final String LARK_NOTICE_JSON_STR = "{\"msg_type\":\"interactive\",\"card\":{\"config\":{\"wide_screen_mode\":true},\"header\":{\"template\":\"greed\",\"title\":{\"content\":\"[通知] %s 动态线程池参数变更\",\"tag\":\"plain_text\"}},\"elements\":[{\"fields\":[{\"is_short\":true,\"text\":{\"content\":\"** 线程池ID：** %s\",\"tag\":\"lark_md\"}},{\"is_short\":true,\"text\":{\"content\":\"** 应用名称：** %s\",\"tag\":\"lark_md\"}},{\"is_short\":true,\"text\":{\"content\":\"** 应用实例：** %s\",\"tag\":\"lark_md\"}}],\"tag\":\"div\"},{\"tag\":\"hr\"},{\"fields\":[{\"is_short\":true,\"text\":{\"content\":\"** 核心线程数：** %s\",\"tag\":\"lark_md\"}},{\"is_short\":true,\"text\":{\"content\":\"** 最大线程数：** %s\",\"tag\":\"lark_md\"}},{\"is_short\":true,\"text\":{\"content\":\"** 核心线程超时：** %s\",\"tag\":\"lark_md\"}},{\"is_short\":true,\"text\":{\"content\":\"** 线程存活时间：** %s\",\"tag\":\"lark_md\"}}],\"tag\":\"div\"},{\"tag\":\"hr\"},{\"fields\":[{\"is_short\":true,\"text\":{\"content\":\"** 队列类型：** %s\",\"tag\":\"lark_md\"}},{\"is_short\":true,\"text\":{\"content\":\"** 队列容量：** %s\",\"tag\":\"lark_md\"}},{\"is_short\":true,\"text\":{\"content\":\"** 执行超时时间：** %s\",\"tag\":\"lark_md\"}}],\"tag\":\"div\"},{\"tag\":\"hr\"},{\"fields\":[{\"is_short\":true,\"text\":{\"content\":\"** AGO 拒绝策略：** %s\",\"tag\":\"lark_md\"}},{\"is_short\":true,\"text\":{\"content\":\"** NOW 拒绝策略：** %s\",\"tag\":\"lark_md\"}},{\"is_short\":true,\"text\":{\"content\":\"** OWNER：** %s\",\"tag\":\"lark_md\"}},{\"is_short\":true,\"text\":{\"content\":\"** 播报时间： ** %s\",\"tag\":\"lark_md\"}}],\"tag\":\"div\"},{\"tag\":\"hr\"},{\"tag\":\"note\",\"elements\":[{\"tag\":\"plain_text\",\"content\":\"提示：动态线程池配置变更实时通知（无限制）\"}]}]}}";

}
