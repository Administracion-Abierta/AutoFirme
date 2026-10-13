package es.gob.jmulticard.apdu.iso7816four;

import com.sun.jna.platform.win32.WinError;
import es.gob.jmulticard.apdu.CommandApdu;
import es.gob.jmulticard.apdu.StatusWord;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/iso7816four/ReadRecordApduCommand.class */
public final class ReadRecordApduCommand extends CommandApdu {
    private static final byte INS_READ_RECORD = -78;
    public static final StatusWord RECORD_NOT_FOUND = new StatusWord((byte) 106, (byte) -125);

    public ReadRecordApduCommand(byte cla) {
        super(cla, (byte) -78, (byte) 0, (byte) 2, null, Integer.valueOf(WinError.ERROR_MORE_DATA));
    }
}
