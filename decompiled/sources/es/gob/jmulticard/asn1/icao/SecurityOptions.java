package es.gob.jmulticard.asn1.icao;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.asn1.Asn1Exception;
import es.gob.jmulticard.asn1.DecoderObject;
import es.gob.jmulticard.asn1.TlvException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/icao/SecurityOptions.class */
public final class SecurityOptions extends DecoderObject {
    private static final byte TAG = 110;

    @Override // es.gob.jmulticard.asn1.DecoderObject
    protected void decodeValue() throws TlvException, Asn1Exception {
        checkTag(getBytes()[0]);
    }

    @Override // es.gob.jmulticard.asn1.DecoderObject
    protected byte getDefaultTag() {
        return (byte) 110;
    }

    public String toString() {
        return HexUtils.hexify(getBytes(), true);
    }
}
