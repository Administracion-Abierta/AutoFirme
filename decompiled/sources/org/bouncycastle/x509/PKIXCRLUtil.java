package org.bouncycastle.x509;

import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.PKIXParameters;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bouncycastle.jce.provider.AnnotatedException;
import org.bouncycastle.util.StoreException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/x509/PKIXCRLUtil.class */
abstract class PKIXCRLUtil {
    PKIXCRLUtil() {
    }

    static Set findCRLs(X509CRLStoreSelector x509CRLStoreSelector, PKIXParameters pKIXParameters) throws AnnotatedException {
        HashSet hashSet = new HashSet();
        try {
            findCRLs(hashSet, x509CRLStoreSelector, pKIXParameters.getCertStores());
            return hashSet;
        } catch (AnnotatedException e) {
            throw new AnnotatedException("Exception obtaining complete CRLs.", e);
        }
    }

    private static void findCRLs(HashSet hashSet, X509CRLStoreSelector x509CRLStoreSelector, List list) throws AnnotatedException {
        AnnotatedException annotatedException = null;
        boolean z = false;
        for (Object obj : list) {
            if (obj instanceof X509Store) {
                try {
                    hashSet.addAll(((X509Store) obj).getMatches(x509CRLStoreSelector));
                    z = true;
                } catch (StoreException e) {
                    annotatedException = new AnnotatedException("Exception searching in X.509 CRL store.", e);
                }
            } else {
                try {
                    hashSet.addAll(((CertStore) obj).getCRLs(x509CRLStoreSelector));
                    z = true;
                } catch (CertStoreException e2) {
                    annotatedException = new AnnotatedException("Exception searching in X.509 CRL store.", e2);
                }
            }
        }
        if (!z && annotatedException != null) {
            throw annotatedException;
        }
    }
}
