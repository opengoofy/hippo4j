package cn.hippo4j.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;

/**
 * Mockito 集成测试
 *
 * @author 王杰
 * So close, so far away
 */
@ExtendWith(MockitoExtension.class)
public class MockitoTests {

    @Mock
    List<String> list;


    @Test
    void mockTests() {
        Mockito.when(list.get(1)).thenReturn("mock success.");
        Assertions.assertEquals("mock success.",list.get(1));
    }
}
