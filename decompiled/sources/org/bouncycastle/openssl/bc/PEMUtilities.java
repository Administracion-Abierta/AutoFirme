package org.bouncycastle.openssl.bc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.BlowfishEngine;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.engines.RC2Engine;
import org.bouncycastle.crypto.generators.OpenSSLPBEParametersGenerator;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.modes.OFBBlockCipher;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.RC2Parameters;
import org.bouncycastle.openssl.EncryptionException;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.util.Integers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/openssl/bc/PEMUtilities.class */
class PEMUtilities {
    private static final Map KEYSIZES = new HashMap();
    private static final Set PKCS5_SCHEME_1 = new HashSet();
    private static final Set PKCS5_SCHEME_2 = new HashSet();

    PEMUtilities() {
    }

    static int getKeySize(String str) {
        if (KEYSIZES.containsKey(str)) {
            return ((Integer) KEYSIZES.get(str)).intValue();
        }
        throw new IllegalStateException("no key size for algorithm: " + str);
    }

    static boolean isPKCS5Scheme1(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return PKCS5_SCHEME_1.contains(aSN1ObjectIdentifier);
    }

    static boolean isPKCS5Scheme2(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return PKCS5_SCHEME_2.contains(aSN1ObjectIdentifier);
    }

    public static boolean isPKCS12(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return aSN1ObjectIdentifier.getId().startsWith(PKCSObjectIdentifiers.pkcs_12PbeIds.getId());
    }

    public static KeyParameter generateSecretKeyForPKCS5Scheme2(String str, char[] cArr, byte[] bArr, int i) {
        PKCS5S2ParametersGenerator pKCS5S2ParametersGenerator = new PKCS5S2ParametersGenerator(new SHA1Digest());
        pKCS5S2ParametersGenerator.init(PBEParametersGenerator.PKCS5PasswordToBytes(cArr), bArr, i);
        return (KeyParameter) pKCS5S2ParametersGenerator.generateDerivedParameters(getKeySize(str));
    }

    static byte[] crypt(boolean z, byte[] bArr, char[] cArr, String str, byte[] bArr2) throws PEMException {
        int i;
        KeyParameter key;
        BlockCipher blockCipherNewInstance;
        byte[] bArr3 = bArr2;
        String str2 = "CBC";
        PKCS7Padding pKCS7Padding = new PKCS7Padding();
        if (str.endsWith("-CFB")) {
            str2 = "CFB";
            pKCS7Padding = null;
        }
        if (str.endsWith("-ECB") || "DES-EDE".equals(str) || "DES-EDE3".equals(str)) {
            str2 = "ECB";
            bArr3 = null;
        }
        if (str.endsWith("-OFB")) {
            str2 = "OFB";
            pKCS7Padding = null;
        }
        if (str.startsWith("DES-EDE")) {
            key = getKey(cArr, 24, bArr2, !str.startsWith("DES-EDE3"));
            blockCipherNewInstance = new DESedeEngine();
        } else if (str.startsWith("DES-")) {
            key = getKey(cArr, 8, bArr2);
            blockCipherNewInstance = new DESEngine();
        } else if (str.startsWith("BF-")) {
            key = getKey(cArr, 16, bArr2);
            blockCipherNewInstance = new BlowfishEngine();
        } else if (str.startsWith("RC2-")) {
            int i2 = 128;
            if (str.startsWith("RC2-40-")) {
                i2 = 40;
            } else if (str.startsWith("RC2-64-")) {
                i2 = 64;
            }
            key = new RC2Parameters(getKey(cArr, i2 / 8, bArr2).getKey(), i2);
            blockCipherNewInstance = new RC2Engine();
        } else {
            if (!str.startsWith("AES-")) {
                throw new EncryptionException("unknown encryption with private key: " + str);
            }
            byte[] bArr4 = bArr2;
            if (bArr4.length > 8) {
                bArr4 = new byte[8];
                System.arraycopy(bArr2, 0, bArr4, 0, 8);
            }
            if (str.startsWith("AES-128-")) {
                i = 128;
            } else if (str.startsWith("AES-192-")) {
                i = 192;
            } else {
                if (!str.startsWith("AES-256-")) {
                    throw new EncryptionException("unknown AES encryption with private key: " + str);
                }
                i = 256;
            }
            key = getKey(cArr, i / 8, bArr4);
            blockCipherNewInstance = AESEngine.newInstance();
        }
        if (str2.equals("CBC")) {
            blockCipherNewInstance = CBCBlockCipher.newInstance(blockCipherNewInstance);
        } else if (str2.equals("CFB")) {
            blockCipherNewInstance = CFBBlockCipher.newInstance(blockCipherNewInstance, blockCipherNewInstance.getBlockSize() * 8);
        } else if (str2.equals("OFB")) {
            blockCipherNewInstance = new OFBBlockCipher(blockCipherNewInstance, blockCipherNewInstance.getBlockSize() * 8);
        }
        try {
            BufferedBlockCipher bufferedBlockCipher = pKCS7Padding == null ? new BufferedBlockCipher(blockCipherNewInstance) : new PaddedBufferedBlockCipher(blockCipherNewInstance, pKCS7Padding);
            if (bArr3 == null) {
                bufferedBlockCipher.init(z, key);
            } else {
                bufferedBlockCipher.init(z, new ParametersWithIV(key, bArr3));
            }
            byte[] bArr5 = new byte[bufferedBlockCipher.getOutputSize(bArr.length)];
            int iProcessBytes = bufferedBlockCipher.processBytes(bArr, 0, bArr.length, bArr5, 0);
            int iDoFinal = iProcessBytes + bufferedBlockCipher.doFinal(bArr5, iProcessBytes);
            if (iDoFinal == bArr5.length) {
                return bArr5;
            }
            byte[] bArr6 = new byte[iDoFinal];
            System.arraycopy(bArr5, 0, bArr6, 0, iDoFinal);
            return bArr6;
        } catch (Exception e) {
            throw new EncryptionException("exception using cipher - please check password and data.", e);
        }
    }

    private static KeyParameter getKey(char[] cArr, int i, byte[] bArr) throws PEMException {
        return getKey(cArr, i, bArr, false);
    }

    private static KeyParameter getKey(char[] cArr, int i, byte[] bArr, boolean z) throws PEMException {
        OpenSSLPBEParametersGenerator openSSLPBEParametersGenerator = new OpenSSLPBEParametersGenerator();
        openSSLPBEParametersGenerator.init(PBEParametersGenerator.PKCS5PasswordToBytes(cArr), bArr, 1);
        KeyParameter keyParameter = (KeyParameter) openSSLPBEParametersGenerator.generateDerivedParameters(i * 8);
        if (!z || keyParameter.getKey().length != 24) {
            return keyParameter;
        }
        byte[] key = keyParameter.getKey();
        System.arraycopy(key, 0, key, 16, 8);
        return new KeyParameter(key);
    }

    static {
        PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithMD2AndDES_CBC);
        PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithMD2AndRC2_CBC);
        PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithMD5AndDES_CBC);
        PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithMD5AndRC2_CBC);
        PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithSHA1AndDES_CBC);
        PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithSHA1AndRC2_CBC);
        PKCS5_SCHEME_2.add(PKCSObjectIdentifiers.id_PBES2);
        PKCS5_SCHEME_2.add(PKCSObjectIdentifiers.des_EDE3_CBC);
        PKCS5_SCHEME_2.add(NISTObjectIdentifiers.id_aes128_CBC);
        PKCS5_SCHEME_2.add(NISTObjectIdentifiers.id_aes192_CBC);
        PKCS5_SCHEME_2.add(NISTObjectIdentifiers.id_aes256_CBC);
        KEYSIZES.put(PKCSObjectIdentifiers.des_EDE3_CBC.getId(), Integers.valueOf(192));
        KEYSIZES.put(NISTObjectIdentifiers.id_aes128_CBC.getId(), Integers.valueOf(128));
        KEYSIZES.put(NISTObjectIdentifiers.id_aes192_CBC.getId(), Integers.valueOf(192));
        KEYSIZES.put(NISTObjectIdentifiers.id_aes256_CBC.getId(), Integers.valueOf(256));
        KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC4.getId(), Integers.valueOf(128));
        KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC4, Integers.valueOf(40));
        KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd2_KeyTripleDES_CBC, Integers.valueOf(128));
        KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, Integers.valueOf(192));
        KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC2_CBC, Integers.valueOf(128));
        KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC2_CBC, Integers.valueOf(40));
    }
}
