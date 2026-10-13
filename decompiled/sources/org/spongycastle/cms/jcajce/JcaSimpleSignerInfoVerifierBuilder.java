package org.spongycastle.cms.jcajce;

import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.cms.DefaultCMSSignatureAlgorithmNameGenerator;
import org.spongycastle.cms.SignerInformationVerifier;
import org.spongycastle.operator.ContentVerifierProvider;
import org.spongycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.spongycastle.operator.DigestCalculatorProvider;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.spongycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/jcajce/JcaSimpleSignerInfoVerifierBuilder.class */
public class JcaSimpleSignerInfoVerifierBuilder {
    private Helper helper = new Helper();

    public JcaSimpleSignerInfoVerifierBuilder setProvider(Provider provider) {
        this.helper = new ProviderHelper(provider);
        return this;
    }

    public JcaSimpleSignerInfoVerifierBuilder setProvider(String providerName) {
        this.helper = new NamedHelper(providerName);
        return this;
    }

    public SignerInformationVerifier build(X509CertificateHolder certHolder) throws OperatorCreationException, CertificateException {
        return new SignerInformationVerifier(new DefaultCMSSignatureAlgorithmNameGenerator(), new DefaultSignatureAlgorithmIdentifierFinder(), this.helper.createContentVerifierProvider(certHolder), this.helper.createDigestCalculatorProvider());
    }

    public SignerInformationVerifier build(X509Certificate certificate) throws OperatorCreationException {
        return new SignerInformationVerifier(new DefaultCMSSignatureAlgorithmNameGenerator(), new DefaultSignatureAlgorithmIdentifierFinder(), this.helper.createContentVerifierProvider(certificate), this.helper.createDigestCalculatorProvider());
    }

    public SignerInformationVerifier build(PublicKey pubKey) throws OperatorCreationException {
        return new SignerInformationVerifier(new DefaultCMSSignatureAlgorithmNameGenerator(), new DefaultSignatureAlgorithmIdentifierFinder(), this.helper.createContentVerifierProvider(pubKey), this.helper.createDigestCalculatorProvider());
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/jcajce/JcaSimpleSignerInfoVerifierBuilder$Helper.class */
    private class Helper {
        private Helper() {
        }

        ContentVerifierProvider createContentVerifierProvider(PublicKey publicKey) throws OperatorCreationException {
            return new JcaContentVerifierProviderBuilder().build(publicKey);
        }

        ContentVerifierProvider createContentVerifierProvider(X509Certificate certificate) throws OperatorCreationException {
            return new JcaContentVerifierProviderBuilder().build(certificate);
        }

        ContentVerifierProvider createContentVerifierProvider(X509CertificateHolder certHolder) throws OperatorCreationException, CertificateException {
            return new JcaContentVerifierProviderBuilder().build(certHolder);
        }

        DigestCalculatorProvider createDigestCalculatorProvider() throws OperatorCreationException {
            return new JcaDigestCalculatorProviderBuilder().build();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/jcajce/JcaSimpleSignerInfoVerifierBuilder$NamedHelper.class */
    private class NamedHelper extends Helper {
        private final String providerName;

        public NamedHelper(String providerName) {
            super();
            this.providerName = providerName;
        }

        @Override // org.spongycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder.Helper
        ContentVerifierProvider createContentVerifierProvider(PublicKey publicKey) throws OperatorCreationException {
            return new JcaContentVerifierProviderBuilder().setProvider(this.providerName).build(publicKey);
        }

        @Override // org.spongycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder.Helper
        ContentVerifierProvider createContentVerifierProvider(X509Certificate certificate) throws OperatorCreationException {
            return new JcaContentVerifierProviderBuilder().setProvider(this.providerName).build(certificate);
        }

        @Override // org.spongycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder.Helper
        DigestCalculatorProvider createDigestCalculatorProvider() throws OperatorCreationException {
            return new JcaDigestCalculatorProviderBuilder().setProvider(this.providerName).build();
        }

        @Override // org.spongycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder.Helper
        ContentVerifierProvider createContentVerifierProvider(X509CertificateHolder certHolder) throws OperatorCreationException, CertificateException {
            return new JcaContentVerifierProviderBuilder().setProvider(this.providerName).build(certHolder);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/jcajce/JcaSimpleSignerInfoVerifierBuilder$ProviderHelper.class */
    private class ProviderHelper extends Helper {
        private final Provider provider;

        public ProviderHelper(Provider provider) {
            super();
            this.provider = provider;
        }

        @Override // org.spongycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder.Helper
        ContentVerifierProvider createContentVerifierProvider(PublicKey publicKey) throws OperatorCreationException {
            return new JcaContentVerifierProviderBuilder().setProvider(this.provider).build(publicKey);
        }

        @Override // org.spongycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder.Helper
        ContentVerifierProvider createContentVerifierProvider(X509Certificate certificate) throws OperatorCreationException {
            return new JcaContentVerifierProviderBuilder().setProvider(this.provider).build(certificate);
        }

        @Override // org.spongycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder.Helper
        DigestCalculatorProvider createDigestCalculatorProvider() throws OperatorCreationException {
            return new JcaDigestCalculatorProviderBuilder().setProvider(this.provider).build();
        }

        @Override // org.spongycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder.Helper
        ContentVerifierProvider createContentVerifierProvider(X509CertificateHolder certHolder) throws OperatorCreationException, CertificateException {
            return new JcaContentVerifierProviderBuilder().setProvider(this.provider).build(certHolder);
        }
    }
}
