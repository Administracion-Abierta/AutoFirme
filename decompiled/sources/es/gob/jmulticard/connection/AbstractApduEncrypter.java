package es.gob.jmulticard.connection;

import es.gob.jmulticard.CryptoHelper;
import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.apdu.CommandApdu;
import es.gob.jmulticard.apdu.ResponseApdu;
import es.gob.jmulticard.asn1.Tlv;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/connection/AbstractApduEncrypter.class */
public abstract class AbstractApduEncrypter {
    protected static final byte TAG_DATA_TLV = -121;
    private static final byte TAG_LE_TLV = -105;
    private static final byte TLV_VALUE_PREFIX_TO_MAC = 1;
    private static final byte CLA_OF_PROTECTED_APDU = 12;
    private static final byte ISO7816_PADDING_PREFIX = -128;
    protected int paddingLength = 8;

    protected abstract byte[] encryptData(byte[] bArr, byte[] bArr2, byte[] bArr3, CryptoHelper cryptoHelper) throws IOException;

    protected abstract byte[] generateMac(byte[] bArr, byte[] bArr2, byte[] bArr3, CryptoHelper cryptoHelper) throws IOException;

    public abstract ResponseApdu decryptResponseApdu(ResponseApdu responseApdu, byte[] bArr, byte[] bArr2, byte[] bArr3, CryptoHelper cryptoHelper) throws IOException;

    public CipheredApdu protectAPDU(CommandApdu unprotectedApdu, byte[] keyCipher, byte[] keyMac, byte[] sendSequenceCounter, CryptoHelper cryptoHelper) throws IOException {
        byte cla = unprotectedApdu.getCla();
        byte ins = unprotectedApdu.getIns();
        byte p1 = unprotectedApdu.getP1();
        byte p2 = unprotectedApdu.getP2();
        byte[] data = unprotectedApdu.getData();
        Integer le = unprotectedApdu.getLe();
        byte[] tlvDataBytes = getDataTlv(data, keyCipher, sendSequenceCounter, cryptoHelper, this.paddingLength);
        byte[] completeDataBytes = getCompleteDataBytes(le, tlvDataBytes);
        byte cla2 = (byte) (cla | 12);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(addPadding7816(new byte[]{cla2, ins, p1, p2}, this.paddingLength));
        baos.write(completeDataBytes);
        byte[] encryptedDataPadded = addPadding7816(baos.toByteArray(), this.paddingLength);
        byte[] mac = generateMac(encryptedDataPadded, sendSequenceCounter, keyMac, cryptoHelper);
        return new CipheredApdu(cla2, ins, p1, p2, completeDataBytes, mac);
    }

    protected static byte[] addPadding7816(byte[] data, int size) {
        byte[] paddedData = new byte[((data.length / size) + 1) * size];
        System.arraycopy(data, 0, paddedData, 0, data.length);
        paddedData[data.length] = -128;
        for (int i = data.length + 1; i < paddedData.length; i++) {
            paddedData[i] = 0;
        }
        return paddedData;
    }

    protected static byte[] removePadding7816(byte[] paddedData) {
        for (int i = paddedData.length - 1; i >= 0; i--) {
            if (paddedData[i] == -128) {
                if (i == 0) {
                    return new byte[0];
                }
                return HexUtils.subArray(paddedData, 0, i);
            }
            if (paddedData[i] != 0) {
                return paddedData;
            }
        }
        return paddedData;
    }

    private static void wipeByteArray(byte[] inArray) {
        if (inArray != null) {
            Arrays.fill(inArray, (byte) 0);
        }
    }

    private static byte[] getCompleteDataBytes(Integer le, byte[] tlvDataBytes) {
        byte[] tlvLeBytes = new byte[0];
        if (le != null) {
            tlvLeBytes = new Tlv((byte) -105, new byte[]{le.byteValue()}).getBytes();
        }
        byte[] completeDataBytes = new byte[tlvDataBytes.length + tlvLeBytes.length];
        System.arraycopy(tlvDataBytes, 0, completeDataBytes, 0, tlvDataBytes.length);
        System.arraycopy(tlvLeBytes, 0, completeDataBytes, tlvDataBytes.length, tlvLeBytes.length);
        return completeDataBytes;
    }

    private byte[] getDataTlv(byte[] data, byte[] keyCipher, byte[] sendSequenceCounter, CryptoHelper cryptoHelper, int paddingSize) throws IOException {
        if (data != null && data.length > 0) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(1);
            byte[] paddedData = addPadding7816(data, paddingSize);
            baos.write(encryptData(paddedData, keyCipher, sendSequenceCounter, cryptoHelper));
            wipeByteArray(paddedData);
            wipeByteArray(data);
            return new Tlv((byte) -121, baos.toByteArray()).getBytes();
        }
        return new byte[0];
    }
}
