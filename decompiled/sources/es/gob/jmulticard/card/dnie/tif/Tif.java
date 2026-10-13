package es.gob.jmulticard.card.dnie.tif;

import es.gob.jmulticard.CryptoHelper;
import es.gob.jmulticard.card.cwa14890.Cwa14890PrivateConstants;
import es.gob.jmulticard.card.cwa14890.Cwa14890PublicConstants;
import es.gob.jmulticard.card.dnie.Dnie;
import es.gob.jmulticard.connection.ApduConnection;
import es.gob.jmulticard.connection.ApduConnectionException;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/dnie/tif/Tif.class */
public final class Tif extends Dnie {
    public Tif(ApduConnection conn, PasswordCallback pwc, CryptoHelper cryptoHlpr, CallbackHandler ch) throws ApduConnectionException {
        super(conn, pwc, cryptoHlpr, ch);
    }

    @Override // es.gob.jmulticard.card.dnie.Dnie
    protected Cwa14890PublicConstants getCwa14890PublicConstants() {
        return new TifCwa14890Constants();
    }

    @Override // es.gob.jmulticard.card.dnie.Dnie
    protected Cwa14890PrivateConstants getCwa14890PrivateConstants() {
        return new TifCwa14890Constants();
    }

    @Override // es.gob.jmulticard.card.dnie.Dnie, es.gob.jmulticard.card.AbstractSmartCard
    public String getCardName() {
        return "TIF (Tarjeta de Identificacion del Funcionario de la DGP)";
    }
}
