package org.spongycastle.pkcs;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.spongycastle.asn1.ASN1Encoding;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.pkcs.Attribute;
import org.spongycastle.asn1.pkcs.CertificationRequest;
import org.spongycastle.asn1.pkcs.CertificationRequestInfo;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.operator.ContentVerifier;
import org.spongycastle.operator.ContentVerifierProvider;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pkcs/PKCS10CertificationRequest.class */
public class PKCS10CertificationRequest {
    private static Attribute[] EMPTY_ARRAY = new Attribute[0];
    private CertificationRequest certificationRequest;

    private static CertificationRequest parseBytes(byte[] encoding) throws IOException {
        try {
            return CertificationRequest.getInstance(ASN1Primitive.fromByteArray(encoding));
        } catch (ClassCastException e) {
            throw new PKCSIOException("malformed data: " + e.getMessage(), e);
        } catch (IllegalArgumentException e2) {
            throw new PKCSIOException("malformed data: " + e2.getMessage(), e2);
        }
    }

    public PKCS10CertificationRequest(CertificationRequest certificationRequest) {
        this.certificationRequest = certificationRequest;
    }

    public PKCS10CertificationRequest(byte[] encoded) throws IOException {
        this(parseBytes(encoded));
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
        ASN1Set attrSet = this.certificationRequest.getCertificationRequestInfo().getAttributes();
        if (attrSet == null) {
            return EMPTY_ARRAY;
        }
        Attribute[] attrs = new Attribute[attrSet.size()];
        for (int i = 0; i != attrSet.size(); i++) {
            attrs[i] = Attribute.getInstance(attrSet.getObjectAt(i));
        }
        return attrs;
    }

    public Attribute[] getAttributes(ASN1ObjectIdentifier type) {
        ASN1Set attrSet = this.certificationRequest.getCertificationRequestInfo().getAttributes();
        if (attrSet == null) {
            return EMPTY_ARRAY;
        }
        List list = new ArrayList();
        for (int i = 0; i != attrSet.size(); i++) {
            Attribute attr = Attribute.getInstance(attrSet.getObjectAt(i));
            if (attr.getAttrType().equals(type)) {
                list.add(attr);
            }
        }
        if (list.size() == 0) {
            return EMPTY_ARRAY;
        }
        return (Attribute[]) list.toArray(new Attribute[list.size()]);
    }

    public byte[] getEncoded() throws IOException {
        return this.certificationRequest.getEncoded();
    }

    public boolean isSignatureValid(ContentVerifierProvider verifierProvider) throws PKCSException {
        CertificationRequestInfo requestInfo = this.certificationRequest.getCertificationRequestInfo();
        try {
            ContentVerifier verifier = verifierProvider.get(this.certificationRequest.getSignatureAlgorithm());
            OutputStream sOut = verifier.getOutputStream();
            sOut.write(requestInfo.getEncoded(ASN1Encoding.DER));
            sOut.close();
            return verifier.verify(getSignature());
        } catch (Exception e) {
            throw new PKCSException("unable to process signature: " + e.getMessage(), e);
        }
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PKCS10CertificationRequest)) {
            return false;
        }
        PKCS10CertificationRequest other = (PKCS10CertificationRequest) o;
        return toASN1Structure().equals(other.toASN1Structure());
    }

    public int hashCode() {
        return toASN1Structure().hashCode();
    }
}
