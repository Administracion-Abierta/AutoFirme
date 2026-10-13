package es.gob.jmulticard.apdu.iso7816eight;

import es.gob.jmulticard.apdu.CommandApdu;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/iso7816eight/EnvelopeDataApduCommand.class */
public final class EnvelopeDataApduCommand extends CommandApdu {
    private static final byte CLA = -112;
    private static final byte INS_ENVELOPE = -62;

    public EnvelopeDataApduCommand(byte[] data) {
        super((byte) -112, (byte) -62, (byte) 0, (byte) 0, data, null);
    }
}
