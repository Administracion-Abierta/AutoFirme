package org.spongycastle.pqc.asn1;

import java.math.BigInteger;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/asn1/XMSSMTPublicKey.class */
public class XMSSMTPublicKey extends ASN1Object {
    private final byte[] publicSeed;
    private final byte[] root;

    public XMSSMTPublicKey(byte[] publicSeed, byte[] root) {
        this.publicSeed = Arrays.clone(publicSeed);
        this.root = Arrays.clone(root);
    }

    private XMSSMTPublicKey(ASN1Sequence seq) {
        if (!ASN1Integer.getInstance(seq.getObjectAt(0)).getValue().equals(BigInteger.valueOf(0L))) {
            throw new IllegalArgumentException("unknown version of sequence");
        }
        this.publicSeed = Arrays.clone(DEROctetString.getInstance(seq.getObjectAt(1)).getOctets());
        this.root = Arrays.clone(DEROctetString.getInstance(seq.getObjectAt(2)).getOctets());
    }

    public static XMSSMTPublicKey getInstance(Object o) {
        if (o instanceof XMSSMTPublicKey) {
            return (XMSSMTPublicKey) o;
        }
        if (o != null) {
            return new XMSSMTPublicKey(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public byte[] getPublicSeed() {
        return Arrays.clone(this.publicSeed);
    }

    public byte[] getRoot() {
        return Arrays.clone(this.root);
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new ASN1Integer(0L));
        v.add(new DEROctetString(this.publicSeed));
        v.add(new DEROctetString(this.root));
        return new DERSequence(v);
    }
}
