package org.bouncycastle.asn1.nist;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/nist/KMACwithSHAKE128_params.class */
public class KMACwithSHAKE128_params extends ASN1Object {
    private static final byte[] EMPTY_STRING = new byte[0];
    private static final int DEF_LENGTH = 256;
    private final int outputLength;
    private final byte[] customizationString;

    public KMACwithSHAKE128_params(int i) {
        this.outputLength = i;
        this.customizationString = EMPTY_STRING;
    }

    public KMACwithSHAKE128_params(int i, byte[] bArr) {
        this.outputLength = i;
        this.customizationString = Arrays.clone(bArr);
    }

    public static KMACwithSHAKE128_params getInstance(Object obj) {
        if (obj instanceof KMACwithSHAKE128_params) {
            return (KMACwithSHAKE128_params) obj;
        }
        if (obj != null) {
            return new KMACwithSHAKE128_params(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private KMACwithSHAKE128_params(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() > 2) {
            throw new IllegalArgumentException("sequence size greater than 2");
        }
        if (aSN1Sequence.size() == 2) {
            this.outputLength = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(0)).intValueExact();
            this.customizationString = Arrays.clone(ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(1)).getOctets());
        } else if (aSN1Sequence.size() != 1) {
            this.outputLength = 256;
            this.customizationString = EMPTY_STRING;
        } else if (aSN1Sequence.getObjectAt(0) instanceof ASN1Integer) {
            this.outputLength = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(0)).intValueExact();
            this.customizationString = EMPTY_STRING;
        } else {
            this.outputLength = 256;
            this.customizationString = Arrays.clone(ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(0)).getOctets());
        }
    }

    public int getOutputLength() {
        return this.outputLength;
    }

    public byte[] getCustomizationString() {
        return Arrays.clone(this.customizationString);
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        if (this.outputLength != 256) {
            aSN1EncodableVector.add(new ASN1Integer(this.outputLength));
        }
        if (this.customizationString.length != 0) {
            aSN1EncodableVector.add(new DEROctetString(getCustomizationString()));
        }
        return new DERSequence(aSN1EncodableVector);
    }
}
