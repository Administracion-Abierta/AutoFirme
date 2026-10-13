package es.gob.afirma.standalone.ui.preferences;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.core.misc.http.DataDownloader;
import es.gob.afirma.core.prefs.KeyStorePreferencesManager;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.core.ui.GenericFileFilter;
import es.gob.afirma.standalone.DesktopUtil;
import es.gob.afirma.standalone.HttpManager;
import es.gob.afirma.standalone.JMulticardUtilities;
import es.gob.afirma.standalone.ProxyUtil;
import es.gob.afirma.standalone.SimpleAfirma;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import es.gob.afirma.standalone.configurator.common.PreferencesManager;
import es.gob.afirma.standalone.configurator.common.PreferencesPlistHandler;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemListener;
import java.awt.event.KeyListener;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/preferences/PreferencesPanelGeneral.class */
final class PreferencesPanelGeneral extends JScrollPane {
    private static final long serialVersionUID = 5442844766530064610L;
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private final PreferencesPanel preferencesPanel;
    private final JComboBox<String> signatureAlgorithms = new JComboBox<>();
    private final JCheckBox avoidAskForClose = new JCheckBox(SimpleAfirmaMessages.getString("PreferencesPanel.36"));
    private final JCheckBox checkForUpdates = new JCheckBox(SimpleAfirmaMessages.getString("PreferencesPanel.87"));
    private final JCheckBox enableJMulticard = new JCheckBox(SimpleAfirmaMessages.getString("PreferencesPanel.165"));
    private final JCheckBox optimizedForVdi = new JCheckBox(SimpleAfirmaMessages.getString("PreferencesPanel.190"));
    private final JCheckBox confirmToSign = new JCheckBox(SimpleAfirmaMessages.getString("PreferencesPanel.179"));
    private final JCheckBox allowSignInvalidSignatures = new JCheckBox(SimpleAfirmaMessages.getString("PreferencesPanel.178"));
    private final JCheckBox massiveOverwrite = new JCheckBox(SimpleAfirmaMessages.getString("PreferencesPanel.160"));
    private final JCheckBox secureConnections = new JCheckBox(SimpleAfirmaMessages.getString("PreferencesPanel.173"));
    private final DisposableInterface disposableInterface;
    private boolean blocked;

    PreferencesPanel getPrefPanel() {
        return this.preferencesPanel;
    }

    DisposableInterface getDisposableInterface() {
        return this.disposableInterface;
    }

    PreferencesPanelGeneral(KeyListener keyListener, ItemListener itemListener, DisposableInterface disposableInterface, PreferencesPanel preferencesPanel, boolean z) {
        this.disposableInterface = disposableInterface;
        this.preferencesPanel = preferencesPanel;
        this.blocked = z;
        createUI(keyListener, itemListener);
    }

    private void createUI(KeyListener keyListener, ItemListener itemListener) {
        getVerticalScrollBar().setUnitIncrement(16);
        JPanel jPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        JPanel jPanel2 = new JPanel(new GridBagLayout());
        jPanel2.setBorder(BorderFactory.createTitledBorder(SimpleAfirmaMessages.getString("PreferencesPanel.108")));
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.fill = 2;
        gridBagConstraints2.weightx = 1.0d;
        gridBagConstraints2.gridy = 0;
        gridBagConstraints2.insets = new Insets(0, 0, 0, 0);
        JButton jButton = new JButton(SimpleAfirmaMessages.getString("PreferencesPanel.107"));
        jButton.setMnemonic('I');
        jButton.addActionListener(actionEvent -> {
            if ((actionEvent.getModifiers() & 8) != 0) {
                String str = (String) AOUIFactory.showInputDialog(getParent(), SimpleAfirmaMessages.getString("PreferencesPanel.109"), SimpleAfirmaMessages.getString("PreferencesPanel.110"), 3, null, null, null);
                if (str == null || str.trim().isEmpty()) {
                    return;
                }
                try {
                    PreferencesPlistHandler.importUserPreferencesFromXml(new String(DataDownloader.downloadData(str), StandardCharsets.UTF_8), isBlocked());
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error importando la configuracion desde red (" + str + "): " + e, (Throwable) e);
                    AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("PreferencesPanel.116"), SimpleAfirmaMessages.getString("PreferencesPanel.117"), 0, e);
                    return;
                }
            } else {
                try {
                    PreferencesPlistHandler.importPreferences(AOUIFactory.getLoadFiles(SimpleAfirmaMessages.getString("PreferencesPanel.86"), null, null, new String[]{"afconfig"}, SimpleAfirmaMessages.getString("PreferencesPanel.111"), false, false, DesktopUtil.getDefaultDialogsIcon(), this)[0].getAbsolutePath(), getParent(), isBlocked());
                } catch (AOCancelledOperationException e2) {
                    return;
                }
            }
            AOUIFactory.showMessageDialog(getParent(), SimpleAfirmaMessages.getString("PreferencesPanel.142"), SimpleAfirmaMessages.getString("PreferencesPanel.143"), 1);
            getDisposableInterface().disposeInterface();
        });
        jButton.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.112"));
        jButton.setEnabled(!this.blocked);
        JButton jButton2 = new JButton(SimpleAfirmaMessages.getString("PreferencesPanel.196"));
        jButton2.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.197"));
        jButton2.setMnemonic('G');
        jButton2.addActionListener(actionEvent2 -> {
            try {
                AOUIFactory.getSaveDataToFile(PreferencesPlistHandler.exportPreferencesToXml().getBytes(), SimpleAfirmaMessages.getString("PreferencesPanel.198"), null, SimpleAfirmaMessages.getString("PreferencesPanel.201"), Collections.singletonList(new GenericFileFilter(new String[]{"afconfig"}, SimpleAfirmaMessages.getString("PreferencesPanel.111"))), this);
                AOUIFactory.showMessageDialog(getParent(), SimpleAfirmaMessages.getString("PreferencesPanel.199"), SimpleAfirmaMessages.getString("PreferencesPanel.200"), 1);
            } catch (AOCancelledOperationException e) {
            } catch (Exception e2) {
                AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("PreferencesPanel.116"), SimpleAfirmaMessages.getString("PreferencesPanel.117"), 0, e2);
            }
        });
        JButton jButton3 = new JButton(SimpleAfirmaMessages.getString("PreferencesPanel.135"));
        jButton3.setMnemonic('R');
        jButton3.addActionListener(actionEvent3 -> {
            if (AOUIFactory.showConfirmDialog(getParent(), SimpleAfirmaMessages.getString("PreferencesPanel.140"), SimpleAfirmaMessages.getString("PreferencesPanel.139"), 0, 2) == 0) {
                loadDefaultPreferences();
            }
        });
        jButton3.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.136"));
        jButton3.setEnabled(!this.blocked);
        JPanel jPanel3 = new JPanel();
        jPanel3.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.fill = 2;
        gridBagConstraints3.weightx = 0.5d;
        gridBagConstraints3.gridx = 0;
        jPanel3.add(jButton, gridBagConstraints3);
        gridBagConstraints3.gridx++;
        jPanel3.add(jButton2, gridBagConstraints3);
        gridBagConstraints3.gridx++;
        jPanel3.add(jButton3, gridBagConstraints3);
        jPanel2.add(jPanel3, gridBagConstraints2);
        gridBagConstraints2.insets = new Insets(5, 7, 3, 7);
        gridBagConstraints2.anchor = 21;
        gridBagConstraints2.gridy++;
        this.avoidAskForClose.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.182"));
        this.avoidAskForClose.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.44"));
        this.avoidAskForClose.addItemListener(itemListener);
        this.avoidAskForClose.addKeyListener(keyListener);
        jPanel2.add(this.avoidAskForClose, gridBagConstraints2);
        gridBagConstraints2.gridy++;
        if (SimpleAfirma.isUpdatesEnabled()) {
            this.checkForUpdates.setEnabled(!this.blocked);
            this.checkForUpdates.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.182"));
            this.checkForUpdates.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.88"));
            this.checkForUpdates.addItemListener(itemListener);
            this.checkForUpdates.addKeyListener(keyListener);
            jPanel2.add(this.checkForUpdates, gridBagConstraints2);
            gridBagConstraints2.gridy++;
        }
        if (Platform.getOS() == Platform.OS.WINDOWS || Platform.getOS() == Platform.OS.LINUX) {
            this.enableJMulticard.setEnabled(!this.blocked);
            this.enableJMulticard.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.182"));
            this.enableJMulticard.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.166"));
            this.enableJMulticard.addItemListener(itemListener);
            this.enableJMulticard.addKeyListener(keyListener);
            jPanel2.add(this.enableJMulticard, gridBagConstraints2);
            gridBagConstraints2.gridy++;
        }
        if (Platform.getOS() == Platform.OS.WINDOWS) {
            this.optimizedForVdi.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.191"));
            this.optimizedForVdi.addItemListener(itemListener);
            this.optimizedForVdi.addKeyListener(keyListener);
            jPanel2.add(this.optimizedForVdi, gridBagConstraints2);
            gridBagConstraints2.gridy++;
        }
        jPanel.add(jPanel2, gridBagConstraints);
        JPanel jPanel4 = new JPanel(new FlowLayout(0));
        jPanel4.setBorder(BorderFactory.createTitledBorder(SimpleAfirmaMessages.getString("PreferencesPanel.17")));
        GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        gridBagConstraints4.gridy = 0;
        gridBagConstraints4.insets = new Insets(0, 0, 4, 7);
        gridBagConstraints4.anchor = 21;
        JLabel jLabel = new JLabel(SimpleAfirmaMessages.getString("PreferencesPanel.18"));
        this.signatureAlgorithms.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.183"));
        this.signatureAlgorithms.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.46"));
        this.signatureAlgorithms.addItemListener(itemListener);
        this.signatureAlgorithms.addKeyListener(keyListener);
        this.signatureAlgorithms.setModel(new DefaultComboBoxModel(new String[]{AOSignConstants.DIGEST_ALGORITHM_SHA1, "SHA512", AOSignConstants.DIGEST_ALGORITHM_SHA384, "SHA256"}));
        jLabel.setLabelFor(this.signatureAlgorithms);
        this.signatureAlgorithms.setEnabled(!isBlocked());
        JPanel jPanel5 = new JPanel(new GridBagLayout());
        jPanel4.add(jPanel5);
        gridBagConstraints4.gridx = 0;
        jPanel5.add(jLabel, gridBagConstraints4);
        gridBagConstraints4.gridx = 1;
        jPanel5.add(this.signatureAlgorithms, gridBagConstraints4);
        JLabel jLabel2 = new JLabel(SimpleAfirmaMessages.getString("PreferencesPanel.161"));
        JButton jButton4 = new JButton(SimpleAfirmaMessages.getString("PreferencesPanel.162"));
        jButton4.setMnemonic('o');
        jButton4.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.163"));
        jButton4.addActionListener(actionEvent4 -> {
            openFormatsDlg(getParent(), isBlocked());
        });
        jLabel2.setLabelFor(jButton4);
        gridBagConstraints4.gridy++;
        gridBagConstraints4.gridx = 0;
        jPanel5.add(jLabel2, gridBagConstraints4);
        gridBagConstraints4.gridx = 1;
        jPanel5.add(jButton4, gridBagConstraints4);
        gridBagConstraints4.gridx = 0;
        gridBagConstraints4.gridwidth = 2;
        gridBagConstraints4.insets = new Insets(4, 0, 3, 7);
        gridBagConstraints4.gridy++;
        this.confirmToSign.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.182"));
        this.confirmToSign.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.180"));
        this.confirmToSign.addItemListener(itemListener);
        this.confirmToSign.addKeyListener(keyListener);
        jPanel5.add(this.confirmToSign, gridBagConstraints4);
        gridBagConstraints4.gridy++;
        this.allowSignInvalidSignatures.setEnabled(!this.blocked);
        this.allowSignInvalidSignatures.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.182"));
        this.allowSignInvalidSignatures.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.178"));
        this.allowSignInvalidSignatures.addItemListener(itemListener);
        this.allowSignInvalidSignatures.addKeyListener(keyListener);
        jPanel5.add(this.allowSignInvalidSignatures, gridBagConstraints4);
        gridBagConstraints4.gridy++;
        this.massiveOverwrite.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.182"));
        this.massiveOverwrite.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.160"));
        this.massiveOverwrite.addItemListener(itemListener);
        this.massiveOverwrite.addKeyListener(keyListener);
        jPanel5.add(this.massiveOverwrite, gridBagConstraints4);
        JPanel jPanel6 = new JPanel(new FlowLayout(0));
        jPanel6.setBorder(BorderFactory.createTitledBorder(SimpleAfirmaMessages.getString("PreferencesPanel.125")));
        JPanel jPanel7 = new JPanel(new GridBagLayout());
        jPanel6.add(jPanel7);
        this.secureConnections.setEnabled(!this.blocked);
        this.secureConnections.addItemListener(itemListener);
        this.secureConnections.addKeyListener(keyListener);
        this.secureConnections.setToolTipText(SimpleAfirmaMessages.getString("PreferencesPanel.174"));
        this.secureConnections.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.182"));
        this.secureConnections.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.174"));
        JButton jButton5 = new JButton(SimpleAfirmaMessages.getString("PreferencesPanel.185"));
        jButton5.setMnemonic('D');
        jButton5.addActionListener(actionEvent5 -> {
            changeSecureDomainsDlg(getParent());
        });
        jButton5.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.186"));
        JButton jButton6 = new JButton(SimpleAfirmaMessages.getString("PreferencesPanel.126"));
        jButton6.setMnemonic('y');
        jButton6.addActionListener(actionEvent6 -> {
            changeProxyDlg(getParent());
        });
        jButton6.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.127"));
        JLabel jLabel3 = new JLabel("<html>" + SimpleAfirmaMessages.getString("PreferencesPanel.128") + "</html>");
        jLabel3.setLabelFor(jButton6);
        JButton jButton7 = new JButton(SimpleAfirmaMessages.getString("PreferencesPanel.194"));
        jButton7.setMnemonic('t');
        jButton7.addActionListener(actionEvent7 -> {
            trustedCertificatesDlg(this);
        });
        jButton7.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.195"));
        GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
        gridBagConstraints5.insets = new Insets(0, 0, 4, 7);
        gridBagConstraints5.gridx = 0;
        gridBagConstraints5.gridy = 0;
        gridBagConstraints5.gridwidth = 2;
        gridBagConstraints5.anchor = 21;
        jPanel7.add(this.secureConnections, gridBagConstraints5);
        gridBagConstraints5.gridx++;
        gridBagConstraints5.insets = new Insets(0, 25, 0, 0);
        jPanel7.add(jButton5, gridBagConstraints5);
        gridBagConstraints5.insets = new Insets(0, 0, 4, 7);
        gridBagConstraints5.anchor = 21;
        gridBagConstraints5.gridx = 0;
        gridBagConstraints5.gridwidth = 1;
        gridBagConstraints5.gridy++;
        jPanel7.add(jLabel3, gridBagConstraints5);
        gridBagConstraints5.gridx++;
        gridBagConstraints5.anchor = 22;
        jPanel7.add(jButton6, gridBagConstraints5);
        gridBagConstraints5.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints5.anchor = 21;
        gridBagConstraints5.gridy++;
        gridBagConstraints5.gridx = 0;
        jPanel7.add(jButton7, gridBagConstraints5);
        gridBagConstraints.gridy++;
        jPanel.add(jPanel4, gridBagConstraints);
        gridBagConstraints.gridy++;
        jPanel.add(jPanel6, gridBagConstraints);
        gridBagConstraints.weighty = 1.0d;
        gridBagConstraints.gridy++;
        jPanel.add(new JPanel(), gridBagConstraints);
        loadPreferences();
        setViewportView(jPanel);
    }

    public static void changeProxyDlg(Container container) {
        container.setCursor(new Cursor(3));
        ProxyPanel proxyPanel = new ProxyPanel();
        proxyPanel.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("ProxyDialog.15"));
        container.setCursor(new Cursor(0));
        if (AOUIFactory.showConfirmDialog(container, proxyPanel, SimpleAfirmaMessages.getString("ProxyDialog.0"), 2, -1) == 0) {
            try {
                proxyPanel.saveData();
                try {
                    ProxyUtil.setProxySettings();
                } catch (Throwable th) {
                    LOGGER.log(Level.SEVERE, "Error al aplicar la configuracion de proxy", th);
                    AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("SimpleAfirma.11"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), AOUIFactory.WARNING_MESSAGE, th);
                }
            } catch (ConfigurationException e) {
                AOUIFactory.showErrorMessage(e.getMessage(), SimpleAfirmaMessages.getString("ProxyDialog.2"), 0, e);
                changeProxyDlg(container);
            }
        }
    }

    public static void changeSecureDomainsDlg(Container container) {
        container.setCursor(new Cursor(3));
        SecureDomainsPanel secureDomainsPanel = new SecureDomainsPanel();
        secureDomainsPanel.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("SecureDomainsDialog.2"));
        container.setCursor(new Cursor(0));
        if (AOUIFactory.showConfirmDialog(container, secureDomainsPanel, SimpleAfirmaMessages.getString("SecureDomainsDialog.0"), 2, -1) == 0) {
            try {
                secureDomainsPanel.saveData();
            } catch (ConfigurationException e) {
                AOUIFactory.showErrorMessage(e.getMessage(), SimpleAfirmaMessages.getString("SecureDomainsDialog.1"), 0, e);
                changeSecureDomainsDlg(container);
            }
        }
    }

    public static void trustedCertificatesDlg(Container container) {
        JOptionPane.showMessageDialog(container, new TrustedCertificatesPanel(), SimpleAfirmaMessages.getString("TrustedCertificatesDialog.0"), -1);
    }

    private static void openFormatsDlg(Component component, boolean z) {
        DefaultFormatPanel defaultFormatPanel = new DefaultFormatPanel();
        defaultFormatPanel.loadPreferences();
        defaultFormatPanel.setBlocked(z);
        defaultFormatPanel.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("DefaultFormatPanel.1"));
        if (AOUIFactory.showConfirmDialog(component, defaultFormatPanel, SimpleAfirmaMessages.getString("PreferencesPanel.164"), 2, -1) == 0) {
            defaultFormatPanel.savePreferences();
        }
    }

    public boolean isBlocked() {
        return this.blocked;
    }

    public void setBlocked(boolean z) {
        this.blocked = z;
    }

    void savePreferences() {
        PreferencesManager.put(PreferencesManager.PREFERENCE_GENERAL_SIGNATURE_ALGORITHM, this.signatureAlgorithms.getSelectedItem().toString());
        PreferencesManager.putBoolean(PreferencesManager.PREFERENCE_GENERAL_OMIT_ASKONCLOSE, this.avoidAskForClose.isSelected());
        PreferencesManager.putBoolean(PreferencesManager.PREFERENCE_GENERAL_CONFIRMTOSIGN, this.confirmToSign.isSelected());
        if (SimpleAfirma.isUpdatesEnabled()) {
            PreferencesManager.putBoolean(PreferencesManager.PREFERENCE_GENERAL_UPDATECHECK, this.checkForUpdates.isSelected());
        }
        PreferencesManager.putBoolean(PreferencesManager.PREFERENCE_GENERAL_ALLOW_INVALID_SIGNATURES, this.allowSignInvalidSignatures.isSelected());
        PreferencesManager.putBoolean(PreferencesManager.PREFERENCE_GENERAL_ENABLED_JMULTICARD, this.enableJMulticard.isSelected());
        PreferencesManager.putBoolean(PreferencesManager.PREFERENCE_GENERAL_VDI_OPTIMIZATION, this.optimizedForVdi.isSelected());
        PreferencesManager.putBoolean(PreferencesManager.PREFERENCE_GENERAL_MASSIVE_OVERWRITE, this.massiveOverwrite.isSelected());
        PreferencesManager.putBoolean(PreferencesManager.PREFERENCE_GENERAL_SECURE_CONNECTIONS, this.secureConnections.isSelected());
        HttpManager.setSecureConnections(this.secureConnections.isSelected());
    }

    void loadPreferences() {
        this.signatureAlgorithms.setSelectedItem(PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_SIGNATURE_ALGORITHM));
        if (this.signatureAlgorithms.getSelectedItem() == null) {
            this.signatureAlgorithms.setSelectedItem(PreferencesManager.get(PreferencesManager.PREFERENCE_GENERAL_SIGNATURE_ALGORITHM, PreferencesManager.PreferencesSource.DEFAULT));
        }
        this.avoidAskForClose.setSelected(PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_GENERAL_OMIT_ASKONCLOSE));
        this.confirmToSign.setSelected(PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_GENERAL_CONFIRMTOSIGN));
        if (!SimpleAfirma.isUpdatesEnabled()) {
            this.checkForUpdates.setSelected(false);
            this.checkForUpdates.setEnabled(false);
        } else {
            this.checkForUpdates.setSelected(PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_GENERAL_UPDATECHECK));
        }
        this.allowSignInvalidSignatures.setSelected(PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_GENERAL_ALLOW_INVALID_SIGNATURES));
        if (JMulticardUtilities.isJMulticardConfigurateBySystem()) {
            this.enableJMulticard.setSelected(false);
            this.enableJMulticard.setEnabled(false);
        } else {
            this.enableJMulticard.setSelected(PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_GENERAL_ENABLED_JMULTICARD));
        }
        this.optimizedForVdi.setSelected(PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_GENERAL_VDI_OPTIMIZATION));
        this.massiveOverwrite.setSelected(PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_GENERAL_MASSIVE_OVERWRITE));
        this.secureConnections.setSelected(PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_GENERAL_SECURE_CONNECTIONS));
    }

    void loadDefaultPreferences() {
        try {
            PreferencesManager.clearAll();
        } catch (Exception e) {
            LOGGER.warning("No se pudo restaurar la configuracion general de la aplicacion: " + e);
        }
        try {
            KeyStorePreferencesManager.clearKeyStorePrefs();
        } catch (Exception e2) {
            LOGGER.warning("No se pudo restaurar la configuracion de almacenes de la aplicacion: " + e2);
        }
        loadPreferences();
        getDisposableInterface().disposeInterface();
    }
}
