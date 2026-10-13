package es.gob.afirma.ui.core.jse.certificateselection;

import java.awt.Rectangle;
import java.security.cert.X509Certificate;
import javax.swing.JLabel;
import javax.swing.JPanel;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/ui/core/jse/certificateselection/CertificateLine.class */
public abstract class CertificateLine extends JPanel {
    private static final long serialVersionUID = 1;
    private final X509Certificate certificate;
    private final JLabel friendlyName;
    private boolean highContrast;

    abstract Rectangle getCertificateLinkBounds();

    CertificateLine(String str, X509Certificate x509Certificate, boolean z) {
        this.friendlyName = new JLabel(str);
        this.certificate = x509Certificate;
        this.highContrast = z;
    }

    X509Certificate getCertificate() {
        return this.certificate;
    }

    JLabel getFriendlyName() {
        return this.friendlyName;
    }

    public boolean isHighContrast() {
        return this.highContrast;
    }

    public void setHighContrast(boolean z) {
        this.highContrast = z;
    }

    public String toString() {
        return this.friendlyName.getText();
    }
}
