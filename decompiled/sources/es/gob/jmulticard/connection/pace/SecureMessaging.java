package es.gob.jmulticard.connection.pace;

import es.gob.jmulticard.CryptoHelper;
import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.JmcLogger;
import es.gob.jmulticard.apdu.CommandApdu;
import es.gob.jmulticard.apdu.ResponseApdu;
import es.gob.jmulticard.asn1.Tlv;
import es.gob.jmulticard.asn1.TlvException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/connection/pace/SecureMessaging.class */
public final class SecureMessaging {
    private final byte[] kenc;
    private final byte[] kmac;
    private final byte[] ssc;
    private final CryptoHelper cryptoHelper;
    private static final int BLOCK_SIZE = 16;

    public SecureMessaging(byte[] ksenc, byte[] ksmac, byte[] initialSSC, CryptoHelper ch) {
        this.cryptoHelper = ch;
        this.kenc = (byte[]) ksenc.clone();
        this.kmac = (byte[]) ksmac.clone();
        this.ssc = (byte[]) initialSSC.clone();
    }

    public CommandApdu wrap(CommandApdu capdu) throws SecureMessagingException {
        byte lc = 0;
        DO97 do97 = null;
        DO87 do87 = null;
        incrementAtIndex(this.ssc);
        byte[] header = new byte[4];
        System.arraycopy(capdu.getBytes(), 0, header, 0, 4);
        header[0] = (byte) (header[0] | 12);
        if (getAPDUStructure(capdu) == 3 || getAPDUStructure(capdu) == 4) {
            do87 = buildDO87((byte[]) capdu.getData().clone());
            lc = (byte) (0 + do87.getEncoded().length);
        }
        if (getAPDUStructure(capdu) == 2 || getAPDUStructure(capdu) == 4) {
            do97 = buildDO97(capdu.getLe().intValue());
            lc = (byte) (lc + do97.getEncoded().length);
        }
        DO8E do8E = buildDO8E(header, do87, do97);
        byte lc2 = (byte) (lc + do8E.getEncoded().length);
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        try {
            bOut.write(header);
            bOut.write(lc2);
            if (do87 != null) {
                bOut.write(do87.getEncoded());
            }
            if (do97 != null) {
                bOut.write(do97.getEncoded());
            }
            bOut.write(do8E.getEncoded());
            bOut.write(0);
            return new CommandApdu(bOut.toByteArray());
        } catch (IOException e) {
            throw new SecureMessagingException(e);
        }
    }

    public ResponseApdu unwrap(ResponseApdu responseApduEncrypted) throws SecureMessagingException {
        byte[] unwrappedAPDUBytes;
        DO87 do87 = null;
        DO99 do99 = null;
        DO8E do8E = null;
        incrementAtIndex(this.ssc);
        int pointer = 0;
        byte[] responseApduBytes = responseApduEncrypted.getData();
        byte[] subArray = new byte[responseApduBytes.length];
        while (pointer < responseApduBytes.length) {
            System.arraycopy(responseApduBytes, pointer, subArray, 0, responseApduBytes.length - pointer);
            try {
                byte[] encodedBytes = new Tlv(subArray).getBytes();
                switch (encodedBytes[0]) {
                    case -121:
                        do87 = new DO87(encodedBytes);
                        break;
                    case -114:
                        do8E = new DO8E(encodedBytes);
                        break;
                    case -103:
                        do99 = new DO99(encodedBytes);
                        break;
                    default:
                        JmcLogger.warning("Encontrada estructura desconocida en la APDU protegida: " + HexUtils.hexify(encodedBytes, false));
                        break;
                }
                pointer += encodedBytes.length;
            } catch (TlvException e1) {
                throw new SecureMessagingException("Los datos de la APDU protegida no forman un TLV valido", e1);
            }
        }
        if (do99 == null || do8E == null) {
            throw new SecureMessagingException("Error desempaquetando el mensaje seguro, DO99 o DO8E no encontrados en la APDU de respuesta: " + HexUtils.hexify(responseApduBytes, true));
        }
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        if (do87 != null) {
            try {
                bout.write(do87.getEncoded());
            } catch (IOException e) {
                throw new SecureMessagingException(e);
            }
        }
        bout.write(do99.getEncoded());
        try {
            byte[] cc = getMac(bout.toByteArray(), this.ssc, this.kmac);
            byte[] do8eData = do8E.getData();
            if (!Arrays.equals(cc, do8eData)) {
                throw new SecureMessagingException("Checksum incorrecto (CC Calculado = " + HexUtils.hexify(cc, false) + ", CC en DO8E = " + HexUtils.hexify(do8eData, false) + ")");
            }
            if (do87 != null) {
                byte[] do87Data = do87.getData();
                try {
                    byte[] data = this.cryptoHelper.aesDecrypt(do87Data, this.cryptoHelper.aesEncrypt(this.ssc, null, this.kenc, CryptoHelper.BlockMode.ECB, CryptoHelper.Padding.NOPADDING), this.kenc, CryptoHelper.BlockMode.CBC, CryptoHelper.Padding.ISO7816_4PADDING);
                    unwrappedAPDUBytes = new byte[data.length + 2];
                    System.arraycopy(data, 0, unwrappedAPDUBytes, 0, data.length);
                    byte[] do99Data = do99.getData();
                    System.arraycopy(do99Data, 0, unwrappedAPDUBytes, data.length, do99Data.length);
                } catch (IOException e2) {
                    throw new SecureMessagingException(e2);
                }
            } else {
                unwrappedAPDUBytes = (byte[]) do99.getData().clone();
            }
            return new ResponseApdu(unwrappedAPDUBytes);
        } catch (InvalidKeyException | NoSuchAlgorithmException e3) {
            throw new SecureMessagingException("Error calculando el CMAC", e3);
        }
    }

    private DO87 buildDO87(byte[] data) throws SecureMessagingException {
        try {
            byte[] encData = this.cryptoHelper.aesEncrypt(data, this.cryptoHelper.aesEncrypt(this.ssc, null, this.kenc, CryptoHelper.BlockMode.ECB, CryptoHelper.Padding.NOPADDING), this.kenc, CryptoHelper.BlockMode.CBC, CryptoHelper.Padding.ISO7816_4PADDING);
            return new DO87(encData);
        } catch (IOException e) {
            throw new SecureMessagingException(e);
        }
    }

    private DO8E buildDO8E(byte[] header, DO87 do87, DO97 do97) throws SecureMessagingException {
        ByteArrayOutputStream m = new ByteArrayOutputStream();
        try {
            if (do87 != null || do97 != null) {
                m.write(addPadding(header));
            } else {
                m.write(header);
            }
            if (do87 != null) {
                m.write(do87.getEncoded());
            }
            if (do97 != null) {
                m.write(do97.getEncoded());
            }
            try {
                return new DO8E(getMac(m.toByteArray(), this.ssc, this.kmac));
            } catch (InvalidKeyException | NoSuchAlgorithmException e) {
                throw new SecureMessagingException("Error calculando el CMAC", e);
            }
        } catch (IOException e2) {
            throw new SecureMessagingException(e2);
        }
    }

    private static DO97 buildDO97(int le) {
        return new DO97(le);
    }

    private static byte getAPDUStructure(CommandApdu capdu) {
        byte[] cardcmd = capdu.getBytes();
        if (cardcmd.length == 4) {
            return (byte) 1;
        }
        if (cardcmd.length == 5) {
            return (byte) 2;
        }
        if (cardcmd.length == 5 + (cardcmd[4] & 255) && cardcmd[4] != 0) {
            return (byte) 3;
        }
        if (cardcmd.length == 6 + (cardcmd[4] & 255) && cardcmd[4] != 0) {
            return (byte) 4;
        }
        if (cardcmd.length == 7 && cardcmd[4] == 0) {
            return (byte) 5;
        }
        if (cardcmd.length == 7 + ((cardcmd[5] & 255) * 256) + (cardcmd[6] & 255) && cardcmd[4] == 0 && (cardcmd[5] != 0 || cardcmd[6] != 0)) {
            return (byte) 6;
        }
        if (cardcmd.length != 9 + ((cardcmd[5] & 255) * 256) + (cardcmd[6] & 255) || cardcmd[4] != 0) {
            return (byte) 0;
        }
        if (cardcmd[5] != 0 || cardcmd[6] != 0) {
            return (byte) 7;
        }
        return (byte) 0;
    }

    private static void incrementAtIndex(byte[] array) {
        byte[] result = new BigInteger(1, array).add(BigInteger.ONE).toByteArray();
        if (result.length > array.length) {
            Arrays.fill(array, (byte) 0);
            return;
        }
        int lengthA = array.length;
        int lengthR = result.length;
        for (int i = 0; i < lengthR; i++) {
            array[(lengthA - 1) - i] = result[(lengthR - 1) - i];
        }
    }

    private byte[] getMac(byte[] data, byte[] ssCounter, byte[] keyBytes) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] n = new byte[ssCounter.length + data.length];
        System.arraycopy(ssCounter, 0, n, 0, ssCounter.length);
        System.arraycopy(data, 0, n, ssCounter.length, data.length);
        return this.cryptoHelper.doAesCmac(addPadding(n), keyBytes);
    }

    private static byte[] addPadding(byte[] data) {
        int len = data.length;
        int nLen = ((len / 16) + 1) * 16;
        byte[] in = new byte[nLen];
        System.arraycopy(data, 0, in, 0, data.length);
        in[len] = -128;
        while (true) {
            len++;
            if (len < in.length) {
                in[len] = 0;
            } else {
                return in;
            }
        }
    }
}
