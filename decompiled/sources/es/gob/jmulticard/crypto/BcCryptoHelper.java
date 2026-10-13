package es.gob.jmulticard.crypto;

import es.gob.jmulticard.CryptoHelper;
import es.gob.jmulticard.DigestAlgorithm;
import es.gob.jmulticard.apdu.iso7816four.pace.PaceChat;
import es.gob.jmulticard.asn1.icao.CardAccess;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECField;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.DefaultCMSSignatureAlgorithmNameGenerator;
import org.bouncycastle.cms.SignerId;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DefaultBufferedBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.ISO7816d4Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.prng.DigestRandomGenerator;
import org.bouncycastle.crypto.prng.RandomGenerator;
import org.bouncycastle.jcajce.provider.asymmetric.ec.KeyPairGeneratorSpi;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveGenParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/crypto/BcCryptoHelper.class */
public final class BcCryptoHelper extends CryptoHelper {
    private static final Logger LOGGER = Logger.getLogger(BcCryptoHelper.class.getName());
    private CryptoHelper.PaceChannelHelper paceChannelHelper = null;

    @Override // es.gob.jmulticard.CryptoHelper
    public byte[] digest(DigestAlgorithm algorithm, byte[] data) throws IOException {
        byte[] out;
        if (algorithm == null) {
            throw new IllegalArgumentException("El algoritmo de huella digital no puede ser nulo");
        }
        if (data == null) {
            throw new IllegalArgumentException("Los datos para realizar la huella digital no pueden ser nulos");
        }
        switch (algorithm) {
            case SHA512:
                SHA512Digest digest512 = new SHA512Digest();
                digest512.update(data, 0, data.length);
                out = new byte[digest512.getDigestSize()];
                digest512.doFinal(out, 0);
                break;
            case SHA384:
                SHA384Digest digest384 = new SHA384Digest();
                digest384.update(data, 0, data.length);
                out = new byte[digest384.getDigestSize()];
                digest384.doFinal(out, 0);
                break;
            case SHA256:
                SHA256Digest digest256 = new SHA256Digest();
                digest256.update(data, 0, data.length);
                out = new byte[digest256.getDigestSize()];
                digest256.doFinal(out, 0);
                break;
            case SHA1:
                SHA1Digest digest = new SHA1Digest();
                digest.update(data, 0, data.length);
                out = new byte[digest.getDigestSize()];
                digest.doFinal(out, 0);
                break;
            default:
                throw new IOException("No se soporta el algoritmo de huella digital indicado: " + algorithm);
        }
        return out;
    }

    private static byte[] doDesede(byte[] data, byte[] key, boolean forEncryption) throws IOException {
        BufferedBlockCipher cipher = new DefaultBufferedBlockCipher(CBCBlockCipher.newInstance(new DESedeEngine()));
        cipher.init(forEncryption, new KeyParameter(prepareDesedeKey(key)));
        byte[] result = new byte[cipher.getOutputSize(data.length)];
        int tam = cipher.processBytes(data, 0, data.length, result, 0);
        try {
            cipher.doFinal(result, tam);
            return result;
        } catch (IllegalStateException | DataLengthException | InvalidCipherTextException e) {
            throw new IOException("Error en el cifrado o descifrado 3DES", e);
        }
    }

    @Override // es.gob.jmulticard.CryptoHelper
    public byte[] desedeEncrypt(byte[] data, byte[] rawKey) throws IOException {
        return doDesede(data, rawKey, true);
    }

    @Override // es.gob.jmulticard.CryptoHelper
    public byte[] desedeDecrypt(byte[] data, byte[] rawKey) throws IOException {
        return doDesede(data, rawKey, false);
    }

    private static byte[] prepareDesedeKey(byte[] key) {
        if (key == null) {
            throw new IllegalArgumentException("La clave 3DES no puede ser nula");
        }
        if (key.length == 24) {
            return key;
        }
        if (key.length == 16) {
            byte[] newKey = new byte[24];
            System.arraycopy(key, 0, newKey, 0, 16);
            System.arraycopy(key, 0, newKey, 16, 8);
            return newKey;
        }
        throw new IllegalArgumentException("Longitud de clave invalida, se esperaba 16 o 24, pero se indico " + key.length);
    }

    private static byte[] doDes(byte[] data, byte[] key, boolean forEncryption) throws IOException {
        BlockCipher engine = new DESEngine();
        BufferedBlockCipher cipher = new DefaultBufferedBlockCipher(engine);
        cipher.init(forEncryption, new KeyParameter(key));
        byte[] cipherText = new byte[cipher.getOutputSize(data.length)];
        int outputLen = cipher.processBytes(data, 0, data.length, cipherText, 0);
        try {
            cipher.doFinal(cipherText, outputLen);
            return cipherText;
        } catch (IllegalStateException | DataLengthException | InvalidCipherTextException e) {
            throw new IOException("Error el el cifrado / descifrado DES", e);
        }
    }

    @Override // es.gob.jmulticard.CryptoHelper
    public byte[] desEncrypt(byte[] data, byte[] key) throws IOException {
        return doDes(data, key, true);
    }

    @Override // es.gob.jmulticard.CryptoHelper
    public byte[] desDecrypt(byte[] data, byte[] key) throws IOException {
        return doDes(data, key, false);
    }

    private static byte[] doRsa(byte[] data, RSAKey key, boolean forEncryption) throws IOException {
        BigInteger publicExponent;
        boolean isPrivateKey = key instanceof RSAPrivateKey;
        BigInteger modulus = key.getModulus();
        if (isPrivateKey) {
            publicExponent = ((RSAPrivateKey) key).getPrivateExponent();
        } else {
            publicExponent = ((RSAPublicKey) key).getPublicExponent();
        }
        CipherParameters akp = new RSAKeyParameters(isPrivateKey, modulus, publicExponent);
        AsymmetricBlockCipher cipher = new RSAEngine();
        cipher.init(forEncryption, akp);
        try {
            return cipher.processBlock(data, 0, data.length);
        } catch (InvalidCipherTextException e) {
            throw new IOException("Error en el cifrado/descifrado RSA", e);
        }
    }

    @Override // es.gob.jmulticard.CryptoHelper
    public byte[] rsaDecrypt(byte[] cipheredData, RSAKey key) throws IOException {
        return doRsa(cipheredData, key, false);
    }

    @Override // es.gob.jmulticard.CryptoHelper
    public byte[] rsaEncrypt(byte[] data, RSAKey key) throws IOException {
        return doRsa(data, key, true);
    }

    @Override // es.gob.jmulticard.CryptoHelper
    public byte[] generateRandomBytes(int numBytes) {
        RandomGenerator sr = new DigestRandomGenerator(new SHA1Digest());
        byte[] ret = new byte[numBytes];
        sr.nextBytes(ret);
        return ret;
    }

    private static byte[] aesEncryptSingleBlock(byte[] key, byte[] dataBlock) {
        CipherParameters encKey = new KeyParameter(key);
        BlockCipher cipher = AESEngine.newInstance();
        cipher.init(true, encKey);
        byte[] s = new byte[cipher.getBlockSize()];
        cipher.processBlock(dataBlock, 0, s, 0);
        return s;
    }

    private static byte[] doAes(byte[] data, byte[] iv, byte[] aesKey, BlockCipherPadding padding, boolean forEncryption) throws InvalidCipherTextException, IOException {
        byte[] ivector;
        BlockCipher engine = AESEngine.newInstance();
        if (iv == null) {
            ivector = null;
        } else if (iv.length == 0) {
            LOGGER.warning("Se usara un vector de inicializacion AES vacio");
            ivector = new byte[engine.getBlockSize()];
        } else {
            ivector = iv;
        }
        ParametersWithIV parameterIV = new ParametersWithIV(new KeyParameter(aesKey), ivector);
        BufferedBlockCipher aesCipher = padding != null ? new PaddedBufferedBlockCipher(CBCBlockCipher.newInstance(AESEngine.newInstance()), padding) : new DefaultBufferedBlockCipher(CBCBlockCipher.newInstance(engine));
        aesCipher.init(forEncryption, parameterIV);
        byte[] buf = new byte[16];
        byte[] obuf = new byte[512];
        InputStream bin = new ByteArrayInputStream(data);
        Throwable th = null;
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            Throwable th2 = null;
            while (true) {
                try {
                    int noBytesRead = bin.read(buf);
                    if (noBytesRead < 0) {
                        break;
                    }
                    int noBytesProcessed = aesCipher.processBytes(buf, 0, noBytesRead, obuf, 0);
                    bout.write(obuf, 0, noBytesProcessed);
                } catch (Throwable th3) {
                    if (bout != null) {
                        if (0 != 0) {
                            try {
                                bout.close();
                            } catch (Throwable th4) {
                                th2.addSuppressed(th4);
                            }
                        } else {
                            bout.close();
                        }
                    }
                    throw th3;
                }
            }
            int noBytesProcessed2 = aesCipher.doFinal(obuf, 0);
            bout.write(obuf, 0, noBytesProcessed2);
            bout.flush();
            byte[] byteArray = bout.toByteArray();
            if (bout != null) {
                if (0 != 0) {
                    try {
                        bout.close();
                    } catch (Throwable th5) {
                        th2.addSuppressed(th5);
                    }
                } else {
                    bout.close();
                }
            }
            if (bin != null) {
                if (0 != 0) {
                    try {
                        bin.close();
                    } catch (Throwable th6) {
                        th.addSuppressed(th6);
                    }
                } else {
                    bin.close();
                }
            }
            return byteArray;
        } catch (Throwable th7) {
            if (bin != null) {
                if (0 != 0) {
                    try {
                        bin.close();
                    } catch (Throwable th8) {
                        th.addSuppressed(th8);
                    }
                } else {
                    bin.close();
                }
            }
            throw th7;
        }
    }

    @Override // es.gob.jmulticard.CryptoHelper
    public byte[] aesDecrypt(byte[] data, byte[] iv, byte[] key, CryptoHelper.BlockMode blockMode, CryptoHelper.Padding padding) throws IOException {
        BlockCipherPadding bcPadding;
        if (data == null) {
            throw new IllegalArgumentException("Los datos a cifrar no pueden ser nulos");
        }
        if (key == null) {
            throw new IllegalArgumentException("La clave de cifrado no puede ser nula");
        }
        switch (padding) {
            case NOPADDING:
                bcPadding = null;
                break;
            case ISO7816_4PADDING:
                bcPadding = new ISO7816d4Padding();
                break;
            default:
                throw new IOException("Algoritmo de relleno no soportado para AES: " + padding);
        }
        try {
            return doAes(data, iv, key, bcPadding, false);
        } catch (IllegalStateException | DataLengthException | InvalidCipherTextException e) {
            throw new IOException("Error en el descifrado AES", e);
        }
    }

    @Override // es.gob.jmulticard.CryptoHelper
    public byte[] aesEncrypt(byte[] data, byte[] iv, byte[] key, CryptoHelper.BlockMode blockMode, CryptoHelper.Padding padding) throws IOException {
        BlockCipherPadding bcPadding;
        if (data == null) {
            throw new IllegalArgumentException("Los datos a cifrar no pueden ser nulos");
        }
        if (key == null) {
            throw new IllegalArgumentException("La clave de cifrado no puede ser nula");
        }
        if (CryptoHelper.BlockMode.ECB.equals(blockMode) && CryptoHelper.Padding.NOPADDING.equals(padding) && data.length == 16) {
            return aesEncryptSingleBlock(key, data);
        }
        switch (padding) {
            case NOPADDING:
                bcPadding = null;
                break;
            case ISO7816_4PADDING:
                bcPadding = new ISO7816d4Padding();
                break;
            default:
                throw new IOException("Algoritmo de relleno no soportado para AES: " + padding);
        }
        try {
            return doAes(data, iv, key, bcPadding, true);
        } catch (IOException | IllegalStateException | DataLengthException | InvalidCipherTextException e) {
            throw new IOException("Error en el cifrado AES", e);
        }
    }

    @Override // es.gob.jmulticard.CryptoHelper
    public KeyPair generateEcKeyPair(CryptoHelper.EcCurve curveName) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        KeyPairGenerator kpg = new KeyPairGeneratorSpi.ECDH();
        AlgorithmParameterSpec parameterSpec = new ECNamedCurveGenParameterSpec(curveName.toString());
        kpg.initialize(parameterSpec);
        return kpg.generateKeyPair();
    }

    @Override // es.gob.jmulticard.CryptoHelper
    public byte[] doAesCmac(byte[] data, byte[] key) {
        BlockCipher cipher = AESEngine.newInstance();
        Mac mac = new CMac(cipher, 64);
        KeyParameter keyP = new KeyParameter(key);
        mac.init(keyP);
        mac.update(data, 0, data.length);
        byte[] out = new byte[mac.getMacSize()];
        mac.doFinal(out, 0);
        return out;
    }

    @Override // es.gob.jmulticard.CryptoHelper
    public AlgorithmParameterSpec getEcPoint(byte[] nonceS, byte[] sharedSecretH, CryptoHelper.EcCurve curveName) {
        AlgorithmParameterSpec ecParams = ECNamedCurveTable.getParameterSpec(curveName.toString());
        BigInteger affineX = os2i(sharedSecretH);
        BigInteger affineY = computeAffineY(affineX, (ECParameterSpec) ecParams);
        ECPoint sharedSecretPointH = new ECPoint(affineX, affineY);
        return mapNonceGmWithEcdh(os2i(nonceS), sharedSecretPointH, (ECParameterSpec) ecParams);
    }

    private static BigInteger os2i(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException();
        }
        return os2i(bytes, 0, bytes.length);
    }

    private static BigInteger os2i(byte[] bytes, int offset, int length) {
        if (bytes == null) {
            throw new IllegalArgumentException("El Octet String no puede ser nulo");
        }
        BigInteger result = BigInteger.ZERO;
        BigInteger base = BigInteger.valueOf(256L);
        for (int i = offset; i < offset + length; i++) {
            result = result.multiply(base).add(BigInteger.valueOf(bytes[i] & 255));
        }
        return result;
    }

    private static BigInteger computeAffineY(BigInteger affineX, ECParameterSpec params) {
        ECCurve bcCurve = toBouncyCastleECCurve(params);
        ECFieldElement a = bcCurve.getA();
        ECFieldElement b = bcCurve.getB();
        ECFieldElement x = bcCurve.fromBigInteger(affineX);
        ECFieldElement y = x.multiply(x).add(a).multiply(x).add(b).sqrt();
        return y.toBigInteger();
    }

    private static ECCurve toBouncyCastleECCurve(ECParameterSpec params) {
        EllipticCurve curve = params.getCurve();
        ECField field = curve.getField();
        if (!(field instanceof ECFieldFp)) {
            throw new IllegalArgumentException("Solo se soporta 'ECFieldFp' y se proporciono '" + field.getClass().getCanonicalName() + OperatorName.SHOW_TEXT_LINE);
        }
        int coFactor = params.getCofactor();
        BigInteger order = params.getOrder();
        BigInteger a = curve.getA();
        BigInteger b = curve.getB();
        BigInteger p = getPrime(params);
        return new ECCurve.Fp(p, a, b, order, BigInteger.valueOf(coFactor));
    }

    private static BigInteger getPrime(ECParameterSpec params) {
        if (params == null) {
            throw new IllegalArgumentException("Los parametros no pueden ser nulos");
        }
        EllipticCurve curve = params.getCurve();
        ECField field = curve.getField();
        if (!(field instanceof ECFieldFp)) {
            throw new IllegalStateException("Solo se soporta 'ECFieldFp' y se proporciono  " + field.getClass().getCanonicalName());
        }
        return ((ECFieldFp) field).getP();
    }

    private static ECParameterSpec mapNonceGmWithEcdh(BigInteger nonceS, ECPoint sharedSecretPointH, ECParameterSpec params) {
        ECPoint generator = params.getGenerator();
        EllipticCurve curve = params.getCurve();
        BigInteger a = curve.getA();
        BigInteger b = curve.getB();
        ECFieldFp field = (ECFieldFp) curve.getField();
        BigInteger p = field.getP();
        BigInteger order = params.getOrder();
        int cofactor = params.getCofactor();
        ECPoint ephemeralGenerator = add(multiply(nonceS, generator, params), sharedSecretPointH, params);
        if (!toBouncyCastleECPoint(ephemeralGenerator, params).isValid()) {
            LOGGER.warning("Se ha generado un punto invalido");
        }
        return new ECParameterSpec(new EllipticCurve(new ECFieldFp(p), a, b), ephemeralGenerator, order, cofactor);
    }

    private static ECPoint multiply(BigInteger s, ECPoint point, ECParameterSpec params) {
        org.bouncycastle.math.ec.ECPoint bcPoint = toBouncyCastleECPoint(point, params);
        org.bouncycastle.math.ec.ECPoint bcProd = bcPoint.multiply(s);
        return fromBouncyCastleECPoint(bcProd);
    }

    private static ECPoint fromBouncyCastleECPoint(org.bouncycastle.math.ec.ECPoint point) {
        org.bouncycastle.math.ec.ECPoint newPoint = point.normalize();
        if (!newPoint.isValid()) {
            LOGGER.warning("Se ha proporcionado un punto invalido");
        }
        return new ECPoint(newPoint.getAffineXCoord().toBigInteger(), newPoint.getAffineYCoord().toBigInteger());
    }

    private static ECPoint add(ECPoint x, ECPoint y, ECParameterSpec params) {
        org.bouncycastle.math.ec.ECPoint bcX = toBouncyCastleECPoint(x, params);
        org.bouncycastle.math.ec.ECPoint bcY = toBouncyCastleECPoint(y, params);
        org.bouncycastle.math.ec.ECPoint bcSum = bcX.add(bcY);
        return fromBouncyCastleECPoint(bcSum);
    }

    private static org.bouncycastle.math.ec.ECPoint toBouncyCastleECPoint(ECPoint point, ECParameterSpec params) {
        ECCurve bcCurve = toBouncyCastleECCurve(params);
        return bcCurve.createPoint(point.getAffineX(), point.getAffineY());
    }

    @Override // es.gob.jmulticard.CryptoHelper
    public X509Certificate[] validateCmsSignature(byte[] signedDataBytes) throws SignatureException, IOException, CertificateException {
        try {
            CMSSignedData cmsSignedData = new CMSSignedData(signedDataBytes);
            Store<X509CertificateHolder> store = cmsSignedData.getCertificates();
            List<X509Certificate> certChain = new ArrayList<>();
            for (SignerInformation si : cmsSignedData.getSignerInfos().getSigners()) {
                Iterator<X509CertificateHolder> certIt = store.getMatches(new CertHolderBySignerIdSelector(si.getSID())).iterator();
                try {
                    X509Certificate cert = generateCertificate(certIt.next().getEncoded());
                    try {
                        cert.checkValidity();
                        try {
                            if (!si.verify(new SignerInformationVerifier(new DefaultCMSSignatureAlgorithmNameGenerator(), new DefaultSignatureAlgorithmIdentifierFinder(), new JcaContentVerifierProviderBuilder().setProvider(new BouncyCastleProvider()).build(cert), new BcDigestCalculatorProvider()))) {
                                throw new SignatureException("Firma del SOD no valida");
                            }
                            certChain.add(cert);
                        } catch (CMSException | OperatorCreationException e) {
                            throw new SignatureException("No se ha podido comprobar la firma del SOD", e);
                        }
                    } catch (CertificateExpiredException | CertificateNotYetValidException e1) {
                        throw new CertificateException("El SignedData contiene un certificado fuera de su periodo temporal de validez", e1);
                    }
                } catch (IOException e2) {
                    throw new CertificateException("El SignedData contiene un certificado en formato incorrecto", e2);
                }
            }
            return (X509Certificate[]) certChain.toArray(new X509Certificate[certChain.size()]);
        } catch (CMSException e3) {
            throw new IOException("Los datos no son un SignedData de PKCS#7/CMS", e3);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/crypto/BcCryptoHelper$CertHolderBySignerIdSelector.class */
    private static final class CertHolderBySignerIdSelector implements Selector<X509CertificateHolder> {
        private final SignerId signerId;

        CertHolderBySignerIdSelector(SignerId sid) {
            if (sid == null) {
                throw new IllegalArgumentException("El ID del firmante no puede ser nulo");
            }
            this.signerId = sid;
        }

        @Override // org.bouncycastle.util.Selector
        public boolean match(X509CertificateHolder o) {
            return this.signerId.getSerialNumber().equals(o.getSerialNumber());
        }

        @Override // org.bouncycastle.util.Selector
        public Object clone() {
            throw new UnsupportedOperationException();
        }
    }

    @Override // es.gob.jmulticard.CryptoHelper
    public byte[] getCmsSignatureSignedContent(byte[] signedDataBytes) throws IOException {
        try {
            CMSSignedData cmsSignedData = new CMSSignedData(signedDataBytes);
            return (byte[]) cmsSignedData.getSignedContent().getContent();
        } catch (CMSException e2) {
            throw new IOException("Los datos no son un SignedData de PKCS#7/CMS", e2);
        }
    }

    @Override // es.gob.jmulticard.CryptoHelper
    public CryptoHelper.PaceChannelHelper getPaceChannelHelper(CardAccess cardAccess, PaceChat paceChat) {
        if (this.paceChannelHelper == null) {
            this.paceChannelHelper = new BcPaceChannelHelper(this, cardAccess, paceChat);
        }
        return this.paceChannelHelper;
    }

    @Override // es.gob.jmulticard.CryptoHelper
    public RSAPublicKey getRsaPublicKey(X509Certificate cert) {
        if (cert == null) {
            throw new IllegalArgumentException("El certificado del cual extraer la clave publica no puede ser nulo");
        }
        try {
            return new CustomRsaPublicKey(cert.getPublicKey().getEncoded());
        } catch (IOException e) {
            throw new IllegalArgumentException("No se ha podido crear la clave publica a partir del certificado", e);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/crypto/BcCryptoHelper$CustomRsaPublicKey.class */
    public static class CustomRsaPublicKey implements RSAPublicKey {
        private static final long serialVersionUID = -1691684007756690866L;
        private final byte[] keyBytes;
        private final BigInteger modulus;
        private final BigInteger exponent;

        CustomRsaPublicKey(byte[] pukBytes) throws IOException {
            if (pukBytes == null) {
                throw new IllegalArgumentException("La codificacion de la clave publica RSA no puede ser nula");
            }
            this.keyBytes = (byte[]) pukBytes.clone();
            DERBitString bs = (DERBitString) ASN1Sequence.getInstance(ASN1Primitive.fromByteArray(pukBytes)).getObjectAt(1);
            ASN1Sequence seq = ASN1Sequence.getInstance(ASN1Primitive.fromByteArray(bs.getBytes()));
            byte[] modBytes = ((ASN1Integer) seq.getObjectAt(0)).getValue().toByteArray();
            if (modBytes == null || modBytes.length < 128) {
                throw new IOException("El modulo obtenido es menor de 128 octetos (1024 bits)");
            }
            byte[] expBytes = ((ASN1Integer) seq.getObjectAt(1)).getValue().toByteArray();
            if (expBytes == null || expBytes.length < 3) {
                throw new IOException("El exponente obtenido es menor de 3 octetos");
            }
            this.modulus = new BigInteger(1, modBytes);
            this.exponent = new BigInteger(1, expBytes);
        }

        @Override // java.security.Key
        public String getAlgorithm() {
            return "RSA";
        }

        @Override // java.security.Key
        public String getFormat() {
            return XMLX509Certificate.JCA_CERT_ID;
        }

        @Override // java.security.Key
        public byte[] getEncoded() {
            return (byte[]) this.keyBytes.clone();
        }

        @Override // java.security.interfaces.RSAKey
        public BigInteger getModulus() {
            return this.modulus;
        }

        @Override // java.security.interfaces.RSAPublicKey
        public BigInteger getPublicExponent() {
            return this.exponent;
        }
    }
}
