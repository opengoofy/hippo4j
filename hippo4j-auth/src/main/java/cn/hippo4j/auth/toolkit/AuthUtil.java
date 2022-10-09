package cn.hippo4j.auth.toolkit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {

    public static boolean enableAuthentication;

    @Value("${hippo4j.core.auth.enabled:true}")
    public void setEnableAuthentication(boolean enabled) {
        AuthUtil.enableAuthentication = enabled;
    }
}