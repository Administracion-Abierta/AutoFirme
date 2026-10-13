package es.gob.jmulticard.card.icao;

import es.gob.jmulticard.CryptoHelper;
import es.gob.jmulticard.JmcLogger;
import es.gob.jmulticard.asn1.Asn1Exception;
import es.gob.jmulticard.asn1.TlvException;
import es.gob.jmulticard.asn1.icao.OptionalDetails;
import es.gob.jmulticard.card.CryptoCardException;
import es.gob.jmulticard.card.CryptoCardSecurityException;
import es.gob.jmulticard.card.PrivateKeyReference;
import es.gob.jmulticard.card.dnie.DnieNfc;
import es.gob.jmulticard.card.iso7816four.FileNotFoundException;
import es.gob.jmulticard.card.iso7816four.Iso7816FourCardException;
import es.gob.jmulticard.card.iso7816four.RequiredSecurityStateNotSatisfiedException;
import es.gob.jmulticard.connection.ApduConnection;
import es.gob.jmulticard.connection.ApduConnectionException;
import java.io.IOException;
import javax.security.auth.callback.CallbackHandler;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/icao/IcaoMrtdWithPace.class */
public final class IcaoMrtdWithPace extends DnieNfc {
    public IcaoMrtdWithPace(ApduConnection conn, CryptoHelper cryptoHlpr, CallbackHandler ch) throws IcaoException, ApduConnectionException {
        super(conn, null, cryptoHlpr, ch);
    }

    @Override // es.gob.jmulticard.card.dnie.DnieNfc, es.gob.jmulticard.card.dnie.Dnie3, es.gob.jmulticard.card.dnie.Dnie, es.gob.jmulticard.card.AbstractSmartCard
    public String getCardName() {
        return "MRTD accedido de forma inalambrica mediante PACE";
    }

    @Override // es.gob.jmulticard.card.dnie.DnieNfc, es.gob.jmulticard.card.dnie.Dnie3, es.gob.jmulticard.card.dnie.Dnie
    public void openSecureChannelIfNotAlreadyOpened() {
        JmcLogger.warning("No se permite apertura de canal CWA-14890, se ignora la peticion");
    }

    @Override // es.gob.jmulticard.card.dnie.DnieNfc, es.gob.jmulticard.card.dnie.Dnie3, es.gob.jmulticard.card.dnie.Dnie
    public void openSecureChannelIfNotAlreadyOpened(boolean doChv) {
        JmcLogger.warning("No se permite apertura de canal CWA-14890, se ignora la peticion");
    }

    @Override // es.gob.jmulticard.card.dnie.DnieNfc, es.gob.jmulticard.card.dnie.Dnie, es.gob.jmulticard.card.CryptoCard
    public byte[] sign(byte[] data, String signAlgorithm, PrivateKeyReference privateKeyRef) {
        throw new UnsupportedOperationException("No se permite firmar con MRTD");
    }

    @Override // es.gob.jmulticard.card.dnie.Dnie
    public String toString() {
        return getCardName();
    }

    @Override // es.gob.jmulticard.card.dnie.Dnie
    public String getIdesp() {
        JmcLogger.info(IcaoMrtdWithPace.class.getName(), "getIdesp", "Este MRTD no tiene IDESP");
        return null;
    }

    @Override // es.gob.jmulticard.card.dnie.Dnie3, es.gob.jmulticard.card.icao.MrtdLds1
    public OptionalDetails getDg13() throws IOException {
        try {
            OptionalDetails ret = new OptionalDetails();
            ret.setDerValue(selectFileByLocationAndRead(FILE_DG13_LOCATION));
            return ret;
        } catch (Asn1Exception | TlvException | Iso7816FourCardException e) {
            throw new CryptoCardException("Error leyendo el DG13", e);
        } catch (FileNotFoundException e2) {
            throw ((IOException) new java.io.FileNotFoundException("DG13 no encontrado").initCause(e2));
        }
    }

    @Override // es.gob.jmulticard.card.dnie.Dnie3, es.gob.jmulticard.card.icao.MrtdLds1
    public byte[] getCardSecurity() throws IOException {
        try {
            return selectFileByLocationAndRead(FILE_CARD_SECURITY_LOCATION);
        } catch (FileNotFoundException e) {
            throw ((IOException) new java.io.FileNotFoundException("CardSecurity no encontrado").initCause(e));
        } catch (Iso7816FourCardException e2) {
            throw new CryptoCardException("Error leyendo el CardSecurity", e2);
        }
    }

    @Override // es.gob.jmulticard.card.dnie.Dnie3, es.gob.jmulticard.card.icao.MrtdLds1
    public byte[] getDg3() throws IOException {
        try {
            return selectFileByLocationAndRead(FILE_DG03_LOCATION);
        } catch (FileNotFoundException e) {
            throw ((IOException) new java.io.FileNotFoundException("DG3 no encontrado").initCause(e));
        } catch (Iso7816FourCardException e2) {
            throw new CryptoCardException("Error leyendo el DG3", e2);
        }
    }

    @Override // es.gob.jmulticard.card.dnie.Dnie3, es.gob.jmulticard.card.icao.MrtdLds1
    public byte[] getDg4() throws IOException {
        try {
            return selectFileByLocationAndRead(FILE_DG04_LOCATION);
        } catch (FileNotFoundException e) {
            throw ((IOException) new java.io.FileNotFoundException("DG4 no encontrado").initCause(e));
        } catch (RequiredSecurityStateNotSatisfiedException e2) {
            throw new CryptoCardSecurityException("Se necesita canal de adminstrador para leer el DG4", e2);
        } catch (Iso7816FourCardException e3) {
            throw new CryptoCardException("Error leyendo el DG4", e3);
        }
    }

    @Override // es.gob.jmulticard.card.dnie.Dnie3, es.gob.jmulticard.card.icao.MrtdLds1
    public byte[] getDg5() throws IOException {
        try {
            return selectFileByLocationAndRead(FILE_DG05_LOCATION);
        } catch (FileNotFoundException e) {
            throw ((IOException) new java.io.FileNotFoundException("DG5 no encontrado").initCause(e));
        } catch (Iso7816FourCardException e2) {
            throw new CryptoCardException("Error leyendo el DG5", e2);
        }
    }

    @Override // es.gob.jmulticard.card.dnie.Dnie3, es.gob.jmulticard.card.icao.MrtdLds1
    public byte[] getDg6() throws IOException {
        try {
            return selectFileByLocationAndRead(FILE_DG06_LOCATION);
        } catch (FileNotFoundException e) {
            throw ((IOException) new java.io.FileNotFoundException("DG6 no encontrado").initCause(e));
        } catch (Iso7816FourCardException e2) {
            throw new CryptoCardException("Error leyendo el DG6", e2);
        }
    }

    @Override // es.gob.jmulticard.card.dnie.Dnie3, es.gob.jmulticard.card.icao.MrtdLds1
    public byte[] getDg8() throws IOException {
        try {
            return selectFileByLocationAndRead(FILE_DG08_LOCATION);
        } catch (FileNotFoundException e) {
            throw ((IOException) new java.io.FileNotFoundException("DG8 no encontrado").initCause(e));
        } catch (Iso7816FourCardException e2) {
            throw new CryptoCardException("Error leyendo el DG8", e2);
        }
    }

    @Override // es.gob.jmulticard.card.dnie.Dnie3, es.gob.jmulticard.card.icao.MrtdLds1
    public byte[] getDg9() throws IOException {
        try {
            return selectFileByLocationAndRead(FILE_DG09_LOCATION);
        } catch (FileNotFoundException e) {
            throw ((IOException) new java.io.FileNotFoundException("DG9 no encontrado").initCause(e));
        } catch (Iso7816FourCardException e2) {
            throw new CryptoCardException("Error leyendo el DG9", e2);
        }
    }

    @Override // es.gob.jmulticard.card.dnie.Dnie3, es.gob.jmulticard.card.icao.MrtdLds1
    public byte[] getDg10() throws IOException {
        try {
            return selectFileByLocationAndRead(FILE_DG10_LOCATION);
        } catch (FileNotFoundException e) {
            throw ((IOException) new java.io.FileNotFoundException("DG10 no encontrado").initCause(e));
        } catch (Iso7816FourCardException e2) {
            throw new CryptoCardException("Error leyendo el DG10", e2);
        }
    }

    @Override // es.gob.jmulticard.card.dnie.Dnie3, es.gob.jmulticard.card.icao.MrtdLds1
    public byte[] getDg15() throws IOException {
        try {
            return selectFileByLocationAndRead(FILE_DG15_LOCATION);
        } catch (FileNotFoundException e) {
            throw ((IOException) new java.io.FileNotFoundException("DG15 no encontrado").initCause(e));
        } catch (Iso7816FourCardException e2) {
            throw new CryptoCardException("Error leyendo el DG15", e2);
        }
    }

    @Override // es.gob.jmulticard.card.dnie.Dnie3, es.gob.jmulticard.card.icao.MrtdLds1
    public byte[] getDg16() throws IOException {
        try {
            return selectFileByLocationAndRead(FILE_DG16_LOCATION);
        } catch (FileNotFoundException e) {
            throw ((IOException) new java.io.FileNotFoundException("DG16 no encontrado").initCause(e));
        } catch (Iso7816FourCardException e2) {
            throw new CryptoCardException("Error leyendo el DG16", e2);
        }
    }
}
