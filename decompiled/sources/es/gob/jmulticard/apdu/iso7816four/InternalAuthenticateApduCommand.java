package es.gob.jmulticard.apdu.iso7816four;

import es.gob.jmulticard.apdu.CommandApdu;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/iso7816four/InternalAuthenticateApduCommand.class */
public final class InternalAuthenticateApduCommand extends CommandApdu {
    private static final byte INS_INTERNAL_AUTHENTICATE = -120;
    private static final byte NO_INFORMATION_GIVEN = 0;

    public InternalAuthenticateApduCommand(byte cla, byte[] randomBytes, byte[] privateKeyRef) {
        super(cla, (byte) -120, (byte) 0, (byte) 0, buildData(randomBytes, privateKeyRef), null);
    }

    private static byte[] buildData(byte[] randomBytes, byte[] privateKeyRef) {
        byte[] ret = new byte[randomBytes.length + privateKeyRef.length];
        System.arraycopy(randomBytes, 0, ret, 0, randomBytes.length);
        System.arraycopy(privateKeyRef, 0, ret, 0 + randomBytes.length, privateKeyRef.length);
        return ret;
    }
}
