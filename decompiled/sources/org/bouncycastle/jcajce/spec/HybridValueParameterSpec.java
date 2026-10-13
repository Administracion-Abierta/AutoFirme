package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.security.auth.Destroyable;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/spec/HybridValueParameterSpec.class */
public class HybridValueParameterSpec implements AlgorithmParameterSpec, Destroyable {
    private final AtomicBoolean hasBeenDestroyed;
    private final boolean doPrepend;
    private volatile byte[] t;
    private volatile AlgorithmParameterSpec baseSpec;

    public HybridValueParameterSpec(byte[] bArr, AlgorithmParameterSpec algorithmParameterSpec) {
        this(bArr, false, algorithmParameterSpec);
    }

    public HybridValueParameterSpec(byte[] bArr, boolean z, AlgorithmParameterSpec algorithmParameterSpec) {
        this.hasBeenDestroyed = new AtomicBoolean(false);
        this.t = bArr;
        this.baseSpec = algorithmParameterSpec;
        this.doPrepend = z;
    }

    public boolean isPrependedT() {
        return this.doPrepend;
    }

    public byte[] getT() {
        byte[] bArr = this.t;
        checkDestroyed();
        return bArr;
    }

    public AlgorithmParameterSpec getBaseParameterSpec() {
        AlgorithmParameterSpec algorithmParameterSpec = this.baseSpec;
        checkDestroyed();
        return algorithmParameterSpec;
    }

    @Override // javax.security.auth.Destroyable
    public boolean isDestroyed() {
        return this.hasBeenDestroyed.get();
    }

    @Override // javax.security.auth.Destroyable
    public void destroy() {
        if (this.hasBeenDestroyed.getAndSet(true)) {
            return;
        }
        Arrays.clear(this.t);
        this.t = null;
        this.baseSpec = null;
    }

    private void checkDestroyed() {
        if (isDestroyed()) {
            throw new IllegalStateException("spec has been destroyed");
        }
    }
}
