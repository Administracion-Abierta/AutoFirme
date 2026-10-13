package es.gob.jmulticard.asn1;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.JmcLogger;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/Tlv.class */
public final class Tlv {
    private final byte tag;
    private final int length;
    private final byte[] bytes;
    private final int valueOffset;

    public Tlv(byte t, byte[] value) {
        int iExtLen;
        if (value == null) {
            throw new IllegalArgumentException("El valor del TLV no puede ser nulo");
        }
        this.valueOffset = 2;
        this.tag = t;
        this.length = value.length;
        if (this.length >= 256) {
            iExtLen = 4;
        } else if (this.length >= 128) {
            iExtLen = 3;
        } else {
            iExtLen = 2;
        }
        this.bytes = new byte[value.length + iExtLen];
        this.bytes[0] = t;
        if (this.length >= 256) {
            this.bytes[1] = -126;
            this.bytes[2] = (byte) ((value.length >> 8) & 255);
            this.bytes[3] = (byte) (value.length & 255);
        } else if (value.length >= 128) {
            this.bytes[1] = -127;
            this.bytes[2] = (byte) value.length;
        } else {
            this.bytes[1] = (byte) value.length;
        }
        System.arraycopy(value, 0, this.bytes, iExtLen, value.length);
    }

    public Tlv(byte[] buffer) throws TlvException {
        if (buffer == null) {
            throw new IllegalArgumentException("El TLV no puede ser nulo");
        }
        if (buffer.length == 0) {
            JmcLogger.warning("Se ha pedido crear un TLV vacio");
            this.length = 0;
            this.bytes = new byte[0];
            this.tag = (byte) -1;
            this.valueOffset = 0;
            return;
        }
        if (buffer.length < 2) {
            throw new IllegalArgumentException("El TLV no puede medir menos de dos octetos: " + HexUtils.hexify(buffer, false));
        }
        byte[] tempBytes = new byte[buffer.length];
        System.arraycopy(buffer, 0, tempBytes, 0, buffer.length);
        int offset = 0 + 1;
        this.tag = tempBytes[0];
        int offset2 = offset + 1;
        int size = tempBytes[offset] & 255;
        boolean indefinite = size == 128;
        if (indefinite) {
            if ((this.tag & 32) == 0) {
                throw new TlvException("Longitud del TLV invalida");
            }
        } else if (size >= 128) {
            int sizeLen = size - 128;
            if (sizeLen > 3) {
                throw new TlvException("TLV demasiado largo");
            }
            size = 0;
            while (sizeLen > 0) {
                int i = offset2;
                offset2++;
                size = (size << 8) + (tempBytes[i] & 255);
                sizeLen--;
            }
        }
        this.length = size;
        this.valueOffset = offset2;
        this.bytes = new byte[this.valueOffset + this.length];
        System.arraycopy(tempBytes, 0, this.bytes, 0, this.valueOffset + this.length);
    }

    public byte[] getBytes() {
        byte[] out = new byte[this.bytes.length];
        System.arraycopy(this.bytes, 0, out, 0, this.bytes.length);
        return out;
    }

    public int getLength() {
        return this.length;
    }

    public byte getTag() {
        return this.tag;
    }

    public byte[] getValue() {
        byte[] out = new byte[this.length];
        System.arraycopy(this.bytes, this.valueOffset, out, 0, this.length);
        return out;
    }

    public static Tlv decode(ByteArrayInputStream recordOfTlv) throws IOException {
        byte tag = (byte) recordOfTlv.read();
        if ((tag & 31) == 31) {
            throw new IOException("El tipo del TLV es invalido");
        }
        int size = recordOfTlv.read() & 255;
        boolean indefinite = size == 128;
        if (indefinite) {
            if ((tag & 32) == 0) {
                throw new IOException("Longitud del TLV invalida");
            }
        } else if (size >= 128) {
            int sizeLen = size - 128;
            if (sizeLen > 3) {
                throw new IOException("TLV demasiado largo");
            }
            size = 0;
            while (sizeLen > 0) {
                size = (size << 8) + (recordOfTlv.read() & 255);
                sizeLen--;
            }
        }
        byte[] value = new byte[size];
        if (value.length != recordOfTlv.read(value)) {
            throw new IndexOutOfBoundsException("La longitud de los datos leidos no coincide con el parametro indicado");
        }
        return new Tlv(tag, value);
    }

    public String toString() {
        return "TLV (Tag=" + HexUtils.hexify(new byte[]{getTag()}, false) + ", Length=" + getLength() + ", Value=" + (getValue().length == 0 ? "vacio" : HexUtils.hexify(getValue(), false)) + ")";
    }
}
