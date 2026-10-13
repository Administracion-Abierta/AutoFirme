package es.gob.jmulticard.card.iso7816eight;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.apdu.CommandApdu;
import es.gob.jmulticard.apdu.ResponseApdu;
import es.gob.jmulticard.apdu.iso7816eight.PsoVerifyCertificateApduCommand;
import es.gob.jmulticard.card.iso7816four.AbstractIso7816FourCard;
import es.gob.jmulticard.connection.ApduConnection;
import es.gob.jmulticard.connection.ApduConnectionException;
import es.gob.jmulticard.connection.cwa14890.SecureChannelException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/iso7816eight/AbstractIso7816EightCard.class */
public abstract class AbstractIso7816EightCard extends AbstractIso7816FourCard {
    protected AbstractIso7816EightCard(byte c, ApduConnection conn) {
        super(c, conn);
    }

    public void verifyCertificate(byte[] cert) throws ApduConnectionException {
        CommandApdu apdu = new PsoVerifyCertificateApduCommand((byte) 0, cert);
        ResponseApdu res = getConnection().transmit(apdu);
        if (!res.isOk()) {
            throw new SecureChannelException("Error en la verificacion del certificado. Se obtuvo el error: " + HexUtils.hexify(res.getBytes(), true));
        }
    }
}
