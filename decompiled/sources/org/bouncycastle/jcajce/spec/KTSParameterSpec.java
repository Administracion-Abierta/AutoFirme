package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/spec/KTSParameterSpec.class */
public class KTSParameterSpec extends KEMKDFSpec implements AlgorithmParameterSpec {
    private final AlgorithmParameterSpec parameterSpec;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/spec/KTSParameterSpec$Builder.class */
    public static final class Builder {
        private final String algorithmName;
        private final int keySizeInBits;
        private AlgorithmParameterSpec parameterSpec;
        private AlgorithmIdentifier kdfAlgorithm;
        private byte[] otherInfo;

        public Builder(String str, int i) {
            this(str, i, null);
        }

        public Builder(String str, int i, byte[] bArr) {
            this.algorithmName = str;
            this.keySizeInBits = i;
            this.kdfAlgorithm = new AlgorithmIdentifier(X9ObjectIdentifiers.id_kdf_kdf3, new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256));
            this.otherInfo = bArr == null ? new byte[0] : Arrays.clone(bArr);
        }

        public Builder withParameterSpec(AlgorithmParameterSpec algorithmParameterSpec) {
            this.parameterSpec = algorithmParameterSpec;
            return this;
        }

        public Builder withNoKdf() {
            this.kdfAlgorithm = null;
            return this;
        }

        public Builder withKdfAlgorithm(AlgorithmIdentifier algorithmIdentifier) {
            if (algorithmIdentifier == null) {
                throw new NullPointerException("kdfAlgorithm cannot be null");
            }
            this.kdfAlgorithm = algorithmIdentifier;
            return this;
        }

        public KTSParameterSpec build() {
            return new KTSParameterSpec(this.algorithmName, this.keySizeInBits, this.parameterSpec, this.kdfAlgorithm, this.otherInfo);
        }
    }

    protected KTSParameterSpec(String str, int i, AlgorithmParameterSpec algorithmParameterSpec, AlgorithmIdentifier algorithmIdentifier, byte[] bArr) {
        super(algorithmIdentifier, bArr, str, i);
        this.parameterSpec = algorithmParameterSpec;
    }

    public AlgorithmParameterSpec getParameterSpec() {
        return this.parameterSpec;
    }
}
