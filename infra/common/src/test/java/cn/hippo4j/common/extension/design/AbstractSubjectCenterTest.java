package cn.hippo4j.common.extension.design;

import lombok.Getter;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class AbstractSubjectCenterTest {

    /**
     *  test Subject center
     */
    @Test
    public void testSubjectCenter() {
        AbstractSubjectCenter.SubjectType subjectType = AbstractSubjectCenter.SubjectType.THREAD_POOL_DYNAMIC_REFRESH;
        SubjectNotifyListener subjectNotifyListener = new SubjectNotifyListener();
        NotifyMessage notifyMessage = new NotifyMessage();
        Assert.assertEquals(0, notifyMessage.getCount().get());

        AbstractSubjectCenter.register(subjectType, subjectNotifyListener);
        Assert.assertEquals(1, AbstractSubjectCenter.size(subjectType));

        AbstractSubjectCenter.notify(subjectType, () -> notifyMessage);
        Assert.assertEquals(1, notifyMessage.getCount().get());

        AbstractSubjectCenter.remove(subjectType.name(), subjectNotifyListener);
        Assert.assertEquals(0, AbstractSubjectCenter.size(subjectType));
    }


    @Getter
    private static final class NotifyMessage {
        private final AtomicInteger count = new AtomicInteger(0);
    }

    /**
     * Subject Response Listener
     */
    private static final class SubjectNotifyListener implements Observer<NotifyMessage>{

        @Override
        public void accept(ObserverMessage<NotifyMessage> observerMessage) {
            observerMessage.message().getCount().incrementAndGet();
        }
    }
}
