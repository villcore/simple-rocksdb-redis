package com.villcore.rocksdb.redis.server.monitor;

import com.villcore.rocksdb.redis.common.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

public class ServerMonitor {

    private static final Logger log = LoggerFactory.getLogger(ServerMonitor.class);

    private static final LongAdder totalChannelCount = new LongAdder();

    private static final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1,
            new NamedThreadFactory("simple-rocksdb-redis-monitor", true));

    static {
        log.info("Start log server status");
        scheduler.scheduleAtFixedRate(ServerMonitor::logServerStatus, 0L, 5L, TimeUnit.SECONDS);
    }

    public static void incrTotalChannelCount() {
        totalChannelCount.increment();
    }

    public static void decrTotalChannelCount() {
        totalChannelCount.decrement();
    }

    private static void logServerStatus() {
        log.info("total_channel_count {}", totalChannelCount.longValue());
    }
}
