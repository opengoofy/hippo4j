package cn.hippo4j.example;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Example application.
 *
 * @author chen.ma
 * @date 2022/1/25 21:34
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExampleApplication.class)
public class ExampleApplication {

    @Test
    public void test() {
        log.info("test success.");
    }

}
