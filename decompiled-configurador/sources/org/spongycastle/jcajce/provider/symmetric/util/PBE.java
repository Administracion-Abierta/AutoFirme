package org.spongycastle.jcajce.provider.symmetric.util;

import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.SecretKey;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.PBEParametersGenerator;
import org.spongycastle.crypto.digests.GOST3411Digest;
import org.spongycastle.crypto.digests.MD2Digest;
import org.spongycastle.crypto.digests.RIPEMD160Digest;
import org.spongycastle.crypto.digests.TigerDigest;
import org.spongycastle.crypto.generators.OpenSSLPBEParametersGenerator;
import org.spongycastle.crypto.generators.PKCS12ParametersGenerator;
import org.spongycastle.crypto.generators.PKCS5S1ParametersGenerator;
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.spongycastle.crypto.params.DESParameters;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.spongycastle.crypto.util.DigestFactory;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/symmetric/util/PBE.class */
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
    public static final int PKCS5S1 = 0;
    public static final int PKCS5S2 = 1;
    public static final int PKCS12 = 2;
    public static final int OPENSSL = 3;
    public static final int PKCS5S1_UTF8 = 4;
    public static final int PKCS5S2_UTF8 = 5;

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/symmetric/util/PBE$Util.class */
    public static class Util {
        private static PBEParametersGenerator makePBEGenerator(int type, int hash) {
            PBEParametersGenerator generator;
            if (type == 0 || type == 4) {
                switch (hash) {
                    case 0:
                        generator = new PKCS5S1ParametersGenerator(DigestFactory.createMD5());
                        break;
                    case 1:
                        generator = new PKCS5S1ParametersGenerator(DigestFactory.createSHA1());
                        break;
                    case 5:
                        generator = new PKCS5S1ParametersGenerator(new MD2Digest());
                        break;
                    default:
                        throw new IllegalStateException("PKCS5 scheme 1 only supports MD2, MD5 and SHA1.");
                }
            } else if (type == 1 || type == 5) {
                switch (hash) {
                    case 0:
                        generator = new PKCS5S2ParametersGenerator(DigestFactory.createMD5());
                        break;
                    case 1:
                        generator = new PKCS5S2ParametersGenerator(DigestFactory.createSHA1());
                        break;
                    case 2:
                        generator = new PKCS5S2ParametersGenerator(new RIPEMD160Digest());
                        break;
                    case 3:
                        generator = new PKCS5S2ParametersGenerator(new TigerDigest());
                        break;
                    case 4:
                        generator = new PKCS5S2ParametersGenerator(DigestFactory.createSHA256());
                        break;
                    case 5:
                        generator = new PKCS5S2ParametersGenerator(new MD2Digest());
                        break;
                    case 6:
                        generator = new PKCS5S2ParametersGenerator(new GOST3411Digest());
                        break;
                    case 7:
                        generator = new PKCS5S2ParametersGenerator(DigestFactory.createSHA224());
                        break;
                    case 8:
                        generator = new PKCS5S2ParametersGenerator(DigestFactory.createSHA384());
                        break;
                    case 9:
                        generator = new PKCS5S2ParametersGenerator(DigestFactory.createSHA512());
                        break;
                    case 10:
                        generator = new PKCS5S2ParametersGenerator(DigestFactory.createSHA3_224());
                        break;
                    case 11:
                        generator = new PKCS5S2ParametersGenerator(DigestFactory.createSHA3_256());
                        break;
                    case 12:
                        generator = new PKCS5S2ParametersGenerator(DigestFactory.createSHA3_384());
                        break;
                    case 13:
                        generator = new PKCS5S2ParametersGenerator(DigestFactory.createSHA3_512());
                        break;
                    default:
                        throw new IllegalStateException("unknown digest scheme for PBE PKCS5S2 encryption.");
                }
            } else if (type == 2) {
                switch (hash) {
                    case 0:
                        generator = new PKCS12ParametersGenerator(DigestFactory.createMD5());
                        break;
                    case 1:
                        generator = new PKCS12ParametersGenerator(DigestFactory.createSHA1());
                        break;
                    case 2:
                        generator = new PKCS12ParametersGenerator(new RIPEMD160Digest());
                        break;
                    case 3:
                        generator = new PKCS12ParametersGenerator(new TigerDigest());
                        break;
                    case 4:
                        generator = new PKCS12ParametersGenerator(DigestFactory.createSHA256());
                        break;
                    case 5:
                        generator = new PKCS12ParametersGenerator(new MD2Digest());
                        break;
                    case 6:
                        generator = new PKCS12ParametersGenerator(new GOST3411Digest());
                        break;
                    case 7:
                        generator = new PKCS12ParametersGenerator(DigestFactory.createSHA224());
                        break;
                    case 8:
                        generator = new PKCS12ParametersGenerator(DigestFactory.createSHA384());
                        break;
                    case 9:
                        generator = new PKCS12ParametersGenerator(DigestFactory.createSHA512());
                        break;
                    default:
                        throw new IllegalStateException("unknown digest scheme for PBE encryption.");
                }
            } else {
                generator = new OpenSSLPBEParametersGenerator();
            }
            return generator;
        }

        public static CipherParameters makePBEParameters(byte[] pbeKey, int scheme, int digest, int keySize, int ivSize, AlgorithmParameterSpec spec, String targetAlgorithm) throws InvalidAlgorithmParameterException {
            CipherParameters param;
            if (spec == null || !(spec instanceof PBEParameterSpec)) {
                throw new InvalidAlgorithmParameterException("Need a PBEParameter spec with a PBE key.");
            }
            PBEParameterSpec pbeParam = (PBEParameterSpec) spec;
            PBEParametersGenerator generator = makePBEGenerator(scheme, digest);
            generator.init(pbeKey, pbeParam.getSalt(), pbeParam.getIterationCount());
            if (ivSize != 0) {
                param = generator.generateDerivedParameters(keySize, ivSize);
            } else {
                param = generator.generateDerivedParameters(keySize);
            }
            if (targetAlgorithm.startsWith("DES")) {
                if (param instanceof ParametersWithIV) {
                    KeyParameter kParam = (KeyParameter) ((ParametersWithIV) param).getParameters();
                    DESParameters.setOddParity(kParam.getKey());
                } else {
                    KeyParameter kParam2 = (KeyParameter) param;
                    DESParameters.setOddParity(kParam2.getKey());
                }
            }
            return param;
        }

        public static CipherParameters makePBEParameters(BCPBEKey pbeKey, AlgorithmParameterSpec spec, String targetAlgorithm) {
            CipherParameters param;
            if (spec == null || !(spec instanceof PBEParameterSpec)) {
                throw new IllegalArgumentException("Need a PBEParameter spec with a PBE key.");
            }
            PBEParameterSpec pbeParam = (PBEParameterSpec) spec;
            PBEParametersGenerator generator = makePBEGenerator(pbeKey.getType(), pbeKey.getDigest());
            byte[] key = pbeKey.getEncoded();
            if (pbeKey.shouldTryWrongPKCS12()) {
                key = new byte[2];
            }
            generator.init(key, pbeParam.getSalt(), pbeParam.getIterationCount());
            if (pbeKey.getIvSize() != 0) {
                param = generator.generateDerivedParameters(pbeKey.getKeySize(), pbeKey.getIvSize());
            } else {
                param = generator.generateDerivedParameters(pbeKey.getKeySize());
            }
            if (targetAlgorithm.startsWith("DES")) {
                if (param instanceof ParametersWithIV) {
                    KeyParameter kParam = (KeyParameter) ((ParametersWithIV) param).getParameters();
                    DESParameters.setOddParity(kParam.getKey());
                } else {
                    KeyParameter kParam2 = (KeyParameter) param;
                    DESParameters.setOddParity(kParam2.getKey());
                }
            }
            return param;
        }

        public static CipherParameters makePBEMacParameters(BCPBEKey pbeKey, AlgorithmParameterSpec spec) {
            if (spec == null || !(spec instanceof PBEParameterSpec)) {
                throw new IllegalArgumentException("Need a PBEParameter spec with a PBE key.");
            }
            PBEParameterSpec pbeParam = (PBEParameterSpec) spec;
            PBEParametersGenerator generator = makePBEGenerator(pbeKey.getType(), pbeKey.getDigest());
            byte[] key = pbeKey.getEncoded();
            generator.init(key, pbeParam.getSalt(), pbeParam.getIterationCount());
            CipherParameters param = generator.generateDerivedMacParameters(pbeKey.getKeySize());
            return param;
        }

        public static CipherParameters makePBEMacParameters(PBEKeySpec keySpec, int type, int hash, int keySize) {
            PBEParametersGenerator generator = makePBEGenerator(type, hash);
            byte[] key = convertPassword(type, keySpec);
            generator.init(key, keySpec.getSalt(), keySpec.getIterationCount());
            CipherParameters param = generator.generateDerivedMacParameters(keySize);
            for (int i = 0; i != key.length; i++) {
                key[i] = 0;
            }
            return param;
        }

        public static CipherParameters makePBEParameters(PBEKeySpec keySpec, int type, int hash, int keySize, int ivSize) {
            CipherParameters param;
            PBEParametersGenerator generator = makePBEGenerator(type, hash);
            byte[] key = convertPassword(type, keySpec);
            generator.init(key, keySpec.getSalt(), keySpec.getIterationCount());
            if (ivSize != 0) {
                param = generator.generateDerivedParameters(keySize, ivSize);
            } else {
                param = generator.generateDerivedParameters(keySize);
            }
            for (int i = 0; i != key.length; i++) {
                key[i] = 0;
            }
            return param;
        }

        public static CipherParameters makePBEMacParameters(SecretKey key, int type, int hash, int keySize, PBEParameterSpec pbeSpec) {
            PBEParametersGenerator generator = makePBEGenerator(type, hash);
            byte[] keyBytes = key.getEncoded();
            generator.init(key.getEncoded(), pbeSpec.getSalt(), pbeSpec.getIterationCount());
            CipherParameters param = generator.generateDerivedMacParameters(keySize);
            for (int i = 0; i != keyBytes.length; i++) {
                keyBytes[i] = 0;
            }
            return param;
        }

        private static byte[] convertPassword(int type, PBEKeySpec keySpec) {
            byte[] key;
            if (type == 2) {
                key = PBEParametersGenerator.PKCS12PasswordToBytes(keySpec.getPassword());
            } else if (type == 5 || type == 4) {
                key = PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(keySpec.getPassword());
            } else {
                key = PBEParametersGenerator.PKCS5PasswordToBytes(keySpec.getPassword());
            }
            return key;
        }
    }
}
