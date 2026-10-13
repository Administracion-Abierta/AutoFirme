package es.gob.jmulticard.asn1.icaovdsned;

import es.gob.jmulticard.asn1.OptionalDecoderObjectElement;
import es.gob.jmulticard.asn1.der.DerInteger;
import es.gob.jmulticard.asn1.der.Sequence;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/icaovdsned/EcdsaSignature.class */
public final class EcdsaSignature extends Sequence {
    public EcdsaSignature() {
        super(new OptionalDecoderObjectElement(DerInteger.class, false), new OptionalDecoderObjectElement(DerInteger.class, false));
    }
}
