package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/SequenceOfUint16.class */
public class SequenceOfUint16 extends ASN1Object {
    private final List<UINT16> uint16s;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/SequenceOfUint16$Builder.class */
    public static class Builder {
        private final List<UINT16> items = new ArrayList();

        public Builder addHashId3(UINT16... uint16Arr) {
            this.items.addAll(Arrays.asList(uint16Arr));
            return this;
        }

        public SequenceOfUint16 build() {
            return new SequenceOfUint16(this.items);
        }
    }

    public SequenceOfUint16(List<UINT16> list) {
        this.uint16s = Collections.unmodifiableList(list);
    }

    private SequenceOfUint16(ASN1Sequence aSN1Sequence) {
        ArrayList arrayList = new ArrayList();
        Iterator<ASN1Encodable> it = aSN1Sequence.iterator();
        while (it.hasNext()) {
            arrayList.add(UINT16.getInstance(it.next()));
        }
        this.uint16s = Collections.unmodifiableList(arrayList);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static SequenceOfUint16 getInstance(Object obj) {
        if (obj instanceof SequenceOfUint16) {
            return (SequenceOfUint16) obj;
        }
        if (obj != null) {
            return new SequenceOfUint16(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public List<UINT16> getUint16s() {
        return this.uint16s;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence((ASN1Encodable[]) this.uint16s.toArray(new ASN1Encodable[0]));
    }
}
