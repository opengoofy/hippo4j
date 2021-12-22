package cn.hippo4j.starter.alarm.ding;

/**
 * Ding alarm constants.
 *
 * @author chen.ma
 * @date 2021/11/26 20:03
 */
public class DingAlarmConstants {

    /**
     * 钉钉机器人 Url
     */
    public static final String DING_ROBOT_SERVER_URL = "https://oapi.dingtalk.com/robot/send?access_token=";

    /**
     * 线程池报警通知标题
     */
    public static final String DING_ALARM_TITLE = "动态线程池告警";

    /**
     * 线程池参数变更通知标题
     */
    public static final String DING_NOTICE_TITLE = "动态线程池通知";

    /**
     * 线程池报警通知文本
     */
    public static final String DING_ALARM_TXT =
            "<font color='#FF0000'>[警报] </font>%s - 动态线程池运行告警 \n\n" +
                    " --- \n\n " +
                    "<font color='#708090' size=2>线程池ID：%s</font> \n\n " +
                    "<font color='#708090' size=2>应用名称：%s</font> \n\n " +
                    "<font color='#778899' size=2>应用实例：%s</font> \n\n " +
                    "<font color='#778899' size=2>报警类型：%s</font> \n\n " +
                    " --- \n\n  " +
                    "<font color='#708090' size=2>核心线程数：%d</font> \n\n " +
                    "<font color='#708090' size=2>最大线程数：%d</font> \n\n " +
                    "<font color='#708090' size=2>当前线程数：%d</font> \n\n " +
                    "<font color='#708090' size=2>活跃线程数：%d</font> \n\n " +
                    "<font color='#708090' size=2>最大任务数：%d</font> \n\n " +
                    "<font color='#708090' size=2>线程池任务总量：%d</font> \n\n " +
                    " --- \n\n  " +
                    "<font color='#708090' size=2>队列类型：%s</font> \n\n " +
                    "<font color='#708090' size=2>队列容量：%d</font> \n\n " +
                    "<font color='#708090' size=2>队列元素个数：%d</font> \n\n " +
                    "<font color='#708090' size=2>队列剩余个数：%d</font> \n\n " +
                    " --- \n\n  " +
                    "<font color='#708090' size=2>拒绝策略：%s</font> \n\n" +
                    "<font color='#708090' size=2>拒绝策略执行次数：</font><font color='#FF0000' size=2>%d</font> \n\n " +
                    "<font color='#708090' size=2>OWNER：@%s</font> \n\n" +
                    "<font color='#708090' size=2>提示：%d 分钟内此线程池不会重复告警（可配置）</font> \n\n" +
                    " --- \n\n  " +
                    "**播报时间：%s**";

    /**
     * 线程池参数变更通知文本
     */
    public static final String DING_NOTICE_TXT =
            "<font color='#2a9d8f'>[通知] </font>%s - 动态线程池参数变更 \n\n" +
                    " --- \n\n " +
                    "<font color='#708090' size=2>线程池ID：%s</font> \n\n " +
                    "<font color='#708090' size=2>应用名称：%s</font> \n\n " +
                    "<font color='#778899' size=2>应用实例：%s</font> \n\n " +
                    " --- \n\n  " +
                    "<font color='#708090' size=2>核心线程数：%s</font> \n\n " +
                    "<font color='#708090' size=2>最大线程数：%s</font> \n\n " +
                    "<font color='#708090' size=2>核心线程超时：%s</font> \n\n " +
                    "<font color='#708090' size=2>线程存活时间：%s / SECONDS</font> \n\n" +
                    " --- \n\n  " +
                    "<font color='#708090' size=2>队列类型：%s</font> \n\n " +
                    "<font color='#708090' size=2>队列容量：%s</font> \n\n " +
                    " --- \n\n  " +
                    "<font color='#708090' size=2>AGO 拒绝策略：%s</font> \n\n" +
                    "<font color='#708090' size=2>NOW 拒绝策略：%s</font> \n\n" +
                    " --- \n\n  " +
                    "<font color='#708090' size=2>提示：动态线程池配置变更实时通知（无限制）</font> \n\n" +
                    "<font color='#708090' size=2>OWNER：@%s</font> \n\n" +
                    " --- \n\n  " +
                    "**播报时间：%s**";

}
