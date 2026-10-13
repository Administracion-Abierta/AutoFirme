package org.spongycastle.cms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSet;
import org.spongycastle.asn1.cms.AttributeTable;
import org.spongycastle.asn1.cms.SignerIdentifier;
import org.spongycastle.asn1.cms.SignerInfo;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.operator.ContentSigner;
import org.spongycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.spongycastle.operator.DigestAlgorithmIdentifierFinder;
import org.spongycastle.operator.DigestCalculator;
import org.spongycastle.operator.DigestCalculatorProvider;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.io.TeeOutputStream;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/SignerInfoGenerator.class */
public class SignerInfoGenerator {
    private final SignerIdentifier signerIdentifier;
    private final CMSAttributeTableGenerator sAttrGen;
    private final CMSAttributeTableGenerator unsAttrGen;
    private final ContentSigner signer;
    private final DigestCalculator digester;
    private final DigestAlgorithmIdentifierFinder digAlgFinder;
    private final CMSSignatureEncryptionAlgorithmFinder sigEncAlgFinder;
    private byte[] calculatedDigest;
    private X509CertificateHolder certHolder;

    SignerInfoGenerator(SignerIdentifier signerIdentifier, ContentSigner signer, DigestCalculatorProvider digesterProvider, CMSSignatureEncryptionAlgorithmFinder sigEncAlgFinder) throws OperatorCreationException {
        this(signerIdentifier, signer, digesterProvider, sigEncAlgFinder, false);
    }

    SignerInfoGenerator(SignerIdentifier signerIdentifier, ContentSigner signer, DigestCalculatorProvider digesterProvider, CMSSignatureEncryptionAlgorithmFinder sigEncAlgFinder, boolean isDirectSignature) throws OperatorCreationException {
        this.digAlgFinder = new DefaultDigestAlgorithmIdentifierFinder();
        this.calculatedDigest = null;
        this.signerIdentifier = signerIdentifier;
        this.signer = signer;
        if (digesterProvider != null) {
            this.digester = digesterProvider.get(this.digAlgFinder.find(signer.getAlgorithmIdentifier()));
        } else {
            this.digester = null;
        }
        if (isDirectSignature) {
            this.sAttrGen = null;
            this.unsAttrGen = null;
        } else {
            this.sAttrGen = new DefaultSignedAttributeTableGenerator();
            this.unsAttrGen = null;
        }
        this.sigEncAlgFinder = sigEncAlgFinder;
    }

    public SignerInfoGenerator(SignerInfoGenerator original, CMSAttributeTableGenerator sAttrGen, CMSAttributeTableGenerator unsAttrGen) {
        this.digAlgFinder = new DefaultDigestAlgorithmIdentifierFinder();
        this.calculatedDigest = null;
        this.signerIdentifier = original.signerIdentifier;
        this.signer = original.signer;
        this.digester = original.digester;
        this.sigEncAlgFinder = original.sigEncAlgFinder;
        this.sAttrGen = sAttrGen;
        this.unsAttrGen = unsAttrGen;
    }

    SignerInfoGenerator(SignerIdentifier signerIdentifier, ContentSigner signer, DigestCalculatorProvider digesterProvider, CMSSignatureEncryptionAlgorithmFinder sigEncAlgFinder, CMSAttributeTableGenerator sAttrGen, CMSAttributeTableGenerator unsAttrGen) throws OperatorCreationException {
        this.digAlgFinder = new DefaultDigestAlgorithmIdentifierFinder();
        this.calculatedDigest = null;
        this.signerIdentifier = signerIdentifier;
        this.signer = signer;
        if (digesterProvider != null) {
            this.digester = digesterProvider.get(this.digAlgFinder.find(signer.getAlgorithmIdentifier()));
        } else {
            this.digester = null;
        }
        this.sAttrGen = sAttrGen;
        this.unsAttrGen = unsAttrGen;
        this.sigEncAlgFinder = sigEncAlgFinder;
    }

    public SignerIdentifier getSID() {
        return this.signerIdentifier;
    }

    public int getGeneratedVersion() {
        return this.signerIdentifier.isTagged() ? 3 : 1;
    }

    public boolean hasAssociatedCertificate() {
        return this.certHolder != null;
    }

    public X509CertificateHolder getAssociatedCertificate() {
        return this.certHolder;
    }

    public AlgorithmIdentifier getDigestAlgorithm() {
        if (this.digester != null) {
            return this.digester.getAlgorithmIdentifier();
        }
        return this.digAlgFinder.find(this.signer.getAlgorithmIdentifier());
    }

    public OutputStream getCalculatingOutputStream() {
        if (this.digester != null) {
            if (this.sAttrGen == null) {
                return new TeeOutputStream(this.digester.getOutputStream(), this.signer.getOutputStream());
            }
            return this.digester.getOutputStream();
        }
        return this.signer.getOutputStream();
    }

    public SignerInfo generate(ASN1ObjectIdentifier contentType) throws CMSException {
        AlgorithmIdentifier digestAlg;
        try {
            ASN1Set signedAttr = null;
            AlgorithmIdentifier digestEncryptionAlgorithm = this.sigEncAlgFinder.findEncryptionAlgorithm(this.signer.getAlgorithmIdentifier());
            if (this.sAttrGen != null) {
                digestAlg = this.digester.getAlgorithmIdentifier();
                this.calculatedDigest = this.digester.getDigest();
                AttributeTable signed = this.sAttrGen.getAttributes(Collections.unmodifiableMap(getBaseParameters(contentType, this.digester.getAlgorithmIdentifier(), digestEncryptionAlgorithm, this.calculatedDigest)));
                signedAttr = getAttributeSet(signed);
                OutputStream sOut = this.signer.getOutputStream();
                sOut.write(signedAttr.getEncoded("DER"));
                sOut.close();
            } else if (this.digester != null) {
                digestAlg = this.digester.getAlgorithmIdentifier();
                this.calculatedDigest = this.digester.getDigest();
            } else {
                digestAlg = this.digAlgFinder.find(this.signer.getAlgorithmIdentifier());
                this.calculatedDigest = null;
            }
            byte[] sigBytes = this.signer.getSignature();
            ASN1Set unsignedAttr = null;
            if (this.unsAttrGen != null) {
                Map parameters = getBaseParameters(contentType, digestAlg, digestEncryptionAlgorithm, this.calculatedDigest);
                parameters.put("encryptedDigest", Arrays.clone(sigBytes));
                AttributeTable unsigned = this.unsAttrGen.getAttributes(Collections.unmodifiableMap(parameters));
                unsignedAttr = getAttributeSet(unsigned);
            }
            return new SignerInfo(this.signerIdentifier, digestAlg, signedAttr, digestEncryptionAlgorithm, new DEROctetString(sigBytes), unsignedAttr);
        } catch (IOException e) {
            throw new CMSException("encoding error.", e);
        }
    }

    void setAssociatedCertificate(X509CertificateHolder certHolder) {
        this.certHolder = certHolder;
    }

    private ASN1Set getAttributeSet(AttributeTable attr) {
        if (attr != null) {
            return new DERSet(attr.toASN1EncodableVector());
        }
        return null;
    }

    private Map getBaseParameters(ASN1ObjectIdentifier contentType, AlgorithmIdentifier digAlgId, AlgorithmIdentifier sigAlgId, byte[] hash) {
        Map param = new HashMap();
        if (contentType != null) {
            param.put("contentType", contentType);
        }
        param.put("digestAlgID", digAlgId);
        param.put("signatureAlgID", sigAlgId);
        param.put("digest", Arrays.clone(hash));
        return param;
    }

    public byte[] getCalculatedDigest() {
        if (this.calculatedDigest != null) {
            return Arrays.clone(this.calculatedDigest);
        }
        return null;
    }

    public CMSAttributeTableGenerator getSignedAttributeTableGenerator() {
        return this.sAttrGen;
    }

    public CMSAttributeTableGenerator getUnsignedAttributeTableGenerator() {
        return this.unsAttrGen;
    }
}
