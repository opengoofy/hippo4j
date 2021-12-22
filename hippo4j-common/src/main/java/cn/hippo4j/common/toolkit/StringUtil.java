package cn.hippo4j.common.toolkit;

/**
 * String util.
 *
 * @author chen.ma
 * @date 2021/12/22 20:58
 */
public class StringUtil {

    public static final String EMPTY = "";

    /**
     * Is blank.
     *
     * @param str
     * @return
     */
    public static boolean isBlank(String str) {
        int length;

        if ((str == null) || ((length = str.length()) == 0)) {
            return true;
        }

        for (int i = 0; i < length; i++) {
            char c = str.charAt(i);

            boolean charNotBlank = Character.isWhitespace(c) || Character.isSpaceChar(c) || c == '\ufeff' || c == '\u202a';
            if (charNotBlank == false) {
                return false;
            }
        }

        return true;
    }

    /**
     * Is not blank.
     *
     * @param str
     * @return
     */
    public static boolean isNotBlank(String str) {
        return isBlank(str) == false;
    }

}
