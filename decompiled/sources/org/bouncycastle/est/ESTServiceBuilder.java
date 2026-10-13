package org.bouncycastle.est;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/est/ESTServiceBuilder.class */
public class ESTServiceBuilder {
    protected final String server;
    protected ESTClientProvider clientProvider;
    protected String label;

    public ESTServiceBuilder(String str) {
        this.server = str;
    }

    public ESTServiceBuilder withLabel(String str) {
        this.label = str;
        return this;
    }

    public ESTServiceBuilder withClientProvider(ESTClientProvider eSTClientProvider) {
        this.clientProvider = eSTClientProvider;
        return this;
    }

    public ESTService build() {
        return new ESTService(this.server, this.label, this.clientProvider);
    }
}
