package es.gob.jmulticard.card.dnie;

import es.gob.jmulticard.CryptoHelper;
import es.gob.jmulticard.DigestAlgorithm;
import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.JmcLogger;
import es.gob.jmulticard.asn1.Asn1Exception;
import es.gob.jmulticard.asn1.TlvException;
import es.gob.jmulticard.asn1.icao.AdditionalPersonalDetails;
import es.gob.jmulticard.asn1.icao.Com;
import es.gob.jmulticard.asn1.icao.DataGroupHash;
import es.gob.jmulticard.asn1.icao.LdsSecurityObject;
import es.gob.jmulticard.asn1.icao.OptionalDetails;
import es.gob.jmulticard.asn1.icao.SecurityOptions;
import es.gob.jmulticard.asn1.icao.Sod;
import es.gob.jmulticard.asn1.icao.SubjectFacePhoto;
import es.gob.jmulticard.asn1.icao.SubjectSignaturePhoto;
import es.gob.jmulticard.card.CardSecurityException;
import es.gob.jmulticard.card.CryptoCardException;
import es.gob.jmulticard.card.CryptoCardSecurityException;
import es.gob.jmulticard.card.PasswordCallbackNotFoundException;
import es.gob.jmulticard.card.PinException;
import es.gob.jmulticard.card.icao.InvalidSecurityObjectException;
import es.gob.jmulticard.card.icao.MrtdLds1;
import es.gob.jmulticard.card.icao.Mrz;
import es.gob.jmulticard.card.iso7816four.FileNotFoundException;
import es.gob.jmulticard.card.iso7816four.Iso7816FourCardException;
import es.gob.jmulticard.card.iso7816four.RequiredSecurityStateNotSatisfiedException;
import es.gob.jmulticard.connection.ApduConnection;
import es.gob.jmulticard.connection.ApduConnectionException;
import es.gob.jmulticard.connection.cwa14890.ChannelType;
import es.gob.jmulticard.connection.cwa14890.Cwa14890OneV2Connection;
import java.io.IOException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/dnie/Dnie3.class */
public class Dnie3 extends Dnie implements MrtdLds1 {
    private String idesp;

    protected Dnie3(ApduConnection conn, PasswordCallback pwc, CryptoHelper cryptoHlpr, CallbackHandler ch) throws ApduConnectionException {
        super(conn, pwc, cryptoHlpr, ch);
        this.idesp = null;
        this.rawConnection = conn;
        try {
            this.idesp = getIdesp();
        } catch (Exception e1) {
            JmcLogger.warning("No se ha podido leer el IDESP del DNIe: " + e1);
            this.idesp = null;
        }
    }

    @Override // es.gob.jmulticard.card.dnie.Dnie, es.gob.jmulticard.card.AbstractSmartCard
    public String getCardName() {
        return "DNIe 3.0";
    }

    @Override // es.gob.jmulticard.card.dnie.Dnie
    public int getPinRetriesLeft() throws ApduConnectionException {
        int retriesLeft;
        if (!(getConnection() instanceof Cwa14890OneV2Connection)) {
            throw new ApduConnectionException("Es necesario abrir canal CWA para obtener los intentos de PIN restantes");
        }
        ChannelType channelType = ((Cwa14890OneV2Connection) getConnection()).getChannelType();
        switch (channelType) {
            case CWA_USER:
                try {
                    openPinChannel();
                    retriesLeft = super.getPinRetriesLeft();
                } catch (CryptoCardException e) {
                    throw new ApduConnectionException("Error estableciendo el canal de PIN para obtener los intentos restantes", e);
                }
                break;
            case CWA_PIN:
                retriesLeft = super.getPinRetriesLeft();
                break;
            default:
                throw new ApduConnectionException("Es necesario abrir canal CWA para obtener los intentos de PIN restantes");
        }
        return retriesLeft;
    }

    @Override // es.gob.jmulticard.card.dnie.Dnie
    public void openSecureChannelIfNotAlreadyOpened() throws CryptoCardException, PinException {
        openSecureChannelIfNotAlreadyOpened(true);
    }

    @Override // es.gob.jmulticard.card.dnie.Dnie
    public void openSecureChannelIfNotAlreadyOpened(boolean doChv) throws CryptoCardException, PinException {
        if (isSecurityChannelOpen()) {
            return;
        }
        JmcLogger.info(Dnie3.class.getName(), "openSecureChannelIfNotAlreadyOpened", "Conexion actual: " + getConnection());
        JmcLogger.info(Dnie3.class.getName(), "openSecureChannelIfNotAlreadyOpened", "Conexion subyacente: " + (this.rawConnection != null ? this.rawConnection : "ninguna"));
        if (!getConnection().isOpen()) {
            try {
                setConnection(this.rawConnection);
            } catch (ApduConnectionException e) {
                throw new CryptoCardException("Error en el establecimiento del canal inicial previo al seguro de PIN", e);
            }
        }
        if (doChv) {
            openPinChannel();
            try {
                verifyPin(getInternalPasswordCallback());
            } catch (PasswordCallbackNotFoundException e2) {
                JmcLogger.info(Dnie3.class.getName(), "openSecureChannelIfNotAlreadyOpened", "No se proporcionaron medios para verificar el PIN: " + e2);
            } catch (ApduConnectionException e3) {
                throw new CryptoCardException("Error en la verificacion de PIN", e3);
            }
            JmcLogger.info(Dnie3.class.getName(), "openSecureChannelIfNotAlreadyOpened", "PIN verificado correctamente");
        }
        openUserChannel();
    }

    public final ApduConnection openPinChannel() throws CryptoCardException {
        ApduConnection pinSecureConnection = new Cwa14890OneV2Connection(this, getConnection(), getCryptoHelper(), DnieFactory.getDnie3PinCwa14890Constants(this.idesp), DnieFactory.getDnie3PinCwa14890Constants(this.idesp), ChannelType.CWA_PIN);
        try {
            selectMasterFile();
        } catch (Exception e) {
            JmcLogger.warning("Error seleccionando el MF tras el establecimiento del canal seguro de PIN: " + e);
        }
        try {
            setConnection(pinSecureConnection);
            JmcLogger.info(Dnie3.class.getName(), "openPinChannel", "Canal seguro de PIN para DNIe establecido");
            return pinSecureConnection;
        } catch (ApduConnectionException e2) {
            throw new CryptoCardException("Error en el establecimiento del canal seguro de PIN", e2);
        }
    }

    public final ApduConnection openUserChannel() throws CryptoCardException {
        ApduConnection usrSecureConnection = new Cwa14890OneV2Connection(this, getConnection(), getCryptoHelper(), DnieFactory.getDnie3UsrCwa14890Constants(this.idesp), DnieFactory.getDnie3UsrCwa14890Constants(this.idesp), ChannelType.CWA_USER);
        try {
            selectMasterFile();
            try {
                setConnection(usrSecureConnection);
                JmcLogger.info(Dnie3.class.getName(), "openUserChannel", "Canal seguro de Usuario para DNIe establecido");
                return usrSecureConnection;
            } catch (ApduConnectionException e) {
                throw new CryptoCardException("Error en el establecimiento del canal seguro de usuario", e);
            }
        } catch (Exception e2) {
            throw new CryptoCardException("Error seleccionando el MF tras el establecimiento del canal seguro de usuario", e2);
        }
    }

    @Override // es.gob.jmulticard.card.dnie.Dnie
    protected boolean needsPinForLoadingCerts() {
        return false;
    }

    /* JADX WARN: Code duplicated, block: B:38:0x0180 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:40:0x01be A[SYNTHETIC] */
    @Override // es.gob.jmulticard.card.icao.MrtdLds1
    public final X509Certificate[] checkSecurityObjects() throws TlvException, SignatureException, IOException, CertificateException, Asn1Exception {
        byte[] dgBytes;
        byte[] actualHash;
        openSecureChannelIfNotAlreadyOpened(false);
        Sod sod = getSod();
        sod.validateSignature();
        LdsSecurityObject ldsSecurityObject = sod.getLdsSecurityObject();
        openSecureChannelIfNotAlreadyOpened(false);
        for (DataGroupHash dgh : ldsSecurityObject.getDataGroupHashes()) {
            switch (dgh.getDataGroupNumber()) {
                case 1:
                    dgBytes = getDg1().getBytes();
                    actualHash = getCryptoHelper().digest(DigestAlgorithm.getDigestAlgorithm(ldsSecurityObject.getDigestAlgorithm()), dgBytes);
                    if (!Arrays.equals(actualHash, dgh.getDataGroupHashValue())) {
                        throw new InvalidSecurityObjectException("El DG" + dgh.getDataGroupNumber() + " no concuerda con la huella del SOD, se esperaba " + HexUtils.hexify(actualHash, false) + " y se ha encontrado " + HexUtils.hexify(dgh.getDataGroupHashValue(), false));
                    }
                    break;
                case 2:
                    dgBytes = getDg2().getBytes();
                    actualHash = getCryptoHelper().digest(DigestAlgorithm.getDigestAlgorithm(ldsSecurityObject.getDigestAlgorithm()), dgBytes);
                    if (!Arrays.equals(actualHash, dgh.getDataGroupHashValue())) {
                        throw new InvalidSecurityObjectException("El DG" + dgh.getDataGroupNumber() + " no concuerda con la huella del SOD, se esperaba " + HexUtils.hexify(actualHash, false) + " y se ha encontrado " + HexUtils.hexify(dgh.getDataGroupHashValue(), false));
                    }
                    break;
                case 3:
                    try {
                        dgBytes = getDg3();
                        actualHash = getCryptoHelper().digest(DigestAlgorithm.getDigestAlgorithm(ldsSecurityObject.getDigestAlgorithm()), dgBytes);
                        if (!Arrays.equals(actualHash, dgh.getDataGroupHashValue())) {
                            throw new InvalidSecurityObjectException("El DG" + dgh.getDataGroupNumber() + " no concuerda con la huella del SOD, se esperaba " + HexUtils.hexify(actualHash, false) + " y se ha encontrado " + HexUtils.hexify(dgh.getDataGroupHashValue(), false));
                        }
                    } catch (CardSecurityException e) {
                        JmcLogger.warning("Se omite la comprobacion del DG3 con el SOD por no poder leerse: " + e);
                    }
                    break;
                case 4:
                    dgBytes = getDg4();
                    actualHash = getCryptoHelper().digest(DigestAlgorithm.getDigestAlgorithm(ldsSecurityObject.getDigestAlgorithm()), dgBytes);
                    if (!Arrays.equals(actualHash, dgh.getDataGroupHashValue())) {
                        throw new InvalidSecurityObjectException("El DG" + dgh.getDataGroupNumber() + " no concuerda con la huella del SOD, se esperaba " + HexUtils.hexify(actualHash, false) + " y se ha encontrado " + HexUtils.hexify(dgh.getDataGroupHashValue(), false));
                    }
                    break;
                case 5:
                    dgBytes = getDg5();
                    actualHash = getCryptoHelper().digest(DigestAlgorithm.getDigestAlgorithm(ldsSecurityObject.getDigestAlgorithm()), dgBytes);
                    if (!Arrays.equals(actualHash, dgh.getDataGroupHashValue())) {
                        throw new InvalidSecurityObjectException("El DG" + dgh.getDataGroupNumber() + " no concuerda con la huella del SOD, se esperaba " + HexUtils.hexify(actualHash, false) + " y se ha encontrado " + HexUtils.hexify(dgh.getDataGroupHashValue(), false));
                    }
                    break;
                case 6:
                    dgBytes = getDg6();
                    actualHash = getCryptoHelper().digest(DigestAlgorithm.getDigestAlgorithm(ldsSecurityObject.getDigestAlgorithm()), dgBytes);
                    if (!Arrays.equals(actualHash, dgh.getDataGroupHashValue())) {
                        throw new InvalidSecurityObjectException("El DG" + dgh.getDataGroupNumber() + " no concuerda con la huella del SOD, se esperaba " + HexUtils.hexify(actualHash, false) + " y se ha encontrado " + HexUtils.hexify(dgh.getDataGroupHashValue(), false));
                    }
                    break;
                case 7:
                    dgBytes = getDg7().getBytes();
                    actualHash = getCryptoHelper().digest(DigestAlgorithm.getDigestAlgorithm(ldsSecurityObject.getDigestAlgorithm()), dgBytes);
                    if (!Arrays.equals(actualHash, dgh.getDataGroupHashValue())) {
                        throw new InvalidSecurityObjectException("El DG" + dgh.getDataGroupNumber() + " no concuerda con la huella del SOD, se esperaba " + HexUtils.hexify(actualHash, false) + " y se ha encontrado " + HexUtils.hexify(dgh.getDataGroupHashValue(), false));
                    }
                    break;
                case 8:
                    dgBytes = getDg8();
                    actualHash = getCryptoHelper().digest(DigestAlgorithm.getDigestAlgorithm(ldsSecurityObject.getDigestAlgorithm()), dgBytes);
                    if (!Arrays.equals(actualHash, dgh.getDataGroupHashValue())) {
                        throw new InvalidSecurityObjectException("El DG" + dgh.getDataGroupNumber() + " no concuerda con la huella del SOD, se esperaba " + HexUtils.hexify(actualHash, false) + " y se ha encontrado " + HexUtils.hexify(dgh.getDataGroupHashValue(), false));
                    }
                    break;
                case 9:
                    dgBytes = getDg9();
                    actualHash = getCryptoHelper().digest(DigestAlgorithm.getDigestAlgorithm(ldsSecurityObject.getDigestAlgorithm()), dgBytes);
                    if (!Arrays.equals(actualHash, dgh.getDataGroupHashValue())) {
                        throw new InvalidSecurityObjectException("El DG" + dgh.getDataGroupNumber() + " no concuerda con la huella del SOD, se esperaba " + HexUtils.hexify(actualHash, false) + " y se ha encontrado " + HexUtils.hexify(dgh.getDataGroupHashValue(), false));
                    }
                    break;
                case 10:
                    dgBytes = getDg10();
                    actualHash = getCryptoHelper().digest(DigestAlgorithm.getDigestAlgorithm(ldsSecurityObject.getDigestAlgorithm()), dgBytes);
                    if (!Arrays.equals(actualHash, dgh.getDataGroupHashValue())) {
                        throw new InvalidSecurityObjectException("El DG" + dgh.getDataGroupNumber() + " no concuerda con la huella del SOD, se esperaba " + HexUtils.hexify(actualHash, false) + " y se ha encontrado " + HexUtils.hexify(dgh.getDataGroupHashValue(), false));
                    }
                    break;
                case 11:
                    dgBytes = getDg11().getBytes();
                    actualHash = getCryptoHelper().digest(DigestAlgorithm.getDigestAlgorithm(ldsSecurityObject.getDigestAlgorithm()), dgBytes);
                    if (!Arrays.equals(actualHash, dgh.getDataGroupHashValue())) {
                        throw new InvalidSecurityObjectException("El DG" + dgh.getDataGroupNumber() + " no concuerda con la huella del SOD, se esperaba " + HexUtils.hexify(actualHash, false) + " y se ha encontrado " + HexUtils.hexify(dgh.getDataGroupHashValue(), false));
                    }
                    break;
                case 12:
                    dgBytes = getDg12();
                    actualHash = getCryptoHelper().digest(DigestAlgorithm.getDigestAlgorithm(ldsSecurityObject.getDigestAlgorithm()), dgBytes);
                    if (!Arrays.equals(actualHash, dgh.getDataGroupHashValue())) {
                        throw new InvalidSecurityObjectException("El DG" + dgh.getDataGroupNumber() + " no concuerda con la huella del SOD, se esperaba " + HexUtils.hexify(actualHash, false) + " y se ha encontrado " + HexUtils.hexify(dgh.getDataGroupHashValue(), false));
                    }
                    break;
                case 13:
                    dgBytes = getDg13().getBytes();
                    actualHash = getCryptoHelper().digest(DigestAlgorithm.getDigestAlgorithm(ldsSecurityObject.getDigestAlgorithm()), dgBytes);
                    if (!Arrays.equals(actualHash, dgh.getDataGroupHashValue())) {
                        throw new InvalidSecurityObjectException("El DG" + dgh.getDataGroupNumber() + " no concuerda con la huella del SOD, se esperaba " + HexUtils.hexify(actualHash, false) + " y se ha encontrado " + HexUtils.hexify(dgh.getDataGroupHashValue(), false));
                    }
                    break;
                case 14:
                    dgBytes = getDg14().getBytes();
                    actualHash = getCryptoHelper().digest(DigestAlgorithm.getDigestAlgorithm(ldsSecurityObject.getDigestAlgorithm()), dgBytes);
                    if (!Arrays.equals(actualHash, dgh.getDataGroupHashValue())) {
                        throw new InvalidSecurityObjectException("El DG" + dgh.getDataGroupNumber() + " no concuerda con la huella del SOD, se esperaba " + HexUtils.hexify(actualHash, false) + " y se ha encontrado " + HexUtils.hexify(dgh.getDataGroupHashValue(), false));
                    }
                    break;
                case 15:
                    dgBytes = getDg15();
                    actualHash = getCryptoHelper().digest(DigestAlgorithm.getDigestAlgorithm(ldsSecurityObject.getDigestAlgorithm()), dgBytes);
                    if (!Arrays.equals(actualHash, dgh.getDataGroupHashValue())) {
                        throw new InvalidSecurityObjectException("El DG" + dgh.getDataGroupNumber() + " no concuerda con la huella del SOD, se esperaba " + HexUtils.hexify(actualHash, false) + " y se ha encontrado " + HexUtils.hexify(dgh.getDataGroupHashValue(), false));
                    }
                    break;
                case 16:
                    dgBytes = getDg16();
                    actualHash = getCryptoHelper().digest(DigestAlgorithm.getDigestAlgorithm(ldsSecurityObject.getDigestAlgorithm()), dgBytes);
                    if (!Arrays.equals(actualHash, dgh.getDataGroupHashValue())) {
                        throw new InvalidSecurityObjectException("El DG" + dgh.getDataGroupNumber() + " no concuerda con la huella del SOD, se esperaba " + HexUtils.hexify(actualHash, false) + " y se ha encontrado " + HexUtils.hexify(dgh.getDataGroupHashValue(), false));
                    }
                    break;
                default:
                    throw new InvalidSecurityObjectException("El SOD define huella para un DG inexistente: " + dgh.getDataGroupNumber());
            }
        }
        return sod.getCertificateChain();
    }

    @Override // es.gob.jmulticard.card.icao.MrtdLds1
    public final byte[] getCardAccess() throws IOException {
        try {
            return selectFileByLocationAndRead(FILE_CARD_ACCESS_LOCATION);
        } catch (FileNotFoundException e) {
            throw ((IOException) new java.io.FileNotFoundException("CardAcess no encontrado").initCause(e));
        } catch (Iso7816FourCardException e2) {
            throw new CryptoCardException("Error leyendo el CardAccess", e2);
        }
    }

    @Override // es.gob.jmulticard.card.icao.MrtdLds1
    public final byte[] getAtrInfo() throws IOException {
        try {
            return selectFileByLocationAndRead(FILE_ATR_INFO_LOCATION);
        } catch (FileNotFoundException e) {
            throw ((IOException) new java.io.FileNotFoundException("ATR/INFO no encontrado").initCause(e));
        } catch (Iso7816FourCardException e2) {
            throw new CryptoCardException("Error leyendo el ATR/INFO", e2);
        }
    }

    @Override // es.gob.jmulticard.card.icao.MrtdLds1
    public final Mrz getDg1() throws IOException {
        try {
            return new Mrz(selectFileByLocationAndRead(FILE_DG01_LOCATION));
        } catch (FileNotFoundException e) {
            throw ((IOException) new java.io.FileNotFoundException("DG1 no encontrado").initCause(e));
        } catch (Iso7816FourCardException e2) {
            throw new CryptoCardException("Error leyendo el DG1", e2);
        }
    }

    @Override // es.gob.jmulticard.card.icao.MrtdLds1
    public final SubjectFacePhoto getDg2() throws IOException {
        SubjectFacePhoto ret = new SubjectFacePhoto();
        try {
            ret.setDerValue(selectFileByLocationAndRead(FILE_DG02_LOCATION));
            return ret;
        } catch (Asn1Exception | TlvException | Iso7816FourCardException e) {
            throw new CryptoCardException("Error leyendo el DG2", e);
        } catch (FileNotFoundException e2) {
            throw ((IOException) new java.io.FileNotFoundException("DG2 no encontrado").initCause(e2));
        }
    }

    @Override // es.gob.jmulticard.card.icao.MrtdLds1
    public byte[] getDg3() throws IOException {
        try {
            return selectFileByLocationAndRead(FILE_DG03_LOCATION);
        } catch (FileNotFoundException e) {
            throw ((IOException) new java.io.FileNotFoundException("DG3 no encontrado").initCause(e));
        } catch (RequiredSecurityStateNotSatisfiedException e2) {
            throw new CardSecurityException("No se tienen permisos para leer el DG3", e2);
        } catch (Iso7816FourCardException e3) {
            throw new CryptoCardException("Error leyendo el DG3", e3);
        }
    }

    @Override // es.gob.jmulticard.card.icao.MrtdLds1
    public final SubjectSignaturePhoto getDg7() throws IOException {
        SubjectSignaturePhoto ret = new SubjectSignaturePhoto();
        try {
            ret.setDerValue(selectFileByLocationAndRead(FILE_DG07_LOCATION));
            return ret;
        } catch (Asn1Exception | TlvException | Iso7816FourCardException e) {
            throw new CryptoCardException("Error leyendo el DG7", e);
        } catch (FileNotFoundException e2) {
            throw ((IOException) new java.io.FileNotFoundException("DG7 no encontrado").initCause(e2));
        }
    }

    @Override // es.gob.jmulticard.card.icao.MrtdLds1
    public final AdditionalPersonalDetails getDg11() throws IOException {
        try {
            AdditionalPersonalDetails personalDetails = new AdditionalPersonalDetails();
            personalDetails.setDerValue(selectFileByLocationAndRead(FILE_DG11_LOCATION));
            return personalDetails;
        } catch (Asn1Exception | TlvException | Iso7816FourCardException e) {
            throw new CryptoCardException("Error leyendo el DG11", e);
        } catch (FileNotFoundException e2) {
            throw ((IOException) new java.io.FileNotFoundException("DG11 no encontrado").initCause(e2));
        }
    }

    @Override // es.gob.jmulticard.card.icao.MrtdLds1
    public final byte[] getDg12() throws IOException {
        try {
            return selectFileByLocationAndRead(FILE_DG12_LOCATION);
        } catch (FileNotFoundException e) {
            throw ((IOException) new java.io.FileNotFoundException("DG12 no encontrado").initCause(e));
        } catch (Iso7816FourCardException e2) {
            throw new CryptoCardException("Error leyendo el DG12", e2);
        }
    }

    @Override // es.gob.jmulticard.card.icao.MrtdLds1
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

    @Override // es.gob.jmulticard.card.icao.MrtdLds1
    public final SecurityOptions getDg14() throws IOException {
        try {
            SecurityOptions securityOptions = new SecurityOptions();
            securityOptions.setDerValue(selectFileByLocationAndRead(FILE_DG14_LOCATION));
            return securityOptions;
        } catch (Asn1Exception | TlvException | Iso7816FourCardException e) {
            throw new CryptoCardException("Error leyendo el DG14", e);
        } catch (FileNotFoundException e2) {
            throw ((IOException) new java.io.FileNotFoundException("DG14 no encontrado").initCause(e2));
        }
    }

    @Override // es.gob.jmulticard.card.icao.MrtdLds1
    public final Sod getSod() throws IOException {
        Sod sod = new Sod(getCryptoHelper());
        try {
            sod.setDerValue(selectFileByLocationAndRead(FILE_SOD_LOCATION));
            return sod;
        } catch (Asn1Exception | TlvException | Iso7816FourCardException e) {
            throw new IOException("No se puede crear un SOD a partir del contenido del fichero", e);
        }
    }

    @Override // es.gob.jmulticard.card.icao.MrtdLds1
    public final Com getCom() throws IOException {
        try {
            Com com2 = new Com();
            com2.setDerValue(selectFileByLocationAndRead(FILE_COM_LOCATION));
            return com2;
        } catch (Asn1Exception | TlvException | Iso7816FourCardException e) {
            throw new CryptoCardException("Error leyendo el 'Common Data' (COM)", e);
        } catch (FileNotFoundException e2) {
            throw ((IOException) new java.io.FileNotFoundException("COM no encontrado").initCause(e2));
        }
    }

    @Override // es.gob.jmulticard.card.icao.MrtdLds1
    public byte[] getCardSecurity() throws IOException {
        throw new UnsupportedOperationException("Este MRTD no tiene CardSecurity");
    }

    @Override // es.gob.jmulticard.card.icao.MrtdLds1
    public byte[] getDg4() throws IOException {
        throw new CryptoCardSecurityException("Hace falta canal de administrador para leer el DG4");
    }

    @Override // es.gob.jmulticard.card.icao.MrtdLds1
    public byte[] getDg5() throws IOException {
        throw new UnsupportedOperationException("Este MRTD no tiene DG5");
    }

    @Override // es.gob.jmulticard.card.icao.MrtdLds1
    public byte[] getDg6() throws IOException {
        throw new UnsupportedOperationException("Este MRTD no tiene DG6");
    }

    @Override // es.gob.jmulticard.card.icao.MrtdLds1
    public byte[] getDg8() throws IOException {
        throw new UnsupportedOperationException("Este MRTD no tiene DG8");
    }

    @Override // es.gob.jmulticard.card.icao.MrtdLds1
    public byte[] getDg9() throws IOException {
        throw new UnsupportedOperationException("Este MRTD no tiene DG9");
    }

    @Override // es.gob.jmulticard.card.icao.MrtdLds1
    public byte[] getDg10() throws IOException {
        throw new UnsupportedOperationException("Este MRTD no tiene DG10");
    }

    @Override // es.gob.jmulticard.card.icao.MrtdLds1
    public byte[] getDg15() throws IOException {
        throw new UnsupportedOperationException("Este MRTD no tiene DG15");
    }

    @Override // es.gob.jmulticard.card.icao.MrtdLds1
    public byte[] getDg16() throws IOException {
        throw new UnsupportedOperationException("Este MRTD no tiene DG16");
    }

    @Override // es.gob.jmulticard.card.icao.MrtdLds1
    public final byte[] getDir() throws IOException {
        throw new UnsupportedOperationException("Este MRTD no tiene DIR");
    }
}
