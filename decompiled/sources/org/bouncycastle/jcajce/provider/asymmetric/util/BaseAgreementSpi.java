package org.bouncycastle.jcajce.provider.asymmetric.util;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import javax.crypto.KeyAgreementSpi;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.agreement.kdf.DHKDFParameters;
import org.bouncycastle.crypto.agreement.kdf.DHKEKGenerator;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.params.DESParameters;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.internal.asn1.gnu.GNUObjectIdentifiers;
import org.bouncycastle.internal.asn1.kisa.KISAObjectIdentifiers;
import org.bouncycastle.internal.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.internal.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.internal.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.jcajce.spec.HybridValueParameterSpec;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/util/BaseAgreementSpi.class */
public abstract class BaseAgreementSpi extends KeyAgreementSpi {
    private static final Map<String, ASN1ObjectIdentifier> defaultOids = new HashMap();
    private static final Map<String, Integer> keySizes = new HashMap();
    private static final Map<String, String> nameTable = new HashMap();
    private static final Hashtable oids = new Hashtable();
    private static final Hashtable des = new Hashtable();
    protected final String kaAlgorithm;
    protected final DerivationFunction kdf;
    protected byte[] ukmParameters;
    protected byte[] ukmParametersSalt;
    private HybridValueParameterSpec hybridSpec;

    public BaseAgreementSpi(String str, DerivationFunction derivationFunction) {
        this.kaAlgorithm = str;
        this.kdf = derivationFunction;
    }

    protected static String getAlgorithm(String str) {
        if (str.indexOf(91) > 0) {
            return str.substring(0, str.indexOf(91));
        }
        if (str.startsWith(NISTObjectIdentifiers.aes.getId())) {
            return "AES";
        }
        if (str.startsWith(GNUObjectIdentifiers.Serpent.getId())) {
            return "Serpent";
        }
        String str2 = nameTable.get(Strings.toUpperCase(str));
        return str2 != null ? str2 : str;
    }

    protected static int getKeySize(String str) {
        if (str.indexOf(91) > 0) {
            return Integer.parseInt(str.substring(str.indexOf(91) + 1, str.indexOf(93)));
        }
        String upperCase = Strings.toUpperCase(str);
        if (keySizes.containsKey(upperCase)) {
            return keySizes.get(upperCase).intValue();
        }
        return -1;
    }

    protected static byte[] trimZeroes(byte[] bArr) {
        if (bArr[0] != 0) {
            return bArr;
        }
        int i = 0;
        while (i < bArr.length && bArr[i] == 0) {
            i++;
        }
        byte[] bArr2 = new byte[bArr.length - i];
        System.arraycopy(bArr, i, bArr2, 0, bArr2.length);
        return bArr2;
    }

    @Override // javax.crypto.KeyAgreementSpi
    protected void engineInit(Key key, SecureRandom secureRandom) throws InvalidKeyException {
        try {
            doInitFromKey(key, null, secureRandom);
        } catch (InvalidAlgorithmParameterException e) {
            throw new InvalidKeyException(e.getMessage());
        }
    }

    @Override // javax.crypto.KeyAgreementSpi
    protected void engineInit(Key key, AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (algorithmParameterSpec instanceof HybridValueParameterSpec) {
            this.hybridSpec = (HybridValueParameterSpec) algorithmParameterSpec;
            doInitFromKey(key, this.hybridSpec.getBaseParameterSpec(), secureRandom);
        } else {
            this.hybridSpec = null;
            doInitFromKey(key, algorithmParameterSpec, secureRandom);
        }
    }

    @Override // javax.crypto.KeyAgreementSpi
    protected byte[] engineGenerateSecret() throws IllegalStateException {
        if (this.kdf == null) {
            return calcSecret();
        }
        byte[] bArrCalcSecret = calcSecret();
        try {
            return getSharedSecretBytes(bArrCalcSecret, null, bArrCalcSecret.length * 8);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    @Override // javax.crypto.KeyAgreementSpi
    protected int engineGenerateSecret(byte[] bArr, int i) throws IllegalStateException, ShortBufferException {
        byte[] bArrEngineGenerateSecret = engineGenerateSecret();
        if (bArr.length - i < bArrEngineGenerateSecret.length) {
            throw new ShortBufferException(this.kaAlgorithm + " key agreement: need " + bArrEngineGenerateSecret.length + " bytes");
        }
        System.arraycopy(bArrEngineGenerateSecret, 0, bArr, i, bArrEngineGenerateSecret.length);
        return bArrEngineGenerateSecret.length;
    }

    @Override // javax.crypto.KeyAgreementSpi
    protected SecretKey engineGenerateSecret(String str) throws NoSuchAlgorithmException {
        String upperCase = Strings.toUpperCase(str);
        String id = str;
        if (oids.containsKey(upperCase)) {
            id = ((ASN1ObjectIdentifier) oids.get(upperCase)).getId();
        }
        byte[] sharedSecretBytes = getSharedSecretBytes(calcSecret(), id, getKeySize(id));
        String algorithm = getAlgorithm(str);
        if (des.containsKey(algorithm)) {
            DESParameters.setOddParity(sharedSecretBytes);
        }
        return new SecretKeySpec(sharedSecretBytes, algorithm);
    }

    private byte[] getSharedSecretBytes(byte[] bArr, String str, int i) throws NoSuchAlgorithmException {
        if (this.kdf == null) {
            if (i <= 0) {
                return bArr;
            }
            byte[] bArr2 = new byte[i / 8];
            System.arraycopy(bArr, 0, bArr2, 0, bArr2.length);
            Arrays.clear(bArr);
            return bArr2;
        }
        if (i < 0) {
            throw new NoSuchAlgorithmException("unknown algorithm encountered: " + str);
        }
        byte[] bArr3 = new byte[i / 8];
        if (this.kdf instanceof DHKEKGenerator) {
            if (str == null) {
                throw new NoSuchAlgorithmException("algorithm OID is null");
            }
            try {
                this.kdf.init(new DHKDFParameters(new ASN1ObjectIdentifier(str), i, bArr, this.ukmParameters));
            } catch (IllegalArgumentException e) {
                throw new NoSuchAlgorithmException("no OID for algorithm: " + str);
            }
        } else if (this.kdf instanceof HKDFBytesGenerator) {
            this.kdf.init(new HKDFParameters(bArr, this.ukmParametersSalt, this.ukmParameters));
        } else {
            this.kdf.init(new KDFParameters(bArr, this.ukmParameters));
        }
        this.kdf.generateBytes(bArr3, 0, bArr3.length);
        Arrays.clear(bArr);
        return bArr3;
    }

    private byte[] calcSecret() {
        if (this.hybridSpec == null) {
            return doCalcSecret();
        }
        byte[] bArrDoCalcSecret = doCalcSecret();
        byte[] bArrConcatenate = this.hybridSpec.isPrependedT() ? Arrays.concatenate(this.hybridSpec.getT(), bArrDoCalcSecret) : Arrays.concatenate(bArrDoCalcSecret, this.hybridSpec.getT());
        Arrays.clear(bArrDoCalcSecret);
        return bArrConcatenate;
    }

    protected abstract byte[] doCalcSecret();

    protected abstract void doInitFromKey(Key key, AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException;

    static {
        Integer numValueOf = Integers.valueOf(64);
        Integer numValueOf2 = Integers.valueOf(128);
        Integer numValueOf3 = Integers.valueOf(192);
        Integer numValueOf4 = Integers.valueOf(256);
        keySizes.put("DES", numValueOf);
        keySizes.put("DESEDE", numValueOf3);
        keySizes.put("BLOWFISH", numValueOf2);
        keySizes.put("AES", numValueOf4);
        keySizes.put(NISTObjectIdentifiers.id_aes128_ECB.getId(), numValueOf2);
        keySizes.put(NISTObjectIdentifiers.id_aes192_ECB.getId(), numValueOf3);
        keySizes.put(NISTObjectIdentifiers.id_aes256_ECB.getId(), numValueOf4);
        keySizes.put(NISTObjectIdentifiers.id_aes128_CBC.getId(), numValueOf2);
        keySizes.put(NISTObjectIdentifiers.id_aes192_CBC.getId(), numValueOf3);
        keySizes.put(NISTObjectIdentifiers.id_aes256_CBC.getId(), numValueOf4);
        keySizes.put(NISTObjectIdentifiers.id_aes128_CFB.getId(), numValueOf2);
        keySizes.put(NISTObjectIdentifiers.id_aes192_CFB.getId(), numValueOf3);
        keySizes.put(NISTObjectIdentifiers.id_aes256_CFB.getId(), numValueOf4);
        keySizes.put(NISTObjectIdentifiers.id_aes128_OFB.getId(), numValueOf2);
        keySizes.put(NISTObjectIdentifiers.id_aes192_OFB.getId(), numValueOf3);
        keySizes.put(NISTObjectIdentifiers.id_aes256_OFB.getId(), numValueOf4);
        keySizes.put(NISTObjectIdentifiers.id_aes128_wrap.getId(), numValueOf2);
        keySizes.put(NISTObjectIdentifiers.id_aes192_wrap.getId(), numValueOf3);
        keySizes.put(NISTObjectIdentifiers.id_aes256_wrap.getId(), numValueOf4);
        keySizes.put(NISTObjectIdentifiers.id_aes128_CCM.getId(), numValueOf2);
        keySizes.put(NISTObjectIdentifiers.id_aes192_CCM.getId(), numValueOf3);
        keySizes.put(NISTObjectIdentifiers.id_aes256_CCM.getId(), numValueOf4);
        keySizes.put(NISTObjectIdentifiers.id_aes128_GCM.getId(), numValueOf2);
        keySizes.put(NISTObjectIdentifiers.id_aes192_GCM.getId(), numValueOf3);
        keySizes.put(NISTObjectIdentifiers.id_aes256_GCM.getId(), numValueOf4);
        keySizes.put(NTTObjectIdentifiers.id_camellia128_wrap.getId(), numValueOf2);
        keySizes.put(NTTObjectIdentifiers.id_camellia192_wrap.getId(), numValueOf3);
        keySizes.put(NTTObjectIdentifiers.id_camellia256_wrap.getId(), numValueOf4);
        keySizes.put(KISAObjectIdentifiers.id_npki_app_cmsSeed_wrap.getId(), numValueOf2);
        keySizes.put(PKCSObjectIdentifiers.id_alg_CMS3DESwrap.getId(), numValueOf3);
        keySizes.put(PKCSObjectIdentifiers.des_EDE3_CBC.getId(), numValueOf3);
        keySizes.put(OIWObjectIdentifiers.desCBC.getId(), numValueOf);
        keySizes.put(CryptoProObjectIdentifiers.gostR28147_gcfb.getId(), numValueOf4);
        keySizes.put(CryptoProObjectIdentifiers.id_Gost28147_89_None_KeyWrap.getId(), numValueOf4);
        keySizes.put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_KeyWrap.getId(), numValueOf4);
        keySizes.put(PKCSObjectIdentifiers.id_hmacWithSHA1.getId(), Integers.valueOf(160));
        keySizes.put(PKCSObjectIdentifiers.id_hmacWithSHA256.getId(), numValueOf4);
        keySizes.put(PKCSObjectIdentifiers.id_hmacWithSHA384.getId(), Integers.valueOf(384));
        keySizes.put(PKCSObjectIdentifiers.id_hmacWithSHA512.getId(), Integers.valueOf(512));
        defaultOids.put("DESEDE", PKCSObjectIdentifiers.des_EDE3_CBC);
        defaultOids.put("AES", NISTObjectIdentifiers.id_aes256_CBC);
        defaultOids.put("CAMELLIA", NTTObjectIdentifiers.id_camellia256_cbc);
        defaultOids.put("SEED", KISAObjectIdentifiers.id_seedCBC);
        defaultOids.put("DES", OIWObjectIdentifiers.desCBC);
        nameTable.put(MiscObjectIdentifiers.cast5CBC.getId(), "CAST5");
        nameTable.put(MiscObjectIdentifiers.as_sys_sec_alg_ideaCBC.getId(), "IDEA");
        nameTable.put(MiscObjectIdentifiers.cryptlib_algorithm_blowfish_ECB.getId(), "Blowfish");
        nameTable.put(MiscObjectIdentifiers.cryptlib_algorithm_blowfish_CBC.getId(), "Blowfish");
        nameTable.put(MiscObjectIdentifiers.cryptlib_algorithm_blowfish_CFB.getId(), "Blowfish");
        nameTable.put(MiscObjectIdentifiers.cryptlib_algorithm_blowfish_OFB.getId(), "Blowfish");
        nameTable.put(OIWObjectIdentifiers.desECB.getId(), "DES");
        nameTable.put(OIWObjectIdentifiers.desCBC.getId(), "DES");
        nameTable.put(OIWObjectIdentifiers.desCFB.getId(), "DES");
        nameTable.put(OIWObjectIdentifiers.desOFB.getId(), "DES");
        nameTable.put(OIWObjectIdentifiers.desEDE.getId(), "DESede");
        nameTable.put(PKCSObjectIdentifiers.des_EDE3_CBC.getId(), "DESede");
        nameTable.put(PKCSObjectIdentifiers.id_alg_CMS3DESwrap.getId(), "DESede");
        nameTable.put(PKCSObjectIdentifiers.id_alg_CMSRC2wrap.getId(), "RC2");
        nameTable.put(PKCSObjectIdentifiers.id_hmacWithSHA1.getId(), "HmacSHA1");
        nameTable.put(PKCSObjectIdentifiers.id_hmacWithSHA224.getId(), "HmacSHA224");
        nameTable.put(PKCSObjectIdentifiers.id_hmacWithSHA256.getId(), "HmacSHA256");
        nameTable.put(PKCSObjectIdentifiers.id_hmacWithSHA384.getId(), "HmacSHA384");
        nameTable.put(PKCSObjectIdentifiers.id_hmacWithSHA512.getId(), "HmacSHA512");
        nameTable.put(NTTObjectIdentifiers.id_camellia128_cbc.getId(), "Camellia");
        nameTable.put(NTTObjectIdentifiers.id_camellia192_cbc.getId(), "Camellia");
        nameTable.put(NTTObjectIdentifiers.id_camellia256_cbc.getId(), "Camellia");
        nameTable.put(NTTObjectIdentifiers.id_camellia128_wrap.getId(), "Camellia");
        nameTable.put(NTTObjectIdentifiers.id_camellia192_wrap.getId(), "Camellia");
        nameTable.put(NTTObjectIdentifiers.id_camellia256_wrap.getId(), "Camellia");
        nameTable.put(KISAObjectIdentifiers.id_npki_app_cmsSeed_wrap.getId(), "SEED");
        nameTable.put(KISAObjectIdentifiers.id_seedCBC.getId(), "SEED");
        nameTable.put(KISAObjectIdentifiers.id_seedMAC.getId(), "SEED");
        nameTable.put(CryptoProObjectIdentifiers.gostR28147_gcfb.getId(), "GOST28147");
        nameTable.put(NISTObjectIdentifiers.id_aes128_wrap.getId(), "AES");
        nameTable.put(NISTObjectIdentifiers.id_aes128_CCM.getId(), "AES");
        nameTable.put(NISTObjectIdentifiers.id_aes128_CCM.getId(), "AES");
        oids.put("DESEDE", PKCSObjectIdentifiers.des_EDE3_CBC);
        oids.put("AES", NISTObjectIdentifiers.id_aes256_CBC);
        oids.put("DES", OIWObjectIdentifiers.desCBC);
        des.put("DES", "DES");
        des.put("DESEDE", "DES");
        des.put(OIWObjectIdentifiers.desCBC.getId(), "DES");
        des.put(PKCSObjectIdentifiers.des_EDE3_CBC.getId(), "DES");
        des.put(PKCSObjectIdentifiers.id_alg_CMS3DESwrap.getId(), "DES");
    }
}
