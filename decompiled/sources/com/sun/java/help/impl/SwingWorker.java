package com.sun.java.help.impl;

import javax.swing.SwingUtilities;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/impl/SwingWorker.class */
public abstract class SwingWorker {
    private Object value;
    private ThreadVar threadVar = new ThreadVar(new Thread(new Runnable(this, new Runnable(this) { // from class: com.sun.java.help.impl.SwingWorker.1
        private final SwingWorker this$0;

        {
            this.this$0 = this;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.this$0.finished();
        }
    }) { // from class: com.sun.java.help.impl.SwingWorker.2
        private final Runnable val$doFinished;
        private final SwingWorker this$0;

        {
            this.this$0 = this;
            this.val$doFinished = runnable;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                this.this$0.setValue(this.this$0.construct());
                this.this$0.threadVar.clear();
                SwingUtilities.invokeLater(this.val$doFinished);
            } catch (Throwable th) {
                this.this$0.threadVar.clear();
                throw th;
            }
        }
    }));

    public abstract Object construct();

    /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/impl/SwingWorker$ThreadVar.class */
    private static class ThreadVar {
        private Thread thread;

        ThreadVar(Thread thread) {
            this.thread = thread;
        }

        synchronized Thread get() {
            return this.thread;
        }

        synchronized void clear() {
            this.thread = null;
        }
    }

    protected synchronized Object getValue() {
        return this.value;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void setValue(Object obj) {
        this.value = obj;
    }

    public void finished() {
    }

    public void interrupt() {
        Thread thread = this.threadVar.get();
        if (thread != null) {
            thread.interrupt();
        }
        this.threadVar.clear();
    }

    public Object get() {
        while (true) {
            Thread thread = this.threadVar.get();
            if (thread == null) {
                return getValue();
            }
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
    }

    public void start() {
        start(5);
    }

    public void start(int i) {
        Thread thread = this.threadVar.get();
        if (thread != null) {
            if (i < 10 && i > 1) {
                thread.setPriority(i);
            }
            thread.start();
        }
    }
}
