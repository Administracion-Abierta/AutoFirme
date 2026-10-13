package es.gob.jmulticard.card.iso7816four;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.apdu.Apdu;
import es.gob.jmulticard.apdu.StatusWord;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/iso7816four/Iso7816FourCardException.class */
public class Iso7816FourCardException extends Exception {
    private final StatusWord returnCode;
    private static final long serialVersionUID = 5935577997660561619L;

    public Iso7816FourCardException(String desc, StatusWord retCode) {
        super(desc);
        this.returnCode = retCode;
    }

    public Iso7816FourCardException(StatusWord retCode, Apdu origin) {
        super("Codigo de retorno: " + retCode + ", APDU de origen: " + HexUtils.hexify(origin.getBytes(), true));
        this.returnCode = retCode;
    }

    public Iso7816FourCardException(String desc, Throwable e) {
        super(desc, e);
        this.returnCode = null;
    }

    public Iso7816FourCardException(String desc) {
        super(desc);
        this.returnCode = null;
    }

    public Iso7816FourCardException(StatusWord retCode, Apdu origin, String desc) {
        super((desc != null ? desc + " - " : "") + "Codigo de retorno " + retCode + ", APDU de origen: " + HexUtils.hexify(origin.getBytes(), true));
        this.returnCode = retCode;
    }

    public StatusWord getStatusWord() {
        return this.returnCode;
    }
}
