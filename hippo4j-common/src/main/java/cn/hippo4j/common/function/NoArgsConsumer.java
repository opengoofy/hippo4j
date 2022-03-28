package cn.hippo4j.common.function;

/**
 * 无参消费者.
 *
 * @author chen.ma
 * @date 2021/11/9 00:06
 */
@FunctionalInterface
public interface NoArgsConsumer {

    /**
     * 方法执行
     */
    void accept();

}
