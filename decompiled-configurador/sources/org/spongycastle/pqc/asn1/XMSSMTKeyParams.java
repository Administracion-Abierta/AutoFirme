package org.spongycastle.pqc.asn1;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/asn1/XMSSMTKeyParams.class */
public class XMSSMTKeyParams extends ASN1Object {
    private final ASN1Integer version;
    private final int height;
    private final int layers;
    private final AlgorithmIdentifier treeDigest;

    public XMSSMTKeyParams(int height, int layers, AlgorithmIdentifier treeDigest) {
        this.version = new ASN1Integer(0L);
        this.height = height;
        this.layers = layers;
        this.treeDigest = treeDigest;
    }

    private XMSSMTKeyParams(ASN1Sequence sequence) {
        this.version = ASN1Integer.getInstance(sequence.getObjectAt(0));
        this.height = ASN1Integer.getInstance(sequence.getObjectAt(1)).getValue().intValue();
        this.layers = ASN1Integer.getInstance(sequence.getObjectAt(2)).getValue().intValue();
        this.treeDigest = AlgorithmIdentifier.getInstance(sequence.getObjectAt(3));
    }

    public static XMSSMTKeyParams getInstance(Object o) {
        if (o instanceof XMSSMTKeyParams) {
            return (XMSSMTKeyParams) o;
        }
        if (o != null) {
            return new XMSSMTKeyParams(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public int getHeight() {
        return this.height;
    }

    public int getLayers() {
        return this.layers;
    }

    public AlgorithmIdentifier getTreeDigest() {
        return this.treeDigest;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.version);
        v.add(new ASN1Integer(this.height));
        v.add(new ASN1Integer(this.layers));
        v.add(this.treeDigest);
        return new DERSequence(v);
    }
}
