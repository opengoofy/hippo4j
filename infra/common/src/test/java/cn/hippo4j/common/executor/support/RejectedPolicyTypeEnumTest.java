package cn.hippo4j.common.executor.support;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * test for {@link RejectedPolicyTypeEnum}
 */
class RejectedPolicyTypeEnumTest {

    @Test
    void testGetType() {
        Assertions.assertEquals(1, RejectedPolicyTypeEnum.CALLER_RUNS_POLICY.getType());
        Assertions.assertEquals(2, RejectedPolicyTypeEnum.ABORT_POLICY.getType());
        Assertions.assertEquals(3, RejectedPolicyTypeEnum.DISCARD_POLICY.getType());
        Assertions.assertEquals(4, RejectedPolicyTypeEnum.DISCARD_OLDEST_POLICY.getType());
        Assertions.assertEquals(5, RejectedPolicyTypeEnum.RUNS_OLDEST_TASK_POLICY.getType());
        Assertions.assertEquals(6, RejectedPolicyTypeEnum.SYNC_PUT_QUEUE_POLICY.getType());
    }

    @Test
    void testGetName() {
        Assertions.assertEquals("CallerRunsPolicy", RejectedPolicyTypeEnum.CALLER_RUNS_POLICY.getName());
        Assertions.assertEquals("AbortPolicy", RejectedPolicyTypeEnum.ABORT_POLICY.getName());
        Assertions.assertEquals("DiscardPolicy", RejectedPolicyTypeEnum.DISCARD_POLICY.getName());
        Assertions.assertEquals("DiscardOldestPolicy", RejectedPolicyTypeEnum.DISCARD_OLDEST_POLICY.getName());
        Assertions.assertEquals("RunsOldestTaskPolicy", RejectedPolicyTypeEnum.RUNS_OLDEST_TASK_POLICY.getName());
        Assertions.assertEquals("SyncPutQueuePolicy", RejectedPolicyTypeEnum.SYNC_PUT_QUEUE_POLICY.getName());
    }

    @Test
    void testValues() {
        Assertions.assertNotNull(RejectedPolicyTypeEnum.values());
    }

    @Test
    void testValueOf() {
        Assertions.assertEquals(RejectedPolicyTypeEnum.CALLER_RUNS_POLICY, RejectedPolicyTypeEnum.valueOf("CALLER_RUNS_POLICY"));
        Assertions.assertEquals(RejectedPolicyTypeEnum.ABORT_POLICY, RejectedPolicyTypeEnum.valueOf("ABORT_POLICY"));
        Assertions.assertEquals(RejectedPolicyTypeEnum.DISCARD_POLICY, RejectedPolicyTypeEnum.valueOf("DISCARD_POLICY"));
        Assertions.assertEquals(RejectedPolicyTypeEnum.DISCARD_OLDEST_POLICY, RejectedPolicyTypeEnum.valueOf("DISCARD_OLDEST_POLICY"));
        Assertions.assertEquals(RejectedPolicyTypeEnum.RUNS_OLDEST_TASK_POLICY, RejectedPolicyTypeEnum.valueOf("RUNS_OLDEST_TASK_POLICY"));
        Assertions.assertEquals(RejectedPolicyTypeEnum.SYNC_PUT_QUEUE_POLICY, RejectedPolicyTypeEnum.valueOf("SYNC_PUT_QUEUE_POLICY"));
    }

    @Test
    void testCreatePolicy() {
        // check legal param: name and type
        Arrays.stream(RejectedPolicyTypeEnum.values()).forEach(each -> {
            Assertions.assertNotNull(RejectedPolicyTypeEnum.createPolicy(each.getName()));
            Assertions.assertNotNull(RejectedPolicyTypeEnum.createPolicy(each.getType()));
        });
        // check illegal null name
        Assertions.assertNotNull(RejectedPolicyTypeEnum.createPolicy(null));
        // check nonexistent name
        Assertions.assertNotNull(RejectedPolicyTypeEnum.createPolicy("ABC"));
        // check nonexistent type
        Assertions.assertNotNull(RejectedPolicyTypeEnum.createPolicy(-1));
    }

    @Test
    void testGetRejectedNameByType() {
        // check legal range of type
        Arrays.stream(RejectedPolicyTypeEnum.values()).forEach(each ->
                Assertions.assertEquals(each.getName(), RejectedPolicyTypeEnum.getRejectedNameByType(each.getType())));
        // check illegal range of type
        Assertions.assertEquals("AbortPolicy", RejectedPolicyTypeEnum.getRejectedNameByType(-1));
    }

    @Test
    void testGetRejectedPolicyTypeEnumByName() {
        // check legal range of name
        Arrays.stream(RejectedPolicyTypeEnum.values()).forEach(each ->
                Assertions.assertEquals(each, RejectedPolicyTypeEnum.getRejectedPolicyTypeEnumByName(each.getName())));
        // check illegal name
        Assertions.assertEquals(RejectedPolicyTypeEnum.ABORT_POLICY,
                RejectedPolicyTypeEnum.getRejectedPolicyTypeEnumByName("XXX"));
    }
}
