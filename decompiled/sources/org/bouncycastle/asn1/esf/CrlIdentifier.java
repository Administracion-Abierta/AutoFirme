package org.bouncycastle.asn1.esf;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/esf/CrlIdentifier.class */
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

    private CrlIdentifier(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() < 2 || aSN1Sequence.size() > 3) {
            throw new IllegalArgumentException();
        }
        this.crlIssuer = X500Name.getInstance(aSN1Sequence.getObjectAt(0));
        this.crlIssuedTime = ASN1UTCTime.getInstance(aSN1Sequence.getObjectAt(1));
        if (aSN1Sequence.size() > 2) {
            this.crlNumber = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(2));
        }
    }

    public CrlIdentifier(X500Name x500Name, ASN1UTCTime aSN1UTCTime) {
        this(x500Name, aSN1UTCTime, null);
    }

    public CrlIdentifier(X500Name x500Name, ASN1UTCTime aSN1UTCTime, BigInteger bigInteger) {
        this.crlIssuer = x500Name;
        this.crlIssuedTime = aSN1UTCTime;
        if (null != bigInteger) {
            this.crlNumber = new ASN1Integer(bigInteger);
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

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(3);
        aSN1EncodableVector.add(this.crlIssuer.toASN1Primitive());
        aSN1EncodableVector.add(this.crlIssuedTime);
        if (null != this.crlNumber) {
            aSN1EncodableVector.add(this.crlNumber);
        }
        return new DERSequence(aSN1EncodableVector);
    }
}
