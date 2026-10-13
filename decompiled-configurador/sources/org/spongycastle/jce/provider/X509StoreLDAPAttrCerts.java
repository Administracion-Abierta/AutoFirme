package org.spongycastle.jce.provider;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.spongycastle.jce.X509LDAPCertStoreParameters;
import org.spongycastle.util.Selector;
import org.spongycastle.util.StoreException;
import org.spongycastle.x509.X509AttributeCertStoreSelector;
import org.spongycastle.x509.X509StoreParameters;
import org.spongycastle.x509.X509StoreSpi;
import org.spongycastle.x509.util.LDAPStoreHelper;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jce/provider/X509StoreLDAPAttrCerts.class */
public class X509StoreLDAPAttrCerts extends X509StoreSpi {
    private LDAPStoreHelper helper;

    @Override // org.spongycastle.x509.X509StoreSpi
    public void engineInit(X509StoreParameters parameters) {
        if (!(parameters instanceof X509LDAPCertStoreParameters)) {
            throw new IllegalArgumentException("Initialization parameters must be an instance of " + X509LDAPCertStoreParameters.class.getName() + ".");
        }
        this.helper = new LDAPStoreHelper((X509LDAPCertStoreParameters) parameters);
    }

    @Override // org.spongycastle.x509.X509StoreSpi
    public Collection engineGetMatches(Selector selector) throws StoreException {
        if (!(selector instanceof X509AttributeCertStoreSelector)) {
            return Collections.EMPTY_SET;
        }
        X509AttributeCertStoreSelector xselector = (X509AttributeCertStoreSelector) selector;
        Set set = new HashSet();
        set.addAll(this.helper.getAACertificates(xselector));
        set.addAll(this.helper.getAttributeCertificateAttributes(xselector));
        set.addAll(this.helper.getAttributeDescriptorCertificates(xselector));
        return set;
    }
}
