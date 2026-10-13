package es.gob.jmulticard.apdu;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.JmcLogger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/Apdu.class */
public class Apdu {
    private byte[] apduBytes = null;

    public byte[] getBytes() {
        if (this.apduBytes == null) {
            JmcLogger.warning("Se han pedido los octetos de una APDU de cuerpo nulo, se devolvera null");
            return null;
        }
        byte[] response = new byte[this.apduBytes.length];
        System.arraycopy(this.apduBytes, 0, response, 0, this.apduBytes.length);
        return response;
    }

    protected void setBytes(byte[] apdu) {
        if (apdu == null) {
            JmcLogger.warning("Se ha pedido crear una APDU con el cuerpo nulo");
            this.apduBytes = null;
            return;
        }
        if (apdu.length == 0) {
            JmcLogger.warning("Se ha pedido crear una APDU con el cuerpo vacio");
        } else if (apdu.length < 1) {
            JmcLogger.warning("Se ha pedido crear una APDU con cuerpo de tamano inferior a dos octetos");
        }
        this.apduBytes = new byte[apdu.length];
        System.arraycopy(apdu, 0, this.apduBytes, 0, apdu.length);
    }

    public String toString() {
        return HexUtils.hexify(getBytes(), getBytes().length > 32);
    }

    protected Apdu() {
    }
}
