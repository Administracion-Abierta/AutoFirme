package org.bouncycastle.jcajce.provider.symmetric;

import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.jcajce.spec.TLSKeyMaterialSpec;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/TLSKDF.class */
public class TLSKDF {

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/TLSKDF$Mappings.class */
    public static class Mappings extends AlgorithmProvider {
        private static final String PREFIX = TLSKDF.class.getName();

        @Override // org.bouncycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("SecretKeyFactory.TLS10KDF", PREFIX + "$TLS10");
            configurableProvider.addAlgorithm("SecretKeyFactory.TLS11KDF", PREFIX + "$TLS11");
            configurableProvider.addAlgorithm("SecretKeyFactory.TLS12WITHSHA256KDF", PREFIX + "$TLS12withSHA256");
            configurableProvider.addAlgorithm("SecretKeyFactory.TLS12WITHSHA384KDF", PREFIX + "$TLS12withSHA384");
            configurableProvider.addAlgorithm("SecretKeyFactory.TLS12WITHSHA512KDF", PREFIX + "$TLS12withSHA512");
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/TLSKDF$TLS10.class */
    public static final class TLS10 extends TLSKeyMaterialFactory {
        public TLS10() {
            super("TLS10KDF");
        }

        @Override // org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory, javax.crypto.SecretKeyFactorySpi
        protected SecretKey engineGenerateSecret(KeySpec keySpec) throws InvalidKeySpecException {
            if (keySpec instanceof TLSKeyMaterialSpec) {
                return new SecretKeySpec(TLSKDF.PRF_legacy((TLSKeyMaterialSpec) keySpec), this.algName);
            }
            throw new InvalidKeySpecException("Invalid KeySpec");
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/TLSKDF$TLS11.class */
    public static final class TLS11 extends TLSKeyMaterialFactory {
        public TLS11() {
            super("TLS11KDF");
        }

        @Override // org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory, javax.crypto.SecretKeyFactorySpi
        protected SecretKey engineGenerateSecret(KeySpec keySpec) throws InvalidKeySpecException {
            if (keySpec instanceof TLSKeyMaterialSpec) {
                return new SecretKeySpec(TLSKDF.PRF_legacy((TLSKeyMaterialSpec) keySpec), this.algName);
            }
            throw new InvalidKeySpecException("Invalid KeySpec");
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/TLSKDF$TLS12.class */
    public static class TLS12 extends TLSKeyMaterialFactory {
        private final Mac prf;

        protected TLS12(String str, Mac mac) {
            super(str);
            this.prf = mac;
        }

        @Override // org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory, javax.crypto.SecretKeyFactorySpi
        protected SecretKey engineGenerateSecret(KeySpec keySpec) throws InvalidKeySpecException {
            if (keySpec instanceof TLSKeyMaterialSpec) {
                return new SecretKeySpec(PRF((TLSKeyMaterialSpec) keySpec, this.prf), this.algName);
            }
            throw new InvalidKeySpecException("Invalid KeySpec");
        }

        private byte[] PRF(TLSKeyMaterialSpec tLSKeyMaterialSpec, Mac mac) {
            byte[] bArrConcatenate = Arrays.concatenate(Strings.toByteArray(tLSKeyMaterialSpec.getLabel()), tLSKeyMaterialSpec.getSeed());
            byte[] secret = tLSKeyMaterialSpec.getSecret();
            byte[] bArr = new byte[tLSKeyMaterialSpec.getLength()];
            TLSKDF.hmac_hash(mac, secret, bArrConcatenate, bArr);
            return bArr;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/TLSKDF$TLS12withSHA256.class */
    public static final class TLS12withSHA256 extends TLS12 {
        public TLS12withSHA256() {
            super("TLS12withSHA256KDF", new HMac(SHA256Digest.newInstance()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/TLSKDF$TLS12withSHA384.class */
    public static final class TLS12withSHA384 extends TLS12 {
        public TLS12withSHA384() {
            super("TLS12withSHA384KDF", new HMac(new SHA384Digest()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/TLSKDF$TLS12withSHA512.class */
    public static final class TLS12withSHA512 extends TLS12 {
        public TLS12withSHA512() {
            super("TLS12withSHA512KDF", new HMac(new SHA512Digest()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/TLSKDF$TLSKeyMaterialFactory.class */
    public static class TLSKeyMaterialFactory extends BaseSecretKeyFactory {
        protected TLSKeyMaterialFactory(String str) {
            super(str, null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static byte[] PRF_legacy(TLSKeyMaterialSpec tLSKeyMaterialSpec) {
        HMac hMac = new HMac(DigestFactory.createMD5());
        HMac hMac2 = new HMac(DigestFactory.createSHA1());
        byte[] bArrConcatenate = Arrays.concatenate(Strings.toByteArray(tLSKeyMaterialSpec.getLabel()), tLSKeyMaterialSpec.getSeed());
        byte[] secret = tLSKeyMaterialSpec.getSecret();
        int length = (secret.length + 1) / 2;
        byte[] bArr = new byte[length];
        byte[] bArr2 = new byte[length];
        System.arraycopy(secret, 0, bArr, 0, length);
        System.arraycopy(secret, secret.length - length, bArr2, 0, length);
        int length2 = tLSKeyMaterialSpec.getLength();
        byte[] bArr3 = new byte[length2];
        byte[] bArr4 = new byte[length2];
        hmac_hash(hMac, bArr, bArrConcatenate, bArr3);
        hmac_hash(hMac2, bArr2, bArrConcatenate, bArr4);
        for (int i = 0; i < length2; i++) {
            int i2 = i;
            bArr3[i2] = (byte) (bArr3[i2] ^ bArr4[i]);
        }
        return bArr3;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void hmac_hash(Mac mac, byte[] bArr, byte[] bArr2, byte[] bArr3) {
        mac.init(new KeyParameter(bArr));
        byte[] bArr4 = bArr2;
        int macSize = mac.getMacSize();
        int length = ((bArr3.length + macSize) - 1) / macSize;
        byte[] bArr5 = new byte[mac.getMacSize()];
        byte[] bArr6 = new byte[mac.getMacSize()];
        for (int i = 0; i < length; i++) {
            mac.update(bArr4, 0, bArr4.length);
            mac.doFinal(bArr5, 0);
            bArr4 = bArr5;
            mac.update(bArr4, 0, bArr4.length);
            mac.update(bArr2, 0, bArr2.length);
            mac.doFinal(bArr6, 0);
            System.arraycopy(bArr6, 0, bArr3, macSize * i, Math.min(macSize, bArr3.length - (macSize * i)));
        }
    }
}
