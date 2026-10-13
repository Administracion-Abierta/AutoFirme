package es.gob.jmulticard.apdu.iso7816four;

import es.gob.jmulticard.apdu.CommandApdu;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/iso7816four/GetChallengeApduCommand.class */
public final class GetChallengeApduCommand extends CommandApdu {
    private static final byte INS_GET_CHALLENGE = -124;
    private static final int DEFAULT_LE = 8;

    public GetChallengeApduCommand(byte cla) {
        this(cla, 8);
    }

    public GetChallengeApduCommand(byte cla, int numBytes) {
        super(cla, (byte) -124, (byte) 0, (byte) 0, null, Integer.valueOf(numBytes));
    }
}
