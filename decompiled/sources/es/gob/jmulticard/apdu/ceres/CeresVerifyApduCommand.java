package es.gob.jmulticard.apdu.ceres;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.apdu.CommandApdu;
import javax.security.auth.callback.PasswordCallback;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/ceres/CeresVerifyApduCommand.class */
public final class CeresVerifyApduCommand extends CommandApdu {
    private static final byte INS_VERIFY = 32;

    public CeresVerifyApduCommand(byte cla, PasswordCallback pinPc) {
        super(cla, (byte) 32, (byte) 0, (byte) 0, getPin(pinPc), null);
    }

    private static byte[] getPin(PasswordCallback pinPc) {
        if (pinPc == null) {
            throw new IllegalArgumentException("El PasswordCallback del PIN no puede ser nulo");
        }
        char[] in = pinPc.getPassword();
        return HexUtils.charArrayToByteArray(in);
    }
}
