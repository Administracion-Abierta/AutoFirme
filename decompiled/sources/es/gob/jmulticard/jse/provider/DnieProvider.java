package es.gob.jmulticard.jse.provider;

import es.gob.jmulticard.card.dnie.Dnie;
import es.gob.jmulticard.connection.ApduConnection;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.security.Provider;
import java.security.ProviderException;
import java.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/jse/provider/DnieProvider.class */
public final class DnieProvider extends Provider {
    private static final String SHA512WITH_RSA = "SHA512withRSA";
    private static final String SHA384WITH_RSA = "SHA384withRSA";
    private static final String SHA256WITH_RSA = "SHA256withRSA";
    private static final String SHA1WITH_RSA = "SHA1withRSA";
    private static final String DNIE_PRIVATE_KEY = "es.gob.jmulticard.jse.provider.DniePrivateKey";
    private static final String KEYSTORE_DNI = "KeyStore.DNI";
    private static final String KEYSTORE_CERES430 = "KeyStore.CERES430";
    private static final String CONFIG_PARAM_PROVIDER_NAME = "name";
    private static final String CONFIG_PARAM_ALLOW_ANOTHER_CARDS = "allowAnotherCards";
    private static final long serialVersionUID = -1046745919235177156L;
    private static final String INFO = "Proveedor para el DNIe y tarjetas compatibles";
    private static final double VERSION = 0.4d;
    private static final String NAME = "DNIeJCAProvider";
    private static ApduConnection defaultConnection = null;

    static ApduConnection getDefaultApduConnection() {
        return defaultConnection;
    }

    public DnieProvider() {
        this(null);
    }

    public DnieProvider(ApduConnection conn) {
        this(conn, NAME);
    }

    public DnieProvider(ApduConnection conn, String name) {
        ApduConnection defaultConnection2;
        super(name, VERSION, INFO);
        if (conn == null) {
            try {
                defaultConnection2 = ProviderUtil.getDefaultConnection();
            } catch (Exception e) {
                throw new ProviderException("No se ha proporcionado una conexion con un lector y no ha podido instanciarse la por defecto", e);
            }
        } else {
            defaultConnection2 = conn;
        }
        defaultConnection = defaultConnection2;
        try {
            Dnie.connect(defaultConnection);
            defaultConnection.close();
            put(KEYSTORE_DNI, "es.gob.jmulticard.jse.provider.DnieKeyStoreImpl");
            put("Signature.SHA1withRSA", "es.gob.jmulticard.jse.provider.DnieSignatureImpl$Sha1");
            put("Signature.SHA256withRSA", "es.gob.jmulticard.jse.provider.DnieSignatureImpl$Sha256");
            put("Signature.SHA384withRSA", "es.gob.jmulticard.jse.provider.DnieSignatureImpl$Sha384");
            put("Signature.SHA512withRSA", "es.gob.jmulticard.jse.provider.DnieSignatureImpl$Sha512");
            put("Signature.SHA1withRSA SupportedKeyClasses", DNIE_PRIVATE_KEY);
            put("Signature.SHA256withRSA SupportedKeyClasses", DNIE_PRIVATE_KEY);
            put("Signature.SHA384withRSA SupportedKeyClasses", DNIE_PRIVATE_KEY);
            put("Signature.SHA512withRSA SupportedKeyClasses", DNIE_PRIVATE_KEY);
            put("Alg.Alias.Signature.1.2.840.113549.1.1.5", "SHA1withRSA");
            put("Alg.Alias.Signature.OID.1.2.840.113549.1.1.5", "SHA1withRSA");
            put("Alg.Alias.Signature.1.3.14.3.2.29", "SHA1withRSA");
            put("Alg.Alias.Signature.SHAwithRSA", "SHA1withRSA");
            put("Alg.Alias.Signature.SHA-1withRSA", "SHA1withRSA");
            put("Alg.Alias.Signature.SHA1withRSAEncryption", "SHA1withRSA");
            put("Alg.Alias.Signature.SHA-1withRSAEncryption", "SHA1withRSA");
            put("Alg.Alias.Signature.1.2.840.113549.1.1.11", "SHA256withRSA");
            put("Alg.Alias.Signature.OID.1.2.840.113549.1.1.11", "SHA256withRSA");
            put("Alg.Alias.Signature.SHA-256withRSA", "SHA256withRSA");
            put("Alg.Alias.Signature.SHA-256withRSAEncryption", "SHA256withRSA");
            put("Alg.Alias.Signature.SHA256withRSAEncryption", "SHA256withRSA");
            put("Alg.Alias.Signature.1.2.840.113549.1.1.12", "SHA384withRSA");
            put("Alg.Alias.Signature.OID.1.2.840.113549.1.1.12", "SHA384withRSA");
            put("Alg.Alias.Signature.SHA-384withRSA", "SHA384withRSA");
            put("Alg.Alias.Signature.SHA-384withRSAEncryption", "SHA384withRSA");
            put("Alg.Alias.Signature.SHA384withRSAEncryption", "SHA384withRSA");
            put("Alg.Alias.Signature.1.2.840.113549.1.1.13", "SHA512withRSA");
            put("Alg.Alias.Signature.OID.1.2.840.113549.1.1.13", "SHA512withRSA");
            put("Alg.Alias.Signature.SHA-512withRSA", "SHA512withRSA");
            put("Alg.Alias.Signature.SHA-512withRSAEncryption", "SHA512withRSA");
            put("Alg.Alias.Signature.SHA512withRSAEncryption", "SHA512withRSA");
            String vmVendor = System.getProperty("java.vendor");
            if (vmVendor == null || (!vmVendor.contains("Oracle") && !vmVendor.contains("Sun Microsystems"))) {
                put("Cipher.RSA/ECB/PKCS1Padding", "es.gob.jmulticard.jse.provider.rsacipher.DnieCipherImpl");
                put("Cipher.RSA/ECB/PKCS1Padding SupportedKeyClasses", DNIE_PRIVATE_KEY);
            }
        } catch (Exception e2) {
            throw new ProviderException("No se ha podido inicializar el proveedor de DNIe", e2);
        }
    }

    public Provider configure(String config) {
        Properties prop = new Properties();
        try {
            Reader reader = new StringReader(config);
            Throwable th = null;
            try {
                try {
                    prop.load(reader);
                    if (reader != null) {
                        if (0 != 0) {
                            try {
                                reader.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            reader.close();
                        }
                    }
                    String providerName = NAME;
                    if (prop.containsKey("name")) {
                        providerName = prop.getProperty("name");
                    }
                    Provider newProvider = new DnieProvider(defaultConnection, providerName);
                    if (prop.containsKey(CONFIG_PARAM_ALLOW_ANOTHER_CARDS)) {
                        newProvider.remove(KEYSTORE_DNI);
                        if (Boolean.parseBoolean(prop.getProperty(CONFIG_PARAM_ALLOW_ANOTHER_CARDS))) {
                            newProvider.put(KEYSTORE_DNI, "es.gob.jmulticard.jse.provider.JMulticardKeyStoreImpl");
                            newProvider.put(KEYSTORE_CERES430, "es.gob.jmulticard.jse.provider.JMulticardKeyStoreImpl");
                        } else {
                            newProvider.put(KEYSTORE_DNI, "es.gob.jmulticard.jse.provider.DnieKeyStoreImpl");
                        }
                    }
                    return newProvider;
                } catch (Throwable th3) {
                    th = th3;
                    throw th3;
                }
            } catch (Throwable th4) {
                if (reader != null) {
                    if (th != null) {
                        try {
                            reader.close();
                        } catch (Throwable th5) {
                            th.addSuppressed(th5);
                        }
                    } else {
                        reader.close();
                    }
                }
                throw th4;
            }
        } catch (IOException e) {
            throw new SecurityException("La configuracion del proveedor debe tener formato de properties", e);
        }
    }

    public boolean isConfigured() {
        return true;
    }
}
