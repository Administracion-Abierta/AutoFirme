package es.gob.afirma.keystores;

import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.keystores.callbacks.CachePasswordCallback;
import es.gob.afirma.keystores.callbacks.NullPasswordCallback;
import es.gob.afirma.keystores.callbacks.UIPasswordCallback;
import javax.security.auth.callback.PasswordCallback;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/keystores/AOKeyStore.class */
public enum AOKeyStore {
    WINDOWS("Windows", 0, "Windows-MY", new CachePasswordCallback("winmydummy".toCharArray()), NullPasswordCallback.getInstance()),
    APPLE("Llavero de Mac", 1, "KeychainStore", new CachePasswordCallback("osxdummy".toCharArray()), new CachePasswordCallback("osxdummy".toCharArray())),
    SHARED_NSS("NSS", 2, "PKCS11", NullPasswordCallback.getInstance(), new UIPasswordCallback(KeyStoreMessages.getString("AOKeyStore.13"))),
    PKCS12("PKCS#12 / PFX", 3, "PKCS12", new UIPasswordCallback(KeyStoreMessages.getString("AOKeyStore.0")), new UIPasswordCallback(KeyStoreMessages.getString("AOKeyStore.1"))),
    JAVA("Java KeyStore / JKS", 5, "JKS", new UIPasswordCallback(KeyStoreMessages.getString("AOKeyStore.2")), new UIPasswordCallback(KeyStoreMessages.getString("AOKeyStore.3"))),
    PKCS11("PKCS#11", 6, "PKCS11", NullPasswordCallback.getInstance(), new UIPasswordCallback(KeyStoreMessages.getString("AOKeyStore.4"))),
    SINGLE("PKCS#7 / X.509", 7, "PKCS7", NullPasswordCallback.getInstance(), NullPasswordCallback.getInstance()),
    MOZ_UNI("Mozilla / Firefox (unificado)", 8, "PKCS11", NullPasswordCallback.getInstance(), new UIPasswordCallback(KeyStoreMessages.getString("AOKeyStore.5"))),
    JCEKS("Java Cryptography Extension KeyStore (JCEKS)", 9, "JCEKS", new UIPasswordCallback(KeyStoreMessages.getString("AOKeyStore.6")), new UIPasswordCallback(KeyStoreMessages.getString("AOKeyStore.7"))),
    JAVACE("Java KeyStore / JKS (Case Exact)", 10, "CaseExactJKS", new UIPasswordCallback(KeyStoreMessages.getString("AOKeyStore.8")), new UIPasswordCallback(KeyStoreMessages.getString("AOKeyStore.9"))),
    TEMD("TEMD (Tarjeta del Ministerio de Defensa)", 11, "PKCS11", null, new UIPasswordCallback(KeyStoreMessages.getString("AOKeyStore.14"))),
    WINADDRESSBOOK("Windows / Internet Explorer (otras personas / libreta de direcciones)", 12, "Windows-ADDRESSBOOK", new CachePasswordCallback("winotherdummy".toCharArray()), NullPasswordCallback.getInstance()),
    WINCA("Windows / Internet Explorer (CA intermedias)", 13, "Windows-ROOT", new CachePasswordCallback("wincadummy".toCharArray()), NullPasswordCallback.getInstance()),
    CERES("Tarjeta FNMT-RCM CERES", 14, "CERES", null, null),
    DNIEJAVA("DNIe y tarjetas FNMT-TIF", 15, "DNI", null, null),
    KNOWN_SMARTCARDS("Tarjetas inteligentes conocidas mediante PKCS#11", 16, "SMARTCARDS", null, null),
    SMARTCAFE("G&D SmartCafe con Applet PKCS#15", 17, "GDSCPKCS15", null, null),
    CERES_430("Tarjeta FNMT-RCM CERES 4.30 o superior", 18, "CERES430", null, null),
    OTHER;

    private String name;
    private final int ordinal;
    private String providerName;
    private PasswordCallback certificatePasswordCallback;
    private PasswordCallback storePasswordCallback;

    AOKeyStore(String str, int i, String str2, PasswordCallback passwordCallback, PasswordCallback passwordCallback2) {
        this.providerName = null;
        this.certificatePasswordCallback = null;
        this.storePasswordCallback = null;
        this.name = str;
        this.ordinal = i;
        this.providerName = str2;
        this.certificatePasswordCallback = passwordCallback;
        this.storePasswordCallback = passwordCallback2;
    }

    AOKeyStore() {
        this.providerName = null;
        this.certificatePasswordCallback = null;
        this.storePasswordCallback = null;
        this.name = "Tipo desconocido";
        this.ordinal = 18;
        this.providerName = null;
        this.certificatePasswordCallback = null;
        this.storePasswordCallback = null;
    }

    @Override // java.lang.Enum
    public String toString() {
        return getName();
    }

    public String getProviderName() {
        return this.providerName;
    }

    public int getOrdinal() {
        return this.ordinal;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public PasswordCallback getCertificatePasswordCallback(Object obj) {
        if (this.certificatePasswordCallback instanceof UIPasswordCallback) {
            ((UIPasswordCallback) this.certificatePasswordCallback).setParent(obj);
        }
        return this.certificatePasswordCallback;
    }

    public PasswordCallback getStorePasswordCallback(Object obj) {
        return getStorePasswordCallback(obj, null);
    }

    public PasswordCallback getStorePasswordCallback(Object obj, String str) {
        if (this.storePasswordCallback instanceof UIPasswordCallback) {
            ((UIPasswordCallback) this.storePasswordCallback).setParent(obj);
            ((UIPasswordCallback) this.storePasswordCallback).setPrompt(str);
        }
        return this.storePasswordCallback;
    }

    public static AOKeyStore getDefaultKeyStoreTypeByOs(Platform.OS os) {
        AOKeyStore aOKeyStore = null;
        if (Platform.OS.WINDOWS.equals(os)) {
            aOKeyStore = WINDOWS;
        }
        if (Platform.OS.MACOSX.equals(os)) {
            aOKeyStore = APPLE;
        }
        if (Platform.OS.LINUX.equals(os)) {
            aOKeyStore = SHARED_NSS;
        }
        if (Platform.OS.SOLARIS.equals(os)) {
            aOKeyStore = MOZ_UNI;
        }
        return aOKeyStore;
    }
}
