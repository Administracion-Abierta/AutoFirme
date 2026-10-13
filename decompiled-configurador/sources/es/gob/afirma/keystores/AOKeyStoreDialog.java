package es.gob.afirma.keystores;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.AOException;
import es.gob.afirma.core.keystores.CertificateContext;
import es.gob.afirma.core.keystores.KeyStoreManager;
import es.gob.afirma.core.keystores.NameCertificateBean;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.core.prefs.KeyStorePreferencesManager;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.core.ui.KeyStoreDialogManager;
import java.io.IOException;
import java.security.KeyStore;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/keystores/AOKeyStoreDialog.class */
public final class AOKeyStoreDialog implements KeyStoreDialogManager {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String[] EXTS = {"pfx", "p12"};
    private static final String EXTS_DESC = " (*.p12, *.pfx)";
    private final AggregatedKeyStoreManager ksm;
    private final Object parentComponent;
    private final boolean checkPrivateKeys;
    private final boolean checkValidity;
    private final boolean showExpiredCertificates;
    private final List<? extends CertificateFilter> certFilters;
    private final boolean mandatoryCertificate;
    private String selectedAlias;
    private boolean allowExternalStores;
    private final String libFileName;

    public AOKeyStoreDialog(AOKeyStoreManager aOKeyStoreManager, Object obj, boolean z, boolean z2, boolean z3) {
        this(aOKeyStoreManager, obj, z, z2, z3, null, false);
    }

    public AOKeyStoreDialog(AOKeyStoreManager aOKeyStoreManager, Object obj, boolean z, boolean z2, boolean z3, String str) {
        this(aOKeyStoreManager, obj, z, z2, z3, null, false, str);
    }

    public AOKeyStoreDialog(AOKeyStoreManager aOKeyStoreManager, Object obj, boolean z, boolean z2, boolean z3, List<? extends CertificateFilter> list, boolean z4) {
        this.selectedAlias = null;
        this.allowExternalStores = true;
        if (aOKeyStoreManager == null) {
            throw new IllegalArgumentException("El almacen de claves no puede ser nulo");
        }
        this.ksm = new AggregatedKeyStoreManager(aOKeyStoreManager);
        this.parentComponent = obj;
        this.checkPrivateKeys = z;
        this.checkValidity = z3;
        this.showExpiredCertificates = z2;
        this.certFilters = list != null ? new ArrayList(list) : null;
        this.mandatoryCertificate = z4;
        this.libFileName = null;
    }

    public AOKeyStoreDialog(AOKeyStoreManager aOKeyStoreManager, Object obj, boolean z, boolean z2, boolean z3, List<? extends CertificateFilter> list, boolean z4, String str) {
        this.selectedAlias = null;
        this.allowExternalStores = true;
        if (aOKeyStoreManager == null) {
            throw new IllegalArgumentException("El almacen de claves no puede ser nulo");
        }
        this.ksm = new AggregatedKeyStoreManager(aOKeyStoreManager);
        this.parentComponent = obj;
        this.checkPrivateKeys = z;
        this.checkValidity = z3;
        this.showExpiredCertificates = z2;
        this.certFilters = list != null ? new ArrayList(list) : null;
        this.mandatoryCertificate = z4;
        this.libFileName = str;
    }

    @Override // es.gob.afirma.core.ui.KeyStoreDialogManager
    public NameCertificateBean[] getNameCertificates() {
        Map<String, String> aliasesByFriendlyName = KeyStoreUtilities.getAliasesByFriendlyName(this.ksm.getAliases(), this.ksm, this.checkPrivateKeys, this.showExpiredCertificates, this.certFilters);
        int i = 0;
        NameCertificateBean[] nameCertificateBeanArr = new NameCertificateBean[aliasesByFriendlyName.size()];
        for (String str : (String[]) aliasesByFriendlyName.keySet().toArray(new String[aliasesByFriendlyName.size()])) {
            X509Certificate[] certificateChain = this.ksm.getCertificateChain(str);
            if (certificateChain != null) {
                int i2 = i;
                i++;
                nameCertificateBeanArr[i2] = new NameCertificateBean(str, aliasesByFriendlyName.get(str), certificateChain);
            } else {
                LOGGER.warning("Se ha encontrado un certificado nulo en el almacen");
            }
        }
        return nameCertificateBeanArr;
    }

    @Override // es.gob.afirma.core.ui.KeyStoreDialogManager
    public void setKeyStoreManager(KeyStoreManager keyStoreManager) {
        this.ksm.removeAll();
        this.ksm.addKeyStoreManager((AOKeyStoreManager) keyStoreManager);
    }

    @Override // es.gob.afirma.core.ui.KeyStoreDialogManager
    public boolean changeKeyStoreManager(int i, Object obj) {
        AOKeyStoreManager aOKeyStoreManagerOpenSystemKeyStore = null;
        try {
            switch (i) {
                case 1:
                default:
                    aOKeyStoreManagerOpenSystemKeyStore = openSystemKeyStore(obj);
                    if (aOKeyStoreManagerOpenSystemKeyStore != null) {
                        KeyStorePreferencesManager.setLastSelectedKeystore(aOKeyStoreManagerOpenSystemKeyStore.getType().getName());
                    }
                    break;
                case 2:
                    aOKeyStoreManagerOpenSystemKeyStore = openMozillaKeyStore(obj);
                    if (aOKeyStoreManagerOpenSystemKeyStore != null) {
                        KeyStorePreferencesManager.setLastSelectedKeystore(AOKeyStore.MOZ_UNI.getName());
                    }
                    break;
                case 3:
                    aOKeyStoreManagerOpenSystemKeyStore = openPkcs12KeyStore(obj, null);
                    if (aOKeyStoreManagerOpenSystemKeyStore != null) {
                        KeyStorePreferencesManager.setLastSelectedKeystore(aOKeyStoreManagerOpenSystemKeyStore.getType().getName());
                    }
                    break;
                case 4:
                    aOKeyStoreManagerOpenSystemKeyStore = openDnieKeyStore(obj);
                    if (aOKeyStoreManagerOpenSystemKeyStore != null) {
                        KeyStorePreferencesManager.setLastSelectedKeystore(aOKeyStoreManagerOpenSystemKeyStore.getType().getName());
                    }
                    break;
            }
        } catch (AOCancelledOperationException e) {
            LOGGER.info("Operacion cancelada por el usuario: " + e);
            return false;
        } catch (IOException e2) {
            if (e2.getCause() != null && e2.getCause().getCause() != null && (e2.getCause().getCause() instanceof UnrecoverableKeyException)) {
                AOUIFactory.showMessageDialog(obj, KeyStoreMessages.getString("AOKeyStoreDialog.11"), KeyStoreMessages.getString("AOKeyStoreDialog.9"), AOUIFactory.ERROR_MESSAGE, e2);
                boolean z = false;
                while (!z) {
                    try {
                        if (!changeKeyStoreManager(i, obj)) {
                            z = true;
                        }
                    } catch (AOCancelledOperationException e3) {
                        LOGGER.info("Operacion cancelada por el usuario: " + e3);
                        z = true;
                    } catch (Exception e4) {
                        AOUIFactory.showErrorMessage(KeyStoreMessages.getString("AOKeyStoreDialog.10"), KeyStoreMessages.getString("AOKeyStoreDialog.9"), AOUIFactory.ERROR_MESSAGE, e4);
                        z = true;
                    }
                }
            }
        } catch (Exception e5) {
            LOGGER.log(Level.SEVERE, "Error cambiando de almacen de claves: " + e5, (Throwable) e5);
            AOUIFactory.showErrorMessage(KeyStoreMessages.getString("AOKeyStoreDialog.10"), KeyStoreMessages.getString("AOKeyStoreDialog.9"), AOUIFactory.ERROR_MESSAGE, e5);
            return false;
        }
        setKeyStoreManager(aOKeyStoreManagerOpenSystemKeyStore);
        return true;
    }

    @Override // es.gob.afirma.core.ui.KeyStoreDialogManager
    public boolean changeKeyStoreManagerToPKCS11(Object obj, String str, String str2) {
        AOKeyStoreManager aOKeyStoreManagerOpenPkcs11KeyStore = null;
        try {
            aOKeyStoreManagerOpenPkcs11KeyStore = openPkcs11KeyStore(obj, str, str2);
        } catch (AOCancelledOperationException e) {
            LOGGER.info("Operacion cancelada por el usuario: " + e);
            return false;
        } catch (IOException e2) {
            if (e2.getCause() != null && e2.getCause().getCause() != null && (e2.getCause().getCause() instanceof UnrecoverableKeyException)) {
                AOUIFactory.showMessageDialog(obj, KeyStoreMessages.getString("AOKeyStoreDialog.11"), KeyStoreMessages.getString("AOKeyStoreDialog.9"), AOUIFactory.ERROR_MESSAGE, e2);
                boolean z = false;
                while (!z) {
                    try {
                        if (!changeKeyStoreManagerToPKCS11(obj, str, str2)) {
                            z = true;
                        }
                    } catch (AOCancelledOperationException e3) {
                        LOGGER.info("Operacion cancelada por el usuario: " + e3);
                        z = true;
                    } catch (Exception e4) {
                        AOUIFactory.showErrorMessage(KeyStoreMessages.getString("AOKeyStoreDialog.10"), KeyStoreMessages.getString("AOKeyStoreDialog.9"), AOUIFactory.ERROR_MESSAGE, e4);
                        z = true;
                    }
                }
            }
        } catch (Exception e5) {
            LOGGER.log(Level.SEVERE, "Error cambiando de almacen de claves: " + e5, (Throwable) e5);
            AOUIFactory.showErrorMessage(KeyStoreMessages.getString("AOKeyStoreDialog.10"), KeyStoreMessages.getString("AOKeyStoreDialog.9"), AOUIFactory.ERROR_MESSAGE, e5);
            return false;
        }
        setKeyStoreManager(aOKeyStoreManagerOpenPkcs11KeyStore);
        if (aOKeyStoreManagerOpenPkcs11KeyStore != null && aOKeyStoreManagerOpenPkcs11KeyStore.getType() != null) {
            KeyStorePreferencesManager.setLastSelectedKeystore(aOKeyStoreManagerOpenPkcs11KeyStore.getType().getName());
            KeyStorePreferencesManager.setLastSelectedKeystoreLib(str2);
            return true;
        }
        return true;
    }

    @Override // es.gob.afirma.core.ui.KeyStoreDialogManager
    public int[] getAvailablesKeyStores() {
        int[] iArr;
        if (Platform.getOS() == Platform.OS.LINUX) {
            if (this.ksm.getType() == AOKeyStore.SHARED_NSS || (this.ksm.getKeyStoreManagers().size() > 0 && this.ksm.getKeyStoreManagers().get(0).getType() == AOKeyStore.SHARED_NSS)) {
                iArr = new int[]{1, 3, 4};
            } else {
                iArr = new int[]{2, 3, 4};
            }
        } else {
            iArr = new int[]{1, 2, 3, 4};
        }
        return iArr;
    }

    @Override // es.gob.afirma.core.ui.KeyStoreDialogManager
    public String getKeyStoreName() {
        AOKeyStoreManager aOKeyStoreManager;
        List<AOKeyStoreManager> keyStoreManagers = this.ksm.getKeyStoreManagers();
        if (keyStoreManagers != null && keyStoreManagers.size() > 0) {
            aOKeyStoreManager = keyStoreManagers.get(0);
        } else {
            aOKeyStoreManager = this.ksm;
        }
        if (aOKeyStoreManager == null || aOKeyStoreManager.getType() == null) {
            return null;
        }
        return aOKeyStoreManager.getType().getName();
    }

    private static AOKeyStoreManager openMozillaKeyStore(Object obj) throws Exception {
        try {
            return AOKeyStoreManagerFactory.getAOKeyStoreManager(AOKeyStore.MOZ_UNI, null, null, AOKeyStore.MOZ_UNI.getStorePasswordCallback(obj), obj);
        } catch (AOCancelledOperationException e) {
            throw e;
        } catch (Exception e2) {
            LOGGER.log(Level.WARNING, "No se ha podido cargar el almacen de claves de Mozilla: " + e2, (Throwable) e2);
            throw e2;
        }
    }

    public static AOKeyStoreManager openPkcs12KeyStore(Object obj, String str) throws Exception {
        String absolutePath = str;
        AOKeyStoreManager aOKeyStoreManager = null;
        if (absolutePath == null || absolutePath.isEmpty()) {
            absolutePath = AOUIFactory.getLoadFiles(KeyStoreMessages.getString("AOKeyStoreDialog.6"), null, null, EXTS, KeyStoreMessages.getString("AOKeyStoreDialog.7") + EXTS_DESC, false, false, null, obj)[0].getAbsolutePath();
        }
        try {
            aOKeyStoreManager = AOKeyStoreManagerFactory.getAOKeyStoreManager(AOKeyStore.PKCS12, absolutePath, null, AOKeyStore.PKCS12.getStorePasswordCallback(obj), obj);
            KeyStorePreferencesManager.setLastSelectedKeystoreLib(absolutePath);
        } catch (AOCancelledOperationException e) {
            throw e;
        } catch (IOException e2) {
            if (e2.getCause() != null && e2.getCause().getCause() != null && (e2.getCause().getCause() instanceof UnrecoverableKeyException)) {
                AOUIFactory.showMessageDialog(obj, KeyStoreMessages.getString("AOKeyStoreDialog.11"), KeyStoreMessages.getString("AOKeyStoreDialog.9"), AOUIFactory.ERROR_MESSAGE, e2);
                boolean z = false;
                while (!z) {
                    try {
                        AOKeyStoreManager aOKeyStoreManagerOpenPkcs12KeyStore = openPkcs12KeyStore(obj, absolutePath);
                        if (aOKeyStoreManagerOpenPkcs12KeyStore != null) {
                            aOKeyStoreManager = aOKeyStoreManagerOpenPkcs12KeyStore;
                            z = true;
                        }
                    } catch (AOCancelledOperationException e3) {
                        LOGGER.info("Operacion cancelada por el usuario: " + e3);
                        throw e3;
                    } catch (Exception e4) {
                        AOUIFactory.showErrorMessage(KeyStoreMessages.getString("AOKeyStoreDialog.10"), KeyStoreMessages.getString("AOKeyStoreDialog.9"), AOUIFactory.ERROR_MESSAGE, e4);
                        throw e4;
                    }
                }
            }
        } catch (Exception e5) {
            LOGGER.log(Level.WARNING, "No se ha podido cargar el almacen de claves PKCS#12 seleccionado: " + e5, (Throwable) e5);
            throw e5;
        }
        return aOKeyStoreManager;
    }

    private static AOKeyStoreManager openDnieKeyStore(Object obj) throws Exception {
        AOKeyStoreManager aOKeyStoreManager = new AOKeyStoreManager();
        try {
            aOKeyStoreManager.init(AOKeyStore.DNIEJAVA, null, null, new Object[]{obj}, true);
            return aOKeyStoreManager;
        } catch (AOCancelledOperationException e) {
            throw e;
        } catch (Exception e2) {
            LOGGER.log(Level.WARNING, "No se ha podido cargar el DNIe: " + e2, (Throwable) e2);
            throw e2;
        }
    }

    public static AOKeyStoreManager openPkcs11KeyStore(Object obj, String str, String str2) throws Exception {
        try {
            AOKeyStore.PKCS11.setName(str);
            return AOKeyStoreManagerFactory.getAOKeyStoreManager(AOKeyStore.PKCS11, str2, null, AOKeyStore.PKCS11.getStorePasswordCallback(obj), obj);
        } catch (AOCancelledOperationException e) {
            throw e;
        } catch (Exception e2) {
            LOGGER.log(Level.WARNING, "No se ha podido cargar el almacen de claves PKCS#11 con nombre: " + str);
            throw e2;
        }
    }

    private static AOKeyStoreManager openSystemKeyStore(Object obj) throws Exception {
        AOKeyStore aOKeyStore;
        Platform.OS os = Platform.getOS();
        if (os == Platform.OS.WINDOWS) {
            aOKeyStore = AOKeyStore.WINDOWS;
        } else if (os == Platform.OS.LINUX || os == Platform.OS.SOLARIS) {
            aOKeyStore = AOKeyStore.SHARED_NSS;
        } else if (os == Platform.OS.MACOSX) {
            aOKeyStore = AOKeyStore.APPLE;
        } else {
            throw new AOKeystoreAlternativeException(null, "No se ha podido identificar un almacen del sistema compatible");
        }
        try {
            return AOKeyStoreManagerFactory.getAOKeyStoreManager(aOKeyStore, null, null, aOKeyStore.getStorePasswordCallback(obj), obj);
        } catch (AOCancelledOperationException e) {
            throw e;
        } catch (Exception e2) {
            LOGGER.log(Level.WARNING, "No se ha podido cargar el almacen del sistema: " + e2, (Throwable) e2);
            throw e2;
        }
    }

    @Override // es.gob.afirma.core.ui.KeyStoreDialogManager
    public Object getKeyEntry(String str) throws AOException {
        KeyStore.PrivateKeyEntry keyEntry = null;
        if (this.checkPrivateKeys) {
            try {
                keyEntry = this.ksm.getKeyEntry(str);
            } catch (Exception e) {
                LOGGER.severe("No se ha podido extraer la clave del almacen: " + e);
                throw new AOException("No se ha podido extraer la clave del almacen", e);
            }
        }
        this.selectedAlias = str;
        if (this.checkValidity && this.ksm != null) {
            String string = null;
            try {
                this.ksm.getCertificate(this.selectedAlias).checkValidity();
            } catch (CertificateExpiredException e2) {
                string = KeyStoreMessages.getString("AOKeyStoreDialog.2");
            } catch (CertificateNotYetValidException e3) {
                string = KeyStoreMessages.getString("AOKeyStoreDialog.3");
            } catch (Exception e4) {
                string = KeyStoreMessages.getString("AOKeyStoreDialog.4");
            }
            boolean z = false;
            if (string != null) {
                LOGGER.warning("Error durante la validacion: " + string);
                if (AOUIFactory.showConfirmDialog(this.parentComponent, string, KeyStoreMessages.getString("AOKeyStoreDialog.5"), AOUIFactory.YES_NO_OPTION, AOUIFactory.WARNING_MESSAGE) != AOUIFactory.YES_OPTION) {
                    z = true;
                }
            }
            if (z) {
                throw new AOCancelledOperationException("Se ha reusado un certificado probablemente no valido");
            }
        }
        return this.checkPrivateKeys ? keyEntry : this.ksm.getCertificateChain(str);
    }

    @Override // es.gob.afirma.core.ui.KeyStoreDialogManager
    public String show() throws AOCertificatesNotFoundException {
        NameCertificateBean[] nameCertificates = getNameCertificates();
        if (this.mandatoryCertificate && nameCertificates != null && nameCertificates.length == 1) {
            this.selectedAlias = nameCertificates[0].getAlias();
            return this.selectedAlias;
        }
        try {
            this.selectedAlias = AOUIFactory.showCertificateSelectionDialog(this.parentComponent, this);
            if (this.selectedAlias == null) {
                throw new AOCancelledOperationException("No se ha seleccionado certificado");
            }
            return this.selectedAlias;
        } catch (IllegalStateException e) {
            throw new AOCertificatesNotFoundException("No se han encontrado certificados validos en el almacen", e);
        }
    }

    @Override // es.gob.afirma.core.ui.KeyStoreDialogManager
    public String getSelectedAlias() {
        return this.selectedAlias;
    }

    @Override // es.gob.afirma.core.ui.KeyStoreDialogManager
    public CertificateContext getSelectedCertificateContext() {
        return new CertificateContext(this.ksm, this.selectedAlias);
    }

    @Override // es.gob.afirma.core.ui.KeyStoreDialogManager
    public void refresh() throws IOException {
        this.ksm.refresh();
    }

    @Override // es.gob.afirma.core.ui.KeyStoreDialogManager
    public void allowOpenExternalStores(boolean z) {
        this.allowExternalStores = z;
    }

    @Override // es.gob.afirma.core.ui.KeyStoreDialogManager
    public boolean isExternalStoresOpeningAllowed() {
        return this.allowExternalStores;
    }

    @Override // es.gob.afirma.core.ui.KeyStoreDialogManager
    public String getLibName() {
        return this.libFileName;
    }
}
