package org.spongycastle.jce.provider;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.spongycastle.jce.X509LDAPCertStoreParameters;
import org.spongycastle.util.Selector;
import org.spongycastle.util.StoreException;
import org.spongycastle.x509.X509CertPairStoreSelector;
import org.spongycastle.x509.X509CertStoreSelector;
import org.spongycastle.x509.X509CertificatePair;
import org.spongycastle.x509.X509StoreParameters;
import org.spongycastle.x509.X509StoreSpi;
import org.spongycastle.x509.util.LDAPStoreHelper;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jce/provider/X509StoreLDAPCerts.class */
public class X509StoreLDAPCerts extends X509StoreSpi {
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
        if (!(selector instanceof X509CertStoreSelector)) {
            return Collections.EMPTY_SET;
        }
        X509CertStoreSelector xselector = (X509CertStoreSelector) selector;
        Set set = new HashSet();
        if (xselector.getBasicConstraints() > 0) {
            set.addAll(this.helper.getCACertificates(xselector));
            set.addAll(getCertificatesFromCrossCertificatePairs(xselector));
        } else if (xselector.getBasicConstraints() == -2) {
            set.addAll(this.helper.getUserCertificates(xselector));
        } else {
            set.addAll(this.helper.getUserCertificates(xselector));
            set.addAll(this.helper.getCACertificates(xselector));
            set.addAll(getCertificatesFromCrossCertificatePairs(xselector));
        }
        return set;
    }

    private Collection getCertificatesFromCrossCertificatePairs(X509CertStoreSelector xselector) throws StoreException {
        Set set = new HashSet();
        X509CertPairStoreSelector ps = new X509CertPairStoreSelector();
        ps.setForwardSelector(xselector);
        ps.setReverseSelector(new X509CertStoreSelector());
        Set<X509CertificatePair> crossCerts = new HashSet(this.helper.getCrossCertificatePairs(ps));
        HashSet hashSet = new HashSet();
        HashSet hashSet2 = new HashSet();
        for (X509CertificatePair pair : crossCerts) {
            if (pair.getForward() != null) {
                hashSet.add(pair.getForward());
            }
            if (pair.getReverse() != null) {
                hashSet2.add(pair.getReverse());
            }
        }
        set.addAll(hashSet);
        set.addAll(hashSet2);
        return set;
    }
}
