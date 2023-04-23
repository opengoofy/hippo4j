package cn.hippo4j.auth.service.impl;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

class UserServiceImplTest {

    @Test
    void checkPasswordLength() {
        //密码为null、空串、过短、过长都会抛出异常
        UserServiceImpl userService = new UserServiceImpl(null, null, null);
        Assert.assertThrows(RuntimeException.class, () -> userService.checkPasswordLength(null));
        Assert.assertThrows(RuntimeException.class, () -> userService.checkPasswordLength(""));
        String shortPassword = "12345";
        Assert.assertThrows(RuntimeException.class, () -> userService.checkPasswordLength(shortPassword));
        String LongPassword = "fjhdjfghdsgahfgajdhsgafghdsbvhbervjdsvhdsbhfbhsdbhfbhsdbavbsbdhjfbhjsdbhfbsdbf";
        Assert.assertThrows(RuntimeException.class, () -> userService.checkPasswordLength(LongPassword));
    }

}