package es.gob.jmulticard.asn1;

import es.gob.jmulticard.HexUtils;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/DecoderObject.class */
public abstract class DecoderObject {
    private byte[] rawDerValue = null;

    protected abstract void decodeValue() throws TlvException, Asn1Exception;

    protected abstract byte getDefaultTag();

    public final byte[] getBytes() {
        byte[] out = new byte[this.rawDerValue.length];
        System.arraycopy(this.rawDerValue, 0, out, 0, this.rawDerValue.length);
        return out;
    }

    public void setDerValue(byte[] value) throws TlvException, Asn1Exception {
        if (value == null) {
            throw new IllegalArgumentException("El valor del objeto ASN.1 no puede ser nulo");
        }
        this.rawDerValue = new byte[value.length];
        System.arraycopy(value, 0, this.rawDerValue, 0, value.length);
        decodeValue();
    }

    public void checkTag(byte tag) throws Asn1Exception {
        if (getDefaultTag() != tag) {
            throw new Asn1Exception("Se esperaba un tipo " + HexUtils.hexify(new byte[]{getDefaultTag()}, false) + " (" + getClass().getName() + ") pero se encontro un tipo " + HexUtils.hexify(new byte[]{tag}, false));
        }
    }
}
