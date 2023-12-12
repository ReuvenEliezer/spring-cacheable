/*
 * Copyright (c) 2017 Pierantonio Cangianiello
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.cache.services.selfexpired;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.TaskScheduler;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;

/**
 * A thread-safe implementation of a HashMap which entries expires after the specified life time.
 * The life-time can be defined on a per-key basis, or using a default one, that is passed to the
 * constructor.
 *
 * @param <K> the Key type
 * @param <V> the Value type
 * @author Pierantonio Cangianiello
 */
public class SelfExpiringMapImpl<K, V> implements SelfExpiringMap<K, V> {

    private static final Logger logger = LogManager.getLogger(SelfExpiringMapImpl.class);

    private ApplicationEventPublisher publisher;

    private final Map<K, V> internalMap;

//    private final Map<K, ExpiringKey<K>> expiringKeys;

    /**
     * Holds the map keys using the given life time for expiration.
     */
    private final DelayQueue<ExpiringKey> delayQueue = new DelayQueue<>();

    /**
     * The default max life time in milliseconds.
     */
    private final Duration maxLifeTime;

    private Object lock = new Object();

    private TaskScheduler taskScheduler;

    private ScheduledFuture<?> timer;

    private boolean isStop = false;

    public SelfExpiringMapImpl(Duration maxLifeTime, TaskScheduler taskScheduler, ApplicationEventPublisher publisher) {
        internalMap = new ConcurrentHashMap<>();
//        expiringKeys = new WeakHashMap<>();
        this.maxLifeTime = maxLifeTime;
        this.taskScheduler = taskScheduler;
        this.publisher = publisher;
    }

    @Override
    public void start() {
        if (timer == null) {
            System.out.println("Start tracking Monitor Expired Object Timer");
            timer = taskScheduler.scheduleWithFixedDelay(() -> cleanup(), 3000);
        }
    }

    @Override
    public void stop() {
        tryStopTimer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        synchronized (lock) {
            return internalMap.size();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        synchronized (lock) {
            return internalMap.isEmpty();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsKey(Object key) {
        synchronized (lock) {
            return internalMap.containsKey(key);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsValue(Object value) {
        synchronized (lock) {
            return internalMap.containsValue(value);
        }
    }

    @Override
    public V get(Object key) {
//        renewKey((K) key);
        return internalMap.get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V put(K key, V value) {
        return this.put(key, value, maxLifeTime.toMillis());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V put(K key, V value, long lifeTimeMillis) {
        synchronized (lock) {
//            logger.debug("put, Thread ID:" + Thread.currentThread().getId());

            if (delayQueue.peek() == null) {
                logger.debug("try to starting timer");
                start();
            }
            ExpiringKey delayedKey = new ExpiringKey(key, lifeTimeMillis);
//            ExpiringKey oldKey = expiringKeys.put(key, delayedKey);
//            expireKey(oldKey);
            delayQueue.add(delayedKey);
            return internalMap.put(key, value);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public V remove(Object key) {
        synchronized (lock) {
            V removedValue = internalMap.remove(key);
//            expireKey(expiringKeys.remove(key));
            if (internalMap.isEmpty()) {
                stop();
            }
            return removedValue;
        }
    }

    /**
     * Not supported.
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
//    @Override
//    public boolean renewKey(K key) {
//        synchronized (lock) {
//            ExpiringKey<K> delayedKey = expiringKeys.get(key);
//            if (delayedKey != null) {
//                delayedKey.renew();
//                return true;
//            }
//            return false;
//        }
//    }

//    private void expireKey(ExpiringKey<K> delayedKey) {
//        if (delayedKey != null) {
//            delayedKey.expire();
//        }
//    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        synchronized (lock) {
            delayQueue.clear();
//            expiringKeys.clear();
            internalMap.clear();
        }
    }

    /**
     * Not supported.
     */
    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported.
     */
    @Override
    public Collection<V> values() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported.
     */
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException();
    }

    private void cleanup() {
//        logger.debug("cleanup, Thread ID:" + Thread.currentThread().getId());

        if (isStop) {
            tryStopTimer();
            return;
        }

        List<V> toPublish = new ArrayList<>();
        synchronized (lock) {
            logger.debug(String.format("Thread id : %s, delayQueue.size is: %s", Thread.currentThread().getId(), delayQueue.size()));
            ExpiringKey<K> delayedKey = delayQueue.poll();

            while (delayedKey != null) {
                K key = delayedKey.getKey();

                logger.debug(String.format("delayQueue.size of key %s is: %s", key, delayQueue.size()));
                V objectValue = internalMap.get(key);
                if (objectValue != null)
                    toPublish.add(objectValue);

                internalMap.remove(key);
//                expiringKeys.remove(key);
                delayedKey = delayQueue.poll();
            }

            if (delayQueue.isEmpty()) {
                logger.debug("delayQueue.isEmpty()");
                tryStopTimer();
            }
        }

        for (V v : toPublish) {
            publisher.publishEvent(v);
        }
    }

    private void tryStopTimer() {
        final String methodName = "tryStopTimer";
        try {
            if (timer != null) {
                logger.debug(methodName + " stopping..");
                timer.cancel(false);
                logger.debug(methodName + " stopped");
            }
        } finally {
            timer = null;
        }
    }


    private class ExpiringKey<K> implements Delayed {

        private long startTime = System.currentTimeMillis();
        private final long maxLifeTimeMillis;
        private final K key;

        public ExpiringKey(K key, long maxLifeTimeMillis) {
            this.maxLifeTimeMillis = maxLifeTimeMillis;
            this.key = key;
        }

        public K getKey() {
            return key;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ExpiringKey<K> other = (ExpiringKey<K>) obj;
            if (this.key != other.key && (this.key == null || !this.key.equals(other.key))) {
                return false;
            }
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            int hash = 7;
            hash = 31 * hash + (this.key != null ? this.key.hashCode() : 0);
            return hash;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(getDelayMillis(), TimeUnit.MILLISECONDS);
        }

        private long getDelayMillis() {
            return (startTime + maxLifeTimeMillis) - System.currentTimeMillis();
        }

        public void renew() {
            startTime = System.currentTimeMillis();
        }

        public void expire() {
            startTime = System.currentTimeMillis() - maxLifeTimeMillis - 1;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int compareTo(Delayed that) {
            return Long.compare(this.getDelayMillis(), ((ExpiringKey) that).getDelayMillis());
        }
    }
}
