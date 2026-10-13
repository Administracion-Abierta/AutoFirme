package org.bouncycastle.pkcs;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.pkcs.CertificationRequest;
import org.bouncycastle.asn1.pkcs.CertificationRequestInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.util.Exceptions;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pkcs/PKCS10CertificationRequest.class */
public class PKCS10CertificationRequest {
    private static Attribute[] EMPTY_ARRAY = new Attribute[0];
    private final CertificationRequest certificationRequest;
    private final boolean isAltRequest;
    private final AlgorithmIdentifier altSignature;
    private final SubjectPublicKeyInfo altPublicKey;
    private final ASN1BitString altSignatureValue;

    private static CertificationRequest parseBytes(byte[] bArr) throws IOException {
        try {
            CertificationRequest certificationRequest = CertificationRequest.getInstance(ASN1Primitive.fromByteArray(bArr));
            if (certificationRequest == null) {
                throw new PKCSIOException("empty data passed to constructor");
            }
            return certificationRequest;
        } catch (ClassCastException e) {
            throw new PKCSIOException("malformed data: " + e.getMessage(), e);
        } catch (IllegalArgumentException e2) {
            throw new PKCSIOException("malformed data: " + e2.getMessage(), e2);
        }
    }

    private static ASN1Encodable getSingleValue(Attribute attribute) {
        ASN1Encodable[] attributeValues = attribute.getAttributeValues();
        if (attributeValues.length != 1) {
            throw new IllegalArgumentException("single value attribute value not size of 1");
        }
        return attributeValues[0];
    }

    public PKCS10CertificationRequest(CertificationRequest certificationRequest) {
        if (certificationRequest == null) {
            throw new NullPointerException("certificationRequest cannot be null");
        }
        this.certificationRequest = certificationRequest;
        ASN1Set attributes = certificationRequest.getCertificationRequestInfo().getAttributes();
        AlgorithmIdentifier algorithmIdentifier = null;
        SubjectPublicKeyInfo subjectPublicKeyInfo = null;
        ASN1BitString aSN1BitString = null;
        if (attributes != null) {
            Enumeration objects = attributes.getObjects();
            while (objects.hasMoreElements()) {
                Attribute attribute = Attribute.getInstance(objects.nextElement());
                algorithmIdentifier = Extension.altSignatureAlgorithm.equals((ASN1Primitive) attribute.getAttrType()) ? AlgorithmIdentifier.getInstance(getSingleValue(attribute)) : algorithmIdentifier;
                subjectPublicKeyInfo = Extension.subjectAltPublicKeyInfo.equals((ASN1Primitive) attribute.getAttrType()) ? SubjectPublicKeyInfo.getInstance(getSingleValue(attribute)) : subjectPublicKeyInfo;
                if (Extension.altSignatureValue.equals((ASN1Primitive) attribute.getAttrType())) {
                    aSN1BitString = ASN1BitString.getInstance(getSingleValue(attribute));
                }
            }
        }
        this.isAltRequest = (algorithmIdentifier != null) | (subjectPublicKeyInfo != null) | (aSN1BitString != null);
        if (this.isAltRequest) {
            if (!((algorithmIdentifier != null) & (subjectPublicKeyInfo != null) & (aSN1BitString != null))) {
                throw new IllegalArgumentException("invalid alternate public key details found");
            }
        }
        this.altSignature = algorithmIdentifier;
        this.altPublicKey = subjectPublicKeyInfo;
        this.altSignatureValue = aSN1BitString;
    }

    public PKCS10CertificationRequest(byte[] bArr) throws IOException {
        this(parseBytes(bArr));
    }

    public CertificationRequest toASN1Structure() {
        return this.certificationRequest;
    }

    public X500Name getSubject() {
        return X500Name.getInstance(this.certificationRequest.getCertificationRequestInfo().getSubject());
    }

    public AlgorithmIdentifier getSignatureAlgorithm() {
        return this.certificationRequest.getSignatureAlgorithm();
    }

    public byte[] getSignature() {
        return this.certificationRequest.getSignature().getOctets();
    }

    public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
        return this.certificationRequest.getCertificationRequestInfo().getSubjectPublicKeyInfo();
    }

    public Attribute[] getAttributes() {
        ASN1Set attributes = this.certificationRequest.getCertificationRequestInfo().getAttributes();
        if (attributes == null) {
            return EMPTY_ARRAY;
        }
        Attribute[] attributeArr = new Attribute[attributes.size()];
        for (int i = 0; i != attributes.size(); i++) {
            attributeArr[i] = Attribute.getInstance(attributes.getObjectAt(i));
        }
        return attributeArr;
    }

    public Attribute[] getAttributes(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        ASN1Set attributes = this.certificationRequest.getCertificationRequestInfo().getAttributes();
        if (attributes == null) {
            return EMPTY_ARRAY;
        }
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i != attributes.size(); i++) {
            Attribute attribute = Attribute.getInstance(attributes.getObjectAt(i));
            if (attribute.getAttrType().equals((ASN1Primitive) aSN1ObjectIdentifier)) {
                arrayList.add(attribute);
            }
        }
        return arrayList.size() == 0 ? EMPTY_ARRAY : (Attribute[]) arrayList.toArray(new Attribute[arrayList.size()]);
    }

    public byte[] getEncoded() throws IOException {
        return this.certificationRequest.getEncoded();
    }

    public boolean isSignatureValid(ContentVerifierProvider contentVerifierProvider) throws PKCSException {
        CertificationRequestInfo certificationRequestInfo = this.certificationRequest.getCertificationRequestInfo();
        try {
            ContentVerifier contentVerifier = contentVerifierProvider.get(this.certificationRequest.getSignatureAlgorithm());
            OutputStream outputStream = contentVerifier.getOutputStream();
            outputStream.write(certificationRequestInfo.getEncoded("DER"));
            outputStream.close();
            return contentVerifier.verify(getSignature());
        } catch (Exception e) {
            throw new PKCSException("unable to process signature: " + e.getMessage(), e);
        }
    }

    public boolean hasAltPublicKey() {
        return this.isAltRequest;
    }

    public boolean isAltSignatureValid(ContentVerifierProvider contentVerifierProvider) throws PKCSException {
        if (!this.isAltRequest) {
            throw new IllegalStateException("no alternate public key present");
        }
        CertificationRequestInfo certificationRequestInfo = this.certificationRequest.getCertificationRequestInfo();
        ASN1Set attributes = certificationRequestInfo.getAttributes();
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        Enumeration objects = attributes.getObjects();
        while (objects.hasMoreElements()) {
            Attribute attribute = Attribute.getInstance(objects.nextElement());
            if (!Extension.altSignatureValue.equals((ASN1Primitive) attribute.getAttrType())) {
                aSN1EncodableVector.add(attribute);
            }
        }
        CertificationRequestInfo certificationRequestInfo2 = new CertificationRequestInfo(certificationRequestInfo.getSubject(), certificationRequestInfo.getSubjectPublicKeyInfo(), new DERSet(aSN1EncodableVector));
        try {
            ContentVerifier contentVerifier = contentVerifierProvider.get(this.altSignature);
            OutputStream outputStream = contentVerifier.getOutputStream();
            outputStream.write(certificationRequestInfo2.getEncoded("DER"));
            outputStream.close();
            return contentVerifier.verify(this.altSignatureValue.getOctets());
        } catch (Exception e) {
            throw new PKCSException("unable to process signature: " + e.getMessage(), e);
        }
    }

    public Extensions getRequestedExtensions() {
        Attribute[] attributes = getAttributes();
        for (int i = 0; i != attributes.length; i++) {
            Attribute attribute = attributes[i];
            if (PKCSObjectIdentifiers.pkcs_9_at_extensionRequest.equals((ASN1Primitive) attribute.getAttrType())) {
                ExtensionsGenerator extensionsGenerator = new ExtensionsGenerator();
                ASN1Set attrValues = attribute.getAttrValues();
                if (attrValues == null || attrValues.size() == 0) {
                    throw new IllegalStateException("pkcs_9_at_extensionRequest present but has no value");
                }
                try {
                    Enumeration objects = ASN1Sequence.getInstance(attrValues.getObjectAt(0)).getObjects();
                    while (objects.hasMoreElements()) {
                        ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(objects.nextElement());
                        boolean z = aSN1Sequence.size() == 3 && ASN1Boolean.getInstance(aSN1Sequence.getObjectAt(1)).isTrue();
                        if (aSN1Sequence.size() == 2) {
                            extensionsGenerator.addExtension(ASN1ObjectIdentifier.getInstance(aSN1Sequence.getObjectAt(0)), false, ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(1)).getOctets());
                        } else {
                            if (aSN1Sequence.size() != 3) {
                                throw new IllegalStateException("incorrect sequence size of Extension get " + aSN1Sequence.size() + " expected 2 or three");
                            }
                            extensionsGenerator.addExtension(ASN1ObjectIdentifier.getInstance(aSN1Sequence.getObjectAt(0)), z, ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(2)).getOctets());
                        }
                    }
                    return extensionsGenerator.generate();
                } catch (IllegalArgumentException e) {
                    throw Exceptions.illegalStateException("asn1 processing issue: " + e.getMessage(), e);
                }
            }
        }
        return null;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof PKCS10CertificationRequest) {
            return toASN1Structure().equals(((PKCS10CertificationRequest) obj).toASN1Structure());
        }
        return false;
    }

    public int hashCode() {
        return toASN1Structure().hashCode();
    }
}
