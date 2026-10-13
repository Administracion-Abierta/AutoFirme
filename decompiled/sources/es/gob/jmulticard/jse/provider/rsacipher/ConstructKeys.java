package es.gob.jmulticard.jse.provider.rsacipher;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/jse/provider/rsacipher/ConstructKeys.class */
final class ConstructKeys {
    private ConstructKeys() {
    }

    private static PublicKey constructPublicKey(byte[] encodedKey, String encodedKeyAlgorithm) throws NoSuchAlgorithmException, InvalidKeyException {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(encodedKeyAlgorithm);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encodedKey);
            return keyFactory.generatePublic(keySpec);
        } catch (InvalidKeySpecException ikse) {
            throw new InvalidKeyException("No se ha podido construir la clave publica: " + ikse, ikse);
        }
    }

    private static PrivateKey constructPrivateKey(byte[] encodedKey, String encodedKeyAlgorithm) throws NoSuchAlgorithmException, InvalidKeyException {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(encodedKeyAlgorithm);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodedKey);
            return keyFactory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException ikse) {
            throw new InvalidKeyException("No se ha podido construir la clave privada: " + ikse, ikse);
        }
    }

    private static SecretKey constructSecretKey(byte[] encodedKey, String encodedKeyAlgorithm) {
        return new SecretKeySpec(encodedKey, encodedKeyAlgorithm);
    }

    static Key constructKey(byte[] encoding, String keyAlgorithm, int keyType) throws NoSuchAlgorithmException, InvalidKeyException {
        switch (keyType) {
            case 1:
                return constructPublicKey(encoding, keyAlgorithm);
            case 2:
                return constructPrivateKey(encoding, keyAlgorithm);
            case 3:
                return constructSecretKey(encoding, keyAlgorithm);
            default:
                throw new InvalidKeyException("Tipo de clave no soportada: " + keyType);
        }
    }
}
