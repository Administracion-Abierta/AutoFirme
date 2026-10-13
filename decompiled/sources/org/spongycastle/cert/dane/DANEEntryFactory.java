package org.spongycastle.cert.dane;

import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.operator.DigestCalculator;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/dane/DANEEntryFactory.class */
public class DANEEntryFactory {
    private final DANEEntrySelectorFactory selectorFactory;

    public DANEEntryFactory(DigestCalculator digestCalculator) {
        this.selectorFactory = new DANEEntrySelectorFactory(digestCalculator);
    }

    public DANEEntry createEntry(String emailAddress, X509CertificateHolder certificate) throws DANEException {
        return createEntry(emailAddress, 3, certificate);
    }

    public DANEEntry createEntry(String emailAddress, int certUsage, X509CertificateHolder certificate) throws DANEException {
        if (certUsage < 0 || certUsage > 3) {
            throw new DANEException("unknown certificate usage: " + certUsage);
        }
        DANEEntrySelector entrySelector = this.selectorFactory.createSelector(emailAddress);
        byte[] flags = {(byte) certUsage, 0, 0};
        return new DANEEntry(entrySelector.getDomainName(), flags, certificate);
    }
}
