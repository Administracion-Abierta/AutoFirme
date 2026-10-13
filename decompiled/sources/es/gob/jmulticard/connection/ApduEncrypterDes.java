package es.gob.jmulticard.connection;

import es.gob.jmulticard.CryptoHelper;
import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.apdu.ResponseApdu;
import es.gob.jmulticard.asn1.bertlv.BerTlv;
import es.gob.jmulticard.connection.cwa14890.InvalidCryptographicChecksumException;
import es.gob.jmulticard.connection.cwa14890.SecureChannelException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/connection/ApduEncrypterDes.class */
public class ApduEncrypterDes extends AbstractApduEncrypter {
    private static final byte TAG_SW_TLV = -103;
    private static final byte TAG_MAC_TLV = -114;
    private static final byte MAC_LENGTH_4 = 4;

    public ApduEncrypterDes() {
        this.paddingLength = 8;
    }

    protected int getMacLength() {
        return 4;
    }

    @Override // es.gob.jmulticard.connection.AbstractApduEncrypter
    protected byte[] encryptData(byte[] data, byte[] key, byte[] ssc, CryptoHelper cryptoHelper) throws IOException {
        return cryptoHelper.desedeEncrypt(data, key);
    }

    @Override // es.gob.jmulticard.connection.AbstractApduEncrypter
    protected byte[] generateMac(byte[] dataPadded, byte[] ssc, byte[] kMac, CryptoHelper cryptoHelper) throws IOException {
        byte[] keyDesBytes = new byte[8];
        System.arraycopy(kMac, 0, keyDesBytes, 0, 8);
        byte[] tmpData = cryptoHelper.desEncrypt(ssc, keyDesBytes);
        int i = 0;
        while (i < dataPadded.length - 8) {
            tmpData = cryptoHelper.desEncrypt(HexUtils.xor(tmpData, HexUtils.subArray(dataPadded, i, 8)), keyDesBytes);
            i += 8;
        }
        byte[] keyTdesBytes = new byte[24];
        System.arraycopy(kMac, 0, keyTdesBytes, 0, 16);
        System.arraycopy(kMac, 0, keyTdesBytes, 16, 8);
        return HexUtils.subArray(cryptoHelper.desedeEncrypt(HexUtils.xor(tmpData, HexUtils.subArray(dataPadded, i, 8)), keyTdesBytes), 0, getMacLength());
    }

    @Override // es.gob.jmulticard.connection.AbstractApduEncrypter
    public ResponseApdu decryptResponseApdu(ResponseApdu responseApdu, byte[] keyCipher, byte[] ssc, byte[] kMac, CryptoHelper cryptoHelper) throws IOException {
        if (!responseApdu.isOk()) {
            return new ResponseApdu(responseApdu.getStatusWord().getBytes(), responseApdu.getBytes());
        }
        ByteArrayInputStream recordOfTlvs = new ByteArrayInputStream(responseApdu.getData());
        BerTlv dataTlv = null;
        BerTlv swTlv = null;
        BerTlv macTlv = null;
        try {
            BerTlv tlv = BerTlv.createInstance(recordOfTlvs);
            if (tlv.getTag() == -121) {
                dataTlv = tlv;
                tlv = BerTlv.createInstance(recordOfTlvs);
            }
            if (tlv.getTag() == TAG_SW_TLV) {
                swTlv = tlv;
                tlv = BerTlv.createInstance(recordOfTlvs);
            }
            if (tlv.getTag() == TAG_MAC_TLV) {
                macTlv = tlv;
            }
            if (macTlv == null) {
                throw new SecureChannelException("No se ha encontrado el TLV del MAC en la APDU");
            }
            if (swTlv == null) {
                throw new SecureChannelException("No se ha encontrado el TLV del StatusWord en la APDU cifrada");
            }
            int tlvsLenght = (dataTlv != null ? 2 + (dataTlv.getValue().length / 128) + dataTlv.getValue().length : 0) + 1 + 1 + swTlv.getValue().length;
            verifyMac(HexUtils.subArray(responseApdu.getData(), 0, tlvsLenght), macTlv.getValue(), ssc, kMac, cryptoHelper);
            if (dataTlv == null) {
                return new ResponseApdu(swTlv.getValue());
            }
            byte[] decryptedData = removePadding7816(cryptoHelper.desedeDecrypt(HexUtils.subArray(dataTlv.getValue(), 1, dataTlv.getValue().length - 1), keyCipher));
            byte[] responseApduBytes = new byte[decryptedData.length + swTlv.getValue().length];
            System.arraycopy(decryptedData, 0, responseApduBytes, 0, decryptedData.length);
            System.arraycopy(swTlv.getValue(), 0, responseApduBytes, decryptedData.length, swTlv.getValue().length);
            return new ResponseApdu(responseApduBytes, responseApdu.getBytes());
        } catch (NegativeArraySizeException e) {
            throw new ApduConnectionException("Error en el formato de la respuesta remitida por el canal seguro", e);
        }
    }

    private void verifyMac(byte[] verificableData, byte[] macTlvBytes, byte[] ssc, byte[] kMac, CryptoHelper cryptoHelper) {
        try {
            byte[] calculatedMac = generateMac(addPadding7816(verificableData, this.paddingLength), ssc, kMac, cryptoHelper);
            if (!HexUtils.arrayEquals(macTlvBytes, calculatedMac)) {
                throw new InvalidCryptographicChecksumException();
            }
        } catch (IOException e) {
            throw new SecurityException("No se pudo calcular el MAC teorico de la respuesta de la tarjeta para su verificacion", e);
        }
    }
}
