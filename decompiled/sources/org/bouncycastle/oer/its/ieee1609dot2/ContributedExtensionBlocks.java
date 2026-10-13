package org.bouncycastle.oer.its.ieee1609dot2;

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

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/ContributedExtensionBlocks.class */
public class ContributedExtensionBlocks extends ASN1Object {
    private final List<ContributedExtensionBlock> contributedExtensionBlocks;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/ContributedExtensionBlocks$Builder.class */
    public static class Builder {
        private final List<ContributedExtensionBlock> extensionBlocks = new ArrayList();

        public Builder add(ContributedExtensionBlock... contributedExtensionBlockArr) {
            this.extensionBlocks.addAll(Arrays.asList(contributedExtensionBlockArr));
            return this;
        }

        public ContributedExtensionBlocks build() {
            return new ContributedExtensionBlocks(this.extensionBlocks);
        }
    }

    public ContributedExtensionBlocks(List<ContributedExtensionBlock> list) {
        this.contributedExtensionBlocks = Collections.unmodifiableList(list);
    }

    private ContributedExtensionBlocks(ASN1Sequence aSN1Sequence) {
        ArrayList arrayList = new ArrayList();
        Iterator<ASN1Encodable> it = aSN1Sequence.iterator();
        while (it.hasNext()) {
            arrayList.add(ContributedExtensionBlock.getInstance(it.next()));
        }
        this.contributedExtensionBlocks = Collections.unmodifiableList(arrayList);
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<ContributedExtensionBlock> getContributedExtensionBlocks() {
        return this.contributedExtensionBlocks;
    }

    public int size() {
        return this.contributedExtensionBlocks.size();
    }

    public static ContributedExtensionBlocks getInstance(Object obj) {
        if (obj instanceof ContributedExtensionBlocks) {
            return (ContributedExtensionBlocks) obj;
        }
        if (obj != null) {
            return new ContributedExtensionBlocks(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence((ASN1Encodable[]) this.contributedExtensionBlocks.toArray(new ContributedExtensionBlock[0]));
    }
}
