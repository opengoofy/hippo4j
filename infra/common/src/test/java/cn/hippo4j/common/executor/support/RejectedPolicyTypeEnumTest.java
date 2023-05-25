package cn.hippo4j.common.executor.support;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * test for {@link RejectedPolicyTypeEnum}
 */
class RejectedPolicyTypeEnumTest {

    @Test
    void assertCreateRejectedPolicyNormal() {
        // check legal param: name and type
        Arrays.stream(RejectedPolicyTypeEnum.values()).forEach(each -> {
            Assertions.assertNotNull(RejectedPolicyTypeEnum.createPolicy(each.getName()));
            Assertions.assertNotNull(RejectedPolicyTypeEnum.createPolicy(each.getType()));
        });
    }

    @Test
    void assertCreateRejectedPolicyWithIllegalName() {
        // check illegal null name
        Assertions.assertNotNull(RejectedPolicyTypeEnum.createPolicy(null));
        // check nonexistent name
        Assertions.assertNotNull(RejectedPolicyTypeEnum.createPolicy("ABC"));
    }

    @Test
    void assertCreateRejectedPolicyWithIllegalType() {
        // check nonexistent type
        Assertions.assertNotNull(RejectedPolicyTypeEnum.createPolicy(-1));
    }

    @Test
    void assertGetRejectedPolicyNameByType() {
        // check legal range of type
        Arrays.stream(RejectedPolicyTypeEnum.values()).forEach(each ->
                Assertions.assertEquals(each.getName(), RejectedPolicyTypeEnum.getRejectedNameByType(each.getType())));
        // check illegal range of type
        Assertions.assertEquals("AbortPolicy", RejectedPolicyTypeEnum.getRejectedNameByType(-1));
    }

    @Test
    void assertGetRejectedPolicyTypeEnumByName() {
        // check legal range of name
        Arrays.stream(RejectedPolicyTypeEnum.values()).forEach(each ->
                Assertions.assertEquals(each, RejectedPolicyTypeEnum.getRejectedPolicyTypeEnumByName(each.getName())));
        // check illegal name
        Assertions.assertEquals(RejectedPolicyTypeEnum.ABORT_POLICY,
                RejectedPolicyTypeEnum.getRejectedPolicyTypeEnumByName("XXX"));
    }
}
