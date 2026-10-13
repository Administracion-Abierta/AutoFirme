package org.spongycastle.asn1.pkcs;

import java.math.BigInteger;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x509.X509Name;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/pkcs/IssuerAndSerialNumber.class */
public class IssuerAndSerialNumber extends ASN1Object {
    X500Name name;
    ASN1Integer certSerialNumber;

    public static IssuerAndSerialNumber getInstance(Object obj) {
        if (obj instanceof IssuerAndSerialNumber) {
            return (IssuerAndSerialNumber) obj;
        }
        if (obj != null) {
            return new IssuerAndSerialNumber(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private IssuerAndSerialNumber(ASN1Sequence seq) {
        this.name = X500Name.getInstance(seq.getObjectAt(0));
        this.certSerialNumber = (ASN1Integer) seq.getObjectAt(1);
    }

    public IssuerAndSerialNumber(X509Name name, BigInteger certSerialNumber) {
        this.name = X500Name.getInstance(name.toASN1Primitive());
        this.certSerialNumber = new ASN1Integer(certSerialNumber);
    }

    public IssuerAndSerialNumber(X509Name name, ASN1Integer certSerialNumber) {
        this.name = X500Name.getInstance(name.toASN1Primitive());
        this.certSerialNumber = certSerialNumber;
    }

    public IssuerAndSerialNumber(X500Name name, BigInteger certSerialNumber) {
        this.name = name;
        this.certSerialNumber = new ASN1Integer(certSerialNumber);
    }

    public X500Name getName() {
        return this.name;
    }

    public ASN1Integer getCertificateSerialNumber() {
        return this.certSerialNumber;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.name);
        v.add(this.certSerialNumber);
        return new DERSequence(v);
    }
}
