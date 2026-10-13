package org.spongycastle.asn1.cmc;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.crmf.PKIPublicationInfo;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/cmc/CMCPublicationInfo.class */
public class CMCPublicationInfo extends ASN1Object {
    private final AlgorithmIdentifier hashAlg;
    private final ASN1Sequence certHashes;
    private final PKIPublicationInfo pubInfo;

    public CMCPublicationInfo(AlgorithmIdentifier hashAlg, byte[][] anchorHashes, PKIPublicationInfo pubInfo) {
        this.hashAlg = hashAlg;
        ASN1EncodableVector v = new ASN1EncodableVector();
        for (int i = 0; i != anchorHashes.length; i++) {
            v.add(new DEROctetString(Arrays.clone(anchorHashes[i])));
        }
        this.certHashes = new DERSequence(v);
        this.pubInfo = pubInfo;
    }

    private CMCPublicationInfo(ASN1Sequence seq) {
        if (seq.size() != 3) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.hashAlg = AlgorithmIdentifier.getInstance(seq.getObjectAt(0));
        this.certHashes = ASN1Sequence.getInstance(seq.getObjectAt(1));
        this.pubInfo = PKIPublicationInfo.getInstance(seq.getObjectAt(2));
    }

    public static CMCPublicationInfo getInstance(Object o) {
        if (o instanceof CMCPublicationInfo) {
            return (CMCPublicationInfo) o;
        }
        if (o != null) {
            return new CMCPublicationInfo(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public AlgorithmIdentifier getHashAlg() {
        return this.hashAlg;
    }

    public byte[][] getCertHashes() {
        byte[][] hashes = new byte[this.certHashes.size()][];
        for (int i = 0; i != hashes.length; i++) {
            hashes[i] = Arrays.clone(ASN1OctetString.getInstance(this.certHashes.getObjectAt(i)).getOctets());
        }
        return hashes;
    }

    public PKIPublicationInfo getPubInfo() {
        return this.pubInfo;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.hashAlg);
        v.add(this.certHashes);
        v.add(this.pubInfo);
        return new DERSequence(v);
    }
}
