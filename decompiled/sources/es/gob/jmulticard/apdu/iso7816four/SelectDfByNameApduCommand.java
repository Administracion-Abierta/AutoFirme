package es.gob.jmulticard.apdu.iso7816four;

import es.gob.jmulticard.apdu.CommandApdu;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/iso7816four/SelectDfByNameApduCommand.class */
public final class SelectDfByNameApduCommand extends CommandApdu {
    private static final byte INS_SELECT_FILE = -92;
    private static final byte SELECT_DF_BY_NAME = 4;

    public SelectDfByNameApduCommand(byte cla, byte[] name) {
        super(cla, (byte) -92, (byte) 4, (byte) 0, name, null);
    }
}
