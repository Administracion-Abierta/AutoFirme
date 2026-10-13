package es.gob.jmulticard.apdu.iso7816four;

import es.gob.jmulticard.apdu.CommandApdu;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/iso7816four/GetResponseApduCommand.class */
public final class GetResponseApduCommand extends CommandApdu {
    private static final byte INS_GET_RESPONSE = -64;

    public GetResponseApduCommand(byte cla, byte le) {
        super(cla, (byte) -64, (byte) 0, (byte) 0, null, Integer.valueOf(String.valueOf((int) le)));
    }
}
