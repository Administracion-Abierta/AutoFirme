package org.spongycastle.asn1.x9;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/x9/X9ECParametersHolder.class */
public abstract class X9ECParametersHolder {
    private X9ECParameters params;

    protected abstract X9ECParameters createParameters();

    public synchronized X9ECParameters getParameters() {
        if (this.params == null) {
            this.params = createParameters();
        }
        return this.params;
    }
}
