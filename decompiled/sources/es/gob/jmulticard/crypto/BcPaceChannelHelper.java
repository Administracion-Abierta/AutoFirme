package es.gob.jmulticard.crypto;

import es.gob.jmulticard.CryptoHelper;
import es.gob.jmulticard.DigestAlgorithm;
import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.JmcLogger;
import es.gob.jmulticard.apdu.CommandApdu;
import es.gob.jmulticard.apdu.ResponseApdu;
import es.gob.jmulticard.apdu.iso7816four.pace.GeneralAuthenticateApduCommand;
import es.gob.jmulticard.apdu.iso7816four.pace.MseSetPaceAlgorithmApduCommand;
import es.gob.jmulticard.apdu.iso7816four.pace.PaceChat;
import es.gob.jmulticard.asn1.Tlv;
import es.gob.jmulticard.asn1.TlvException;
import es.gob.jmulticard.asn1.icao.CardAccess;
import es.gob.jmulticard.card.icao.IcaoException;
import es.gob.jmulticard.card.icao.InvalidCanOrMrzException;
import es.gob.jmulticard.card.icao.WirelessInitializer;
import es.gob.jmulticard.connection.ApduConnection;
import es.gob.jmulticard.connection.ApduConnectionException;
import es.gob.jmulticard.connection.pace.PaceException;
import es.gob.jmulticard.connection.pace.SecureMessaging;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;
import org.bouncycastle.asn1.teletrust.TeleTrusTNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/crypto/BcPaceChannelHelper.class */
public class BcPaceChannelHelper extends CryptoHelper.PaceChannelHelper {
    private final CardAccess cardAccess;
    private final PaceChat paceChat;

    public BcPaceChannelHelper(CryptoHelper ch, CardAccess crdAccess, PaceChat paceCht) {
        super(ch);
        this.cardAccess = crdAccess;
        this.paceChat = paceCht;
    }

    @Override // es.gob.jmulticard.CryptoHelper.PaceChannelHelper
    public SecureMessaging openPaceChannel(byte cla, WirelessInitializer pi, ApduConnection conn) throws ApduConnectionException, IcaoException {
        if (conn == null) {
            throw new IllegalArgumentException("El canal de conexion no puede ser nulo");
        }
        if (pi == null) {
            throw new IllegalArgumentException("Es necesario proporcionar un inicializador para abrir canal PACE");
        }
        if (this.cryptoHelper == null) {
            throw new IllegalArgumentException("El CryptoHelper no puede ser nulo");
        }
        if (!conn.isOpen()) {
            conn.open();
        }
        CardAccess.PaceAlgorithm paceAlgorithm = this.cardAccess.getPaceAlgorithm();
        CardAccess.PaceAlgorithmParam paceAlgorithmParam = this.cardAccess.getPaceAlgorithmParam();
        CommandApdu comm = new MseSetPaceAlgorithmApduCommand(cla, paceAlgorithm, pi.getPasswordType(), this.paceChat, paceAlgorithmParam);
        ResponseApdu res = conn.transmit(comm);
        if (!res.isOk()) {
            throw new PaceException(res.getStatusWord(), comm, "Error estableciendo el algoritmo del protocolo PACE (fallo en el MSE Set)");
        }
        JmcLogger.info(BcPaceChannelHelper.class.getName(), "openPaceChannel", "Establecido el algoritmo para PACE con el comando MSE Set");
        CommandApdu comm2 = new GeneralAuthenticateApduCommand((byte) 16, new GeneralAuthenticateApduCommand.DataEncryptedNonce());
        ResponseApdu res2 = conn.transmit(comm2);
        if (!res2.isOk()) {
            throw new PaceException(res2.getStatusWord(), comm2, "Error solicitando el aleatorio de calculo PACE (nonce)");
        }
        JmcLogger.info(BcPaceChannelHelper.class.getName(), "openPaceChannel", "Solicitado el aleatorio de calculo PACE (Nonce) con el primer comando General Autenticate");
        try {
            byte[] nonce = new Tlv(new Tlv(res2.getData()).getValue()).getValue();
            X9ECParameters ecdhParameters = TeleTrusTNamedCurves.getByName(paceAlgorithmParam.getCurveName());
            ECPoint pointG = ecdhParameters.getG();
            ECCurve.Fp curve = (ECCurve.Fp) ecdhParameters.getCurve();
            Random rnd = new SecureRandom();
            byte[] x1 = new byte[curve.getFieldSize() / 8];
            rnd.nextBytes(x1);
            BigInteger prkIFDDH1 = new BigInteger(1, x1);
            ECPoint pukIFDDH1 = pointG.multiply(prkIFDDH1);
            byte[] pukIFDDH1UncompressedBytes = pukIFDDH1.getEncoded(false);
            CommandApdu comm3 = new GeneralAuthenticateApduCommand((byte) 16, new GeneralAuthenticateApduCommand.DataMapNonce(pukIFDDH1UncompressedBytes));
            ResponseApdu res3 = conn.transmit(comm3);
            if (!res3.isOk()) {
                throw new PaceException(res3.getStatusWord(), comm3, "Error mapeando el aleatorio de calculo PACE (nonce)");
            }
            JmcLogger.info(BcPaceChannelHelper.class.getName(), "openPaceChannel", "Mapeado el aleatorio de calculo PACE (Nonce) con el segundo comando General Autenticate");
            try {
                byte[] pukIccDh1 = unwrapEcKey(res3.getData());
                ECPoint sharedSecretH = byteArrayToECPoint(pukIccDh1, curve).multiply(prkIFDDH1);
                BigInteger ms = new BigInteger(1, getSecretNonce(pi, nonce, paceAlgorithm.getKeyLength(), this.cardAccess.getPaceDigestAlgorithm()));
                ECPoint gTemp = pointG.multiply(ms);
                ECPoint newPointG = gTemp.add(sharedSecretH);
                byte[] x2 = new byte[curve.getFieldSize() / 8];
                rnd.setSeed(rnd.nextLong());
                rnd.nextBytes(x2);
                BigInteger prkIFDDH2 = new BigInteger(1, x2);
                ECPoint pukIFDDH2 = newPointG.multiply(prkIFDDH2);
                byte[] pukIFDDH2UncompressedBytes = pukIFDDH2.getEncoded(false);
                CommandApdu comm4 = new GeneralAuthenticateApduCommand((byte) 16, new GeneralAuthenticateApduCommand.DataPerformKeyAgreement(pukIFDDH2UncompressedBytes));
                ResponseApdu res4 = conn.transmit(comm4);
                if (!res4.isOk()) {
                    throw new PaceException(res4.getStatusWord(), comm4, "Error en el tercer comando General Authenticate");
                }
                try {
                    byte[] pukIccDh2 = unwrapEcKey(res4.getData());
                    ECPoint y2FromNewG = byteArrayToECPoint(pukIccDh2, curve);
                    ECPoint.Fp sharedSecretK = (ECPoint.Fp) y2FromNewG.multiply(prkIFDDH2);
                    byte[] secretK = bigIntToByteArray(sharedSecretK.normalize().getXCoord().toBigInteger());
                    JmcLogger.info(BcPaceChannelHelper.class.getName(), "openPaceChannel", "Acordadas claves de canal con el tercer comando General Authenticate");
                    try {
                        byte[] kenc = padAndDigest(secretK, KENC_PADDING, this.cardAccess.getPaceDigestAlgorithm(), paceAlgorithm.getKeyLength());
                        try {
                            byte[] kmac = padAndDigest(secretK, KMAC_PADDING, this.cardAccess.getPaceDigestAlgorithm(), paceAlgorithm.getKeyLength());
                            byte[] pukIccDh2Descompressed = new byte[pukIccDh2.length - 1];
                            System.arraycopy(pukIccDh2, 1, pukIccDh2Descompressed, 0, pukIccDh2.length - 1);
                            byte[] data = HexUtils.concatenateByteArrays(MAC_PADDING_PRE, HexUtils.concatenateByteArrays(paceAlgorithm.getBytes(), HexUtils.concatenateByteArrays(MAC2_PADDING_POST, pukIccDh2Descompressed)));
                            try {
                                byte[] mac8bytes = this.cryptoHelper.doAesCmac(data, kmac);
                                CommandApdu comm5 = new GeneralAuthenticateApduCommand((byte) 0, new GeneralAuthenticateApduCommand.DataMutualAuthentication(mac8bytes));
                                ResponseApdu res5 = conn.transmit(comm5);
                                if (!res5.isOk()) {
                                    throw new InvalidCanOrMrzException(res5.getStatusWord(), comm5, "Error estableciendo el algoritmo del protocolo PACE (fallo en el General Authenticate)");
                                }
                                byte[] ssc = new byte[paceAlgorithm.getKeyLength() / 8];
                                Arrays.fill(ssc, (byte) 0);
                                JmcLogger.info(BcPaceChannelHelper.class.getName(), "openPaceChannel", "Canal Pace abierto");
                                JmcLogger.debug(BcPaceChannelHelper.class.getName(), "openPaceChannel", "Claves de canal Pace:\n  Kenc: " + HexUtils.hexify(kenc, true) + "\n  Kmac: " + HexUtils.hexify(kmac, true) + "\n  Ssc: " + HexUtils.hexify(ssc, true));
                                return new SecureMessaging(kenc, kmac, new byte[paceAlgorithm.getKeyLength() / 8], this.cryptoHelper);
                            } catch (InvalidKeyException | NoSuchAlgorithmException e) {
                                throw new PaceException("Error descifrando el 'nonce'", e);
                            }
                        } catch (IOException e2) {
                            throw new PaceException("Error obteniendo el 'kmac' a partir del CAN/MRZ/PIN", e2);
                        }
                    } catch (IOException e3) {
                        throw new PaceException("Error obteniendo el 'kenc' a partir del CAN/MRZ/PIN", e3);
                    }
                } catch (TlvException e4) {
                    throw new PaceException("Error obteniendo la clave efimera EC publica de la tarjeta", e4);
                }
            } catch (TlvException e5) {
                throw new PaceException("Error obteniendo la clave efimera EC publica de la tarjeta", e5);
            }
        } catch (TlvException e6) {
            throw new PaceException("El aleatorio de calculo PACE (Nonce) obtenido (" + HexUtils.hexify(res2.getData(), true) + ") no sigue el formato esperado", e6);
        }
    }

    protected byte[] padAndDigest(byte[] input, byte[] padding, DigestAlgorithm digestAlgorithm, int resultBitLength) throws IOException {
        byte[] dest = new byte[resultBitLength / 8];
        System.arraycopy(this.cryptoHelper.digest(digestAlgorithm, HexUtils.concatenateByteArrays(input, padding)), 0, dest, 0, resultBitLength / 8);
        return dest;
    }

    protected byte[] getSecretNonce(WirelessInitializer pi, byte[] nonce, int aesKeyLength, DigestAlgorithm digestAlgorithm) throws PaceException {
        try {
            byte[] sk = padAndDigest(pi.getBytes(), CAN_MRZ_PADDING, digestAlgorithm, aesKeyLength);
            try {
                return this.cryptoHelper.aesDecrypt(nonce, new byte[0], sk, CryptoHelper.BlockMode.CBC, CryptoHelper.Padding.NOPADDING);
            } catch (IOException e) {
                throw new PaceException("Error descifrando el 'nonce'", e);
            }
        } catch (IOException e2) {
            throw new PaceException("Error obteniendo el 'sk' a partir del " + pi.getPasswordType(), e2);
        }
    }

    protected static ECPoint byteArrayToECPoint(byte[] value, ECCurve.Fp curve) {
        byte[] x = new byte[(value.length - 1) / 2];
        byte[] y = new byte[(value.length - 1) / 2];
        if (value[0] != 4) {
            throw new IllegalArgumentException("No se ha encontrado un punto no comprimido");
        }
        System.arraycopy(value, 1, x, 0, (value.length - 1) / 2);
        System.arraycopy(value, 1 + ((value.length - 1) / 2), y, 0, (value.length - 1) / 2);
        ECFieldElement.Fp xE = (ECFieldElement.Fp) curve.fromBigInteger(new BigInteger(1, x));
        ECFieldElement.Fp yE = (ECFieldElement.Fp) curve.fromBigInteger(new BigInteger(1, y));
        return curve.createPoint(xE.toBigInteger(), yE.toBigInteger());
    }
}
