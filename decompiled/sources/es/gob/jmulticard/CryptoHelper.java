package es.gob.jmulticard;

import es.gob.jmulticard.apdu.iso7816four.pace.PaceChat;
import es.gob.jmulticard.asn1.Tlv;
import es.gob.jmulticard.asn1.TlvException;
import es.gob.jmulticard.asn1.icao.CardAccess;
import es.gob.jmulticard.card.icao.IcaoException;
import es.gob.jmulticard.card.icao.WirelessInitializer;
import es.gob.jmulticard.connection.ApduConnection;
import es.gob.jmulticard.connection.ApduConnectionException;
import es.gob.jmulticard.connection.pace.SecureMessaging;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/CryptoHelper.class */
public abstract class CryptoHelper {
    private static final byte PKCS1_BLOCK_TYPE = 1;
    private static final byte PKCS1_FILL = -1;
    private static final byte PKCS1_DELIMIT = 0;

    /* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/CryptoHelper$BlockMode.class */
    public enum BlockMode {
        CBC,
        ECB
    }

    public abstract byte[] digest(DigestAlgorithm digestAlgorithm, byte[] bArr) throws IOException;

    public abstract byte[] desedeEncrypt(byte[] bArr, byte[] bArr2) throws IOException;

    public abstract byte[] desedeDecrypt(byte[] bArr, byte[] bArr2) throws IOException;

    public abstract byte[] desEncrypt(byte[] bArr, byte[] bArr2) throws IOException;

    public abstract byte[] desDecrypt(byte[] bArr, byte[] bArr2) throws IOException;

    public abstract byte[] aesDecrypt(byte[] bArr, byte[] bArr2, byte[] bArr3, BlockMode blockMode, Padding padding) throws IOException;

    public abstract byte[] aesEncrypt(byte[] bArr, byte[] bArr2, byte[] bArr3, BlockMode blockMode, Padding padding) throws IOException;

    public abstract byte[] rsaDecrypt(byte[] bArr, RSAKey rSAKey) throws IOException;

    public abstract byte[] rsaEncrypt(byte[] bArr, RSAKey rSAKey) throws IOException;

    public abstract byte[] generateRandomBytes(int i) throws IOException;

    public abstract KeyPair generateEcKeyPair(EcCurve ecCurve) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException;

    public abstract byte[] doAesCmac(byte[] bArr, byte[] bArr2) throws NoSuchAlgorithmException, InvalidKeyException;

    public abstract AlgorithmParameterSpec getEcPoint(byte[] bArr, byte[] bArr2, EcCurve ecCurve);

    public abstract byte[] getCmsSignatureSignedContent(byte[] bArr) throws IOException;

    public abstract X509Certificate[] validateCmsSignature(byte[] bArr) throws SignatureException, IOException, CertificateException;

    public abstract RSAPublicKey getRsaPublicKey(X509Certificate x509Certificate);

    public abstract PaceChannelHelper getPaceChannelHelper(CardAccess cardAccess, PaceChat paceChat);

    /* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/CryptoHelper$Padding.class */
    public enum Padding {
        NOPADDING("NOPADDING"),
        ISO7816_4PADDING("ISO7816-4Padding");

        private final String algName;

        Padding(String alg) {
            this.algName = alg;
        }

        @Override // java.lang.Enum
        public String toString() {
            return this.algName;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/CryptoHelper$EcCurve.class */
    public enum EcCurve {
        BRAINPOOL_P256_R1("brainpoolp256r1");

        private final String name;

        EcCurve(String n) {
            this.name = n;
        }

        @Override // java.lang.Enum
        public String toString() {
            return this.name;
        }
    }

    public static final byte[] addPkcs1PaddingForPrivateKeyOperation(byte[] inByteArray, int keySize) throws IOException {
        if (inByteArray == null) {
            throw new IllegalArgumentException("Los datos de entrada no pueden ser nulos");
        }
        int len = keySize / 8;
        if (inByteArray.length > len - 3) {
            throw new IllegalArgumentException("Los datos son demasiado grandes para el valor de clave indicado: " + inByteArray.length + " > " + len + "-3");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream(len);
        baos.write(0);
        baos.write(1);
        while (baos.size() < len - (1 + inByteArray.length)) {
            baos.write(-1);
        }
        baos.write(0);
        baos.write(inByteArray);
        return baos.toByteArray();
    }

    public static X509Certificate generateCertificate(byte[] encoded) throws CertificateException {
        return generateCertificate(new ByteArrayInputStream(encoded));
    }

    public static X509Certificate generateCertificate(InputStream is) throws CertificateException {
        CertificateFactory cf = CertificateFactory.getInstance(XMLX509Certificate.JCA_CERT_ID);
        return (X509Certificate) cf.generateCertificate(is);
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/CryptoHelper$PaceChannelHelper.class */
    public static abstract class PaceChannelHelper {
        protected static final byte[] CAN_MRZ_PADDING = {0, 0, 0, 3};
        protected static final byte[] KENC_PADDING = {0, 0, 0, 1};
        protected static final byte[] KMAC_PADDING = {0, 0, 0, 2};
        protected static final byte[] MAC_PADDING_PRE = {127, 73, 79, 6};
        protected static final byte[] MAC2_PADDING_POST = {-122, 65, 4};
        protected final CryptoHelper cryptoHelper;

        public abstract SecureMessaging openPaceChannel(byte b, WirelessInitializer wirelessInitializer, ApduConnection apduConnection) throws ApduConnectionException, IcaoException;

        protected PaceChannelHelper(CryptoHelper ch) {
            this.cryptoHelper = ch;
        }

        protected static byte[] bigIntToByteArray(BigInteger bi) {
            byte[] temp = bi.toByteArray();
            if (temp[0] == 0) {
                byte[] returnbytes = new byte[temp.length - 1];
                System.arraycopy(temp, 1, returnbytes, 0, returnbytes.length);
                return returnbytes;
            }
            return temp;
        }

        protected static byte[] unwrapEcKey(byte[] key) throws TlvException {
            return new Tlv(new Tlv(key).getValue()).getValue();
        }
    }
}
