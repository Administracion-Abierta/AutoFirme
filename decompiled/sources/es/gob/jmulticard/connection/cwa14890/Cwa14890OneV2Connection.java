package es.gob.jmulticard.connection.cwa14890;

import es.gob.jmulticard.CryptoHelper;
import es.gob.jmulticard.card.cwa14890.Cwa14890Card;
import es.gob.jmulticard.card.cwa14890.Cwa14890PrivateConstants;
import es.gob.jmulticard.card.cwa14890.Cwa14890PublicConstants;
import es.gob.jmulticard.connection.AbstractApduEncrypter;
import es.gob.jmulticard.connection.ApduConnection;
import es.gob.jmulticard.connection.ApduEncrypterDesMac8;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/connection/cwa14890/Cwa14890OneV2Connection.class */
public class Cwa14890OneV2Connection extends Cwa14890OneV1Connection {
    private final ChannelType channelType;

    public Cwa14890OneV2Connection(ApduConnection connection, CryptoHelper cryptoHlpr, ChannelType chType) {
        super(connection, cryptoHlpr);
        this.channelType = chType;
    }

    public Cwa14890OneV2Connection(Cwa14890Card card, ApduConnection connection, CryptoHelper cryptoHlpr, Cwa14890PublicConstants cwaConsts, Cwa14890PrivateConstants cwaPrivConsts, ChannelType chType) {
        super(card, connection, cryptoHlpr, cwaConsts, cwaPrivConsts);
        this.channelType = chType;
    }

    public ChannelType getChannelType() {
        return this.channelType;
    }

    @Override // es.gob.jmulticard.connection.cwa14890.Cwa14890OneV1Connection
    protected AbstractApduEncrypter instantiateApduEncrypter() {
        return new ApduEncrypterDesMac8();
    }

    @Override // es.gob.jmulticard.connection.cwa14890.Cwa14890OneV1Connection
    public String toString() {
        return "Conexion de tipo CWA-14890-V2 " + (isOpen() ? "abierta sobre " + getSubConnection() : "cerrada");
    }
}
