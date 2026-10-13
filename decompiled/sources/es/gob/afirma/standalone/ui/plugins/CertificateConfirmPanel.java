package es.gob.afirma.standalone.ui.plugins;

import com.aowagie.text.pdf.ColumnText;
import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.core.ui.GenericFileFilter;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/plugins/CertificateConfirmPanel.class */
public class CertificateConfirmPanel extends JPanel {
    private static final long serialVersionUID = -1501351782356550463L;

    public CertificateConfirmPanel(X509Certificate[][] x509CertificateArr) {
        setLayout(new GridBagLayout());
        setAlignmentY(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        Component jLabel = new JLabel(SimpleAfirmaMessages.getString("CertificateConfirmPanel.0"));
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (X509Certificate[] x509CertificateArr2 : x509CertificateArr) {
            String cn = AOUtil.getCN(x509CertificateArr2[0]);
            boolean zBefore = x509CertificateArr2[0].getNotAfter().before(new Date());
            JLabel jLabel2 = new JLabel();
            if (zBefore) {
                jLabel2.setText(" - " + cn + "(" + SimpleAfirmaMessages.getString("CertificateConfirmPanel.9") + ")");
            } else {
                jLabel2.setText(" - " + cn);
            }
            arrayList.add(jLabel2);
            JButton jButton = new JButton(SimpleAfirmaMessages.getString("CertificateConfirmPanel.1"));
            jButton.setIcon(new ImageIcon(getClass().getResource("/resources/certificate_16.png")));
            if (zBefore) {
                jButton.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("CertificateConfirmPanel.10", cn));
            } else {
                jButton.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("CertificateConfirmPanel.2", cn));
            }
            jButton.addActionListener(new DownloadCertAction(x509CertificateArr2[0]));
            arrayList2.add(jButton);
        }
        Component jLabel3 = new JLabel(SimpleAfirmaMessages.getString("CertificateConfirmPanel.3"));
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.gridy = 0;
        add(jLabel, gridBagConstraints);
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        for (int i = 0; i < arrayList.size(); i++) {
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy++;
            gridBagConstraints.weightx = 1.0d;
            add((Component) arrayList.get(i), gridBagConstraints);
            gridBagConstraints.gridx = 1;
            gridBagConstraints.weightx = 0.0d;
            add((Component) arrayList2.get(i), gridBagConstraints);
        }
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy++;
        add(jLabel3, gridBagConstraints);
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/plugins/CertificateConfirmPanel$DownloadCertAction.class */
    class DownloadCertAction implements ActionListener {
        private final X509Certificate cert;

        public DownloadCertAction(X509Certificate x509Certificate) {
            this.cert = x509Certificate;
        }

        public void actionPerformed(ActionEvent actionEvent) {
            try {
                download();
            } catch (AOCancelledOperationException e) {
            } catch (Exception e2) {
                JOptionPane.showMessageDialog((Component) null, SimpleAfirmaMessages.getString("CertificateConfirmPanel.4"), SimpleAfirmaMessages.getString("CertificateConfirmPanel.5"), 0);
            }
        }

        private void download() throws IOException, CertificateEncodingException {
            AOUIFactory.getSaveDataToFile(this.cert.getEncoded(), SimpleAfirmaMessages.getString("CertificateConfirmPanel.7"), null, SimpleAfirmaMessages.getString("CertificateConfirmPanel.8"), Collections.singletonList(new GenericFileFilter(new String[]{"cer"}, SimpleAfirmaMessages.getString("CertificateConfirmPanel.6"))), this);
        }
    }
}
