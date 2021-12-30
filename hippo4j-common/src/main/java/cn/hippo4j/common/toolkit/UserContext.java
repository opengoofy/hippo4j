package cn.hippo4j.common.toolkit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User context (Transition scheme).
 *
 * @author chen.ma
 * @date 2021/11/17 21:13
 */
public class UserContext {

    private static final ThreadLocal<User> USER_THREAD_LOCAL = new ThreadLocal();

    public static void setUserInfo(String username, String userRole) {
        USER_THREAD_LOCAL.set(new User(username, userRole));
    }

    public static String getUserName() {
        return USER_THREAD_LOCAL.get().username;
    }

    public static String getUserRole() {
        return USER_THREAD_LOCAL.get().userRole;
    }

    public static void clear() {
        USER_THREAD_LOCAL.remove();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class User {

        private String username;

        private String userRole;

    }

}
