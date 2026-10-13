package org.spongycastle.cms.jcajce;

import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import org.spongycastle.asn1.cms.AttributeTable;
import org.spongycastle.cert.jcajce.JcaX509CertificateHolder;
import org.spongycastle.cms.CMSAttributeTableGenerator;
import org.spongycastle.cms.DefaultSignedAttributeTableGenerator;
import org.spongycastle.cms.SignerInfoGenerator;
import org.spongycastle.cms.SignerInfoGeneratorBuilder;
import org.spongycastle.operator.ContentSigner;
import org.spongycastle.operator.DigestCalculatorProvider;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.operator.jcajce.JcaContentSignerBuilder;
import org.spongycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/jcajce/JcaSimpleSignerInfoGeneratorBuilder.class */
public class JcaSimpleSignerInfoGeneratorBuilder {
    private Helper helper = new Helper();
    private boolean hasNoSignedAttributes;
    private CMSAttributeTableGenerator signedGen;
    private CMSAttributeTableGenerator unsignedGen;

    public JcaSimpleSignerInfoGeneratorBuilder setProvider(String providerName) throws OperatorCreationException {
        this.helper = new NamedHelper(providerName);
        return this;
    }

    public JcaSimpleSignerInfoGeneratorBuilder setProvider(Provider provider) throws OperatorCreationException {
        this.helper = new ProviderHelper(provider);
        return this;
    }

    public JcaSimpleSignerInfoGeneratorBuilder setDirectSignature(boolean hasNoSignedAttributes) {
        this.hasNoSignedAttributes = hasNoSignedAttributes;
        return this;
    }

    public JcaSimpleSignerInfoGeneratorBuilder setSignedAttributeGenerator(CMSAttributeTableGenerator signedGen) {
        this.signedGen = signedGen;
        return this;
    }

    public JcaSimpleSignerInfoGeneratorBuilder setSignedAttributeGenerator(AttributeTable attrTable) {
        this.signedGen = new DefaultSignedAttributeTableGenerator(attrTable);
        return this;
    }

    public JcaSimpleSignerInfoGeneratorBuilder setUnsignedAttributeGenerator(CMSAttributeTableGenerator unsignedGen) {
        this.unsignedGen = unsignedGen;
        return this;
    }

    public SignerInfoGenerator build(String algorithmName, PrivateKey privateKey, X509Certificate certificate) throws OperatorCreationException, CertificateEncodingException {
        ContentSigner contentSigner = this.helper.createContentSigner(algorithmName, privateKey);
        return configureAndBuild().build(contentSigner, new JcaX509CertificateHolder(certificate));
    }

    public SignerInfoGenerator build(String algorithmName, PrivateKey privateKey, byte[] keyIdentifier) throws OperatorCreationException, CertificateEncodingException {
        ContentSigner contentSigner = this.helper.createContentSigner(algorithmName, privateKey);
        return configureAndBuild().build(contentSigner, keyIdentifier);
    }

    private SignerInfoGeneratorBuilder configureAndBuild() throws OperatorCreationException {
        SignerInfoGeneratorBuilder infoGeneratorBuilder = new SignerInfoGeneratorBuilder(this.helper.createDigestCalculatorProvider());
        infoGeneratorBuilder.setDirectSignature(this.hasNoSignedAttributes);
        infoGeneratorBuilder.setSignedAttributeGenerator(this.signedGen);
        infoGeneratorBuilder.setUnsignedAttributeGenerator(this.unsignedGen);
        return infoGeneratorBuilder;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/jcajce/JcaSimpleSignerInfoGeneratorBuilder$Helper.class */
    private class Helper {
        private Helper() {
        }

        ContentSigner createContentSigner(String algorithm, PrivateKey privateKey) throws OperatorCreationException {
            return new JcaContentSignerBuilder(algorithm).build(privateKey);
        }

        DigestCalculatorProvider createDigestCalculatorProvider() throws OperatorCreationException {
            return new JcaDigestCalculatorProviderBuilder().build();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/jcajce/JcaSimpleSignerInfoGeneratorBuilder$NamedHelper.class */
    private class NamedHelper extends Helper {
        private final String providerName;

        public NamedHelper(String providerName) {
            super();
            this.providerName = providerName;
        }

        @Override // org.spongycastle.cms.jcajce.JcaSimpleSignerInfoGeneratorBuilder.Helper
        ContentSigner createContentSigner(String algorithm, PrivateKey privateKey) throws OperatorCreationException {
            return new JcaContentSignerBuilder(algorithm).setProvider(this.providerName).build(privateKey);
        }

        @Override // org.spongycastle.cms.jcajce.JcaSimpleSignerInfoGeneratorBuilder.Helper
        DigestCalculatorProvider createDigestCalculatorProvider() throws OperatorCreationException {
            return new JcaDigestCalculatorProviderBuilder().setProvider(this.providerName).build();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/jcajce/JcaSimpleSignerInfoGeneratorBuilder$ProviderHelper.class */
    private class ProviderHelper extends Helper {
        private final Provider provider;

        public ProviderHelper(Provider provider) {
            super();
            this.provider = provider;
        }

        @Override // org.spongycastle.cms.jcajce.JcaSimpleSignerInfoGeneratorBuilder.Helper
        ContentSigner createContentSigner(String algorithm, PrivateKey privateKey) throws OperatorCreationException {
            return new JcaContentSignerBuilder(algorithm).setProvider(this.provider).build(privateKey);
        }

        @Override // org.spongycastle.cms.jcajce.JcaSimpleSignerInfoGeneratorBuilder.Helper
        DigestCalculatorProvider createDigestCalculatorProvider() throws OperatorCreationException {
            return new JcaDigestCalculatorProviderBuilder().setProvider(this.provider).build();
        }
    }
}
