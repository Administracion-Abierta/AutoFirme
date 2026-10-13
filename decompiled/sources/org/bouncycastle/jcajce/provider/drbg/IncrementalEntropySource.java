package org.bouncycastle.jcajce.provider.drbg;

import org.bouncycastle.crypto.prng.EntropySource;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/drbg/IncrementalEntropySource.class */
interface IncrementalEntropySource extends EntropySource {
    byte[] getEntropy(long j) throws InterruptedException;
}
