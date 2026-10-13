package es.gob.jmulticard.apdu.iso7816eight;

import es.gob.jmulticard.apdu.CommandApdu;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/iso7816eight/PsoVerifyCertificateApduCommand.class */
public final class PsoVerifyCertificateApduCommand extends CommandApdu {
    private static final byte INS_PERFORM_SECURITY_OPERATION = 42;
    private static final byte DATA_FIELD_RESPONSE_EMPTY = 0;
    private static final byte DATA_FIELD_COMMAND_VERIFY_CERTIFICATE = -82;

    public PsoVerifyCertificateApduCommand(byte cla, byte[] certEncoding) {
        super(cla, (byte) 42, (byte) 0, (byte) -82, certEncoding, null);
    }
}
