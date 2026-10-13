package es.gob.jmulticard.asn1.der.x509;

import es.gob.jmulticard.asn1.OptionalDecoderObjectElement;
import es.gob.jmulticard.asn1.der.ObjectIdentifier;
import es.gob.jmulticard.asn1.der.Sequence;
import es.gob.jmulticard.asn1.der.Utf8String;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/x509/AttributeTypeAndDistinguishedValue.class */
public final class AttributeTypeAndDistinguishedValue extends Sequence {
    public AttributeTypeAndDistinguishedValue() {
        super(new OptionalDecoderObjectElement(ObjectIdentifier.class, false), new OptionalDecoderObjectElement(Utf8String.class, false));
    }

    public String toString() {
        return getElementAt(0).toString() + "=\"" + getElementAt(1).toString() + OperatorName.SHOW_TEXT_LINE_AND_SPACE;
    }
}
