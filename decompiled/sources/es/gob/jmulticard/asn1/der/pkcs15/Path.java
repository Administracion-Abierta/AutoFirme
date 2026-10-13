package es.gob.jmulticard.asn1.der.pkcs15;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.asn1.OptionalDecoderObjectElement;
import es.gob.jmulticard.asn1.der.DerInteger;
import es.gob.jmulticard.asn1.der.OctectString;
import es.gob.jmulticard.asn1.der.Sequence;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/pkcs15/Path.class */
public final class Path extends Sequence {
    public Path() {
        super(new OptionalDecoderObjectElement(OctectString.class, false), new OptionalDecoderObjectElement(DerInteger.class, true), new OptionalDecoderObjectElement(PathLength.class, true));
    }

    String getPathString() {
        return HexUtils.hexify(((OctectString) getElementAt(0)).getOctectStringByteValue(), false);
    }

    public byte[] getPathBytes() {
        return ((OctectString) getElementAt(0)).getOctectStringByteValue();
    }

    public String toString() {
        return getPathString();
    }
}
