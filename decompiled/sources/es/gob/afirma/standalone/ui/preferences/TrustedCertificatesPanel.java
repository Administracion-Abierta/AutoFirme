package es.gob.afirma.standalone.ui.preferences;

import com.sun.jna.platform.win32.WinError;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.http.SslSecurityManager;
import es.gob.afirma.core.misc.http.TrustStoreManager;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import es.gob.afirma.ui.core.jse.certificateselection.CertificateUtils;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/preferences/TrustedCertificatesPanel.class */
final class TrustedCertificatesPanel extends JPanel {
    private static final long serialVersionUID = -3168095095548385291L;
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final int PREFERRED_WIDTH = 420;
    private static final int PREFERRED_HEIGHT = 150;
    private JTable table;
    private DefaultTableModel model;
    private JScrollPane certsScrollPane;
    private JPanel importedCertPanels;
    static final String TRUSTED_KS_PWD = "changeit";
    private JButton importCertButton = null;
    private JButton viewCertButton = null;
    private JButton deleteCertButton = null;
    List<X509Certificate> savedCerts = new ArrayList();
    private boolean noCerts = true;

    void createUI() {
        setLayout(new GridBagLayout());
        setMinimumSize(new Dimension(PREFERRED_WIDTH, 150));
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 0.0d;
        gridBagConstraints.gridy = 0;
        this.importedCertPanels = new JPanel();
        this.importedCertPanels.setBorder(BorderFactory.createTitledBorder(SimpleAfirmaMessages.getString("TrustedCertificatesDialog.1")));
        createImportedCertsTable();
        this.importedCertPanels.add(this.certsScrollPane);
        gridBagConstraints.gridy++;
        add(this.importedCertPanels, gridBagConstraints);
        gridBagConstraints.gridy++;
        add(createButtonsPanel(), gridBagConstraints);
    }

    TrustedCertificatesPanel() {
        createUI();
    }

    private JPanel createButtonsPanel() {
        JPanel jPanel = new JPanel(new FlowLayout(0));
        this.importCertButton = new JButton(SimpleAfirmaMessages.getString("TrustedCertificatesDialog.2"));
        this.importCertButton.addActionListener(actionEvent -> {
            importCertificatesDlg(this);
        });
        jPanel.add(this.importCertButton);
        this.viewCertButton = new JButton(SimpleAfirmaMessages.getString("TrustedCertificatesDialog.3"));
        this.viewCertButton.addActionListener(actionEvent2 -> {
            CertificateUtils.openCert(this, this.savedCerts.get(this.table.getSelectedRow()));
        });
        this.viewCertButton.setEnabled(false);
        jPanel.add(this.viewCertButton);
        this.deleteCertButton = new JButton(SimpleAfirmaMessages.getString("TrustedCertificatesDialog.5"));
        this.deleteCertButton.addActionListener(actionEvent3 -> {
            deleteCert(this.table.getSelectedRow());
        });
        this.deleteCertButton.setEnabled(false);
        jPanel.add(this.deleteCertButton);
        if (this.noCerts) {
            this.viewCertButton.setEnabled(false);
            this.deleteCertButton.setEnabled(false);
        }
        return jPanel;
    }

    private void createImportedCertsTable() {
        this.model = new DefaultTableModel((Object[][]) null, new String[]{"Nombre", "Emitido por", "Fecha de expiracion"}) { // from class: es.gob.afirma.standalone.ui.preferences.TrustedCertificatesPanel.1
            private static final long serialVersionUID = -3513927556747399446L;

            public boolean isCellEditable(int i, int i2) {
                return false;
            }
        };
        Object[] objArrObtainTrustedCerts = obtainTrustedCerts(this);
        this.model.setRowCount(0);
        if (objArrObtainTrustedCerts != null) {
            for (Object obj : objArrObtainTrustedCerts) {
                this.model.addRow((Object[]) obj);
                this.noCerts = false;
            }
        }
        this.table = new JTable(this.model);
        this.table.setSelectionMode(0);
        this.table.getSelectionModel().addListSelectionListener(listSelectionEvent -> {
            this.viewCertButton.setEnabled(true);
            this.deleteCertButton.setEnabled(true);
        });
        this.table.addMouseListener(new MouseAdapter() { // from class: es.gob.afirma.standalone.ui.preferences.TrustedCertificatesPanel.2
            public void mouseClicked(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    CertificateUtils.openCert(TrustedCertificatesPanel.this.table, TrustedCertificatesPanel.this.savedCerts.get(TrustedCertificatesPanel.this.table.getSelectedRow()));
                }
            }
        });
        this.certsScrollPane = new JScrollPane();
        this.certsScrollPane.setBounds(10, 11, WinError.ERROR_NO_GUID_TRANSLATION, 227);
        this.certsScrollPane.setViewportView(this.table);
        this.certsScrollPane.setVisible(true);
    }

    public void importCertificatesDlg(Container container) {
        new ImportCertificatesDialog(null).setVisible(true);
        updateTableInfo();
    }

    private Object[] obtainTrustedCerts(Container container) {
        Object[] objArr;
        this.savedCerts.clear();
        try {
            X509Certificate[] certificates = TrustStoreManager.getInstance(container).getCertificates();
            objArr = new Object[certificates.length];
            for (int i = 0; i < certificates.length; i++) {
                X509Certificate x509Certificate = certificates[i];
                this.savedCerts.add(x509Certificate);
                objArr[i] = new Object[]{AOUtil.getCN(x509Certificate.getSubjectX500Principal().toString()), AOUtil.getCN(x509Certificate.getIssuerX500Principal().toString()), new SimpleDateFormat("dd-MM-yyyy").format(x509Certificate.getNotAfter()).toString()};
            }
        } catch (Exception e) {
            AOUIFactory.showErrorMessage(container, SimpleAfirmaMessages.getString("TrustedCertificatesDialog.27"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), 0, e);
            objArr = null;
        }
        return objArr;
    }

    private void deleteCert(int i) {
        X509Certificate x509Certificate = this.savedCerts.get(i);
        if (AOUIFactory.showConfirmDialog(this, SimpleAfirmaMessages.getString("TrustedCertificatesDialog.29", AOUtil.getCN(x509Certificate.getSubjectX500Principal().toString())), SimpleAfirmaMessages.getString("SimpleAfirma.48"), 0, 2) == 0) {
            try {
                LOGGER.info("Se elimina del almacen de confianza el certificado con el numero de serie: " + AOUtil.hexify(x509Certificate.getSerialNumber().toByteArray(), false));
                TrustStoreManager.getInstance().deleteCert(x509Certificate);
                SslSecurityManager.configureAfirmaTrustManagers();
                this.savedCerts.remove(i);
                updateTableInfo();
            } catch (Exception e) {
                AOUIFactory.showErrorMessage(this, SimpleAfirmaMessages.getString("TrustedCertificatesDialog.30"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), 0, e);
            }
        }
    }

    private void updateTableInfo() {
        Object[] objArrObtainTrustedCerts = obtainTrustedCerts(this);
        this.model.setRowCount(0);
        if (objArrObtainTrustedCerts != null) {
            for (Object obj : objArrObtainTrustedCerts) {
                this.model.addRow((Object[]) obj);
                this.noCerts = false;
            }
        }
        this.table = new JTable(this.model);
        this.table.getSelectionModel().addListSelectionListener(listSelectionEvent -> {
            boolean z = !this.table.getSelectionModel().isSelectionEmpty();
            this.viewCertButton.setEnabled(z);
            this.deleteCertButton.setEnabled(z);
        });
        this.certsScrollPane.setViewportView(this.table);
    }
}
