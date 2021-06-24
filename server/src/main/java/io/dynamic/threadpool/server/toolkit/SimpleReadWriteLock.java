package io.dynamic.threadpool.server.toolkit;

/**
 * 简单读写锁.
 *
 * @author chen.ma
 * @date 2021/6/24 21:26
 */
public class SimpleReadWriteLock {

    /**
     * Try read lock.
     */
    public synchronized boolean tryReadLock() {
        if (isWriteLocked()) {
            return false;
        } else {
            status++;
            return true;
        }
    }

    /**
     * Release the read lock.
     */
    public synchronized void releaseReadLock() {
        status--;
    }

    /**
     * Try write lock.
     */
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

    /**
     * Zero means no lock; Negative Numbers mean write locks; Positive Numbers mean read locks, and the numeric value
     * represents the number of read locks.
     */
    private int status = 0;
}
