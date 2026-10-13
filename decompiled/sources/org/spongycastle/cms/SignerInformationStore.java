package org.spongycastle.cms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.spongycastle.util.Iterable;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/SignerInformationStore.class */
public class SignerInformationStore implements Iterable<SignerInformation> {
    private List all;
    private Map table = new HashMap();

    public SignerInformationStore(SignerInformation signerInfo) {
        this.all = new ArrayList();
        this.all = new ArrayList(1);
        this.all.add(signerInfo);
        SignerId sid = signerInfo.getSID();
        this.table.put(sid, this.all);
    }

    public SignerInformationStore(Collection<SignerInformation> signerInfos) {
        this.all = new ArrayList();
        for (SignerInformation signer : signerInfos) {
            SignerId sid = signer.getSID();
            ArrayList arrayList = (ArrayList) this.table.get(sid);
            if (arrayList == null) {
                arrayList = new ArrayList(1);
                this.table.put(sid, arrayList);
            }
            arrayList.add(signer);
        }
        this.all = new ArrayList(signerInfos);
    }

    public SignerInformation get(SignerId selector) {
        Collection<SignerInformation> signers = getSigners(selector);
        if (signers.size() == 0) {
            return null;
        }
        return signers.iterator().next();
    }

    public int size() {
        return this.all.size();
    }

    public Collection<SignerInformation> getSigners() {
        return new ArrayList(this.all);
    }

    public Collection<SignerInformation> getSigners(SignerId selector) {
        if (selector.getIssuer() != null && selector.getSubjectKeyIdentifier() != null) {
            List results = new ArrayList();
            Collection<SignerInformation> signers = getSigners(new SignerId(selector.getIssuer(), selector.getSerialNumber()));
            if (signers != null) {
                results.addAll(signers);
            }
            Collection<SignerInformation> signers2 = getSigners(new SignerId(selector.getSubjectKeyIdentifier()));
            if (signers2 != null) {
                results.addAll(signers2);
            }
            return results;
        }
        List list = (ArrayList) this.table.get(selector);
        return list == null ? new ArrayList() : new ArrayList(list);
    }

    @Override // org.spongycastle.util.Iterable, java.lang.Iterable
    public Iterator<SignerInformation> iterator() {
        return getSigners().iterator();
    }
}
