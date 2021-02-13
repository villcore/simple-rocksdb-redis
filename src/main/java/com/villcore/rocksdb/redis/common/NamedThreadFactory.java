package com.villcore.rocksdb.redis.common;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public class NamedThreadFactory implements ThreadFactory {

    private final String name;
    private final boolean daemon;
    private final AtomicLong seq;

    public NamedThreadFactory(String name, boolean daemon) {
        this.name = name;
        this.daemon = daemon;
        this.seq = new AtomicLong(0L);
    }

    @Override
    public Thread newThread(Runnable r) {
        String name = this.name + "-" + seq.incrementAndGet();
        Thread t = new Thread(r, name);
        t.setDaemon(daemon);
        return t;
    }
}
