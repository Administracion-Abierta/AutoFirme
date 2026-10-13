package es.gob.jmulticard.asn1.der.x509;

import es.gob.jmulticard.asn1.DecoderObject;
import es.gob.jmulticard.asn1.OptionalDecoderObjectElement;
import es.gob.jmulticard.asn1.der.GeneralizedTime;
import es.gob.jmulticard.asn1.der.Set;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/x509/AttributeValues.class */
public final class AttributeValues extends Set {
    public AttributeValues() {
        super(new OptionalDecoderObjectElement(GeneralizedTime.class, false));
    }

    public DecoderObject[] getValues() {
        DecoderObject[] ret = new DecoderObject[getElementCount()];
        for (int i = 0; i < getElementCount(); i++) {
            ret[i] = getElementAt(i);
        }
        return ret;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getElementCount(); i++) {
            sb.append(getElementAt(i).toString());
            sb.append(", ");
        }
        String ret = sb.toString();
        return ret.substring(0, ret.length() - 3);
    }
}
