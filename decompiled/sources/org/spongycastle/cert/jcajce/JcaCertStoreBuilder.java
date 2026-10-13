package org.spongycastle.cert.jcajce;

import java.security.GeneralSecurityException;
import java.security.Provider;
import java.security.cert.CRLException;
import java.security.cert.CertStore;
import java.security.cert.CertificateException;
import java.security.cert.CollectionCertStoreParameters;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.spongycastle.cert.X509CRLHolder;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.util.Store;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/jcajce/JcaCertStoreBuilder.class */
public class JcaCertStoreBuilder {
    private Object provider;
    private List certs = new ArrayList();
    private List crls = new ArrayList();
    private JcaX509CertificateConverter certificateConverter = new JcaX509CertificateConverter();
    private JcaX509CRLConverter crlConverter = new JcaX509CRLConverter();
    private String type = "Collection";

    public JcaCertStoreBuilder addCertificates(Store certStore) {
        this.certs.addAll(certStore.getMatches(null));
        return this;
    }

    public JcaCertStoreBuilder addCertificate(X509CertificateHolder cert) {
        this.certs.add(cert);
        return this;
    }

    public JcaCertStoreBuilder addCRLs(Store crlStore) {
        this.crls.addAll(crlStore.getMatches(null));
        return this;
    }

    public JcaCertStoreBuilder addCRL(X509CRLHolder crl) {
        this.crls.add(crl);
        return this;
    }

    public JcaCertStoreBuilder setProvider(String providerName) {
        this.certificateConverter.setProvider(providerName);
        this.crlConverter.setProvider(providerName);
        this.provider = providerName;
        return this;
    }

    public JcaCertStoreBuilder setProvider(Provider provider) {
        this.certificateConverter.setProvider(provider);
        this.crlConverter.setProvider(provider);
        this.provider = provider;
        return this;
    }

    public JcaCertStoreBuilder setType(String type) {
        this.type = type;
        return this;
    }

    public CertStore build() throws GeneralSecurityException {
        CollectionCertStoreParameters params = convertHolders(this.certificateConverter, this.crlConverter);
        if (this.provider instanceof String) {
            return CertStore.getInstance(this.type, params, (String) this.provider);
        }
        if (this.provider instanceof Provider) {
            return CertStore.getInstance(this.type, params, (Provider) this.provider);
        }
        return CertStore.getInstance(this.type, params);
    }

    private CollectionCertStoreParameters convertHolders(JcaX509CertificateConverter certificateConverter, JcaX509CRLConverter crlConverter) throws CertificateException, CRLException {
        List jcaObjs = new ArrayList(this.certs.size() + this.crls.size());
        Iterator it = this.certs.iterator();
        while (it.hasNext()) {
            jcaObjs.add(certificateConverter.getCertificate((X509CertificateHolder) it.next()));
        }
        Iterator it2 = this.crls.iterator();
        while (it2.hasNext()) {
            jcaObjs.add(crlConverter.getCRL((X509CRLHolder) it2.next()));
        }
        return new CollectionCertStoreParameters(jcaObjs);
    }
}
