package es.gob.afirma.standalone.crypto;

import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import java.security.cert.X509Certificate;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/crypto/CertificateInfo.class */
public final class CertificateInfo {
    private final Icon icon;
    private final String iconTooltip;
    private String holderName;
    private String issuerName;

    CertificateInfo(X509Certificate x509Certificate, String str, Icon icon, String str2) {
        if (str != null && !str.isEmpty()) {
            this.holderName = str;
        } else if (x509Certificate != null) {
            this.holderName = AOUtil.getCN(x509Certificate);
        } else {
            this.holderName = null;
        }
        if (x509Certificate != null) {
            this.issuerName = AOUtil.getCN(x509Certificate.getIssuerX500Principal().toString());
        } else {
            this.issuerName = null;
        }
        if (icon != null) {
            this.icon = icon;
        } else {
            this.icon = new ImageIcon(getClass().getResource("/resources/default_cert_ico.png"));
        }
        if (str2 != null) {
            this.iconTooltip = str2;
        } else {
            this.iconTooltip = SimpleAfirmaMessages.getString("CertificateInfo.3");
        }
    }

    public Icon getIcon() {
        return this.icon;
    }

    public String getIconTooltip() {
        return this.iconTooltip;
    }

    public String getHolderName() {
        return this.holderName;
    }

    public String getIssuerName() {
        return this.issuerName;
    }
}
