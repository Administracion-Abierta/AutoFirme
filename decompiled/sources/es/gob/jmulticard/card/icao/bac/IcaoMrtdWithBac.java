package es.gob.jmulticard.card.icao.bac;

import es.gob.jmulticard.CryptoHelper;
import es.gob.jmulticard.card.PrivateKeyReference;
import es.gob.jmulticard.card.dnie.Dnie3;
import es.gob.jmulticard.connection.ApduConnection;
import es.gob.jmulticard.connection.ApduConnectionException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/icao/bac/IcaoMrtdWithBac.class */
public final class IcaoMrtdWithBac extends Dnie3 {
    public IcaoMrtdWithBac(ApduConnection conn, CryptoHelper cryptoHlpr) throws ApduConnectionException {
        super(conn, null, cryptoHlpr, null);
    }

    @Override // es.gob.jmulticard.card.dnie.Dnie3, es.gob.jmulticard.card.dnie.Dnie, es.gob.jmulticard.card.AbstractSmartCard
    public String getCardName() {
        return "MRTD accedido de forma inalambrica mediante BAC";
    }

    @Override // es.gob.jmulticard.card.dnie.Dnie3, es.gob.jmulticard.card.dnie.Dnie
    public void openSecureChannelIfNotAlreadyOpened() {
        throw new UnsupportedOperationException("No se permite apertura de canal CWA-14890");
    }

    @Override // es.gob.jmulticard.card.dnie.Dnie
    public String toString() {
        return getCardName();
    }

    @Override // es.gob.jmulticard.card.dnie.Dnie, es.gob.jmulticard.card.CryptoCard
    public byte[] sign(byte[] data, String signAlgorithm, PrivateKeyReference privateKeyReference) {
        throw new UnsupportedOperationException("No se permite firmar con un MRTD");
    }
}
