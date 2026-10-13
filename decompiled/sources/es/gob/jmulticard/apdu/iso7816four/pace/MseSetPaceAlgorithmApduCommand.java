package es.gob.jmulticard.apdu.iso7816four.pace;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.apdu.iso7816four.MseSetAuthenticationTemplateApduCommand;
import es.gob.jmulticard.asn1.icao.CardAccess;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/iso7816four/pace/MseSetPaceAlgorithmApduCommand.class */
public final class MseSetPaceAlgorithmApduCommand extends MseSetAuthenticationTemplateApduCommand {

    /* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/iso7816four/pace/MseSetPaceAlgorithmApduCommand$PacePasswordType.class */
    public enum PacePasswordType {
        MRZ(new byte[]{-125, 1, 1}),
        CAN(new byte[]{-125, 1, 2}),
        PIN(new byte[]{-125, 1, 3});

        private final byte[] pwdTypeBytes;

        PacePasswordType(byte[] tpy) {
            this.pwdTypeBytes = (byte[]) tpy.clone();
        }

        public byte[] getBytes() {
            return (byte[]) this.pwdTypeBytes.clone();
        }
    }

    /* JADX WARN: Illegal instructions before constructor call */
    public MseSetPaceAlgorithmApduCommand(byte cla, CardAccess.PaceAlgorithm algorithm, PacePasswordType pwdType, PaceChat paceChat, CardAccess.PaceAlgorithmParam algorithmParam) {
        byte[][] bArr = new byte[5][];
        bArr[0] = new byte[]{-128};
        bArr[1] = algorithm.getBytes();
        bArr[2] = pwdType.getBytes();
        bArr[3] = paceChat != null ? paceChat.getBytes() : new byte[0];
        bArr[4] = algorithmParam != null ? algorithmParam.getBytes() : new byte[0];
        super(cla, HexUtils.concatenateByteArrays(bArr));
    }
}
