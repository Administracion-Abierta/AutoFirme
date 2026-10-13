package es.gob.jmulticard.connection.cwa14890;

import es.gob.jmulticard.apdu.CommandApdu;
import java.io.ByteArrayOutputStream;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/connection/cwa14890/Cwa14890CipheredApdu.class */
public final class Cwa14890CipheredApdu extends CommandApdu {
    private static final byte TAG_CRYPTOGRAPHIC_CHECKSUM = -114;
    private final byte[] mac;
    private final byte[] data;

    byte[] getMac() {
        byte[] out = new byte[this.mac.length];
        System.arraycopy(this.mac, 0, out, 0, this.mac.length);
        return out;
    }

    byte[] getCryptogramData() {
        byte[] out = new byte[this.data.length];
        System.arraycopy(this.data, 0, out, 0, this.data.length);
        return out;
    }

    Cwa14890CipheredApdu(byte cla, byte ins, byte p1, byte p2, byte[] apduData, byte[] apduMac) {
        super(cla, ins, p1, p2, buildData(apduData, apduMac), null);
        this.mac = new byte[apduMac.length];
        System.arraycopy(apduMac, 0, this.mac, 0, apduMac.length);
        this.data = new byte[apduData.length];
        System.arraycopy(apduData, 0, this.data, 0, apduData.length);
    }

    private static byte[] buildData(byte[] data, byte[] mac) {
        if (data == null || mac == null) {
            throw new IllegalArgumentException("Ni los datos (TLV) ni el MAC pueden ser nulos");
        }
        if (mac.length != 4 && mac.length != 8) {
            throw new IllegalArgumentException("El MAC debe medir cuatro u ocho octetos, y el recibido mide " + mac.length + " octetos");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            baos.write(data);
            baos.write(TAG_CRYPTOGRAPHIC_CHECKSUM);
            baos.write((byte) mac.length);
            baos.write(mac);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Error creando la APDU cifrada", e);
        }
    }

    @Override // es.gob.jmulticard.apdu.CommandApdu
    public void setLe(int le) {
        throw new UnsupportedOperationException("No se puede establecer el Le en una APDU cifrada");
    }
}
