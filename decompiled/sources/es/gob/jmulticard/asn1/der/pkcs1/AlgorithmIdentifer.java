package es.gob.jmulticard.asn1.der.pkcs1;

import es.gob.jmulticard.asn1.OptionalDecoderObjectElement;
import es.gob.jmulticard.asn1.der.ObjectIdentifier;
import es.gob.jmulticard.asn1.der.Sequence;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/pkcs1/AlgorithmIdentifer.class */
public final class AlgorithmIdentifer extends Sequence {
    public AlgorithmIdentifer() {
        super(new OptionalDecoderObjectElement(ObjectIdentifier.class, false));
    }

    public String toString() {
        return getElementAt(0).toString();
    }
}
