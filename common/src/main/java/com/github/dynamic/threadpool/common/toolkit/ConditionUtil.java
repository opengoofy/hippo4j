package com.github.dynamic.threadpool.common.toolkit;

import com.github.dynamic.threadpool.common.function.NoArgsConsumer;

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
