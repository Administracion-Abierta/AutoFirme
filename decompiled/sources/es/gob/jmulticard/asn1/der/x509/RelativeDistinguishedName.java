package es.gob.jmulticard.asn1.der.x509;

import es.gob.jmulticard.asn1.OptionalDecoderObjectElement;
import es.gob.jmulticard.asn1.der.Set;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/x509/RelativeDistinguishedName.class */
public final class RelativeDistinguishedName extends Set {
    public RelativeDistinguishedName() {
        super(new OptionalDecoderObjectElement(AttributeTypeAndDistinguishedValue.class, false));
    }

    public String toString() {
        return getElementAt(0).toString();
    }
}
