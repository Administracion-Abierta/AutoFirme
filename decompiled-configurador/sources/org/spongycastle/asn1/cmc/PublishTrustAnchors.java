package org.spongycastle.asn1.cmc;

import java.math.BigInteger;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/cmc/PublishTrustAnchors.class */
public class PublishTrustAnchors extends ASN1Object {
    private final ASN1Integer seqNumber;
    private final AlgorithmIdentifier hashAlgorithm;
    private final ASN1Sequence anchorHashes;

    public PublishTrustAnchors(BigInteger seqNumber, AlgorithmIdentifier hashAlgorithm, byte[][] anchorHashes) {
        this.seqNumber = new ASN1Integer(seqNumber);
        this.hashAlgorithm = hashAlgorithm;
        ASN1EncodableVector v = new ASN1EncodableVector();
        for (int i = 0; i != anchorHashes.length; i++) {
            v.add(new DEROctetString(Arrays.clone(anchorHashes[i])));
        }
        this.anchorHashes = new DERSequence(v);
    }

    private PublishTrustAnchors(ASN1Sequence seq) {
        if (seq.size() != 3) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.seqNumber = ASN1Integer.getInstance(seq.getObjectAt(0));
        this.hashAlgorithm = AlgorithmIdentifier.getInstance(seq.getObjectAt(1));
        this.anchorHashes = ASN1Sequence.getInstance(seq.getObjectAt(2));
    }

    public static PublishTrustAnchors getInstance(Object o) {
        if (o instanceof PublishTrustAnchors) {
            return (PublishTrustAnchors) o;
        }
        if (o != null) {
            return new PublishTrustAnchors(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public BigInteger getSeqNumber() {
        return this.seqNumber.getValue();
    }

    public AlgorithmIdentifier getHashAlgorithm() {
        return this.hashAlgorithm;
    }

    public byte[][] getAnchorHashes() {
        byte[][] hashes = new byte[this.anchorHashes.size()][];
        for (int i = 0; i != hashes.length; i++) {
            hashes[i] = Arrays.clone(ASN1OctetString.getInstance(this.anchorHashes.getObjectAt(i)).getOctets());
        }
        return hashes;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.seqNumber);
        v.add(this.hashAlgorithm);
        v.add(this.anchorHashes);
        return new DERSequence(v);
    }
}
