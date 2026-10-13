package org.bouncycastle.jcajce.provider.symmetric;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.engines.ARIAEngine;
import org.bouncycastle.crypto.engines.ARIAWrapEngine;
import org.bouncycastle.crypto.engines.ARIAWrapPadEngine;
import org.bouncycastle.crypto.engines.RFC3211WrapEngine;
import org.bouncycastle.crypto.generators.Poly1305KeyGenerator;
import org.bouncycastle.crypto.macs.GMac;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.CCMBlockCipher;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.modes.OFBBlockCipher;
import org.bouncycastle.internal.asn1.cms.CCMParameters;
import org.bouncycastle.internal.asn1.cms.GCMParameters;
import org.bouncycastle.internal.asn1.nsri.NSRIObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BlockCipherProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.GcmSpecUtil;
import org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;
import org.bouncycastle.jcajce.spec.AEADParameterSpec;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/ARIA.class */
public final class ARIA {

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/ARIA$AlgParamGen.class */
    public static class AlgParamGen extends BaseAlgorithmParameterGenerator {
        @Override // java.security.AlgorithmParameterGeneratorSpi
        protected void engineInit(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
            throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for ARIA parameter generation.");
        }

        @Override // java.security.AlgorithmParameterGeneratorSpi
        protected AlgorithmParameters engineGenerateParameters() {
            byte[] bArr = new byte[16];
            if (this.random == null) {
                this.random = CryptoServicesRegistrar.getSecureRandom();
            }
            this.random.nextBytes(bArr);
            try {
                AlgorithmParameters algorithmParametersCreateParametersInstance = createParametersInstance("ARIA");
                algorithmParametersCreateParametersInstance.init(new IvParameterSpec(bArr));
                return algorithmParametersCreateParametersInstance;
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/ARIA$AlgParams.class */
    public static class AlgParams extends IvAlgorithmParameters {
        @Override // org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters, java.security.AlgorithmParametersSpi
        protected String engineToString() {
            return "ARIA IV";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/ARIA$AlgParamsCCM.class */
    public static class AlgParamsCCM extends BaseAlgorithmParameters {
        private CCMParameters ccmParams;

        @Override // java.security.AlgorithmParametersSpi
        protected void engineInit(AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException {
            if (GcmSpecUtil.isGcmSpec(algorithmParameterSpec)) {
                this.ccmParams = CCMParameters.getInstance(GcmSpecUtil.extractGcmParameters(algorithmParameterSpec));
            } else {
                if (!(algorithmParameterSpec instanceof AEADParameterSpec)) {
                    throw new InvalidParameterSpecException("AlgorithmParameterSpec class not recognized: " + algorithmParameterSpec.getClass().getName());
                }
                this.ccmParams = new CCMParameters(((AEADParameterSpec) algorithmParameterSpec).getNonce(), ((AEADParameterSpec) algorithmParameterSpec).getMacSizeInBits() / 8);
            }
        }

        @Override // java.security.AlgorithmParametersSpi
        protected void engineInit(byte[] bArr) throws IOException {
            this.ccmParams = CCMParameters.getInstance(bArr);
        }

        @Override // java.security.AlgorithmParametersSpi
        protected void engineInit(byte[] bArr, String str) throws IOException {
            if (!isASN1FormatString(str)) {
                throw new IOException("unknown format specified");
            }
            this.ccmParams = CCMParameters.getInstance(bArr);
        }

        @Override // java.security.AlgorithmParametersSpi
        protected byte[] engineGetEncoded() throws IOException {
            return this.ccmParams.getEncoded();
        }

        @Override // java.security.AlgorithmParametersSpi
        protected byte[] engineGetEncoded(String str) throws IOException {
            if (isASN1FormatString(str)) {
                return this.ccmParams.getEncoded();
            }
            throw new IOException("unknown format specified");
        }

        @Override // java.security.AlgorithmParametersSpi
        protected String engineToString() {
            return "CCM";
        }

        @Override // org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters
        protected AlgorithmParameterSpec localEngineGetParameterSpec(Class cls) throws InvalidParameterSpecException {
            if (cls == AlgorithmParameterSpec.class || GcmSpecUtil.isGcmSpec(cls)) {
                return GcmSpecUtil.gcmSpecExtractable() ? GcmSpecUtil.extractGcmSpec(this.ccmParams.toASN1Primitive()) : new AEADParameterSpec(this.ccmParams.getNonce(), this.ccmParams.getIcvLen() * 8);
            }
            if (cls == AEADParameterSpec.class) {
                return new AEADParameterSpec(this.ccmParams.getNonce(), this.ccmParams.getIcvLen() * 8);
            }
            if (cls == IvParameterSpec.class) {
                return new IvParameterSpec(this.ccmParams.getNonce());
            }
            throw new InvalidParameterSpecException("AlgorithmParameterSpec not recognized: " + cls.getName());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/ARIA$AlgParamsGCM.class */
    public static class AlgParamsGCM extends BaseAlgorithmParameters {
        private GCMParameters gcmParams;

        @Override // java.security.AlgorithmParametersSpi
        protected void engineInit(AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException {
            if (GcmSpecUtil.isGcmSpec(algorithmParameterSpec)) {
                this.gcmParams = GcmSpecUtil.extractGcmParameters(algorithmParameterSpec);
            } else {
                if (!(algorithmParameterSpec instanceof AEADParameterSpec)) {
                    throw new InvalidParameterSpecException("AlgorithmParameterSpec class not recognized: " + algorithmParameterSpec.getClass().getName());
                }
                this.gcmParams = new GCMParameters(((AEADParameterSpec) algorithmParameterSpec).getNonce(), ((AEADParameterSpec) algorithmParameterSpec).getMacSizeInBits() / 8);
            }
        }

        @Override // java.security.AlgorithmParametersSpi
        protected void engineInit(byte[] bArr) throws IOException {
            this.gcmParams = GCMParameters.getInstance(bArr);
        }

        @Override // java.security.AlgorithmParametersSpi
        protected void engineInit(byte[] bArr, String str) throws IOException {
            if (!isASN1FormatString(str)) {
                throw new IOException("unknown format specified");
            }
            this.gcmParams = GCMParameters.getInstance(bArr);
        }

        @Override // java.security.AlgorithmParametersSpi
        protected byte[] engineGetEncoded() throws IOException {
            return this.gcmParams.getEncoded();
        }

        @Override // java.security.AlgorithmParametersSpi
        protected byte[] engineGetEncoded(String str) throws IOException {
            if (isASN1FormatString(str)) {
                return this.gcmParams.getEncoded();
            }
            throw new IOException("unknown format specified");
        }

        @Override // java.security.AlgorithmParametersSpi
        protected String engineToString() {
            return "GCM";
        }

        @Override // org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters
        protected AlgorithmParameterSpec localEngineGetParameterSpec(Class cls) throws InvalidParameterSpecException {
            if (cls == AlgorithmParameterSpec.class || GcmSpecUtil.isGcmSpec(cls)) {
                return GcmSpecUtil.gcmSpecExtractable() ? GcmSpecUtil.extractGcmSpec(this.gcmParams.toASN1Primitive()) : new AEADParameterSpec(this.gcmParams.getNonce(), this.gcmParams.getIcvLen() * 8);
            }
            if (cls == AEADParameterSpec.class) {
                return new AEADParameterSpec(this.gcmParams.getNonce(), this.gcmParams.getIcvLen() * 8);
            }
            if (cls == IvParameterSpec.class) {
                return new IvParameterSpec(this.gcmParams.getNonce());
            }
            throw new InvalidParameterSpecException("AlgorithmParameterSpec not recognized: " + cls.getName());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/ARIA$CBC.class */
    public static class CBC extends BaseBlockCipher {
        public CBC() {
            super(new CBCBlockCipher(new ARIAEngine()), 128);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/ARIA$CCM.class */
    public static class CCM extends BaseBlockCipher {
        public CCM() {
            super((AEADBlockCipher) new CCMBlockCipher(new ARIAEngine()), false, 12);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/ARIA$CFB.class */
    public static class CFB extends BaseBlockCipher {
        public CFB() {
            super(new BufferedBlockCipher(new CFBBlockCipher(new ARIAEngine(), 128)), 128);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/ARIA$ECB.class */
    public static class ECB extends BaseBlockCipher {
        public ECB() {
            super(new BlockCipherProvider() { // from class: org.bouncycastle.jcajce.provider.symmetric.ARIA.ECB.1
                @Override // org.bouncycastle.jcajce.provider.symmetric.util.BlockCipherProvider
                public BlockCipher get() {
                    return new ARIAEngine();
                }
            });
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/ARIA$GCM.class */
    public static class GCM extends BaseBlockCipher {
        public GCM() {
            super(new GCMBlockCipher(new ARIAEngine()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/ARIA$GMAC.class */
    public static class GMAC extends BaseMac {
        public GMAC() {
            super(new GMac(new GCMBlockCipher(new ARIAEngine())));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/ARIA$KeyFactory.class */
    public static class KeyFactory extends BaseSecretKeyFactory {
        public KeyFactory() {
            super("ARIA", null);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/ARIA$KeyGen.class */
    public static class KeyGen extends BaseKeyGenerator {
        public KeyGen() {
            this(256);
        }

        public KeyGen(int i) {
            super("ARIA", i, new CipherKeyGenerator());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/ARIA$KeyGen128.class */
    public static class KeyGen128 extends KeyGen {
        public KeyGen128() {
            super(128);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/ARIA$KeyGen192.class */
    public static class KeyGen192 extends KeyGen {
        public KeyGen192() {
            super(192);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/ARIA$KeyGen256.class */
    public static class KeyGen256 extends KeyGen {
        public KeyGen256() {
            super(256);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/ARIA$Mappings.class */
    public static class Mappings extends SymmetricAlgorithmProvider {
        private static final String PREFIX = ARIA.class.getName();

        @Override // org.bouncycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("AlgorithmParameters.ARIA", PREFIX + "$AlgParams");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters", NSRIObjectIdentifiers.id_aria128_cbc, "ARIA");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters", NSRIObjectIdentifiers.id_aria192_cbc, "ARIA");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters", NSRIObjectIdentifiers.id_aria256_cbc, "ARIA");
            configurableProvider.addAlgorithm("AlgorithmParameterGenerator.ARIA", PREFIX + "$AlgParamGen");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria128_cbc, "ARIA");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria192_cbc, "ARIA");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria256_cbc, "ARIA");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria128_ofb, "ARIA");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria192_ofb, "ARIA");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria256_ofb, "ARIA");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria128_cfb, "ARIA");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria192_cfb, "ARIA");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria256_cfb, "ARIA");
            configurableProvider.addAlgorithm("Cipher.ARIA", PREFIX + "$ECB");
            configurableProvider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria128_ecb, PREFIX + "$ECB");
            configurableProvider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria192_ecb, PREFIX + "$ECB");
            configurableProvider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria256_ecb, PREFIX + "$ECB");
            configurableProvider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria128_cbc, PREFIX + "$CBC");
            configurableProvider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria192_cbc, PREFIX + "$CBC");
            configurableProvider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria256_cbc, PREFIX + "$CBC");
            configurableProvider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria128_cfb, PREFIX + "$CFB");
            configurableProvider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria192_cfb, PREFIX + "$CFB");
            configurableProvider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria256_cfb, PREFIX + "$CFB");
            configurableProvider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria128_ofb, PREFIX + "$OFB");
            configurableProvider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria192_ofb, PREFIX + "$OFB");
            configurableProvider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria256_ofb, PREFIX + "$OFB");
            configurableProvider.addAlgorithm("Cipher.ARIARFC3211WRAP", PREFIX + "$RFC3211Wrap");
            configurableProvider.addAlgorithm("Cipher.ARIAWRAP", PREFIX + "$Wrap");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria128_kw, "ARIAWRAP");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria192_kw, "ARIAWRAP");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria256_kw, "ARIAWRAP");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.ARIAKW", "ARIAWRAP");
            configurableProvider.addAlgorithm("Cipher.ARIAWRAPPAD", PREFIX + "$WrapPad");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria128_kwp, "ARIAWRAPPAD");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria192_kwp, "ARIAWRAPPAD");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria256_kwp, "ARIAWRAPPAD");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.ARIAKWP", "ARIAWRAPPAD");
            configurableProvider.addAlgorithm("KeyGenerator.ARIA", PREFIX + "$KeyGen");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria128_kw, PREFIX + "$KeyGen128");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria192_kw, PREFIX + "$KeyGen192");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria256_kw, PREFIX + "$KeyGen256");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria128_kwp, PREFIX + "$KeyGen128");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria192_kwp, PREFIX + "$KeyGen192");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria256_kwp, PREFIX + "$KeyGen256");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria128_ecb, PREFIX + "$KeyGen128");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria192_ecb, PREFIX + "$KeyGen192");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria256_ecb, PREFIX + "$KeyGen256");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria128_cbc, PREFIX + "$KeyGen128");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria192_cbc, PREFIX + "$KeyGen192");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria256_cbc, PREFIX + "$KeyGen256");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria128_cfb, PREFIX + "$KeyGen128");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria192_cfb, PREFIX + "$KeyGen192");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria256_cfb, PREFIX + "$KeyGen256");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria128_ofb, PREFIX + "$KeyGen128");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria192_ofb, PREFIX + "$KeyGen192");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria256_ofb, PREFIX + "$KeyGen256");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria128_ccm, PREFIX + "$KeyGen128");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria192_ccm, PREFIX + "$KeyGen192");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria256_ccm, PREFIX + "$KeyGen256");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria128_gcm, PREFIX + "$KeyGen128");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria192_gcm, PREFIX + "$KeyGen192");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria256_gcm, PREFIX + "$KeyGen256");
            configurableProvider.addAlgorithm("SecretKeyFactory.ARIA", PREFIX + "$KeyFactory");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory", NSRIObjectIdentifiers.id_aria128_cbc, "ARIA");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory", NSRIObjectIdentifiers.id_aria192_cbc, "ARIA");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory", NSRIObjectIdentifiers.id_aria256_cbc, "ARIA");
            configurableProvider.addAlgorithm("AlgorithmParameterGenerator.ARIACCM", PREFIX + "$AlgParamGen");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NSRIObjectIdentifiers.id_aria128_ccm, "ARIACCM");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NSRIObjectIdentifiers.id_aria192_ccm, "ARIACCM");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NSRIObjectIdentifiers.id_aria256_ccm, "ARIACCM");
            configurableProvider.addAlgorithm("Cipher.ARIACCM", PREFIX + "$CCM");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria128_ccm, "CCM");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria192_ccm, "CCM");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria256_ccm, "CCM");
            configurableProvider.addAlgorithm("AlgorithmParameterGenerator.ARIAGCM", PREFIX + "$AlgParamGen");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NSRIObjectIdentifiers.id_aria128_gcm, "ARIAGCM");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NSRIObjectIdentifiers.id_aria192_gcm, "ARIAGCM");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NSRIObjectIdentifiers.id_aria256_gcm, "ARIAGCM");
            configurableProvider.addAlgorithm("Cipher.ARIAGCM", PREFIX + "$GCM");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria128_gcm, "ARIAGCM");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria192_gcm, "ARIAGCM");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria256_gcm, "ARIAGCM");
            addGMacAlgorithm(configurableProvider, "ARIA", PREFIX + "$GMAC", PREFIX + "$KeyGen");
            addPoly1305Algorithm(configurableProvider, "ARIA", PREFIX + "$Poly1305", PREFIX + "$Poly1305KeyGen");
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/ARIA$OFB.class */
    public static class OFB extends BaseBlockCipher {
        public OFB() {
            super(new BufferedBlockCipher(new OFBBlockCipher(new ARIAEngine(), 128)), 128);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/ARIA$Poly1305.class */
    public static class Poly1305 extends BaseMac {
        public Poly1305() {
            super(new org.bouncycastle.crypto.macs.Poly1305(new ARIAEngine()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/ARIA$Poly1305KeyGen.class */
    public static class Poly1305KeyGen extends BaseKeyGenerator {
        public Poly1305KeyGen() {
            super("Poly1305-ARIA", 256, new Poly1305KeyGenerator());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/ARIA$RFC3211Wrap.class */
    public static class RFC3211Wrap extends BaseWrapCipher {
        public RFC3211Wrap() {
            super(new RFC3211WrapEngine(new ARIAEngine()), 16);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/ARIA$Wrap.class */
    public static class Wrap extends BaseWrapCipher {
        public Wrap() {
            super(new ARIAWrapEngine());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/ARIA$WrapPad.class */
    public static class WrapPad extends BaseWrapCipher {
        public WrapPad() {
            super(new ARIAWrapPadEngine());
        }
    }

    private ARIA() {
    }
}
