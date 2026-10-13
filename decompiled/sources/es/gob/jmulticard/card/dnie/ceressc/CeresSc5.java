package es.gob.jmulticard.card.dnie.ceressc;

import es.gob.jmulticard.CryptoHelper;
import es.gob.jmulticard.DigestAlgorithm;
import es.gob.jmulticard.apdu.iso7816four.pace.PaceChat;
import es.gob.jmulticard.asn1.icao.CardAccess;
import es.gob.jmulticard.card.CardMessages;
import es.gob.jmulticard.card.cwa14890.Cwa14890PrivateConstants;
import es.gob.jmulticard.card.cwa14890.Cwa14890PublicConstants;
import es.gob.jmulticard.card.dnie.Dnie;
import es.gob.jmulticard.card.icao.IcaoException;
import es.gob.jmulticard.card.icao.WirelessInitializer;
import es.gob.jmulticard.card.icao.WirelessInitializerPin;
import es.gob.jmulticard.connection.ApduConnection;
import es.gob.jmulticard.connection.ApduConnectionException;
import es.gob.jmulticard.connection.pace.PaceConnection;
import es.gob.jmulticard.connection.pace.PaceException;
import es.gob.jmulticard.connection.pace.SecureMessaging;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/dnie/ceressc/CeresSc5.class */
public final class CeresSc5 extends Dnie {
    public CeresSc5(ApduConnection conn, PasswordCallback pwc, CryptoHelper cryptoHlpr, CallbackHandler ch) throws ApduConnectionException, IcaoException {
        super(getPaceConnection(conn, ch, cryptoHlpr), pwc, cryptoHlpr, ch);
    }

    private static ApduConnection getPaceConnection(ApduConnection con, CallbackHandler ch, CryptoHelper cryptoHelper) throws ApduConnectionException, IcaoException {
        String prompt = CardMessages.getString("DnieNFC.0");
        PasswordCallback pinCallback = new PasswordCallback(prompt, false);
        try {
            ch.handle(new Callback[]{pinCallback});
            WirelessInitializer paceInitializer = new WirelessInitializerPin(pinCallback.getPassword());
            SecureMessaging sm = cryptoHelper.getPaceChannelHelper(new CardAccess(CardAccess.PaceAlgorithm.PACE_ECDH_GM_AES_CBC_CMAC_192, CardAccess.PaceAlgorithmParam.BRAINPOOL_256_R1, DigestAlgorithm.SHA256), new PaceChat(PaceChat.TerminalType.ST)).openPaceChannel((byte) 0, paceInitializer, con);
            return new PaceConnection(con, cryptoHelper, sm);
        } catch (Exception e) {
            throw new PaceException("Error obteniendo el PIN", e);
        }
    }

    @Override // es.gob.jmulticard.card.dnie.Dnie, es.gob.jmulticard.card.AbstractSmartCard
    public String getCardName() {
        return "FNMT TC CERES v5.x";
    }

    @Override // es.gob.jmulticard.card.dnie.Dnie
    protected Cwa14890PublicConstants getCwa14890PublicConstants() {
        throw new UnsupportedOperationException("Esta tarjeta no soporta CWA-14890");
    }

    @Override // es.gob.jmulticard.card.dnie.Dnie
    protected Cwa14890PrivateConstants getCwa14890PrivateConstants() {
        throw new UnsupportedOperationException("Esta tarjeta no soporta CWA-14890");
    }
}
