package es.gob.jmulticard.jse.smartcardio;

import com.ctc.wstx.cfg.XmlConsts;
import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.apdu.ResponseApdu;
import es.gob.jmulticard.connection.AbstractApduConnectionIso7816;
import es.gob.jmulticard.connection.ApduConnection;
import es.gob.jmulticard.connection.ApduConnectionException;
import es.gob.jmulticard.connection.ApduConnectionProtocol;
import es.gob.jmulticard.connection.LostChannelException;
import es.gob.jmulticard.connection.NoReadersFoundException;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardNotPresentException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.TerminalFactory;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/jse/smartcardio/SmartcardIoConnection.class */
public final class SmartcardIoConnection extends AbstractApduConnectionIso7816 {
    private static final boolean DEBUG = false;
    private static final int MAX_APDU_SIZE = 255;
    private static final String SCARD_W_RESET_CARD = "SCARD_W_RESET_CARD";
    private static final String SYSTEM_PROPERTY_IGNORE_VIRTUAL_READERS = "ignoreVirtualReaders";
    private static final Logger LOGGER = Logger.getLogger(SmartcardIoConnection.class.getName());
    private int terminalNumber = -1;
    private CardChannel cardChannel = null;
    private Card card = null;
    private boolean exclusive = false;
    private ApduConnectionProtocol protocol = ApduConnectionProtocol.ANY;
    List<CardTerminal> terminales = null;

    static {
        try {
            LibJ2PCSCGNULinuxFix.fixNativeLibrary();
        } catch (Error | Exception e) {
            LOGGER.warning("No se han podido aplicar las correcciones al error 529339 de Debian: " + e);
        }
        String osName = System.getProperty("os.name");
        if (osName != null && osName.startsWith("Mac OS X") && new File("/System/Library/Frameworks/PCSC.framework/Versions/Current").isDirectory()) {
            System.setProperty("sun.security.smartcardio.library", "/System/Library/Frameworks/PCSC.framework/Versions/Current/PCSC");
        }
    }

    public String toString() {
        String str;
        StringBuilder sbAppend = new StringBuilder().append("Conexion de bajo nivel JSR-268 ");
        if (isOpen()) {
            str = "abierta en modo " + (this.exclusive ? "" : XmlConsts.XML_SA_NO) + " exclusivo";
        } else {
            str = "cerrada";
        }
        return sbAppend.append(str).toString();
    }

    @Override // es.gob.jmulticard.connection.ApduConnection
    public void close() throws ApduConnectionException {
        if (this.card != null) {
            try {
                this.card.disconnect(false);
                this.card = null;
            } catch (Exception e) {
                throw new ApduConnectionException("Error intentando cerrar el objeto de tarjeta inteligente, la conexion puede quedar abierta pero inutil", e);
            }
        }
        this.cardChannel = null;
    }

    @Override // es.gob.jmulticard.connection.ApduConnection
    public String getTerminalInfo(int terminal) throws ApduConnectionException {
        CardTerminal cardTerminal;
        try {
            List<CardTerminal> terminalList = getTerminals();
            if (terminal < terminalList.size() && (cardTerminal = terminalList.get(terminal)) != null) {
                return cardTerminal.getName();
            }
            return null;
        } catch (Exception ex) {
            throw new ApduConnectionException("Error recuperando la lista de lectores de tarjetas del sistema", ex);
        }
    }

    private List<CardTerminal> getTerminals() {
        if (this.terminales != null) {
            return this.terminales;
        }
        try {
            this.terminales = TerminalFactory.getDefault().terminals().list();
            boolean ignoreVirtualReaders = Boolean.getBoolean(SYSTEM_PROPERTY_IGNORE_VIRTUAL_READERS);
            if (ignoreVirtualReaders) {
                List<CardTerminal> filteredList = new ArrayList<>();
                for (CardTerminal terminal : this.terminales) {
                    if (!isVirtual(terminal)) {
                        filteredList.add(terminal);
                    }
                }
                this.terminales = filteredList;
            }
            return this.terminales;
        } catch (CardException e) {
            LOGGER.log(Level.WARNING, "No se ha podido recuperar la lista de lectores del sistema", e);
            return Collections.emptyList();
        }
    }

    private static boolean isVirtual(CardTerminal terminal) {
        String name = terminal.getName();
        return name != null && name.startsWith("Windows Hello");
    }

    @Override // es.gob.jmulticard.connection.ApduConnection
    public long[] getTerminals(boolean onlyWithCardPresent) throws ApduConnectionException {
        List<CardTerminal> terminalList = getTerminals();
        try {
            ArrayList<Long> idsTerminales = new ArrayList<>(terminalList.size());
            for (int idx = 0; idx < terminalList.size(); idx++) {
                if (onlyWithCardPresent) {
                    if (terminalList.get(idx).isCardPresent()) {
                        idsTerminales.add(Long.valueOf(idx));
                    }
                } else {
                    idsTerminales.add(Long.valueOf(idx));
                }
            }
            long[] ids = new long[idsTerminales.size()];
            for (int i = 0; i < ids.length; i++) {
                ids[i] = idsTerminales.get(i).longValue();
            }
            return ids;
        } catch (Exception ex) {
            throw new ApduConnectionException("Error recuperando la lista de lectores de tarjetas del sistema", ex);
        }
    }

    @Override // es.gob.jmulticard.connection.ApduConnection
    public boolean isOpen() {
        return this.card != null;
    }

    @Override // es.gob.jmulticard.connection.ApduConnection
    public void open() throws ApduConnectionException {
        if (isOpen()) {
            return;
        }
        System.setProperty("sun.security.smartcardio.t0GetResponse", "false");
        System.setProperty("sun.security.smartcardio.t1GetResponse", "false");
        List<CardTerminal> terminalList = getTerminals();
        try {
            if (terminalList.isEmpty()) {
                throw new NoReadersFoundException();
            }
            if (this.terminalNumber == -1) {
                long[] cadsWithCard = getTerminals(true);
                if (cadsWithCard.length <= 0) {
                    throw new ApduConnectionException("En el sistema no hay ningun terminal con tarjeta insertada");
                }
                this.terminalNumber = (int) cadsWithCard[0];
            }
            if (terminalList.size() <= this.terminalNumber) {
                throw new ApduConnectionException("No se detecto el lector de tarjetas numero " + this.terminalNumber);
            }
            this.card = terminalList.get(this.terminalNumber).connect(this.protocol.toString());
            if (this.exclusive) {
                try {
                    this.card.beginExclusive();
                } catch (CardException e) {
                    throw new ApduConnectionException("No se ha podido abrir la conexion exclusiva con el lector de tarjetas numero " + Integer.toString(this.terminalNumber), e);
                }
            }
            this.cardChannel = this.card.getBasicChannel();
            this.protocol = ApduConnectionProtocol.getApduConnectionProtocol(this.card.getProtocol());
        } catch (CardNotPresentException e2) {
            throw new es.gob.jmulticard.connection.CardNotPresentException(e2);
        } catch (CardException e3) {
            throw new ApduConnectionException("No se ha podido abrir la conexion con el lector de tarjetas numero " + this.terminalNumber, e3);
        }
    }

    @Override // es.gob.jmulticard.connection.ApduConnection
    public byte[] reset() throws ApduConnectionException {
        if (this.card != null) {
            try {
                this.card.disconnect(true);
            } catch (CardException e) {
                LOGGER.warning("Error reiniciando la tarjeta: " + e);
            }
        }
        this.card = null;
        open();
        if (this.card != null) {
            return this.card.getATR().getBytes();
        }
        throw new ApduConnectionException("Error indefinido reiniciando la conexion con la tarjeta");
    }

    public void setExclusiveUse(boolean ex) {
        if (this.card == null) {
            this.exclusive = ex;
        } else {
            LOGGER.warning("No se puede cambiar el modo de acceso a la tarjeta con la conexion abierta, se mantendra el modo EXCLUSIVE=" + Boolean.toString(this.exclusive));
        }
    }

    @Override // es.gob.jmulticard.connection.ApduConnection
    public void setProtocol(ApduConnectionProtocol p) {
        if (p == null) {
            LOGGER.warning("El protocolo de conexion no puede ser nulo, se usara T=0");
            this.protocol = ApduConnectionProtocol.T0;
        } else {
            this.protocol = p;
        }
    }

    @Override // es.gob.jmulticard.connection.ApduConnection
    public void setTerminal(int terminalN) {
        if (this.terminalNumber == terminalN) {
            return;
        }
        boolean wasOpened = isOpen();
        this.terminalNumber = terminalN;
        if (wasOpened) {
            try {
                close();
            } catch (ApduConnectionException e) {
                LOGGER.warning("Error intentando cerrar la conexion con el lector: " + e);
            }
            try {
                open();
            } catch (Exception e2) {
                LOGGER.warning("Error intentando abrir la conexion con el lector: " + e2);
            }
        }
    }

    @Override // es.gob.jmulticard.connection.AbstractApduConnectionIso7816
    public ResponseApdu internalTransmit(byte[] command) throws ApduConnectionException {
        String str;
        String str2;
        if (this.cardChannel == null) {
            throw new ApduConnectionException("No se puede transmitir sobre una conexion cerrada");
        }
        CommandAPDU commandApdu = new CommandAPDU(command);
        boolean isChv = commandApdu.getINS() == 32;
        try {
            ResponseApdu response = new ResponseApdu(this.cardChannel.transmit(commandApdu).getBytes());
            return response;
        } catch (CardException e) {
            Throwable t = e.getCause();
            if (t != null && SCARD_W_RESET_CARD.equals(t.getMessage())) {
                throw new LostChannelException(t.getMessage(), t);
            }
            StringBuilder sbAppend = new StringBuilder().append("Error de comunicacion con la tarjeta tratando de transmitir la APDU");
            if (isChv) {
                str2 = " de verificacion de PIN";
            } else {
                str2 = "\n" + HexUtils.hexify(command, command.length > 32) + "\nAl lector " + Integer.toString(this.terminalNumber) + " en modo EXCLUSIVE=" + Boolean.toString(this.exclusive) + " con el protocolo " + getProtocol();
            }
            throw new ApduConnectionException(sbAppend.append(str2).toString(), e);
        } catch (Exception e2) {
            StringBuilder sbAppend2 = new StringBuilder().append("Error de comunicacion con la tarjeta tratando de transmitir la APDU");
            if (isChv) {
                str = " de verificacion de PIN";
            } else {
                str = "\n" + HexUtils.hexify(command, command.length > 32) + "\nAl lector " + Integer.toString(this.terminalNumber) + " en modo EXCLUSIVE=" + Boolean.toString(this.exclusive) + " con el protocolo " + getProtocol();
            }
            throw new ApduConnectionException(sbAppend2.append(str).toString(), e2);
        }
    }

    public ApduConnectionProtocol getProtocol() {
        return this.protocol;
    }

    public boolean isExclusiveUse() {
        return this.exclusive;
    }

    @Override // es.gob.jmulticard.connection.ApduConnection
    public ApduConnection getSubConnection() {
        return null;
    }

    @Override // es.gob.jmulticard.connection.AbstractApduConnectionIso7816
    public int getMaxApduSize() {
        return 255;
    }
}
