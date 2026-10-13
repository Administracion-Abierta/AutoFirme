package es.gob.afirma.standalone.ui.preferences;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.misc.LoggerUtil;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.core.prefs.KeyStorePreferencesManager;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.keystores.AOKeyStore;
import es.gob.afirma.keystores.AOKeyStoreDialog;
import es.gob.afirma.keystores.AOKeyStoreManager;
import es.gob.afirma.keystores.AOKeyStoreManagerFactory;
import es.gob.afirma.keystores.AggregatedKeyStoreManager;
import es.gob.afirma.keystores.CertificateFilter;
import es.gob.afirma.keystores.MultipleCertificateFilter;
import es.gob.afirma.keystores.filters.PseudonymFilter;
import es.gob.afirma.keystores.filters.SkipAuthDNIeFilter;
import es.gob.afirma.keystores.filters.rfc.KeyUsageFilter;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import es.gob.afirma.standalone.SimpleKeyStoreManager;
import es.gob.afirma.standalone.configurator.common.PreferencesManager;
import es.gob.afirma.ui.core.jse.certificateselection.CertificateSelectionDialog;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/preferences/PreferencesPanelKeystores.class */
final class PreferencesPanelKeystores extends JScrollPane {
    private static final long serialVersionUID = 3491050093362323228L;
    JComboBox<RegisteredKeystore> keystores;
    JComboBox<RegisteredKeystore> smartCards;
    private static final String EXTS_DESC_PKCS12 = " (*.p12, *.pfx)";
    private static final String DEFAULT_VALUE = "default";
    String localKeystoreSelectedPath;
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    static final String[] EXTS_PKCS12 = {"pfx", "p12"};
    private final JPanel panelKeystores = new JPanel();
    private final JButton showContentButton = new JButton(SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.2"));
    final JButton connectButton = new JButton(SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.10"));
    final JButton addCardButton = new JButton(SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.11"));
    final JButton modifyCardButton = new JButton(SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.23"));
    final JButton deleteCardButton = new JButton(SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.24"));
    private final JCheckBox callsFromNavigator = new JCheckBox(SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.3"));
    private final JCheckBox hideDniStartScreen = new JCheckBox(SimpleAfirmaMessages.getString("PreferencesPanel.81"));
    private final JCheckBox showExpiredCerts = new JCheckBox(SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.6"));
    private final JCheckBox onlySignature = new JCheckBox(SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.7"));
    private final JCheckBox onlyAlias = new JCheckBox(SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.8"));
    private final JCheckBox skipAuthCertDnie = new JCheckBox(SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.32"));
    private boolean blocked = true;

    PreferencesPanelKeystores(KeyListener keyListener, ItemListener itemListener, boolean z) {
        setBlocked(z);
        createUI(keyListener, itemListener);
    }

    void createUI(KeyListener keyListener, ItemListener itemListener) {
        getVerticalScrollBar().setUnitIncrement(16);
        JPanel jPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 1.0d;
        gridBagConstraints.gridy = 0;
        this.keystores = new JComboBox<>();
        this.keystores.addActionListener(new ActionListener() { // from class: es.gob.afirma.standalone.ui.preferences.PreferencesPanelKeystores.1
            public void actionPerformed(ActionEvent actionEvent) {
                if (PreferencesPanelKeystores.this.keystores.getSelectedItem() == null) {
                    return;
                }
                RegisteredKeystore registeredKeystore = (RegisteredKeystore) PreferencesPanelKeystores.this.keystores.getSelectedItem();
                if (registeredKeystore.getName().equals(AOKeyStore.PKCS12.getName()) && actionEvent.getModifiers() != 0) {
                    try {
                        PreferencesPanelKeystores.this.localKeystoreSelectedPath = AOUIFactory.getLoadFiles(SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.12"), null, null, PreferencesPanelKeystores.EXTS_PKCS12, SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.13") + PreferencesPanelKeystores.EXTS_DESC_PKCS12, false, false, null, this)[0].getAbsolutePath();
                        return;
                    } catch (AOCancelledOperationException e) {
                        PreferencesPanelKeystores.this.keystores.setSelectedIndex(0);
                        return;
                    }
                }
                if (registeredKeystore.getProviderName().equals(AOKeyStore.PKCS11.getProviderName()) && actionEvent.getModifiers() != 0) {
                    PreferencesPanelKeystores.this.localKeystoreSelectedPath = registeredKeystore.getLib();
                    return;
                }
                if (registeredKeystore.getName().equals(AOKeyStore.DNIEJAVA.getName()) && actionEvent.getModifiers() != 0) {
                    try {
                        new AOKeyStoreManager().init(AOKeyStore.DNIEJAVA, null, null, new Object[]{this}, true);
                    } catch (AOCancelledOperationException e2) {
                        PreferencesPanelKeystores.this.keystores.setSelectedIndex(0);
                        throw e2;
                    } catch (Exception e3) {
                        PreferencesPanelKeystores.this.keystores.setSelectedIndex(0);
                        PreferencesPanelKeystores.LOGGER.log(Level.WARNING, "No se ha podido cargar el DNIe: " + e3, (Throwable) e3);
                        AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.14"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), AOUIFactory.ERROR_MESSAGE, e3);
                    }
                }
            }
        });
        this.panelKeystores.setBorder(BorderFactory.createTitledBorder(SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.1")));
        this.panelKeystores.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.fill = 2;
        gridBagConstraints2.weightx = 1.0d;
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 0;
        this.keystores.addItemListener(itemListener);
        this.keystores.addKeyListener(keyListener);
        this.panelKeystores.add(this.keystores, gridBagConstraints2);
        gridBagConstraints2.gridwidth = 2;
        gridBagConstraints2.gridx++;
        this.showContentButton.addActionListener(new ActionListener() { // from class: es.gob.afirma.standalone.ui.preferences.PreferencesPanelKeystores.2
            public void actionPerformed(ActionEvent actionEvent) {
                String str = null;
                AOKeyStore keyStore = SimpleKeyStoreManager.getKeyStore(((RegisteredKeystore) PreferencesPanelKeystores.this.keystores.getSelectedItem()).getName());
                if (keyStore == null) {
                    Map<String, String> userSmartCardsRegistered = KeyStorePreferencesManager.getUserSmartCardsRegistered();
                    for (String str2 : userSmartCardsRegistered.keySet()) {
                        if (((RegisteredKeystore) PreferencesPanelKeystores.this.keystores.getSelectedItem()).getName().equals(str2)) {
                            keyStore = AOKeyStore.PKCS11;
                            keyStore.setName(str2);
                            str = userSmartCardsRegistered.get(str2);
                        }
                    }
                    for (String str3 : KeyStorePreferencesManager.getSystemSmartCardsRegistered().keySet()) {
                        if (((RegisteredKeystore) PreferencesPanelKeystores.this.keystores.getSelectedItem()).getName().equals(str3)) {
                            keyStore = AOKeyStore.PKCS11;
                            keyStore.setName(str3);
                            str = userSmartCardsRegistered.get(str3);
                        }
                    }
                } else if (AOKeyStore.PKCS12.equals(keyStore) || AOKeyStore.PKCS11.getName().equals(keyStore.getName())) {
                    str = PreferencesPanelKeystores.this.localKeystoreSelectedPath;
                    if (str == null) {
                        str = PreferencesManager.get(PreferencesManager.PREFERENCE_LOCAL_KEYSTORE_PATH);
                    }
                }
                if (keyStore == null) {
                    AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.36"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), AOUIFactory.ERROR_MESSAGE, null);
                    return;
                }
                try {
                    AggregatedKeyStoreManager aOKeyStoreManager = AOKeyStoreManagerFactory.getAOKeyStoreManager(keyStore, str, "default", keyStore.getStorePasswordCallback(this), this);
                    String name = null;
                    if (str != null) {
                        name = new File(str).getName();
                    }
                    new CertificateSelectionDialog(PreferencesPanelKeystores.this, new AOKeyStoreDialog(aOKeyStoreManager, this, true, PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_KEYSTORE_SHOWEXPIREDCERTS), false, PreferencesPanelKeystores.getCertFilters(), false, name), SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.18", keyStore.toString()), SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.19", keyStore.toString()), false, true).showDialog(false);
                } catch (AOCancelledOperationException e) {
                } catch (Exception e2) {
                    AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.20"), SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.18", keyStore.toString()), 0, e2);
                    Logger.getLogger("es.gob.afirma").warning("Error al recuperar el almacen por defecto seleccionado: " + actionEvent);
                }
            }
        });
        this.showContentButton.setMnemonic('V');
        this.panelKeystores.add(this.showContentButton, gridBagConstraints2);
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy++;
        gridBagConstraints2.insets = new Insets(5, 0, 0, 0);
        this.callsFromNavigator.setEnabled(!this.blocked);
        this.callsFromNavigator.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.182"));
        this.callsFromNavigator.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.17"));
        this.callsFromNavigator.addItemListener(itemListener);
        this.callsFromNavigator.addKeyListener(keyListener);
        this.panelKeystores.add(this.callsFromNavigator, gridBagConstraints2);
        this.hideDniStartScreen.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.182"));
        this.hideDniStartScreen.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.82"));
        this.hideDniStartScreen.addItemListener(itemListener);
        this.hideDniStartScreen.addKeyListener(keyListener);
        gridBagConstraints2.gridy++;
        this.panelKeystores.add(this.hideDniStartScreen, gridBagConstraints2);
        jPanel.add(this.panelKeystores, gridBagConstraints);
        JPanel jPanel2 = new JPanel();
        jPanel2.setBorder(BorderFactory.createTitledBorder(SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.4")));
        jPanel2.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.fill = 2;
        gridBagConstraints3.insets = new Insets(5, 0, 0, 0);
        gridBagConstraints3.weightx = 1.0d;
        gridBagConstraints3.gridy = 0;
        this.showExpiredCerts.setEnabled(!this.blocked);
        this.showExpiredCerts.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.182"));
        this.showExpiredCerts.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.177"));
        this.showExpiredCerts.addItemListener(itemListener);
        this.showExpiredCerts.addKeyListener(keyListener);
        jPanel2.add(this.showExpiredCerts, gridBagConstraints3);
        gridBagConstraints3.gridy++;
        this.onlySignature.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.182"));
        this.onlySignature.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.2"));
        this.onlySignature.addItemListener(itemListener);
        this.onlySignature.addKeyListener(keyListener);
        jPanel2.add(this.onlySignature, gridBagConstraints3);
        gridBagConstraints3.gridy++;
        this.onlyAlias.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.182"));
        this.onlyAlias.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.5"));
        this.onlyAlias.addItemListener(itemListener);
        this.onlyAlias.addKeyListener(keyListener);
        jPanel2.add(this.onlyAlias, gridBagConstraints3);
        gridBagConstraints3.gridy++;
        this.skipAuthCertDnie.getAccessibleContext().setAccessibleName(SimpleAfirmaMessages.getString("PreferencesPanel.182"));
        this.skipAuthCertDnie.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.32"));
        this.skipAuthCertDnie.addItemListener(itemListener);
        this.skipAuthCertDnie.addKeyListener(keyListener);
        jPanel2.add(this.skipAuthCertDnie, gridBagConstraints3);
        gridBagConstraints.gridy++;
        jPanel.add(jPanel2, gridBagConstraints);
        JPanel jPanel3 = new JPanel();
        jPanel3.setBorder(BorderFactory.createTitledBorder(SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.9")));
        jPanel3.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        gridBagConstraints4.fill = 2;
        gridBagConstraints4.gridy = 0;
        gridBagConstraints4.gridx = 0;
        gridBagConstraints4.weightx = 5.0d;
        gridBagConstraints4.gridwidth = 5;
        jPanel3.add(new JLabel(SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.33")), gridBagConstraints4);
        this.smartCards = new JComboBox<>();
        loadSmartCardsComboBox(this.smartCards);
        loadKeyStoresComboBox(this.keystores, this.smartCards);
        this.smartCards.repaint();
        this.keystores.repaint();
        this.connectButton.setEnabled(false);
        this.modifyCardButton.setEnabled(false);
        this.deleteCardButton.setEnabled(false);
        this.smartCards.addActionListener(actionEvent -> {
            if (actionEvent.getModifiers() != 0 && this.smartCards.getSelectedIndex() == 0) {
                this.connectButton.setEnabled(false);
                this.modifyCardButton.setEnabled(false);
                this.deleteCardButton.setEnabled(false);
            } else if (actionEvent.getModifiers() != 0 && ((RegisteredKeystore) this.smartCards.getSelectedItem()).isSystemSmartCard()) {
                this.connectButton.setEnabled(true);
                this.modifyCardButton.setEnabled(false);
                this.deleteCardButton.setEnabled(false);
            } else {
                this.connectButton.setEnabled(true);
                this.modifyCardButton.setEnabled(true);
                this.deleteCardButton.setEnabled(true);
            }
        });
        loadPreferences();
        gridBagConstraints4.gridy++;
        gridBagConstraints4.weightx = 1.0d;
        gridBagConstraints4.gridwidth = 1;
        jPanel3.add(this.smartCards, gridBagConstraints4);
        gridBagConstraints4.gridx++;
        this.connectButton.addActionListener(actionEvent2 -> {
            connectSmartCard(this);
        });
        this.connectButton.setMnemonic('T');
        jPanel3.add(this.connectButton, gridBagConstraints4);
        gridBagConstraints4.gridx++;
        this.addCardButton.setMnemonic('J');
        this.addCardButton.addActionListener(actionEvent3 -> {
            addSmartCardDlg(this);
        });
        jPanel3.add(this.addCardButton, gridBagConstraints4);
        gridBagConstraints4.gridx++;
        this.modifyCardButton.setMnemonic('I');
        this.modifyCardButton.addActionListener(actionEvent4 -> {
            modifySmartCardDlg(this);
        });
        jPanel3.add(this.modifyCardButton, gridBagConstraints4);
        gridBagConstraints4.gridx++;
        this.deleteCardButton.setMnemonic('M');
        this.deleteCardButton.addActionListener(actionEvent5 -> {
            deleteSmartCardDlg(this, ((RegisteredKeystore) this.smartCards.getSelectedItem()).getName());
        });
        jPanel3.add(this.deleteCardButton, gridBagConstraints4);
        gridBagConstraints.gridy++;
        jPanel.add(jPanel3, gridBagConstraints);
        gridBagConstraints.gridy++;
        gridBagConstraints.weighty = 1.0d;
        jPanel.add(new JPanel(), gridBagConstraints);
        JPanel jPanel4 = new JPanel(new FlowLayout(4));
        JButton jButton = new JButton(SimpleAfirmaMessages.getString("PreferencesPanel.147"));
        jButton.setMnemonic('R');
        jButton.addActionListener(actionEvent6 -> {
            if (AOUIFactory.showConfirmDialog(getParent(), SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.15"), SimpleAfirmaMessages.getString("PreferencesPanel.139"), 0, 2) == 0) {
                restorePreferences();
            }
        });
        jButton.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("PreferencesPanel.136"));
        gridBagConstraints.gridy++;
        gridBagConstraints.weighty = 0.0d;
        jPanel4.add(jButton, gridBagConstraints);
        gridBagConstraints.gridy++;
        jPanel.add(jPanel4, gridBagConstraints);
        setViewportView(jPanel);
    }

    private static void loadKeyStoresComboBox(JComboBox<RegisteredKeystore> jComboBox, JComboBox<RegisteredKeystore> jComboBox2) {
        Platform.OS os = Platform.getOS();
        if (Platform.OS.WINDOWS.equals(os)) {
            jComboBox.addItem(new RegisteredKeystore(AOKeyStore.WINDOWS));
            if (SimpleKeyStoreManager.isFirefoxAvailable()) {
                jComboBox.addItem(new RegisteredKeystore(AOKeyStore.MOZ_UNI));
            }
        } else if (Platform.OS.MACOSX.equals(os)) {
            jComboBox.addItem(new RegisteredKeystore(AOKeyStore.APPLE));
            if (SimpleKeyStoreManager.isFirefoxAvailable()) {
                jComboBox.addItem(new RegisteredKeystore(AOKeyStore.MOZ_UNI));
            }
        } else {
            jComboBox.addItem(new RegisteredKeystore(AOKeyStore.SHARED_NSS));
        }
        jComboBox.addItem(new RegisteredKeystore(AOKeyStore.PKCS12));
        jComboBox.addItem(new RegisteredKeystore(AOKeyStore.DNIEJAVA));
        if (jComboBox2 != null && jComboBox2.getItemCount() > 1) {
            for (int i = 1; i < jComboBox2.getItemCount(); i++) {
                jComboBox.addItem(jComboBox2.getItemAt(i));
            }
        }
    }

    private static void loadSmartCardsComboBox(JComboBox<RegisteredKeystore> jComboBox) {
        RegisteredKeystore registeredKeystore = new RegisteredKeystore();
        registeredKeystore.setName(SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.31"));
        jComboBox.addItem(registeredKeystore);
        Map<String, String> userSmartCardsRegistered = KeyStorePreferencesManager.getUserSmartCardsRegistered();
        if (!userSmartCardsRegistered.isEmpty()) {
            for (String str : userSmartCardsRegistered.keySet()) {
                RegisteredKeystore registeredKeystore2 = new RegisteredKeystore(AOKeyStore.PKCS11);
                registeredKeystore2.setName(str);
                registeredKeystore2.setLib(userSmartCardsRegistered.get(str));
                jComboBox.addItem(registeredKeystore2);
            }
        }
        Map<String, String> systemSmartCardsRegistered = KeyStorePreferencesManager.getSystemSmartCardsRegistered();
        if (!systemSmartCardsRegistered.isEmpty()) {
            for (String str2 : systemSmartCardsRegistered.keySet()) {
                RegisteredKeystore registeredKeystore3 = new RegisteredKeystore(AOKeyStore.PKCS11, true);
                registeredKeystore3.setName(str2);
                registeredKeystore3.setLib(systemSmartCardsRegistered.get(str2));
                jComboBox.addItem(registeredKeystore3);
            }
        }
    }

    void connectSmartCard(Container container) {
        AOKeyStore aOKeyStore = AOKeyStore.PKCS11;
        aOKeyStore.setName(((RegisteredKeystore) this.smartCards.getSelectedItem()).getName());
        container.setCursor(new Cursor(3));
        try {
            if (AOKeyStoreManagerFactory.getAOKeyStoreManager(aOKeyStore, ((RegisteredKeystore) this.smartCards.getSelectedItem()).getLib(), ((RegisteredKeystore) this.smartCards.getSelectedItem()).getName(), aOKeyStore.getStorePasswordCallback(container), container) != null) {
                AOUIFactory.showMessageDialog(container, SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.29"), SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.30"), AOUIFactory.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.28"), SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.18", aOKeyStore.toString()), 0, e);
            Logger.getLogger("es.gob.afirma").warning("Error al conectar tarjeta inteligente: " + e);
        }
        container.setCursor(new Cursor(0));
    }

    public void addSmartCardDlg(Container container) {
        SmartCardPanel smartCardPanel = new SmartCardPanel();
        smartCardPanel.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("SmartCardDialog.0"));
        if (AOUIFactory.showConfirmDialog(container, smartCardPanel, SimpleAfirmaMessages.getString("SmartCardDialog.0"), 2, -1) == 0) {
            String text = smartCardPanel.getCardNameTxt().getText();
            String text2 = smartCardPanel.getControllerPathTxt().getText();
            if (text.isEmpty() || text2.isEmpty()) {
                AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.27"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), AOUIFactory.ERROR_MESSAGE, new Exception(SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.27")));
                addSmartCardDlg(container);
                return;
            }
            if (checkDuplicatedName(text)) {
                AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.34"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), AOUIFactory.ERROR_MESSAGE, new Exception(SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.34")));
                addSmartCardDlg(container);
                return;
            }
            if (checkDuplicatedLib(text2)) {
                AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.35"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), AOUIFactory.ERROR_MESSAGE, new Exception(SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.35")));
                addSmartCardDlg(container);
                return;
            }
            container.setCursor(new Cursor(3));
            LOGGER.info("Creamos almacen para la tarjeta inteligente: " + LoggerUtil.getTrimStr(text));
            if (KeyStorePreferencesManager.addSmartCardToUserRec(text, text2)) {
                RegisteredKeystore registeredKeystore = new RegisteredKeystore();
                registeredKeystore.setName(text);
                registeredKeystore.setLib(text2);
                registeredKeystore.setProviderName(AOKeyStore.PKCS11.getProviderName());
                this.smartCards.addItem(registeredKeystore);
                this.smartCards.setSelectedItem(registeredKeystore);
                this.smartCards.repaint();
                this.keystores.addItem(registeredKeystore);
                this.keystores.repaint();
            }
            if (this.smartCards.getItemCount() > 1) {
                this.connectButton.setEnabled(true);
                this.modifyCardButton.setEnabled(true);
                this.deleteCardButton.setEnabled(true);
            }
            container.setCursor(new Cursor(0));
        }
    }

    public void modifySmartCardDlg(Container container) {
        container.setCursor(new Cursor(3));
        SmartCardPanel smartCardPanel = new SmartCardPanel();
        smartCardPanel.getAccessibleContext().setAccessibleDescription(SimpleAfirmaMessages.getString("SmartCardDialog.9"));
        String name = ((RegisteredKeystore) this.smartCards.getSelectedItem()).getName();
        String lib = ((RegisteredKeystore) this.smartCards.getSelectedItem()).getLib();
        smartCardPanel.getCardNameTxt().setText(name);
        smartCardPanel.getControllerPathTxt().setText(lib);
        smartCardPanel.repaint();
        container.setCursor(new Cursor(0));
        if (AOUIFactory.showConfirmDialog(container, smartCardPanel, SimpleAfirmaMessages.getString("SmartCardDialog.9"), 2, -1) == 0) {
            if (smartCardPanel.getCardNameTxt().getText().isEmpty() || smartCardPanel.getControllerPathTxt().getText().isEmpty()) {
                AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.27"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), AOUIFactory.ERROR_MESSAGE, new Exception(SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.27")));
                modifySmartCardDlg(container);
                return;
            }
            if (checkDuplicatedName(smartCardPanel.getCardNameTxt().getText())) {
                AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.34"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), AOUIFactory.ERROR_MESSAGE, new Exception(SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.34")));
                modifySmartCardDlg(container);
                return;
            }
            if (checkDuplicatedLib(smartCardPanel.getControllerPathTxt().getText())) {
                AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.35"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), AOUIFactory.ERROR_MESSAGE, new Exception(SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.35")));
                modifySmartCardDlg(container);
                return;
            }
            if (KeyStorePreferencesManager.deleteSmartCardRec(name)) {
                for (int i = 0; i < this.smartCards.getItemCount(); i++) {
                    System.out.println(((RegisteredKeystore) this.smartCards.getItemAt(i)).getName());
                    if (((RegisteredKeystore) this.smartCards.getItemAt(i)).getName().equals(name)) {
                        this.smartCards.removeItemAt(i);
                    }
                }
                for (int i2 = 0; i2 < this.keystores.getItemCount(); i2++) {
                    if (((RegisteredKeystore) this.keystores.getItemAt(i2)).getName().equals(name)) {
                        this.keystores.removeItemAt(i2);
                    }
                }
            }
            this.keystores.repaint();
            this.smartCards.repaint();
            if (KeyStorePreferencesManager.addSmartCardToUserRec(smartCardPanel.getCardNameTxt().getText(), smartCardPanel.getControllerPathTxt().getText())) {
                RegisteredKeystore registeredKeystore = new RegisteredKeystore();
                registeredKeystore.setName(smartCardPanel.getCardNameTxt().getText());
                registeredKeystore.setLib(smartCardPanel.getControllerPathTxt().getText());
                registeredKeystore.setProviderName(AOKeyStore.PKCS11.getProviderName());
                this.smartCards.addItem(registeredKeystore);
                this.smartCards.setSelectedItem(registeredKeystore);
                this.smartCards.repaint();
                this.keystores.addItem(registeredKeystore);
                this.keystores.repaint();
            }
        }
    }

    public void deleteSmartCardDlg(Container container, String str) {
        container.setCursor(new Cursor(3));
        container.setCursor(new Cursor(0));
        if (AOUIFactory.showConfirmDialog(container, SimpleAfirmaMessages.getString("PreferencesPanelKeyStores.25"), SimpleAfirmaMessages.getString("SmartCardDialog.10"), 2, -1) == 0) {
            LOGGER.info("Eliminamos el almacen de la tarjeta inteligente " + LoggerUtil.getTrimStr(str));
            if (KeyStorePreferencesManager.deleteSmartCardRec(str)) {
                for (int i = 0; i < this.keystores.getItemCount(); i++) {
                    if (((RegisteredKeystore) this.keystores.getItemAt(i)).getName().equals(str)) {
                        this.keystores.removeItemAt(i);
                    }
                }
                for (int i2 = 0; i2 < this.smartCards.getItemCount(); i2++) {
                    if (((RegisteredKeystore) this.smartCards.getItemAt(i2)).getName().equals(str)) {
                        this.smartCards.removeItemAt(i2);
                    }
                }
            }
            this.keystores.repaint();
            this.smartCards.repaint();
            if (this.smartCards.getItemCount() == 1 || this.smartCards.getSelectedIndex() == 0) {
                this.connectButton.setEnabled(false);
                this.modifyCardButton.setEnabled(false);
                this.deleteCardButton.setEnabled(false);
            }
        }
    }

    private boolean checkDuplicatedName(String str) {
        for (int i = 0; i < this.smartCards.getItemCount(); i++) {
            if (str.equals(((RegisteredKeystore) this.smartCards.getItemAt(i)).getName())) {
                return true;
            }
        }
        return false;
    }

    private boolean checkDuplicatedLib(String str) {
        for (int i = 0; i < this.smartCards.getItemCount(); i++) {
            if (str.equals(((RegisteredKeystore) this.smartCards.getItemAt(i)).getLib())) {
                return true;
            }
        }
        return false;
    }

    void savePreferences() {
        if (this.localKeystoreSelectedPath != null) {
            PreferencesManager.put(PreferencesManager.PREFERENCE_LOCAL_KEYSTORE_PATH, this.localKeystoreSelectedPath);
            KeyStorePreferencesManager.setLastSelectedKeystoreLib(this.localKeystoreSelectedPath);
        }
        PreferencesManager.putBoolean(PreferencesManager.PREFERENCE_USE_DEFAULT_STORE_IN_BROWSER_CALLS, this.callsFromNavigator.isSelected());
        RegisteredKeystore registeredKeystore = (RegisteredKeystore) this.keystores.getSelectedItem();
        AOKeyStore keyStore = SimpleKeyStoreManager.getKeyStore(registeredKeystore.getName());
        if (keyStore == null && AOKeyStore.PKCS11.getProviderName().equals(registeredKeystore.getProviderName())) {
            keyStore = AOKeyStore.PKCS11;
            keyStore.setName(registeredKeystore.getName());
        }
        if (keyStore != null && AOKeyStore.PKCS11.getProviderName().equals(keyStore.getProviderName()) && registeredKeystore.getLib() != null) {
            PreferencesManager.put(PreferencesManager.PREFERENCE_LOCAL_KEYSTORE_PATH, registeredKeystore.getLib());
        }
        if (AOKeyStore.PKCS12.getName().equals(registeredKeystore.getName())) {
            keyStore = AOKeyStore.PKCS12;
        }
        if (keyStore != null) {
            String name = keyStore.getName();
            if (this.keystores.getSelectedIndex() == 0) {
                name = "default";
            }
            PreferencesManager.put(PreferencesManager.PREFERENCE_KEYSTORE_DEFAULT_STORE, name);
            KeyStorePreferencesManager.setLastSelectedKeystore(keyStore.getName());
        }
        PreferencesManager.putBoolean(PreferencesManager.PREFERENCE_KEYSTORE_SHOWEXPIREDCERTS, this.showExpiredCerts.isSelected());
        PreferencesManager.putBoolean(PreferencesManager.PREFERENCE_GENERAL_HIDE_DNIE_START_SCREEN, this.hideDniStartScreen.isSelected());
        PreferencesManager.putBoolean(PreferencesManager.PREFERENCE_KEYSTORE_SIGN_ONLY_CERTS, this.onlySignature.isSelected());
        PreferencesManager.putBoolean(PreferencesManager.PREFERENCE_KEYSTORE_ALIAS_ONLY_CERTS, this.onlyAlias.isSelected());
        PreferencesManager.putBoolean(PreferencesManager.PREFERENCE_KEYSTORE_SKIP_AUTH_CERT_DNIE, this.skipAuthCertDnie.isSelected());
    }

    void loadPreferences() {
        String str = PreferencesManager.get(PreferencesManager.PREFERENCE_KEYSTORE_DEFAULT_STORE);
        if ("default".equals(str)) {
            this.keystores.setSelectedIndex(0);
        } else {
            AOKeyStore keyStore = SimpleKeyStoreManager.getKeyStore(str);
            RegisteredKeystore registeredKeystore = null;
            if (str != null) {
                if (keyStore == null) {
                    Map<String, String> userSmartCardsRegistered = KeyStorePreferencesManager.getUserSmartCardsRegistered();
                    if (!userSmartCardsRegistered.isEmpty()) {
                        for (String str2 : userSmartCardsRegistered.keySet()) {
                            if (str.equals(str2)) {
                                registeredKeystore = new RegisteredKeystore(AOKeyStore.PKCS11, false);
                                registeredKeystore.setName(str2);
                            }
                        }
                    }
                    Map<String, String> systemSmartCardsRegistered = KeyStorePreferencesManager.getSystemSmartCardsRegistered();
                    if (!systemSmartCardsRegistered.isEmpty()) {
                        for (String str3 : systemSmartCardsRegistered.keySet()) {
                            if (str.equals(str3)) {
                                registeredKeystore = new RegisteredKeystore(AOKeyStore.PKCS11, true);
                                registeredKeystore.setName(str3);
                            }
                        }
                    }
                } else {
                    registeredKeystore = new RegisteredKeystore(keyStore, false);
                }
            }
            if (registeredKeystore != null) {
                for (int i = 0; i < this.keystores.getItemCount(); i++) {
                    if (registeredKeystore.getName().equals(((RegisteredKeystore) this.keystores.getItemAt(i)).getName())) {
                        this.keystores.setSelectedIndex(i);
                    }
                }
            }
        }
        this.keystores.repaint();
        this.callsFromNavigator.setSelected(PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_USE_DEFAULT_STORE_IN_BROWSER_CALLS));
        this.hideDniStartScreen.setSelected(PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_GENERAL_HIDE_DNIE_START_SCREEN));
        this.showExpiredCerts.setSelected(PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_KEYSTORE_SHOWEXPIREDCERTS));
        this.onlySignature.setSelected(PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_KEYSTORE_SIGN_ONLY_CERTS));
        this.onlyAlias.setSelected(PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_KEYSTORE_ALIAS_ONLY_CERTS));
        this.skipAuthCertDnie.setSelected(PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_KEYSTORE_SKIP_AUTH_CERT_DNIE));
        revalidate();
        repaint();
    }

    void restorePreferences() {
        for (String str : (String[]) KeyStorePreferencesManager.getUserSmartCardsRegistered().keySet().toArray(new String[0])) {
            KeyStorePreferencesManager.deleteSmartCardRec(str);
        }
        this.smartCards.removeAllItems();
        loadSmartCardsComboBox(this.smartCards);
        this.smartCards.repaint();
        this.keystores.removeAllItems();
        loadKeyStoresComboBox(this.keystores, this.smartCards);
        this.keystores.repaint();
        try {
            KeyStorePreferencesManager.clearKeyStorePrefs();
        } catch (BackingStoreException e) {
            LOGGER.log(Level.WARNING, "No se puede borrar la configuracion relativa a las tarjetas", (Throwable) e);
        }
        PreferencesManager.remove(PreferencesManager.PREFERENCE_KEYSTORE_DEFAULT_STORE);
        PreferencesManager.remove(PreferencesManager.PREFERENCE_USE_DEFAULT_STORE_IN_BROWSER_CALLS);
        PreferencesManager.remove(PreferencesManager.PREFERENCE_GENERAL_HIDE_DNIE_START_SCREEN);
        PreferencesManager.remove(PreferencesManager.PREFERENCE_KEYSTORE_SHOWEXPIREDCERTS);
        PreferencesManager.remove(PreferencesManager.PREFERENCE_KEYSTORE_SIGN_ONLY_CERTS);
        PreferencesManager.remove(PreferencesManager.PREFERENCE_KEYSTORE_ALIAS_ONLY_CERTS);
        PreferencesManager.remove(PreferencesManager.PREFERENCE_KEYSTORE_SKIP_AUTH_CERT_DNIE);
        try {
            PreferencesManager.flush();
        } catch (BackingStoreException e2) {
            LOGGER.log(Level.WARNING, "Error al restaurar la configuracion de almacenes", (Throwable) e2);
        }
        loadPreferences();
    }

    static List<? extends CertificateFilter> getCertFilters() {
        ArrayList arrayList = new ArrayList();
        if (PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_KEYSTORE_SIGN_ONLY_CERTS)) {
            arrayList.add(new KeyUsageFilter(KeyUsageFilter.SIGN_CERT_USAGE));
        }
        if (PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_KEYSTORE_SKIP_AUTH_CERT_DNIE)) {
            arrayList.add(new SkipAuthDNIeFilter());
        }
        if (PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_KEYSTORE_ALIAS_ONLY_CERTS)) {
            arrayList.add(new PseudonymFilter(PseudonymFilter.VALUE_ONLY));
        }
        if (arrayList.size() > 1) {
            return Arrays.asList(new MultipleCertificateFilter((CertificateFilter[]) arrayList.toArray(new CertificateFilter[0])));
        }
        if (arrayList.size() == 1) {
            return arrayList;
        }
        return null;
    }

    public boolean isBlocked() {
        return this.blocked;
    }

    public void setBlocked(boolean z) {
        this.blocked = z;
    }
}
