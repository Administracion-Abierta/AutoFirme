package es.gob.afirma.ui.core.jse.certificateselection;

import java.security.cert.X509Certificate;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/ui/core/jse/certificateselection/CertificateLineFactory.class */
public class CertificateLineFactory {
    private final CertificateLineView view;

    private CertificateLineFactory(CertificateLineView certificateLineView) {
        this.view = certificateLineView;
    }

    public static CertificateLineFactory newInstance(CertificateLineView certificateLineView) {
        return new CertificateLineFactory(certificateLineView);
    }

    public CertificateLine buildCertificateLine(String str, X509Certificate x509Certificate, boolean z) {
        CertificateLine defaultCertificateLine;
        switch (this.view) {
            case REPRESENTATIVE:
                defaultCertificateLine = new RepresentativeCertificateLine(str, x509Certificate, z);
                break;
            case PSEUDONYM:
                defaultCertificateLine = new PseudonymCertificateLine(str, x509Certificate, z);
                break;
            case PERSONAL:
            default:
                defaultCertificateLine = new DefaultCertificateLine(str, x509Certificate, z);
                break;
        }
        defaultCertificateLine.setFocusable(true);
        return defaultCertificateLine;
    }
}
