package es.gob.jmulticard.apdu.iso7816four;

import es.gob.jmulticard.apdu.CommandApdu;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/iso7816four/ReadBinaryApduCommand.class */
public final class ReadBinaryApduCommand extends CommandApdu {
    private static final byte INS_READ_BINARY = -80;

    public ReadBinaryApduCommand(byte cla, byte msbOffset, byte lsbOffset, int readLength) {
        super(cla, (byte) -80, msbOffset, lsbOffset, null, Integer.valueOf(String.valueOf(readLength)));
    }
}
