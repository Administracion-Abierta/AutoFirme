package org.bouncycastle.jcajce.spec;

import java.security.PrivateKey;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/spec/KEMExtractSpec.class */
public class KEMExtractSpec extends KEMKDFSpec implements AlgorithmParameterSpec {
    private static final byte[] EMPTY_OTHER_INFO = new byte[0];
    private static AlgorithmIdentifier DefKdf = new AlgorithmIdentifier(X9ObjectIdentifiers.id_kdf_kdf3, new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256));
    private final PrivateKey privateKey;
    private final byte[] encapsulation;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/spec/KEMExtractSpec$Builder.class */
    public static final class Builder {
        private final PrivateKey privateKey;
        private final byte[] encapsulation;
        private final String algorithmName;
        private final int keySizeInBits;
        private AlgorithmIdentifier kdfAlgorithm = new AlgorithmIdentifier(X9ObjectIdentifiers.id_kdf_kdf3, new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256));
        private byte[] otherInfo = KEMExtractSpec.EMPTY_OTHER_INFO;

        public Builder(PrivateKey privateKey, byte[] bArr, String str, int i) {
            this.privateKey = privateKey;
            this.encapsulation = Arrays.clone(bArr);
            this.algorithmName = str;
            this.keySizeInBits = i;
        }

        public Builder withNoKdf() {
            this.kdfAlgorithm = null;
            return this;
        }

        public Builder withKdfAlgorithm(AlgorithmIdentifier algorithmIdentifier) {
            this.kdfAlgorithm = algorithmIdentifier;
            return this;
        }

        public Builder withOtherInfo(byte[] bArr) {
            this.otherInfo = bArr == null ? KEMExtractSpec.EMPTY_OTHER_INFO : Arrays.clone(bArr);
            return this;
        }

        public KEMExtractSpec build() {
            return new KEMExtractSpec(this.privateKey, this.encapsulation, this.algorithmName, this.keySizeInBits, this.kdfAlgorithm, this.otherInfo);
        }
    }

    private KEMExtractSpec(PrivateKey privateKey, byte[] bArr, String str, int i, AlgorithmIdentifier algorithmIdentifier, byte[] bArr2) {
        super(algorithmIdentifier, bArr2, str, i);
        this.privateKey = privateKey;
        this.encapsulation = Arrays.clone(bArr);
    }

    public KEMExtractSpec(PrivateKey privateKey, byte[] bArr, String str) {
        this(privateKey, bArr, str, 256);
    }

    public KEMExtractSpec(PrivateKey privateKey, byte[] bArr, String str, int i) {
        this(privateKey, bArr, str, i, DefKdf, EMPTY_OTHER_INFO);
    }

    public byte[] getEncapsulation() {
        return Arrays.clone(this.encapsulation);
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }
}
