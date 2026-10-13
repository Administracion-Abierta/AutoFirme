package org.bouncycastle.cms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.util.Iterable;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/RecipientInformationStore.class */
public class RecipientInformationStore implements Iterable<RecipientInformation> {
    private final List all;
    private final Map table;

    public RecipientInformationStore(RecipientInformation recipientInformation) {
        this.table = new HashMap();
        this.all = new ArrayList(1);
        this.all.add(recipientInformation);
        this.table.put(recipientInformation.getRID(), this.all);
    }

    public RecipientInformationStore(Collection<RecipientInformation> collection) {
        this.table = new HashMap();
        for (RecipientInformation recipientInformation : collection) {
            RecipientId rid = recipientInformation.getRID();
            ArrayList arrayList = (ArrayList) this.table.get(rid);
            if (arrayList == null) {
                arrayList = new ArrayList(1);
                this.table.put(rid, arrayList);
            }
            arrayList.add(recipientInformation);
        }
        this.all = new ArrayList(collection);
    }

    public RecipientInformation get(RecipientId recipientId) {
        Collection<RecipientInformation> recipients = getRecipients(recipientId);
        if (recipients.size() == 0) {
            return null;
        }
        return recipients.iterator().next();
    }

    public int size() {
        return this.all.size();
    }

    public Collection<RecipientInformation> getRecipients() {
        return new ArrayList(this.all);
    }

    public Collection<RecipientInformation> getRecipients(RecipientId recipientId) {
        if (recipientId instanceof PKIXRecipientId) {
            PKIXRecipientId pKIXRecipientId = (PKIXRecipientId) recipientId;
            X500Name issuer = pKIXRecipientId.getIssuer();
            byte[] subjectKeyIdentifier = pKIXRecipientId.getSubjectKeyIdentifier();
            if (issuer != null && subjectKeyIdentifier != null) {
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = (ArrayList) this.table.get(new PKIXRecipientId(pKIXRecipientId.getType(), issuer, pKIXRecipientId.getSerialNumber(), null));
                if (arrayList2 != null) {
                    arrayList.addAll(arrayList2);
                }
                ArrayList arrayList3 = (ArrayList) this.table.get(new PKIXRecipientId(pKIXRecipientId.getType(), null, null, subjectKeyIdentifier));
                if (arrayList3 != null) {
                    arrayList.addAll(arrayList3);
                }
                return arrayList;
            }
        }
        ArrayList arrayList4 = (ArrayList) this.table.get(recipientId);
        return arrayList4 == null ? new ArrayList() : new ArrayList(arrayList4);
    }

    @Override // org.bouncycastle.util.Iterable, java.lang.Iterable
    public Iterator<RecipientInformation> iterator() {
        return getRecipients().iterator();
    }
}
