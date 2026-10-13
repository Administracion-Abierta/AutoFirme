package es.gob.jmulticard.connection;

import es.gob.jmulticard.apdu.CommandApdu;
import es.gob.jmulticard.apdu.ResponseApdu;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/connection/ApduConnection.class */
public interface ApduConnection {
    void setProtocol(ApduConnectionProtocol apduConnectionProtocol);

    void open() throws ApduConnectionException;

    void close() throws ApduConnectionException;

    ResponseApdu transmit(CommandApdu commandApdu) throws ApduConnectionException;

    byte[] reset() throws ApduConnectionException;

    long[] getTerminals(boolean z) throws ApduConnectionException;

    String getTerminalInfo(int i) throws ApduConnectionException;

    void setTerminal(int i) throws ApduConnectionException;

    boolean isOpen();

    ApduConnection getSubConnection();
}
