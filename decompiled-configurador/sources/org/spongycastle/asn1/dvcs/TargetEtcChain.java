package org.spongycastle.asn1.dvcs;

import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERTaggedObject;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/dvcs/TargetEtcChain.class */
public class TargetEtcChain extends ASN1Object {
    private CertEtcToken target;
    private ASN1Sequence chain;
    private PathProcInput pathProcInput;

    public TargetEtcChain(CertEtcToken target) {
        this(target, null, null);
    }

    public TargetEtcChain(CertEtcToken target, CertEtcToken[] chain) {
        this(target, chain, null);
    }

    public TargetEtcChain(CertEtcToken target, PathProcInput pathProcInput) {
        this(target, null, pathProcInput);
    }

    public TargetEtcChain(CertEtcToken target, CertEtcToken[] chain, PathProcInput pathProcInput) {
        this.target = target;
        if (chain != null) {
            this.chain = new DERSequence(chain);
        }
        this.pathProcInput = pathProcInput;
    }

    private TargetEtcChain(ASN1Sequence seq) {
        int i = 0 + 1;
        this.target = CertEtcToken.getInstance(seq.getObjectAt(0));
        if (seq.size() > 1) {
            int i2 = i + 1;
            ASN1Encodable obj = seq.getObjectAt(i);
            if (obj instanceof ASN1TaggedObject) {
                extractPathProcInput(obj);
                return;
            }
            this.chain = ASN1Sequence.getInstance(obj);
            if (seq.size() > 2) {
                extractPathProcInput(seq.getObjectAt(i2));
            }
        }
    }

    private void extractPathProcInput(ASN1Encodable obj) {
        ASN1TaggedObject tagged = ASN1TaggedObject.getInstance(obj);
        switch (tagged.getTagNo()) {
            case 0:
                this.pathProcInput = PathProcInput.getInstance(tagged, false);
                return;
            default:
                throw new IllegalArgumentException("Unknown tag encountered: " + tagged.getTagNo());
        }
    }

    public static TargetEtcChain getInstance(Object obj) {
        if (obj instanceof TargetEtcChain) {
            return (TargetEtcChain) obj;
        }
        if (obj != null) {
            return new TargetEtcChain(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public static TargetEtcChain getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.target);
        if (this.chain != null) {
            v.add(this.chain);
        }
        if (this.pathProcInput != null) {
            v.add(new DERTaggedObject(false, 0, this.pathProcInput));
        }
        return new DERSequence(v);
    }

    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append("TargetEtcChain {\n");
        s.append("target: " + this.target + "\n");
        if (this.chain != null) {
            s.append("chain: " + this.chain + "\n");
        }
        if (this.pathProcInput != null) {
            s.append("pathProcInput: " + this.pathProcInput + "\n");
        }
        s.append("}\n");
        return s.toString();
    }

    public CertEtcToken getTarget() {
        return this.target;
    }

    public CertEtcToken[] getChain() {
        if (this.chain != null) {
            return CertEtcToken.arrayFromSequence(this.chain);
        }
        return null;
    }

    public PathProcInput getPathProcInput() {
        return this.pathProcInput;
    }

    public static TargetEtcChain[] arrayFromSequence(ASN1Sequence seq) {
        TargetEtcChain[] tmp = new TargetEtcChain[seq.size()];
        for (int i = 0; i != tmp.length; i++) {
            tmp[i] = getInstance(seq.getObjectAt(i));
        }
        return tmp;
    }
}
