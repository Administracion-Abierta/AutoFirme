package es.gob.jmulticard.asn1.der.x509;

import es.gob.jmulticard.asn1.der.SequenceOf;
import javax.security.auth.x500.X500Principal;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/x509/RdnSequence.class */
public final class RdnSequence extends SequenceOf {
    public RdnSequence() {
        super(RelativeDistinguishedName.class);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        int lastIndex = getElementCount();
        for (int i = 0; i < lastIndex; i++) {
            stringBuilder.append(getElementAt(i));
            if (i != lastIndex - 1) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }

    public X500Principal getKeyprincipal() {
        return new X500Principal(toString());
    }
}
