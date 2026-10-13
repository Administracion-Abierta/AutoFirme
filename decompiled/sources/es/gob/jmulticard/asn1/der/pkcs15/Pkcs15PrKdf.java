package es.gob.jmulticard.asn1.der.pkcs15;

import es.gob.jmulticard.asn1.Asn1Exception;
import es.gob.jmulticard.asn1.TlvException;
import javax.security.auth.x500.X500Principal;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/pkcs15/Pkcs15PrKdf.class */
public interface Pkcs15PrKdf {
    void setDerValue(byte[] bArr) throws TlvException, Asn1Exception;

    int getKeyCount();

    byte[] getKeyId(int i);

    byte getKeyReference(int i);

    X500Principal getKeyPrincipal(int i);
}
