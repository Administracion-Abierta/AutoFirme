package org.spongycastle.cms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.util.Iterable;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/RecipientInformationStore.class */
public class RecipientInformationStore implements Iterable<RecipientInformation> {
    private final List all;
    private final Map table;

    public RecipientInformationStore(RecipientInformation recipientInformation) {
        this.table = new HashMap();
        this.all = new ArrayList(1);
        this.all.add(recipientInformation);
        RecipientId sid = recipientInformation.getRID();
        this.table.put(sid, this.all);
    }

    public RecipientInformationStore(Collection<RecipientInformation> recipientInfos) {
        this.table = new HashMap();
        for (RecipientInformation recipientInformation : recipientInfos) {
            RecipientId rid = recipientInformation.getRID();
            ArrayList arrayList = (ArrayList) this.table.get(rid);
            if (arrayList == null) {
                arrayList = new ArrayList(1);
                this.table.put(rid, arrayList);
            }
            arrayList.add(recipientInformation);
        }
        this.all = new ArrayList(recipientInfos);
    }

    public RecipientInformation get(RecipientId selector) {
        Collection<Recipient> recipients = getRecipients(selector);
        if (recipients.size() == 0) {
            return null;
        }
        return (RecipientInformation) recipients.iterator().next();
    }

    public int size() {
        return this.all.size();
    }

    public Collection<RecipientInformation> getRecipients() {
        return new ArrayList(this.all);
    }

    public Collection<Recipient> getRecipients(RecipientId selector) {
        if (selector instanceof KeyTransRecipientId) {
            KeyTransRecipientId keyTrans = (KeyTransRecipientId) selector;
            X500Name issuer = keyTrans.getIssuer();
            byte[] subjectKeyId = keyTrans.getSubjectKeyIdentifier();
            if (issuer != null && subjectKeyId != null) {
                List results = new ArrayList();
                Collection<Recipient> recipients = getRecipients(new KeyTransRecipientId(issuer, keyTrans.getSerialNumber()));
                if (recipients != null) {
                    results.addAll(recipients);
                }
                Collection<Recipient> recipients2 = getRecipients(new KeyTransRecipientId(subjectKeyId));
                if (recipients2 != null) {
                    results.addAll(recipients2);
                }
                return results;
            }
        }
        List list = (ArrayList) this.table.get(selector);
        return list == null ? new ArrayList() : new ArrayList(list);
    }

    @Override // org.spongycastle.util.Iterable, java.lang.Iterable
    public Iterator<RecipientInformation> iterator() {
        return getRecipients().iterator();
    }
}
