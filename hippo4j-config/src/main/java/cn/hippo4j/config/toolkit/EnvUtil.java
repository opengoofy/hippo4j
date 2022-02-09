package cn.hippo4j.config.toolkit;

import org.apache.commons.lang3.StringUtils;

import java.nio.file.Paths;
import java.util.Objects;

/**
 * Env Util.
 *
 * @author chen.ma
 * @date 2022/2/9 07:46
 */
public class EnvUtil {

    public static final String HIPPO4J_HOME_KEY = "hippo4j.home";

    public static final String STANDALONE_MODE_PROPERTY_NAME = "hippo4j.standalone";

    private static String HIPPO4J_HOME_PATH = null;

    private static Boolean IS_STANDALONE = null;

    /**
     * Get hippo4j home.
     *
     * @return
     */
    public static String getHippo4JHome() {
        if (StringUtils.isBlank(HIPPO4J_HOME_PATH)) {
            String hippo4jHome = System.getProperty(HIPPO4J_HOME_KEY);
            if (StringUtils.isBlank(hippo4jHome)) {
                hippo4jHome = Paths.get(System.getProperty("user.home"), "hippo4j").toString();
            }

            return hippo4jHome;
        }

        return HIPPO4J_HOME_PATH;
    }

    /**
     * Standalone mode or not.
     *
     * @return
     */
    public static boolean getStandaloneMode() {
        if (Objects.isNull(IS_STANDALONE)) {
            IS_STANDALONE = Boolean.getBoolean(STANDALONE_MODE_PROPERTY_NAME);
        }

        return IS_STANDALONE;
    }

}
