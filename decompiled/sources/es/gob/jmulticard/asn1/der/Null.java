package es.gob.jmulticard.asn1.der;

import es.gob.jmulticard.JmcLogger;
import es.gob.jmulticard.asn1.Asn1Exception;
import es.gob.jmulticard.asn1.DecoderObject;
import es.gob.jmulticard.asn1.TlvException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/Null.class */
public final class Null extends DecoderObject {
    @Override // es.gob.jmulticard.asn1.DecoderObject
    protected void decodeValue() {
    }

    @Override // es.gob.jmulticard.asn1.DecoderObject
    protected byte getDefaultTag() {
        return (byte) 0;
    }

    @Override // es.gob.jmulticard.asn1.DecoderObject
    public void setDerValue(byte[] value) throws TlvException, Asn1Exception {
        if (value == null || value.length == 0) {
            super.setDerValue(new byte[0]);
        } else {
            JmcLogger.warning("Se ha proporcionado datos a un tipo nulo, estos se ignoraran");
        }
    }

    @Override // es.gob.jmulticard.asn1.DecoderObject
    public void checkTag(byte tag) {
    }
}
