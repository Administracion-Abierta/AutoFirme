package es.gob.afirma.standalone.ui;

import java.security.cert.X509Certificate;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/CertInfoLabelLinkImpl.class */
public final class CertInfoLabelLinkImpl implements LabelLinkListener {
    private final X509Certificate cert;

    public CertInfoLabelLinkImpl(X509Certificate x509Certificate) {
        this.cert = x509Certificate;
    }

    @Override // es.gob.afirma.standalone.ui.LabelLinkListener
    public void openLink() {
        SignDataPanel.openCertificate(this.cert);
    }
}
