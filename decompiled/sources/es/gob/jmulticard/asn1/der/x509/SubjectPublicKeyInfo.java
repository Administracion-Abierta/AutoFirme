package es.gob.jmulticard.asn1.der.x509;

import es.gob.jmulticard.asn1.OptionalDecoderObjectElement;
import es.gob.jmulticard.asn1.der.Sequence;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/x509/SubjectPublicKeyInfo.class */
public final class SubjectPublicKeyInfo extends Sequence {
    public SubjectPublicKeyInfo() {
        super(new OptionalDecoderObjectElement(AlgorithmIdentifier.class, false), new OptionalDecoderObjectElement(SubjectPublicKey.class, false));
    }

    public byte[] getSubjectPublicKey() {
        byte[] tmp = ((SubjectPublicKey) getElementAt(1)).getValue();
        byte[] ret = new byte[tmp.length - 1];
        System.arraycopy(tmp, 1, ret, 0, ret.length);
        return ret;
    }
}
