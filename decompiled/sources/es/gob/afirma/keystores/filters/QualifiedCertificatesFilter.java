package es.gob.afirma.keystores.filters;

import es.gob.afirma.core.keystores.KeyStoreManager;
import es.gob.afirma.keystores.CertificateFilter;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/filters/QualifiedCertificatesFilter.class */
public final class QualifiedCertificatesFilter extends CertificateFilter {
    private final String serialNumber;

    public QualifiedCertificatesFilter(String str) {
        this.serialNumber = prepareSerialNumber(str);
    }

    @Override // es.gob.afirma.keystores.CertificateFilter
    public boolean matches(X509Certificate x509Certificate) {
        String certificateSN = getCertificateSN(x509Certificate);
        if (certificateSN != null) {
            return prepareSerialNumber(certificateSN).equalsIgnoreCase(this.serialNumber);
        }
        return false;
    }

    @Override // es.gob.afirma.keystores.CertificateFilter
    public String[] matches(String[] strArr, KeyStoreManager keyStoreManager) {
        ArrayList arrayList = new ArrayList();
        for (String str : strArr) {
            X509Certificate certificate = keyStoreManager.getCertificate(str);
            if (certificate == null) {
                Logger.getLogger("es.gob.afirma").warning("No se pudo recuperar el certificado: " + str);
            } else {
                try {
                    if (matches(certificate)) {
                        if (isSignatureCert(certificate)) {
                            arrayList.add(str);
                        } else {
                            String strSearchQualifiedSignatureCertificate = searchQualifiedSignatureCertificate(certificate, keyStoreManager, strArr);
                            if (strSearchQualifiedSignatureCertificate == null) {
                                if (!new AuthenticationDNIeFilter().matches(certificate)) {
                                    arrayList.add(str);
                                }
                            } else {
                                arrayList.add(strSearchQualifiedSignatureCertificate);
                            }
                        }
                    }
                } catch (Exception e) {
                    Logger.getLogger("es.gob.afirma").warning("Error en la verificacion del certificado '" + certificate.getSerialNumber() + "': " + e);
                }
            }
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    private static String searchQualifiedSignatureCertificate(X509Certificate x509Certificate, KeyStoreManager keyStoreManager, String[] strArr) {
        boolean zEquals;
        boolean zEquals2;
        boolean zEquals3;
        for (String str : strArr) {
            X509Certificate certificate = keyStoreManager.getCertificate(str);
            if (!x509Certificate.getSerialNumber().equals(certificate.getSerialNumber())) {
                if (x509Certificate.getIssuerDN() == null) {
                    zEquals = certificate.getIssuerDN() == null;
                } else {
                    zEquals = x509Certificate.getIssuerDN().equals(certificate.getIssuerDN());
                }
                boolean z = zEquals;
                if (FilterUtils.getSubjectSN(x509Certificate) == null) {
                    zEquals2 = FilterUtils.getSubjectSN(certificate) == null;
                } else {
                    zEquals2 = FilterUtils.getSubjectSN(x509Certificate).equals(FilterUtils.getSubjectSN(certificate));
                }
                boolean z2 = zEquals2;
                if (getExpiredDate(x509Certificate) == null) {
                    zEquals3 = getExpiredDate(certificate) == null;
                } else {
                    zEquals3 = getExpiredDate(x509Certificate).equals(getExpiredDate(certificate));
                }
                boolean z3 = zEquals3;
                if (isSignatureCert(certificate) && z && z2 && z3) {
                    Logger.getLogger("es.gob.afirma").info("Se selecciona un certificado pareja de firma del certificado del numero de serie indicado");
                    return str;
                }
            }
        }
        return null;
    }

    private static boolean isSignatureCert(X509Certificate x509Certificate) {
        if (x509Certificate.getKeyUsage() == null) {
            return false;
        }
        return checkKeyUsages(x509Certificate.getKeyUsage(), new KeyUsagesPattern(x509Certificate.getIssuerDN()).getSignaturePattern());
    }

    private static boolean checkKeyUsages(boolean[] zArr, Boolean[] boolArr) {
        for (int i = 0; i < boolArr.length; i++) {
            if (boolArr[i] != null && boolArr[i].booleanValue() != zArr[i]) {
                return false;
            }
        }
        return true;
    }

    private static String getExpiredDate(X509Certificate x509Certificate) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(x509Certificate.getNotAfter());
    }

    private static String getCertificateSN(X509Certificate x509Certificate) {
        if (x509Certificate.getSerialNumber() == null) {
            return null;
        }
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
