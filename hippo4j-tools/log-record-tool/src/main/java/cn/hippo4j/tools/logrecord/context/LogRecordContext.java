package cn.hippo4j.tools.logrecord.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Stack;

/**
 * 日志记录上下文.
 *
 * @author chen.ma
 * @date 2021/10/23 21:47
 */
public class LogRecordContext {

    private static final ThreadLocal<Stack<Map<String, Object>>> VARIABLE_MAP_STACK = new TransmittableThreadLocal();

    /**
     * 出栈.
     */
    public static void clear() {
        if (VARIABLE_MAP_STACK.get() != null) {
            VARIABLE_MAP_STACK.get().pop();
        }
    }

    /**
     * 初始化.
     */
    public static void putEmptySpan() {
        Stack<Map<String, Object>> mapStack = VARIABLE_MAP_STACK.get();
        if (mapStack == null) {
            Stack<Map<String, Object>> stack = new Stack<>();
            VARIABLE_MAP_STACK.set(stack);
        }

        VARIABLE_MAP_STACK.get().push(Maps.newHashMap());
    }

    public static Map<String, Object> getVariables() {
        Stack<Map<String, Object>> mapStack = VARIABLE_MAP_STACK.get();
        return mapStack.peek();
    }

}
