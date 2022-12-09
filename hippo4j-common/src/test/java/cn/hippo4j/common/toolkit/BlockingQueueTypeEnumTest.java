package cn.hippo4j.common.toolkit;

import cn.hippo4j.common.executor.support.BlockingQueueTypeEnum;
import cn.hippo4j.common.spi.TestInterfaceSPI;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.BlockingQueue;

/**
 * @author hongdan.qin
 * @date 2022/12/5 20:01
 */
public class BlockingQueueTypeEnumTest {
    @Test
    void unboundQueue() {
        final BlockingQueue arrayBlockingQueue = BlockingQueueTypeEnum.createBlockingQueue("ArrayBlockingQueue", 10);
        System.out.println(arrayBlockingQueue);
    }

    @Test
    void wildGeneric() {
        List<?> ll = new ArrayList<String>();
        System.out.println(ll.get(0));
//        无法编译
//        ll.add("dd");
    }

    public static List<?> ll() {
        return null;
    }

    @Test
    void classLoader() {
        for (TestInterfaceSPI spi : ServiceLoader.load(TestInterfaceSPI.class)) {
            System.out.println(spi);
        }
    }

    @Test
    void test202212061813() {
        final BlockingQueue<Runnable> arrayBlockingQueue = BlockingQueueTypeEnum.createBlockingQueue("MyArrayBlockingQueue", null);
        System.out.println(arrayBlockingQueue);
//        arrayBlockingQueue.add("dd");

        BlockingQueue workQueue = BlockingQueueTypeEnum.createBlockingQueue("MyArrayBlockingQueue", 20);
        workQueue.add("dd");
        System.out.println(workQueue);

        BlockingQueue<Runnable> workQueue1 = BlockingQueueTypeEnum.createBlockingQueue("LinkedBlockingQueue", 20);
        workQueue1.add(new Runnable() {
            @Override
            public void run() {
            }
        });

        System.out.println(workQueue1);
    }

    interface O<T, R> {
        R say(T content);
    }

    abstract class S<T, R> implements O<T, R> {
        public abstract R say(T content);

        public <WT, WR> WR write(WT content) {
            return null;
        }
    }

    class C<T, R extends CharSequence> extends S<T, R> {
        @Override
        public R say(T content) {
            return null;
        }

        @Override
        public <WT1, WR1> WR1 write(WT1 content) {
            return super.write(content);
        }
    }

    class D<T, R> extends S<T, R> {
        @Override
        public R say(T content) {
            return null;
        }
//        can't be compile
//        @Override
//        public String write(String content) {
//            return super.write(content);
//        }

        //        @Override
        public <WT extends String, WR extends String> WR write(WT content) {
            return super.write(content);
        }
    }

    @Test
    void test() {
        D<String, String> d = new D<>();
        S<String, String> l = d;
        d.write("ddd");
        l.write(Integer.valueOf(2));
        l.write("ddd");
    }

    @Test
    void test202212071347(){
        final List<String> ls = Arrays.asList("sd", "2");
        System.out.println(ls);
    }

}
