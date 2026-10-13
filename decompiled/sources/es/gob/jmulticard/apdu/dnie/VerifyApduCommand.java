package es.gob.jmulticard.apdu.dnie;

import es.gob.jmulticard.apdu.CommandApdu;
import javax.security.auth.callback.PasswordCallback;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/dnie/VerifyApduCommand.class */
public final class VerifyApduCommand extends CommandApdu {
    public static final byte INS_VERIFY = 32;
    private final PasswordCallback pwc;

    public VerifyApduCommand(byte cla, PasswordCallback pinPc) {
        super(cla, (byte) 32, (byte) 0, (byte) 0, new byte[]{0}, null);
        if (pinPc == null) {
            throw new IllegalArgumentException("No se puede verificar el titular con un PasswordCallback nulo");
        }
        this.pwc = pinPc;
    }

    @Override // es.gob.jmulticard.apdu.CommandApdu
    public byte[] getData() {
        char[] pass = this.pwc.getPassword();
        byte[] k = new byte[pass.length];
        for (int i = 0; i < k.length; i++) {
            k[i] = (byte) pass[i];
        }
        for (int i2 = 0; i2 < k.length; i2++) {
            pass[i2] = 0;
        }
        return k;
    }
}
