package org.spongycastle.cms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Encoding;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.DERNull;
import org.spongycastle.asn1.DERSet;
import org.spongycastle.asn1.cms.Attribute;
import org.spongycastle.asn1.cms.AttributeTable;
import org.spongycastle.asn1.cms.CMSAlgorithmProtection;
import org.spongycastle.asn1.cms.CMSAttributes;
import org.spongycastle.asn1.cms.IssuerAndSerialNumber;
import org.spongycastle.asn1.cms.SignerIdentifier;
import org.spongycastle.asn1.cms.SignerInfo;
import org.spongycastle.asn1.cms.Time;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.DigestInfo;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.operator.ContentVerifier;
import org.spongycastle.operator.DigestCalculator;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.operator.RawContentVerifier;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.io.TeeOutputStream;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/SignerInformation.class */
public class SignerInformation {
    private final SignerId sid;
    private final CMSProcessable content;
    private final byte[] signature;
    private final ASN1ObjectIdentifier contentType;
    private final boolean isCounterSignature;
    private AttributeTable signedAttributeValues;
    private AttributeTable unsignedAttributeValues;
    private byte[] resultDigest;
    protected final SignerInfo info;
    protected final AlgorithmIdentifier digestAlgorithm;
    protected final AlgorithmIdentifier encryptionAlgorithm;
    protected final ASN1Set signedAttributeSet;
    protected final ASN1Set unsignedAttributeSet;

    SignerInformation(SignerInfo info, ASN1ObjectIdentifier contentType, CMSProcessable content, byte[] resultDigest) {
        this.info = info;
        this.contentType = contentType;
        this.isCounterSignature = contentType == null;
        SignerIdentifier s = info.getSID();
        if (s.isTagged()) {
            ASN1OctetString octs = ASN1OctetString.getInstance(s.getId());
            this.sid = new SignerId(octs.getOctets());
        } else {
            IssuerAndSerialNumber iAnds = IssuerAndSerialNumber.getInstance(s.getId());
            this.sid = new SignerId(iAnds.getName(), iAnds.getSerialNumber().getValue());
        }
        this.digestAlgorithm = info.getDigestAlgorithm();
        this.signedAttributeSet = info.getAuthenticatedAttributes();
        this.unsignedAttributeSet = info.getUnauthenticatedAttributes();
        this.encryptionAlgorithm = info.getDigestEncryptionAlgorithm();
        this.signature = info.getEncryptedDigest().getOctets();
        this.content = content;
        this.resultDigest = resultDigest;
    }

    protected SignerInformation(SignerInformation baseInfo) {
        this.info = baseInfo.info;
        this.contentType = baseInfo.contentType;
        this.isCounterSignature = baseInfo.isCounterSignature();
        this.sid = baseInfo.getSID();
        this.digestAlgorithm = this.info.getDigestAlgorithm();
        this.signedAttributeSet = this.info.getAuthenticatedAttributes();
        this.unsignedAttributeSet = this.info.getUnauthenticatedAttributes();
        this.encryptionAlgorithm = this.info.getDigestEncryptionAlgorithm();
        this.signature = this.info.getEncryptedDigest().getOctets();
        this.content = baseInfo.content;
        this.resultDigest = baseInfo.resultDigest;
    }

    public boolean isCounterSignature() {
        return this.isCounterSignature;
    }

    public ASN1ObjectIdentifier getContentType() {
        return this.contentType;
    }

    private byte[] encodeObj(ASN1Encodable obj) throws IOException {
        if (obj != null) {
            return obj.toASN1Primitive().getEncoded();
        }
        return null;
    }

    public SignerId getSID() {
        return this.sid;
    }

    public int getVersion() {
        return this.info.getVersion().getValue().intValue();
    }

    public AlgorithmIdentifier getDigestAlgorithmID() {
        return this.digestAlgorithm;
    }

    public String getDigestAlgOID() {
        return this.digestAlgorithm.getAlgorithm().getId();
    }

    public byte[] getDigestAlgParams() {
        try {
            return encodeObj(this.digestAlgorithm.getParameters());
        } catch (Exception e) {
            throw new RuntimeException("exception getting digest parameters " + e);
        }
    }

    public byte[] getContentDigest() {
        if (this.resultDigest == null) {
            throw new IllegalStateException("method can only be called after verify.");
        }
        return Arrays.clone(this.resultDigest);
    }

    public String getEncryptionAlgOID() {
        return this.encryptionAlgorithm.getAlgorithm().getId();
    }

    public byte[] getEncryptionAlgParams() {
        try {
            return encodeObj(this.encryptionAlgorithm.getParameters());
        } catch (Exception e) {
            throw new RuntimeException("exception getting encryption parameters " + e);
        }
    }

    public AttributeTable getSignedAttributes() {
        if (this.signedAttributeSet != null && this.signedAttributeValues == null) {
            this.signedAttributeValues = new AttributeTable(this.signedAttributeSet);
        }
        return this.signedAttributeValues;
    }

    public AttributeTable getUnsignedAttributes() {
        if (this.unsignedAttributeSet != null && this.unsignedAttributeValues == null) {
            this.unsignedAttributeValues = new AttributeTable(this.unsignedAttributeSet);
        }
        return this.unsignedAttributeValues;
    }

    public byte[] getSignature() {
        return Arrays.clone(this.signature);
    }

    public SignerInformationStore getCounterSignatures() {
        AttributeTable unsignedAttributeTable = getUnsignedAttributes();
        if (unsignedAttributeTable == null) {
            return new SignerInformationStore(new ArrayList(0));
        }
        List counterSignatures = new ArrayList();
        ASN1EncodableVector allCSAttrs = unsignedAttributeTable.getAll(CMSAttributes.counterSignature);
        for (int i = 0; i < allCSAttrs.size(); i++) {
            Attribute counterSignatureAttribute = (Attribute) allCSAttrs.get(i);
            ASN1Set values = counterSignatureAttribute.getAttrValues();
            if (values.size() < 1) {
            }
            Enumeration en = values.getObjects();
            while (en.hasMoreElements()) {
                SignerInfo si = SignerInfo.getInstance(en.nextElement());
                counterSignatures.add(new SignerInformation(si, null, new CMSProcessableByteArray(getSignature()), null));
            }
        }
        return new SignerInformationStore(counterSignatures);
    }

    public byte[] getEncodedSignedAttributes() throws IOException {
        if (this.signedAttributeSet != null) {
            return this.signedAttributeSet.getEncoded(ASN1Encoding.DER);
        }
        return null;
    }

    private boolean doVerify(SignerInformationVerifier verifier) throws CMSException {
        String encName = CMSSignedHelper.INSTANCE.getEncryptionAlgName(getEncryptionAlgOID());
        try {
            ContentVerifier contentVerifier = verifier.getContentVerifier(this.encryptionAlgorithm, this.info.getDigestAlgorithm());
            try {
                OutputStream sigOut = contentVerifier.getOutputStream();
                if (this.resultDigest == null) {
                    DigestCalculator calc = verifier.getDigestCalculator(getDigestAlgorithmID());
                    if (this.content != null) {
                        OutputStream digOut = calc.getOutputStream();
                        if (this.signedAttributeSet == null) {
                            if (contentVerifier instanceof RawContentVerifier) {
                                this.content.write(digOut);
                            } else {
                                OutputStream cOut = new TeeOutputStream(digOut, sigOut);
                                this.content.write(cOut);
                                cOut.close();
                            }
                        } else {
                            this.content.write(digOut);
                            sigOut.write(getEncodedSignedAttributes());
                        }
                        digOut.close();
                    } else if (this.signedAttributeSet != null) {
                        sigOut.write(getEncodedSignedAttributes());
                    } else {
                        throw new CMSException("data not encapsulated in signature - use detached constructor.");
                    }
                    this.resultDigest = calc.getDigest();
                } else if (this.signedAttributeSet == null) {
                    if (this.content != null) {
                        this.content.write(sigOut);
                    }
                } else {
                    sigOut.write(getEncodedSignedAttributes());
                }
                sigOut.close();
                ASN1Primitive validContentType = getSingleValuedSignedAttribute(CMSAttributes.contentType, "content-type");
                if (validContentType == null) {
                    if (!this.isCounterSignature && this.signedAttributeSet != null) {
                        throw new CMSException("The content-type attribute type MUST be present whenever signed attributes are present in signed-data");
                    }
                } else {
                    if (this.isCounterSignature) {
                        throw new CMSException("[For counter signatures,] the signedAttributes field MUST NOT contain a content-type attribute");
                    }
                    if (!(validContentType instanceof ASN1ObjectIdentifier)) {
                        throw new CMSException("content-type attribute value not of ASN.1 type 'OBJECT IDENTIFIER'");
                    }
                    ASN1ObjectIdentifier signedContentType = (ASN1ObjectIdentifier) validContentType;
                    if (!signedContentType.equals(this.contentType)) {
                        throw new CMSException("content-type attribute value does not match eContentType");
                    }
                }
                AttributeTable signedAttrTable = getSignedAttributes();
                AttributeTable unsignedAttrTable = getUnsignedAttributes();
                if (unsignedAttrTable != null && unsignedAttrTable.getAll(CMSAttributes.cmsAlgorithmProtect).size() > 0) {
                    throw new CMSException("A cmsAlgorithmProtect attribute MUST be a signed attribute");
                }
                if (signedAttrTable != null) {
                    ASN1EncodableVector protectionAttributes = signedAttrTable.getAll(CMSAttributes.cmsAlgorithmProtect);
                    if (protectionAttributes.size() > 1) {
                        throw new CMSException("Only one instance of a cmsAlgorithmProtect attribute can be present");
                    }
                    if (protectionAttributes.size() > 0) {
                        Attribute attr = Attribute.getInstance(protectionAttributes.get(0));
                        if (attr.getAttrValues().size() != 1) {
                            throw new CMSException("A cmsAlgorithmProtect attribute MUST contain exactly one value");
                        }
                        CMSAlgorithmProtection algorithmProtection = CMSAlgorithmProtection.getInstance(attr.getAttributeValues()[0]);
                        if (!CMSUtils.isEquivalent(algorithmProtection.getDigestAlgorithm(), this.info.getDigestAlgorithm())) {
                            throw new CMSException("CMS Algorithm Identifier Protection check failed for digestAlgorithm");
                        }
                        if (!CMSUtils.isEquivalent(algorithmProtection.getSignatureAlgorithm(), this.info.getDigestEncryptionAlgorithm())) {
                            throw new CMSException("CMS Algorithm Identifier Protection check failed for signatureAlgorithm");
                        }
                    }
                }
                ASN1Primitive validMessageDigest = getSingleValuedSignedAttribute(CMSAttributes.messageDigest, "message-digest");
                if (validMessageDigest == null) {
                    if (this.signedAttributeSet != null) {
                        throw new CMSException("the message-digest signed attribute type MUST be present when there are any signed attributes present");
                    }
                } else {
                    if (!(validMessageDigest instanceof ASN1OctetString)) {
                        throw new CMSException("message-digest attribute value not of ASN.1 type 'OCTET STRING'");
                    }
                    ASN1OctetString signedMessageDigest = (ASN1OctetString) validMessageDigest;
                    if (!Arrays.constantTimeAreEqual(this.resultDigest, signedMessageDigest.getOctets())) {
                        throw new CMSSignerDigestMismatchException("message-digest attribute value does not match calculated value");
                    }
                }
                if (signedAttrTable != null && signedAttrTable.getAll(CMSAttributes.counterSignature).size() > 0) {
                    throw new CMSException("A countersignature attribute MUST NOT be a signed attribute");
                }
                AttributeTable unsignedAttrTable2 = getUnsignedAttributes();
                if (unsignedAttrTable2 != null) {
                    ASN1EncodableVector csAttrs = unsignedAttrTable2.getAll(CMSAttributes.counterSignature);
                    for (int i = 0; i < csAttrs.size(); i++) {
                        Attribute csAttr = Attribute.getInstance(csAttrs.get(i));
                        if (csAttr.getAttrValues().size() < 1) {
                            throw new CMSException("A countersignature attribute MUST contain at least one AttributeValue");
                        }
                    }
                }
                try {
                    if (this.signedAttributeSet == null && this.resultDigest != null && (contentVerifier instanceof RawContentVerifier)) {
                        RawContentVerifier rawVerifier = (RawContentVerifier) contentVerifier;
                        if (encName.equals("RSA")) {
                            DigestInfo digInfo = new DigestInfo(new AlgorithmIdentifier(this.digestAlgorithm.getAlgorithm(), DERNull.INSTANCE), this.resultDigest);
                            return rawVerifier.verify(digInfo.getEncoded(ASN1Encoding.DER), getSignature());
                        }
                        return rawVerifier.verify(this.resultDigest, getSignature());
                    }
                    return contentVerifier.verify(getSignature());
                } catch (IOException e) {
                    throw new CMSException("can't process mime object to create signature.", e);
                }
            } catch (IOException e2) {
                throw new CMSException("can't process mime object to create signature.", e2);
            } catch (OperatorCreationException e3) {
                throw new CMSException("can't create digest calculator: " + e3.getMessage(), e3);
            }
        } catch (OperatorCreationException e4) {
            throw new CMSException("can't create content verifier: " + e4.getMessage(), e4);
        }
    }

    public boolean verify(SignerInformationVerifier verifier) throws CMSException {
        Time signingTime = getSigningTime();
        if (verifier.hasAssociatedCertificate() && signingTime != null) {
            X509CertificateHolder dcv = verifier.getAssociatedCertificate();
            if (!dcv.isValidOn(signingTime.getDate())) {
                throw new CMSVerifierCertificateNotValidException("verifier not valid at signingTime");
            }
        }
        return doVerify(verifier);
    }

    public SignerInfo toASN1Structure() {
        return this.info;
    }

    private ASN1Primitive getSingleValuedSignedAttribute(ASN1ObjectIdentifier attrOID, String printableName) throws CMSException {
        AttributeTable unsignedAttrTable = getUnsignedAttributes();
        if (unsignedAttrTable != null && unsignedAttrTable.getAll(attrOID).size() > 0) {
            throw new CMSException("The " + printableName + " attribute MUST NOT be an unsigned attribute");
        }
        AttributeTable signedAttrTable = getSignedAttributes();
        if (signedAttrTable == null) {
            return null;
        }
        ASN1EncodableVector v = signedAttrTable.getAll(attrOID);
        switch (v.size()) {
            case 0:
                return null;
            case 1:
                Attribute t = (Attribute) v.get(0);
                ASN1Set attrValues = t.getAttrValues();
                if (attrValues.size() != 1) {
                    throw new CMSException("A " + printableName + " attribute MUST have a single attribute value");
                }
                return attrValues.getObjectAt(0).toASN1Primitive();
            default:
                throw new CMSException("The SignedAttributes in a signerInfo MUST NOT include multiple instances of the " + printableName + " attribute");
        }
    }

    private Time getSigningTime() throws CMSException {
        ASN1Primitive validSigningTime = getSingleValuedSignedAttribute(CMSAttributes.signingTime, "signing-time");
        if (validSigningTime == null) {
            return null;
        }
        try {
            return Time.getInstance(validSigningTime);
        } catch (IllegalArgumentException e) {
            throw new CMSException("signing-time attribute value not a valid 'Time' structure");
        }
    }

    public static SignerInformation replaceUnsignedAttributes(SignerInformation signerInformation, AttributeTable unsignedAttributes) {
        SignerInfo sInfo = signerInformation.info;
        ASN1Set unsignedAttr = null;
        if (unsignedAttributes != null) {
            unsignedAttr = new DERSet(unsignedAttributes.toASN1EncodableVector());
        }
        return new SignerInformation(new SignerInfo(sInfo.getSID(), sInfo.getDigestAlgorithm(), sInfo.getAuthenticatedAttributes(), sInfo.getDigestEncryptionAlgorithm(), sInfo.getEncryptedDigest(), unsignedAttr), signerInformation.contentType, signerInformation.content, null);
    }

    public static SignerInformation addCounterSigners(SignerInformation signerInformation, SignerInformationStore counterSigners) {
        ASN1EncodableVector v;
        SignerInfo sInfo = signerInformation.info;
        AttributeTable unsignedAttr = signerInformation.getUnsignedAttributes();
        if (unsignedAttr != null) {
            v = unsignedAttr.toASN1EncodableVector();
        } else {
            v = new ASN1EncodableVector();
        }
        ASN1EncodableVector sigs = new ASN1EncodableVector();
        Iterator<SignerInformation> it = counterSigners.getSigners().iterator();
        while (it.hasNext()) {
            sigs.add(it.next().toASN1Structure());
        }
        v.add(new Attribute(CMSAttributes.counterSignature, new DERSet(sigs)));
        return new SignerInformation(new SignerInfo(sInfo.getSID(), sInfo.getDigestAlgorithm(), sInfo.getAuthenticatedAttributes(), sInfo.getDigestEncryptionAlgorithm(), sInfo.getEncryptedDigest(), new DERSet(v)), signerInformation.contentType, signerInformation.content, null);
    }
}
