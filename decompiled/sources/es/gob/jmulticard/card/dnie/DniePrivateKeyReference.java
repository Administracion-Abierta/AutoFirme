package es.gob.jmulticard.card.dnie;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.card.CryptoCard;
import es.gob.jmulticard.card.Location;
import es.gob.jmulticard.card.PrivateKeyReference;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/dnie/DniePrivateKeyReference.class */
public final class DniePrivateKeyReference implements PrivateKeyReference {
    private final CryptoCard dnieCard;
    private final byte[] identifier;
    private final Location keyPath;
    private final String label;
    private final byte reference;
    private final int keySize;

    public DniePrivateKeyReference(CryptoCard card, byte[] id, Location path, String lbl, byte ref, int keyLength) {
        this.dnieCard = card;
        this.identifier = id != null ? (byte[]) id.clone() : null;
        this.keyPath = path;
        this.label = lbl;
        this.reference = ref;
        this.keySize = keyLength;
    }

    public String toString() {
        return "Clave privada de DNIe con etiqueta '" + this.label + "', identificador '" + HexUtils.hexify(this.identifier, false) + "' y ruta '" + this.keyPath + OperatorName.SHOW_TEXT_LINE;
    }

    public CryptoCard getDnieCard() {
        return this.dnieCard;
    }

    public byte[] getIdentifier() {
        return this.identifier;
    }

    public Location getKeyPath() {
        return this.keyPath;
    }

    public String getLabel() {
        return this.label;
    }

    public byte getKeyReference() {
        return this.reference;
    }

    public int getKeyBitSize() {
        return this.keySize;
    }
}
