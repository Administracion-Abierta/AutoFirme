package es.gob.jmulticard.card;

import es.gob.jmulticard.apdu.CommandApdu;
import es.gob.jmulticard.apdu.ResponseApdu;
import es.gob.jmulticard.connection.ApduConnection;
import es.gob.jmulticard.connection.ApduConnectionException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/AbstractSmartCard.class */
public abstract class AbstractSmartCard implements Card {
    private final byte cla;
    private ApduConnection connection;

    public abstract String getCardName();

    protected ApduConnection getConnection() {
        return this.connection;
    }

    protected ResponseApdu sendArbitraryApdu(CommandApdu apdu) throws ApduConnectionException {
        return this.connection.transmit(apdu);
    }

    protected final void setConnection(ApduConnection conn) throws ApduConnectionException {
        if (!conn.isOpen()) {
            conn.open();
        }
        this.connection = conn;
    }

    protected byte getCla() {
        return this.cla;
    }

    protected AbstractSmartCard(byte c, ApduConnection conn) {
        if (conn == null) {
            throw new IllegalArgumentException("La conexion con la tarjeta no puede ser nula");
        }
        this.cla = c;
        this.connection = conn;
    }
}
