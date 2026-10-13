package org.spongycastle.est.jcajce;

import java.net.Socket;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.net.ssl.KeyManager;
import javax.net.ssl.X509TrustManager;
import org.spongycastle.est.ESTClientProvider;
import org.spongycastle.est.ESTService;
import org.spongycastle.est.ESTServiceBuilder;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/est/jcajce/JsseESTServiceBuilder.class */
public class JsseESTServiceBuilder extends ESTServiceBuilder {
    protected SSLSocketFactoryCreator socketFactoryCreator;
    protected JsseHostnameAuthorizer hostNameAuthorizer;
    protected int timeoutMillis;
    protected ChannelBindingProvider bindingProvider;
    protected Set<String> supportedSuites;
    protected Long absoluteLimit;
    protected SSLSocketFactoryCreatorBuilder sslSocketFactoryCreatorBuilder;
    protected boolean filterCipherSuites;

    public JsseESTServiceBuilder(String server, SSLSocketFactoryCreator socketFactoryCreator) {
        super(server);
        this.hostNameAuthorizer = new JsseDefaultHostnameAuthorizer(null);
        this.timeoutMillis = 0;
        this.supportedSuites = new HashSet();
        this.filterCipherSuites = true;
        if (socketFactoryCreator == null) {
            throw new NullPointerException("No socket factory creator.");
        }
        this.socketFactoryCreator = socketFactoryCreator;
    }

    public JsseESTServiceBuilder(String server) {
        super(server);
        this.hostNameAuthorizer = new JsseDefaultHostnameAuthorizer(null);
        this.timeoutMillis = 0;
        this.supportedSuites = new HashSet();
        this.filterCipherSuites = true;
        this.sslSocketFactoryCreatorBuilder = new SSLSocketFactoryCreatorBuilder(JcaJceUtils.getTrustAllTrustManager());
    }

    public JsseESTServiceBuilder(String server, X509TrustManager trustManager) {
        super(server);
        this.hostNameAuthorizer = new JsseDefaultHostnameAuthorizer(null);
        this.timeoutMillis = 0;
        this.supportedSuites = new HashSet();
        this.filterCipherSuites = true;
        this.sslSocketFactoryCreatorBuilder = new SSLSocketFactoryCreatorBuilder(trustManager);
    }

    public JsseESTServiceBuilder(String server, X509TrustManager[] trustManager) {
        super(server);
        this.hostNameAuthorizer = new JsseDefaultHostnameAuthorizer(null);
        this.timeoutMillis = 0;
        this.supportedSuites = new HashSet();
        this.filterCipherSuites = true;
        this.sslSocketFactoryCreatorBuilder = new SSLSocketFactoryCreatorBuilder(trustManager);
    }

    public JsseESTServiceBuilder withHostNameAuthorizer(JsseHostnameAuthorizer hostNameAuthorizer) {
        this.hostNameAuthorizer = hostNameAuthorizer;
        return this;
    }

    @Override // org.spongycastle.est.ESTServiceBuilder
    public JsseESTServiceBuilder withClientProvider(ESTClientProvider clientProvider) {
        this.clientProvider = clientProvider;
        return this;
    }

    public JsseESTServiceBuilder withTimeout(int timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
        return this;
    }

    public JsseESTServiceBuilder withReadLimit(long absoluteLimit) {
        this.absoluteLimit = Long.valueOf(absoluteLimit);
        return this;
    }

    public JsseESTServiceBuilder withChannelBindingProvider(ChannelBindingProvider channelBindingProvider) {
        this.bindingProvider = channelBindingProvider;
        return this;
    }

    public JsseESTServiceBuilder addCipherSuites(String name) {
        this.supportedSuites.add(name);
        return this;
    }

    public JsseESTServiceBuilder addCipherSuites(String[] names) {
        this.supportedSuites.addAll(Arrays.asList(names));
        return this;
    }

    public JsseESTServiceBuilder withTLSVersion(String tlsVersion) {
        if (this.socketFactoryCreator != null) {
            throw new IllegalStateException("Socket Factory Creator was defined in the constructor.");
        }
        this.sslSocketFactoryCreatorBuilder.withTLSVersion(tlsVersion);
        return this;
    }

    public JsseESTServiceBuilder withSecureRandom(SecureRandom secureRandom) {
        if (this.socketFactoryCreator != null) {
            throw new IllegalStateException("Socket Factory Creator was defined in the constructor.");
        }
        this.sslSocketFactoryCreatorBuilder.withSecureRandom(secureRandom);
        return this;
    }

    public JsseESTServiceBuilder withProvider(String tlsProviderName) throws NoSuchProviderException {
        if (this.socketFactoryCreator != null) {
            throw new IllegalStateException("Socket Factory Creator was defined in the constructor.");
        }
        this.sslSocketFactoryCreatorBuilder.withProvider(tlsProviderName);
        return this;
    }

    public JsseESTServiceBuilder withProvider(Provider tlsProvider) {
        if (this.socketFactoryCreator != null) {
            throw new IllegalStateException("Socket Factory Creator was defined in the constructor.");
        }
        this.sslSocketFactoryCreatorBuilder.withProvider(tlsProvider);
        return this;
    }

    public JsseESTServiceBuilder withKeyManager(KeyManager keyManager) {
        if (this.socketFactoryCreator != null) {
            throw new IllegalStateException("Socket Factory Creator was defined in the constructor.");
        }
        this.sslSocketFactoryCreatorBuilder.withKeyManager(keyManager);
        return this;
    }

    public JsseESTServiceBuilder withKeyManagers(KeyManager[] keyManagers) {
        if (this.socketFactoryCreator != null) {
            throw new IllegalStateException("Socket Factory Creator was defined in the constructor.");
        }
        this.sslSocketFactoryCreatorBuilder.withKeyManagers(keyManagers);
        return this;
    }

    public JsseESTServiceBuilder withFilterCipherSuites(boolean filter) {
        this.filterCipherSuites = filter;
        return this;
    }

    @Override // org.spongycastle.est.ESTServiceBuilder
    public ESTService build() {
        if (this.bindingProvider == null) {
            this.bindingProvider = new ChannelBindingProvider() { // from class: org.spongycastle.est.jcajce.JsseESTServiceBuilder.1
                @Override // org.spongycastle.est.jcajce.ChannelBindingProvider
                public boolean canAccessChannelBinding(Socket sock) {
                    return false;
                }

                @Override // org.spongycastle.est.jcajce.ChannelBindingProvider
                public byte[] getChannelBinding(Socket sock, String binding) {
                    return null;
                }
            };
        }
        if (this.socketFactoryCreator == null) {
            this.socketFactoryCreator = this.sslSocketFactoryCreatorBuilder.build();
        }
        if (this.clientProvider == null) {
            this.clientProvider = new DefaultESTHttpClientProvider(this.hostNameAuthorizer, this.socketFactoryCreator, this.timeoutMillis, this.bindingProvider, this.supportedSuites, this.absoluteLimit, this.filterCipherSuites);
        }
        return super.build();
    }
}
