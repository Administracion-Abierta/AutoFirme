package es.gob.afirma.standalone.ui.preferences;

import com.sun.jna.platform.win32.WinError;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.http.SslSecurityManager;
import es.gob.afirma.core.misc.http.TrustStoreManager;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.standalone.DesktopUtil;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import es.gob.afirma.ui.core.jse.certificateselection.CertificateUtils;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/preferences/ConfirmImportCertDialog.class */
final class ConfirmImportCertDialog extends JDialog {
    private static final long serialVersionUID = -3168095095548385291L;
    private static final int PREFERRED_WIDTH = 600;
    private static final int PREFERRED_HEIGHT = 600;
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private final List<X509Certificate> certsToImport;
    private JButton openCertBtn;
    private JButton deleteCertBtn;
    private JButton importCertBtn;
    private JTable table;
    private int result = -1;

    ConfirmImportCertDialog(X509Certificate[] x509CertificateArr, Container container) {
        this.certsToImport = new ArrayList(Arrays.asList(x509CertificateArr));
        createUI(container);
    }

    void createUI(Container container) {
        setModal(true);
        setTitle(SimpleAfirmaMessages.getString("TrustedCertificatesDialog.6"));
        setIconImages(DesktopUtil.getIconImages());
        setDefaultCloseOperation(2);
        setPreferredSize(new Dimension(600, 600));
        setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.weighty = 0.0d;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(11, 11, 0, 11);
        add(new JLabel(SimpleAfirmaMessages.getString("TrustedCertificatesDialog.15")), gridBagConstraints);
        gridBagConstraints.weighty = 1.0d;
        gridBagConstraints.gridy++;
        gridBagConstraints.insets = new Insets(5, 11, 0, 11);
        add(createLoadedCertsTable(), gridBagConstraints);
        gridBagConstraints.weighty = 0.0d;
        gridBagConstraints.gridy++;
        add(createCertsButtonsPanel(), gridBagConstraints);
        gridBagConstraints.gridy++;
        gridBagConstraints.insets = new Insets(11, 11, 11, 11);
        add(createDialogButtonsPanel(), gridBagConstraints);
        pack();
        setLocationRelativeTo(container);
    }

    private JScrollPane createLoadedCertsTable() {
        JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.setBounds(10, 11, WinError.ERROR_NO_GUID_TRANSLATION, 227);
        DefaultTableModel defaultTableModel = new DefaultTableModel((Object[][]) null, new String[]{"Nombre", "Emitido por", "Fecha de expiracion"}) { // from class: es.gob.afirma.standalone.ui.preferences.ConfirmImportCertDialog.1
            private static final long serialVersionUID = -8796402986663262751L;

            public boolean isCellEditable(int i, int i2) {
                return false;
            }
        };
        Object[] objArr = new Object[this.certsToImport.size()];
        for (int i = 0; i < this.certsToImport.size(); i++) {
            objArr[i] = new Object[]{AOUtil.getCN(this.certsToImport.get(i).getSubjectX500Principal().toString()), AOUtil.getCN(this.certsToImport.get(i).getIssuerX500Principal().toString()), new SimpleDateFormat("dd-MM-yyyy").format(this.certsToImport.get(i).getNotAfter()).toString()};
        }
        for (Object obj : objArr) {
            defaultTableModel.addRow((Object[]) obj);
        }
        this.table = new JTable(defaultTableModel);
        this.table.getSelectionModel().addListSelectionListener(listSelectionEvent -> {
            this.openCertBtn.setEnabled(true);
            this.deleteCertBtn.setEnabled(true);
        });
        jScrollPane.setViewportView(this.table);
        return jScrollPane;
    }

    private JPanel createCertsButtonsPanel() {
        JPanel jPanel = new JPanel(new FlowLayout(0));
        this.openCertBtn = new JButton(SimpleAfirmaMessages.getString("TrustedCertificatesDialog.17"));
        this.openCertBtn.addActionListener(actionEvent -> {
            CertificateUtils.openCert(this, this.certsToImport.get(this.table.getSelectedRow()));
        });
        this.openCertBtn.setEnabled(false);
        jPanel.add(this.openCertBtn);
        this.deleteCertBtn = new JButton(SimpleAfirmaMessages.getString("TrustedCertificatesDialog.18"));
        this.deleteCertBtn.addActionListener(actionEvent2 -> {
            int[] selectedRows = this.table.getSelectedRows();
            if (selectedRows.length > 0) {
                for (int length = selectedRows.length - 1; length > -1; length--) {
                    int i = selectedRows[length];
                    this.table.getModel().removeRow(i);
                    this.certsToImport.remove(i);
                }
            }
            if (this.table.getSelectedRow() == -1) {
                this.openCertBtn.setEnabled(false);
                this.deleteCertBtn.setEnabled(false);
            }
            if (this.table.getRowCount() == 0) {
                this.importCertBtn.setEnabled(false);
            }
        });
        this.deleteCertBtn.setEnabled(false);
        jPanel.add(this.deleteCertBtn);
        return jPanel;
    }

    private JPanel createDialogButtonsPanel() {
        JPanel jPanel = new JPanel(new FlowLayout(2));
        this.importCertBtn = new JButton(SimpleAfirmaMessages.getString("TrustedCertificatesDialog.19"));
        this.importCertBtn.addActionListener(actionEvent -> {
            this.result = 0;
            importCerts(this);
        });
        jPanel.add(this.importCertBtn);
        JButton jButton = new JButton(SimpleAfirmaMessages.getString("TrustedCertificatesDialog.28"));
        jButton.addActionListener(actionEvent2 -> {
            this.result = 2;
            dispose();
        });
        jPanel.add(jButton);
        return jPanel;
    }

    private void importCerts(Container container) {
        try {
            TrustStoreManager trustStoreManager = TrustStoreManager.getInstance(container);
            Iterator<X509Certificate> it = this.certsToImport.iterator();
            while (it.hasNext()) {
                LOGGER.info("Se importa en el almacen de confianza el certificado con el numero de serie: " + AOUtil.hexify(it.next().getSerialNumber().toByteArray(), false));
            }
            trustStoreManager.importCerts((X509Certificate[]) this.certsToImport.toArray(new X509Certificate[0]));
            SslSecurityManager.configureAfirmaTrustManagers();
            setVisible(false);
        } catch (Exception e) {
            AOUIFactory.showErrorMessage(container, SimpleAfirmaMessages.getString("SignPanel.18"), SimpleAfirmaMessages.getString("SimpleAfirma.48"), 0, e);
        }
    }

    public int getResult() {
        return this.result;
    }
}
