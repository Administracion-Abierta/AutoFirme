package es.gob.afirma.standalone.ui.preferences;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.misc.http.SslSecurityManager;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.standalone.DesktopUtil;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Locale;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLHandshakeException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/preferences/ImportCertificatesDialog.class */
final class ImportCertificatesDialog extends JDialog {
    private static final long serialVersionUID = -3168095095548385291L;
    private static final String SCHEMA_SEPARATOR = "://";
    private static final String HTTPS_SCHEMA = "https://";
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    ImportCertificatesDialog(JFrame jFrame) {
        super(jFrame, true);
        createUI();
    }

    void createUI() {
        setTitle(SimpleAfirmaMessages.getString("TrustedCertificatesDialog.6"));
        setIconImages(DesktopUtil.getIconImages());
        setDefaultCloseOperation(2);
        setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(11, 11, 0, 11);
        add(new JLabel(SimpleAfirmaMessages.getString("TrustedCertificatesDialog.7")), gridBagConstraints);
        JPanel jPanelCreateImportLocalCertUI = createImportLocalCertUI();
        gridBagConstraints.gridy++;
        add(jPanelCreateImportLocalCertUI, gridBagConstraints);
        JPanel jPanelCreateImportRemoteCertUI = createImportRemoteCertUI();
        gridBagConstraints.gridy++;
        add(jPanelCreateImportRemoteCertUI, gridBagConstraints);
        Component jButton = new JButton(SimpleAfirmaMessages.getString("TrustedCertificatesDialog.28"));
        jButton.addActionListener(actionEvent -> {
            dispose();
        });
        gridBagConstraints.fill = 0;
        gridBagConstraints.anchor = 14;
        gridBagConstraints.insets = new Insets(11, 11, 11, 11);
        gridBagConstraints.gridy++;
        add(jButton, gridBagConstraints);
        pack();
        setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
    }

    private JPanel createImportLocalCertUI() {
        JPanel jPanel = new JPanel();
        jPanel.setBorder(BorderFactory.createTitledBorder(SimpleAfirmaMessages.getString("TrustedCertificatesDialog.6")));
        jPanel.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        jPanel.add(new JLabel(SimpleAfirmaMessages.getString("TrustedCertificatesDialog.9")), gridBagConstraints);
        JButton jButton = new JButton(SimpleAfirmaMessages.getString("TrustedCertificatesDialog.13"));
        jButton.addActionListener(actionEvent -> {
            loadLocalCert(this);
        });
        gridBagConstraints.weightx = 0.0d;
        gridBagConstraints.gridx++;
        jPanel.add(jButton, gridBagConstraints);
        return jPanel;
    }

    private JPanel createImportRemoteCertUI() {
        JPanel jPanel = new JPanel();
        jPanel.setBorder(BorderFactory.createTitledBorder(SimpleAfirmaMessages.getString("TrustedCertificatesDialog.10")));
        jPanel.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new Insets(5, 5, 0, 5);
        jPanel.add(new JLabel(SimpleAfirmaMessages.getString("TrustedCertificatesDialog.11")), gridBagConstraints);
        gridBagConstraints.gridy++;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.insets = new Insets(0, 5, 5, 5);
        final JTextField jTextField = new JTextField();
        jPanel.add(jTextField, gridBagConstraints);
        gridBagConstraints.weightx = 0.0d;
        gridBagConstraints.gridx++;
        final JButton jButton = new JButton(SimpleAfirmaMessages.getString("TrustedCertificatesDialog.12"));
        jButton.addActionListener(actionEvent -> {
            downloadRemoteCert(this, jTextField);
        });
        jButton.setEnabled(false);
        jPanel.add(jButton, gridBagConstraints);
        jTextField.getDocument().addDocumentListener(new DocumentListener() { // from class: es.gob.afirma.standalone.ui.preferences.ImportCertificatesDialog.1
            public void insertUpdate(DocumentEvent documentEvent) {
                if (jTextField.getText().isEmpty()) {
                    jButton.setEnabled(false);
                } else {
                    jButton.setEnabled(true);
                }
            }

            public void removeUpdate(DocumentEvent documentEvent) {
                if (jTextField.getText().isEmpty()) {
                    jButton.setEnabled(false);
                } else {
                    jButton.setEnabled(true);
                }
            }

            public void changedUpdate(DocumentEvent documentEvent) {
                if (jTextField.getText().isEmpty()) {
                    jButton.setEnabled(false);
                } else {
                    jButton.setEnabled(true);
                }
            }
        });
        return jPanel;
    }

    private void downloadRemoteCert(Container container, JTextField jTextField) {
        X509Certificate[] x509CertificateArrDownloadServerCerts;
        try {
            URL urlBuildUrl = buildUrl(jTextField.getText());
            try {
                x509CertificateArrDownloadServerCerts = downloadServerCerts(urlBuildUrl, false);
            } catch (SSLHandshakeException e) {
                x509CertificateArrDownloadServerCerts = downloadServerCerts(urlBuildUrl, true);
            }
            ConfirmImportCertDialog confirmImportCertDialog = new ConfirmImportCertDialog(x509CertificateArrDownloadServerCerts, this);
            confirmImportCertDialog.setVisible(true);
            if (confirmImportCertDialog.getResult() == 0) {
                setVisible(false);
            }
        } catch (Exception e2) {
            AOUIFactory.showErrorMessage(container, SimpleAfirmaMessages.getString("TrustedCertificatesDialog.27"), SimpleAfirmaMessages.getString("SimpleAfirma.48"), 0, e2);
        }
    }

    private static URL buildUrl(String str) throws MalformedURLException {
        String str2;
        if (str.toLowerCase(Locale.ENGLISH).startsWith(HTTPS_SCHEMA)) {
            str2 = str;
        } else if (str.indexOf(SCHEMA_SEPARATOR) == -1) {
            str2 = HTTPS_SCHEMA + str;
        } else {
            str2 = HTTPS_SCHEMA + str.substring(str.indexOf(SCHEMA_SEPARATOR) + SCHEMA_SEPARATOR.length());
        }
        return new URL(str2);
    }

    private static X509Certificate[] downloadServerCerts(URL url, boolean z) throws GeneralSecurityException, IOException {
        X509Certificate[] x509CertificateArr;
        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
        if (z) {
            SslSecurityManager.disableSslChecks(httpsURLConnection);
        }
        httpsURLConnection.connect();
        Certificate[] serverCertificates = httpsURLConnection.getServerCertificates();
        httpsURLConnection.disconnect();
        if (serverCertificates.length > 1) {
            x509CertificateArr = new X509Certificate[serverCertificates.length - 1];
            for (int i = 1; i < serverCertificates.length; i++) {
                x509CertificateArr[i - 1] = (X509Certificate) serverCertificates[i];
            }
        } else {
            x509CertificateArr = new X509Certificate[]{(X509Certificate) serverCertificates[0]};
        }
        return x509CertificateArr;
    }

    private void loadLocalCert(Container container) {
        try {
            File[] loadFiles = AOUIFactory.getLoadFiles(SimpleAfirmaMessages.getString("TrustedCertificatesDialog.20"), null, null, new String[]{"cer", "cert", "crt"}, SimpleAfirmaMessages.getString("TrustedCertificatesDialog.21"), false, true, DesktopUtil.getDefaultDialogsIcon(), this);
            try {
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
                X509Certificate[] x509CertificateArr = new X509Certificate[loadFiles.length];
                for (int i = 0; i < loadFiles.length; i++) {
                    FileInputStream fileInputStream = new FileInputStream(loadFiles[i]);
                    Throwable th = null;
                    try {
                        try {
                            x509CertificateArr[i] = (X509Certificate) certificateFactory.generateCertificate(fileInputStream);
                            if (fileInputStream != null) {
                                if (0 != 0) {
                                    try {
                                        fileInputStream.close();
                                    } catch (Throwable th2) {
                                        th.addSuppressed(th2);
                                    }
                                } else {
                                    fileInputStream.close();
                                }
                            }
                        } catch (Throwable th3) {
                            if (fileInputStream != null) {
                                if (th != null) {
                                    try {
                                        fileInputStream.close();
                                    } catch (Throwable th4) {
                                        th.addSuppressed(th4);
                                    }
                                } else {
                                    fileInputStream.close();
                                }
                            }
                            throw th3;
                        }
                    } catch (Throwable th5) {
                        th = th5;
                        throw th5;
                    }
                }
                ConfirmImportCertDialog confirmImportCertDialog = new ConfirmImportCertDialog(x509CertificateArr, container);
                confirmImportCertDialog.setVisible(true);
                if (confirmImportCertDialog.getResult() == 0) {
                    setVisible(false);
                }
            } catch (Exception e) {
                AOUIFactory.showErrorMessage(container, SimpleAfirmaMessages.getString("TrustedCertificatesDialog.27"), SimpleAfirmaMessages.getString("SimpleAfirma.48"), 0, e);
            }
        } catch (AOCancelledOperationException e2) {
        }
    }
}
