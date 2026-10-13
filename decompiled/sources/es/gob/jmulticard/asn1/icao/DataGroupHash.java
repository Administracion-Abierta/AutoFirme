package es.gob.jmulticard.asn1.icao;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.asn1.OptionalDecoderObjectElement;
import es.gob.jmulticard.asn1.der.DerInteger;
import es.gob.jmulticard.asn1.der.OctectString;
import es.gob.jmulticard.asn1.der.Sequence;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/icao/DataGroupHash.class */
public final class DataGroupHash extends Sequence {
    public DataGroupHash() {
        super(new OptionalDecoderObjectElement(DerInteger.class, false), new OptionalDecoderObjectElement(OctectString.class, false));
    }

    public String toString() {
        return "DataGroupHash para el DG" + getElementAt(0) + " con valor de huella: " + HexUtils.hexify(((OctectString) getElementAt(1)).getOctectStringByteValue(), false);
    }

    public int getDataGroupNumber() {
        return ((DerInteger) getElementAt(0)).getIntegerValue().intValue();
    }

    public byte[] getDataGroupHashValue() {
        return ((OctectString) getElementAt(1)).getOctectStringByteValue();
    }
}
