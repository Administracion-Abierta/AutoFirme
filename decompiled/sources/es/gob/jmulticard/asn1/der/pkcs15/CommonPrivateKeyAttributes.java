package es.gob.jmulticard.asn1.der.pkcs15;

import es.gob.jmulticard.asn1.OptionalDecoderObjectElement;
import es.gob.jmulticard.asn1.der.Sequence;
import es.gob.jmulticard.asn1.der.x509.RdnSequence;
import javax.security.auth.x500.X500Principal;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/pkcs15/CommonPrivateKeyAttributes.class */
public final class CommonPrivateKeyAttributes extends Sequence {
    public CommonPrivateKeyAttributes() {
        super(new OptionalDecoderObjectElement(RdnSequence.class, true));
    }

    public X500Principal getKeyPrincipal() {
        for (int i = 0; i < getElementCount(); i++) {
            Object o = getElementAt(i);
            if (o instanceof RdnSequence) {
                return ((RdnSequence) o).getKeyprincipal();
            }
        }
        return null;
    }
}
