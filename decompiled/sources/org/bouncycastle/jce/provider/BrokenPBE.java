package org.bouncycastle.jce.provider;

import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import org.bouncycastle.crypto.generators.PKCS5S1ParametersGenerator;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jce/provider/BrokenPBE.class */
public interface BrokenPBE {
    public static final int MD5 = 0;
    public static final int SHA1 = 1;
    public static final int RIPEMD160 = 2;
    public static final int PKCS5S1 = 0;
    public static final int PKCS5S2 = 1;
    public static final int PKCS12 = 2;
    public static final int OLD_PKCS12 = 3;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jce/provider/BrokenPBE$Util.class */
    public static class Util {
        private static void setOddParity(byte[] bArr) {
            for (int i = 0; i < bArr.length; i++) {
                byte b = bArr[i];
                bArr[i] = (byte) ((b & 254) | ((((((((b >> 1) ^ (b >> 2)) ^ (b >> 3)) ^ (b >> 4)) ^ (b >> 5)) ^ (b >> 6)) ^ (b >> 7)) ^ 1));
            }
        }

        private static PBEParametersGenerator makePBEGenerator(int i, int i2) {
            PBEParametersGenerator pKCS12ParametersGenerator;
            if (i == 0) {
                switch (i2) {
                    case 0:
                        pKCS12ParametersGenerator = new PKCS5S1ParametersGenerator(new MD5Digest());
                        break;
                    case 1:
                        pKCS12ParametersGenerator = new PKCS5S1ParametersGenerator(new SHA1Digest());
                        break;
                    default:
                        throw new IllegalStateException("PKCS5 scheme 1 only supports only MD5 and SHA1.");
                }
            } else if (i == 1) {
                pKCS12ParametersGenerator = new PKCS5S2ParametersGenerator();
            } else if (i == 3) {
                switch (i2) {
                    case 0:
                        pKCS12ParametersGenerator = new OldPKCS12ParametersGenerator(new MD5Digest());
                        break;
                    case 1:
                        pKCS12ParametersGenerator = new OldPKCS12ParametersGenerator(new SHA1Digest());
                        break;
                    case 2:
                        pKCS12ParametersGenerator = new OldPKCS12ParametersGenerator(new RIPEMD160Digest());
                        break;
                    default:
                        throw new IllegalStateException("unknown digest scheme for PBE encryption.");
                }
            } else {
                switch (i2) {
                    case 0:
                        pKCS12ParametersGenerator = new PKCS12ParametersGenerator(new MD5Digest());
                        break;
                    case 1:
                        pKCS12ParametersGenerator = new PKCS12ParametersGenerator(new SHA1Digest());
                        break;
                    case 2:
                        pKCS12ParametersGenerator = new PKCS12ParametersGenerator(new RIPEMD160Digest());
                        break;
                    default:
                        throw new IllegalStateException("unknown digest scheme for PBE encryption.");
                }
            }
            return pKCS12ParametersGenerator;
        }

        static CipherParameters makePBEParameters(BCPBEKey bCPBEKey, AlgorithmParameterSpec algorithmParameterSpec, int i, int i2, String str, int i3, int i4) {
            if (algorithmParameterSpec == null || !(algorithmParameterSpec instanceof PBEParameterSpec)) {
                throw new IllegalArgumentException("Need a PBEParameter spec with a PBE key.");
            }
            PBEParameterSpec pBEParameterSpec = (PBEParameterSpec) algorithmParameterSpec;
            PBEParametersGenerator pBEParametersGeneratorMakePBEGenerator = makePBEGenerator(i, i2);
            byte[] encoded = bCPBEKey.getEncoded();
            pBEParametersGeneratorMakePBEGenerator.init(encoded, pBEParameterSpec.getSalt(), pBEParameterSpec.getIterationCount());
            CipherParameters cipherParametersGenerateDerivedParameters = i4 != 0 ? pBEParametersGeneratorMakePBEGenerator.generateDerivedParameters(i3, i4) : pBEParametersGeneratorMakePBEGenerator.generateDerivedParameters(i3);
            if (str.startsWith("DES")) {
                if (cipherParametersGenerateDerivedParameters instanceof ParametersWithIV) {
                    setOddParity(((KeyParameter) ((ParametersWithIV) cipherParametersGenerateDerivedParameters).getParameters()).getKey());
                } else {
                    setOddParity(((KeyParameter) cipherParametersGenerateDerivedParameters).getKey());
                }
            }
            for (int i5 = 0; i5 != encoded.length; i5++) {
                encoded[i5] = 0;
            }
            return cipherParametersGenerateDerivedParameters;
        }

        static CipherParameters makePBEMacParameters(BCPBEKey bCPBEKey, AlgorithmParameterSpec algorithmParameterSpec, int i, int i2, int i3) {
            if (algorithmParameterSpec == null || !(algorithmParameterSpec instanceof PBEParameterSpec)) {
                throw new IllegalArgumentException("Need a PBEParameter spec with a PBE key.");
            }
            PBEParameterSpec pBEParameterSpec = (PBEParameterSpec) algorithmParameterSpec;
            PBEParametersGenerator pBEParametersGeneratorMakePBEGenerator = makePBEGenerator(i, i2);
            byte[] encoded = bCPBEKey.getEncoded();
            pBEParametersGeneratorMakePBEGenerator.init(encoded, pBEParameterSpec.getSalt(), pBEParameterSpec.getIterationCount());
            CipherParameters cipherParametersGenerateDerivedMacParameters = pBEParametersGeneratorMakePBEGenerator.generateDerivedMacParameters(i3);
            for (int i4 = 0; i4 != encoded.length; i4++) {
                encoded[i4] = 0;
            }
            return cipherParametersGenerateDerivedMacParameters;
        }
    }
}
