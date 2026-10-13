package es.gob.jmulticard.asn1.der.x509;

import es.gob.jmulticard.asn1.OptionalDecoderObjectElement;
import es.gob.jmulticard.asn1.der.ObjectIdentifier;
import es.gob.jmulticard.asn1.der.Sequence;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/x509/Attribute.class */
public final class Attribute extends Sequence {
    public Attribute() {
        super(new OptionalDecoderObjectElement(ObjectIdentifier.class, false), new OptionalDecoderObjectElement(AttributeValues.class, false));
    }

    public AttributeValues getAttributeValues() {
        return (AttributeValues) getElementAt(1);
    }

    public String toString() {
        return getElementAt(0).toString() + " = " + getElementAt(1).toString();
    }
}
