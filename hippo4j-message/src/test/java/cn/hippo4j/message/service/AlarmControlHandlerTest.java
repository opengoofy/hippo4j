package cn.hippo4j.message.service;

import cn.hippo4j.message.dto.AlarmControlDTO;
import cn.hippo4j.message.enums.NotifyTypeEnum;
import org.junit.Assert;
import org.junit.Test;

public class AlarmControlHandlerTest {

    @Test
    public void assertIsNotSendAlarm() {
        AlarmControlHandler alarmControlHandler = new AlarmControlHandler();
        AlarmControlDTO alarmControlDTO = new AlarmControlDTO("1", "Wechat", NotifyTypeEnum.ACTIVITY);
        Assert.assertFalse(alarmControlHandler.isSendAlarm(alarmControlDTO));
    }

    @Test
    public void assertIsSendAlarm() {
        AlarmControlHandler alarmControlHandler = new AlarmControlHandler();
        AlarmControlDTO alarmControlDTO = new AlarmControlDTO("1", "Wechat", NotifyTypeEnum.ACTIVITY);
        alarmControlHandler.initCacheAndLock("1", "Wechat", 1);
        Assert.assertTrue(alarmControlHandler.isSendAlarm(alarmControlDTO));
    }

    @Test
    public void assertExpireAfterSendAlarm() throws InterruptedException {
        AlarmControlHandler alarmControlHandler = new AlarmControlHandler();
        AlarmControlDTO alarmControlDTO = new AlarmControlDTO("1", "Wechat", NotifyTypeEnum.ACTIVITY);
        alarmControlHandler.initCacheAndLock("1", "Wechat", 1);
        alarmControlHandler.isSendAlarm(alarmControlDTO);
        Thread.sleep(60 * 1000);
        Assert.assertTrue(alarmControlHandler.isSendAlarm(alarmControlDTO));
    }
}
