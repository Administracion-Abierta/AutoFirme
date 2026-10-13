package org.spongycastle.asn1.cmc;

import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.x509.Extension;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/cmc/ExtensionReq.class */
public class ExtensionReq extends ASN1Object {
    private final Extension[] extensions;

    public static ExtensionReq getInstance(Object obj) {
        if (obj instanceof ExtensionReq) {
            return (ExtensionReq) obj;
        }
        if (obj != null) {
            return new ExtensionReq(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public static ExtensionReq getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public ExtensionReq(Extension Extension) {
        this.extensions = new Extension[]{Extension};
    }

    public ExtensionReq(Extension[] extensions) {
        this.extensions = Utils.clone(extensions);
    }

    private ExtensionReq(ASN1Sequence seq) {
        this.extensions = new Extension[seq.size()];
        for (int i = 0; i != seq.size(); i++) {
            this.extensions[i] = Extension.getInstance(seq.getObjectAt(i));
        }
    }

    public Extension[] getExtensions() {
        return Utils.clone(this.extensions);
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.extensions);
    }
}
