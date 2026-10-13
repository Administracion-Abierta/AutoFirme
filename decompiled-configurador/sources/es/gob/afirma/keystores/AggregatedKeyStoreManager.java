package es.gob.afirma.keystores;

import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.signers.AOSignConstants;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/keystores/AggregatedKeyStoreManager.class */
public class AggregatedKeyStoreManager extends AOKeyStoreManager {
    private final List<AOKeyStoreManager> ksms = new ArrayList();

    AggregatedKeyStoreManager(AOKeyStoreManager aOKeyStoreManager) {
        if (aOKeyStoreManager == null) {
            throw new IllegalArgumentException("El gestor principal de almacenes no puede ser nulo");
        }
        addKeyStoreManager(aOKeyStoreManager);
        setKeyStoreType(aOKeyStoreManager.getType());
    }

    protected AggregatedKeyStoreManager() {
    }

    @Override // es.gob.afirma.keystores.AOKeyStoreManager
    public AOKeyStore getType() {
        AOKeyStore type = super.getType();
        if (type == null && this.ksms != null && this.ksms.size() > 0) {
            type = this.ksms.get(0).getType();
        }
        return type;
    }

    @Override // es.gob.afirma.keystores.AOKeyStoreManager
    protected AOKeyStore getType(String str) {
        for (AOKeyStoreManager aOKeyStoreManager : this.ksms) {
            if (aOKeyStoreManager.getCertificate(str) != null) {
                return aOKeyStoreManager.getType(str);
            }
        }
        LOGGER.warning("Se ha pedido el tipo de almacen de un alias no contenido en este gestor, se devolvera el tipo por defecto");
        return getType();
    }

    public final void addKeyStoreManager(AOKeyStoreManager aOKeyStoreManager) {
        if (aOKeyStoreManager != null) {
            if (aOKeyStoreManager.isPreferred()) {
                String[] aliases = aOKeyStoreManager.getAliases();
                MessageDigest messageDigest = null;
                try {
                    messageDigest = MessageDigest.getInstance(AOSignConstants.DIGEST_ALGORITHM_SHA1);
                } catch (NoSuchAlgorithmException e) {
                    LOGGER.warning("No se ha podido instanciar el generador de huellas digitales SHA1, pueden aparecer duplicados en la lista de certificados: " + e);
                }
                if (messageDigest != null) {
                    for (String str : aliases) {
                        try {
                            String strHexify = AOUtil.hexify(messageDigest.digest(aOKeyStoreManager.getCertificate(str).getEncoded()), false);
                            LOGGER.info("El certificado de huella '" + strHexify + "' se tomara unicamente del almacen preferente");
                            Iterator<AOKeyStoreManager> it = this.ksms.iterator();
                            while (it.hasNext()) {
                                it.next().deactivateEntry(strHexify);
                            }
                        } catch (CertificateEncodingException e2) {
                            LOGGER.severe("No se ha podido obtener la huella del certificado con numero de serie '" + aOKeyStoreManager.getCertificate(str).getSerialNumber() + "', pueden aparecer duplicados en la lista de certificados: " + e2);
                        }
                    }
                }
            }
            this.ksms.add(aOKeyStoreManager);
        }
    }

    @Override // es.gob.afirma.keystores.AOKeyStoreManager, es.gob.afirma.core.keystores.KeyStoreManager
    public String[] getAliases() {
        ArrayList arrayList = new ArrayList();
        for (AOKeyStoreManager aOKeyStoreManager : this.ksms) {
            try {
                arrayList.addAll(Arrays.asList(aOKeyStoreManager.getAliases()));
            } catch (Exception e) {
                LOGGER.warning("No se pudieron obtener los alias del almacen " + aOKeyStoreManager.getType() + ": " + e);
            }
        }
        return (String[]) arrayList.toArray(new String[0]);
    }

    @Override // es.gob.afirma.keystores.AOKeyStoreManager, es.gob.afirma.core.keystores.KeyStoreManager
    public X509Certificate getCertificate(String str) {
        for (AOKeyStoreManager aOKeyStoreManager : this.ksms) {
            try {
                if (Arrays.asList(aOKeyStoreManager.getAliases()).contains(str)) {
                    return aOKeyStoreManager.getCertificate(str);
                }
            } catch (Exception e) {
                LOGGER.warning("No se pudieron obtener los alias del almacen " + aOKeyStoreManager.getType() + ": " + e);
            }
        }
        LOGGER.warning("El almacen no contiene ningun certificado con el alias especificado, se devolvera null");
        return null;
    }

    @Override // es.gob.afirma.keystores.AOKeyStoreManager, es.gob.afirma.core.keystores.KeyStoreManager
    public KeyStore.PrivateKeyEntry getKeyEntry(String str) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableEntryException {
        for (AOKeyStoreManager aOKeyStoreManager : this.ksms) {
            try {
                if (Arrays.asList(aOKeyStoreManager.getAliases()).contains(str)) {
                    return aOKeyStoreManager.getKeyEntry(str);
                }
            } catch (Exception e) {
                LOGGER.warning("No se pudieron obtener los alias del almacen " + aOKeyStoreManager.getType() + ": " + e);
            }
        }
        LOGGER.warning("El almacen no contiene ninguna clave el alias especificado, se devolvera null");
        return null;
    }

    @Override // es.gob.afirma.keystores.AOKeyStoreManager, es.gob.afirma.core.keystores.KeyStoreManager
    public X509Certificate[] getCertificateChain(String str) {
        for (AOKeyStoreManager aOKeyStoreManager : this.ksms) {
            try {
                if (Arrays.asList(aOKeyStoreManager.getAliases()).contains(str)) {
                    return aOKeyStoreManager.getCertificateChain(str);
                }
            } catch (Exception e) {
                LOGGER.warning("No se pudieron obtener los alias del almacen " + aOKeyStoreManager.getType() + ": " + e);
            }
        }
        LOGGER.warning("El almacen no contiene ninguna cadena de certificados con el alias especificado, se devolvera null");
        return null;
    }

    @Override // es.gob.afirma.keystores.AOKeyStoreManager, es.gob.afirma.core.keystores.KeyStoreManager
    public void refresh() throws IOException {
        for (AOKeyStoreManager aOKeyStoreManager : this.ksms) {
            try {
                aOKeyStoreManager.refresh();
            } catch (Exception e) {
                aOKeyStoreManager.setKeyStore(null);
                LOGGER.warning("Error al actualizar el almacen de tipo " + aOKeyStoreManager.getType() + ": " + e);
            }
        }
    }

    @Override // es.gob.afirma.keystores.AOKeyStoreManager
    protected boolean lacksKeyStores() {
        return this.ksms.isEmpty();
    }

    @Override // es.gob.afirma.keystores.AOKeyStoreManager, es.gob.afirma.core.keystores.KeyStoreManager
    public boolean isKeyEntry(String str) throws KeyStoreException {
        for (AOKeyStoreManager aOKeyStoreManager : this.ksms) {
            try {
                if (Arrays.asList(aOKeyStoreManager.getAliases()).contains(str)) {
                    return aOKeyStoreManager.isKeyEntry(str);
                }
            } catch (Exception e) {
                LOGGER.warning("No se pudieron obtener los alias del almacen " + aOKeyStoreManager.getType() + ": " + e);
            }
        }
        throw new KeyStoreException("Se ha pedido comprobar la clave privada de un certificado no contenido en este gestor");
    }

    @Override // es.gob.afirma.keystores.AOKeyStoreManager, es.gob.afirma.core.keystores.KeyStoreManager
    public void deactivateEntry(String str) {
        Iterator<AOKeyStoreManager> it = this.ksms.iterator();
        while (it.hasNext()) {
            it.next().deactivateEntry(str);
        }
    }

    List<AOKeyStoreManager> getKeyStoreManagers() {
        return (List) ((ArrayList) this.ksms).clone();
    }

    public void removeAll() {
        this.ksms.clear();
        setKeyStoreType(null);
    }
}
