package es.gob.jmulticard.asn1.icao;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.asn1.OptionalDecoderObjectElement;
import es.gob.jmulticard.asn1.der.DerInteger;
import es.gob.jmulticard.asn1.der.Sequence;
import es.gob.jmulticard.asn1.der.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/icao/LdsSecurityObject.class */
public final class LdsSecurityObject extends Sequence {
    public LdsSecurityObject() {
        super(new OptionalDecoderObjectElement(DerInteger.class, false), new OptionalDecoderObjectElement(AlgorithmIdentifier.class, false), new OptionalDecoderObjectElement(DataGroupHashValues.class, false), new OptionalDecoderObjectElement(Sequence.class, true));
    }

    public String toString() {
        StringBuilder ret = new StringBuilder("LDSSecurityObject V");
        ret.append(getElementAt(0));
        ret.append(" con huellas para el algoritmo ");
        ret.append(getElementAt(1));
        ret.append(" y con el siguiente contenido:");
        DataGroupHashValues dghv = (DataGroupHashValues) getElementAt(2);
        for (DataGroupHash dgh : dghv.getDataGroupsHashes()) {
            ret.append("\n  DG");
            ret.append(dgh.getDataGroupNumber());
            ret.append(" con huella ");
            ret.append(HexUtils.hexify(dgh.getDataGroupHashValue(), false));
        }
        return ret.toString();
    }

    public String getDigestAlgorithm() {
        return getElementAt(1).toString();
    }

    public DataGroupHash[] getDataGroupHashes() {
        return ((DataGroupHashValues) getElementAt(2)).getDataGroupsHashes();
    }
}
