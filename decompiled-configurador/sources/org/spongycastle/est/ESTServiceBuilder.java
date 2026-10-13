package org.spongycastle.est;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/est/ESTServiceBuilder.class */
public class ESTServiceBuilder {
    protected final String server;
    protected ESTClientProvider clientProvider;
    protected String label;

    public ESTServiceBuilder(String server) {
        this.server = server;
    }

    public ESTServiceBuilder withLabel(String label) {
        this.label = label;
        return this;
    }

    public ESTServiceBuilder withClientProvider(ESTClientProvider clientProvider) {
        this.clientProvider = clientProvider;
        return this;
    }

    public ESTService build() {
        return new ESTService(this.server, this.label, this.clientProvider);
    }
}
