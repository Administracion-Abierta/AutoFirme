package org.spongycastle.cms;

import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.cms.IssuerAndSerialNumber;
import org.spongycastle.asn1.cms.SignerIdentifier;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.operator.ContentSigner;
import org.spongycastle.operator.DigestCalculatorProvider;
import org.spongycastle.operator.OperatorCreationException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/SignerInfoGeneratorBuilder.class */
public class SignerInfoGeneratorBuilder {
    private DigestCalculatorProvider digestProvider;
    private boolean directSignature;
    private CMSAttributeTableGenerator signedGen;
    private CMSAttributeTableGenerator unsignedGen;
    private CMSSignatureEncryptionAlgorithmFinder sigEncAlgFinder;

    public SignerInfoGeneratorBuilder(DigestCalculatorProvider digestProvider) {
        this(digestProvider, new DefaultCMSSignatureEncryptionAlgorithmFinder());
    }

    public SignerInfoGeneratorBuilder(DigestCalculatorProvider digestProvider, CMSSignatureEncryptionAlgorithmFinder sigEncAlgFinder) {
        this.digestProvider = digestProvider;
        this.sigEncAlgFinder = sigEncAlgFinder;
    }

    public SignerInfoGeneratorBuilder setDirectSignature(boolean hasNoSignedAttributes) {
        this.directSignature = hasNoSignedAttributes;
        return this;
    }

    public SignerInfoGeneratorBuilder setSignedAttributeGenerator(CMSAttributeTableGenerator signedGen) {
        this.signedGen = signedGen;
        return this;
    }

    public SignerInfoGeneratorBuilder setUnsignedAttributeGenerator(CMSAttributeTableGenerator unsignedGen) {
        this.unsignedGen = unsignedGen;
        return this;
    }

    public SignerInfoGenerator build(ContentSigner contentSigner, X509CertificateHolder certHolder) throws OperatorCreationException {
        SignerIdentifier sigId = new SignerIdentifier(new IssuerAndSerialNumber(certHolder.toASN1Structure()));
        SignerInfoGenerator sigInfoGen = createGenerator(contentSigner, sigId);
        sigInfoGen.setAssociatedCertificate(certHolder);
        return sigInfoGen;
    }

    public SignerInfoGenerator build(ContentSigner contentSigner, byte[] subjectKeyIdentifier) throws OperatorCreationException {
        SignerIdentifier sigId = new SignerIdentifier((ASN1OctetString) new DEROctetString(subjectKeyIdentifier));
        return createGenerator(contentSigner, sigId);
    }

    private SignerInfoGenerator createGenerator(ContentSigner contentSigner, SignerIdentifier sigId) throws OperatorCreationException {
        if (this.directSignature) {
            return new SignerInfoGenerator(sigId, contentSigner, this.digestProvider, this.sigEncAlgFinder, true);
        }
        if (this.signedGen != null || this.unsignedGen != null) {
            if (this.signedGen == null) {
                this.signedGen = new DefaultSignedAttributeTableGenerator();
            }
            return new SignerInfoGenerator(sigId, contentSigner, this.digestProvider, this.sigEncAlgFinder, this.signedGen, this.unsignedGen);
        }
        return new SignerInfoGenerator(sigId, contentSigner, this.digestProvider, this.sigEncAlgFinder);
    }
}
