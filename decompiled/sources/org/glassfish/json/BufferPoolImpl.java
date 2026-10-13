package org.glassfish.json;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.glassfish.json.api.BufferPool;

/* JADX INFO: loaded from: autofirma.jar:org/glassfish/json/BufferPoolImpl.class */
class BufferPoolImpl implements BufferPool {
    private volatile WeakReference<ConcurrentLinkedQueue<char[]>> queue;

    BufferPoolImpl() {
    }

    @Override // org.glassfish.json.api.BufferPool
    public final char[] take() {
        char[] t = getQueue().poll();
        if (t == null) {
            return new char[4096];
        }
        return t;
    }

    private ConcurrentLinkedQueue<char[]> getQueue() {
        ConcurrentLinkedQueue<char[]> d;
        WeakReference<ConcurrentLinkedQueue<char[]>> q = this.queue;
        if (q != null && (d = q.get()) != null) {
            return d;
        }
        ConcurrentLinkedQueue<char[]> d2 = new ConcurrentLinkedQueue<>();
        this.queue = new WeakReference<>(d2);
        return d2;
    }

    @Override // org.glassfish.json.api.BufferPool
    public final void recycle(char[] t) {
        getQueue().offer(t);
    }
}
