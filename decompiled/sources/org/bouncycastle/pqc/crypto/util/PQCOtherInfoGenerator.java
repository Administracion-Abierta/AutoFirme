package org.bouncycastle.pqc.crypto.util;

import java.io.IOException;
import java.security.SecureRandom;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.EncapsulatedSecretExtractor;
import org.bouncycastle.crypto.EncapsulatedSecretGenerator;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.DEROtherInfo;
import org.bouncycastle.pqc.crypto.KEMParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMExtractor;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMGenerator;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMKeyPairGenerator;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.ntru.NTRUKEMExtractor;
import org.bouncycastle.pqc.crypto.ntru.NTRUKEMGenerator;
import org.bouncycastle.pqc.crypto.ntru.NTRUKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.ntru.NTRUKeyPairGenerator;
import org.bouncycastle.pqc.crypto.ntru.NTRUParameters;
import org.bouncycastle.pqc.crypto.ntru.NTRUPrivateKeyParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/util/PQCOtherInfoGenerator.class */
public class PQCOtherInfoGenerator {
    protected final DEROtherInfo.Builder otherInfoBuilder;
    protected final SecureRandom random;
    protected boolean used = false;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/util/PQCOtherInfoGenerator$PartyU.class */
    public static class PartyU extends PQCOtherInfoGenerator {
        private AsymmetricCipherKeyPair aKp;
        private EncapsulatedSecretExtractor encSE;

        public PartyU(KEMParameters kEMParameters, AlgorithmIdentifier algorithmIdentifier, byte[] bArr, byte[] bArr2, SecureRandom secureRandom) {
            super(algorithmIdentifier, bArr, bArr2, secureRandom);
            if (kEMParameters instanceof MLKEMParameters) {
                MLKEMKeyPairGenerator mLKEMKeyPairGenerator = new MLKEMKeyPairGenerator();
                mLKEMKeyPairGenerator.init(new MLKEMKeyGenerationParameters(secureRandom, (MLKEMParameters) kEMParameters));
                this.aKp = mLKEMKeyPairGenerator.generateKeyPair();
                this.encSE = new MLKEMExtractor((MLKEMPrivateKeyParameters) this.aKp.getPrivate());
                return;
            }
            if (!(kEMParameters instanceof NTRUParameters)) {
                throw new IllegalArgumentException("unknown KEMParameters");
            }
            NTRUKeyPairGenerator nTRUKeyPairGenerator = new NTRUKeyPairGenerator();
            nTRUKeyPairGenerator.init(new NTRUKeyGenerationParameters(secureRandom, (NTRUParameters) kEMParameters));
            this.aKp = nTRUKeyPairGenerator.generateKeyPair();
            this.encSE = new NTRUKEMExtractor((NTRUPrivateKeyParameters) this.aKp.getPrivate());
        }

        public PQCOtherInfoGenerator withSuppPubInfo(byte[] bArr) {
            this.otherInfoBuilder.withSuppPubInfo(bArr);
            return this;
        }

        public byte[] getSuppPrivInfoPartA() {
            return PQCOtherInfoGenerator.getEncoded(this.aKp.getPublic());
        }

        public DEROtherInfo generate(byte[] bArr) {
            this.otherInfoBuilder.withSuppPrivInfo(this.encSE.extractSecret(bArr));
            return this.otherInfoBuilder.build();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/util/PQCOtherInfoGenerator$PartyV.class */
    public static class PartyV extends PQCOtherInfoGenerator {
        private EncapsulatedSecretGenerator encSG;

        public PartyV(KEMParameters kEMParameters, AlgorithmIdentifier algorithmIdentifier, byte[] bArr, byte[] bArr2, SecureRandom secureRandom) {
            super(algorithmIdentifier, bArr, bArr2, secureRandom);
            if (kEMParameters instanceof MLKEMParameters) {
                this.encSG = new MLKEMGenerator(secureRandom);
            } else {
                if (!(kEMParameters instanceof NTRUParameters)) {
                    throw new IllegalArgumentException("unknown KEMParameters");
                }
                this.encSG = new NTRUKEMGenerator(secureRandom);
            }
        }

        public PQCOtherInfoGenerator withSuppPubInfo(byte[] bArr) {
            this.otherInfoBuilder.withSuppPubInfo(bArr);
            return this;
        }

        public byte[] getSuppPrivInfoPartB(byte[] bArr) {
            this.used = false;
            try {
                SecretWithEncapsulation secretWithEncapsulationGenerateEncapsulated = this.encSG.generateEncapsulated(PQCOtherInfoGenerator.getPublicKey(bArr));
                this.otherInfoBuilder.withSuppPrivInfo(secretWithEncapsulationGenerateEncapsulated.getSecret());
                return secretWithEncapsulationGenerateEncapsulated.getEncapsulation();
            } catch (IOException e) {
                throw new IllegalArgumentException("cannot decode public key");
            }
        }

        public DEROtherInfo generate() {
            if (this.used) {
                throw new IllegalStateException("builder already used");
            }
            this.used = true;
            return this.otherInfoBuilder.build();
        }
    }

    public PQCOtherInfoGenerator(AlgorithmIdentifier algorithmIdentifier, byte[] bArr, byte[] bArr2, SecureRandom secureRandom) {
        this.otherInfoBuilder = new DEROtherInfo.Builder(algorithmIdentifier, bArr, bArr2);
        this.random = secureRandom;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static byte[] getEncoded(AsymmetricKeyParameter asymmetricKeyParameter) {
        try {
            return SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(asymmetricKeyParameter).getEncoded();
        } catch (IOException e) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static AsymmetricKeyParameter getPublicKey(byte[] bArr) throws IOException {
        return PublicKeyFactory.createKey(bArr);
    }
}
