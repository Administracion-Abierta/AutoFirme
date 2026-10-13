package es.gob.afirma.keystores.filters;

import es.gob.afirma.core.keystores.KeyStoreManager;
import es.gob.afirma.keystores.CertificateFilter;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/filters/SSLFilter.class */
public final class SSLFilter extends CertificateFilter {
    private final String serialNumber;
    private final AuthenticationDNIeFilter authenticationDnieCertFilter = new AuthenticationDNIeFilter();
    private final SignatureDNIeFilter signatureDnieCertFilter = new SignatureDNIeFilter();

    public SSLFilter(String str) {
        this.serialNumber = prepareSerialNumber(str);
    }

    @Override // es.gob.afirma.keystores.CertificateFilter
    public boolean matches(X509Certificate x509Certificate) {
        return prepareSerialNumber(getCertificateSN(x509Certificate)).equalsIgnoreCase(this.serialNumber);
    }

    @Override // es.gob.afirma.keystores.CertificateFilter
    public String[] matches(String[] strArr, KeyStoreManager keyStoreManager) {
        X509Certificate[] x509CertificateArr = new X509Certificate[strArr.length];
        for (int i = 0; i < strArr.length; i++) {
            x509CertificateArr[i] = keyStoreManager.getCertificate(strArr[i]);
        }
        ArrayList arrayList = new ArrayList();
        for (int i2 = 0; i2 < strArr.length; i2++) {
            X509Certificate certificate = keyStoreManager.getCertificate(strArr[i2]);
            try {
                if (matches(certificate)) {
                    if (isAuthenticationDnieCert(certificate)) {
                        String associatedCertAlias = getAssociatedCertAlias(keyStoreManager, certificate, strArr, i2);
                        if (associatedCertAlias != null) {
                            arrayList.add(associatedCertAlias);
                        }
                    } else {
                        arrayList.add(strArr[i2]);
                    }
                }
            } catch (Exception e) {
                Logger.getLogger("es.gob.afirma").warning("Error en la verificacion del certificado '" + certificate.getSerialNumber() + "': " + e);
            }
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    private boolean isAuthenticationDnieCert(X509Certificate x509Certificate) {
        return this.authenticationDnieCertFilter.matches(x509Certificate);
    }

    private boolean isSignatureDnieCert(X509Certificate x509Certificate) {
        return this.signatureDnieCertFilter.matches(x509Certificate);
    }

    private String getAssociatedCertAlias(KeyStoreManager keyStoreManager, X509Certificate x509Certificate, String[] strArr, int i) {
        for (int i2 = 0; i2 < strArr.length; i2++) {
            if (i != i2) {
                X509Certificate certificate = keyStoreManager.getCertificate(strArr[i2]);
                if (isSignatureDnieCert(certificate) && FilterUtils.getSubjectSN(certificate) != null && FilterUtils.getSubjectSN(certificate).equalsIgnoreCase(FilterUtils.getSubjectSN(x509Certificate)) && getExpiredDate(certificate).equals(getExpiredDate(x509Certificate))) {
                    return strArr[i2];
                }
            }
        }
        return null;
    }

    private static String getExpiredDate(X509Certificate x509Certificate) {
        return new SimpleDateFormat("yyyy-MM-dd").format(x509Certificate.getNotAfter());
    }

    private static String getCertificateSN(X509Certificate x509Certificate) {
        return FilterUtils.bigIntegerToHex(x509Certificate.getSerialNumber());
    }

    private static String prepareSerialNumber(String str) {
        String strReplace = str.trim().replace(" ", "").replace("#", "");
        int i = 0;
        while (i < strReplace.length() && strReplace.charAt(i) == '0') {
            i++;
        }
        return strReplace.substring(i);
    }
}
