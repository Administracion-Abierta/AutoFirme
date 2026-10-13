package es.gob.jmulticard.apdu.dnie;

import es.gob.jmulticard.apdu.CommandApdu;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/dnie/RetriesLeftApduCommand.class */
public final class RetriesLeftApduCommand extends CommandApdu {
    private static final byte INS_VERIFY = 32;

    public RetriesLeftApduCommand() {
        super(new byte[]{0, 32, 0, 0, 0});
    }

    @Override // es.gob.jmulticard.apdu.CommandApdu
    public byte[] getData() {
        return null;
    }
}
