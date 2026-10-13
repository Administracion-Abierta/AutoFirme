package es.gob.afirma.keystores.filters;

import es.gob.afirma.keystores.CertificateFilter;
import java.security.cert.X509Certificate;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/filters/TextContainedCertificateFilter.class */
public final class TextContainedCertificateFilter extends CertificateFilter {
    private final String[] subjectTexts;
    private final String[] issuerTexts;

    public TextContainedCertificateFilter(String[] strArr, String[] strArr2) {
        if (strArr == null && strArr2 == null) {
            throw new IllegalArgumentException("Al menos uno de los criterios de filtrado debe no ser nulo");
        }
        this.subjectTexts = strArr != null ? (String[]) strArr.clone() : null;
        this.issuerTexts = strArr2 != null ? (String[]) strArr2.clone() : null;
    }

    @Override // es.gob.afirma.keystores.CertificateFilter
    public boolean matches(X509Certificate x509Certificate) {
        if (this.subjectTexts != null) {
            String lowerCase = x509Certificate.getSubjectX500Principal().toString().toLowerCase();
            for (String str : this.subjectTexts) {
                if (str != null && !lowerCase.contains(str.toLowerCase())) {
                    return false;
                }
            }
        }
        if (this.issuerTexts != null) {
            String lowerCase2 = x509Certificate.getIssuerX500Principal().toString().toLowerCase();
            for (String str2 : this.issuerTexts) {
                if (str2 != null && !lowerCase2.contains(str2.toLowerCase())) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }
}
