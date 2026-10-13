package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ItsUtils;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/SequenceOfPsid.class */
public class SequenceOfPsid extends ASN1Object {
    private final List<Psid> psids;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/SequenceOfPsid$Builder.class */
    public static class Builder {
        private List<Psid> items = new ArrayList();

        public Builder setItems(List<Psid> list) {
            this.items = list;
            return this;
        }

        public Builder setItem(Psid... psidArr) {
            for (int i = 0; i != psidArr.length; i++) {
                this.items.add(psidArr[i]);
            }
            return this;
        }

        public SequenceOfPsid createSequenceOfPsidSsp() {
            return new SequenceOfPsid(this.items);
        }
    }

    public SequenceOfPsid(List<Psid> list) {
        this.psids = Collections.unmodifiableList(list);
    }

    private SequenceOfPsid(ASN1Sequence aSN1Sequence) {
        ArrayList arrayList = new ArrayList();
        Iterator<ASN1Encodable> it = aSN1Sequence.iterator();
        while (it.hasNext()) {
            arrayList.add(Psid.getInstance(it.next()));
        }
        this.psids = Collections.unmodifiableList(arrayList);
    }

    public static SequenceOfPsid getInstance(Object obj) {
        if (obj instanceof SequenceOfPsid) {
            return (SequenceOfPsid) obj;
        }
        if (obj != null) {
            return new SequenceOfPsid(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<Psid> getPsids() {
        return this.psids;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(this.psids);
    }
}
