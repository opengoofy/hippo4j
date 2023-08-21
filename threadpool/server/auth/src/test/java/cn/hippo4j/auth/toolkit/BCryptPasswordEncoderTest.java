package cn.hippo4j.auth.toolkit;

import cn.hippo4j.common.toolkit.Assert;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptPasswordEncoderTest {

    @Test
    public void bCryptPasswordEncoderTest() {

        String password = "12345abc";

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode(password);
        boolean matches = bCryptPasswordEncoder.matches(password, encode);
        Assert.isTrue(matches);
    }

}
