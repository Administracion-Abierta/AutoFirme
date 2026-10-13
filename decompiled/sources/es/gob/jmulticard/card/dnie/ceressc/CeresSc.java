package es.gob.jmulticard.card.dnie.ceressc;

import es.gob.jmulticard.CryptoHelper;
import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.JmcLogger;
import es.gob.jmulticard.card.Atr;
import es.gob.jmulticard.card.CardMessages;
import es.gob.jmulticard.card.CryptoCardException;
import es.gob.jmulticard.card.InvalidCardException;
import es.gob.jmulticard.card.PinException;
import es.gob.jmulticard.card.cwa14890.Cwa14890PrivateConstants;
import es.gob.jmulticard.card.cwa14890.Cwa14890PublicConstants;
import es.gob.jmulticard.card.dnie.Dnie;
import es.gob.jmulticard.connection.ApduConnection;
import es.gob.jmulticard.connection.ApduConnectionException;
import es.gob.jmulticard.connection.cwa14890.ChannelType;
import es.gob.jmulticard.connection.cwa14890.Cwa14890Connection;
import es.gob.jmulticard.connection.cwa14890.Cwa14890OneV2Connection;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/dnie/ceressc/CeresSc.class */
public final class CeresSc extends Dnie {
    private static final byte[] ATR_MASK_TC = {-1, -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, -1, -1, -1};
    public static final Atr ATR_TC = new Atr(new byte[]{59, 127, 0, 0, 0, 0, 106, 70, 78, 77, 84, 0, 0, 0, 0, 0, 0, 3, -112, 0}, ATR_MASK_TC);
    private static String cardVersion = null;

    public CeresSc(ApduConnection conn, PasswordCallback pwc, CryptoHelper cryptoHlpr, CallbackHandler ch) throws ApduConnectionException, InvalidCardException {
        super(conn, pwc, cryptoHlpr, ch);
        checkAtr(conn.reset());
    }

    @Override // es.gob.jmulticard.card.dnie.Dnie
    protected Cwa14890PublicConstants getCwa14890PublicConstants() {
        return new CeresScCwa14890Constants();
    }

    @Override // es.gob.jmulticard.card.dnie.Dnie
    protected boolean needsPinForLoadingCerts() {
        return false;
    }

    @Override // es.gob.jmulticard.card.dnie.Dnie
    protected Cwa14890PrivateConstants getCwa14890PrivateConstants() {
        return new CeresScCwa14890Constants();
    }

    @Override // es.gob.jmulticard.card.dnie.Dnie
    public void openSecureChannelIfNotAlreadyOpened() throws CryptoCardException, PinException {
        if (isSecurityChannelOpen()) {
            return;
        }
        JmcLogger.info(CeresSc.class.getName(), "openSecureChannelIfNotAlreadyOpened", "Conexion actual: " + getConnection());
        JmcLogger.info(CeresSc.class.getName(), "openSecureChannelIfNotAlreadyOpened", "Conexion subyacente: " + this.rawConnection);
        if (!getConnection().isOpen()) {
            try {
                setConnection(this.rawConnection);
            } catch (ApduConnectionException e) {
                throw new CryptoCardException("Error en el establecimiento del canal inicial", e);
            }
        }
        if (!(getConnection() instanceof Cwa14890Connection)) {
            ApduConnection secureConnection = new Cwa14890OneV2Connection(this, getConnection(), getCryptoHelper(), getCwa14890PublicConstants(), getCwa14890PrivateConstants(), ChannelType.CWA_USER);
            try {
                selectMasterFile();
            } catch (Exception e2) {
                JmcLogger.warning("Error seleccionando el MF tras el establecimiento del canal seguro de PIN: " + e2);
            }
            try {
                setConnection(secureConnection);
            } catch (ApduConnectionException e3) {
                throw new CryptoCardException("Error en el establecimiento del canal seguro", e3);
            }
        }
        try {
            verifyPin(getInternalPasswordCallback());
        } catch (ApduConnectionException e4) {
            throw new CryptoCardException("Error en la apertura del canal seguro", e4);
        }
    }

    @Override // es.gob.jmulticard.card.dnie.Dnie
    protected String getPinMessage(int retriesLeft) {
        return CardMessages.getString("Gen.0", Integer.toString(retriesLeft));
    }

    private static void checkAtr(byte[] atrBytes) throws InvalidCardException {
        Atr tmpAtr = new Atr(atrBytes, ATR_MASK_TC);
        if (ATR_TC.equals(tmpAtr) && atrBytes[15] >= 4 && atrBytes[16] >= 48) {
            cardVersion = HexUtils.hexify(new byte[]{atrBytes[15]}, false) + "." + HexUtils.hexify(new byte[]{atrBytes[16]}, false);
            JmcLogger.info(CeresSc.class.getName(), "checkAtr", "Encontrada TC CERES en version " + cardVersion);
            return;
        }
        throw new InvalidCardException("CERES", ATR_TC, atrBytes);
    }

    @Override // es.gob.jmulticard.card.dnie.Dnie
    public String toString() {
        return "Tarjeta FNMT CERES" + (cardVersion != null ? " version " + cardVersion : "");
    }
}
