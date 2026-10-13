package es.gob.jmulticard.asn1.der.pkcs15;

import es.gob.jmulticard.asn1.DecoderObject;
import es.gob.jmulticard.asn1.OptionalDecoderObjectElement;
import es.gob.jmulticard.asn1.der.Record;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/pkcs15/Odf.class */
public final class Odf extends Record {
    public Odf() {
        super(new OptionalDecoderObjectElement(PrivateKeysContextSpecific.class, true), new OptionalDecoderObjectElement(PublicKeysContextSpecific.class, true), new OptionalDecoderObjectElement(TrustedPublicKeysContextSpecific.class, true), new OptionalDecoderObjectElement(SecretKeysContextSpecific.class, true), new OptionalDecoderObjectElement(CertificatesContextSpecific.class, true));
    }

    public Path getCdfPath() {
        for (int i = 0; i < getElementCount(); i++) {
            DecoderObject dobj = getElementAt(i);
            if (dobj instanceof CertificatesContextSpecific) {
                return ((CertificatesContextSpecific) dobj).getCertificatesPath();
            }
        }
        return null;
    }

    public Path getPrKdfPath() {
        for (int i = 0; i < getElementCount(); i++) {
            DecoderObject dobj = getElementAt(i);
            if (dobj instanceof PrivateKeysContextSpecific) {
                return ((PrivateKeysContextSpecific) dobj).getPrivateKeysPath();
            }
        }
        return null;
    }

    public Path getPuKdfPath() {
        for (int i = 0; i < getElementCount(); i++) {
            DecoderObject dobj = getElementAt(i);
            if (dobj instanceof PublicKeysContextSpecific) {
                return ((PublicKeysContextSpecific) dobj).getPublicKeysPath();
            }
        }
        return null;
    }

    public String toString() {
        return "ODF: \n Ruta hacia el CDF: " + getCdfPath() + "\n Ruta hacia el PrKDF: " + getPrKdfPath() + "\n Ruta hacia el PuKDF: " + getPuKdfPath();
    }
}
