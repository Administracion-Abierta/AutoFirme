package org.spongycastle.asn1.cryptopro;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/cryptopro/Gost2814789EncryptedKey.class */
public class Gost2814789EncryptedKey extends ASN1Object {
    private final byte[] encryptedKey;
    private final byte[] maskKey;
    private final byte[] macKey;

    private Gost2814789EncryptedKey(ASN1Sequence seq) {
        if (seq.size() == 2) {
            this.encryptedKey = Arrays.clone(ASN1OctetString.getInstance(seq.getObjectAt(0)).getOctets());
            this.macKey = Arrays.clone(ASN1OctetString.getInstance(seq.getObjectAt(1)).getOctets());
            this.maskKey = null;
        } else {
            if (seq.size() == 3) {
                this.encryptedKey = Arrays.clone(ASN1OctetString.getInstance(seq.getObjectAt(0)).getOctets());
                this.maskKey = Arrays.clone(ASN1OctetString.getInstance(ASN1TaggedObject.getInstance(seq.getObjectAt(1)), false).getOctets());
                this.macKey = Arrays.clone(ASN1OctetString.getInstance(seq.getObjectAt(2)).getOctets());
                return;
            }
            throw new IllegalArgumentException("unknown sequence length: " + seq.size());
        }
    }

    public static Gost2814789EncryptedKey getInstance(Object obj) {
        if (obj instanceof Gost2814789EncryptedKey) {
            return (Gost2814789EncryptedKey) obj;
        }
        if (obj != null) {
            return new Gost2814789EncryptedKey(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public Gost2814789EncryptedKey(byte[] encryptedKey, byte[] macKey) {
        this(encryptedKey, null, macKey);
    }

    public Gost2814789EncryptedKey(byte[] encryptedKey, byte[] maskKey, byte[] macKey) {
        this.encryptedKey = Arrays.clone(encryptedKey);
        this.maskKey = Arrays.clone(maskKey);
        this.macKey = Arrays.clone(macKey);
    }

    public byte[] getEncryptedKey() {
        return this.encryptedKey;
    }

    public byte[] getMaskKey() {
        return this.maskKey;
    }

    public byte[] getMacKey() {
        return this.macKey;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new DEROctetString(this.encryptedKey));
        if (this.maskKey != null) {
            v.add(new DERTaggedObject(false, 0, new DEROctetString(this.encryptedKey)));
        }
        v.add(new DEROctetString(this.macKey));
        return new DERSequence(v);
    }
}
