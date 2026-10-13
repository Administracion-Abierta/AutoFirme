package org.spongycastle.jcajce.provider.symmetric;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.IvParameterSpec;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.misc.CAST5CBCParameters;
import org.spongycastle.asn1.misc.MiscObjectIdentifiers;
import org.spongycastle.crypto.CipherKeyGenerator;
import org.spongycastle.crypto.engines.CAST5Engine;
import org.spongycastle.crypto.modes.CBCBlockCipher;
import org.spongycastle.jcajce.provider.config.ConfigurableProvider;
import org.spongycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;
import org.spongycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters;
import org.spongycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.spongycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.spongycastle.jcajce.provider.util.AlgorithmProvider;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/symmetric/CAST5.class */
public final class CAST5 {
    private CAST5() {
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/symmetric/CAST5$ECB.class */
    public static class ECB extends BaseBlockCipher {
        public ECB() {
            super(new CAST5Engine());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/symmetric/CAST5$CBC.class */
    public static class CBC extends BaseBlockCipher {
        public CBC() {
            super(new CBCBlockCipher(new CAST5Engine()), 64);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/symmetric/CAST5$KeyGen.class */
    public static class KeyGen extends BaseKeyGenerator {
        public KeyGen() {
            super("CAST5", 128, new CipherKeyGenerator());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/symmetric/CAST5$AlgParamGen.class */
    public static class AlgParamGen extends BaseAlgorithmParameterGenerator {
        @Override // java.security.AlgorithmParameterGeneratorSpi
        protected void engineInit(AlgorithmParameterSpec genParamSpec, SecureRandom random) throws InvalidAlgorithmParameterException {
            throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for CAST5 parameter generation.");
        }

        @Override // java.security.AlgorithmParameterGeneratorSpi
        protected AlgorithmParameters engineGenerateParameters() {
            byte[] iv = new byte[8];
            if (this.random == null) {
                this.random = new SecureRandom();
            }
            this.random.nextBytes(iv);
            try {
                AlgorithmParameters params = createParametersInstance("CAST5");
                params.init(new IvParameterSpec(iv));
                return params;
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/symmetric/CAST5$AlgParams.class */
    public static class AlgParams extends BaseAlgorithmParameters {
        private byte[] iv;
        private int keyLength = 128;

        @Override // java.security.AlgorithmParametersSpi
        protected byte[] engineGetEncoded() {
            byte[] tmp = new byte[this.iv.length];
            System.arraycopy(this.iv, 0, tmp, 0, this.iv.length);
            return tmp;
        }

        @Override // java.security.AlgorithmParametersSpi
        protected byte[] engineGetEncoded(String format) throws IOException {
            if (isASN1FormatString(format)) {
                return new CAST5CBCParameters(engineGetEncoded(), this.keyLength).getEncoded();
            }
            if (format.equals("RAW")) {
                return engineGetEncoded();
            }
            return null;
        }

        @Override // org.spongycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters
        protected AlgorithmParameterSpec localEngineGetParameterSpec(Class paramSpec) throws InvalidParameterSpecException {
            if (paramSpec == IvParameterSpec.class) {
                return new IvParameterSpec(this.iv);
            }
            throw new InvalidParameterSpecException("unknown parameter spec passed to CAST5 parameters object.");
        }

        @Override // java.security.AlgorithmParametersSpi
        protected void engineInit(AlgorithmParameterSpec paramSpec) throws InvalidParameterSpecException {
            if (paramSpec instanceof IvParameterSpec) {
                this.iv = ((IvParameterSpec) paramSpec).getIV();
                return;
            }
            throw new InvalidParameterSpecException("IvParameterSpec required to initialise a CAST5 parameters algorithm parameters object");
        }

        @Override // java.security.AlgorithmParametersSpi
        protected void engineInit(byte[] params) throws IOException {
            this.iv = new byte[params.length];
            System.arraycopy(params, 0, this.iv, 0, this.iv.length);
        }

        @Override // java.security.AlgorithmParametersSpi
        protected void engineInit(byte[] params, String format) throws IOException {
            if (isASN1FormatString(format)) {
                ASN1InputStream aIn = new ASN1InputStream(params);
                CAST5CBCParameters p = CAST5CBCParameters.getInstance(aIn.readObject());
                this.keyLength = p.getKeyLength();
                this.iv = p.getIV();
                return;
            }
            if (format.equals("RAW")) {
                engineInit(params);
                return;
            }
            throw new IOException("Unknown parameters format in IV parameters object");
        }

        @Override // java.security.AlgorithmParametersSpi
        protected String engineToString() {
            return "CAST5 Parameters";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/symmetric/CAST5$Mappings.class */
    public static class Mappings extends AlgorithmProvider {
        private static final String PREFIX = CAST5.class.getName();

        @Override // org.spongycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider provider) {
            provider.addAlgorithm("AlgorithmParameters.CAST5", PREFIX + "$AlgParams");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.1.2.840.113533.7.66.10", "CAST5");
            provider.addAlgorithm("AlgorithmParameterGenerator.CAST5", PREFIX + "$AlgParamGen");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator.1.2.840.113533.7.66.10", "CAST5");
            provider.addAlgorithm("Cipher.CAST5", PREFIX + "$ECB");
            provider.addAlgorithm("Cipher", MiscObjectIdentifiers.cast5CBC, PREFIX + "$CBC");
            provider.addAlgorithm("KeyGenerator.CAST5", PREFIX + "$KeyGen");
            provider.addAlgorithm("Alg.Alias.KeyGenerator", MiscObjectIdentifiers.cast5CBC, "CAST5");
        }
    }
}
