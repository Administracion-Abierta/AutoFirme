package org.spongycastle.x509;

import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import org.spongycastle.util.Selector;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/x509/X509CertStoreSelector.class */
public class X509CertStoreSelector extends X509CertSelector implements Selector {
    @Override // org.spongycastle.util.Selector
    public boolean match(Object obj) {
        if (!(obj instanceof X509Certificate)) {
            return false;
        }
        X509Certificate other = (X509Certificate) obj;
        return super.match((Certificate) other);
    }

    @Override // java.security.cert.X509CertSelector, java.security.cert.CertSelector
    public boolean match(Certificate cert) {
        return match((Object) cert);
    }

    @Override // java.security.cert.X509CertSelector, java.security.cert.CertSelector, org.spongycastle.util.Selector
    public Object clone() {
        X509CertStoreSelector selector = (X509CertStoreSelector) super.clone();
        return selector;
    }

    public static X509CertStoreSelector getInstance(X509CertSelector selector) {
        if (selector == null) {
            throw new IllegalArgumentException("cannot create from null selector");
        }
        X509CertStoreSelector cs = new X509CertStoreSelector();
        cs.setAuthorityKeyIdentifier(selector.getAuthorityKeyIdentifier());
        cs.setBasicConstraints(selector.getBasicConstraints());
        cs.setCertificate(selector.getCertificate());
        cs.setCertificateValid(selector.getCertificateValid());
        cs.setMatchAllSubjectAltNames(selector.getMatchAllSubjectAltNames());
        try {
            cs.setPathToNames(selector.getPathToNames());
            cs.setExtendedKeyUsage(selector.getExtendedKeyUsage());
            cs.setNameConstraints(selector.getNameConstraints());
            cs.setPolicy(selector.getPolicy());
            cs.setSubjectPublicKeyAlgID(selector.getSubjectPublicKeyAlgID());
            cs.setSubjectAlternativeNames(selector.getSubjectAlternativeNames());
            cs.setIssuer(selector.getIssuer());
            cs.setKeyUsage(selector.getKeyUsage());
            cs.setPrivateKeyValid(selector.getPrivateKeyValid());
            cs.setSerialNumber(selector.getSerialNumber());
            cs.setSubject(selector.getSubject());
            cs.setSubjectKeyIdentifier(selector.getSubjectKeyIdentifier());
            cs.setSubjectPublicKey(selector.getSubjectPublicKey());
            return cs;
        } catch (IOException e) {
            throw new IllegalArgumentException("error in passed in selector: " + e);
        }
    }
}
