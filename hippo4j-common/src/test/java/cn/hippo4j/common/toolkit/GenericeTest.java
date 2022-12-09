package cn.hippo4j.common.toolkit;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author hongdan.qin
 * @date 2022/12/7 14:43
 */
public class GenericeTest {
    public void test() {
        final List<String> ls = Arrays.asList("sd", "2");
        System.out.println(ls.get(0));
    }

    @Test
    public void test1() {
        System.out.println(System.getProperty("java.class.path"));
    }
}
