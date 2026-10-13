package es.gob.jmulticard.card.fnmt.ceres;

import es.gob.jmulticard.CryptoHelper;
import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.JmcLogger;
import es.gob.jmulticard.apdu.CommandApdu;
import es.gob.jmulticard.apdu.ResponseApdu;
import es.gob.jmulticard.apdu.StatusWord;
import es.gob.jmulticard.apdu.ceres.CeresLoadDataApduCommand;
import es.gob.jmulticard.apdu.ceres.CeresSignDataApduCommand;
import es.gob.jmulticard.apdu.ceres.CeresVerifyApduCommand;
import es.gob.jmulticard.apdu.dnie.RetriesLeftApduCommand;
import es.gob.jmulticard.apdu.iso7816eight.EnvelopeDataApduCommand;
import es.gob.jmulticard.asn1.Asn1Exception;
import es.gob.jmulticard.asn1.TlvException;
import es.gob.jmulticard.asn1.custom.fnmt.ceres.CeresCdf;
import es.gob.jmulticard.asn1.custom.fnmt.ceres.CeresPrKdf;
import es.gob.jmulticard.asn1.der.pkcs1.DigestInfo;
import es.gob.jmulticard.asn1.der.pkcs15.Cdf;
import es.gob.jmulticard.asn1.der.pkcs15.Pkcs15Cdf;
import es.gob.jmulticard.asn1.der.pkcs15.Pkcs15PrKdf;
import es.gob.jmulticard.asn1.der.pkcs15.PrKdf;
import es.gob.jmulticard.card.Atr;
import es.gob.jmulticard.card.AuthenticationModeLockedException;
import es.gob.jmulticard.card.BadPinException;
import es.gob.jmulticard.card.CardMessages;
import es.gob.jmulticard.card.CompressionUtils;
import es.gob.jmulticard.card.CryptoCard;
import es.gob.jmulticard.card.CryptoCardException;
import es.gob.jmulticard.card.InvalidCardException;
import es.gob.jmulticard.card.Location;
import es.gob.jmulticard.card.PasswordCallbackNotFoundException;
import es.gob.jmulticard.card.PinException;
import es.gob.jmulticard.card.PrivateKeyReference;
import es.gob.jmulticard.card.iso7816eight.AbstractIso7816EightCard;
import es.gob.jmulticard.card.iso7816four.Iso7816FourCardException;
import es.gob.jmulticard.connection.ApduConnection;
import es.gob.jmulticard.connection.ApduConnectionException;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/fnmt/ceres/Ceres.class */
public final class Ceres extends AbstractIso7816EightCard implements CryptoCard {
    private static final byte CLA = 0;
    private final CryptoHelper cryptoHelper;
    private static final int MAX_APDU_SIZE = 255;
    private static final String MASTER_FILE_NAME = "Master.File";
    private static final byte ERROR_PIN_SW1 = 103;
    private static final byte ERROR_PIN_SW2 = 99;
    private Map<String, X509Certificate> certs;
    private Map<String, String> aliasByCertAndKeyId;
    private Map<String, Byte> keys;
    private PasswordCallback passwordCallback;
    private boolean authenticated;
    private CallbackHandler callbackHandler;
    private static final byte[] ATR_MASK_TC = {-1, -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, -1, -1, -1};
    private static final Atr ATR_TC = new Atr(new byte[]{59, 127, 0, 0, 0, 0, 106, 70, 78, 77, 84, 0, 0, 0, 0, 0, 0, 3, -112, 0}, ATR_MASK_TC);
    private static final byte[] ATR_MASK_ST = {-1, -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, -1, -1, -1};
    private static final Atr ATR_ST = new Atr(new byte[]{59, 127, 0, 0, 0, 0, 106, 67, 69, 82, 69, 83, 2, 44, 52, 0, 0, 3, -112, 0}, ATR_MASK_ST);
    private static final byte[] ATR_MASK_SLE_FN20 = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    private static final Atr ATR_SLE_FN20 = new Atr(new byte[]{59, -17, 0, 0, 64, 20, -128, 37, 67, 69, 82, 69, 83, 87, 5, 96, 1, 2, 3, -112, 0}, ATR_MASK_SLE_FN20);
    private static final byte[] ATR_MASK_SLE_FN19 = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    private static final Atr ATR_SLE_FN19 = new Atr(new byte[]{59, -17, 0, 0, 64, 20, -128, 37, 67, 69, 82, 69, 83, 87, 1, 22, 1, 1, 3, -112, 0}, ATR_MASK_SLE_FN19);
    private static final Location CDF_LOCATION = new Location("50156004");
    private static final Location PRKDF_LOCATION = new Location("50156001");

    public void setPasswordCallback(PasswordCallback pwc) {
        this.passwordCallback = pwc;
    }

    private static void checkAtr(byte[] atrBytes) throws InvalidCardException {
        Atr tmpAtr = new Atr(atrBytes, ATR_MASK_TC);
        if (ATR_TC.equals(tmpAtr)) {
            if (atrBytes[15] >= 4 && atrBytes[16] >= 48) {
                throw new InvalidCardException("Encontrada CERES en version " + HexUtils.hexify(new byte[]{atrBytes[15]}, false) + "." + HexUtils.hexify(new byte[]{atrBytes[16]}, false) + ", pero las versiones iguales o superiores a la 04.30 no estan soportadas por este controlador");
            }
            return;
        }
        Atr tmpAtr2 = new Atr(atrBytes, ATR_MASK_ST);
        if (ATR_ST.equals(tmpAtr2)) {
            return;
        }
        Atr tmpAtr3 = new Atr(atrBytes, ATR_MASK_SLE_FN19);
        if (ATR_SLE_FN19.equals(tmpAtr3)) {
            return;
        }
        Atr tmpAtr4 = new Atr(atrBytes, ATR_MASK_SLE_FN20);
        if (ATR_SLE_FN20.equals(tmpAtr4)) {
        } else {
            throw new InvalidCardException("CERES", ATR_TC, atrBytes);
        }
    }

    public static void connect(ApduConnection conn) throws ApduConnectionException, InvalidCardException {
        conn.open();
        checkAtr(conn.reset());
    }

    public Ceres(ApduConnection conn, CryptoHelper ch) throws ApduConnectionException, InvalidCardException {
        super((byte) 0, conn);
        this.passwordCallback = null;
        this.authenticated = false;
        if (ch == null) {
            throw new IllegalArgumentException("El CryptoHelper no puede ser nulo");
        }
        connect(getConnection());
        try {
            preload();
            this.cryptoHelper = ch;
        } catch (Exception e) {
            throw new ApduConnectionException("Error cargando las estructuras iniciales de la tarjeta", e);
        }
    }

    private void preload() throws TlvException, Iso7816FourCardException, IOException, CertificateException, Asn1Exception {
        selectMasterFile();
        byte[] cdfBytes = selectFileByLocationAndRead(CDF_LOCATION);
        Pkcs15Cdf cdf = new CeresCdf();
        try {
            cdf.setDerValue(cdfBytes);
        } catch (Exception e) {
            JmcLogger.info(Ceres.class.getName(), "preload", "El CDF no es de tipo FNMT, se intenta en modo PKCS#15: " + e);
            cdf = new Cdf();
            cdf.setDerValue(cdfBytes);
        }
        this.certs = new LinkedHashMap(cdf.getCertificateCount());
        this.aliasByCertAndKeyId = new LinkedHashMap(cdf.getCertificateCount());
        for (int i = 0; i < cdf.getCertificateCount(); i++) {
            Location l = new Location(cdf.getCertificatePath(i).replace("\\", "").trim());
            try {
                X509Certificate cert = CompressionUtils.getCertificateFromCompressedOrNotData(selectFileByLocationAndRead(l), this.cryptoHelper);
                String alias = i + " " + cert.getSerialNumber();
                this.aliasByCertAndKeyId.put(HexUtils.hexify(cdf.getCertificateId(i), false), alias);
                this.certs.put(alias, cert);
            } catch (IOException e2) {
                JmcLogger.warning("No se ha encontrado un certificado referenciado, se pasa al siguiente: " + e2);
            }
        }
        byte[] prkdfValue = selectFileByLocationAndRead(PRKDF_LOCATION);
        Pkcs15PrKdf prkdf = new PrKdf();
        try {
            prkdf.setDerValue(prkdfValue);
        } catch (Exception e3) {
            JmcLogger.info(Ceres.class.getName(), "preload", "El PrKDF no es de tipo PKCS#15, se intenta en modo FNMT: " + e3);
            prkdf = new CeresPrKdf();
            prkdf.setDerValue(prkdfValue);
        }
        this.keys = new LinkedHashMap();
        for (int i2 = 0; i2 < prkdf.getKeyCount(); i2++) {
            String alias2 = this.aliasByCertAndKeyId.get(HexUtils.hexify(prkdf.getKeyId(i2), false));
            if (alias2 != null) {
                this.keys.put(alias2, Byte.valueOf(prkdf.getKeyReference(i2)));
            }
        }
        hideCertsWithoutKey();
    }

    private void hideCertsWithoutKey() {
        try {
            String[] aliases = getAliases();
            for (String alias : aliases) {
                if (this.keys.get(alias) == null) {
                    this.certs.remove(alias);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("No se han podido leer los alias de los certificados de la tarjeta CERES", e);
        }
    }

    @Override // es.gob.jmulticard.card.CryptoCard
    public String[] getAliases() {
        return (String[]) this.certs.keySet().toArray(new String[0]);
    }

    @Override // es.gob.jmulticard.card.CryptoCard
    public X509Certificate getCertificate(String alias) {
        return this.certs.get(alias);
    }

    @Override // es.gob.jmulticard.card.CryptoCard
    public PrivateKeyReference getPrivateKey(String alias) {
        return new CeresPrivateKeyReference(this.keys.get(alias).byteValue(), ((RSAPublicKey) this.certs.get(alias).getPublicKey()).getModulus().bitLength());
    }

    @Override // es.gob.jmulticard.card.CryptoCard
    public byte[] sign(byte[] data, String algorithm, PrivateKeyReference keyRef) throws CryptoCardException, PinException {
        if (data == null) {
            throw new CryptoCardException("Los datos a firmar no pueden ser nulos");
        }
        if (keyRef == null) {
            throw new IllegalArgumentException("La clave privada no puede ser nula");
        }
        if (!(keyRef instanceof CeresPrivateKeyReference)) {
            throw new IllegalArgumentException("La clave proporcionada debe ser de tipo CeresPrivateKeyReference, pero se ha recibido de tipo " + keyRef.getClass().getName());
        }
        CeresPrivateKeyReference ceresPrivateKey = (CeresPrivateKeyReference) keyRef;
        if (!this.authenticated) {
            try {
                verifyPin(getInternalPasswordCallback());
                this.authenticated = true;
            } catch (ApduConnectionException e1) {
                throw new CryptoCardException("Error en la verificacion de PIN", e1);
            }
        }
        try {
            byte[] digestInfo = DigestInfo.encode(algorithm, data, this.cryptoHelper);
            loadData(ceresPrivateKey.getKeyBitSize(), digestInfo);
            CommandApdu cmd = new CeresSignDataApduCommand(ceresPrivateKey.getKeyReference(), ceresPrivateKey.getKeyBitSize());
            try {
                ResponseApdu res = sendArbitraryApdu(cmd);
                if (!res.isOk()) {
                    throw new CryptoCardException("No se han podido firmar los datos. Respuesta: " + HexUtils.hexify(res.getBytes(), true));
                }
                return res.getData();
            } catch (ApduConnectionException e) {
                throw new CryptoCardException("Error firmando los datos", e);
            }
        } catch (Exception e2) {
            throw new CryptoCardException("Error creando el DigestInfo para la firma con el algoritmo " + algorithm, e2);
        }
    }

    private void loadData(int keyBitSize, byte[] digestInfo) throws CryptoCardException {
        try {
            byte[] paddedData = CryptoHelper.addPkcs1PaddingForPrivateKeyOperation(digestInfo, keyBitSize);
            if (keyBitSize <= 1024) {
                try {
                    ResponseApdu res = sendArbitraryApdu(new CeresLoadDataApduCommand(paddedData));
                    if (!res.isOk()) {
                        throw new CryptoCardException("No se han podido enviar los datos a firmar a la tarjeta. Respuesta: " + HexUtils.hexify(res.getBytes(), true));
                    }
                    return;
                } catch (ApduConnectionException e) {
                    throw new CryptoCardException("Error enviando los datos a firmar a la tarjeta", e);
                }
            }
            if (keyBitSize == 2048) {
                byte[] envelopedLoadDataApdu = {-112, 88, 0, 0, 0, 1, 0};
                byte[] data = new byte[255];
                System.arraycopy(envelopedLoadDataApdu, 0, data, 0, envelopedLoadDataApdu.length);
                System.arraycopy(paddedData, 0, data, envelopedLoadDataApdu.length, 255 - envelopedLoadDataApdu.length);
                try {
                    ResponseApdu res2 = sendArbitraryApdu(new EnvelopeDataApduCommand(data));
                    if (!res2.isOk()) {
                        throw new CryptoCardException("No se han podido enviar (primera tanda) los datos a firmar a la tarjeta. Respuesta: " + HexUtils.hexify(res2.getBytes(), true));
                    }
                    byte[] data2 = new byte[8];
                    System.arraycopy(paddedData, 255 - envelopedLoadDataApdu.length, data2, 0, 8);
                    try {
                        ResponseApdu res3 = sendArbitraryApdu(new EnvelopeDataApduCommand(data2));
                        if (!res3.isOk()) {
                            throw new CryptoCardException("No se han podido enviar (segunda tanda) los datos a firmar a la tarjeta. Respuesta: " + HexUtils.hexify(res3.getBytes(), true));
                        }
                        return;
                    } catch (ApduConnectionException e2) {
                        throw new CryptoCardException("Error en el segundo envio a la tarjeta de los datos a firmar", e2);
                    }
                } catch (Exception e3) {
                    throw new CryptoCardException("Error en el primer envio a la tarjeta de los datos a firmar", e3);
                }
            }
            throw new IllegalArgumentException("Solo se soportan claves de 2048 o 1024 bits");
        } catch (IOException e1) {
            throw new CryptoCardException("Error realizando el relleno PKCS#1 de los datos a firmar", e1);
        }
    }

    @Override // es.gob.jmulticard.card.iso7816four.AbstractIso7816FourCard
    protected void selectMasterFile() throws Iso7816FourCardException, ApduConnectionException {
        selectFileByName(MASTER_FILE_NAME);
    }

    @Override // es.gob.jmulticard.card.iso7816four.AbstractIso7816FourCard
    public void verifyPin(PasswordCallback pinPc) throws PinException, ApduConnectionException {
        if (pinPc == null) {
            throw new PasswordCallbackNotFoundException("No se ha establecido un PasswordCallback");
        }
        CommandApdu chv = new CeresVerifyApduCommand((byte) 0, pinPc);
        ResponseApdu verifyResponse = sendArbitraryApdu(chv);
        if (!verifyResponse.isOk()) {
            if (verifyResponse.getStatusWord().getMsb() == 103 || verifyResponse.getStatusWord().getMsb() == 99) {
                throw new BadPinException(verifyResponse.getStatusWord().getLsb() - (-64));
            }
            if (new StatusWord((byte) 105, (byte) -125).equals(verifyResponse.getStatusWord())) {
                throw new AuthenticationModeLockedException();
            }
            throw new ApduConnectionException(new Iso7816FourCardException("Error en la verificacion de PIN (" + verifyResponse.getStatusWord() + ")", verifyResponse.getStatusWord()));
        }
    }

    protected PasswordCallback getInternalPasswordCallback() throws PinException {
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

    private int getPinRetriesLeft() throws PinException {
        CommandApdu verifyCommandApdu = new RetriesLeftApduCommand();
        try {
            ResponseApdu verifyResponse = getConnection().transmit(verifyCommandApdu);
            return verifyResponse.getStatusWord().getLsb() - (-64);
        } catch (ApduConnectionException e) {
            throw new PinException("Error obteniendo el PIN del CallbackHandler", e);
        }
    }

    @Override // es.gob.jmulticard.card.AbstractSmartCard
    public String getCardName() {
        return "FNMT-RCM CERES";
    }

    public CallbackHandler getCallbackHandler() {
        return this.callbackHandler;
    }

    public void setCallbackHandler(CallbackHandler callh) {
        this.callbackHandler = callh;
    }
}
