package org.spongycastle.est.jcajce;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import org.spongycastle.est.ESTClientSourceProvider;
import org.spongycastle.est.Source;
import org.spongycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/est/jcajce/DefaultESTClientSourceProvider.class */
class DefaultESTClientSourceProvider implements ESTClientSourceProvider {
    private final SSLSocketFactory sslSocketFactory;
    private final JsseHostnameAuthorizer hostNameAuthorizer;
    private final int timeout;
    private final ChannelBindingProvider bindingProvider;
    private final Set<String> cipherSuites;
    private final Long absoluteLimit;
    private final boolean filterSupportedSuites;

    public DefaultESTClientSourceProvider(SSLSocketFactory socketFactory, JsseHostnameAuthorizer hostNameAuthorizer, int timeout, ChannelBindingProvider bindingProvider, Set<String> cipherSuites, Long absoluteLimit, boolean filterSupportedSuites) throws GeneralSecurityException {
        this.sslSocketFactory = socketFactory;
        this.hostNameAuthorizer = hostNameAuthorizer;
        this.timeout = timeout;
        this.bindingProvider = bindingProvider;
        this.cipherSuites = cipherSuites;
        this.absoluteLimit = absoluteLimit;
        this.filterSupportedSuites = filterSupportedSuites;
    }

    @Override // org.spongycastle.est.ESTClientSourceProvider
    public Source makeSource(String host, int port) throws IOException {
        SSLSocket sock = (SSLSocket) this.sslSocketFactory.createSocket(host, port);
        sock.setSoTimeout(this.timeout);
        if (this.cipherSuites != null && !this.cipherSuites.isEmpty()) {
            if (this.filterSupportedSuites) {
                HashSet<String> fs = new HashSet<>();
                String[] supportedCipherSuites = sock.getSupportedCipherSuites();
                for (int i = 0; i != supportedCipherSuites.length; i++) {
                    fs.add(supportedCipherSuites[i]);
                }
                List<String> j = new ArrayList<>();
                for (String s : this.cipherSuites) {
                    if (fs.contains(s)) {
                        j.add(s);
                    }
                }
                if (j.isEmpty()) {
                    throw new IllegalStateException("No supplied cipher suite is supported by the provider.");
                }
                sock.setEnabledCipherSuites((String[]) j.toArray(new String[j.size()]));
            } else {
                sock.setEnabledCipherSuites((String[]) this.cipherSuites.toArray(new String[this.cipherSuites.size()]));
            }
        }
        sock.startHandshake();
        if (this.hostNameAuthorizer != null && !this.hostNameAuthorizer.verified(host, sock.getSession())) {
            throw new IOException("Host name could not be verified.");
        }
        String t = Strings.toLowerCase(sock.getSession().getCipherSuite());
        if (t.contains("_des_") || t.contains("_des40_") || t.contains("_3des_")) {
            throw new IOException("EST clients must not use DES ciphers");
        }
        if (Strings.toLowerCase(sock.getSession().getCipherSuite()).contains("null")) {
            throw new IOException("EST clients must not use NULL ciphers");
        }
        if (Strings.toLowerCase(sock.getSession().getCipherSuite()).contains("anon")) {
            throw new IOException("EST clients must not use anon ciphers");
        }
        if (Strings.toLowerCase(sock.getSession().getCipherSuite()).contains("export")) {
            throw new IOException("EST clients must not use export ciphers");
        }
        if (sock.getSession().getProtocol().equalsIgnoreCase("tlsv1")) {
            try {
                sock.close();
            } catch (Exception e) {
            }
            throw new IOException("EST clients must not use TLSv1");
        }
        if (this.hostNameAuthorizer != null && !this.hostNameAuthorizer.verified(host, sock.getSession())) {
            throw new IOException("Hostname was not verified: " + host);
        }
        return new LimitedSSLSocketSource(sock, this.bindingProvider, this.absoluteLimit);
    }
}
