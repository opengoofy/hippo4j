package cn.hippo4j.common.toolkit;

/**
 * User context (Transition scheme).
 *
 * @author chen.ma
 * @date 2021/11/17 21:13
 */
public class UserContext {

    private static String username;

    public static void setUserName(String username) {
        UserContext.username = username;
    }

    public static String getUserName() {
        return username;
    }

}
