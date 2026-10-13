package es.gob.jmulticard.apdu.ceres;

import es.gob.jmulticard.apdu.CommandApdu;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/ceres/CeresLoadDataApduCommand.class */
public final class CeresLoadDataApduCommand extends CommandApdu {
    private static final byte CLA = -112;
    private static final byte INS_LOAD_DATA = 88;

    public CeresLoadDataApduCommand(byte[] paddedDigestInfo) {
        super((byte) -112, (byte) 88, (byte) 0, (byte) 0, paddedDigestInfo, null);
    }
}
