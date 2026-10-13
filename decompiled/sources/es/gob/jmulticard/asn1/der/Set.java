package es.gob.jmulticard.asn1.der;

import es.gob.jmulticard.asn1.OptionalDecoderObjectElement;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/Set.class */
public abstract class Set extends Sequence {
    private static final byte TAG_SET = 49;

    protected Set(OptionalDecoderObjectElement... types) {
        super(types);
    }

    @Override // es.gob.jmulticard.asn1.der.Sequence, es.gob.jmulticard.asn1.DecoderObject
    protected final byte getDefaultTag() {
        return (byte) 49;
    }
}
