package es.gob.jmulticard.connection.cwa14890;

import es.gob.jmulticard.CryptoHelper;
import es.gob.jmulticard.DigestAlgorithm;
import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.JmcLogger;
import es.gob.jmulticard.apdu.CommandApdu;
import es.gob.jmulticard.apdu.ResponseApdu;
import es.gob.jmulticard.apdu.StatusWord;
import es.gob.jmulticard.card.cwa14890.Cwa14890Card;
import es.gob.jmulticard.card.cwa14890.Cwa14890PrivateConstants;
import es.gob.jmulticard.card.cwa14890.Cwa14890PublicConstants;
import es.gob.jmulticard.connection.AbstractApduEncrypter;
import es.gob.jmulticard.connection.ApduConnection;
import es.gob.jmulticard.connection.ApduConnectionException;
import es.gob.jmulticard.connection.ApduConnectionProtocol;
import es.gob.jmulticard.connection.ApduEncrypterDes;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.pdmodel.interactive.action.PDWindowsLaunchParams;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/connection/cwa14890/Cwa14890OneV1Connection.class */
public class Cwa14890OneV1Connection implements Cwa14890Connection {
    private static final int KICC_LENGTH = 32;
    private static final int KIFD_LENGTH = 32;
    private static final byte ISO_9796_2_PADDING_START = 106;
    private static final byte ISO_9796_2_PADDING_END = -68;
    private static final byte MSB_INCORRECT_LE = 108;
    private static final byte MSB_INCORRECT_LE_PACE = 98;
    private final CryptoHelper cryptoHelper;
    private Cwa14890Card card;
    protected ApduConnection subConnection;
    private byte[] kenc = null;
    private byte[] kmac = null;
    private byte[] ssc = null;
    protected boolean openState = false;
    private final AbstractApduEncrypter apduEncrypter;
    private Cwa14890PublicConstants pubConsts;
    private Cwa14890PrivateConstants privConsts;
    private static final StatusWord INVALID_CRYPTO_CHECKSUM = new StatusWord((byte) 102, (byte) -120);
    private static final byte[] SECURE_CHANNEL_KENC_AUX = {0, 0, 0, 1};
    private static final byte[] SECURE_CHANNEL_KMAC_AUX = {0, 0, 0, 2};

    protected AbstractApduEncrypter instantiateApduEncrypter() {
        return new ApduEncrypterDes();
    }

    public String toString() {
        return "Conexion de tipo CWA-14890-V1 " + (isOpen() ? "abierta sobre " + getSubConnection() : "cerrada");
    }

    Cwa14890OneV1Connection(ApduConnection connection, CryptoHelper cryptoHlpr) {
        if (cryptoHlpr == null) {
            throw new IllegalArgumentException("CryptoHelper no puede ser nulo");
        }
        this.subConnection = connection instanceof Cwa14890Connection ? ((Cwa14890Connection) connection).getSubConnection() : connection;
        this.cryptoHelper = cryptoHlpr;
        this.apduEncrypter = instantiateApduEncrypter();
    }

    public Cwa14890OneV1Connection(Cwa14890Card connectedCard, ApduConnection connection, CryptoHelper cryptoHlpr, Cwa14890PublicConstants cwaConsts, Cwa14890PrivateConstants cwaPrivConsts) {
        if (connectedCard == null) {
            throw new IllegalArgumentException("No se ha proporcionado la tarjeta CWA-14890 con la que abrir el canal seguro");
        }
        if (cryptoHlpr == null) {
            throw new IllegalArgumentException("CryptoHelper no puede ser nulo");
        }
        if (cwaConsts == null) {
            throw new IllegalArgumentException("las claves CWA-14890 no pueden ser nulas");
        }
        this.card = connectedCard;
        this.subConnection = connection instanceof Cwa14890Connection ? ((Cwa14890Connection) connection).getSubConnection() : connection;
        this.cryptoHelper = cryptoHlpr;
        this.apduEncrypter = instantiateApduEncrypter();
        this.pubConsts = cwaConsts;
        this.privConsts = cwaPrivConsts;
    }

    @Override // es.gob.jmulticard.connection.ApduConnection
    public void open() throws ApduConnectionException {
        ApduConnection conn = this.subConnection;
        conn.open();
        byte[] serial = getPaddedSerial();
        JmcLogger.info(Cwa14890OneV1Connection.class.getName(), PDWindowsLaunchParams.OPERATION_OPEN, "Obtenido el numero de serie de la tarjeta");
        try {
            this.card.verifyIcc();
            JmcLogger.info(Cwa14890OneV1Connection.class.getName(), PDWindowsLaunchParams.OPERATION_OPEN, "Verificado correctamente el certificado de la tarjeta");
            try {
                RSAPublicKey iccPublicKey = this.card.getIccCertPublicKey();
                try {
                    this.card.verifyIfdCertificateChain(this.pubConsts);
                    JmcLogger.info(Cwa14890OneV1Connection.class.getName(), PDWindowsLaunchParams.OPERATION_OPEN, "Verificada correctamente la cadena de certificados del controlador");
                    try {
                        byte[] randomIfd = this.cryptoHelper.generateRandomBytes(8);
                        try {
                            byte[] kicc = internalAuthentication(randomIfd, iccPublicKey);
                            JmcLogger.info(Cwa14890OneV1Connection.class.getName(), PDWindowsLaunchParams.OPERATION_OPEN, "Autenticacion interna de la tarjeta completada");
                            byte[] randomIcc = this.card.getChallenge();
                            try {
                                byte[] kifd = externalAuthentication(serial, randomIcc, iccPublicKey);
                                JmcLogger.info(Cwa14890OneV1Connection.class.getName(), PDWindowsLaunchParams.OPERATION_OPEN, "Autenticacion externa de la tarjeta completada");
                                byte[] kidficc = HexUtils.xor(kicc, kifd);
                                try {
                                    this.kenc = generateKenc(kidficc);
                                    try {
                                        this.kmac = generateKmac(kidficc);
                                        this.ssc = generateSsc(randomIfd, randomIcc);
                                        JmcLogger.info(Cwa14890OneV1Connection.class.getName(), PDWindowsLaunchParams.OPERATION_OPEN, "Generadas claves y contador de secuencia para el canal CWA-14890");
                                        this.openState = true;
                                    } catch (IOException e) {
                                        conn.close();
                                        throw new ApduConnectionException("Error al generar la clave Kmac para el tratamiento del canal seguro", e);
                                    }
                                } catch (IOException e2) {
                                    conn.close();
                                    throw new ApduConnectionException("Error al generar la clave Kenc para el tratamiento del canal seguro", e2);
                                }
                            } catch (Exception e3) {
                                conn.close();
                                throw new ApduConnectionException("Error durante el proceso de autenticacion externa de la tarjeta", e3);
                            }
                        } catch (Exception e4) {
                            conn.close();
                            throw new ApduConnectionException("Error durante el proceso de autenticacion interna de la tarjeta", e4);
                        }
                    } catch (IOException e1) {
                        conn.close();
                        throw new SecureChannelException("No se pudo generar el array de aleatorios", e1);
                    }
                } catch (Exception e5) {
                    conn.close();
                    throw new ApduConnectionException("Error al verificar la cadena de certificados del controlador", e5);
                }
            } catch (IOException e6) {
                conn.close();
                throw new ApduConnectionException("No se pudo leer certificado de componente", e6);
            }
        } catch (IOException e7) {
            conn.close();
            throw new IllegalStateException("No se han podido validar los certificados CWA-14890", e7);
        } catch (SecurityException e8) {
            conn.close();
            throw new IllegalStateException("Condicion de seguridad no satisfecha en la validacion de los certificados CWA-14890", e8);
        } catch (CertificateException e9) {
            conn.close();
            throw new IllegalStateException("No se han podido tratar los certificados CWA-14890", e9);
        }
    }

    private byte[] generateKenc(byte[] kidficc) throws IOException {
        byte[] kidficcConcat = HexUtils.concatenateByteArrays(kidficc, SECURE_CHANNEL_KENC_AUX);
        byte[] keyEnc = new byte[16];
        System.arraycopy(this.cryptoHelper.digest(DigestAlgorithm.SHA1, kidficcConcat), 0, keyEnc, 0, keyEnc.length);
        return keyEnc;
    }

    private byte[] generateKmac(byte[] kidficc) throws IOException {
        byte[] kidficcConcat = HexUtils.concatenateByteArrays(kidficc, SECURE_CHANNEL_KMAC_AUX);
        byte[] keyMac = new byte[16];
        System.arraycopy(this.cryptoHelper.digest(DigestAlgorithm.SHA1, kidficcConcat), 0, keyMac, 0, keyMac.length);
        return keyMac;
    }

    private static byte[] generateSsc(byte[] randomIfd, byte[] randomIcc) {
        byte[] ssc = new byte[8];
        System.arraycopy(randomIcc, 4, ssc, 0, 4);
        System.arraycopy(randomIfd, 4, ssc, 4, 4);
        return ssc;
    }

    public static byte[] internalAuthGetInternalAuthenticateMessage(Cwa14890Card card, Cwa14890PublicConstants pubConsts, byte[] randomIfd) throws ApduConnectionException {
        try {
            card.setKeysToAuthentication(card.getChrCCvIfd(pubConsts), card.getRefIccPrivateKey(pubConsts));
            return card.getInternalAuthenticateMessage(randomIfd, card.getChrCCvIfd(pubConsts));
        } catch (Exception e) {
            throw new SecureChannelException("Error durante el establecimiento de la clave publica de Terminal y la privada de Componente para su autenticacion", e);
        }
    }

    public static byte[] internalAuthValidateInternalAuthenticateMessage(byte[] chrCCvIfd, byte[] sigMinCiphered, byte[] randomIfd, RSAPrivateKey ifdPrivateKey, int ifdKeyLength, Cwa14890PublicConstants pubConsts, RSAPublicKey iccPublicKey, CryptoHelper cryptoHelper) throws IOException {
        byte[] sigMin = cryptoHelper.rsaDecrypt(sigMinCiphered, ifdPrivateKey);
        byte[] desMsg = cryptoHelper.rsaEncrypt(sigMin, iccPublicKey);
        if (desMsg[0] != 106 || desMsg[desMsg.length - 1] != -68) {
            byte[] sub = iccPublicKey.getModulus().subtract(new BigInteger(1, sigMin)).toByteArray();
            byte[] niccMinusSig = new byte[ifdKeyLength];
            if (sub.length > ifdKeyLength && sub[0] == 0) {
                System.arraycopy(sub, 1, niccMinusSig, 0, sub.length - 1);
            } else {
                System.arraycopy(sub, 0, niccMinusSig, 0, sub.length);
            }
            desMsg = cryptoHelper.rsaDecrypt(niccMinusSig, iccPublicKey);
            if (desMsg[0] != 106 || desMsg[desMsg.length - 1] != -68) {
                throw new SecureChannelException("Error en la autenticacion interna para el establecimiento del canal seguro. El mensaje descifrado es:\n" + HexUtils.hexify(desMsg, true));
            }
        }
        byte[] prnd1 = new byte[((ifdKeyLength - 32) - DigestAlgorithm.SHA1.getDigestLength()) - 2];
        System.arraycopy(desMsg, 1, prnd1, 0, prnd1.length);
        byte[] kicc = new byte[32];
        System.arraycopy(desMsg, prnd1.length + 1, kicc, 0, kicc.length);
        byte[] hash = new byte[DigestAlgorithm.SHA1.getDigestLength()];
        System.arraycopy(desMsg, prnd1.length + kicc.length + 1, hash, 0, hash.length);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(prnd1);
        baos.write(kicc);
        baos.write(randomIfd);
        baos.write(chrCCvIfd);
        byte[] calculatedHash = cryptoHelper.digest(DigestAlgorithm.SHA1, baos.toByteArray());
        if (!HexUtils.arrayEquals(hash, calculatedHash)) {
            throw new SecureChannelException("Error en la comprobacion de la clave de autenticacion interna. Se obtuvo el hash '" + HexUtils.hexify(calculatedHash, false) + "' cuando se esperaba: '" + HexUtils.hexify(hash, false) + OperatorName.SHOW_TEXT_LINE);
        }
        return kicc;
    }

    private byte[] internalAuthentication(byte[] randomIfd, RSAPublicKey iccPublicKey) throws IOException {
        byte[] sigMinCiphered = internalAuthGetInternalAuthenticateMessage(this.card, this.pubConsts, randomIfd);
        return internalAuthValidateInternalAuthenticateMessage(this.card.getChrCCvIfd(this.pubConsts), sigMinCiphered, randomIfd, this.card.getIfdPrivateKey(this.privConsts), this.card.getIfdKeyLength(this.pubConsts), this.pubConsts, iccPublicKey, this.cryptoHelper);
    }

    private byte[] externalAuthentication(byte[] serial, byte[] randomIcc, RSAPublicKey iccPublicKey) throws IOException {
        byte[] prnd2 = this.cryptoHelper.generateRandomBytes(((this.card.getIfdKeyLength(this.pubConsts) - 2) - 32) - DigestAlgorithm.SHA1.getDigestLength());
        byte[] kifd = this.cryptoHelper.generateRandomBytes(32);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(prnd2);
        baos.write(kifd);
        baos.write(randomIcc);
        baos.write(serial);
        byte[] hash = this.cryptoHelper.digest(DigestAlgorithm.SHA1, baos.toByteArray());
        baos.reset();
        baos.write(106);
        baos.write(prnd2);
        baos.write(kifd);
        baos.write(hash);
        baos.write(-68);
        byte[] msg = baos.toByteArray();
        RSAPrivateKey ifdPrivateKey = this.card.getIfdPrivateKey(this.privConsts);
        byte[] sig = this.cryptoHelper.rsaDecrypt(msg, ifdPrivateKey);
        BigInteger biSig = new BigInteger(1, sig);
        byte[] sigMin = ifdPrivateKey.getModulus().subtract(biSig).min(biSig).toByteArray();
        byte[] extAuthenticationData = this.cryptoHelper.rsaEncrypt(sigMin, iccPublicKey);
        boolean valid = this.card.externalAuthentication(extAuthenticationData);
        if (!valid) {
            throw new SecureChannelException("Error durante la autenticacion externa del canal seguro");
        }
        return kifd;
    }

    private byte[] getPaddedSerial() throws ApduConnectionException {
        byte[] serial = this.card.getSerialNumber();
        byte[] paddedSerial = serial;
        if (paddedSerial.length < 8) {
            paddedSerial = new byte[8];
            int i = 0;
            while (i < 8 - serial.length) {
                paddedSerial[i] = 0;
                i++;
            }
            System.arraycopy(serial, 0, paddedSerial, i, serial.length);
        }
        return paddedSerial;
    }

    @Override // es.gob.jmulticard.connection.ApduConnection
    public void close() throws ApduConnectionException {
        if (this.openState) {
            this.subConnection.close();
            this.openState = false;
        }
    }

    @Override // es.gob.jmulticard.connection.ApduConnection
    public ResponseApdu transmit(CommandApdu command) throws ApduConnectionException {
        try {
            this.ssc = increment(this.ssc);
            CommandApdu protectedApdu = this.apduEncrypter.protectAPDU(command, this.kenc, this.kmac, this.ssc, this.cryptoHelper);
            ResponseApdu responseApdu = this.subConnection.transmit(protectedApdu);
            if (INVALID_CRYPTO_CHECKSUM.equals(responseApdu.getStatusWord())) {
                throw new InvalidCryptographicChecksumException();
            }
            try {
                this.ssc = increment(this.ssc);
                ResponseApdu decipherApdu = this.apduEncrypter.decryptResponseApdu(responseApdu, this.kenc, this.ssc, this.kmac, this.cryptoHelper);
                if (decipherApdu.getStatusWord().getMsb() == 108) {
                    command.setLe(decipherApdu.getStatusWord().getLsb());
                    return transmit(command);
                }
                if (decipherApdu.getStatusWord().getMsb() == 98) {
                    command.setLe(command.getLe().intValue() - 1);
                    return transmit(command);
                }
                return decipherApdu;
            } catch (Exception e) {
                throw new ApduConnectionException("Error en la desencriptacion de la APDU de respuesta recibida por el canal seguro", e);
            }
        } catch (IOException e2) {
            throw new SecureChannelException("Error en la encriptacion de la APDU para su envio por el canal seguro", e2);
        }
    }

    @Override // es.gob.jmulticard.connection.ApduConnection
    public byte[] reset() throws ApduConnectionException {
        this.openState = false;
        byte[] atr = this.subConnection.reset();
        open();
        return atr;
    }

    @Override // es.gob.jmulticard.connection.ApduConnection
    public long[] getTerminals(boolean onlyWithCardPresent) throws ApduConnectionException {
        return this.subConnection.getTerminals(onlyWithCardPresent);
    }

    @Override // es.gob.jmulticard.connection.ApduConnection
    public String getTerminalInfo(int terminal) throws ApduConnectionException {
        return this.subConnection.getTerminalInfo(terminal);
    }

    @Override // es.gob.jmulticard.connection.ApduConnection
    public void setTerminal(int t) throws ApduConnectionException {
        this.subConnection.setTerminal(t);
    }

    @Override // es.gob.jmulticard.connection.ApduConnection
    public boolean isOpen() {
        return this.openState && this.subConnection.isOpen();
    }

    private static byte[] increment(byte[] data) {
        BigInteger bi = new BigInteger(1, data).add(BigInteger.ONE);
        byte[] biArray = bi.toByteArray();
        if (biArray.length > 8) {
            byte[] incrementedValue = new byte[8];
            System.arraycopy(biArray, biArray.length - incrementedValue.length, incrementedValue, 0, incrementedValue.length);
            return incrementedValue;
        }
        if (biArray.length < 8) {
            byte[] incrementedValue2 = new byte[8];
            System.arraycopy(biArray, 0, incrementedValue2, incrementedValue2.length - biArray.length, biArray.length);
            return incrementedValue2;
        }
        return biArray;
    }

    @Override // es.gob.jmulticard.connection.ApduConnection
    public ApduConnection getSubConnection() {
        return this.subConnection;
    }

    @Override // es.gob.jmulticard.connection.ApduConnection
    public void setProtocol(ApduConnectionProtocol p) {
        if (this.subConnection != null) {
            this.subConnection.setProtocol(p);
        }
    }

    @Override // es.gob.jmulticard.connection.cwa14890.Cwa14890Connection
    public byte[] getKenc() {
        return this.kenc;
    }

    @Override // es.gob.jmulticard.connection.cwa14890.Cwa14890Connection
    public byte[] getKmac() {
        return this.kmac;
    }

    @Override // es.gob.jmulticard.connection.cwa14890.Cwa14890Connection
    public byte[] getSsc() {
        return this.ssc;
    }
}
