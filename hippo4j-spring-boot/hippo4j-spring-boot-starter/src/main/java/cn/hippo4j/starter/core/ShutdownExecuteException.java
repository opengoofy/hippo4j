package cn.hippo4j.starter.core;

/**
 * Shutdown execute exception.
 *
 * @author chen.ma
 * @date 2021/12/9 21:48
 */
public class ShutdownExecuteException extends Exception {

    public ShutdownExecuteException() {
        super("Execute task when stopped.");
    }

}
