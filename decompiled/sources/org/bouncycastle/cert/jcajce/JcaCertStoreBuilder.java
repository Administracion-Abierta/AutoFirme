package org.bouncycastle.cert.jcajce;

import java.security.GeneralSecurityException;
import java.security.Provider;
import java.security.cert.CRLException;
import java.security.cert.CertStore;
import java.security.cert.CertificateException;
import java.security.cert.CollectionCertStoreParameters;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.Store;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/jcajce/JcaCertStoreBuilder.class */
public class JcaCertStoreBuilder {
    private Object provider;
    private List certs = new ArrayList();
    private List crls = new ArrayList();
    private JcaX509CertificateConverter certificateConverter = new JcaX509CertificateConverter();
    private JcaX509CRLConverter crlConverter = new JcaX509CRLConverter();
    private String type = "Collection";

    public JcaCertStoreBuilder addCertificates(Store store) {
        this.certs.addAll(store.getMatches(null));
        return this;
    }

    public JcaCertStoreBuilder addCertificate(X509CertificateHolder x509CertificateHolder) {
        this.certs.add(x509CertificateHolder);
        return this;
    }

    public JcaCertStoreBuilder addCRLs(Store store) {
        this.crls.addAll(store.getMatches(null));
        return this;
    }

    public JcaCertStoreBuilder addCRL(X509CRLHolder x509CRLHolder) {
        this.crls.add(x509CRLHolder);
        return this;
    }

    public JcaCertStoreBuilder setProvider(String str) {
        this.certificateConverter.setProvider(str);
        this.crlConverter.setProvider(str);
        this.provider = str;
        return this;
    }

    public JcaCertStoreBuilder setProvider(Provider provider) {
        this.certificateConverter.setProvider(provider);
        this.crlConverter.setProvider(provider);
        this.provider = provider;
        return this;
    }

    public JcaCertStoreBuilder setType(String str) {
        this.type = str;
        return this;
    }

    public CertStore build() throws GeneralSecurityException {
        CollectionCertStoreParameters collectionCertStoreParametersConvertHolders = convertHolders(this.certificateConverter, this.crlConverter);
        if (this.provider instanceof String) {
            return CertStore.getInstance(this.type, collectionCertStoreParametersConvertHolders, (String) this.provider);
        }
        return this.provider instanceof Provider ? CertStore.getInstance(this.type, collectionCertStoreParametersConvertHolders, (Provider) this.provider) : CertStore.getInstance(this.type, collectionCertStoreParametersConvertHolders);
    }

    private CollectionCertStoreParameters convertHolders(JcaX509CertificateConverter jcaX509CertificateConverter, JcaX509CRLConverter jcaX509CRLConverter) throws CertificateException, CRLException {
        ArrayList arrayList = new ArrayList(this.certs.size() + this.crls.size());
        Iterator it = this.certs.iterator();
        while (it.hasNext()) {
            arrayList.add(jcaX509CertificateConverter.getCertificate((X509CertificateHolder) it.next()));
        }
        Iterator it2 = this.crls.iterator();
        while (it2.hasNext()) {
            arrayList.add(jcaX509CRLConverter.getCRL((X509CRLHolder) it2.next()));
        }
        return new CollectionCertStoreParameters(arrayList);
    }
}
