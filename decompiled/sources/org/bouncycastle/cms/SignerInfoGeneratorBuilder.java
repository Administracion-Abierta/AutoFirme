package org.bouncycastle.cms;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.SignerIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.ExtendedContentSigner;
import org.bouncycastle.operator.OperatorCreationException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/SignerInfoGeneratorBuilder.class */
public class SignerInfoGeneratorBuilder {
    private final DigestAlgorithmIdentifierFinder digAlgFinder;
    private DigestCalculatorProvider digestProvider;
    private boolean directSignature;
    private CMSAttributeTableGenerator signedGen;
    private CMSAttributeTableGenerator unsignedGen;
    private CMSSignatureEncryptionAlgorithmFinder sigEncAlgFinder;
    private AlgorithmIdentifier contentDigest;

    public SignerInfoGeneratorBuilder(DigestCalculatorProvider digestCalculatorProvider) {
        this(digestCalculatorProvider, new DefaultCMSSignatureEncryptionAlgorithmFinder());
    }

    public SignerInfoGeneratorBuilder(DigestCalculatorProvider digestCalculatorProvider, CMSSignatureEncryptionAlgorithmFinder cMSSignatureEncryptionAlgorithmFinder) {
        this.digAlgFinder = new DefaultDigestAlgorithmIdentifierFinder();
        this.digestProvider = digestCalculatorProvider;
        this.sigEncAlgFinder = cMSSignatureEncryptionAlgorithmFinder;
    }

    public SignerInfoGeneratorBuilder setDirectSignature(boolean z) {
        this.directSignature = z;
        return this;
    }

    public SignerInfoGeneratorBuilder setContentDigest(AlgorithmIdentifier algorithmIdentifier) {
        this.contentDigest = algorithmIdentifier;
        return this;
    }

    public SignerInfoGeneratorBuilder setSignedAttributeGenerator(CMSAttributeTableGenerator cMSAttributeTableGenerator) {
        this.signedGen = cMSAttributeTableGenerator;
        return this;
    }

    public SignerInfoGeneratorBuilder setUnsignedAttributeGenerator(CMSAttributeTableGenerator cMSAttributeTableGenerator) {
        this.unsignedGen = cMSAttributeTableGenerator;
        return this;
    }

    public SignerInfoGenerator build(ContentSigner contentSigner, X509CertificateHolder x509CertificateHolder) throws OperatorCreationException {
        SignerInfoGenerator signerInfoGeneratorCreateGenerator = createGenerator(contentSigner, new SignerIdentifier(new IssuerAndSerialNumber(x509CertificateHolder.toASN1Structure())));
        signerInfoGeneratorCreateGenerator.setAssociatedCertificate(x509CertificateHolder);
        return signerInfoGeneratorCreateGenerator;
    }

    public SignerInfoGenerator build(ContentSigner contentSigner, byte[] bArr) throws OperatorCreationException {
        return createGenerator(contentSigner, new SignerIdentifier((ASN1OctetString) new DEROctetString(bArr)));
    }

    private SignerInfoGenerator createGenerator(ContentSigner contentSigner, SignerIdentifier signerIdentifier) throws OperatorCreationException {
        DigestCalculator digestCalculator;
        if (this.contentDigest != null) {
            digestCalculator = this.digestProvider.get(this.contentDigest);
        } else {
            AlgorithmIdentifier algorithmIdentifierFind = null;
            if (contentSigner instanceof ExtendedContentSigner) {
                algorithmIdentifierFind = ((ExtendedContentSigner) contentSigner).getDigestAlgorithmIdentifier();
            }
            if (algorithmIdentifierFind == null) {
                algorithmIdentifierFind = this.digAlgFinder.find(contentSigner.getAlgorithmIdentifier());
            }
            if (algorithmIdentifierFind == null) {
                throw new OperatorCreationException("no digest algorithm specified for signature algorithm");
            }
            digestCalculator = this.digestProvider.get(algorithmIdentifierFind);
        }
        if (this.directSignature) {
            return new SignerInfoGenerator(signerIdentifier, contentSigner, digestCalculator.getAlgorithmIdentifier(), this.sigEncAlgFinder);
        }
        if (this.signedGen == null && this.unsignedGen == null) {
            return new SignerInfoGenerator(signerIdentifier, contentSigner, digestCalculator, this.sigEncAlgFinder, new DefaultSignedAttributeTableGenerator(), null);
        }
        if (this.signedGen == null) {
            this.signedGen = new DefaultSignedAttributeTableGenerator();
        }
        return new SignerInfoGenerator(signerIdentifier, contentSigner, digestCalculator, this.sigEncAlgFinder, this.signedGen, this.unsignedGen);
    }
}
