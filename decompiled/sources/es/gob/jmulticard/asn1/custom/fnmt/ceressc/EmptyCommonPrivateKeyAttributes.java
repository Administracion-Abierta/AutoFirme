package es.gob.jmulticard.asn1.custom.fnmt.ceressc;

import es.gob.jmulticard.asn1.Asn1Exception;
import es.gob.jmulticard.asn1.DecoderObject;
import es.gob.jmulticard.asn1.TlvException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/custom/fnmt/ceressc/EmptyCommonPrivateKeyAttributes.class */
public final class EmptyCommonPrivateKeyAttributes extends DecoderObject {
    @Override // es.gob.jmulticard.asn1.DecoderObject
    protected void decodeValue() throws TlvException, Asn1Exception {
    }

    @Override // es.gob.jmulticard.asn1.DecoderObject
    protected byte getDefaultTag() {
        return (byte) -1;
    }
}
