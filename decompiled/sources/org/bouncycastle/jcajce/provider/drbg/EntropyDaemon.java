package org.bouncycastle.jcajce.provider.drbg;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/drbg/EntropyDaemon.class */
class EntropyDaemon implements Runnable {
    private static final Logger LOG = Logger.getLogger(EntropyDaemon.class.getName());
    private final LinkedList<Runnable> tasks = new LinkedList<>();

    void addTask(Runnable runnable) {
        synchronized (this.tasks) {
            this.tasks.add(runnable);
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        Runnable runnablePoll;
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (this.tasks) {
                runnablePoll = this.tasks.poll();
            }
            if (runnablePoll != null) {
                runnablePoll.run();
            } else {
                try {
                    Thread.sleep(5000L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("entropy thread interrupted - exiting");
        }
    }
}
