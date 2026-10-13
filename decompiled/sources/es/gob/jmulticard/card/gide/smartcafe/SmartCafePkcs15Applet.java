package es.gob.jmulticard.card.gide.smartcafe;

import es.gob.jmulticard.CryptoHelper;
import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.JmcLogger;
import es.gob.jmulticard.apdu.CommandApdu;
import es.gob.jmulticard.apdu.ResponseApdu;
import es.gob.jmulticard.apdu.StatusWord;
import es.gob.jmulticard.apdu.gide.GiDeRetriesLeftApduCommand;
import es.gob.jmulticard.apdu.gide.GiDeVerifyApduCommand;
import es.gob.jmulticard.apdu.iso7816eight.PsoSignHashApduCommand;
import es.gob.jmulticard.apdu.iso7816four.MseSetComputationApduCommand;
import es.gob.jmulticard.apdu.iso7816four.SelectFileApduResponse;
import es.gob.jmulticard.apdu.iso7816four.SelectFileByIdApduCommand;
import es.gob.jmulticard.asn1.Asn1Exception;
import es.gob.jmulticard.asn1.TlvException;
import es.gob.jmulticard.asn1.der.pkcs1.DigestInfo;
import es.gob.jmulticard.asn1.der.pkcs15.Cdf;
import es.gob.jmulticard.asn1.der.pkcs15.Odf;
import es.gob.jmulticard.asn1.der.pkcs15.Path;
import es.gob.jmulticard.card.Atr;
import es.gob.jmulticard.card.AuthenticationModeLockedException;
import es.gob.jmulticard.card.BadPinException;
import es.gob.jmulticard.card.CardMessages;
import es.gob.jmulticard.card.CryptoCard;
import es.gob.jmulticard.card.CryptoCardException;
import es.gob.jmulticard.card.InvalidCardException;
import es.gob.jmulticard.card.Location;
import es.gob.jmulticard.card.PasswordCallbackNotFoundException;
import es.gob.jmulticard.card.PinException;
import es.gob.jmulticard.card.PrivateKeyReference;
import es.gob.jmulticard.card.iso7816four.AbstractIso7816FourCard;
import es.gob.jmulticard.card.iso7816four.FileNotFoundException;
import es.gob.jmulticard.card.iso7816four.Iso7816FourCardException;
import es.gob.jmulticard.connection.ApduConnection;
import es.gob.jmulticard.connection.ApduConnectionException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/gide/smartcafe/SmartCafePkcs15Applet.class */
public final class SmartCafePkcs15Applet extends AbstractIso7816FourCard implements CryptoCard {
    private static final byte CLA = 0;
    private static final byte ERROR_PIN_SW1 = 99;
    private PasswordCallback passwordCallback;
    private CallbackHandler callbackHandler;
    private boolean authenticated;
    private final CryptoHelper cryptoHelper;
    private static final byte[] ATR_MASK = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 15};
    private static final Atr ATR = new Atr(new byte[]{59, -9, 24, 0, 0, -128, 49, -2, 69, 115, 102, 116, 101, 45, 110, 102, -60}, ATR_MASK);
    private static final byte[] ATR_MASK_MSC = {-1, -1, -1, -1, -1};
    private static final Atr ATR_MSC = new Atr(new byte[]{59, -128, -128, 1, 1}, ATR_MASK_MSC);
    private static final byte[] ATR_MASK_TCL = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 15};
    private static final Atr ATR_TCL = new Atr(new byte[]{59, -9, 24, 0, 0, -128, 49, -2, 69, 115, 102, 116, 101, 45, 110, 102, -60}, ATR_MASK_TCL);
    private static final byte[] PKCS15_NAME = {-96, 0, 0, 0, 99, 80, 75, 67, 83, 45, 49, 53};
    private static final byte[] ODF_PATH = {80, 49};
    private static final byte[] MF_PATH = {63, 0};
    private static final Map<String, X509Certificate> CERTS_BY_ALIAS = new ConcurrentHashMap();
    private static final Map<String, Integer> KEYNO_BY_ALIAS = new ConcurrentHashMap();

    public SmartCafePkcs15Applet(ApduConnection conn, CryptoHelper cryptoHlpr) throws IOException {
        super((byte) 0, conn);
        this.passwordCallback = null;
        this.callbackHandler = null;
        this.authenticated = false;
        if (cryptoHlpr == null) {
            throw new IllegalArgumentException("El CryptoHelper no puede ser nulo");
        }
        this.cryptoHelper = cryptoHlpr;
        conn.reset();
        connect(conn);
        try {
            selectFileByName(PKCS15_NAME);
            try {
                preloadCertificates();
                int keyCount = getKeyCount(sendArbitraryApdu(new CommandApdu(new byte[]{0, -54, 1, 2, 6})));
                JmcLogger.info(SmartCafePkcs15Applet.class.getName(), "constructor", "Se ha" + (keyCount > 1 ? OperatorName.ENDPATH : "") + " encontrado " + keyCount + " clave" + (keyCount > 1 ? OperatorName.CLOSE_AND_STROKE : "") + " y " + CERTS_BY_ALIAS.size() + " certificado" + (CERTS_BY_ALIAS.size() > 1 ? OperatorName.CLOSE_AND_STROKE : "") + " en la tarjeta");
                for (int i = 0; i < keyCount; i++) {
                    ResponseApdu res = sendArbitraryApdu(new CommandApdu(new byte[]{-128, 58, (byte) i, 1, 0}));
                    if (!res.isOk()) {
                        JmcLogger.severe("Error obteniendo el modulo de la clave " + i + ": " + res);
                    } else {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        baos.write(0);
                        baos.write(res.getData());
                        BigInteger modulus = new BigInteger(baos.toByteArray());
                        storeKeyOrdinal(i, modulus);
                    }
                }
                Set<String> aliases = CERTS_BY_ALIAS.keySet();
                for (String alias : aliases) {
                    if (!KEYNO_BY_ALIAS.containsKey(alias)) {
                        JmcLogger.info(SmartCafePkcs15Applet.class.getName(), "constructor", "El certificado '" + alias + "' se descarta por carecer de clave privada");
                        CERTS_BY_ALIAS.remove(alias);
                    }
                }
                if (aliases.isEmpty()) {
                    throw new IOException("La tarjeta no contiene claves");
                }
            } catch (Asn1Exception | TlvException | Iso7816FourCardException e) {
                throw new IOException("No se han podido leer los certificados", e);
            }
        } catch (Iso7816FourCardException e2) {
            throw new IOException("No se ha podido seleccionar el Applet AET PKCS#15", e2);
        }
    }

    private static void storeKeyOrdinal(int ordinal, BigInteger publicKeyModulus) {
        Set<String> aliases = CERTS_BY_ALIAS.keySet();
        for (String alias : aliases) {
            PublicKey publicKey = CERTS_BY_ALIAS.get(alias).getPublicKey();
            if (publicKey instanceof RSAPublicKey) {
                BigInteger certPublicKeyModulus = ((RSAPublicKey) publicKey).getModulus();
                if (certPublicKeyModulus.equals(publicKeyModulus)) {
                    KEYNO_BY_ALIAS.put(alias, Integer.valueOf(ordinal));
                }
            }
        }
    }

    private static int getKeyCount(ResponseApdu ra) throws IOException {
        if (!ra.isOk()) {
            throw new IOException("No se ha podido determinar el numero de claves en tarjeta: " + HexUtils.hexify(ra.getBytes(), true));
        }
        byte[] res = ra.getData();
        if (res.length == 6 && res[0] == 127 && res[1] == -1 && res[2] == 32 && res[4] == 12 && res[5] == 11) {
            return 32 - res[3];
        }
        throw new IOException("No se ha podido determinar el numero de claves en tarjeta: " + HexUtils.hexify(ra.getBytes(), true));
    }

    public void setPasswordCallback(PasswordCallback pwc) {
        this.passwordCallback = pwc;
    }

    public void setCallbackHandler(CallbackHandler callh) {
        this.callbackHandler = callh;
    }

    public static void connect(ApduConnection conn) throws IOException {
        if (conn == null) {
            throw new IllegalArgumentException("La conexion no puede ser nula");
        }
        if (!conn.isOpen()) {
            conn.open();
        }
        checkAtr(conn.reset());
    }

    private void preloadCertificates() throws TlvException, Iso7816FourCardException, IOException, Asn1Exception {
        selectMasterFile();
        selectFileById(ODF_PATH);
        byte[] odfBytes = readBinaryComplete(162);
        Odf odf = new Odf();
        odf.setDerValue(odfBytes);
        Path cdfPath = odf.getCdfPath();
        Cdf cdf = new Cdf();
        try {
            selectMasterFile();
            byte[] cdfBytes = selectFileByIdAndRead(cdfPath.getPathBytes());
            cdf.setDerValue(cdfBytes);
            if (cdf.getCertificateCount() < 1) {
                JmcLogger.warning("La tarjeta no contiene ningun certificado");
            }
            for (int i = 0; i < cdf.getCertificateCount(); i++) {
                preloadCertificate(cdf.getCertificatePath(i), cdf.getCertificateAlias(i));
            }
        } catch (Exception e) {
            throw new ApduConnectionException("No se ha podido cargar el CDF de la tarjeta", e);
        }
    }

    private void preloadCertificate(String certificatePath, String certificateAlias) {
        try {
            int fileLength = -1;
            for (Location certLocation = new Location(certificatePath); certLocation != null; certLocation = certLocation.getChild()) {
                byte[] id = certLocation.getFile();
                try {
                    fileLength = selectFileById(id);
                } catch (FileNotFoundException e) {
                    JmcLogger.warning("El CDF indicaba un certificado en la ruta '" + certLocation + "', pero un elemento de esta no existe, se ignorara: " + e);
                }
            }
            if (fileLength <= 0) {
                JmcLogger.warning("El certificado " + certificateAlias + " del dispositivo esta vacio");
            } else {
                byte[] certBytes = readBinaryComplete(fileLength);
                CERTS_BY_ALIAS.put(certificateAlias, CryptoHelper.generateCertificate(certBytes));
            }
        } catch (Exception e2) {
            JmcLogger.severe("Error en la lectura del certificado " + certificateAlias + " del dispositivo: " + e2);
        }
    }

    @Override // es.gob.jmulticard.card.AbstractSmartCard
    public String getCardName() {
        return "G&D SmartCafe 3.2 (PKCS#15 Applet)";
    }

    @Override // es.gob.jmulticard.card.CryptoCard
    public String[] getAliases() {
        return (String[]) CERTS_BY_ALIAS.keySet().toArray(new String[0]);
    }

    @Override // es.gob.jmulticard.card.CryptoCard
    public X509Certificate getCertificate(String alias) {
        return CERTS_BY_ALIAS.get(alias);
    }

    @Override // es.gob.jmulticard.card.iso7816four.AbstractIso7816FourCard
    protected void selectMasterFile() throws Iso7816FourCardException, ApduConnectionException {
        selectFileById(MF_PATH);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(getCardName()).append("\n Tarjeta con ").append(CERTS_BY_ALIAS.size()).append(" certificado(s):\n");
        String[] aliases = getAliases();
        for (int i = 0; i < aliases.length; i++) {
            sb.append("  ");
            sb.append(i + 1);
            sb.append(" - ");
            sb.append(aliases[i]);
        }
        return sb.toString();
    }

    @Override // es.gob.jmulticard.card.iso7816four.AbstractIso7816FourCard
    public int selectFileById(byte[] id) throws Iso7816FourCardException, ApduConnectionException {
        CommandApdu selectCommand = new SelectFileByIdApduCommand(getCla(), id);
        ResponseApdu res = getConnection().transmit(selectCommand);
        if (HexUtils.arrayEquals(res.getBytes(), new byte[]{106, -126})) {
            throw new FileNotFoundException(id);
        }
        SelectFileApduResponse response = new SelectFileApduResponse(res);
        if (response.isOk()) {
            return HexUtils.getUnsignedInt(new byte[]{response.getData()[4], response.getData()[5]}, 0);
        }
        StatusWord sw = response.getStatusWord();
        if (sw.equals(new StatusWord((byte) 106, (byte) -126))) {
            throw new FileNotFoundException(id);
        }
        throw new Iso7816FourCardException(sw, selectCommand);
    }

    @Override // es.gob.jmulticard.card.iso7816four.AbstractIso7816FourCard
    public void verifyPin(PasswordCallback psc) throws PinException, ApduConnectionException {
        if (psc == null) {
            throw new PasswordCallbackNotFoundException("No se puede verificar el titular con un PasswordCallback nulo");
        }
        ResponseApdu verifyResponse = getConnection().transmit(new GiDeVerifyApduCommand(psc));
        if (!verifyResponse.isOk()) {
            if (verifyResponse.getStatusWord().getMsb() == 99) {
                throw new BadPinException(verifyResponse.getStatusWord().getLsb() - (-64));
            }
            if (verifyResponse.getStatusWord().getMsb() == 105 && verifyResponse.getStatusWord().getLsb() == -125) {
                throw new AuthenticationModeLockedException();
            }
            throw new ApduConnectionException(new Iso7816FourCardException("Error en la verificacion de PIN (" + verifyResponse.getStatusWord() + ")", verifyResponse.getStatusWord()));
        }
    }

    @Override // es.gob.jmulticard.card.CryptoCard
    public PrivateKeyReference getPrivateKey(String alias) {
        if (!KEYNO_BY_ALIAS.containsKey(alias)) {
            return null;
        }
        return new SmartCafePrivateKeyReference(KEYNO_BY_ALIAS.get(alias));
    }

    @Override // es.gob.jmulticard.card.CryptoCard
    public byte[] sign(byte[] data, String algorithm, PrivateKeyReference keyRef) throws CryptoCardException, PinException {
        if (data == null) {
            throw new CryptoCardException("Los datos a firmar no pueden ser nulos");
        }
        if (keyRef == null) {
            throw new IllegalArgumentException("La clave privada no puede ser nula");
        }
        if (!(keyRef instanceof SmartCafePrivateKeyReference)) {
            throw new IllegalArgumentException("La clave proporcionada debe ser de tipo " + SmartCafePrivateKeyReference.class.getName() + ", pero se ha recibido de tipo " + keyRef.getClass().getName());
        }
        SmartCafePrivateKeyReference scPrivateKey = (SmartCafePrivateKeyReference) keyRef;
        if (!this.authenticated) {
            try {
                verifyPin(getInternalPasswordCallback());
                this.authenticated = true;
            } catch (ApduConnectionException e1) {
                throw new CryptoCardException("Error en la verificacion de PIN", e1);
            }
        }
        ResponseApdu res = null;
        try {
            res = sendArbitraryApdu(new MseSetComputationApduCommand((byte) 1, new byte[]{(byte) scPrivateKey.getKeyOrdinal()}, new byte[]{2}));
            if (res == null || !res.isOk()) {
                throw new CryptoCardException("No se ha podido establecer la clave y el algoritmo de firma" + (res != null ? " (repuesta=" + res + ")" : ""));
            }
            try {
                byte[] digestInfo = DigestInfo.encode(algorithm, data, this.cryptoHelper);
                try {
                    res = sendArbitraryApdu(new PsoSignHashApduCommand((byte) 1, digestInfo));
                    if (res == null || !res.isOk()) {
                        throw new CryptoCardException("No se ha podido firmar el DigestInfo" + (res != null ? " (repuesta=" + res + ")" : ""));
                    }
                    return res.getData();
                } catch (ApduConnectionException e) {
                    throw new CryptoCardException("Error firmando (repuesta=" + res + ")", e);
                }
            } catch (IOException e2) {
                throw new CryptoCardException("Error en el calculo de la huella para firmar", e2);
            }
        } catch (ApduConnectionException e3) {
            throw new CryptoCardException("Error estableciendo la clave y el algoritmo de firma (repuesta=" + res + ")", e3);
        }
    }

    private int getPinRetriesLeft() throws PinException {
        CommandApdu verifyCommandApdu = new GiDeRetriesLeftApduCommand();
        try {
            ResponseApdu verifyResponse = getConnection().transmit(verifyCommandApdu);
            if (verifyResponse.isOk() || verifyResponse.getBytes().length > 2) {
                return verifyResponse.getBytes()[1];
            }
            throw new PinException("Error comprobando los intentos restantes de PIN con respuesta: " + verifyResponse.getStatusWord());
        } catch (ApduConnectionException e) {
            throw new PinException("Error obteniendo el PIN del CallbackHandler", e);
        }
    }

    private PasswordCallback getInternalPasswordCallback() throws PinException {
        if (this.passwordCallback != null) {
            if (getPinRetriesLeft() == 0) {
                throw new AuthenticationModeLockedException();
            }
            return this.passwordCallback;
        }
        if (this.callbackHandler != null) {
            int retriesLeft = getPinRetriesLeft();
            if (retriesLeft == 0) {
                throw new AuthenticationModeLockedException();
            }
            PasswordCallback pwc = new PasswordCallback(CardMessages.getString("Gen.0", Integer.toString(retriesLeft)), false);
            try {
                this.callbackHandler.handle(new Callback[]{pwc});
                return pwc;
            } catch (IOException e) {
                throw new PinException("Error obteniendo el PIN del CallbackHandler", e);
            } catch (UnsupportedCallbackException e2) {
                throw new PinException("El CallbackHandler no soporta pedir el PIN al usuario", e2);
            }
        }
        throw new PinException("No hay ningun metodo para obtener el PIN");
    }

    private static void checkAtr(byte[] atrBytes) throws InvalidCardException {
        Atr tmpAtr = new Atr(atrBytes, ATR_MASK);
        if (ATR.equals(tmpAtr)) {
            JmcLogger.info(SmartCafePkcs15Applet.class.getName(), "checkAtr", "Detectada G&D SmartCafe 3.2");
        } else if (ATR_MSC.equals(tmpAtr)) {
            JmcLogger.info(SmartCafePkcs15Applet.class.getName(), "checkAtr", "Detectada G&D Mobile Security Card");
        } else {
            if (ATR_TCL.equals(tmpAtr)) {
                JmcLogger.info(SmartCafePkcs15Applet.class.getName(), "checkAtr", "Detectada G&D SmartCafe 3.2 via T=CL (conexion inalambrica)");
                return;
            }
            throw new InvalidCardException("La tarjeta no es una SmartCafe 3.2 (ATR encontrado: " + HexUtils.hexify(atrBytes, false) + ")");
        }
    }
}
