package es.gob.jmulticard.apdu.iso7816four;

import es.gob.jmulticard.apdu.CommandApdu;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/iso7816four/SelectFileByIdApduCommand.class */
public final class SelectFileByIdApduCommand extends CommandApdu {
    private static final byte INS_SELECT_FILE = -92;
    private static final byte SELECT_BY_ID = 0;
    private static final byte SEARCH_FIRST = 0;

    public SelectFileByIdApduCommand(byte cla, byte[] fileId) {
        super(cla, (byte) -92, (byte) 0, (byte) 0, fileId, null);
        if (fileId == null || (fileId.length != 2 && fileId.length != 0)) {
            throw new IllegalArgumentException("El identificador de fichero debe tener exactamente dos octetos");
        }
    }
}
