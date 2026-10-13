package es.gob.jmulticard.apdu.iso7816four;

import es.gob.jmulticard.apdu.CommandApdu;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/iso7816four/MseSetApduCommand.class */
abstract class MseSetApduCommand extends CommandApdu {
    private static final byte INS_MANAGE_ENVIROMENT = 34;
    protected static final byte SET_FOR_AUTHENTICATION = -63;
    protected static final byte SET_FOR_COMPUTATION = 65;
    protected static final byte DST = -74;
    protected static final byte AT = -92;
    protected static final byte PUBLIC_KEY_REFERENCE = -125;
    protected static final byte PRIVATE_KEY_REFERENCE = -124;
    protected static final byte ALGORITHM_REFERENCE = -128;

    protected MseSetApduCommand(byte cla, byte param1, byte param2, byte[] data) {
        super(cla, (byte) 34, param1, param2, data, null);
    }
}
