package org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.SignerIdentifier;
import org.bouncycastle.asn1.cms.SignerInfo;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.io.TeeOutputStream;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/SignerInfoGenerator.class */
public class SignerInfoGenerator {
    private final SignerIdentifier signerIdentifier;
    private final CMSAttributeTableGenerator sAttrGen;
    private final CMSAttributeTableGenerator unsAttrGen;
    private final ContentSigner signer;
    private final DigestCalculator digester;
    private final AlgorithmIdentifier digestAlgorithm;
    private final CMSSignatureEncryptionAlgorithmFinder sigEncAlgFinder;
    private byte[] calculatedDigest;
    private X509CertificateHolder certHolder;

    SignerInfoGenerator(SignerIdentifier signerIdentifier, ContentSigner contentSigner, AlgorithmIdentifier algorithmIdentifier, CMSSignatureEncryptionAlgorithmFinder cMSSignatureEncryptionAlgorithmFinder) {
        this.calculatedDigest = null;
        this.signerIdentifier = signerIdentifier;
        this.signer = contentSigner;
        this.digestAlgorithm = algorithmIdentifier;
        this.digester = null;
        this.sAttrGen = null;
        this.unsAttrGen = null;
        this.sigEncAlgFinder = cMSSignatureEncryptionAlgorithmFinder;
    }

    SignerInfoGenerator(SignerIdentifier signerIdentifier, ContentSigner contentSigner, DigestCalculator digestCalculator, CMSSignatureEncryptionAlgorithmFinder cMSSignatureEncryptionAlgorithmFinder, CMSAttributeTableGenerator cMSAttributeTableGenerator, CMSAttributeTableGenerator cMSAttributeTableGenerator2) {
        this.calculatedDigest = null;
        this.signerIdentifier = signerIdentifier;
        this.signer = contentSigner;
        this.digestAlgorithm = digestCalculator.getAlgorithmIdentifier();
        this.digester = digestCalculator;
        this.sAttrGen = cMSAttributeTableGenerator;
        this.unsAttrGen = cMSAttributeTableGenerator2;
        this.sigEncAlgFinder = cMSSignatureEncryptionAlgorithmFinder;
    }

    public SignerInfoGenerator(SignerInfoGenerator signerInfoGenerator, CMSAttributeTableGenerator cMSAttributeTableGenerator, CMSAttributeTableGenerator cMSAttributeTableGenerator2) {
        this.calculatedDigest = null;
        this.signerIdentifier = signerInfoGenerator.signerIdentifier;
        this.signer = signerInfoGenerator.signer;
        this.digestAlgorithm = signerInfoGenerator.digestAlgorithm;
        this.digester = signerInfoGenerator.digester;
        this.sigEncAlgFinder = signerInfoGenerator.sigEncAlgFinder;
        this.certHolder = signerInfoGenerator.certHolder;
        this.sAttrGen = cMSAttributeTableGenerator;
        this.unsAttrGen = cMSAttributeTableGenerator2;
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
        return this.digestAlgorithm;
    }

    public OutputStream getCalculatingOutputStream() {
        if (this.digester != null) {
            return this.sAttrGen == null ? new TeeOutputStream(this.digester.getOutputStream(), this.signer.getOutputStream()) : this.digester.getOutputStream();
        }
        return this.signer.getOutputStream();
    }

    public SignerInfo generate(ASN1ObjectIdentifier aSN1ObjectIdentifier) throws CMSException {
        AlgorithmIdentifier algorithmIdentifier;
        try {
            ASN1Set attributeSet = null;
            AlgorithmIdentifier algorithmIdentifierFindEncryptionAlgorithm = this.sigEncAlgFinder.findEncryptionAlgorithm(this.signer.getAlgorithmIdentifier());
            if (this.sAttrGen != null) {
                algorithmIdentifier = this.digester.getAlgorithmIdentifier();
                this.calculatedDigest = this.digester.getDigest();
                attributeSet = getAttributeSet(this.sAttrGen.getAttributes(Collections.unmodifiableMap(getBaseParameters(aSN1ObjectIdentifier, this.digester.getAlgorithmIdentifier(), algorithmIdentifierFindEncryptionAlgorithm, this.calculatedDigest))));
                OutputStream outputStream = this.signer.getOutputStream();
                outputStream.write(attributeSet.getEncoded("DER"));
                outputStream.close();
            } else {
                algorithmIdentifier = this.digestAlgorithm;
                if (this.digester != null) {
                    this.calculatedDigest = this.digester.getDigest();
                } else {
                    this.calculatedDigest = null;
                }
            }
            byte[] signature = this.signer.getSignature();
            ASN1Set attributeSet2 = null;
            if (this.unsAttrGen != null) {
                Map baseParameters = getBaseParameters(aSN1ObjectIdentifier, algorithmIdentifier, algorithmIdentifierFindEncryptionAlgorithm, this.calculatedDigest);
                baseParameters.put("encryptedDigest", Arrays.clone(signature));
                attributeSet2 = getAttributeSet(this.unsAttrGen.getAttributes(Collections.unmodifiableMap(baseParameters)));
            }
            if (this.sAttrGen == null && EdECObjectIdentifiers.id_Ed448.equals((ASN1Primitive) algorithmIdentifierFindEncryptionAlgorithm.getAlgorithm())) {
                algorithmIdentifier = new AlgorithmIdentifier(NISTObjectIdentifiers.id_shake256);
            }
            return new SignerInfo(this.signerIdentifier, algorithmIdentifier, attributeSet, algorithmIdentifierFindEncryptionAlgorithm, new DEROctetString(signature), attributeSet2);
        } catch (IOException e) {
            throw new CMSException("encoding error.", e);
        }
    }

    void setAssociatedCertificate(X509CertificateHolder x509CertificateHolder) {
        this.certHolder = x509CertificateHolder;
    }

    private ASN1Set getAttributeSet(AttributeTable attributeTable) {
        if (attributeTable != null) {
            return new DERSet(attributeTable.toASN1EncodableVector());
        }
        return null;
    }

    private Map getBaseParameters(ASN1ObjectIdentifier aSN1ObjectIdentifier, AlgorithmIdentifier algorithmIdentifier, AlgorithmIdentifier algorithmIdentifier2, byte[] bArr) {
        HashMap map = new HashMap();
        if (aSN1ObjectIdentifier != null) {
            map.put("contentType", aSN1ObjectIdentifier);
        }
        map.put("digestAlgID", algorithmIdentifier);
        map.put("signatureAlgID", algorithmIdentifier2);
        map.put("digest", Arrays.clone(bArr));
        return map;
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
