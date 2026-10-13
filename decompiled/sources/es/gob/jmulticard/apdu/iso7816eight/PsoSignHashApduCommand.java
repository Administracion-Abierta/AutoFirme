package es.gob.jmulticard.apdu.iso7816eight;

import es.gob.jmulticard.apdu.CommandApdu;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/iso7816eight/PsoSignHashApduCommand.class */
public final class PsoSignHashApduCommand extends CommandApdu {
    private static final byte INS_PERFORM_SECURITY_OPERATION = 42;
    private static final byte DATA_FIELD_SIGN_OPERATION = -98;
    private static final byte DATA_FIELD_SIGN_HASH = -102;

    public PsoSignHashApduCommand(byte cla, byte[] digestInfo) {
        super(cla, (byte) 42, (byte) -98, (byte) -102, digestInfo, null);
    }
}
