package es.gob.afirma.signers.xades;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.logging.Logger;
import org.spongycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/xades/KeyUtil.class */
public final class KeyUtil {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    private KeyUtil() {
    }

    public static ECPrivateKey ecBc2Jce(BCECPrivateKey bCECPrivateKey) {
        try {
            PrivateKey privateKeyGeneratePrivate = KeyFactory.getInstance("EC").generatePrivate(new PKCS8EncodedKeySpec(bCECPrivateKey.getEncoded()));
            if (privateKeyGeneratePrivate instanceof ECPrivateKey) {
                return (ECPrivateKey) privateKeyGeneratePrivate;
            }
            LOGGER.info("No se ha podido convertir la clave de BC a JRE, es posible que falle el proceso");
            return bCECPrivateKey;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            LOGGER.info("No se ha podido convertir la clave de BC a JRE, es posible que falle el proceso: " + e);
            return bCECPrivateKey;
        }
    }

    public static boolean isMsCapiPrivateKey(Key key) {
        try {
            return Class.forName("sun.security.mscapi.CPrivateKey", false, KeyUtil.class.getClassLoader()).isAssignableFrom(key.getClass());
        } catch (Exception e) {
            LOGGER.info("La clase \"sun.security.mscapi.CPrivateKey\" no esta disponible para comprobar el tipo de clave");
            return false;
        }
    }
}
