package org.spongycastle.pqc.asn1;

import java.math.BigInteger;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/asn1/ParSet.class */
public class ParSet extends ASN1Object {
    private static final BigInteger ZERO = BigInteger.valueOf(0);
    private int t;
    private int[] h;
    private int[] w;
    private int[] k;

    private static int checkBigIntegerInIntRangeAndPositive(BigInteger b) {
        if (b.compareTo(BigInteger.valueOf(2147483647L)) > 0 || b.compareTo(ZERO) <= 0) {
            throw new IllegalArgumentException("BigInteger not in Range: " + b.toString());
        }
        return b.intValue();
    }

    private ParSet(ASN1Sequence seq) {
        if (seq.size() != 4) {
            throw new IllegalArgumentException("sie of seqOfParams = " + seq.size());
        }
        BigInteger asn1int = ((ASN1Integer) seq.getObjectAt(0)).getValue();
        this.t = checkBigIntegerInIntRangeAndPositive(asn1int);
        ASN1Sequence seqOfPSh = (ASN1Sequence) seq.getObjectAt(1);
        ASN1Sequence seqOfPSw = (ASN1Sequence) seq.getObjectAt(2);
        ASN1Sequence seqOfPSK = (ASN1Sequence) seq.getObjectAt(3);
        if (seqOfPSh.size() != this.t || seqOfPSw.size() != this.t || seqOfPSK.size() != this.t) {
            throw new IllegalArgumentException("invalid size of sequences");
        }
        this.h = new int[seqOfPSh.size()];
        this.w = new int[seqOfPSw.size()];
        this.k = new int[seqOfPSK.size()];
        for (int i = 0; i < this.t; i++) {
            this.h[i] = checkBigIntegerInIntRangeAndPositive(((ASN1Integer) seqOfPSh.getObjectAt(i)).getValue());
            this.w[i] = checkBigIntegerInIntRangeAndPositive(((ASN1Integer) seqOfPSw.getObjectAt(i)).getValue());
            this.k[i] = checkBigIntegerInIntRangeAndPositive(((ASN1Integer) seqOfPSK.getObjectAt(i)).getValue());
        }
    }

    public ParSet(int t, int[] h, int[] w, int[] k) {
        this.t = t;
        this.h = h;
        this.w = w;
        this.k = k;
    }

    public static ParSet getInstance(Object o) {
        if (o instanceof ParSet) {
            return (ParSet) o;
        }
        if (o != null) {
            return new ParSet(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public int getT() {
        return this.t;
    }

    public int[] getH() {
        return Arrays.clone(this.h);
    }

    public int[] getW() {
        return Arrays.clone(this.w);
    }

    public int[] getK() {
        return Arrays.clone(this.k);
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector seqOfPSh = new ASN1EncodableVector();
        ASN1EncodableVector seqOfPSw = new ASN1EncodableVector();
        ASN1EncodableVector seqOfPSK = new ASN1EncodableVector();
        for (int i = 0; i < this.h.length; i++) {
            seqOfPSh.add(new ASN1Integer(this.h[i]));
            seqOfPSw.add(new ASN1Integer(this.w[i]));
            seqOfPSK.add(new ASN1Integer(this.k[i]));
        }
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new ASN1Integer(this.t));
        v.add(new DERSequence(seqOfPSh));
        v.add(new DERSequence(seqOfPSw));
        v.add(new DERSequence(seqOfPSK));
        return new DERSequence(v);
    }
}
