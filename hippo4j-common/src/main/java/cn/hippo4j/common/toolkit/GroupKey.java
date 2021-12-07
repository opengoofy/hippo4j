package cn.hippo4j.common.toolkit;

import cn.hippo4j.common.constant.Constants;
import org.springframework.util.StringUtils;

import static cn.hippo4j.common.constant.Constants.GROUP_KEY_DELIMITER;

/**
 * Group key.
 *
 * @author chen.ma
 * @date 2021/6/24 21:12
 */
public class GroupKey {

    public static String getKey(String dataId, String group) {
        return getKey(dataId, group, "");
    }

    public static String getKey(String dataId, String group, String datumStr) {
        return doGetKey(dataId, group, datumStr);
    }

    public static String getKey(String... params) {
        StringBuilder groupKey = new StringBuilder();
        groupKey.append(params[0]).append(GROUP_KEY_DELIMITER);

        for (int i = 1; i < params.length - 1; i++) {
            groupKey.append(params[i]).append(GROUP_KEY_DELIMITER);
        }

        groupKey.append(params[params.length]);
        return groupKey.toString();
    }

    public static String getKeyTenant(String dataId, String group, String tenant) {
        return doGetKey(dataId, group, tenant);
    }

    private static String doGetKey(String dataId, String group, String datumStr) {
        StringBuilder sb = new StringBuilder();
        urlEncode(dataId, sb);
        sb.append(GROUP_KEY_DELIMITER);
        urlEncode(group, sb);
        if (!StringUtils.isEmpty(datumStr)) {
            sb.append(GROUP_KEY_DELIMITER);
            urlEncode(datumStr, sb);
        }

        return sb.toString();
    }

    public static String[] parseKey(String groupKey) {
        return groupKey.split(Constants.GROUP_KEY_DELIMITER_TRANSLATION);
    }

    public static void urlEncode(String str, StringBuilder sb) {
        for (int idx = 0; idx < str.length(); ++idx) {
            char c = str.charAt(idx);
            if ('+' == c) {
                sb.append("%2B");
            } else if ('%' == c) {
                sb.append("%25");
            } else {
                sb.append(c);
            }
        }
    }

}
