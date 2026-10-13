package es.gob.jmulticard.asn1.der.pkcs15;

import es.gob.jmulticard.asn1.OptionalDecoderObjectElement;
import es.gob.jmulticard.asn1.der.DerBoolean;
import es.gob.jmulticard.asn1.der.Sequence;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/pkcs15/CommonKeyAttributes.class */
public final class CommonKeyAttributes extends Sequence {
    public CommonKeyAttributes() {
        super(new OptionalDecoderObjectElement(Identifier.class, false), new OptionalDecoderObjectElement(KeyUsageFlags.class, false), new OptionalDecoderObjectElement(DerBoolean.class, true), new OptionalDecoderObjectElement(AccessFlags.class, false), new OptionalDecoderObjectElement(Reference.class, false));
    }

    public byte[] getIdentifier() {
        return ((Identifier) getElementAt(0)).getOctectStringByteValue();
    }

    public Reference getReference() {
        for (int i = 0; i < getElementCount(); i++) {
            Object o = getElementAt(i);
            if (o instanceof Reference) {
                return (Reference) o;
            }
        }
        return null;
    }
}
