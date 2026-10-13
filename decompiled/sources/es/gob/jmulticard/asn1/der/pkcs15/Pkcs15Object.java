package es.gob.jmulticard.asn1.der.pkcs15;

import es.gob.jmulticard.asn1.DecoderObject;
import es.gob.jmulticard.asn1.OptionalDecoderObjectElement;
import es.gob.jmulticard.asn1.der.ContextSpecific;
import es.gob.jmulticard.asn1.der.Sequence;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/pkcs15/Pkcs15Object.class */
public abstract class Pkcs15Object extends Sequence {
    protected Pkcs15Object(Class<? extends DecoderObject> classAttributes, Class<? extends ContextSpecific> subclassAttributes, Class<? extends ContextSpecific> typeAttributes) {
        super(new OptionalDecoderObjectElement(CommonObjectAttributes.class, false), new OptionalDecoderObjectElement(classAttributes, false), new OptionalDecoderObjectElement(subclassAttributes, true), new OptionalDecoderObjectElement(typeAttributes, false));
    }

    protected final CommonObjectAttributes getCommonObjectAttributes() {
        return (CommonObjectAttributes) getElementAt(0);
    }

    public final DecoderObject getClassAttributes() {
        return getElementAt(1);
    }

    public final DecoderObject getSubclassAttributes() {
        if (getElementCount() == 3) {
            return null;
        }
        return getElementAt(2);
    }

    public final DecoderObject getTypeAttributes() {
        if (getElementCount() == 3) {
            return getElementAt(2);
        }
        return getElementAt(3);
    }
}
