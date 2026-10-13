package es.gob.jmulticard.apdu.ceres;

import es.gob.jmulticard.apdu.CommandApdu;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/ceres/CeresSignDataApduCommand.class */
public final class CeresSignDataApduCommand extends CommandApdu {
    private static final byte CLA = -112;
    private static final byte INS_SIGN_DATA = 90;
    private static final byte RSA_SIGN = -128;

    public CeresSignDataApduCommand(byte reference, int keySize) {
        super((byte) -112, (byte) 90, (byte) -128, reference, null, Integer.valueOf(keySize / 8));
    }
}
