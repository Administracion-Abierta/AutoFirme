package org.bouncycastle.pqc.crypto.newhope;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.pqc.crypto.ExchangePair;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/newhope/NHSecretKeyProcessor.class */
public class NHSecretKeyProcessor {
    private final Xof xof;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/newhope/NHSecretKeyProcessor$PartyUBuilder.class */
    public static class PartyUBuilder {
        private final AsymmetricCipherKeyPair aKp;
        private final NHAgreement agreement = new NHAgreement();
        private byte[] sharedInfo = null;
        private boolean used = false;

        public PartyUBuilder(SecureRandom secureRandom) {
            NHKeyPairGenerator nHKeyPairGenerator = new NHKeyPairGenerator();
            nHKeyPairGenerator.init(new KeyGenerationParameters(secureRandom, 2048));
            this.aKp = nHKeyPairGenerator.generateKeyPair();
            this.agreement.init(this.aKp.getPrivate());
        }

        public PartyUBuilder withSharedInfo(byte[] bArr) {
            this.sharedInfo = Arrays.clone(bArr);
            return this;
        }

        public byte[] getPartA() {
            return ((NHPublicKeyParameters) this.aKp.getPublic()).getPubData();
        }

        public NHSecretKeyProcessor build(byte[] bArr) {
            if (this.used) {
                throw new IllegalStateException("builder already used");
            }
            this.used = true;
            return new NHSecretKeyProcessor(this.agreement.calculateAgreement(new NHPublicKeyParameters(bArr)), this.sharedInfo);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/newhope/NHSecretKeyProcessor$PartyVBuilder.class */
    public static class PartyVBuilder {
        protected final SecureRandom random;
        private byte[] sharedInfo = null;
        private byte[] sharedSecret = null;
        private boolean used = false;

        public PartyVBuilder(SecureRandom secureRandom) {
            this.random = secureRandom;
        }

        public PartyVBuilder withSharedInfo(byte[] bArr) {
            this.sharedInfo = Arrays.clone(bArr);
            return this;
        }

        public byte[] getPartB(byte[] bArr) {
            ExchangePair exchangePairGenerateExchange = new NHExchangePairGenerator(this.random).generateExchange(new NHPublicKeyParameters(bArr));
            this.sharedSecret = exchangePairGenerateExchange.getSharedValue();
            return ((NHPublicKeyParameters) exchangePairGenerateExchange.getPublicKey()).getPubData();
        }

        public NHSecretKeyProcessor build() {
            if (this.used) {
                throw new IllegalStateException("builder already used");
            }
            this.used = true;
            return new NHSecretKeyProcessor(this.sharedSecret, this.sharedInfo);
        }
    }

    private NHSecretKeyProcessor(byte[] bArr, byte[] bArr2) {
        this.xof = new SHAKEDigest(256);
        this.xof.update(bArr, 0, bArr.length);
        if (bArr2 != null) {
            this.xof.update(bArr2, 0, bArr2.length);
        }
        Arrays.fill(bArr, (byte) 0);
    }

    public byte[] processKey(byte[] bArr) {
        byte[] bArr2 = new byte[bArr.length];
        this.xof.doFinal(bArr2, 0, bArr2.length);
        xor(bArr, bArr2);
        Arrays.fill(bArr2, (byte) 0);
        return bArr;
    }

    private static void xor(byte[] bArr, byte[] bArr2) {
        for (int i = 0; i != bArr.length; i++) {
            int i2 = i;
            bArr[i2] = (byte) (bArr[i2] ^ bArr2[i]);
        }
    }
}
