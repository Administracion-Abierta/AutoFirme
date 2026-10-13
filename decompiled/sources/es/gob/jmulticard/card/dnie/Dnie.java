package es.gob.jmulticard.card.dnie;

import es.gob.jmulticard.CryptoHelper;
import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.JmcLogger;
import es.gob.jmulticard.apdu.CommandApdu;
import es.gob.jmulticard.apdu.ResponseApdu;
import es.gob.jmulticard.apdu.StatusWord;
import es.gob.jmulticard.apdu.dnie.ChangePinApduCommand;
import es.gob.jmulticard.apdu.dnie.GetChipInfoApduCommand;
import es.gob.jmulticard.apdu.dnie.LoadDataApduCommand;
import es.gob.jmulticard.apdu.dnie.RetriesLeftApduCommand;
import es.gob.jmulticard.apdu.dnie.SignDataApduCommand;
import es.gob.jmulticard.apdu.dnie.VerifyApduCommand;
import es.gob.jmulticard.apdu.iso7816eight.PsoSignHashApduCommand;
import es.gob.jmulticard.apdu.iso7816four.ExternalAuthenticateApduCommand;
import es.gob.jmulticard.apdu.iso7816four.InternalAuthenticateApduCommand;
import es.gob.jmulticard.apdu.iso7816four.MseSetAuthenticationKeyApduCommand;
import es.gob.jmulticard.apdu.iso7816four.MseSetComputationApduCommand;
import es.gob.jmulticard.asn1.custom.fnmt.ceressc.CeresScPrKdf;
import es.gob.jmulticard.asn1.der.pkcs1.DigestInfo;
import es.gob.jmulticard.asn1.der.pkcs15.Cdf;
import es.gob.jmulticard.asn1.der.pkcs15.Pkcs15Cdf;
import es.gob.jmulticard.asn1.der.pkcs15.PrKdf;
import es.gob.jmulticard.card.AuthenticationModeLockedException;
import es.gob.jmulticard.card.BadPinException;
import es.gob.jmulticard.card.CardMessages;
import es.gob.jmulticard.card.CompressionUtils;
import es.gob.jmulticard.card.CryptoCardException;
import es.gob.jmulticard.card.Location;
import es.gob.jmulticard.card.PasswordCallbackNotFoundException;
import es.gob.jmulticard.card.PinException;
import es.gob.jmulticard.card.PrivateKeyReference;
import es.gob.jmulticard.card.cwa14890.Cwa14890Card;
import es.gob.jmulticard.card.cwa14890.Cwa14890PrivateConstants;
import es.gob.jmulticard.card.cwa14890.Cwa14890PublicConstants;
import es.gob.jmulticard.card.iso7816eight.AbstractIso7816EightCard;
import es.gob.jmulticard.card.iso7816four.Iso7816FourCardException;
import es.gob.jmulticard.connection.ApduConnection;
import es.gob.jmulticard.connection.ApduConnectionException;
import es.gob.jmulticard.connection.LostChannelException;
import es.gob.jmulticard.connection.cwa14890.Cwa14890Connection;
import es.gob.jmulticard.connection.cwa14890.Cwa14890OneV1Connection;
import es.gob.jmulticard.connection.cwa14890.SecureChannelException;
import es.gob.jmulticard.connection.pace.PaceConnection;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/dnie/Dnie.class */
public class Dnie extends AbstractIso7816EightCard implements Dni, Cwa14890Card {
    private static final byte ERROR_PIN_SW1 = 99;
    private static final String MASTER_FILE_NAME = "Master.File";
    private final Set<String> aliases;
    private final Map<String, X509Certificate> certs;
    private final Map<String, String> aliasByCertAndKeyId;
    private final Map<String, DniePrivateKeyReference> keyReferences;
    private final CryptoHelper cryptoHelper;
    protected ApduConnection rawConnection;
    private PasswordCallback passwordCallback;
    private CallbackHandler callbackHandler;
    private static final byte[] CERT_ICC_FILE_ID = {96, 31};
    private static final Location CDF_LOCATION = new Location("50156004");
    private static final Location PRKDF_LOCATION = new Location("50156001");
    private static final Location IDESP_LOCATION = new Location("3F000006");

    protected Dnie(ApduConnection conn, PasswordCallback pwc, CryptoHelper cryptoHlpr, CallbackHandler ch) throws ApduConnectionException {
        super((byte) 0, conn);
        this.aliases = new HashSet();
        this.certs = new LinkedHashMap();
        this.aliasByCertAndKeyId = new LinkedHashMap();
        this.keyReferences = new LinkedHashMap();
        conn.reset();
        connect(conn);
        this.rawConnection = conn;
        this.callbackHandler = ch;
        this.passwordCallback = pwc;
        if (cryptoHlpr == null) {
            throw new IllegalArgumentException("El CryptoHelper no puede ser nulo");
        }
        this.cryptoHelper = cryptoHlpr;
        loadCertificates();
    }

    protected final CryptoHelper getCryptoHelper() {
        return this.cryptoHelper;
    }

    protected final PasswordCallback getPasswordCallback() {
        return this.passwordCallback;
    }

    protected Cwa14890PublicConstants getCwa14890PublicConstants() {
        return new DnieCwa14890Constants();
    }

    protected Cwa14890PrivateConstants getCwa14890PrivateConstants() {
        return new DnieCwa14890Constants();
    }

    protected final byte[] signInternal(byte[] data, String signAlgorithm, PrivateKeyReference privateKeyReference) throws CryptoCardException, PinException {
        if (!(privateKeyReference instanceof DniePrivateKeyReference)) {
            throw new IllegalArgumentException("La referencia a la clave privada tiene que ser de tipo DniePrivateKeyReference");
        }
        return signOperation(data, signAlgorithm, privateKeyReference);
    }

    private byte[] signOperation(byte[] data, String signAlgorithm, PrivateKeyReference privateKeyReference) throws CryptoCardException, PinException {
        openSecureChannelIfNotAlreadyOpened();
        try {
            CommandApdu apdu = new MseSetComputationApduCommand((byte) 0, ((DniePrivateKeyReference) privateKeyReference).getKeyPath().getLastFilePath(), null);
            ResponseApdu res = getConnection().transmit(apdu);
            if (!res.isOk()) {
                throw new DnieCardException("Error en el establecimiento de las clave de firma con respuesta: " + res.getStatusWord(), res.getStatusWord());
            }
            JmcLogger.info(Dnie.class.getName(), "signOperation", "Establecidas las claves de firma");
            try {
                byte[] digestInfo = DigestInfo.encode(signAlgorithm, data, this.cryptoHelper);
                CommandApdu apdu2 = new PsoSignHashApduCommand((byte) 0, digestInfo);
                ResponseApdu res2 = getConnection().transmit(apdu2);
                if (!res2.isOk()) {
                    JmcLogger.severe("Recibida APDU inesperada de respuesta al PSOSignHash:\n" + HexUtils.hexify(res2.getBytes(), true));
                    throw new DnieCardException("Error durante la operacion de firma con respuesta: " + res2.getStatusWord(), res2.getStatusWord());
                }
                JmcLogger.info(Dnie.class.getName(), "signOperation", "Realizada correctamente la firma electronica");
                return res2.getData();
            } catch (IOException e) {
                throw new DnieCardException("Error en el calculo de la huella para firmar", e);
            }
        } catch (LostChannelException e2) {
            try {
                getConnection().close();
                if (getConnection() instanceof Cwa14890Connection) {
                    setConnection(getConnection().getSubConnection());
                }
                return signOperation(data, signAlgorithm, privateKeyReference);
            } catch (ApduConnectionException ex) {
                throw new DnieCardException("No se pudo recuperar el canal seguro para firmar (" + e2 + ")", ex);
            }
        } catch (ApduConnectionException e3) {
            throw new DnieCardException("Error en la transmision de comandos para firma a la tarjeta", e3);
        }
    }

    protected PasswordCallback getInternalPasswordCallback() throws PinException {
        try {
            int retriesLeft = getPinRetriesLeft();
            if (retriesLeft == 0) {
                throw new AuthenticationModeLockedException();
            }
            if (this.passwordCallback != null) {
                return this.passwordCallback;
            }
            if (this.callbackHandler != null) {
                PasswordCallback pwc = new PasswordCallback(getPinMessage(retriesLeft), false);
                try {
                    this.callbackHandler.handle(new Callback[]{pwc});
                    if (pwc.getPassword() == null || pwc.getPassword().length < 1) {
                        throw new BadPinException("El PIN no puede ser nulo ni vacio");
                    }
                    return pwc;
                } catch (IOException | UnsupportedCallbackException e) {
                    throw new PasswordCallbackNotFoundException("El CallbackHandler no ha permitido pedir el PIN al usuario", e);
                }
            }
            throw new PasswordCallbackNotFoundException("No hay ningun metodo para obtener el PIN");
        } catch (ApduConnectionException e2) {
            throw new PinException("No se ha podido obtener el numero de intentos restantes de PIN", e2);
        }
    }

    protected String getPinMessage(int retriesLeft) {
        return CardMessages.getString("Dnie.0", Integer.toString(retriesLeft));
    }

    protected final boolean isSecurityChannelOpen() {
        return (getConnection() instanceof Cwa14890Connection) && getConnection().isOpen() && !(getConnection() instanceof PaceConnection);
    }

    protected boolean needsPinForLoadingCerts() {
        return true;
    }

    @Override // es.gob.jmulticard.card.iso7816four.AbstractIso7816FourCard
    protected final void selectMasterFile() throws Iso7816FourCardException, ApduConnectionException {
        selectFileByName(MASTER_FILE_NAME);
    }

    public String toString() {
        return getCardName();
    }

    @Override // es.gob.jmulticard.card.cwa14890.Cwa14890Card
    public final byte[] getSerialNumber() throws ApduConnectionException {
        ResponseApdu response = getConnection().transmit(new GetChipInfoApduCommand());
        if (response.isOk()) {
            return response.getData();
        }
        throw new ApduConnectionException("Respuesta invalida en la obtencion del numero de serie con el codigo: " + response.getStatusWord());
    }

    @Override // es.gob.jmulticard.card.AbstractSmartCard
    public String getCardName() {
        return "DNIe";
    }

    @Override // es.gob.jmulticard.card.CryptoCard
    public final String[] getAliases() {
        return (String[]) this.aliases.toArray(new String[0]);
    }

    @Override // es.gob.jmulticard.card.CryptoCard
    public final X509Certificate getCertificate(String alias) {
        return this.certs.get(alias);
    }

    @Override // es.gob.jmulticard.card.cwa14890.Cwa14890Card
    public final void verifyIcc() {
    }

    @Override // es.gob.jmulticard.card.cwa14890.Cwa14890Card
    public final RSAPublicKey getIccCertPublicKey() throws IOException {
        try {
            selectMasterFile();
            byte[] iccCertEncoded = selectFileByIdAndRead(CERT_ICC_FILE_ID);
            try {
                X509Certificate iccCert = CryptoHelper.generateCertificate(iccCertEncoded);
                return this.cryptoHelper.getRsaPublicKey(iccCert);
            } catch (CertificateException e) {
                throw new IOException("No se pudo obtener el certificado de componente", e);
            }
        } catch (Iso7816FourCardException e2) {
            throw new IOException("Error en la seleccion del certificado de componente de la tarjeta", e2);
        } catch (ApduConnectionException e3) {
            throw new IOException("Error en el envio de APDU para la seleccion del certificado de componente de la tarjeta", e3);
        }
    }

    @Override // es.gob.jmulticard.card.cwa14890.Cwa14890Card
    public final void verifyIfdCertificateChain(Cwa14890PublicConstants consts) throws ApduConnectionException {
        try {
            setPublicKeyToVerification(consts.getRefCCvCaPublicKey());
            try {
                verifyCertificate(consts.getCCvCa());
                try {
                    setPublicKeyToVerification(consts.getChrCCvCa());
                    try {
                        verifyCertificate(consts.getCCvIfd());
                    } catch (SecureChannelException e) {
                        throw new SecureChannelException("Error en la verificacion del certificado de Terminal", e);
                    }
                } catch (SecureChannelException e2) {
                    throw new SecureChannelException("Error al establecer la clave publica del certificado de CA intermedia de Terminal para su verificacion en tarjeta", e2);
                }
            } catch (SecureChannelException e3) {
                throw new SecureChannelException("Error en la verificacion del certificado de la CA intermedia de Terminal", e3);
            }
        } catch (SecureChannelException e4) {
            throw new SecureChannelException("Error al seleccionar para verificacion la clave publica de la CA raiz de los certificados verificables por la tarjeta", e4);
        }
    }

    @Override // es.gob.jmulticard.card.cwa14890.Cwa14890Card
    public final byte[] getRefIccPrivateKey(Cwa14890PublicConstants consts) {
        return consts.getRefIccPrivateKey();
    }

    @Override // es.gob.jmulticard.card.cwa14890.Cwa14890Card
    public final byte[] getChrCCvIfd(Cwa14890PublicConstants consts) {
        return consts.getChrCCvIfd();
    }

    @Override // es.gob.jmulticard.card.cwa14890.Cwa14890Card
    public final RSAPrivateKey getIfdPrivateKey(Cwa14890PrivateConstants consts) {
        return consts.getIfdPrivateKey();
    }

    @Override // es.gob.jmulticard.card.cwa14890.Cwa14890Card
    public final void setKeysToAuthentication(byte[] refPublicKey, byte[] refPrivateKey) throws ApduConnectionException {
        CommandApdu apdu = new MseSetAuthenticationKeyApduCommand((byte) 0, refPublicKey, refPrivateKey);
        ResponseApdu res = getConnection().transmit(apdu);
        if (!res.isOk()) {
            throw new SecureChannelException("Error durante el establecimiento de las claves publica y privada para autenticacion (error: " + res.getStatusWord() + ")");
        }
    }

    @Override // es.gob.jmulticard.card.cwa14890.Cwa14890Card
    public byte[] getInternalAuthenticateMessage(byte[] randomIfd, byte[] chrCCvIfd) throws ApduConnectionException {
        CommandApdu apdu = new InternalAuthenticateApduCommand((byte) 0, randomIfd, chrCCvIfd);
        ResponseApdu res = getConnection().transmit(apdu);
        if (res.isOk()) {
            return res.getData();
        }
        throw new ApduConnectionException("Respuesta invalida en la obtencion del mensaje de autenticacion interna con el codigo: " + res.getStatusWord());
    }

    @Override // es.gob.jmulticard.card.cwa14890.Cwa14890Card
    public final boolean externalAuthentication(byte[] extAuthenticationData) throws ApduConnectionException {
        CommandApdu apdu = new ExternalAuthenticateApduCommand((byte) 0, extAuthenticationData);
        return getConnection().transmit(apdu).isOk();
    }

    @Override // es.gob.jmulticard.card.CryptoCard
    public final PrivateKeyReference getPrivateKey(String alias) {
        return this.keyReferences.get(alias);
    }

    @Override // es.gob.jmulticard.card.CryptoCard
    public byte[] sign(byte[] data, String signAlgorithm, PrivateKeyReference privateKeyReference) throws CryptoCardException, PinException {
        byte[] signBytes = signInternal(data, signAlgorithm, privateKeyReference);
        try {
            getConnection().close();
        } catch (ApduConnectionException e) {
            JmcLogger.severe("No se ha podido cerrar el canal despues de una firma, es posible que fallen operaciones: " + e);
        }
        return signBytes;
    }

    @Override // es.gob.jmulticard.card.iso7816four.AbstractIso7816FourCard
    public final void verifyPin(PasswordCallback psc) throws PinException, ApduConnectionException {
        if (psc == null) {
            throw new IllegalArgumentException("No se puede verificar el titular con un PasswordCallback nulo");
        }
        ResponseApdu verifyResponse = getConnection().transmit(new VerifyApduCommand((byte) 0, psc));
        if (!verifyResponse.isOk()) {
            if (verifyResponse.getStatusWord().getMsb() == 99) {
                throw new BadPinException(verifyResponse.getStatusWord().getLsb() - (-64));
            }
            if (verifyResponse.getStatusWord().getMsb() == 105 && verifyResponse.getStatusWord().getLsb() == -125) {
                throw new AuthenticationModeLockedException();
            }
            if (verifyResponse.getStatusWord().getMsb() == 0 && verifyResponse.getStatusWord().getLsb() == 0) {
                throw new ApduConnectionException("Se ha perdido el canal NFC");
            }
            throw new ApduConnectionException(new Iso7816FourCardException("Error en la verificacion de PIN (" + verifyResponse.getStatusWord() + ")", verifyResponse.getStatusWord()));
        }
    }

    @Override // es.gob.jmulticard.card.cwa14890.Cwa14890Card
    public final int getIfdKeyLength(Cwa14890PublicConstants consts) {
        return consts.getIfdKeyLength();
    }

    public final byte[] cipherData(byte[] data, PrivateKeyReference privateKeyReference) throws CryptoCardException, PinException, LostChannelException {
        openSecureChannelIfNotAlreadyOpened();
        try {
            CommandApdu apdu = new LoadDataApduCommand(data);
            ResponseApdu res = getConnection().transmit(apdu);
            if (!res.isOk()) {
                JmcLogger.severe("Recibida APDU inesperada de respuesta a la carga de datos para cifrado RSA:\n" + HexUtils.hexify(res.getBytes(), true));
                throw new DnieCardException("Error durante la operacion de carga de datos para cifrado RSA: " + res.getStatusWord(), res.getStatusWord());
            }
            CommandApdu apdu2 = new SignDataApduCommand(((DniePrivateKeyReference) privateKeyReference).getKeyReference(), ((DniePrivateKeyReference) privateKeyReference).getKeyBitSize());
            ResponseApdu res2 = getConnection().transmit(apdu2);
            if (!res2.isOk()) {
                JmcLogger.severe("Recibida APDU inesperada de respuesta al SignData:\n" + HexUtils.hexify(res2.getBytes(), true));
                throw new DnieCardException("Error durante la operacion de cifrado RSA con respuesta: " + res2.getStatusWord(), res2.getStatusWord());
            }
            return res2.getData();
        } catch (LostChannelException e) {
            throw e;
        } catch (ApduConnectionException e2) {
            throw new DnieCardException("Error en la transmision de comandos para cifrado a la tarjeta", e2);
        }
    }

    public void openSecureChannelIfNotAlreadyOpened() throws CryptoCardException, PinException {
        openSecureChannelIfNotAlreadyOpened(true);
    }

    public void openSecureChannelIfNotAlreadyOpened(boolean doChv) throws CryptoCardException, PinException {
        if (!isSecurityChannelOpen()) {
            if (!(getConnection() instanceof Cwa14890Connection)) {
                ApduConnection secureConnection = new Cwa14890OneV1Connection(this, getConnection(), this.cryptoHelper, getCwa14890PublicConstants(), getCwa14890PrivateConstants());
                try {
                    setConnection(secureConnection);
                } catch (ApduConnectionException e) {
                    throw new CryptoCardException("Error en el establecimiento del canal seguro", e);
                }
            }
            if (doChv) {
                try {
                    verifyPin(getInternalPasswordCallback());
                } catch (ApduConnectionException e2) {
                    throw new CryptoCardException("Error en la apertura del canal seguro", e2);
                }
            }
        }
    }

    public int getPinRetriesLeft() throws ApduConnectionException {
        CommandApdu verifyCommandApdu = new RetriesLeftApduCommand();
        ResponseApdu verifyResponse = getConnection().transmit(verifyCommandApdu);
        StatusWord resSw = verifyResponse.getStatusWord();
        if (resSw.getMsb() == 105 && resSw.getLsb() == -126) {
            throw new ApduConnectionException("Error obteniendo los intentos restantes de PIN: " + resSw);
        }
        return resSw.getLsb() - (-64);
    }

    public final byte[] changePIN(String oldPin, String newPin) throws CryptoCardException, PinException {
        openSecureChannelIfNotAlreadyOpened();
        try {
            selectMasterFile();
            byte[] pinFile = {0, 0};
            selectFileById(pinFile);
            CommandApdu apdu = new ChangePinApduCommand(oldPin.getBytes(), newPin.getBytes());
            ResponseApdu res = getConnection().transmit(apdu);
            if (!res.isOk()) {
                throw new DnieCardException("Error en el establecimiento de las variables de entorno para el cambio de PIN", res.getStatusWord());
            }
            return res.getData();
        } catch (Iso7816FourCardException e) {
            throw new DnieCardException("No se pudo seleccionar el fichero de PIN de la tarjeta", e);
        } catch (LostChannelException e2) {
            JmcLogger.warning("Se ha perdido el canal seguro para cambiar el PIN, se procede a recuperarlo: " + e2);
            try {
                getConnection().close();
                if (getConnection() instanceof Cwa14890Connection) {
                    setConnection(getConnection().getSubConnection());
                }
                return changePIN(oldPin, newPin);
            } catch (Exception ex) {
                throw new DnieCardException("No se pudo recuperar el canal seguro para firmar", ex);
            }
        } catch (ApduConnectionException e3) {
            throw new DnieCardException("Error en la transmision de comandos para cambio de PIN a la tarjeta", e3);
        }
    }

    public final void setCallbackHandler(CallbackHandler handler) {
        this.callbackHandler = handler;
    }

    public final void setPasswordCallback(PasswordCallback pwc) {
        this.passwordCallback = pwc;
    }

    public String getIdesp() throws Iso7816FourCardException, IOException {
        String idEsp = new String(selectFileByLocationAndRead(IDESP_LOCATION));
        return idEsp;
    }

    public static void connect(ApduConnection conn) throws ApduConnectionException {
        if (!conn.isOpen()) {
            conn.open();
        }
    }

    private void loadCertificates() throws ApduConnectionException {
        PrKdf prkdf;
        if (needsPinForLoadingCerts()) {
            try {
                openSecureChannelIfNotAlreadyOpened(true);
            } catch (CryptoCardException | PinException e) {
                throw new ApduConnectionException("Error en la verificacion de PIN para la carga de certificados", e);
            }
        }
        try {
            selectMasterFile();
            byte[] cdfBytes = selectFileByLocationAndRead(CDF_LOCATION);
            Pkcs15Cdf cdf = new Cdf();
            cdf.setDerValue(cdfBytes);
            JmcLogger.info(Dnie.class.getName(), "loadCertificates", "Ledido el CDF del DNIe: " + cdf.getCertificateCount() + " certificados");
            for (int i = 0; i < cdf.getCertificateCount(); i++) {
                Location loc = new Location(cdf.getCertificatePath(i).replace("\\", "").trim());
                X509Certificate cert = CompressionUtils.getCertificateFromCompressedOrNotData(selectFileByLocationAndRead(loc), this.cryptoHelper);
                String alias = cdf.getCertificateAlias(i);
                this.aliasByCertAndKeyId.put(HexUtils.hexify(cdf.getCertificateId(i), false), alias);
                this.certs.put(alias, cert);
                JmcLogger.info(Dnie.class.getName(), "loadCertificates", "Cargado certificado: " + alias);
            }
            byte[] prkdfBytes = selectFileByLocationAndRead(PRKDF_LOCATION);
            try {
                prkdf = new PrKdf();
                prkdf.setDerValue(prkdfBytes);
            } catch (Exception e2) {
                JmcLogger.warning("Detectado posible PrKDF con CommonPrivateKeyAttributes vacio, se prueba con estructura alternativa: " + e2);
                prkdf = new CeresScPrKdf();
                prkdf.setDerValue(prkdfBytes);
            }
            JmcLogger.info(Dnie.class.getName(), "loadCertificates", "Cargado PrKDF: " + prkdf.getKeyCount() + " claves privadas");
            for (int i2 = 0; i2 < prkdf.getKeyCount(); i2++) {
                String alias2 = this.aliasByCertAndKeyId.get(HexUtils.hexify(prkdf.getKeyId(i2), false));
                if (alias2 != null) {
                    this.keyReferences.put(alias2, new DniePrivateKeyReference(this, prkdf.getKeyIdentifier(i2), new Location(prkdf.getKeyPath(i2)), prkdf.getKeyName(i2), prkdf.getKeyReference(i2), this.cryptoHelper.getRsaPublicKey(this.certs.get(alias2)).getModulus().bitLength()));
                }
            }
            this.aliases.addAll(this.certs.keySet());
            hideCertsWithoutKey();
        } catch (Exception e3) {
            throw new ApduConnectionException("Error cargando las estructuras iniciales de la tarjeta", e3);
        }
    }

    private void hideCertsWithoutKey() {
        for (String alias : this.aliases) {
            if (this.keyReferences.get(alias) == null) {
                this.aliases.remove(alias);
            }
        }
    }
}
