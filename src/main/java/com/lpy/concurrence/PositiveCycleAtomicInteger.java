package com.lpy.concurrence;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 正整数循环自动整数;<br/>
 * 若增加操作大于指定边界值，新值设置为0<br/>
 * 若减少操作小于边界值0，新值设置为指定最大边界值
 *
 * @author chengshaojin
 * @since 2018/12/7
 */
public class PositiveCycleAtomicInteger {

    /**
     * 上限-边界值
     */
    private int maxBound;

    private volatile AtomicInteger atomicInteger;

    /**
     * Creates a new AtomicInteger with initial value {@code 0}.
     */
    public PositiveCycleAtomicInteger(int maxBound) {
        if (maxBound <= 0) {
            throw new IllegalArgumentException("maxBound must greater than 0");
        }
        this.maxBound = maxBound;
        atomicInteger = new AtomicInteger();
    }

    /**
     * Creates a new AtomicInteger with the given positive initial value.
     *
     * @param maxBound     the upper limit of this cycle {@code PositiveCycleAtomicInteger}
     * @param initialValue the initial positive value (value must be greater than 0 and must be less than maxBound)
     */
    public PositiveCycleAtomicInteger(int maxBound, int initialValue) {
        if (maxBound <= 0) {
            throw new IllegalArgumentException("maxBound must greater than 0");
        }
        if (initialValue < 0) {
            throw new IllegalArgumentException("initialValue must be equal or greater than 0");
        }
        if (maxBound <= initialValue) {
            throw new IllegalArgumentException("initialValue must be greater than initialValue");
        }
        this.maxBound = maxBound;
        atomicInteger = new AtomicInteger(initialValue);
    }

    /**
     * Gets the current value.
     *
     * @return the current value
     */
    public final int get() {
        return atomicInteger.get();
    }

    /**
     * Sets to the given positive value.
     *
     * @param newValue the new positive value
     */
    public final void set(int newValue) {
        if (newValue < 0) {
            throw new IllegalArgumentException("initialValue must be equal or greater than 0");
        }
        atomicInteger.set(newValue);
    }

    /**
     * Eventually sets to the given value.
     *
     * @param newValue the new value
     * @since 1.6
     */
    public final void lazySet(int newValue) {
        if (newValue < 0) {
            throw new IllegalArgumentException("initialValue must be equal or greater than 0");
        }
        atomicInteger.lazySet(newValue);
    }

    /**
     * Atomically sets to the given value and returns the old value.
     *
     * @param newValue the new value
     * @return the previous value
     */
    public final int getAndSet(int newValue) {
        if (newValue < 0) {
            throw new IllegalArgumentException("initialValue must be equal or greater than 0");
        }
        for (; ; ) {
            int current = get();
            if (atomicInteger.compareAndSet(current, newValue)) {
                return current;
            }
        }
    }

    /**
     * Atomically increments by one the current value. If reach the upper limit, new value set to the lower limit 0;
     *
     * @return the previous value
     */
    public final int getAndIncrement() {
        for (; ; ) {
            int current = atomicInteger.get();
            int next = current == maxBound ? 0 : current + 1;
            if (atomicInteger.compareAndSet(current, next)) {
                return current;
            }
        }
    }

    /**
     * Atomically decrements by one the current value. If reach the lower limit, new value set to the specific upper limit.
     *
     * @return the previous value
     */
    public final int getAndDecrement() {
        for (; ; ) {
            int current = get();
            int next = current == 0 ? maxBound : current - 1;
            if (atomicInteger.compareAndSet(current, next)) {
                return current;
            }
        }
    }

    /**
     * Atomically adds the given value to the current value. If reach the upper limit, new value set to the lower limit 0;
     *
     * @param delta the value to add
     * @return the previous value
     */
    public final int getAndAdd(int delta) {
        for (; ; ) {
            int current = get();
            int next = current + delta;
            next = next > maxBound ? 0 : next;
            if (atomicInteger.compareAndSet(current, next)) {
                return current;
            }
        }
    }

    /**
     * Atomically increments by one the current value. If reach the upper limit, new value set to the lower limit 0;
     *
     * @return the updated value
     */
    public final int incrementAndGet() {
        for (; ; ) {
            int current = get();
            int next = current == maxBound ? 0 : current + 1;
            if (atomicInteger.compareAndSet(current, next)) {
                return next;
            }
        }
    }

    /**
     * Atomically decrements by one the current value. If reach the lower limit, new value set to the specific upper limit.
     *
     * @return the updated value
     */
    public final int decrementAndGet() {
        for (; ; ) {
            int current = get();
            int next = current == 0 ? maxBound : current - 1;
            if (atomicInteger.compareAndSet(current, next)) {
                return next;
            }
        }
    }

    /**
     * Atomically adds the given value to the current value. If reach the upper limit, new value set to the lower limit 0;
     *
     * @param delta the value to add
     * @return the updated value
     */
    public final int addAndGet(int delta) {
        for (; ; ) {
            int current = get();
            int next = current + delta;
            next = next > maxBound ? 0 : next;
            if (atomicInteger.compareAndSet(current, next)) {
                return next;
            }
        }
    }

    /**
     * Returns the String representation of the current value.
     *
     * @return the String representation of the current value.
     */
    @Override
    public String toString() {
        return Integer.toString(get());
    }


    public int intValue() {
        return get();
    }

    public long longValue() {
        return (long) get();
    }

    public float floatValue() {
        return (float) get();
    }

    public double doubleValue() {
        return (double) get();
    }
}
