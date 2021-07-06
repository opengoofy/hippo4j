package io.dynamic.threadpool.config.toolkit;

/**
 * 简单读写锁.
 *
 * @author chen.ma
 * @date 2021/6/24 21:26
 */
public class SimpleReadWriteLock {

    public synchronized boolean tryReadLock() {
        if (isWriteLocked()) {
            return false;
        } else {
            status++;
            return true;
        }
    }

    public synchronized void releaseReadLock() {
        status--;
    }

    public synchronized boolean tryWriteLock() {
        if (!isFree()) {
            return false;
        } else {
            status = -1;
            return true;
        }
    }

    public synchronized void releaseWriteLock() {
        status = 0;
    }

    private boolean isWriteLocked() {
        return status < 0;
    }

    private boolean isFree() {
        return status == 0;
    }

    private int status = 0;
}
