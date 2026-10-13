package org.bouncycastle.oer.its.etsi102941;

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

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/etsi102941/SequenceOfCtlCommand.class */
public class SequenceOfCtlCommand extends ASN1Object {
    private final List<CtlCommand> ctlCommands;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/etsi102941/SequenceOfCtlCommand$Builder.class */
    public static class Builder {
        private final List<CtlCommand> items = new ArrayList();

        public Builder addHashId8(CtlCommand... ctlCommandArr) {
            this.items.addAll(Arrays.asList(ctlCommandArr));
            return this;
        }

        public SequenceOfCtlCommand build() {
            return new SequenceOfCtlCommand(this.items);
        }
    }

    public SequenceOfCtlCommand(List<CtlCommand> list) {
        this.ctlCommands = Collections.unmodifiableList(list);
    }

    private SequenceOfCtlCommand(ASN1Sequence aSN1Sequence) {
        ArrayList arrayList = new ArrayList();
        Iterator<ASN1Encodable> it = aSN1Sequence.iterator();
        while (it.hasNext()) {
            arrayList.add(CtlCommand.getInstance(it.next()));
        }
        this.ctlCommands = Collections.unmodifiableList(arrayList);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static SequenceOfCtlCommand getInstance(Object obj) {
        if (obj instanceof SequenceOfCtlCommand) {
            return (SequenceOfCtlCommand) obj;
        }
        if (obj != null) {
            return new SequenceOfCtlCommand(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public List<CtlCommand> getCtlCommands() {
        return this.ctlCommands;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence((ASN1Encodable[]) this.ctlCommands.toArray(new ASN1Encodable[0]));
    }
}
