package org.spongycastle.asn1.esf;

import java.math.BigInteger;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1UTCTime;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.x500.X500Name;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/esf/CrlIdentifier.class */
public class CrlIdentifier extends ASN1Object {
    private X500Name crlIssuer;
    private ASN1UTCTime crlIssuedTime;
    private ASN1Integer crlNumber;

    public static CrlIdentifier getInstance(Object obj) {
        if (obj instanceof CrlIdentifier) {
            return (CrlIdentifier) obj;
        }
        if (obj != null) {
            return new CrlIdentifier(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private CrlIdentifier(ASN1Sequence seq) {
        if (seq.size() < 2 || seq.size() > 3) {
            throw new IllegalArgumentException();
        }
        this.crlIssuer = X500Name.getInstance(seq.getObjectAt(0));
        this.crlIssuedTime = ASN1UTCTime.getInstance(seq.getObjectAt(1));
        if (seq.size() > 2) {
            this.crlNumber = ASN1Integer.getInstance(seq.getObjectAt(2));
        }
    }

    public CrlIdentifier(X500Name crlIssuer, ASN1UTCTime crlIssuedTime) {
        this(crlIssuer, crlIssuedTime, null);
    }

    public CrlIdentifier(X500Name crlIssuer, ASN1UTCTime crlIssuedTime, BigInteger crlNumber) {
        this.crlIssuer = crlIssuer;
        this.crlIssuedTime = crlIssuedTime;
        if (null != crlNumber) {
            this.crlNumber = new ASN1Integer(crlNumber);
        }
    }

    public X500Name getCrlIssuer() {
        return this.crlIssuer;
    }

    public ASN1UTCTime getCrlIssuedTime() {
        return this.crlIssuedTime;
    }

    public BigInteger getCrlNumber() {
        if (null == this.crlNumber) {
            return null;
        }
        return this.crlNumber.getValue();
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.crlIssuer.toASN1Primitive());
        v.add(this.crlIssuedTime);
        if (null != this.crlNumber) {
            v.add(this.crlNumber);
        }
        return new DERSequence(v);
    }
}
