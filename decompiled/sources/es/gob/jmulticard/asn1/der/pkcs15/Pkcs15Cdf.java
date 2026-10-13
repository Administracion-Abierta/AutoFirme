package es.gob.jmulticard.asn1.der.pkcs15;

import es.gob.jmulticard.asn1.Asn1Exception;
import es.gob.jmulticard.asn1.TlvException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/pkcs15/Pkcs15Cdf.class */
public interface Pkcs15Cdf {
    int getCertificateCount();

    String getCertificatePath(int i);

    byte[] getCertificateId(int i);

    void setDerValue(byte[] bArr) throws TlvException, Asn1Exception;

    String getCertificateAlias(int i);
}
