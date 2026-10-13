package es.gob.jmulticard.card;

import es.gob.jmulticard.HexUtils;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/InvalidCardException.class */
public final class InvalidCardException extends CardException {
    private static final long serialVersionUID = 4888120866657775782L;
    private final Atr atr;
    private final String name;
    private final byte[] badAtr;

    public InvalidCardException(String desc) {
        super(desc);
        this.name = null;
        this.badAtr = null;
        this.atr = null;
    }

    public InvalidCardException(String expectedCardName, Atr expectedAtr, byte[] foundAtr) {
        super("Se esperaba una tarjeta de tipo '" + expectedCardName + "' pero se encontro otra con ATR=" + (foundAtr == null ? "NULO" : HexUtils.hexify(foundAtr, false)));
        this.atr = expectedAtr;
        this.name = expectedCardName;
        if (foundAtr != null) {
            this.badAtr = new byte[foundAtr.length];
            System.arraycopy(foundAtr, 0, this.badAtr, 0, this.badAtr.length);
        } else {
            this.badAtr = null;
        }
    }

    public Atr getExpectedAtr() {
        return this.atr;
    }

    public String getExpectedCardName() {
        return this.name;
    }

    public byte[] getFoundAtr() {
        if (this.badAtr == null) {
            return null;
        }
        byte[] tmpAtr = new byte[this.badAtr.length];
        System.arraycopy(this.badAtr, 0, tmpAtr, 0, this.badAtr.length);
        return tmpAtr;
    }
}
