package es.gob.afirma.keystores;

import es.gob.afirma.core.keystores.KeyStoreManager;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/CertificateFilter.class */
public abstract class CertificateFilter {
    public abstract boolean matches(X509Certificate x509Certificate);

    public String[] matches(String[] strArr, KeyStoreManager keyStoreManager) {
        ArrayList arrayList = new ArrayList();
        for (String str : strArr) {
            if (matches(keyStoreManager.getCertificate(str))) {
                arrayList.add(str);
            }
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }
}
