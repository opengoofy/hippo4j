package cn.hippo4j.auth.filter;

import cn.hippo4j.common.toolkit.ReflectUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

class JWTAuthenticationFilterTest {

    @Test
    void getMessageTest() {
        JWTAuthenticationFilter filter = new JWTAuthenticationFilter(null);
        Assertions.assertEquals("用户不存在", ReflectUtil.invoke(filter,
                "getMessage", new UsernameNotFoundException("")));
        Assertions.assertEquals("密码错误", ReflectUtil.invoke(filter,
                "getMessage", new BadCredentialsException("")));
    }
}