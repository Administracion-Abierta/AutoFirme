package es.gob.jmulticard.asn1.der.pkcs15;

import es.gob.jmulticard.asn1.DecoderObject;
import es.gob.jmulticard.asn1.OptionalDecoderObjectElement;
import es.gob.jmulticard.asn1.der.DerInteger;
import es.gob.jmulticard.asn1.der.Sequence;
import es.gob.jmulticard.asn1.der.x509.RdnSequence;
import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/pkcs15/X509CertificateAttributes.class */
public final class X509CertificateAttributes extends Sequence {
    public X509CertificateAttributes() {
        super(new OptionalDecoderObjectElement(Path.class, false), new OptionalDecoderObjectElement(RdnSequence.class, true), new OptionalDecoderObjectElement(CertificateIssuerContextSpecific.class, true), new OptionalDecoderObjectElement(DerInteger.class, true));
    }

    String getIssuer() {
        DecoderObject d = getObject(CertificateIssuerContextSpecific.class);
        if (d == null) {
            return null;
        }
        return d.toString();
    }

    String getSubject() {
        DecoderObject d = getObject(RdnSequence.class);
        if (d == null) {
            return null;
        }
        return d.toString();
    }

    String getPath() {
        DecoderObject d = getObject(Path.class);
        if (d == null) {
            return null;
        }
        return ((Path) d).getPathString();
    }

    byte[] getPathBytes() {
        DecoderObject d = getObject(Path.class);
        if (d == null) {
            return null;
        }
        return ((Path) d).getPathBytes();
    }

    BigInteger getSerialNumber() {
        DecoderObject d = getObject(DerInteger.class);
        if (d == null) {
            return null;
        }
        return ((DerInteger) d).getIntegerValue();
    }

    private DecoderObject getObject(Class<?> objectType) {
        if (objectType == null) {
            return null;
        }
        for (int i = 0; i < getElementCount(); i++) {
            if (getElementAt(i).getClass().equals(objectType)) {
                return getElementAt(i);
            }
        }
        return null;
    }

    public String toString() {
        return "Atributos del certificado\n Ruta: " + getPath() + (getSubject() != null ? "\n Titular: " + getSubject() : "") + (getIssuer() != null ? "\n Emisor: " + getIssuer() : "") + (getSerialNumber() != null ? "\n Numero de serie: " + getSerialNumber().toString() : "");
    }
}
