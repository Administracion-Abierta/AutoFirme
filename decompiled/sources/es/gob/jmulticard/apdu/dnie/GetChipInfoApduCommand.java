package es.gob.jmulticard.apdu.dnie;

import es.gob.jmulticard.apdu.CommandApdu;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/dnie/GetChipInfoApduCommand.class */
public final class GetChipInfoApduCommand extends CommandApdu {
    private static final byte DEFAULT_CLA = -112;
    private static final byte INS_GET_CHIP_INFO = -72;
    private static final byte INSTRUCTION_PARAMETER_P1 = 0;
    private static final byte INSTRUCTION_PARAMETER_P2 = 0;
    private static final byte MAXIMUM_LENGTH_EXPECTED_LE = 7;

    public GetChipInfoApduCommand() {
        this((byte) -112);
    }

    private GetChipInfoApduCommand(byte cla) {
        super(cla, (byte) -72, (byte) 0, (byte) 0, null, 7);
    }
}
