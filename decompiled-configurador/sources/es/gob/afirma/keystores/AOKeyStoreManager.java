package es.gob.afirma.keystores;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.keystores.KeyStoreManager;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.jmulticard.CancelledOperationException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import javax.security.auth.callback.PasswordCallback;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/keystores/AOKeyStoreManager.class */
public class AOKeyStoreManager implements KeyStoreManager {
    protected static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private AOKeyStore ksType;
    private KeyStore ks;
    private InputStream storeIs;
    private PasswordCallback storePasswordCallBack;
    private Object[] storeParams;
    private Object parentComponent = null;
    private final Set<String> deactivatedCertificatesThumbprints = new HashSet();
    private String[] cachedAliases = null;
    private boolean preferred = false;
    private PasswordCallback entryPasswordCallBack = null;

    protected Object getParentComponent() {
        return this.parentComponent;
    }

    @Override // es.gob.afirma.core.keystores.KeyStoreManager
    public void setParentComponent(Object obj) {
        this.parentComponent = obj;
    }

    protected void resetCachedAliases() {
        this.cachedAliases = null;
    }

    protected String[] getCachedAliases() {
        if (this.cachedAliases != null) {
            return (String[]) this.cachedAliases.clone();
        }
        return null;
    }

    protected void setCachedAliases(String[] strArr) {
        this.cachedAliases = (String[]) strArr.clone();
    }

    protected boolean isPreferred() {
        return this.preferred;
    }

    void setPreferred(boolean z) {
        this.preferred = z;
    }

    protected void setKeyStore(KeyStore keyStore) {
        this.ks = keyStore;
    }

    public KeyStore getKeyStore() {
        return this.ks;
    }

    @Override // es.gob.afirma.core.keystores.KeyStoreManager
    public void refresh() throws IOException {
        resetCachedAliases();
        try {
            init(this.ksType, this.storeIs, this.storePasswordCallBack, this.storeParams, true);
        } catch (AOKeyStoreManagerException e) {
            this.ks = null;
            throw new IOException("Error al refrescar el almacen, se ocultaran sus entradas " + e, e);
        }
    }

    protected boolean lacksKeyStores() {
        return this.ks == null;
    }

    protected final void setKeyStoreType(AOKeyStore aOKeyStore) {
        this.ksType = aOKeyStore;
    }

    public AOKeyStore getType() {
        return this.ksType;
    }

    protected AOKeyStore getType(String str) {
        return getType();
    }

    @Override // es.gob.afirma.core.keystores.KeyStoreManager
    public void setEntryPasswordCallBack(PasswordCallback passwordCallback) {
        this.entryPasswordCallBack = passwordCallback;
    }

    protected PasswordCallback getEntryPasswordCallBack() {
        return this.entryPasswordCallBack;
    }

    public void init(AOKeyStore aOKeyStore, InputStream inputStream, PasswordCallback passwordCallback, Object[] objArr, boolean z) throws AOKeyStoreManagerException, IOException {
        if (aOKeyStore == null) {
            throw new IllegalArgumentException("Se ha solicitado inicializar un AOKeyStore nulo");
        }
        LOGGER.info("Inicializamos el almacen de tipo: " + aOKeyStore);
        resetCachedAliases();
        this.ksType = aOKeyStore;
        this.storeIs = inputStream;
        this.storePasswordCallBack = passwordCallback;
        if (objArr == null) {
            this.storeParams = null;
        } else {
            this.storeParams = new Object[objArr.length];
            System.arraycopy(objArr, 0, this.storeParams, 0, objArr.length);
        }
        switch (this.ksType) {
            case SINGLE:
                this.ks = AOKeyStoreManagerHelperSingle.initSingle(inputStream, passwordCallback);
                return;
            case SMARTCAFE:
                setParentComponent((objArr == null || objArr.length <= 0) ? null : objArr[0]);
                this.ks = AOKeyStoreManagerHelperFullJava.initSmartCafeJava(getParentComponent());
                return;
            case CERES:
                setParentComponent((objArr == null || objArr.length <= 0) ? null : objArr[0]);
                this.ks = AOKeyStoreManagerHelperFullJava.initCeresJava(getParentComponent());
                return;
            case DNIEJAVA:
                setParentComponent((objArr == null || objArr.length <= 0) ? null : objArr[0]);
                this.ks = AOKeyStoreManagerHelperFullJava.initDnieJava(getParentComponent());
                return;
            case CERES_430:
                setParentComponent((objArr == null || objArr.length <= 0) ? null : objArr[0]);
                this.ks = AOKeyStoreManagerHelperFullJava.initJMulticard(getParentComponent());
                return;
            case JAVACE:
            case JCEKS:
                this.ks = AOKeyStoreManagerHelperJava.initJava(inputStream, passwordCallback, this.ksType);
                return;
            case WINCA:
            case WINADDRESSBOOK:
                this.ks = AOKeyStoreManagerHelperCapiAddressBook.initCAPIAddressBook(this.ksType);
                return;
            case PKCS11:
                Object[] objArr2 = null;
                if (objArr != null) {
                    objArr2 = new Object[objArr.length];
                    System.arraycopy(objArr, 0, objArr2, 0, objArr.length);
                }
                this.ks = AOKeyStoreManagerHelperPkcs11.initPKCS11(passwordCallback, objArr2, z, getParentComponent());
                return;
            default:
                throw new UnsupportedOperationException("Tipo de almacen no soportado: " + this.ksType);
        }
    }

    @Override // es.gob.afirma.core.keystores.KeyStoreManager
    public KeyStore.PrivateKeyEntry getKeyEntry(String str) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableEntryException {
        KeyStore.PasswordProtection passwordProtection;
        if (this.ks == null) {
            throw new IllegalStateException("Se han pedido claves a un almacen no inicializado");
        }
        if (str == null) {
            throw new IllegalArgumentException("El alias no puede ser nulo");
        }
        if (this.entryPasswordCallBack != null) {
            passwordProtection = new KeyStore.PasswordProtection(this.entryPasswordCallBack.getPassword());
        } else {
            PasswordCallback certificatePasswordCallback = getType(str).getCertificatePasswordCallback(getParentComponent());
            if (certificatePasswordCallback != null) {
                passwordProtection = new KeyStore.PasswordProtection(certificatePasswordCallback.getPassword());
            } else {
                passwordProtection = null;
            }
        }
        return (KeyStore.PrivateKeyEntry) this.ks.getEntry(str, passwordProtection);
    }

    @Override // es.gob.afirma.core.keystores.KeyStoreManager
    public X509Certificate getCertificate(String str) {
        if (str == null) {
            LOGGER.warning("El alias del certificado es nulo, se devolvera null");
            return null;
        }
        if (this.ks == null) {
            LOGGER.warning("No se ha podido recuperar el certificado con el alias especificado porque el KeyStore no estaba inicializado, se devolvera null");
            return null;
        }
        try {
            return (X509Certificate) this.ks.getCertificate(str);
        } catch (CancelledOperationException e) {
            throw new AOCancelledOperationException("Se cancelo uso de la tarjeta a traves del driver Java: " + e, e);
        } catch (Exception e2) {
            LOGGER.severe("Error intentando recuperar el certificado, se devolvera null: " + e2);
            return null;
        }
    }

    @Override // es.gob.afirma.core.keystores.KeyStoreManager
    public X509Certificate[] getCertificateChain(String str) {
        if (str == null) {
            LOGGER.warning("El alias del certificado es nulo, se devolvera una cadena vacia");
            return new X509Certificate[0];
        }
        if (this.ks == null) {
            LOGGER.warning("No se ha podido recuperar el certificado con el alias especificado porque el KeyStore no estaba inicializado, se devolvera una cadena vacia");
            return new X509Certificate[0];
        }
        try {
            Certificate[] certificateChain = this.ks.getCertificateChain(str);
            if (certificateChain == null) {
                return new X509Certificate[0];
            }
            ArrayList arrayList = new ArrayList();
            for (Certificate certificate : certificateChain) {
                if (certificate instanceof X509Certificate) {
                    arrayList.add((X509Certificate) certificate);
                }
            }
            return (X509Certificate[]) arrayList.toArray(new X509Certificate[0]);
        } catch (Exception e) {
            LOGGER.severe("Error intentando recuperar la cadena del certificado, se continuara con el siguiente almacen: " + e);
            LOGGER.warning("El almacen no contiene ningun certificado con el alias especificado, se devolvera una cadena vacia");
            return new X509Certificate[0];
        }
    }

    @Override // es.gob.afirma.core.keystores.KeyStoreManager
    public String[] getAliases() {
        if (this.ks == null) {
            throw new IllegalStateException("Se han pedido alias a un almacen no inicializado");
        }
        if (this.cachedAliases != null) {
            return (String[]) this.cachedAliases.clone();
        }
        try {
            this.cachedAliases = cleanDeactivatedAliases((String[]) Collections.list(this.ks.aliases()).toArray(new String[0]));
            return (String[]) this.cachedAliases.clone();
        } catch (KeyStoreException e) {
            LOGGER.severe("Error intentando recuperar los alias, se devolvera una lista vacia: " + e);
            return new String[0];
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("Gestor de almacenes de claves ");
        sb.append(this.ksType);
        if (this.ksType != null && this.ksType.getName() != null) {
            sb.append(" con nombre ");
            sb.append(this.ksType.getName());
        }
        return sb.toString();
    }

    @Override // es.gob.afirma.core.keystores.KeyStoreManager
    public boolean isKeyEntry(String str) throws KeyStoreException {
        return getKeyStore().isKeyEntry(str);
    }

    protected String[] cleanDeactivatedAliases(String[] strArr) {
        if (this.deactivatedCertificatesThumbprints.isEmpty()) {
            return strArr;
        }
        ArrayList arrayList = new ArrayList();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(AOSignConstants.DIGEST_ALGORITHM_SHA1);
            for (String str : strArr) {
                try {
                    if (!this.deactivatedCertificatesThumbprints.contains(AOUtil.hexify(messageDigest.digest(getCertificate(str).getEncoded()), false))) {
                        arrayList.add(str);
                    }
                } catch (CertificateEncodingException e) {
                    LOGGER.severe("No se ha podido obtener la huella del certificado actual, pueden aparecer duplicados en la lista de certificados: " + e);
                }
            }
            return (String[]) arrayList.toArray(new String[arrayList.size()]);
        } catch (NoSuchAlgorithmException e2) {
            LOGGER.warning("No se ha podido instanciar el generador de huellas digitales SHA1, pueden aparecer duplicados en la lista de certificados: " + e2);
            return strArr;
        }
    }

    @Override // es.gob.afirma.core.keystores.KeyStoreManager
    public void deactivateEntry(String str) {
        if (str != null) {
            this.deactivatedCertificatesThumbprints.add(str);
        }
        resetCachedAliases();
    }
}
