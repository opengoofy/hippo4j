package cn.hippo4j.common.toolkit;

import cn.hippo4j.common.function.NoArgsConsumer;

/**
 * Condition util.
 *
 * @author chen.ma
 * @date 2021/11/6 22:40
 */
public class ConditionUtil {

    public static void condition(boolean condition, NoArgsConsumer trueConsumer, NoArgsConsumer falseConsumer) {
        if (condition) {
            trueConsumer.accept();
        } else {
            falseConsumer.accept();
        }
    }

}
