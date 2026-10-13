package org.java_websocket.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/* JADX INFO: loaded from: autofirma.jar:org/java_websocket/util/NamedThreadFactory.class */
public class NamedThreadFactory implements ThreadFactory {
    private final ThreadFactory defaultThreadFactory;
    private final AtomicInteger threadNumber;
    private final String threadPrefix;
    private final boolean daemon;

    public NamedThreadFactory(String threadPrefix) {
        this.defaultThreadFactory = Executors.defaultThreadFactory();
        this.threadNumber = new AtomicInteger(1);
        this.threadPrefix = threadPrefix;
        this.daemon = false;
    }

    public NamedThreadFactory(String threadPrefix, boolean daemon) {
        this.defaultThreadFactory = Executors.defaultThreadFactory();
        this.threadNumber = new AtomicInteger(1);
        this.threadPrefix = threadPrefix;
        this.daemon = daemon;
    }

    @Override // java.util.concurrent.ThreadFactory
    public Thread newThread(Runnable runnable) {
        Thread thread = this.defaultThreadFactory.newThread(runnable);
        thread.setDaemon(this.daemon);
        thread.setName(this.threadPrefix + "-" + this.threadNumber);
        return thread;
    }
}
