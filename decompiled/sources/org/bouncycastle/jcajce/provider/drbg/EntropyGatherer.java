package org.bouncycastle.jcajce.provider.drbg;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/drbg/EntropyGatherer.class */
class EntropyGatherer implements Runnable {
    private static final Logger LOG = Logger.getLogger(EntropyGatherer.class.getName());
    private final long pause = getPause();
    private final AtomicBoolean seedAvailable;
    private final AtomicReference<byte[]> entropy;
    private final IncrementalEntropySource baseRandom;

    EntropyGatherer(IncrementalEntropySource incrementalEntropySource, AtomicBoolean atomicBoolean, AtomicReference<byte[]> atomicReference) {
        this.baseRandom = incrementalEntropySource;
        this.seedAvailable = atomicBoolean;
        this.entropy = atomicReference;
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            this.entropy.set(this.baseRandom.getEntropy(this.pause));
            this.seedAvailable.set(true);
        } catch (InterruptedException e) {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("entropy request interrupted - exiting");
            }
            Thread.currentThread().interrupt();
        }
    }

    private static long getPause() {
        String propertyValue = Properties.getPropertyValue("org.bouncycastle.drbg.gather_pause_secs");
        if (propertyValue == null) {
            return 5000L;
        }
        try {
            return Long.parseLong(propertyValue) * 1000;
        } catch (Exception e) {
            return 5000L;
        }
    }
}
