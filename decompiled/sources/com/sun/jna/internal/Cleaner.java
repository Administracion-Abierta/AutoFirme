package com.sun.jna.internal;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/internal/Cleaner.class */
public class Cleaner {
    private static final Cleaner INSTANCE = new Cleaner();
    private final ReferenceQueue<Object> referenceQueue = new ReferenceQueue<>();
    private final Thread cleanerThread = new Thread() { // from class: com.sun.jna.internal.Cleaner.1
        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            while (true) {
                try {
                    Reference<? extends Object> ref = Cleaner.this.referenceQueue.remove();
                    if (ref instanceof CleanerRef) {
                        ((CleanerRef) ref).clean();
                    }
                } catch (InterruptedException e) {
                    return;
                } catch (Exception ex) {
                    Logger.getLogger(Cleaner.class.getName()).log(Level.SEVERE, (String) null, (Throwable) ex);
                }
            }
        }
    };
    private CleanerRef firstCleanable;

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/internal/Cleaner$Cleanable.class */
    public interface Cleanable {
        void clean();
    }

    public static Cleaner getCleaner() {
        return INSTANCE;
    }

    private Cleaner() {
        this.cleanerThread.setName("JNA Cleaner");
        this.cleanerThread.setDaemon(true);
        this.cleanerThread.start();
    }

    public synchronized Cleanable register(Object obj, Runnable cleanupTask) {
        return add(new CleanerRef(this, obj, this.referenceQueue, cleanupTask));
    }

    private synchronized CleanerRef add(CleanerRef ref) {
        if (this.firstCleanable == null) {
            this.firstCleanable = ref;
        } else {
            ref.setNext(this.firstCleanable);
            this.firstCleanable.setPrevious(ref);
            this.firstCleanable = ref;
        }
        return ref;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized boolean remove(CleanerRef ref) {
        boolean inChain = false;
        if (ref == this.firstCleanable) {
            this.firstCleanable = ref.getNext();
            inChain = true;
        }
        if (ref.getPrevious() != null) {
            ref.getPrevious().setNext(ref.getNext());
        }
        if (ref.getNext() != null) {
            ref.getNext().setPrevious(ref.getPrevious());
        }
        if (ref.getPrevious() != null || ref.getNext() != null) {
            inChain = true;
        }
        ref.setNext(null);
        ref.setPrevious(null);
        return inChain;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/internal/Cleaner$CleanerRef.class */
    private static class CleanerRef extends PhantomReference<Object> implements Cleanable {
        private final Cleaner cleaner;
        private final Runnable cleanupTask;
        private CleanerRef previous;
        private CleanerRef next;

        public CleanerRef(Cleaner cleaner, Object referent, ReferenceQueue<? super Object> q, Runnable cleanupTask) {
            super(referent, q);
            this.cleaner = cleaner;
            this.cleanupTask = cleanupTask;
        }

        @Override // com.sun.jna.internal.Cleaner.Cleanable
        public void clean() {
            if (this.cleaner.remove(this)) {
                this.cleanupTask.run();
            }
        }

        CleanerRef getPrevious() {
            return this.previous;
        }

        void setPrevious(CleanerRef previous) {
            this.previous = previous;
        }

        CleanerRef getNext() {
            return this.next;
        }

        void setNext(CleanerRef next) {
            this.next = next;
        }
    }
}
