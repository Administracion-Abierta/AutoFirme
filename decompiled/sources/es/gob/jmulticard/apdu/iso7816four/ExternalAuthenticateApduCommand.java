package es.gob.jmulticard.apdu.iso7816four;

import es.gob.jmulticard.apdu.CommandApdu;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/iso7816four/ExternalAuthenticateApduCommand.class */
public final class ExternalAuthenticateApduCommand extends CommandApdu {
    private static final byte INS_EXTERNAL_AUTHENTICATE = -126;
    private static final byte NO_INFORMATION_GIVEN = 0;

    public ExternalAuthenticateApduCommand(byte cla, byte[] authenticationToken) {
        super(cla, (byte) -126, (byte) 0, (byte) 0, authenticationToken, null);
    }
}
