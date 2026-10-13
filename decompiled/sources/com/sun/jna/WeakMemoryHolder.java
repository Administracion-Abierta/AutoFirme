package com.sun.jna;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.IdentityHashMap;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/WeakMemoryHolder.class */
public class WeakMemoryHolder {
    ReferenceQueue<Object> referenceQueue = new ReferenceQueue<>();
    IdentityHashMap<Reference<Object>, Memory> backingMap = new IdentityHashMap<>();

    public synchronized void put(Object o, Memory m) {
        clean();
        Reference<Object> reference = new WeakReference<>(o, this.referenceQueue);
        this.backingMap.put(reference, m);
    }

    public synchronized void clean() {
        Reference<? extends Object> referencePoll = this.referenceQueue.poll();
        while (true) {
            Reference<? extends Object> reference = referencePoll;
            if (reference != null) {
                this.backingMap.remove(reference);
                referencePoll = this.referenceQueue.poll();
            } else {
                return;
            }
        }
    }
}
