package org.bouncycastle.asn1.isismtt.ocsp;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/isismtt/ocsp/RequestedCertificate.class */
public class RequestedCertificate extends ASN1Object implements ASN1Choice {
    public static final int certificate = -1;
    public static final int publicKeyCertificate = 0;
    public static final int attributeCertificate = 1;
    private Certificate cert;
    private byte[] publicKeyCert;
    private byte[] attributeCert;

    public static RequestedCertificate getInstance(Object obj) {
        if (obj == null || (obj instanceof RequestedCertificate)) {
            return (RequestedCertificate) obj;
        }
        if (obj instanceof ASN1Sequence) {
            return new RequestedCertificate(Certificate.getInstance(obj));
        }
        if (obj instanceof ASN1TaggedObject) {
            return new RequestedCertificate((ASN1TaggedObject) obj);
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    public static RequestedCertificate getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        if (z) {
            return getInstance(aSN1TaggedObject.getExplicitBaseObject());
        }
        throw new IllegalArgumentException("choice item must be explicitly tagged");
    }

    private RequestedCertificate(ASN1TaggedObject aSN1TaggedObject) {
        if (aSN1TaggedObject.getTagNo() == 0) {
            this.publicKeyCert = ASN1OctetString.getInstance(aSN1TaggedObject, true).getOctets();
        } else {
            if (aSN1TaggedObject.getTagNo() != 1) {
                throw new IllegalArgumentException("unknown tag number: " + aSN1TaggedObject.getTagNo());
            }
            this.attributeCert = ASN1OctetString.getInstance(aSN1TaggedObject, true).getOctets();
        }
    }

    public RequestedCertificate(Certificate certificate2) {
        this.cert = certificate2;
    }

    public RequestedCertificate(int i, byte[] bArr) {
        this(new DERTaggedObject(i, new DEROctetString(bArr)));
    }

    public int getType() {
        if (this.cert != null) {
            return -1;
        }
        return this.publicKeyCert != null ? 0 : 1;
    }

    public byte[] getCertificateBytes() {
        if (this.cert == null) {
            return this.publicKeyCert != null ? Arrays.clone(this.publicKeyCert) : Arrays.clone(this.attributeCert);
        }
        try {
            return this.cert.getEncoded();
        } catch (IOException e) {
            throw new IllegalStateException("can't decode certificate: " + e);
        }
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        if (this.publicKeyCert != null) {
            return new DERTaggedObject(0, new DEROctetString(this.publicKeyCert));
        }
        return this.attributeCert != null ? new DERTaggedObject(1, new DEROctetString(this.attributeCert)) : this.cert.toASN1Primitive();
    }
}
