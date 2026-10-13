package es.gob.jmulticard.connection.pace;

import es.gob.jmulticard.CryptoHelper;
import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.JmcLogger;
import es.gob.jmulticard.apdu.CommandApdu;
import es.gob.jmulticard.apdu.ResponseApdu;
import es.gob.jmulticard.apdu.StatusWord;
import es.gob.jmulticard.connection.ApduConnection;
import es.gob.jmulticard.connection.ApduConnectionException;
import es.gob.jmulticard.connection.cwa14890.ChannelType;
import es.gob.jmulticard.connection.cwa14890.Cwa14890OneV2Connection;
import es.gob.jmulticard.connection.cwa14890.InvalidCryptographicChecksumException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/connection/pace/PaceConnection.class */
public final class PaceConnection extends Cwa14890OneV2Connection {
    private static final StatusWord INVALID_CRYPTO_CHECKSUM = new StatusWord((byte) 102, (byte) -120);
    private static final byte MSB_INCORRECT_LE = 108;
    private final SecureMessaging sm;

    public PaceConnection(ApduConnection connection, CryptoHelper cryptoHlpr, SecureMessaging secMsg) {
        super(connection, cryptoHlpr, ChannelType.PACE);
        this.sm = secMsg;
        this.subConnection = connection;
    }

    @Override // es.gob.jmulticard.connection.cwa14890.Cwa14890OneV2Connection, es.gob.jmulticard.connection.cwa14890.Cwa14890OneV1Connection
    public String toString() {
        return "Conexion de tipo PACE " + (isOpen() ? "abierta sobre " + getSubConnection() : "cerrada");
    }

    @Override // es.gob.jmulticard.connection.cwa14890.Cwa14890OneV1Connection, es.gob.jmulticard.connection.ApduConnection
    public void open() {
        this.openState = true;
    }

    @Override // es.gob.jmulticard.connection.cwa14890.Cwa14890OneV1Connection, es.gob.jmulticard.connection.ApduConnection
    public ResponseApdu transmit(CommandApdu command) throws ApduConnectionException {
        boolean isChv = command.getIns() == 32;
        JmcLogger.debug(PaceConnection.class.getName(), "transmit", "APDU de comando en claro: " + (isChv ? "Verificacion de PIN" : HexUtils.hexify(command.getBytes(), true)));
        try {
            CommandApdu protectedApdu = this.sm.wrap(command);
            ResponseApdu responseApdu = this.subConnection.transmit(protectedApdu);
            if (!responseApdu.getStatusWord().isOk() && !new StatusWord((byte) 98, (byte) -126).equals(responseApdu.getStatusWord())) {
                throw new ApduConnectionException("Error transmitiendo la APDU cifrada:\n  Error: " + responseApdu.getStatusWord() + "\n  Respuesta:\n" + responseApdu + "\n  Comando cifrado:\n" + (isChv ? "Verificacion de PIN" : protectedApdu) + "\n  Comando en claro:\n" + (isChv ? "Verificacion de PIN" : command) + '\n');
            }
            try {
                ResponseApdu decipherApdu = this.sm.unwrap(responseApdu);
                JmcLogger.debug(PaceConnection.class.getName(), "transmit", "APDU de respuesta en claro: " + HexUtils.hexify(decipherApdu.getBytes(), true));
                if (INVALID_CRYPTO_CHECKSUM.equals(decipherApdu.getStatusWord())) {
                    throw new InvalidCryptographicChecksumException();
                }
                if (decipherApdu.getStatusWord().getMsb() == 108) {
                    command.setLe(decipherApdu.getStatusWord().getLsb());
                    return transmit(command);
                }
                return decipherApdu;
            } catch (SecureMessagingException e1) {
                throw new ApduConnectionException("No ha sido posible descifrar un mensaje seguro con el canal PACE", e1);
            }
        } catch (SecureMessagingException e) {
            throw new ApduConnectionException("No ha sido posible cifrar un mensaje seguro con el canal PACE", e);
        }
    }
}
