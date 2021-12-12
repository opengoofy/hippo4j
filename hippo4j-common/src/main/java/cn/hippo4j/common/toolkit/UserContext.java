package cn.hippo4j.common.toolkit;

/**
 * User context (Transition scheme).
 *
 * @author chen.ma
 * @date 2021/11/17 21:13
 */
public class UserContext {

    private static String username;

    private static String userRole;

    public static void setUserName(String username) {
        UserContext.username = username;
    }

    public static void setUserRole(String userRole) {
        UserContext.userRole = userRole;
    }

    public static void setUserInfo(String username, String userRole) {
        UserContext.username = username;
        UserContext.userRole = userRole;
    }

    public static String getUserName() {
        return username;
    }

    public static String getUserRole() {
        return userRole;
    }

}
