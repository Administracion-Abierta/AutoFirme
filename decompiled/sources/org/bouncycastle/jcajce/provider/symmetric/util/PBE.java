package org.bouncycastle.jcajce.provider.symmetric.util;

import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.SecretKey;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.GOST3411Digest;
import org.bouncycastle.crypto.digests.MD2Digest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.digests.TigerDigest;
import org.bouncycastle.crypto.generators.OpenSSLPBEParametersGenerator;
import org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import org.bouncycastle.crypto.generators.PKCS5S1ParametersGenerator;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.DESParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.util.DigestFactory;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/util/PBE.class */
public interface PBE {
    public static final int MD5 = 0;
    public static final int SHA1 = 1;
    public static final int RIPEMD160 = 2;
    public static final int TIGER = 3;
    public static final int SHA256 = 4;
    public static final int MD2 = 5;
    public static final int GOST3411 = 6;
    public static final int SHA224 = 7;
    public static final int SHA384 = 8;
    public static final int SHA512 = 9;
    public static final int SHA3_224 = 10;
    public static final int SHA3_256 = 11;
    public static final int SHA3_384 = 12;
    public static final int SHA3_512 = 13;
    public static final int SM3 = 14;
    public static final int PKCS5S1 = 0;
    public static final int PKCS5S2 = 1;
    public static final int PKCS12 = 2;
    public static final int OPENSSL = 3;
    public static final int PKCS5S1_UTF8 = 4;
    public static final int PKCS5S2_UTF8 = 5;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/util/PBE$Util.class */
    public static class Util {
        private static PBEParametersGenerator makePBEGenerator(int i, int i2) {
            PBEParametersGenerator pKCS5S1ParametersGenerator;
            if (i == 0 || i == 4) {
                switch (i2) {
                    case 0:
                        pKCS5S1ParametersGenerator = new PKCS5S1ParametersGenerator(DigestFactory.createMD5());
                        break;
                    case 1:
                        pKCS5S1ParametersGenerator = new PKCS5S1ParametersGenerator(DigestFactory.createSHA1());
                        break;
                    case 5:
                        pKCS5S1ParametersGenerator = new PKCS5S1ParametersGenerator(new MD2Digest());
                        break;
                    default:
                        throw new IllegalStateException("PKCS5 scheme 1 only supports MD2, MD5 and SHA1.");
                }
            } else if (i == 1 || i == 5) {
                switch (i2) {
                    case 0:
                        pKCS5S1ParametersGenerator = new PKCS5S2ParametersGenerator(DigestFactory.createMD5PRF());
                        break;
                    case 1:
                        pKCS5S1ParametersGenerator = new PKCS5S2ParametersGenerator(DigestFactory.createSHA1PRF());
                        break;
                    case 2:
                        pKCS5S1ParametersGenerator = new PKCS5S2ParametersGenerator(new RIPEMD160Digest(CryptoServicePurpose.PRF));
                        break;
                    case 3:
                        pKCS5S1ParametersGenerator = new PKCS5S2ParametersGenerator(new TigerDigest(CryptoServicePurpose.PRF));
                        break;
                    case 4:
                        pKCS5S1ParametersGenerator = new PKCS5S2ParametersGenerator(DigestFactory.createSHA256PRF());
                        break;
                    case 5:
                        pKCS5S1ParametersGenerator = new PKCS5S2ParametersGenerator(new MD2Digest(CryptoServicePurpose.PRF));
                        break;
                    case 6:
                        pKCS5S1ParametersGenerator = new PKCS5S2ParametersGenerator(new GOST3411Digest(CryptoServicePurpose.PRF));
                        break;
                    case 7:
                        pKCS5S1ParametersGenerator = new PKCS5S2ParametersGenerator(DigestFactory.createSHA224PRF());
                        break;
                    case 8:
                        pKCS5S1ParametersGenerator = new PKCS5S2ParametersGenerator(DigestFactory.createSHA384PRF());
                        break;
                    case 9:
                        pKCS5S1ParametersGenerator = new PKCS5S2ParametersGenerator(DigestFactory.createSHA512PRF());
                        break;
                    case 10:
                        pKCS5S1ParametersGenerator = new PKCS5S2ParametersGenerator(DigestFactory.createSHA3_224PRF());
                        break;
                    case 11:
                        pKCS5S1ParametersGenerator = new PKCS5S2ParametersGenerator(DigestFactory.createSHA3_256PRF());
                        break;
                    case 12:
                        pKCS5S1ParametersGenerator = new PKCS5S2ParametersGenerator(DigestFactory.createSHA3_384PRF());
                        break;
                    case 13:
                        pKCS5S1ParametersGenerator = new PKCS5S2ParametersGenerator(DigestFactory.createSHA3_512PRF());
                        break;
                    case 14:
                        pKCS5S1ParametersGenerator = new PKCS5S2ParametersGenerator(new SM3Digest(CryptoServicePurpose.PRF));
                        break;
                    default:
                        throw new IllegalStateException("unknown digest scheme for PBE PKCS5S2 encryption.");
                }
            } else if (i == 2) {
                switch (i2) {
                    case 0:
                        pKCS5S1ParametersGenerator = new PKCS12ParametersGenerator(DigestFactory.createMD5PRF());
                        break;
                    case 1:
                        pKCS5S1ParametersGenerator = new PKCS12ParametersGenerator(DigestFactory.createSHA1PRF());
                        break;
                    case 2:
                        pKCS5S1ParametersGenerator = new PKCS12ParametersGenerator(new RIPEMD160Digest(CryptoServicePurpose.PRF));
                        break;
                    case 3:
                        pKCS5S1ParametersGenerator = new PKCS12ParametersGenerator(new TigerDigest(CryptoServicePurpose.PRF));
                        break;
                    case 4:
                        pKCS5S1ParametersGenerator = new PKCS12ParametersGenerator(DigestFactory.createSHA256PRF());
                        break;
                    case 5:
                        pKCS5S1ParametersGenerator = new PKCS12ParametersGenerator(new MD2Digest(CryptoServicePurpose.PRF));
                        break;
                    case 6:
                        pKCS5S1ParametersGenerator = new PKCS12ParametersGenerator(new GOST3411Digest(CryptoServicePurpose.PRF));
                        break;
                    case 7:
                        pKCS5S1ParametersGenerator = new PKCS12ParametersGenerator(DigestFactory.createSHA224PRF());
                        break;
                    case 8:
                        pKCS5S1ParametersGenerator = new PKCS12ParametersGenerator(DigestFactory.createSHA384PRF());
                        break;
                    case 9:
                        pKCS5S1ParametersGenerator = new PKCS12ParametersGenerator(DigestFactory.createSHA512PRF());
                        break;
                    default:
                        throw new IllegalStateException("unknown digest scheme for PBE encryption.");
                }
            } else {
                pKCS5S1ParametersGenerator = new OpenSSLPBEParametersGenerator();
            }
            return pKCS5S1ParametersGenerator;
        }

        public static CipherParameters makePBEParameters(byte[] bArr, int i, int i2, int i3, int i4, AlgorithmParameterSpec algorithmParameterSpec, String str) throws InvalidAlgorithmParameterException {
            if (algorithmParameterSpec == null || !(algorithmParameterSpec instanceof PBEParameterSpec)) {
                throw new InvalidAlgorithmParameterException("Need a PBEParameter spec with a PBE key.");
            }
            PBEParameterSpec pBEParameterSpec = (PBEParameterSpec) algorithmParameterSpec;
            PBEParametersGenerator pBEParametersGeneratorMakePBEGenerator = makePBEGenerator(i, i2);
            pBEParametersGeneratorMakePBEGenerator.init(bArr, pBEParameterSpec.getSalt(), pBEParameterSpec.getIterationCount());
            CipherParameters cipherParametersGenerateDerivedParameters = i4 != 0 ? pBEParametersGeneratorMakePBEGenerator.generateDerivedParameters(i3, i4) : pBEParametersGeneratorMakePBEGenerator.generateDerivedParameters(i3);
            if (str.startsWith("DES")) {
                if (cipherParametersGenerateDerivedParameters instanceof ParametersWithIV) {
                    DESParameters.setOddParity(((KeyParameter) ((ParametersWithIV) cipherParametersGenerateDerivedParameters).getParameters()).getKey());
                } else {
                    DESParameters.setOddParity(((KeyParameter) cipherParametersGenerateDerivedParameters).getKey());
                }
            }
            return cipherParametersGenerateDerivedParameters;
        }

        public static CipherParameters makePBEParameters(BCPBEKey bCPBEKey, AlgorithmParameterSpec algorithmParameterSpec, String str) {
            if (algorithmParameterSpec == null || !(algorithmParameterSpec instanceof PBEParameterSpec)) {
                throw new IllegalArgumentException("Need a PBEParameter spec with a PBE key.");
            }
            PBEParameterSpec pBEParameterSpec = (PBEParameterSpec) algorithmParameterSpec;
            PBEParametersGenerator pBEParametersGeneratorMakePBEGenerator = makePBEGenerator(bCPBEKey.getType(), bCPBEKey.getDigest());
            byte[] encoded = bCPBEKey.getEncoded();
            if (bCPBEKey.shouldTryWrongPKCS12()) {
                encoded = new byte[2];
            }
            pBEParametersGeneratorMakePBEGenerator.init(encoded, pBEParameterSpec.getSalt(), pBEParameterSpec.getIterationCount());
            CipherParameters cipherParametersGenerateDerivedParameters = bCPBEKey.getIvSize() != 0 ? pBEParametersGeneratorMakePBEGenerator.generateDerivedParameters(bCPBEKey.getKeySize(), bCPBEKey.getIvSize()) : pBEParametersGeneratorMakePBEGenerator.generateDerivedParameters(bCPBEKey.getKeySize());
            if (str.startsWith("DES")) {
                if (cipherParametersGenerateDerivedParameters instanceof ParametersWithIV) {
                    DESParameters.setOddParity(((KeyParameter) ((ParametersWithIV) cipherParametersGenerateDerivedParameters).getParameters()).getKey());
                } else {
                    DESParameters.setOddParity(((KeyParameter) cipherParametersGenerateDerivedParameters).getKey());
                }
            }
            return cipherParametersGenerateDerivedParameters;
        }

        public static CipherParameters makePBEMacParameters(BCPBEKey bCPBEKey, AlgorithmParameterSpec algorithmParameterSpec) {
            if (algorithmParameterSpec == null || !(algorithmParameterSpec instanceof PBEParameterSpec)) {
                throw new IllegalArgumentException("Need a PBEParameter spec with a PBE key.");
            }
            PBEParameterSpec pBEParameterSpec = (PBEParameterSpec) algorithmParameterSpec;
            PBEParametersGenerator pBEParametersGeneratorMakePBEGenerator = makePBEGenerator(bCPBEKey.getType(), bCPBEKey.getDigest());
            pBEParametersGeneratorMakePBEGenerator.init(bCPBEKey.getEncoded(), pBEParameterSpec.getSalt(), pBEParameterSpec.getIterationCount());
            return pBEParametersGeneratorMakePBEGenerator.generateDerivedMacParameters(bCPBEKey.getKeySize());
        }

        public static CipherParameters makePBEMacParameters(PBEKeySpec pBEKeySpec, int i, int i2, int i3) {
            PBEParametersGenerator pBEParametersGeneratorMakePBEGenerator = makePBEGenerator(i, i2);
            byte[] bArrConvertPassword = convertPassword(i, pBEKeySpec);
            pBEParametersGeneratorMakePBEGenerator.init(bArrConvertPassword, pBEKeySpec.getSalt(), pBEKeySpec.getIterationCount());
            CipherParameters cipherParametersGenerateDerivedMacParameters = pBEParametersGeneratorMakePBEGenerator.generateDerivedMacParameters(i3);
            for (int i4 = 0; i4 != bArrConvertPassword.length; i4++) {
                bArrConvertPassword[i4] = 0;
            }
            return cipherParametersGenerateDerivedMacParameters;
        }

        public static CipherParameters makePBEParameters(PBEKeySpec pBEKeySpec, int i, int i2, int i3, int i4) {
            PBEParametersGenerator pBEParametersGeneratorMakePBEGenerator = makePBEGenerator(i, i2);
            byte[] bArrConvertPassword = convertPassword(i, pBEKeySpec);
            pBEParametersGeneratorMakePBEGenerator.init(bArrConvertPassword, pBEKeySpec.getSalt(), pBEKeySpec.getIterationCount());
            CipherParameters cipherParametersGenerateDerivedParameters = i4 != 0 ? pBEParametersGeneratorMakePBEGenerator.generateDerivedParameters(i3, i4) : pBEParametersGeneratorMakePBEGenerator.generateDerivedParameters(i3);
            for (int i5 = 0; i5 != bArrConvertPassword.length; i5++) {
                bArrConvertPassword[i5] = 0;
            }
            return cipherParametersGenerateDerivedParameters;
        }

        public static CipherParameters makePBEMacParameters(SecretKey secretKey, int i, int i2, int i3, PBEParameterSpec pBEParameterSpec) {
            PBEParametersGenerator pBEParametersGeneratorMakePBEGenerator = makePBEGenerator(i, i2);
            byte[] encoded = secretKey.getEncoded();
            pBEParametersGeneratorMakePBEGenerator.init(secretKey.getEncoded(), pBEParameterSpec.getSalt(), pBEParameterSpec.getIterationCount());
            CipherParameters cipherParametersGenerateDerivedMacParameters = pBEParametersGeneratorMakePBEGenerator.generateDerivedMacParameters(i3);
            for (int i4 = 0; i4 != encoded.length; i4++) {
                encoded[i4] = 0;
            }
            return cipherParametersGenerateDerivedMacParameters;
        }

        private static byte[] convertPassword(int i, PBEKeySpec pBEKeySpec) {
            byte[] bArrPKCS5PasswordToUTF8Bytes;
            if (i == 2) {
                bArrPKCS5PasswordToUTF8Bytes = PBEParametersGenerator.PKCS12PasswordToBytes(pBEKeySpec.getPassword());
            } else {
                bArrPKCS5PasswordToUTF8Bytes = (i == 5 || i == 4) ? PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(pBEKeySpec.getPassword()) : PBEParametersGenerator.PKCS5PasswordToBytes(pBEKeySpec.getPassword());
            }
            return bArrPKCS5PasswordToUTF8Bytes;
        }
    }
}
