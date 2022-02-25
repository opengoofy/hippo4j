package cn.hippo4j.core.executor.support;

import net.bytebuddy.agent.ByteBuddyAgent;

import java.lang.instrument.Instrumentation;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Can completely solve the OOM problem caused by {@link LinkedBlockingQueue}.
 *
 * @author ZhangZiCheng
 * @date 2022/2/24 20:47
 */
public class MemoryLimitedLinkedBlockingQueue<E> extends LinkedBlockingQueue<E> {

    private static final long serialVersionUID = 9066384065603805582L;

    private final Instrumentation inst;

    private long memoryLimit;

    private final LongAdder memory = new LongAdder();

    private final ReentrantLock acquireLock = new ReentrantLock();

    private final Condition notLimited = acquireLock.newCondition();

    private final ReentrantLock releaseLock = new ReentrantLock();

    private final Condition notEmpty = releaseLock.newCondition();

    static {
        //init ByteBuddyAgent
        ByteBuddyAgent.install();
    }

    public MemoryLimitedLinkedBlockingQueue() {
        this(Integer.MAX_VALUE);
    }

    public MemoryLimitedLinkedBlockingQueue(long memoryLimit) {
        super(Integer.MAX_VALUE);
        this.memoryLimit = memoryLimit;
        this.inst = ByteBuddyAgent.getInstrumentation();
    }

    public MemoryLimitedLinkedBlockingQueue(Collection<? extends E> c, long memoryLimit) {
        super(c);
        this.memoryLimit = memoryLimit;
        this.inst = ByteBuddyAgent.getInstrumentation();
    }

    public void setMemoryLimit(long memoryLimit) {
        if (memoryLimit <= 0) {
            throw new IllegalArgumentException();
        }
        this.memoryLimit = memoryLimit;
    }

    public long getMemoryLimit() {
        return memoryLimit;
    }

    public long getCurrentMemory() {
        return memory.sum();
    }

    public long getCurrentRemainMemory() {
        return getMemoryLimit() - getCurrentMemory();
    }

    /**
     * Signals a waiting take. Called only from put/offer (which do not
     * otherwise ordinarily lock takeLock.)
     */
    private void signalNotEmpty() {
        releaseLock.lock();
        try {
            notEmpty.signal();
        } finally {
            releaseLock.unlock();
        }
    }

    /**
     * Signals a waiting put. Called only from take/poll.
     */
    private void signalNotLimited() {
        acquireLock.lock();
        try {
            notLimited.signal();
        } finally {
            acquireLock.unlock();
        }
    }

    /**
     * Locks to prevent both puts and takes.
     */
    private void fullyLock() {
        acquireLock.lock();
        releaseLock.lock();
    }

    /**
     * Unlocks to allow both puts and takes.
     */
    private void fullyUnlock() {
        releaseLock.unlock();
        acquireLock.unlock();
    }

    @Override
    public void put(E e) throws InterruptedException {
        acquireInterruptibly(e);
        super.put(e);
    }

    private void acquireInterruptibly(E e) throws InterruptedException {
        if (e == null) {
            throw new NullPointerException();
        }
        final long objectSize = inst.getObjectSize(e);
        acquireLock.lockInterruptibly();
        try {
            while (memory.sum() + objectSize >= memoryLimit) {
                notLimited.await();
            }
            memory.add(objectSize);
            if (memory.sum() < memoryLimit) {
                notLimited.signal();
            }
        } finally {
            acquireLock.unlock();
        }
        if (memory.sum() > 0) {
            signalNotEmpty();
        }
    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        return acquire(e, timeout, unit) && super.offer(e, timeout, unit);
    }

    private boolean acquire(E e, long timeout, TimeUnit unit) throws InterruptedException {
        if (e == null) {
            throw new NullPointerException();
        }
        long nanos = unit.toNanos(timeout);
        final long objectSize = inst.getObjectSize(e);
        acquireLock.lockInterruptibly();
        try {
            while (memory.sum() + objectSize >= memoryLimit) {
                if (nanos <= 0) {
                    return false;
                }
                nanos = notLimited.awaitNanos(nanos);
            }
            memory.add(objectSize);
            if (memory.sum() < memoryLimit) {
                notLimited.signal();
            }
        } finally {
            acquireLock.unlock();
        }
        if (memory.sum() > 0) {
            signalNotEmpty();
        }
        return true;
    }

    @Override
    public boolean offer(E e) {
        return acquire(e) && super.offer(e);
    }

    private boolean acquire(E e) {
        if (e == null) {
            throw new NullPointerException();
        }
        if (memory.sum() >= memoryLimit) {
            return false;
        }
        acquireLock.lock();
        try {
            final long objectSize = inst.getObjectSize(e);
            if (memory.sum() + objectSize < memoryLimit) {
                memory.add(objectSize);
                if (memory.sum() < memoryLimit) {
                    notLimited.signal();
                }
            }
        } finally {
            acquireLock.unlock();
        }
        if (memory.sum() > 0) {
            signalNotEmpty();
        }
        return true;
    }

    @Override
    public E take() throws InterruptedException {
        final E e = super.take();
        releaseInterruptibly(e);
        return e;
    }

    private void releaseInterruptibly(E e) throws InterruptedException {
        if (null == e) {
            return;
        }
        final long objectSize = inst.getObjectSize(e);
        releaseLock.lockInterruptibly();
        try {
            while (memory.sum() == 0) {
                notEmpty.await();
            }
            memory.add(-objectSize);
            if (memory.sum() > 0) {
                notEmpty.signal();
            }
        } finally {
            releaseLock.unlock();
        }
        if (memory.sum() < memoryLimit) {
            signalNotLimited();
        }
    }

    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        final E e = super.poll(timeout, unit);
        releaseInterruptibly(e, timeout, unit);
        return e;
    }

    private void releaseInterruptibly(E e, long timeout, TimeUnit unit) throws InterruptedException {
        if (null == e) {
            return;
        }
        final long objectSize = inst.getObjectSize(e);
        long nanos = unit.toNanos(timeout);
        releaseLock.lockInterruptibly();
        try {
            while (memory.sum() == 0) {
                if (nanos <= 0) {
                    return;
                }
                nanos = notEmpty.awaitNanos(nanos);
            }
            memory.add(-objectSize);
            if (memory.sum() > 0) {
                notEmpty.signal();
            }
        } finally {
            releaseLock.unlock();
        }
        if (memory.sum() < memoryLimit) {
            signalNotLimited();
        }
    }

    @Override
    public E poll() {
        final E e = super.poll();
        release(e);
        return e;
    }

    private void release(Object e) {
        if (null == e) {
            return;
        }
        if (memory.sum() == 0) {
            return;
        }
        final long objectSize = inst.getObjectSize(e);
        releaseLock.lock();
        try {
            if (memory.sum() > 0) {
                memory.add(-objectSize);
                if (memory.sum() > 0) {
                    notEmpty.signal();
                }
            }
        } finally {
            releaseLock.unlock();
        }
        if (memory.sum() < memoryLimit) {
            signalNotLimited();
        }
    }

    @Override
    public boolean remove(Object o) {
        final boolean success = super.remove(o);
        if (success) {
            release(o);
        }
        return success;
    }

    @Override
    public void clear() {
        super.clear();
        fullyLock();
        try {
            if (memory.sumThenReset() < memoryLimit) {
                notLimited.signal();
            }
        } finally {
            fullyUnlock();
        }
    }
}
