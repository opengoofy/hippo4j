package cn.hippo4j.common.constant;

/**
 * Constants.
 *
 * @author chen.ma
 * @date 2021/6/16 23:01
 */
public class Constants {

    public static final String TP_ID = "tpId";

    public static final String ITEM_ID = "itemId";

    public static final String NAMESPACE = "namespace";

    public static final String GROUP_KEY = "groupKey";

    public static final String AUTHORITIES_KEY = "auth";

    public static final String ACCESS_TOKEN = "accessToken";

    public static final String TOKEN_TTL = "tokenTtl";

    public static final String DEFAULT_NAMESPACE_ID = "public";

    public static final String NULL = "";

    public static final String UP = "UP";

    public static final String ENCODE = "UTF-8";

    public static final int CONFIG_LONG_POLL_TIMEOUT = 30000;

    public static final String LINE_SEPARATOR = Character.toString((char) 1);

    public static final String WORD_SEPARATOR = Character.toString((char) 2);

    public static final String GENERAL_SPLIT_SYMBOL = ",";

    public static final String IDENTIFY_SLICER_SYMBOL = "_";

    public static final String LONG_POLLING_LINE_SEPARATOR = "\r\n";

    public static final String BASE_PATH = "/hippo4j/v1/cs";

    public static final String CONFIG_CONTROLLER_PATH = BASE_PATH + "/configs";

    public static final String LISTENER_PATH = CONFIG_CONTROLLER_PATH + "/listener";

    public static final String MONITOR_PATH = BASE_PATH + "/monitor";

    public static final String HEALTH_CHECK_PATH = BASE_PATH + "/health/check";

    public static final String PROBE_MODIFY_REQUEST = "Listening-Configs";

    public static final String LONG_PULLING_TIMEOUT = "Long-Pulling-Timeout";

    public static final String LONG_PULLING_TIMEOUT_NO_HANGUP = "Long-Pulling-Timeout-No-Hangup";

    public static final String LONG_PULLING_CLIENT_IDENTIFICATION = "Long-Pulling-Client-Identification";

    public static final String LISTENING_CONFIGS = "Listening-Configs";

    public static final String WEIGHT_CONFIGS = "Weight-Configs";

    public static final String GROUP_KEY_DELIMITER = "+";

    public static final String GROUP_KEY_DELIMITER_TRANSLATION = "\\+";

    public static final long EVICTION_INTERVAL_TIMER_IN_MS = 60 * 1000;

    public static final int SCHEDULED_THREAD_CORE_NUM = 1;

    public static final int MAP_INITIAL_CAPACITY = 16;

    public static final int HEALTH_CHECK_INTERVAL = 5;

    public static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

    public static final String DEFAULT_GROUP = "default group";

    public static final String UNKNOWN = "unknown";

    public static final String EXECUTE_TIMEOUT_TRACE = "executeTimeoutTrace";

    public static final int HTTP_EXECUTE_TIMEOUT = 5000;

}
