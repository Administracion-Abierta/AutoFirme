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

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/SequenceOfHashedId8.class */
public class SequenceOfHashedId8 extends ASN1Object {
    private final List<HashedId8> hashedId8s;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/SequenceOfHashedId8$Builder.class */
    public static class Builder {
        private final List<HashedId8> items = new ArrayList();

        public Builder addHashId8(HashedId8... hashedId8Arr) {
            this.items.addAll(Arrays.asList(hashedId8Arr));
            return this;
        }

        public SequenceOfHashedId8 build() {
            return new SequenceOfHashedId8(this.items);
        }
    }

    public SequenceOfHashedId8(List<HashedId8> list) {
        this.hashedId8s = Collections.unmodifiableList(list);
    }

    private SequenceOfHashedId8(ASN1Sequence aSN1Sequence) {
        ArrayList arrayList = new ArrayList();
        Iterator<ASN1Encodable> it = aSN1Sequence.iterator();
        while (it.hasNext()) {
            arrayList.add(HashedId8.getInstance(it.next()));
        }
        this.hashedId8s = Collections.unmodifiableList(arrayList);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static SequenceOfHashedId8 getInstance(Object obj) {
        if (obj instanceof SequenceOfHashedId8) {
            return (SequenceOfHashedId8) obj;
        }
        if (obj != null) {
            return new SequenceOfHashedId8(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public List<HashedId8> getHashedId8s() {
        return this.hashedId8s;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence((ASN1Encodable[]) this.hashedId8s.toArray(new ASN1Encodable[0]));
    }
}
