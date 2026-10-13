package es.gob.afirma.ui.core.jse.certificateselection;

import es.gob.afirma.core.keystores.KeyUsage;
import es.gob.afirma.core.misc.AOUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.font.TextAttribute;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import javax.swing.JLabel;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/ui/core/jse/certificateselection/DefaultCertificateLine.class */
class DefaultCertificateLine extends CertificateLine {
    private static final long serialVersionUID = 5012625058876812352L;
    private static final String VERDANA_FONT_NAME = "Verdana";
    private static final Font SUBJECT_FONT = new Font(VERDANA_FONT_NAME, 1, 14);
    private static final Font DETAILS_FONT = new Font(VERDANA_FONT_NAME, 0, 11);
    private JLabel alias;
    private JLabel issuer;
    private JLabel dates;
    private JLabel propertiesLink;

    DefaultCertificateLine(String str, X509Certificate x509Certificate, boolean z) {
        super(str, x509Certificate, z);
        this.alias = null;
        this.issuer = null;
        this.dates = null;
        this.propertiesLink = null;
        createUI();
    }

    void createUI() {
        String string;
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 4;
        Component jLabel = new JLabel(CertificateIconManager.getIcon(getCertificate()));
        gridBagConstraints.insets = new Insets(2, 2, 2, 5);
        add(jLabel, gridBagConstraints);
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.gridx++;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.insets = new Insets(5, 0, 0, 5);
        this.alias = new JLabel(getFriendlyName().getText());
        if (isHighContrast()) {
            this.alias.setForeground(Color.WHITE);
        } else {
            this.alias.setForeground(Color.BLACK);
        }
        this.alias.setFont(SUBJECT_FONT);
        add(this.alias, gridBagConstraints);
        gridBagConstraints.gridy++;
        gridBagConstraints.insets = new Insets(0, 0, 0, 5);
        this.issuer = new JLabel(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.2", AOUtil.getCN(getCertificate().getIssuerX500Principal().getName()), new KeyUsage(getCertificate()).toString()));
        if (isHighContrast()) {
            this.issuer.setForeground(Color.WHITE);
        } else {
            this.issuer.setForeground(Color.BLACK);
        }
        this.issuer.setFont(DETAILS_FONT);
        add(this.issuer, gridBagConstraints);
        gridBagConstraints.gridy++;
        this.dates = new JLabel(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.3", formatDate(getCertificate().getNotBefore()), formatDate(getCertificate().getNotAfter())));
        if (isHighContrast()) {
            this.dates.setForeground(Color.WHITE);
        } else {
            this.dates.setForeground(Color.BLACK);
        }
        this.dates.setFont(DETAILS_FONT);
        add(this.dates, gridBagConstraints);
        gridBagConstraints.gridy++;
        this.propertiesLink = new JLabel(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.5"));
        if (isHighContrast()) {
            this.propertiesLink.setForeground(Color.YELLOW);
        } else {
            this.propertiesLink.setForeground(Color.BLACK);
        }
        Font font = this.propertiesLink.getFont();
        Map attributes = font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        this.propertiesLink.setFont(font.deriveFont(attributes));
        Font font2 = DETAILS_FONT;
        Map attributes2 = font2.getAttributes();
        attributes2.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        this.propertiesLink.setFont(font2.deriveFont(attributes2));
        add(this.propertiesLink, gridBagConstraints);
        String cn = AOUtil.getCN(getCertificate());
        if (CertificateUtils.isExpired(getCertificate())) {
            string = CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.31");
        } else {
            string = CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.30");
        }
        setToolTipText(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.29", cn, string));
    }

    private static String formatDate(Date date) {
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    @Override // es.gob.afirma.ui.core.jse.certificateselection.CertificateLine
    Rectangle getCertificateLinkBounds() {
        return this.propertiesLink.getBounds();
    }

    public JLabel getAlias() {
        return this.alias;
    }

    public JLabel getIssuer() {
        return this.issuer;
    }

    public JLabel getDates() {
        return this.dates;
    }

    public JLabel getPropertiesLink() {
        return this.propertiesLink;
    }
}
