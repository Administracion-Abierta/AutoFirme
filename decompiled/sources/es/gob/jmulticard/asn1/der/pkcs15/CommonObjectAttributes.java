package es.gob.jmulticard.asn1.der.pkcs15;

import es.gob.jmulticard.asn1.OptionalDecoderObjectElement;
import es.gob.jmulticard.asn1.der.Sequence;
import es.gob.jmulticard.asn1.der.Utf8String;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/pkcs15/CommonObjectAttributes.class */
public final class CommonObjectAttributes extends Sequence {
    public CommonObjectAttributes() {
        super(new OptionalDecoderObjectElement(Utf8String.class, false), new OptionalDecoderObjectElement(CommonObjectFlags.class, true), new OptionalDecoderObjectElement(Identifier.class, true));
    }

    public String getLabel() {
        return getElementAt(0).toString();
    }

    public byte[] getIdentifier() {
        for (int i = 0; i < getElementCount(); i++) {
            Object o = getElementAt(i);
            if (o instanceof Identifier) {
                return (byte[]) ((Identifier) o).getOctectStringByteValue().clone();
            }
        }
        return null;
    }
}
