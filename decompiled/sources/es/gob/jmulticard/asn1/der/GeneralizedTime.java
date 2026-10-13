package es.gob.jmulticard.asn1.der;

import es.gob.jmulticard.asn1.Asn1Exception;
import es.gob.jmulticard.asn1.DecoderObject;
import es.gob.jmulticard.asn1.Tlv;
import es.gob.jmulticard.asn1.TlvException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/GeneralizedTime.class */
public final class GeneralizedTime extends DecoderObject {
    private static final byte TAG = 24;
    private String strValue = null;

    @Override // es.gob.jmulticard.asn1.DecoderObject
    protected byte getDefaultTag() {
        return (byte) 24;
    }

    @Override // es.gob.jmulticard.asn1.DecoderObject
    protected void decodeValue() throws TlvException, Asn1Exception {
        Tlv tlv = new Tlv(getBytes());
        checkTag(tlv.getTag());
        this.strValue = new String(tlv.getValue(), StandardCharsets.US_ASCII);
    }

    public Date getDateValue() throws Asn1Exception {
        try {
            return new SimpleDateFormat("yyyyMMdd").parse(this.strValue.substring(0, 8));
        } catch (ParseException e) {
            throw new Asn1Exception("La fecha no estaba en el formato esperado: " + this.strValue, e);
        }
    }

    public String toString() {
        return this.strValue;
    }
}
