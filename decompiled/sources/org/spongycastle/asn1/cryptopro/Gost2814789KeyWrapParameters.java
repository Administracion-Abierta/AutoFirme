package org.spongycastle.asn1.cryptopro;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/cryptopro/Gost2814789KeyWrapParameters.class */
public class Gost2814789KeyWrapParameters extends ASN1Object {
    private final ASN1ObjectIdentifier encryptionParamSet;
    private final byte[] ukm;

    private Gost2814789KeyWrapParameters(ASN1Sequence seq) {
        if (seq.size() == 2) {
            this.encryptionParamSet = ASN1ObjectIdentifier.getInstance(seq.getObjectAt(0));
            this.ukm = ASN1OctetString.getInstance(seq.getObjectAt(1)).getOctets();
        } else {
            if (seq.size() == 1) {
                this.encryptionParamSet = ASN1ObjectIdentifier.getInstance(seq.getObjectAt(0));
                this.ukm = null;
                return;
            }
            throw new IllegalArgumentException("unknown sequence length: " + seq.size());
        }
    }

    public static Gost2814789KeyWrapParameters getInstance(Object obj) {
        if (obj instanceof Gost2814789KeyWrapParameters) {
            return (Gost2814789KeyWrapParameters) obj;
        }
        if (obj != null) {
            return new Gost2814789KeyWrapParameters(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public Gost2814789KeyWrapParameters(ASN1ObjectIdentifier encryptionParamSet) {
        this(encryptionParamSet, null);
    }

    public Gost2814789KeyWrapParameters(ASN1ObjectIdentifier encryptionParamSet, byte[] ukm) {
        this.encryptionParamSet = encryptionParamSet;
        this.ukm = Arrays.clone(ukm);
    }

    public ASN1ObjectIdentifier getEncryptionParamSet() {
        return this.encryptionParamSet;
    }

    public byte[] getUkm() {
        return this.ukm;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.encryptionParamSet);
        if (this.ukm != null) {
            v.add(new DEROctetString(this.ukm));
        }
        return new DERSequence(v);
    }
}
