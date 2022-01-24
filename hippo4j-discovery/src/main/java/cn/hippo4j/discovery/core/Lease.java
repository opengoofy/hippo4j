package cn.hippo4j.discovery.core;

/**
 * Lease.
 *
 * @author chen.ma
 * @date 2021/8/8 22:49
 */
public class Lease<T> {

    enum Action {
        /**
         * REGISTER
         */
        REGISTER,

        /**
         * CANCEL
         */
        CANCEL,

        /**
         * RENEW
         */
        RENEW
    }

    private T holder;

    private long evictionTimestamp;

    private long registrationTimestamp;

    private long serviceUpTimestamp;

    /**
     * Make it volatile so that the expiration task would see this quicker
     */
    private volatile long lastUpdateTimestamp;

    private long duration;

    public static final int DEFAULT_DURATION_IN_SECS = 90;

    public Lease(T r) {
        holder = r;
        registrationTimestamp = System.currentTimeMillis();
        lastUpdateTimestamp = registrationTimestamp;
        duration = DEFAULT_DURATION_IN_SECS * 1000;
    }

    public void renew() {
        lastUpdateTimestamp = System.currentTimeMillis() + duration;
    }

    public void cancel() {
        if (evictionTimestamp <= 0) {
            evictionTimestamp = System.currentTimeMillis();
        }
    }

    public void serviceUp() {
        if (serviceUpTimestamp == 0) {
            serviceUpTimestamp = System.currentTimeMillis();
        }
    }

    public void setServiceUpTimestamp(long serviceUpTimestamp) {
        this.serviceUpTimestamp = serviceUpTimestamp;
    }

    public boolean isExpired() {
        return isExpired(0L);
    }

    public boolean isExpired(long additionalLeaseMs) {
        return (evictionTimestamp > 0 || System.currentTimeMillis() > (lastUpdateTimestamp + duration + additionalLeaseMs));
    }

    public long getRegistrationTimestamp() {
        return registrationTimestamp;
    }

    public long getLastRenewalTimestamp() {
        return lastUpdateTimestamp;
    }

    public long getEvictionTimestamp() {
        return evictionTimestamp;
    }

    public long getServiceUpTimestamp() {
        return serviceUpTimestamp;
    }

    public T getHolder() {
        return holder;
    }

}
