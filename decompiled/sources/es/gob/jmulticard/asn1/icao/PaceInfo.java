package es.gob.jmulticard.asn1.icao;

import es.gob.jmulticard.asn1.OptionalDecoderObjectElement;
import es.gob.jmulticard.asn1.der.DerInteger;
import es.gob.jmulticard.asn1.der.ObjectIdentifier;
import es.gob.jmulticard.asn1.der.Sequence;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/icao/PaceInfo.class */
public final class PaceInfo extends Sequence {
    public PaceInfo() {
        super(new OptionalDecoderObjectElement(ObjectIdentifier.class, false), new OptionalDecoderObjectElement(DerInteger.class, false), new OptionalDecoderObjectElement(DerInteger.class, true));
    }

    public String toString() {
        StringBuilder ret = new StringBuilder("PACEInfo V");
        ret.append(getElementAt(1));
        ret.append(" para el protocolo ");
        ret.append(getElementAt(0));
        if (getElementCount() > 2) {
            ret.append(" y con identificador de parametro ");
            ret.append(getElementAt(2));
        }
        return ret.toString();
    }

    public String getProtocol() {
        return getElementAt(0).toString();
    }

    public int getVersion() {
        return ((DerInteger) getElementAt(1)).getIntegerValue().intValue();
    }

    public int getparameterId() {
        if (getElementCount() > 2) {
            return ((DerInteger) getElementAt(2)).getIntegerValue().intValue();
        }
        return -1;
    }
}
