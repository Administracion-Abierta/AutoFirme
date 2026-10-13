package es.gob.jmulticard.connection;

import es.gob.jmulticard.CryptoHelper;
import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.JmcLogger;
import es.gob.jmulticard.apdu.ResponseApdu;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/connection/ApduEncrypterAes.class */
public final class ApduEncrypterAes extends AbstractApduEncrypter {
    public ApduEncrypterAes() {
        JmcLogger.info(ApduEncrypterAes.class.getName(), "constructor", "Se usara AES y CMAC para el cifrado de mensajes en el canal seguro");
        this.paddingLength = 16;
    }

    @Override // es.gob.jmulticard.connection.AbstractApduEncrypter
    protected byte[] encryptData(byte[] data, byte[] key, byte[] ssc, CryptoHelper cryptoHelper) throws IOException {
        if (ssc == null) {
            throw new IllegalArgumentException("El contador de secuencia no puede ser nulo en esta version de CWA-14890");
        }
        byte[] iv = cryptoHelper.aesEncrypt(ssc, new byte[0], key, CryptoHelper.BlockMode.CBC, CryptoHelper.Padding.NOPADDING);
        return cryptoHelper.aesEncrypt(data, iv, key, CryptoHelper.BlockMode.CBC, CryptoHelper.Padding.NOPADDING);
    }

    @Override // es.gob.jmulticard.connection.AbstractApduEncrypter
    protected byte[] generateMac(byte[] dataPadded, byte[] ssc, byte[] kMac, CryptoHelper cryptoHelper) throws IOException {
        try {
            byte[] mac = cryptoHelper.doAesCmac(HexUtils.concatenateByteArrays(ssc, dataPadded), kMac);
            byte[] ret = new byte[8];
            System.arraycopy(mac, 0, ret, 0, 8);
            return ret;
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            throw new IOException("Error creando la CMAC de la APDU cifrada", e);
        }
    }

    @Override // es.gob.jmulticard.connection.AbstractApduEncrypter
    public ResponseApdu decryptResponseApdu(ResponseApdu responseApdu, byte[] keyCipher, byte[] ssc, byte[] kMac, CryptoHelper cryptoHelper) {
        return null;
    }
}
