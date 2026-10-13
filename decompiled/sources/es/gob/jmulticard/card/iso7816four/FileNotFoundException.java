package es.gob.jmulticard.card.iso7816four;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.apdu.StatusWord;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/iso7816four/FileNotFoundException.class */
public final class FileNotFoundException extends Iso7816FourCardException {
    private static final long serialVersionUID = -1114043381519603316L;
    private final byte[] id;
    private static final StatusWord FILE_NOT_FOUND_RETURN_CODE = new StatusWord((byte) 106, (byte) -126);

    public FileNotFoundException() {
        super("Fichero no encontrado", FILE_NOT_FOUND_RETURN_CODE);
        this.id = null;
    }

    public FileNotFoundException(byte[] fileId) {
        super("Fichero no encontrado: " + HexUtils.hexify(fileId, false), FILE_NOT_FOUND_RETURN_CODE);
        this.id = new byte[fileId.length];
        System.arraycopy(fileId, 0, this.id, 0, fileId.length);
    }

    public FileNotFoundException(String filename) {
        super("Fichero no encontrado: " + filename, FILE_NOT_FOUND_RETURN_CODE);
        this.id = filename.getBytes();
    }

    public byte[] getFileId() {
        byte[] out = new byte[this.id.length];
        System.arraycopy(this.id, 0, out, 0, this.id.length);
        return out;
    }
}
