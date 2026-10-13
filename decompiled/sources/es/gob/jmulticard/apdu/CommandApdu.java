package es.gob.jmulticard.apdu;

import java.io.ByteArrayOutputStream;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/CommandApdu.class */
public class CommandApdu extends Apdu {
    private final byte cla;
    private final byte ins;
    private final byte p1;
    private final byte p2;
    private Integer le;
    private final byte[] body;

    private static byte[] getBody(byte[] bytes) {
        if (bytes == null || bytes.length < 5) {
            throw new IllegalArgumentException("La longitud del array de octetos debe ser igual o mayor que 5");
        }
        int i = bytes[4] & 255;
        if (bytes.length > 5) {
            byte[] data = new byte[i];
            System.arraycopy(bytes, 5, data, 0, i);
            return data;
        }
        return null;
    }

    private static Integer getLength(byte[] bytes) {
        if (bytes == null || bytes.length < 5) {
            throw new IllegalArgumentException("La longitud del array de octetos debe ser igual o mayor que 5.");
        }
        int i = bytes[4] & 255;
        if (bytes.length > 5 && bytes.length > i + 5) {
            return Integer.valueOf(bytes[i + 5]);
        }
        if (bytes.length == 5) {
            return Integer.valueOf(i);
        }
        return null;
    }

    public CommandApdu(byte[] bytes) {
        this(bytes[0], bytes[1], bytes[2], bytes[3], getBody(bytes), getLength(bytes));
    }

    public CommandApdu(byte apduCla, byte apduIns, byte param1, byte param2, byte[] data, Integer ne) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.cla = apduCla;
        baos.write(apduCla);
        this.ins = apduIns;
        baos.write(apduIns);
        this.p1 = param1;
        baos.write(param1);
        this.p2 = param2;
        baos.write(param2);
        if (data == null) {
            this.body = null;
        } else {
            this.body = new byte[data.length];
            System.arraycopy(data, 0, this.body, 0, data.length);
            if (data.length <= 255) {
                baos.write(Integer.valueOf(String.valueOf(this.body.length)).byteValue());
            } else {
                baos.write(0);
                baos.write((byte) (data.length >> 8));
                baos.write((byte) (data.length & 255));
            }
            if (this.body.length > 0) {
                try {
                    baos.write(this.body);
                } catch (Exception e) {
                    throw new IllegalArgumentException("No se pueden tratar los datos de la APDU", e);
                }
            }
        }
        this.le = ne;
        if (ne != null) {
            if (ne.intValue() <= 255) {
                baos.write(ne.byteValue());
            } else {
                baos.write((byte) (ne.intValue() >> 8));
                baos.write((byte) (ne.intValue() & 255));
            }
        }
        setBytes(baos.toByteArray());
    }

    public byte getCla() {
        return this.cla;
    }

    public byte[] getData() {
        if (this.body == null) {
            return null;
        }
        byte[] out = new byte[this.body.length];
        System.arraycopy(this.body, 0, out, 0, this.body.length);
        return out;
    }

    public byte getIns() {
        return this.ins;
    }

    public Integer getLe() {
        return this.le;
    }

    public byte getP1() {
        return this.p1;
    }

    public byte getP2() {
        return this.p2;
    }

    public void setLe(int apduLe) {
        this.le = Integer.valueOf(String.valueOf(apduLe));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(this.cla);
        baos.write(this.ins);
        baos.write(this.p1);
        baos.write(this.p2);
        if (this.body != null && this.body.length > 0) {
            try {
                baos.write(this.body);
            } catch (Exception e) {
                throw new IllegalArgumentException("No se pueden tratar los datos de la APDU", e);
            }
        }
        baos.write(apduLe);
        setBytes(baos.toByteArray());
    }
}
