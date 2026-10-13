package es.gob.jmulticard.asn1.der.x509;

import es.gob.jmulticard.asn1.OptionalDecoderObjectElement;
import es.gob.jmulticard.asn1.der.ObjectIdentifier;
import es.gob.jmulticard.asn1.der.Sequence;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/x509/AlgorithmIdentifier.class */
public final class AlgorithmIdentifier extends Sequence {
    public AlgorithmIdentifier() {
        super(new OptionalDecoderObjectElement(ObjectIdentifier.class, false), new OptionalDecoderObjectElement(ObjectIdentifier.class, true));
    }

    public String toString() {
        return getElementAt(0).toString();
    }
}
