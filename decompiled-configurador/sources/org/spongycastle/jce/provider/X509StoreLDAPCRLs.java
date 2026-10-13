package org.spongycastle.jce.provider;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.spongycastle.jce.X509LDAPCertStoreParameters;
import org.spongycastle.util.Selector;
import org.spongycastle.util.StoreException;
import org.spongycastle.x509.X509CRLStoreSelector;
import org.spongycastle.x509.X509StoreParameters;
import org.spongycastle.x509.X509StoreSpi;
import org.spongycastle.x509.util.LDAPStoreHelper;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jce/provider/X509StoreLDAPCRLs.class */
public class X509StoreLDAPCRLs extends X509StoreSpi {
    private LDAPStoreHelper helper;

    @Override // org.spongycastle.x509.X509StoreSpi
    public void engineInit(X509StoreParameters params) {
        if (!(params instanceof X509LDAPCertStoreParameters)) {
            throw new IllegalArgumentException("Initialization parameters must be an instance of " + X509LDAPCertStoreParameters.class.getName() + ".");
        }
        this.helper = new LDAPStoreHelper((X509LDAPCertStoreParameters) params);
    }

    @Override // org.spongycastle.x509.X509StoreSpi
    public Collection engineGetMatches(Selector selector) throws StoreException {
        if (!(selector instanceof X509CRLStoreSelector)) {
            return Collections.EMPTY_SET;
        }
        X509CRLStoreSelector xselector = (X509CRLStoreSelector) selector;
        Set set = new HashSet();
        if (xselector.isDeltaCRLIndicatorEnabled()) {
            set.addAll(this.helper.getDeltaCertificateRevocationLists(xselector));
        } else {
            set.addAll(this.helper.getDeltaCertificateRevocationLists(xselector));
            set.addAll(this.helper.getAttributeAuthorityRevocationLists(xselector));
            set.addAll(this.helper.getAttributeCertificateRevocationLists(xselector));
            set.addAll(this.helper.getAuthorityRevocationLists(xselector));
            set.addAll(this.helper.getCertificateRevocationLists(xselector));
        }
        return set;
    }
}
