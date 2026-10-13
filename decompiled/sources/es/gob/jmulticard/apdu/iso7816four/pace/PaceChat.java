package es.gob.jmulticard.apdu.iso7816four.pace;

import es.gob.jmulticard.HexUtils;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/iso7816four/pace/PaceChat.class */
public final class PaceChat {
    private static final byte[] TAG = {127, 76};
    private final TerminalType terminalType;

    /* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/iso7816four/pace/PaceChat$TerminalType.class */
    public enum TerminalType {
        ST(new byte[]{6, 9, 4, 0, 127, 0, 7, 3, 1, 2, 3, 83, 1, 2});

        private final byte[] encoding;

        TerminalType(byte[] body) {
            this.encoding = body;
        }

        byte[] getBytes() {
            return this.encoding;
        }
    }

    public PaceChat(TerminalType termType) {
        this.terminalType = termType;
    }

    byte[] getBytes() {
        byte[] terminalTypeBytes = this.terminalType.getBytes();
        return HexUtils.concatenateByteArrays(TAG, new byte[]{(byte) terminalTypeBytes.length}, terminalTypeBytes);
    }
}
