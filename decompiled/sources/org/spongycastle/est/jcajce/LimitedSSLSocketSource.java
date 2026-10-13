package org.spongycastle.est.jcajce;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import org.spongycastle.est.LimitedSource;
import org.spongycastle.est.Source;
import org.spongycastle.est.TLSUniqueProvider;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/est/jcajce/LimitedSSLSocketSource.class */
class LimitedSSLSocketSource implements Source<SSLSession>, TLSUniqueProvider, LimitedSource {
    protected final SSLSocket socket;
    private final ChannelBindingProvider bindingProvider;
    private final Long absoluteReadLimit;

    public LimitedSSLSocketSource(SSLSocket sock, ChannelBindingProvider bindingProvider, Long absoluteReadLimit) {
        this.socket = sock;
        this.bindingProvider = bindingProvider;
        this.absoluteReadLimit = absoluteReadLimit;
    }

    @Override // org.spongycastle.est.Source
    public InputStream getInputStream() throws IOException {
        return this.socket.getInputStream();
    }

    @Override // org.spongycastle.est.Source
    public OutputStream getOutputStream() throws IOException {
        return this.socket.getOutputStream();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.spongycastle.est.Source
    public SSLSession getSession() {
        return this.socket.getSession();
    }

    @Override // org.spongycastle.est.TLSUniqueProvider
    public byte[] getTLSUnique() {
        if (isTLSUniqueAvailable()) {
            return this.bindingProvider.getChannelBinding(this.socket, "tls-unique");
        }
        throw new IllegalStateException("No binding provider.");
    }

    @Override // org.spongycastle.est.TLSUniqueProvider
    public boolean isTLSUniqueAvailable() {
        return this.bindingProvider.canAccessChannelBinding(this.socket);
    }

    @Override // org.spongycastle.est.Source
    public void close() throws IOException {
        this.socket.close();
    }

    @Override // org.spongycastle.est.LimitedSource
    public Long getAbsoluteReadLimit() {
        return this.absoluteReadLimit;
    }
}
