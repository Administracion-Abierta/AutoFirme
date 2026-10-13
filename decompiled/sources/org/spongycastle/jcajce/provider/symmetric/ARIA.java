package org.spongycastle.jcajce.provider.symmetric;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.IvParameterSpec;
import org.spongycastle.asn1.cms.CCMParameters;
import org.spongycastle.asn1.cms.GCMParameters;
import org.spongycastle.asn1.nsri.NSRIObjectIdentifiers;
import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.BufferedBlockCipher;
import org.spongycastle.crypto.CipherKeyGenerator;
import org.spongycastle.crypto.engines.ARIAEngine;
import org.spongycastle.crypto.engines.ARIAWrapEngine;
import org.spongycastle.crypto.engines.ARIAWrapPadEngine;
import org.spongycastle.crypto.engines.RFC3211WrapEngine;
import org.spongycastle.crypto.generators.Poly1305KeyGenerator;
import org.spongycastle.crypto.macs.GMac;
import org.spongycastle.crypto.modes.CBCBlockCipher;
import org.spongycastle.crypto.modes.CFBBlockCipher;
import org.spongycastle.crypto.modes.GCMBlockCipher;
import org.spongycastle.crypto.modes.OFBBlockCipher;
import org.spongycastle.jcajce.provider.config.ConfigurableProvider;
import org.spongycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;
import org.spongycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters;
import org.spongycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.spongycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.spongycastle.jcajce.provider.symmetric.util.BaseMac;
import org.spongycastle.jcajce.provider.symmetric.util.BaseWrapCipher;
import org.spongycastle.jcajce.provider.symmetric.util.BlockCipherProvider;
import org.spongycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;
import org.spongycastle.jcajce.spec.AEADParameterSpec;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/symmetric/ARIA.class */
public final class ARIA {
    private ARIA() {
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/symmetric/ARIA$ECB.class */
    public static class ECB extends BaseBlockCipher {
        public ECB() {
            super(new BlockCipherProvider() { // from class: org.spongycastle.jcajce.provider.symmetric.ARIA.ECB.1
                @Override // org.spongycastle.jcajce.provider.symmetric.util.BlockCipherProvider
                public BlockCipher get() {
                    return new ARIAEngine();
                }
            });
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/symmetric/ARIA$CBC.class */
    public static class CBC extends BaseBlockCipher {
        public CBC() {
            super(new CBCBlockCipher(new ARIAEngine()), 128);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/symmetric/ARIA$CFB.class */
    public static class CFB extends BaseBlockCipher {
        public CFB() {
            super(new BufferedBlockCipher(new CFBBlockCipher(new ARIAEngine(), 128)), 128);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/symmetric/ARIA$OFB.class */
    public static class OFB extends BaseBlockCipher {
        public OFB() {
            super(new BufferedBlockCipher(new OFBBlockCipher(new ARIAEngine(), 128)), 128);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/symmetric/ARIA$Wrap.class */
    public static class Wrap extends BaseWrapCipher {
        public Wrap() {
            super(new ARIAWrapEngine());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/symmetric/ARIA$WrapPad.class */
    public static class WrapPad extends BaseWrapCipher {
        public WrapPad() {
            super(new ARIAWrapPadEngine());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/symmetric/ARIA$RFC3211Wrap.class */
    public static class RFC3211Wrap extends BaseWrapCipher {
        public RFC3211Wrap() {
            super(new RFC3211WrapEngine(new ARIAEngine()), 16);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/symmetric/ARIA$GMAC.class */
    public static class GMAC extends BaseMac {
        public GMAC() {
            super(new GMac(new GCMBlockCipher(new ARIAEngine())));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/symmetric/ARIA$Poly1305.class */
    public static class Poly1305 extends BaseMac {
        public Poly1305() {
            super(new org.spongycastle.crypto.macs.Poly1305(new ARIAEngine()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/symmetric/ARIA$Poly1305KeyGen.class */
    public static class Poly1305KeyGen extends BaseKeyGenerator {
        public Poly1305KeyGen() {
            super("Poly1305-ARIA", 256, new Poly1305KeyGenerator());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/symmetric/ARIA$KeyGen.class */
    public static class KeyGen extends BaseKeyGenerator {
        public KeyGen() {
            this(256);
        }

        public KeyGen(int keySize) {
            super("ARIA", keySize, new CipherKeyGenerator());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/symmetric/ARIA$KeyGen128.class */
    public static class KeyGen128 extends KeyGen {
        public KeyGen128() {
            super(128);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/symmetric/ARIA$KeyGen192.class */
    public static class KeyGen192 extends KeyGen {
        public KeyGen192() {
            super(192);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/symmetric/ARIA$KeyGen256.class */
    public static class KeyGen256 extends KeyGen {
        public KeyGen256() {
            super(256);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/symmetric/ARIA$AlgParamGen.class */
    public static class AlgParamGen extends BaseAlgorithmParameterGenerator {
        @Override // java.security.AlgorithmParameterGeneratorSpi
        protected void engineInit(AlgorithmParameterSpec genParamSpec, SecureRandom random) throws InvalidAlgorithmParameterException {
            throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for ARIA parameter generation.");
        }

        @Override // java.security.AlgorithmParameterGeneratorSpi
        protected AlgorithmParameters engineGenerateParameters() {
            byte[] iv = new byte[16];
            if (this.random == null) {
                this.random = new SecureRandom();
            }
            this.random.nextBytes(iv);
            try {
                AlgorithmParameters params = createParametersInstance("ARIA");
                params.init(new IvParameterSpec(iv));
                return params;
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/symmetric/ARIA$AlgParams.class */
    public static class AlgParams extends IvAlgorithmParameters {
        @Override // org.spongycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters, java.security.AlgorithmParametersSpi
        protected String engineToString() {
            return "ARIA IV";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/symmetric/ARIA$AlgParamsGCM.class */
    public static class AlgParamsGCM extends BaseAlgorithmParameters {
        private GCMParameters gcmParams;

        @Override // java.security.AlgorithmParametersSpi
        protected void engineInit(AlgorithmParameterSpec paramSpec) throws InvalidParameterSpecException {
            if (GcmSpecUtil.isGcmSpec(paramSpec)) {
                this.gcmParams = GcmSpecUtil.extractGcmParameters(paramSpec);
            } else {
                if (paramSpec instanceof AEADParameterSpec) {
                    this.gcmParams = new GCMParameters(((AEADParameterSpec) paramSpec).getNonce(), ((AEADParameterSpec) paramSpec).getMacSizeInBits() / 8);
                    return;
                }
                throw new InvalidParameterSpecException("AlgorithmParameterSpec class not recognized: " + paramSpec.getClass().getName());
            }
        }

        @Override // java.security.AlgorithmParametersSpi
        protected void engineInit(byte[] params) throws IOException {
            this.gcmParams = GCMParameters.getInstance(params);
        }

        @Override // java.security.AlgorithmParametersSpi
        protected void engineInit(byte[] params, String format) throws IOException {
            if (!isASN1FormatString(format)) {
                throw new IOException("unknown format specified");
            }
            this.gcmParams = GCMParameters.getInstance(params);
        }

        @Override // java.security.AlgorithmParametersSpi
        protected byte[] engineGetEncoded() throws IOException {
            return this.gcmParams.getEncoded();
        }

        @Override // java.security.AlgorithmParametersSpi
        protected byte[] engineGetEncoded(String format) throws IOException {
            if (!isASN1FormatString(format)) {
                throw new IOException("unknown format specified");
            }
            return this.gcmParams.getEncoded();
        }

        @Override // java.security.AlgorithmParametersSpi
        protected String engineToString() {
            return "GCM";
        }

        @Override // org.spongycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters
        protected AlgorithmParameterSpec localEngineGetParameterSpec(Class paramSpec) throws InvalidParameterSpecException {
            if (paramSpec == AlgorithmParameterSpec.class || GcmSpecUtil.isGcmSpec(paramSpec)) {
                if (GcmSpecUtil.gcmSpecExists()) {
                    return GcmSpecUtil.extractGcmSpec(this.gcmParams.toASN1Primitive());
                }
                return new AEADParameterSpec(this.gcmParams.getNonce(), this.gcmParams.getIcvLen() * 8);
            }
            if (paramSpec == AEADParameterSpec.class) {
                return new AEADParameterSpec(this.gcmParams.getNonce(), this.gcmParams.getIcvLen() * 8);
            }
            if (paramSpec == IvParameterSpec.class) {
                return new IvParameterSpec(this.gcmParams.getNonce());
            }
            throw new InvalidParameterSpecException("AlgorithmParameterSpec not recognized: " + paramSpec.getName());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/symmetric/ARIA$AlgParamsCCM.class */
    public static class AlgParamsCCM extends BaseAlgorithmParameters {
        private CCMParameters ccmParams;

        @Override // java.security.AlgorithmParametersSpi
        protected void engineInit(AlgorithmParameterSpec paramSpec) throws InvalidParameterSpecException {
            if (GcmSpecUtil.isGcmSpec(paramSpec)) {
                this.ccmParams = CCMParameters.getInstance(GcmSpecUtil.extractGcmParameters(paramSpec));
            } else {
                if (paramSpec instanceof AEADParameterSpec) {
                    this.ccmParams = new CCMParameters(((AEADParameterSpec) paramSpec).getNonce(), ((AEADParameterSpec) paramSpec).getMacSizeInBits() / 8);
                    return;
                }
                throw new InvalidParameterSpecException("AlgorithmParameterSpec class not recognized: " + paramSpec.getClass().getName());
            }
        }

        @Override // java.security.AlgorithmParametersSpi
        protected void engineInit(byte[] params) throws IOException {
            this.ccmParams = CCMParameters.getInstance(params);
        }

        @Override // java.security.AlgorithmParametersSpi
        protected void engineInit(byte[] params, String format) throws IOException {
            if (!isASN1FormatString(format)) {
                throw new IOException("unknown format specified");
            }
            this.ccmParams = CCMParameters.getInstance(params);
        }

        @Override // java.security.AlgorithmParametersSpi
        protected byte[] engineGetEncoded() throws IOException {
            return this.ccmParams.getEncoded();
        }

        @Override // java.security.AlgorithmParametersSpi
        protected byte[] engineGetEncoded(String format) throws IOException {
            if (!isASN1FormatString(format)) {
                throw new IOException("unknown format specified");
            }
            return this.ccmParams.getEncoded();
        }

        @Override // java.security.AlgorithmParametersSpi
        protected String engineToString() {
            return "CCM";
        }

        @Override // org.spongycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters
        protected AlgorithmParameterSpec localEngineGetParameterSpec(Class paramSpec) throws InvalidParameterSpecException {
            if (paramSpec == AlgorithmParameterSpec.class || GcmSpecUtil.isGcmSpec(paramSpec)) {
                if (GcmSpecUtil.gcmSpecExists()) {
                    return GcmSpecUtil.extractGcmSpec(this.ccmParams.toASN1Primitive());
                }
                return new AEADParameterSpec(this.ccmParams.getNonce(), this.ccmParams.getIcvLen() * 8);
            }
            if (paramSpec == AEADParameterSpec.class) {
                return new AEADParameterSpec(this.ccmParams.getNonce(), this.ccmParams.getIcvLen() * 8);
            }
            if (paramSpec == IvParameterSpec.class) {
                return new IvParameterSpec(this.ccmParams.getNonce());
            }
            throw new InvalidParameterSpecException("AlgorithmParameterSpec not recognized: " + paramSpec.getName());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/symmetric/ARIA$Mappings.class */
    public static class Mappings extends SymmetricAlgorithmProvider {
        private static final String PREFIX = ARIA.class.getName();

        @Override // org.spongycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider provider) {
            provider.addAlgorithm("AlgorithmParameters.ARIA", PREFIX + "$AlgParams");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters", NSRIObjectIdentifiers.id_aria128_cbc, "ARIA");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters", NSRIObjectIdentifiers.id_aria192_cbc, "ARIA");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters", NSRIObjectIdentifiers.id_aria256_cbc, "ARIA");
            provider.addAlgorithm("AlgorithmParameterGenerator.ARIA", PREFIX + "$AlgParamGen");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria128_cbc, "ARIA");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria192_cbc, "ARIA");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria256_cbc, "ARIA");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria128_ofb, "ARIA");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria192_ofb, "ARIA");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria256_ofb, "ARIA");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria128_cfb, "ARIA");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria192_cfb, "ARIA");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria256_cfb, "ARIA");
            provider.addAlgorithm("Cipher.ARIA", PREFIX + "$ECB");
            provider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria128_ecb, PREFIX + "$ECB");
            provider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria192_ecb, PREFIX + "$ECB");
            provider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria256_ecb, PREFIX + "$ECB");
            provider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria128_cbc, PREFIX + "$CBC");
            provider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria192_cbc, PREFIX + "$CBC");
            provider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria256_cbc, PREFIX + "$CBC");
            provider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria128_cfb, PREFIX + "$CFB");
            provider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria192_cfb, PREFIX + "$CFB");
            provider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria256_cfb, PREFIX + "$CFB");
            provider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria128_ofb, PREFIX + "$OFB");
            provider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria192_ofb, PREFIX + "$OFB");
            provider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria256_ofb, PREFIX + "$OFB");
            provider.addAlgorithm("Cipher.ARIARFC3211WRAP", PREFIX + "$RFC3211Wrap");
            provider.addAlgorithm("Cipher.ARIAWRAP", PREFIX + "$Wrap");
            provider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria128_kw, "ARIAWRAP");
            provider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria192_kw, "ARIAWRAP");
            provider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria256_kw, "ARIAWRAP");
            provider.addAlgorithm("Alg.Alias.Cipher.ARIAKW", "ARIAWRAP");
            provider.addAlgorithm("Cipher.ARIAWRAPPAD", PREFIX + "$WrapPad");
            provider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria128_kwp, "ARIAWRAPPAD");
            provider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria192_kwp, "ARIAWRAPPAD");
            provider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria256_kwp, "ARIAWRAPPAD");
            provider.addAlgorithm("Alg.Alias.Cipher.ARIAKWP", "ARIAWRAPPAD");
            provider.addAlgorithm("KeyGenerator.ARIA", PREFIX + "$KeyGen");
            provider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria128_kw, PREFIX + "$KeyGen128");
            provider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria192_kw, PREFIX + "$KeyGen192");
            provider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria256_kw, PREFIX + "$KeyGen256");
            provider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria128_kwp, PREFIX + "$KeyGen128");
            provider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria192_kwp, PREFIX + "$KeyGen192");
            provider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria256_kwp, PREFIX + "$KeyGen256");
            provider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria128_ecb, PREFIX + "$KeyGen128");
            provider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria192_ecb, PREFIX + "$KeyGen192");
            provider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria256_ecb, PREFIX + "$KeyGen256");
            provider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria128_cbc, PREFIX + "$KeyGen128");
            provider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria192_cbc, PREFIX + "$KeyGen192");
            provider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria256_cbc, PREFIX + "$KeyGen256");
            provider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria128_cfb, PREFIX + "$KeyGen128");
            provider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria192_cfb, PREFIX + "$KeyGen192");
            provider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria256_cfb, PREFIX + "$KeyGen256");
            provider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria128_ofb, PREFIX + "$KeyGen128");
            provider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria192_ofb, PREFIX + "$KeyGen192");
            provider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria256_ofb, PREFIX + "$KeyGen256");
            provider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria128_ccm, PREFIX + "$KeyGen128");
            provider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria192_ccm, PREFIX + "$KeyGen192");
            provider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria256_ccm, PREFIX + "$KeyGen256");
            provider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria128_gcm, PREFIX + "$KeyGen128");
            provider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria192_gcm, PREFIX + "$KeyGen192");
            provider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria256_gcm, PREFIX + "$KeyGen256");
            provider.addAlgorithm("AlgorithmParameterGenerator.ARIACCM", PREFIX + "$AlgParamGenCCM");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NSRIObjectIdentifiers.id_aria128_ccm, "CCM");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NSRIObjectIdentifiers.id_aria192_ccm, "CCM");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NSRIObjectIdentifiers.id_aria256_ccm, "CCM");
            provider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria128_ccm, "CCM");
            provider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria192_ccm, "CCM");
            provider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria256_ccm, "CCM");
            provider.addAlgorithm("AlgorithmParameterGenerator.ARIAGCM", PREFIX + "$AlgParamGenGCM");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NSRIObjectIdentifiers.id_aria128_gcm, "GCM");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NSRIObjectIdentifiers.id_aria192_gcm, "GCM");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NSRIObjectIdentifiers.id_aria256_gcm, "GCM");
            provider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria128_gcm, "GCM");
            provider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria192_gcm, "GCM");
            provider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria256_gcm, "GCM");
            addGMacAlgorithm(provider, "ARIA", PREFIX + "$GMAC", PREFIX + "$KeyGen");
            addPoly1305Algorithm(provider, "ARIA", PREFIX + "$Poly1305", PREFIX + "$Poly1305KeyGen");
        }
    }
}
