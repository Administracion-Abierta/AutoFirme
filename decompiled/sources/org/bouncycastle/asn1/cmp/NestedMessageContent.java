package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Sequence;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/cmp/NestedMessageContent.class */
public class NestedMessageContent extends PKIMessages {
    public NestedMessageContent(PKIMessage pKIMessage) {
        super(pKIMessage);
    }

    public NestedMessageContent(PKIMessage[] pKIMessageArr) {
        super(pKIMessageArr);
    }

    public NestedMessageContent(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
    }

    public static NestedMessageContent getInstance(Object obj) {
        if (obj instanceof NestedMessageContent) {
            return (NestedMessageContent) obj;
        }
        if (obj != null) {
            return new NestedMessageContent(ASN1Sequence.getInstance(obj));
        }
        return null;
    }
}
