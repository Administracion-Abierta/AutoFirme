package es.gob.afirma.ui.core.jse.certificateselection;

import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.Platform;
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
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/ui/core/jse/certificateselection/RepresentativeCertificateLine.class */
class RepresentativeCertificateLine extends CertificateLine {
    private static final long serialVersionUID = 5012625058876812352L;
    private static final String VERDANA_FONT_NAME = "Verdana";
    private static final Font SUBJECT_FONT = new Font(VERDANA_FONT_NAME, 1, 14);
    private static final Font DETAILS_FONT = new Font(VERDANA_FONT_NAME, 0, 11);
    private JLabel organizationLabel;
    private JLabel agentLabel;
    private JLabel dates;
    private JLabel propertiesLink;

    RepresentativeCertificateLine(String str, X509Certificate x509Certificate, boolean z) {
        super(str, x509Certificate, z);
        this.organizationLabel = null;
        this.agentLabel = null;
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
        String rDNvalue = new PrincipalStructure(getCertificate().getSubjectX500Principal()).getRDNvalue(PDAnnotationLink.HIGHLIGHT_MODE_OUTLINE);
        this.organizationLabel = new JLabel();
        if (rDNvalue != null) {
            this.organizationLabel.setText(rDNvalue);
        } else {
            this.organizationLabel.setText(getFriendlyName().getText());
        }
        this.organizationLabel.setFont(SUBJECT_FONT);
        if (isHighContrast()) {
            this.organizationLabel.setForeground(Color.WHITE);
        } else {
            this.organizationLabel.setForeground(Color.BLACK);
        }
        add(this.organizationLabel, gridBagConstraints);
        gridBagConstraints.gridy++;
        gridBagConstraints.insets = new Insets(0, 0, 0, 5);
        String cn = AOUtil.getCN(getCertificate());
        this.agentLabel = new JLabel(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.28", cn));
        this.agentLabel.setFont(DETAILS_FONT);
        if (isHighContrast()) {
            this.agentLabel.setForeground(Color.WHITE);
        } else {
            this.agentLabel.setForeground(Color.BLACK);
        }
        add(this.agentLabel, gridBagConstraints);
        gridBagConstraints.gridy++;
        this.dates = new JLabel(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.3", formatDate(getCertificate().getNotBefore()), formatDate(getCertificate().getNotAfter())));
        this.dates.setFont(DETAILS_FONT);
        if (isHighContrast()) {
            this.dates.setForeground(Color.WHITE);
        } else {
            this.dates.setForeground(Color.BLACK);
        }
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
        if (!Platform.OS.MACOSX.equals(Platform.getOS())) {
            Font font2 = DETAILS_FONT;
            Map attributes2 = font2.getAttributes();
            attributes2.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            this.propertiesLink.setFont(font2.deriveFont(attributes2));
            add(this.propertiesLink, gridBagConstraints);
        }
        if (CertificateUtils.isExpired(getCertificate())) {
            string = CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.31");
        } else {
            string = CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.30");
        }
        setToolTipText(CertificateSelectionDialogMessages.getString("CertificateSelectionPanel.32", this.organizationLabel.getText(), cn, string));
    }

    private static String formatDate(Date date) {
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    @Override // es.gob.afirma.ui.core.jse.certificateselection.CertificateLine
    Rectangle getCertificateLinkBounds() {
        return this.propertiesLink.getBounds();
    }

    public JLabel getOrganizationLabel() {
        return this.organizationLabel;
    }

    public JLabel getAgentLabel() {
        return this.agentLabel;
    }

    public JLabel getDates() {
        return this.dates;
    }

    public JLabel getPropertiesLink() {
        return this.propertiesLink;
    }
}
