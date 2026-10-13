package es.gob.afirma.keystores.filters;

import com.twelvemonkeys.lang.DateUtil;
import es.gob.afirma.core.keystores.KeyStoreManager;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.keystores.CertificateFilter;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/filters/PseudonymFilter.class */
public final class PseudonymFilter extends CertificateFilter {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    public static final String VALUE_PSEUDONYM_AND_OTHERS = "andothers";
    public static final String VALUE_ONLY = "only";
    private final String value;

    public PseudonymFilter() {
        this.value = VALUE_PSEUDONYM_AND_OTHERS;
    }

    public PseudonymFilter(String str) {
        this.value = str;
    }

    @Override // es.gob.afirma.keystores.CertificateFilter
    public boolean matches(X509Certificate x509Certificate) {
        return AOUtil.isPseudonymCert(x509Certificate);
    }

    @Override // es.gob.afirma.keystores.CertificateFilter
    public String[] matches(String[] strArr, KeyStoreManager keyStoreManager) {
        String[] certsWithPseudonymOrOthers;
        if (VALUE_ONLY.equalsIgnoreCase(this.value)) {
            certsWithPseudonymOrOthers = getOnlyCertsWithPseudonymExtension(strArr, keyStoreManager);
        } else {
            certsWithPseudonymOrOthers = getCertsWithPseudonymOrOthers(strArr, keyStoreManager);
        }
        return certsWithPseudonymOrOthers;
    }

    private String[] getOnlyCertsWithPseudonymExtension(String[] strArr, KeyStoreManager keyStoreManager) {
        ArrayList arrayList = new ArrayList();
        for (String str : strArr) {
            if (matches(keyStoreManager.getCertificate(str))) {
                arrayList.add(str);
            }
        }
        return (String[]) arrayList.toArray(new String[0]);
    }

    private String[] getCertsWithPseudonymOrOthers(String[] strArr, KeyStoreManager keyStoreManager) {
        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap(strArr.length);
        ConcurrentHashMap concurrentHashMap2 = new ConcurrentHashMap();
        for (String str : strArr) {
            X509Certificate certificate = keyStoreManager.getCertificate(str);
            if (matches(certificate)) {
                concurrentHashMap2.put(str, certificate);
            } else {
                concurrentHashMap.put(str, certificate);
            }
        }
        if (concurrentHashMap2.isEmpty()) {
            return strArr;
        }
        Iterator it = concurrentHashMap2.keySet().iterator();
        while (it.hasNext()) {
            X509Certificate x509Certificate = (X509Certificate) concurrentHashMap2.get((String) it.next());
            for (String str2 : concurrentHashMap.keySet()) {
                if (isPseudonymFor(x509Certificate, (X509Certificate) concurrentHashMap.get(str2))) {
                    LOGGER.info(String.format("Se omite el certificado '%s' por haber un certificado de seudonimo equivalente", str2));
                    concurrentHashMap.remove(str2);
                }
            }
        }
        concurrentHashMap2.putAll(concurrentHashMap);
        return (String[]) concurrentHashMap2.keySet().toArray(new String[0]);
    }

    private static boolean isPseudonymFor(X509Certificate x509Certificate, X509Certificate x509Certificate2) {
        return x509Certificate != null && x509Certificate2 != null && x509Certificate.getIssuerX500Principal().equals(x509Certificate2.getIssuerX500Principal()) && Arrays.equals(x509Certificate.getKeyUsage(), x509Certificate2.getKeyUsage()) && Math.abs(x509Certificate.getNotAfter().getTime() - x509Certificate2.getNotAfter().getTime()) < DateUtil.MINUTE;
    }
}
