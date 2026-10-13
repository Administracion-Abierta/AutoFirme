package org.spongycastle.cms.jcajce;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.cert.jcajce.JcaX509CertificateHolder;
import org.spongycastle.cms.CMSAttributeTableGenerator;
import org.spongycastle.cms.CMSSignatureEncryptionAlgorithmFinder;
import org.spongycastle.cms.DefaultCMSSignatureEncryptionAlgorithmFinder;
import org.spongycastle.cms.SignerInfoGenerator;
import org.spongycastle.cms.SignerInfoGeneratorBuilder;
import org.spongycastle.operator.ContentSigner;
import org.spongycastle.operator.DigestCalculatorProvider;
import org.spongycastle.operator.OperatorCreationException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/jcajce/JcaSignerInfoGeneratorBuilder.class */
public class JcaSignerInfoGeneratorBuilder {
    private SignerInfoGeneratorBuilder builder;

    public JcaSignerInfoGeneratorBuilder(DigestCalculatorProvider digestProvider) {
        this(digestProvider, new DefaultCMSSignatureEncryptionAlgorithmFinder());
    }

    public JcaSignerInfoGeneratorBuilder(DigestCalculatorProvider digestProvider, CMSSignatureEncryptionAlgorithmFinder sigEncAlgFinder) {
        this.builder = new SignerInfoGeneratorBuilder(digestProvider, sigEncAlgFinder);
    }

    public JcaSignerInfoGeneratorBuilder setDirectSignature(boolean hasNoSignedAttributes) {
        this.builder.setDirectSignature(hasNoSignedAttributes);
        return this;
    }

    public JcaSignerInfoGeneratorBuilder setSignedAttributeGenerator(CMSAttributeTableGenerator signedGen) {
        this.builder.setSignedAttributeGenerator(signedGen);
        return this;
    }

    public JcaSignerInfoGeneratorBuilder setUnsignedAttributeGenerator(CMSAttributeTableGenerator unsignedGen) {
        this.builder.setUnsignedAttributeGenerator(unsignedGen);
        return this;
    }

    public SignerInfoGenerator build(ContentSigner contentSigner, X509CertificateHolder certHolder) throws OperatorCreationException {
        return this.builder.build(contentSigner, certHolder);
    }

    public SignerInfoGenerator build(ContentSigner contentSigner, byte[] keyIdentifier) throws OperatorCreationException {
        return this.builder.build(contentSigner, keyIdentifier);
    }

    public SignerInfoGenerator build(ContentSigner contentSigner, X509Certificate certificate) throws OperatorCreationException, CertificateEncodingException {
        return build(contentSigner, new JcaX509CertificateHolder(certificate));
    }
}
