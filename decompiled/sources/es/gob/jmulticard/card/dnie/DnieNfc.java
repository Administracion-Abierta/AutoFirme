package es.gob.jmulticard.card.dnie;

import es.gob.jmulticard.CryptoHelper;
import es.gob.jmulticard.DigestAlgorithm;
import es.gob.jmulticard.JmcLogger;
import es.gob.jmulticard.apdu.iso7816four.pace.MseSetPaceAlgorithmApduCommand;
import es.gob.jmulticard.asn1.icao.CardAccess;
import es.gob.jmulticard.callback.CustomTextInputCallback;
import es.gob.jmulticard.card.CardMessages;
import es.gob.jmulticard.card.CryptoCardException;
import es.gob.jmulticard.card.PinException;
import es.gob.jmulticard.card.PrivateKeyReference;
import es.gob.jmulticard.card.icao.IcaoException;
import es.gob.jmulticard.card.icao.InvalidCanOrMrzException;
import es.gob.jmulticard.card.icao.WirelessInitializer;
import es.gob.jmulticard.card.icao.WirelessInitializerCan;
import es.gob.jmulticard.card.icao.WirelessInitializerMrz;
import es.gob.jmulticard.connection.ApduConnection;
import es.gob.jmulticard.connection.ApduConnectionException;
import es.gob.jmulticard.connection.cwa14890.Cwa14890Connection;
import es.gob.jmulticard.connection.pace.PaceConnection;
import es.gob.jmulticard.connection.pace.PaceException;
import es.gob.jmulticard.connection.pace.SecureMessaging;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/dnie/DnieNfc.class */
public class DnieNfc extends Dnie3 {
    private static MseSetPaceAlgorithmApduCommand.PacePasswordType paceInitType;
    private static String paceInitValue;

    public DnieNfc(ApduConnection conn, PasswordCallback pwc, CryptoHelper cryptoHlpr, CallbackHandler ch) throws IcaoException, ApduConnectionException {
        super(getPaceConnection(conn, ch, cryptoHlpr), pwc, cryptoHlpr, ch);
    }

    @Override // es.gob.jmulticard.card.dnie.Dnie3, es.gob.jmulticard.card.dnie.Dnie, es.gob.jmulticard.card.AbstractSmartCard
    public String getCardName() {
        return "DNIe 3.0/4.0 accedido de forma inalambrica";
    }

    private static ApduConnection getPaceConnection(ApduConnection con, CallbackHandler ch, CryptoHelper cryptoHelper) throws ApduConnectionException, IcaoException {
        Callback textInputCallback;
        Method m;
        WirelessInitializer paceInitializer;
        String prompt = CardMessages.getString("DnieNFC.0");
        try {
            textInputCallback = (Callback) Class.forName("javax.security.auth.callback.TextInputCallback").getConstructor(String.class).newInstance(prompt);
            m = textInputCallback.getClass().getMethod("getText", new Class[0]);
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            JmcLogger.info(DnieNfc.class.getName(), "getPaceConnection", "No se ha encontrado la clase 'javax.security.auth.callback.TextInputCallback', se usara 'es.gob.jmulticard.callback.CustomTextInputCallback': " + e);
            textInputCallback = new CustomTextInputCallback(prompt);
            m = null;
        }
        paceInitValue = null;
        paceInitType = null;
        try {
            ch.handle(new Callback[]{textInputCallback});
            if (textInputCallback instanceof CustomTextInputCallback) {
                paceInitValue = ((CustomTextInputCallback) textInputCallback).getText();
            } else if (m != null) {
                try {
                    Object o = m.invoke(textInputCallback, new Object[0]);
                    if (!(o instanceof String)) {
                        throw new IllegalStateException("El TextInputCallback ha devuelto un dato de tipo " + (o == null ? "null" : o.getClass().getName()));
                    }
                    paceInitValue = (String) o;
                } catch (IllegalAccessException | IllegalArgumentException | SecurityException | InvocationTargetException e2) {
                    throw new IllegalStateException("El TextInputCallback no tiene un metodo 'getText'", e2);
                }
            } else {
                throw new IllegalStateException("El TextInputCallback no tiene un metodo 'getText'");
            }
            paceInitType = getPasswordType(paceInitValue);
            if (paceInitValue == null || paceInitValue.isEmpty()) {
                throw new InvalidCanOrMrzException("El CAN/MRZ no puede ser nulo ni vacio");
            }
            try {
                switch (paceInitType) {
                    case MRZ:
                        paceInitializer = WirelessInitializerMrz.deriveMrz(paceInitValue, cryptoHelper);
                        break;
                    case CAN:
                        paceInitializer = new WirelessInitializerCan(paceInitValue);
                        break;
                    default:
                        throw new UnsupportedOperationException("Tipo de inicializador PACE no soportado: " + paceInitType);
                }
                CryptoHelper.PaceChannelHelper channelHelper = cryptoHelper.getPaceChannelHelper(new CardAccess(CardAccess.PaceAlgorithm.PACE_ECDH_GM_AES_CBC_CMAC_128, CardAccess.PaceAlgorithmParam.BRAINPOOL_256_R1, DigestAlgorithm.SHA1), null);
                SecureMessaging sm = channelHelper.openPaceChannel((byte) 0, paceInitializer, con);
                return new PaceConnection(con, cryptoHelper, sm);
            } catch (InvalidCanOrMrzException e3) {
                paceInitValue = null;
                paceInitType = null;
                throw e3;
            }
        } catch (Exception e4) {
            throw new PaceException("Error obteniendo el CAN", e4);
        }
    }

    private ApduConnection getPaceConnection(ApduConnection con, CryptoHelper.PaceChannelHelper pch) throws ApduConnectionException, IcaoException {
        WirelessInitializer paceInitializer;
        switch (paceInitType) {
            case MRZ:
                paceInitializer = WirelessInitializerMrz.deriveMrz(paceInitValue, getCryptoHelper());
                break;
            case CAN:
                paceInitializer = new WirelessInitializerCan(paceInitValue);
                break;
            default:
                throw new UnsupportedOperationException("No se soporta el codigo de inicializacion de PACE: " + paceInitType);
        }
        SecureMessaging sm = pch.openPaceChannel((byte) 0, paceInitializer, con);
        return new PaceConnection(con, getCryptoHelper(), sm);
    }

    @Override // es.gob.jmulticard.card.dnie.Dnie3, es.gob.jmulticard.card.dnie.Dnie
    public void openSecureChannelIfNotAlreadyOpened() throws CryptoCardException, PinException {
        openSecureChannelIfNotAlreadyOpened(true);
    }

    @Override // es.gob.jmulticard.card.dnie.Dnie3, es.gob.jmulticard.card.dnie.Dnie
    public void openSecureChannelIfNotAlreadyOpened(boolean doChv) throws CryptoCardException, PinException {
        if (!(getConnection() instanceof Cwa14890Connection)) {
            try {
                this.rawConnection = getPaceConnection(getConnection(), getCryptoHelper().getPaceChannelHelper(new CardAccess(CardAccess.PaceAlgorithm.PACE_ECDH_GM_AES_CBC_CMAC_128, CardAccess.PaceAlgorithmParam.BRAINPOOL_256_R1, DigestAlgorithm.SHA1), null));
                try {
                    setConnection(this.rawConnection);
                } catch (ApduConnectionException e) {
                    throw new CryptoCardException("Error al abrir el canal PACE", e);
                }
            } catch (IcaoException e2) {
                throw new CryptoCardException("Error en el establecimiento del canal PACE", e2);
            } catch (ApduConnectionException e3) {
                throw new CryptoCardException("Error en la transmision de la APDU", e3);
            }
        }
        super.openSecureChannelIfNotAlreadyOpened(doChv);
    }

    @Override // es.gob.jmulticard.card.dnie.Dnie, es.gob.jmulticard.card.CryptoCard
    public byte[] sign(byte[] data, String signAlgorithm, PrivateKeyReference privateKeyReference) throws CryptoCardException, PinException {
        byte[] ret = signInternal(data, signAlgorithm, privateKeyReference);
        try {
            setConnection(getConnection().getSubConnection());
            resetCard();
        } catch (ApduConnectionException e) {
            JmcLogger.warning("Fallo en el reinicio del canal seguro: " + e);
        }
        return ret;
    }

    private void resetCard() {
        try {
            selectMasterFile();
        } catch (Exception e1) {
            JmcLogger.info(DnieNfc.class.getName(), "resetCard", "Paso de canal seguro a no seguro al reiniciar: " + e1);
        }
    }

    private static MseSetPaceAlgorithmApduCommand.PacePasswordType getPasswordType(String paceInitializationValue) {
        if (isNumeric(paceInitializationValue) && paceInitializationValue.length() <= 6) {
            return MseSetPaceAlgorithmApduCommand.PacePasswordType.CAN;
        }
        return MseSetPaceAlgorithmApduCommand.PacePasswordType.MRZ;
    }

    private static boolean isNumeric(CharSequence cs) {
        if (cs == null || cs.length() == 0) {
            return false;
        }
        int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
