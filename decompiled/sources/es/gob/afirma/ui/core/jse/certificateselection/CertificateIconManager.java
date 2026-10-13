package es.gob.afirma.ui.core.jse.certificateselection;

import java.security.cert.X509Certificate;
import java.util.Date;
import javax.swing.ImageIcon;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/ui/core/jse/certificateselection/CertificateIconManager.class */
final class CertificateIconManager {
    private static final long EXPIRITY_WARNING_LEVEL = 630000000;
    private static final ImageIcon ICON_ACCV_NORMAL = new ImageIcon(CertificateSelectionPanel.class.getClassLoader().getResource("resources/accvicon.png"));
    private static final ImageIcon ICON_ACCV_WARNING = new ImageIcon(CertificateSelectionPanel.class.getClassLoader().getResource("resources/accvicon_w.png"));
    private static final ImageIcon ICON_ACCV_ERROR = new ImageIcon(CertificateSelectionPanel.class.getClassLoader().getResource("resources/accvicon_e.png"));
    private static final ImageIcon ICON_CNP_NORMAL = new ImageIcon(CertificateSelectionPanel.class.getClassLoader().getResource("resources/cnpicon.png"));
    private static final ImageIcon ICON_CNP_WARNING = new ImageIcon(CertificateSelectionPanel.class.getClassLoader().getResource("resources/cnpicon_w.png"));
    private static final ImageIcon ICON_CNP_ERROR = new ImageIcon(CertificateSelectionPanel.class.getClassLoader().getResource("resources/cnpicon_e.png"));
    private static final ImageIcon ICON_DNIE_NORMAL = new ImageIcon(CertificateSelectionPanel.class.getClassLoader().getResource("resources/dnieicon.png"));
    private static final ImageIcon ICON_DNIE_WARNING = new ImageIcon(CertificateSelectionPanel.class.getClassLoader().getResource("resources/dnieicon_w.png"));
    private static final ImageIcon ICON_DNIE_ERROR = new ImageIcon(CertificateSelectionPanel.class.getClassLoader().getResource("resources/dnieicon_e.png"));
    private static final ImageIcon ICON_OTHER_NORMAL = new ImageIcon(CertificateSelectionPanel.class.getClassLoader().getResource("resources/certicon.png"));
    private static final ImageIcon ICON_OTHER_WARNING = new ImageIcon(CertificateSelectionPanel.class.getClassLoader().getResource("resources/certicon_w.png"));
    private static final ImageIcon ICON_OTHER_ERROR = new ImageIcon(CertificateSelectionPanel.class.getClassLoader().getResource("resources/certicon_e.png"));
    private static final ImageIcon ICON_FNMT_NORMAL = new ImageIcon(CertificateSelectionPanel.class.getClassLoader().getResource("resources/fnmticon.png"));
    private static final ImageIcon ICON_FNMT_WARNING = new ImageIcon(CertificateSelectionPanel.class.getClassLoader().getResource("resources/fnmticon_w.png"));
    private static final ImageIcon ICON_FNMT_ERROR = new ImageIcon(CertificateSelectionPanel.class.getClassLoader().getResource("resources/fnmticon_e.png"));

    CertificateIconManager() {
    }

    static {
        ICON_ACCV_NORMAL.setDescription(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.22"));
        ICON_ACCV_NORMAL.getAccessibleContext().setAccessibleDescription(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.22"));
        ICON_ACCV_WARNING.setDescription(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.23"));
        ICON_ACCV_WARNING.getAccessibleContext().setAccessibleDescription(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.23"));
        ICON_ACCV_ERROR.setDescription(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.24"));
        ICON_ACCV_ERROR.getAccessibleContext().setAccessibleDescription(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.24"));
        ICON_DNIE_NORMAL.setDescription(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.4"));
        ICON_DNIE_NORMAL.getAccessibleContext().setAccessibleDescription(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.4"));
        ICON_DNIE_WARNING.setDescription(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.7"));
        ICON_DNIE_WARNING.getAccessibleContext().setAccessibleDescription(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.7"));
        ICON_DNIE_ERROR.setDescription(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.9"));
        ICON_DNIE_ERROR.getAccessibleContext().setAccessibleDescription(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.9"));
        ICON_CNP_NORMAL.setDescription(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.16"));
        ICON_CNP_NORMAL.getAccessibleContext().setAccessibleDescription(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.16"));
        ICON_CNP_WARNING.setDescription(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.17"));
        ICON_CNP_WARNING.getAccessibleContext().setAccessibleDescription(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.17"));
        ICON_CNP_ERROR.setDescription(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.18"));
        ICON_CNP_ERROR.getAccessibleContext().setAccessibleDescription(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.18"));
        ICON_OTHER_NORMAL.setDescription(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.11"));
        ICON_OTHER_NORMAL.getAccessibleContext().setAccessibleDescription(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.11"));
        ICON_OTHER_WARNING.setDescription(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.13"));
        ICON_OTHER_WARNING.getAccessibleContext().setAccessibleDescription(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.13"));
        ICON_OTHER_ERROR.setDescription(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.15"));
        ICON_OTHER_ERROR.getAccessibleContext().setAccessibleDescription(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.15"));
        ICON_FNMT_NORMAL.setDescription(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.19"));
        ICON_FNMT_NORMAL.getAccessibleContext().setAccessibleDescription(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.19"));
        ICON_FNMT_WARNING.setDescription(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.20"));
        ICON_FNMT_WARNING.getAccessibleContext().setAccessibleDescription(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.20"));
        ICON_FNMT_ERROR.setDescription(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.21"));
        ICON_FNMT_ERROR.getAccessibleContext().setAccessibleDescription(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.21"));
    }

    private static boolean isAccvCert(X509Certificate x509Certificate) {
        if (x509Certificate == null) {
            return false;
        }
        String upperCase = x509Certificate.getIssuerX500Principal().toString().toUpperCase();
        return upperCase.contains("O=Generalitat Valenciana") && upperCase.contains("OU=PKIGVA");
    }

    private static boolean isDNIeCert(X509Certificate x509Certificate) {
        if (x509Certificate == null) {
            return false;
        }
        String upperCase = x509Certificate.getIssuerX500Principal().toString().toUpperCase();
        return upperCase.contains("O=DIRECCION GENERAL DE LA POLICIA") && upperCase.contains("OU=DNIE");
    }

    private static boolean isCnpCert(X509Certificate x509Certificate) {
        if (x509Certificate == null) {
            return false;
        }
        String upperCase = x509Certificate.getIssuerX500Principal().toString().toUpperCase();
        return upperCase.contains("O=DIRECCION GENERAL DE LA POLICIA") && upperCase.contains("OU=CNP");
    }

    private static boolean isFnmtCert(X509Certificate x509Certificate) {
        if (x509Certificate == null) {
            return false;
        }
        return x509Certificate.getIssuerX500Principal().toString().toUpperCase().contains("O=FNMT");
    }

    static ImageIcon getNormalIcon(X509Certificate x509Certificate) {
        if (isAccvCert(x509Certificate)) {
            return ICON_ACCV_NORMAL;
        }
        if (isDNIeCert(x509Certificate)) {
            return ICON_DNIE_NORMAL;
        }
        if (isCnpCert(x509Certificate)) {
            return ICON_CNP_NORMAL;
        }
        if (isFnmtCert(x509Certificate)) {
            return ICON_FNMT_NORMAL;
        }
        return ICON_OTHER_NORMAL;
    }

    static ImageIcon getWarningIcon(X509Certificate x509Certificate) {
        if (isAccvCert(x509Certificate)) {
            return ICON_ACCV_WARNING;
        }
        if (isDNIeCert(x509Certificate)) {
            return ICON_DNIE_WARNING;
        }
        if (isCnpCert(x509Certificate)) {
            return ICON_CNP_WARNING;
        }
        if (isFnmtCert(x509Certificate)) {
            return ICON_FNMT_WARNING;
        }
        return ICON_OTHER_WARNING;
    }

    static ImageIcon getExpiredIcon(X509Certificate x509Certificate) {
        if (isAccvCert(x509Certificate)) {
            return ICON_ACCV_ERROR;
        }
        if (isDNIeCert(x509Certificate)) {
            return ICON_DNIE_ERROR;
        }
        if (isCnpCert(x509Certificate)) {
            return ICON_CNP_ERROR;
        }
        if (isFnmtCert(x509Certificate)) {
            return ICON_FNMT_ERROR;
        }
        return ICON_OTHER_ERROR;
    }

    static ImageIcon getIcon(X509Certificate x509Certificate) {
        long time = x509Certificate.getNotAfter().getTime();
        long time2 = new Date().getTime();
        if (time2 >= time || time2 <= x509Certificate.getNotBefore().getTime()) {
            return getExpiredIcon(x509Certificate);
        }
        if (time - time2 < EXPIRITY_WARNING_LEVEL) {
            return getWarningIcon(x509Certificate);
        }
        return getNormalIcon(x509Certificate);
    }
}
