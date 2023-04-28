package cn.hippo4j.core.proxy;

import cn.hippo4j.common.api.ThreadPoolCheckAlarm;
import cn.hippo4j.common.config.ApplicationContextHolder;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicLong;


@RunWith(MockitoJUnitRunner.class)
public class RejectedProxyInvocationHandlerTest {

    @Mock
    private ThreadPoolCheckAlarm mockAlarmHandler;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private Method mockMethod;
    private RejectedProxyInvocationHandler handler;

    @Before
    public void setUp() {
        Object target = new Object();
        String threadPoolId = "test-pool";
        AtomicLong rejectCount = new AtomicLong(0);
        handler = new RejectedProxyInvocationHandler(target, threadPoolId, rejectCount);
    }

    @Test
    public void testInvoke() throws Throwable {

        Object[] mockArgs = new Object[] {"arg1", "arg2"};
        MockedStatic<ApplicationContextHolder> mockedStatic = Mockito.mockStatic(ApplicationContextHolder.class);
        mockedStatic.when(ApplicationContextHolder::getInstance).thenReturn(applicationContext);
        mockedStatic.when(() -> ApplicationContextHolder.getBean(ThreadPoolCheckAlarm.class)).thenReturn(mockAlarmHandler);
        Mockito.doNothing().when(mockAlarmHandler).asyncSendRejectedAlarm("test-pool");
        handler.invoke(null, mockMethod, mockArgs);

        Mockito.when(mockMethod.invoke(new Object(), mockArgs)).thenThrow(new IllegalAccessException());
        try {
            handler.invoke(null, mockMethod, mockArgs);
        } catch (Exception ex){
            Assertions.assertTrue(ex instanceof InvocationTargetException);
        }
    }
}
