package org.bouncycastle.est.jcajce;

import java.net.Socket;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.net.ssl.KeyManager;
import javax.net.ssl.X509TrustManager;
import org.bouncycastle.est.ESTClientProvider;
import org.bouncycastle.est.ESTService;
import org.bouncycastle.est.ESTServiceBuilder;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/est/jcajce/JsseESTServiceBuilder.class */
public class JsseESTServiceBuilder extends ESTServiceBuilder {
    protected SSLSocketFactoryCreator socketFactoryCreator;
    protected JsseHostnameAuthorizer hostNameAuthorizer;
    protected int timeoutMillis;
    protected ChannelBindingProvider bindingProvider;
    protected Set<String> supportedSuites;
    protected Long absoluteLimit;
    protected SSLSocketFactoryCreatorBuilder sslSocketFactoryCreatorBuilder;
    protected boolean filterCipherSuites;

    public JsseESTServiceBuilder(String str, int i, SSLSocketFactoryCreator sSLSocketFactoryCreator) {
        super(str + ":" + i);
        this.hostNameAuthorizer = new JsseDefaultHostnameAuthorizer(null);
        this.timeoutMillis = 0;
        this.supportedSuites = new HashSet();
        this.filterCipherSuites = true;
        if (sSLSocketFactoryCreator == null) {
            throw new NullPointerException("No socket factory creator.");
        }
        this.socketFactoryCreator = sSLSocketFactoryCreator;
    }

    public JsseESTServiceBuilder(String str, SSLSocketFactoryCreator sSLSocketFactoryCreator) {
        super(str);
        this.hostNameAuthorizer = new JsseDefaultHostnameAuthorizer(null);
        this.timeoutMillis = 0;
        this.supportedSuites = new HashSet();
        this.filterCipherSuites = true;
        if (sSLSocketFactoryCreator == null) {
            throw new NullPointerException("No socket factory creator.");
        }
        this.socketFactoryCreator = sSLSocketFactoryCreator;
    }

    public JsseESTServiceBuilder(String str) {
        super(str);
        this.hostNameAuthorizer = new JsseDefaultHostnameAuthorizer(null);
        this.timeoutMillis = 0;
        this.supportedSuites = new HashSet();
        this.filterCipherSuites = true;
        this.sslSocketFactoryCreatorBuilder = new SSLSocketFactoryCreatorBuilder(JcaJceUtils.getTrustAllTrustManager());
    }

    public JsseESTServiceBuilder(String str, int i, X509TrustManager x509TrustManager) {
        super(str + ":" + i);
        this.hostNameAuthorizer = new JsseDefaultHostnameAuthorizer(null);
        this.timeoutMillis = 0;
        this.supportedSuites = new HashSet();
        this.filterCipherSuites = true;
        this.sslSocketFactoryCreatorBuilder = new SSLSocketFactoryCreatorBuilder(x509TrustManager);
    }

    public JsseESTServiceBuilder(String str, X509TrustManager x509TrustManager) {
        super(str);
        this.hostNameAuthorizer = new JsseDefaultHostnameAuthorizer(null);
        this.timeoutMillis = 0;
        this.supportedSuites = new HashSet();
        this.filterCipherSuites = true;
        this.sslSocketFactoryCreatorBuilder = new SSLSocketFactoryCreatorBuilder(x509TrustManager);
    }

    public JsseESTServiceBuilder(String str, int i, X509TrustManager[] x509TrustManagerArr) {
        this(str + ":" + i, x509TrustManagerArr);
    }

    public JsseESTServiceBuilder(String str, X509TrustManager[] x509TrustManagerArr) {
        super(str);
        this.hostNameAuthorizer = new JsseDefaultHostnameAuthorizer(null);
        this.timeoutMillis = 0;
        this.supportedSuites = new HashSet();
        this.filterCipherSuites = true;
        this.sslSocketFactoryCreatorBuilder = new SSLSocketFactoryCreatorBuilder(x509TrustManagerArr);
    }

    public JsseESTServiceBuilder withHostNameAuthorizer(JsseHostnameAuthorizer jsseHostnameAuthorizer) {
        this.hostNameAuthorizer = jsseHostnameAuthorizer;
        return this;
    }

    @Override // org.bouncycastle.est.ESTServiceBuilder
    public JsseESTServiceBuilder withClientProvider(ESTClientProvider eSTClientProvider) {
        this.clientProvider = eSTClientProvider;
        return this;
    }

    public JsseESTServiceBuilder withTimeout(int i) {
        this.timeoutMillis = i;
        return this;
    }

    public JsseESTServiceBuilder withReadLimit(long j) {
        this.absoluteLimit = Long.valueOf(j);
        return this;
    }

    public JsseESTServiceBuilder withChannelBindingProvider(ChannelBindingProvider channelBindingProvider) {
        this.bindingProvider = channelBindingProvider;
        return this;
    }

    public JsseESTServiceBuilder addCipherSuites(String str) {
        this.supportedSuites.add(str);
        return this;
    }

    public JsseESTServiceBuilder addCipherSuites(String[] strArr) {
        this.supportedSuites.addAll(Arrays.asList(strArr));
        return this;
    }

    public JsseESTServiceBuilder withTLSVersion(String str) {
        if (this.socketFactoryCreator != null) {
            throw new IllegalStateException("Socket Factory Creator was defined in the constructor.");
        }
        this.sslSocketFactoryCreatorBuilder.withTLSVersion(str);
        return this;
    }

    public JsseESTServiceBuilder withSecureRandom(SecureRandom secureRandom) {
        if (this.socketFactoryCreator != null) {
            throw new IllegalStateException("Socket Factory Creator was defined in the constructor.");
        }
        this.sslSocketFactoryCreatorBuilder.withSecureRandom(secureRandom);
        return this;
    }

    public JsseESTServiceBuilder withProvider(String str) throws NoSuchProviderException {
        if (this.socketFactoryCreator != null) {
            throw new IllegalStateException("Socket Factory Creator was defined in the constructor.");
        }
        this.sslSocketFactoryCreatorBuilder.withProvider(str);
        return this;
    }

    public JsseESTServiceBuilder withProvider(Provider provider) {
        if (this.socketFactoryCreator != null) {
            throw new IllegalStateException("Socket Factory Creator was defined in the constructor.");
        }
        this.sslSocketFactoryCreatorBuilder.withProvider(provider);
        return this;
    }

    public JsseESTServiceBuilder withKeyManager(KeyManager keyManager) {
        if (this.socketFactoryCreator != null) {
            throw new IllegalStateException("Socket Factory Creator was defined in the constructor.");
        }
        this.sslSocketFactoryCreatorBuilder.withKeyManager(keyManager);
        return this;
    }

    public JsseESTServiceBuilder withKeyManagers(KeyManager[] keyManagerArr) {
        if (this.socketFactoryCreator != null) {
            throw new IllegalStateException("Socket Factory Creator was defined in the constructor.");
        }
        this.sslSocketFactoryCreatorBuilder.withKeyManagers(keyManagerArr);
        return this;
    }

    public JsseESTServiceBuilder withFilterCipherSuites(boolean z) {
        this.filterCipherSuites = z;
        return this;
    }

    @Override // org.bouncycastle.est.ESTServiceBuilder
    public ESTService build() {
        if (this.bindingProvider == null) {
            this.bindingProvider = new ChannelBindingProvider() { // from class: org.bouncycastle.est.jcajce.JsseESTServiceBuilder.1
                @Override // org.bouncycastle.est.jcajce.ChannelBindingProvider
                public boolean canAccessChannelBinding(Socket socket) {
                    return false;
                }

                @Override // org.bouncycastle.est.jcajce.ChannelBindingProvider
                public byte[] getChannelBinding(Socket socket, String str) {
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
