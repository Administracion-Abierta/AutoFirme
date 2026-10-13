package es.gob.jmulticard.apdu.dnie;

import es.gob.jmulticard.apdu.CommandApdu;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/dnie/SignDataApduCommand.class */
public final class SignDataApduCommand extends CommandApdu {
    private static final byte CLA = -112;
    private static final byte INS_SIGN_DATA = 90;
    private static final byte RSA_SIGN = -128;

    public SignDataApduCommand(byte reference, int keySize) {
        super((byte) -112, (byte) 90, (byte) -128, reference, null, Integer.valueOf(keySize / 8));
    }
}
