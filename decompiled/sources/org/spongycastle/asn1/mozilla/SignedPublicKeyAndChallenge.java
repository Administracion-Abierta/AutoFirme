package org.spongycastle.asn1.mozilla;

import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERBitString;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/mozilla/SignedPublicKeyAndChallenge.class */
public class SignedPublicKeyAndChallenge extends ASN1Object {
    private final PublicKeyAndChallenge pubKeyAndChal;
    private final ASN1Sequence pkacSeq;

    public static SignedPublicKeyAndChallenge getInstance(Object obj) {
        if (obj instanceof SignedPublicKeyAndChallenge) {
            return (SignedPublicKeyAndChallenge) obj;
        }
        if (obj != null) {
            return new SignedPublicKeyAndChallenge(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private SignedPublicKeyAndChallenge(ASN1Sequence seq) {
        this.pkacSeq = seq;
        this.pubKeyAndChal = PublicKeyAndChallenge.getInstance(seq.getObjectAt(0));
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.pkacSeq;
    }

    public PublicKeyAndChallenge getPublicKeyAndChallenge() {
        return this.pubKeyAndChal;
    }

    public AlgorithmIdentifier getSignatureAlgorithm() {
        return AlgorithmIdentifier.getInstance(this.pkacSeq.getObjectAt(1));
    }

    public DERBitString getSignature() {
        return DERBitString.getInstance(this.pkacSeq.getObjectAt(2));
    }
}
