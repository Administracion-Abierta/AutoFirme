package es.gob.jmulticard.jse.provider.ceres;

import es.gob.jmulticard.card.fnmt.ceres.Ceres;
import es.gob.jmulticard.connection.ApduConnection;
import es.gob.jmulticard.jse.provider.ProviderUtil;
import java.security.Provider;
import java.security.ProviderException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/jse/provider/ceres/CeresProvider.class */
public final class CeresProvider extends Provider {
    private static final String SHA512WITH_RSA = "SHA512withRSA";
    private static final String SHA384WITH_RSA = "SHA384withRSA";
    private static final String SHA256WITH_RSA = "SHA256withRSA";
    private static final String SHA1WITH_RSA = "SHA1withRSA";
    private static final String ES_GOB_JMULTICARD_CARD_CERES_PRIVATE_KEY = "es.gob.jmulticard.jse.provider.ceres.CeresPrivateKey";
    private static final long serialVersionUID = -1046745919235177156L;
    private static final String INFO = "Proveedor para tarjeta FNMT-RCM-CERES";
    private static final double VERSION = 0.2d;
    private static final String NAME = "CeresJCAProvider";
    private static ApduConnection defaultConnection = null;

    static ApduConnection getDefaultApduConnection() {
        return defaultConnection;
    }

    public CeresProvider() {
        this(null);
    }

    public CeresProvider(ApduConnection conn) {
        ApduConnection defaultConnection2;
        super(NAME, VERSION, INFO);
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
            Ceres.connect(defaultConnection);
            defaultConnection.close();
            put("KeyStore.CERES", "es.gob.jmulticard.jse.provider.ceres.CeresKeyStoreImpl");
            put("Signature.SHA1withRSA", "es.gob.jmulticard.jse.provider.ceres.CeresSignatureImpl$Sha1");
            put("Signature.SHA256withRSA", "es.gob.jmulticard.jse.provider.ceres.CeresSignatureImpl$Sha256");
            put("Signature.SHA384withRSA", "es.gob.jmulticard.jse.provider.ceres.CeresSignatureImpl$Sha384");
            put("Signature.SHA512withRSA", "es.gob.jmulticard.jse.provider.ceres.CeresSignatureImpl$Sha512");
            put("Signature.SHA1withRSA SupportedKeyClasses", ES_GOB_JMULTICARD_CARD_CERES_PRIVATE_KEY);
            put("Signature.SHA256withRSA SupportedKeyClasses", ES_GOB_JMULTICARD_CARD_CERES_PRIVATE_KEY);
            put("Signature.SHA384withRSA SupportedKeyClasses", ES_GOB_JMULTICARD_CARD_CERES_PRIVATE_KEY);
            put("Signature.SHA512withRSA SupportedKeyClasses", ES_GOB_JMULTICARD_CARD_CERES_PRIVATE_KEY);
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
        } catch (Exception e2) {
            throw new ProviderException("No se ha podido conectar con la tarjeta CERES", e2);
        }
    }
}
