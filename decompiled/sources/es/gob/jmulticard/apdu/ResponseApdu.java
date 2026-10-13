package es.gob.jmulticard.apdu;

import es.gob.jmulticard.JmcLogger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/ResponseApdu.class */
public class ResponseApdu extends Apdu {
    private final byte[] encryptedByes;

    public ResponseApdu(byte[] fullBytes) {
        this(fullBytes, null);
    }

    public ResponseApdu(byte[] fullBytes, byte[] encrypted) {
        setBytes(fullBytes);
        this.encryptedByes = encrypted != null ? (byte[]) encrypted.clone() : null;
    }

    public byte[] getData() {
        byte[] dat = new byte[getBytes().length - 2];
        System.arraycopy(getBytes(), 0, dat, 0, getBytes().length - 2);
        return dat;
    }

    public StatusWord getStatusWord() {
        if (getBytes() == null) {
            JmcLogger.warning("Se ha pedido la palabra de estado de una APDU de cuerpo nulo, se devolvera null");
            return null;
        }
        if (getBytes().length < 2) {
            JmcLogger.warning("Se ha pedido la palabra de estado de una APDU de cuerpo vacio o sin palabra de estado, se devolvera null");
            return null;
        }
        return new StatusWord(getBytes()[getBytes().length - 2], getBytes()[getBytes().length - 1]);
    }

    public boolean isOk() {
        if (getBytes() == null || getBytes().length < 2) {
            return false;
        }
        return getStatusWord().isOk();
    }

    public byte[] getEncryptedByes() {
        return this.encryptedByes != null ? this.encryptedByes : getBytes();
    }
}
