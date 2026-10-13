package es.gob.jmulticard.card.dnie;

import es.gob.jmulticard.CryptoHelper;
import es.gob.jmulticard.JmcLogger;
import es.gob.jmulticard.card.Atr;
import es.gob.jmulticard.card.InvalidCardException;
import es.gob.jmulticard.card.dnie.ceressc.CeresSc;
import es.gob.jmulticard.card.dnie.tif.Tif;
import es.gob.jmulticard.card.icao.IcaoException;
import es.gob.jmulticard.card.icao.IcaoMrtdWithPace;
import es.gob.jmulticard.card.icao.InvalidCanOrMrzException;
import es.gob.jmulticard.card.icao.MrtdLds1;
import es.gob.jmulticard.connection.ApduConnection;
import es.gob.jmulticard.connection.ApduConnectionException;
import es.gob.jmulticard.connection.CardNotPresentException;
import es.gob.jmulticard.connection.NoReadersFoundException;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/dnie/DnieFactory.class */
public final class DnieFactory {
    private static final byte[] ATR_MASK = {-1, -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, -1, -1};
    private static final byte[] ATR_NFC_MASK = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, 0};
    private static final byte[] ATR_NFC2_MASK = {-1, -1, -1, -1, -1, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, 0, 0};
    private static final Atr ATR_NFC = new Atr(new byte[]{59, -120, -128, 1, -31, -13, 94, 17, 119, -127, -95, 0, 3}, ATR_NFC_MASK);
    private static final Atr ATR_NFC2 = new Atr(new byte[]{59, -116, -128, 1, 80, 66, -114, -109, 42, -31, -13, 94, 17, 119, -127, -127, 2}, ATR_NFC2_MASK);
    private static final Atr ATR = new Atr(new byte[]{59, 127, 0, 0, 0, 0, 106, 68, 78, 73, 101, 0, 0, 0, 0, 0, 0, 0, -112, 0}, ATR_MASK);
    private static final Atr ATR_TIF = new Atr(new byte[]{59, 127, 0, 0, 0, 0, 106, 84, 73, 70, 49, 0, 0, 0, 0, 0, 0, 0, -112, 0}, ATR_MASK);
    private static final String DNIE3_R2_IDESP = "BMP100001";

    private DnieFactory() {
    }

    public static Dnie getDnie(ApduConnection conn, PasswordCallback pwc, CryptoHelper cryptoHelper, CallbackHandler ch) throws BurnedDnieCardException, ApduConnectionException, InvalidCardException {
        return getDnie(conn, pwc, cryptoHelper, ch, true);
    }

    public static DnieNfc getDnieNfc(ApduConnection conn, CryptoHelper cryptoHelper, CallbackHandler ch) throws BurnedDnieCardException, ApduConnectionException, InvalidCardException {
        if (conn == null) {
            throw new IllegalArgumentException("La conexion no puede ser nula");
        }
        if (cryptoHelper == null) {
            throw new IllegalArgumentException("El CryptoHelper no puede ser nulo");
        }
        if (ch == null) {
            throw new IllegalArgumentException("El CallbackHandler no puede ser nulo");
        }
        InvalidCardException invalidCardException = null;
        CardNotPresentException cardNotPresentException = null;
        long[] terminals = conn.getTerminals(false);
        if (terminals.length < 1) {
            throw new NoReadersFoundException();
        }
        for (long terminal : terminals) {
            conn.setTerminal((int) terminal);
            try {
                byte[] responseAtr = conn.reset();
                Atr actualAtr = new Atr(responseAtr, ATR_MASK);
                byte[] actualAtrBytes = actualAtr.getBytes();
                if (ATR_NFC.equals(actualAtr) || ATR_NFC2.equals(actualAtr)) {
                    try {
                        JmcLogger.info(DnieFactory.class.getName(), "getDnieNfc", "Detectado DNIe 3.0 o 4.0 por NFC: " + new DnieAtr(actualAtr));
                        return new DnieNfc(conn, null, cryptoHelper, ch);
                    } catch (InvalidCanOrMrzException e) {
                        throw new InvalidAccessCodeException("El CAN indicado del DNIe no es correcto", e);
                    } catch (IcaoException e2) {
                        throw new ApduConnectionException("No se ha podido abrir el canal PACE", e2);
                    }
                }
                if (actualAtrBytes[actualAtrBytes.length - 1] == -127 && actualAtrBytes[actualAtrBytes.length - 2] == 101) {
                    throw new BurnedDnieCardException(actualAtr);
                }
                invalidCardException = new InvalidCardException("DNIe por NFC", ATR, responseAtr);
            } catch (CardNotPresentException e3) {
                cardNotPresentException = e3;
            }
        }
        if (invalidCardException != null) {
            throw invalidCardException;
        }
        if (cardNotPresentException != null) {
            throw cardNotPresentException;
        }
        throw new ApduConnectionException("No se ha podido conectar con ningun lector de tarjetas NFC");
    }

    public static MrtdLds1 getEmrtdNfc(ApduConnection conn, CryptoHelper cryptoHelper, CallbackHandler ch) throws ApduConnectionException {
        if (conn == null) {
            throw new IllegalArgumentException("La conexion no puede ser nula");
        }
        CardNotPresentException cardNotPresentException = null;
        long[] terminals = conn.getTerminals(false);
        if (terminals.length < 1) {
            throw new NoReadersFoundException();
        }
        for (long terminal : terminals) {
            conn.setTerminal((int) terminal);
            try {
                byte[] responseAtr = conn.reset();
                Atr actualAtr = new Atr(responseAtr, ATR_MASK);
                if (ATR_NFC.equals(actualAtr) || ATR_NFC2.equals(actualAtr)) {
                    try {
                        JmcLogger.info(DnieFactory.class.getName(), "getEmrtdNfc", "Detectado eMRTD con ATR: " + new DnieAtr(actualAtr));
                        return new IcaoMrtdWithPace(conn, cryptoHelper, ch);
                    } catch (InvalidCanOrMrzException e) {
                        throw new InvalidAccessCodeException("El MRZ del documento de viaje no es correcto", e);
                    } catch (IcaoException e2) {
                        throw new ApduConnectionException("No se ha podido abrir el canal PACE", e2);
                    }
                }
            } catch (CardNotPresentException e3) {
                cardNotPresentException = e3;
            }
        }
        if (cardNotPresentException != null) {
            throw cardNotPresentException;
        }
        throw new ApduConnectionException("No se ha podido conectar con ningun lector de tarjetas NFC");
    }

    public static Dnie getDnie(ApduConnection conn, PasswordCallback pwc, CryptoHelper cryptoHelper, CallbackHandler ch, boolean includeCloneCards) throws BurnedDnieCardException, ApduConnectionException, InvalidCardException {
        String terminalName;
        if (conn == null) {
            throw new IllegalArgumentException("La conexion no puede ser nula");
        }
        if (cryptoHelper == null) {
            throw new IllegalArgumentException("El CryptoHelper no puede ser nulo");
        }
        InvalidCardException invalidCardException = null;
        CardNotPresentException cardNotPresentException = null;
        long[] terminals = conn.getTerminals(false);
        if (terminals.length < 1) {
            throw new NoReadersFoundException();
        }
        for (long terminal : terminals) {
            try {
                terminalName = conn.getTerminalInfo((int) terminal);
            } catch (ApduConnectionException e) {
                terminalName = null;
            }
            System.out.println("Buscando tarjeta compatible en el terminal " + terminal + " (" + terminalName + ")");
            conn.setTerminal((int) terminal);
            try {
                byte[] responseAtr = conn.reset();
                Atr actualAtr = new Atr(responseAtr, ATR_MASK);
                byte[] actualAtrBytes = actualAtr.getBytes();
                if (ATR_NFC.equals(actualAtr) || ATR_NFC2.equals(actualAtr)) {
                    try {
                        JmcLogger.info(DnieFactory.class.getName(), "getDnie", "Detectado DNIe 3.0 o 4.0: " + new DnieAtr(actualAtr));
                        return new DnieNfc(conn, pwc, cryptoHelper, ch);
                    } catch (InvalidCanOrMrzException e2) {
                        throw new InvalidAccessCodeException("El CAN indicado del DNIe no es correcto", e2);
                    } catch (IcaoException e3) {
                        throw new ApduConnectionException("No se ha podido abrir el canal PACE", e3);
                    }
                }
                if (ATR.equals(actualAtr)) {
                    if (actualAtrBytes[15] == 4) {
                        JmcLogger.info(DnieFactory.class.getName(), "getDnie", "Detectado DNIe 3.0 o 4.0 con ATR " + new DnieAtr(actualAtr).toString());
                        return new Dnie3(conn, pwc, cryptoHelper, ch);
                    }
                    JmcLogger.info(DnieFactory.class.getName(), "getDnie", "Detectado DNIe 2.0");
                    return new Dnie(conn, pwc, cryptoHelper, ch);
                }
                if (includeCloneCards) {
                    if (ATR_TIF.equals(actualAtr)) {
                        JmcLogger.info(DnieFactory.class.getName(), "getDnie", "Detectada TIF");
                        return new Tif(conn, pwc, cryptoHelper, ch);
                    }
                    if (CeresSc.ATR_TC.equals(actualAtr)) {
                        JmcLogger.info(DnieFactory.class.getName(), "getDnie", "Detectada tarjeta FNMT CERES 4.30 o superior");
                        return new CeresSc(conn, pwc, cryptoHelper, ch);
                    }
                }
                if (actualAtrBytes[actualAtrBytes.length - 1] == -127 && actualAtrBytes[actualAtrBytes.length - 2] == 101) {
                    throw new BurnedDnieCardException(actualAtr);
                }
                invalidCardException = new InvalidCardException("DNIe", ATR, responseAtr);
            } catch (CardNotPresentException e4) {
                cardNotPresentException = e4;
            }
        }
        if (invalidCardException != null) {
            throw invalidCardException;
        }
        if (cardNotPresentException != null) {
            throw cardNotPresentException;
        }
        throw new ApduConnectionException("No se ha podido conectar con ningun lector de tarjetas");
    }

    public static Dnie3Cwa14890Constants getDnie3UsrCwa14890Constants(String idesp) {
        if (idesp == null || idesp.isEmpty()) {
            JmcLogger.warning("El IDESP proporcionado es nulo o vacio, se usaran las constantes CWA14890 de usuario para los DNIe 3 modernos");
            return new Dnie3r2UsrCwa14890Constants();
        }
        if (DNIE3_R2_IDESP.compareTo(idesp) > 0) {
            return new Dnie3UsrCwa14890Constants();
        }
        return new Dnie3r2UsrCwa14890Constants();
    }

    static Dnie3Cwa14890Constants getDnie3PinCwa14890Constants(String idesp) {
        if (idesp == null || idesp.isEmpty()) {
            JmcLogger.warning("El IDESP proporcionado era nulo o vacio, se usaran las constantes CWA14890 de PIN para los DNIe 3 modernos");
            return new Dnie3r2PinCwa14890Constants();
        }
        if (DNIE3_R2_IDESP.compareTo(idesp) > 0) {
            return new Dnie3PinCwa14890Constants();
        }
        return new Dnie3r2PinCwa14890Constants();
    }
}
