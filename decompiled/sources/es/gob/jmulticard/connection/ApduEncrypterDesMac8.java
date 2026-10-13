package es.gob.jmulticard.connection;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/connection/ApduEncrypterDesMac8.class */
public final class ApduEncrypterDesMac8 extends ApduEncrypterDes {
    private static final byte MAC_LENGTH_8 = 8;

    @Override // es.gob.jmulticard.connection.ApduEncrypterDes
    protected int getMacLength() {
        return 8;
    }
}
