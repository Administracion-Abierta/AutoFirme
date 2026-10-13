package es.gob.jmulticard.asn1.der.pkcs15;

import es.gob.jmulticard.asn1.OptionalDecoderObjectElement;
import es.gob.jmulticard.asn1.der.DerInteger;
import es.gob.jmulticard.asn1.der.Sequence;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/pkcs15/PrivateRsaKeyAttributes.class */
public final class PrivateRsaKeyAttributes extends Sequence {
    public PrivateRsaKeyAttributes() {
        super(new OptionalDecoderObjectElement(Path.class, false), new OptionalDecoderObjectElement(DerInteger.class, false));
    }

    public String getPath() {
        return ((Path) getElementAt(0)).getPathString();
    }

    public int getKeyLength() {
        return ((DerInteger) getElementAt(1)).getIntegerValue().intValue();
    }
}
