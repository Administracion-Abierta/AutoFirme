package es.gob.jmulticard.asn1.der.pkcs15;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.asn1.OptionalDecoderObjectElement;
import es.gob.jmulticard.asn1.der.OctectString;
import es.gob.jmulticard.asn1.der.Sequence;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/pkcs15/CommonCertificateAttributes.class */
public final class CommonCertificateAttributes extends Sequence {
    public CommonCertificateAttributes() {
        super(new OptionalDecoderObjectElement(Identifier.class, false));
    }

    public byte[] getId() {
        if (getElementAt(0) == null) {
            throw new IllegalStateException("No existe el identificador dentro del objeto");
        }
        return ((OctectString) getElementAt(0)).getOctectStringByteValue();
    }

    public String toString() {
        return HexUtils.hexify(getId(), false);
    }
}
