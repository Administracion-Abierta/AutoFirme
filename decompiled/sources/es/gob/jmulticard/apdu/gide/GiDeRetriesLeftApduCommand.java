package es.gob.jmulticard.apdu.gide;

import es.gob.jmulticard.apdu.CommandApdu;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/gide/GiDeRetriesLeftApduCommand.class */
public final class GiDeRetriesLeftApduCommand extends CommandApdu {
    private static final byte CLA = -128;

    public GiDeRetriesLeftApduCommand() {
        super(new byte[]{-128, 52, 0, 2, 0});
    }

    @Override // es.gob.jmulticard.apdu.CommandApdu
    public byte[] getData() {
        return null;
    }
}
