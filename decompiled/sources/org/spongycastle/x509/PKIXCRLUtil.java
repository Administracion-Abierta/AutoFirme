package org.spongycastle.x509;

import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.PKIXParameters;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.spongycastle.jce.provider.AnnotatedException;
import org.spongycastle.util.StoreException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/x509/PKIXCRLUtil.class */
class PKIXCRLUtil {
    PKIXCRLUtil() {
    }

    public Set findCRLs(X509CRLStoreSelector crlselect, ExtendedPKIXParameters paramsPKIX, Date currentDate) throws AnnotatedException {
        Set<X509CRL> initialSet = new HashSet();
        try {
            initialSet.addAll(findCRLs(crlselect, paramsPKIX.getAdditionalStores()));
            initialSet.addAll(findCRLs(crlselect, paramsPKIX.getStores()));
            initialSet.addAll(findCRLs(crlselect, paramsPKIX.getCertStores()));
            Set finalSet = new HashSet();
            Date validityDate = currentDate;
            if (paramsPKIX.getDate() != null) {
                validityDate = paramsPKIX.getDate();
            }
            for (X509CRL crl : initialSet) {
                if (crl.getNextUpdate().after(validityDate)) {
                    X509Certificate cert = crlselect.getCertificateChecking();
                    if (cert != null) {
                        if (crl.getThisUpdate().before(cert.getNotAfter())) {
                            finalSet.add(crl);
                        }
                    } else {
                        finalSet.add(crl);
                    }
                }
            }
            return finalSet;
        } catch (AnnotatedException e) {
            throw new AnnotatedException("Exception obtaining complete CRLs.", e);
        }
    }

    public Set findCRLs(X509CRLStoreSelector crlselect, PKIXParameters paramsPKIX) throws AnnotatedException {
        Set completeSet = new HashSet();
        try {
            completeSet.addAll(findCRLs(crlselect, paramsPKIX.getCertStores()));
            return completeSet;
        } catch (AnnotatedException e) {
            throw new AnnotatedException("Exception obtaining complete CRLs.", e);
        }
    }

    private final Collection findCRLs(X509CRLStoreSelector crlSelect, List crlStores) throws AnnotatedException {
        Set crls = new HashSet();
        AnnotatedException lastException = null;
        boolean foundValidStore = false;
        for (Object obj : crlStores) {
            if (obj instanceof X509Store) {
                X509Store store = (X509Store) obj;
                try {
                    crls.addAll(store.getMatches(crlSelect));
                    foundValidStore = true;
                } catch (StoreException e) {
                    lastException = new AnnotatedException("Exception searching in X.509 CRL store.", e);
                }
            } else {
                CertStore store2 = (CertStore) obj;
                try {
                    crls.addAll(store2.getCRLs(crlSelect));
                    foundValidStore = true;
                } catch (CertStoreException e2) {
                    lastException = new AnnotatedException("Exception searching in X.509 CRL store.", e2);
                }
            }
        }
        if (!foundValidStore && lastException != null) {
            throw lastException;
        }
        return crls;
    }
}
