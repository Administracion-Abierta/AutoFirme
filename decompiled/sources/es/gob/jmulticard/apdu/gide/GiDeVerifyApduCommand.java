package es.gob.jmulticard.apdu.gide;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.apdu.CommandApdu;
import javax.security.auth.callback.PasswordCallback;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/gide/GiDeVerifyApduCommand.class */
public final class GiDeVerifyApduCommand extends CommandApdu {
    private static final byte INS_VERIFY = 32;
    private static final byte CLA = 1;

    public GiDeVerifyApduCommand(PasswordCallback pinPc) {
        super((byte) 1, (byte) 32, (byte) 0, (byte) 2, getPin(pinPc), null);
    }

    private static byte[] getPin(PasswordCallback pinPc) {
        if (pinPc == null) {
            throw new IllegalArgumentException("El PasswordCallback del PIN no puede ser nulo");
        }
        char[] in = pinPc.getPassword();
        return HexUtils.charArrayToByteArray(in);
    }
}
