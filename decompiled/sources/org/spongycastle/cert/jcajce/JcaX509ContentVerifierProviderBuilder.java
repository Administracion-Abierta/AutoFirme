package org.spongycastle.cert.jcajce;

import java.security.Provider;
import java.security.cert.CertificateException;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.cert.X509ContentVerifierProviderBuilder;
import org.spongycastle.operator.ContentVerifierProvider;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.operator.jcajce.JcaContentVerifierProviderBuilder;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/jcajce/JcaX509ContentVerifierProviderBuilder.class */
public class JcaX509ContentVerifierProviderBuilder implements X509ContentVerifierProviderBuilder {
    private JcaContentVerifierProviderBuilder builder = new JcaContentVerifierProviderBuilder();

    public JcaX509ContentVerifierProviderBuilder setProvider(Provider provider) {
        this.builder.setProvider(provider);
        return this;
    }

    public JcaX509ContentVerifierProviderBuilder setProvider(String providerName) {
        this.builder.setProvider(providerName);
        return this;
    }

    @Override // org.spongycastle.cert.X509ContentVerifierProviderBuilder
    public ContentVerifierProvider build(SubjectPublicKeyInfo validatingKeyInfo) throws OperatorCreationException {
        return this.builder.build(validatingKeyInfo);
    }

    @Override // org.spongycastle.cert.X509ContentVerifierProviderBuilder
    public ContentVerifierProvider build(X509CertificateHolder validatingKeyInfo) throws OperatorCreationException {
        try {
            return this.builder.build(validatingKeyInfo);
        } catch (CertificateException e) {
            throw new OperatorCreationException("Unable to process certificate: " + e.getMessage(), e);
        }
    }
}
