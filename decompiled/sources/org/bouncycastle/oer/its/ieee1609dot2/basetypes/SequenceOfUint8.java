package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/SequenceOfUint8.class */
public class SequenceOfUint8 extends ASN1Object {
    private final List<UINT8> uint8s;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/SequenceOfUint8$Builder.class */
    public static class Builder {
        private final List<UINT8> items = new ArrayList();

        public Builder addHashId3(UINT8... uint8Arr) {
            this.items.addAll(Arrays.asList(uint8Arr));
            return this;
        }

        public SequenceOfUint8 build() {
            return new SequenceOfUint8(this.items);
        }
    }

    public SequenceOfUint8(List<UINT8> list) {
        this.uint8s = Collections.unmodifiableList(list);
    }

    private SequenceOfUint8(ASN1Sequence aSN1Sequence) {
        ArrayList arrayList = new ArrayList();
        Iterator<ASN1Encodable> it = aSN1Sequence.iterator();
        while (it.hasNext()) {
            arrayList.add(UINT8.getInstance(it.next()));
        }
        this.uint8s = Collections.unmodifiableList(arrayList);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static SequenceOfUint8 getInstance(Object obj) {
        if (obj instanceof SequenceOfUint8) {
            return (SequenceOfUint8) obj;
        }
        if (obj != null) {
            return new SequenceOfUint8(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public List<UINT8> getUint8s() {
        return this.uint8s;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        Iterator<UINT8> it = this.uint8s.iterator();
        while (it.hasNext()) {
            aSN1EncodableVector.add(it.next().toASN1Primitive());
        }
        return new DERSequence(aSN1EncodableVector);
    }
}
