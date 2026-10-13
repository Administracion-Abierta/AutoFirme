package es.gob.jmulticard.asn1.icao;

import es.gob.jmulticard.asn1.der.SequenceOf;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/icao/DataGroupHashValues.class */
public final class DataGroupHashValues extends SequenceOf {
    public DataGroupHashValues() {
        super(DataGroupHash.class);
    }

    public DataGroupHash[] getDataGroupsHashes() {
        DataGroupHash[] ret = new DataGroupHash[getElementCount()];
        for (int i = 0; i < getElementCount(); i++) {
            ret[i] = (DataGroupHash) getElementAt(i);
        }
        return ret;
    }
}
