package es.gob.jmulticard.apdu.dnie;

import es.gob.jmulticard.apdu.CommandApdu;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/dnie/ChangePinApduCommand.class */
public final class ChangePinApduCommand extends CommandApdu {
    private static final byte CLA = -112;
    private static final byte INS_CHANGE_PIN = 36;
    private static final byte INSTRUCTION_PARAMETER_P1 = 0;
    private static final byte INSTRUCTION_PARAMETER_P2 = 0;
    private static final byte CHV_CODE = 1;

    public ChangePinApduCommand(byte[] oldPIN, byte[] newPIN) {
        super((byte) -112, (byte) 36, (byte) 0, (byte) 0, buidData(oldPIN, newPIN), null);
    }

    private static byte[] buidData(byte[] oldPin, byte[] newPin) {
        byte[] data = new byte[2 + oldPin.length + 1 + newPin.length];
        data[0] = 1;
        data[1] = (byte) oldPin.length;
        System.arraycopy(oldPin, 0, data, 2, oldPin.length);
        data[2 + oldPin.length] = (byte) newPin.length;
        System.arraycopy(newPin, 0, data, 3 + oldPin.length, newPin.length);
        return data;
    }
}
