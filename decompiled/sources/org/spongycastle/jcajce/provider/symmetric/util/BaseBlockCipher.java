package org.spongycastle.jcajce.provider.symmetric.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.interfaces.PBEKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.RC2ParameterSpec;
import javax.crypto.spec.RC5ParameterSpec;
import org.spongycastle.asn1.cms.GCMParameters;
import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.BufferedBlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.OutputLengthException;
import org.spongycastle.crypto.engines.DSTU7624Engine;
import org.spongycastle.crypto.modes.AEADBlockCipher;
import org.spongycastle.crypto.modes.CBCBlockCipher;
import org.spongycastle.crypto.modes.CCMBlockCipher;
import org.spongycastle.crypto.modes.CFBBlockCipher;
import org.spongycastle.crypto.modes.CTSBlockCipher;
import org.spongycastle.crypto.modes.EAXBlockCipher;
import org.spongycastle.crypto.modes.GCFBBlockCipher;
import org.spongycastle.crypto.modes.GCMBlockCipher;
import org.spongycastle.crypto.modes.GOFBBlockCipher;
import org.spongycastle.crypto.modes.KCCMBlockCipher;
import org.spongycastle.crypto.modes.KCTRBlockCipher;
import org.spongycastle.crypto.modes.KGCMBlockCipher;
import org.spongycastle.crypto.modes.OCBBlockCipher;
import org.spongycastle.crypto.modes.OFBBlockCipher;
import org.spongycastle.crypto.modes.OpenPGPCFBBlockCipher;
import org.spongycastle.crypto.modes.PGPCFBBlockCipher;
import org.spongycastle.crypto.modes.SICBlockCipher;
import org.spongycastle.crypto.paddings.BlockCipherPadding;
import org.spongycastle.crypto.paddings.ISO10126d2Padding;
import org.spongycastle.crypto.paddings.ISO7816d4Padding;
import org.spongycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.spongycastle.crypto.paddings.TBCPadding;
import org.spongycastle.crypto.paddings.X923Padding;
import org.spongycastle.crypto.paddings.ZeroBytePadding;
import org.spongycastle.crypto.params.AEADParameters;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.crypto.params.ParametersWithSBox;
import org.spongycastle.crypto.params.RC2Parameters;
import org.spongycastle.crypto.params.RC5Parameters;
import org.spongycastle.jcajce.PBKDF1Key;
import org.spongycastle.jcajce.PBKDF1KeyWithParameters;
import org.spongycastle.jcajce.PKCS12Key;
import org.spongycastle.jcajce.PKCS12KeyWithParameters;
import org.spongycastle.jcajce.spec.AEADParameterSpec;
import org.spongycastle.jcajce.spec.GOST28147ParameterSpec;
import org.spongycastle.jcajce.spec.RepeatedSecretKeySpec;
import org.spongycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/symmetric/util/BaseBlockCipher.class */
public class BaseBlockCipher extends BaseWrapCipher implements PBE {
    private static final Class gcmSpecClass = ClassUtil.loadClass(BaseBlockCipher.class, "javax.crypto.spec.GCMParameterSpec");
    private Class[] availableSpecs;
    private BlockCipher baseEngine;
    private BlockCipherProvider engineProvider;
    private GenericBlockCipher cipher;
    private ParametersWithIV ivParam;
    private AEADParameters aeadParams;
    private int keySizeInBits;
    private int scheme;
    private int digest;
    private int ivLength;
    private boolean padded;
    private boolean fixedIv;
    private PBEParameterSpec pbeSpec;
    private String pbeAlgorithm;
    private String modeName;

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/symmetric/util/BaseBlockCipher$GenericBlockCipher.class */
    private interface GenericBlockCipher {
        void init(boolean z, CipherParameters cipherParameters) throws IllegalArgumentException;

        boolean wrapOnNoPadding();

        String getAlgorithmName();

        BlockCipher getUnderlyingCipher();

        int getOutputSize(int i);

        int getUpdateOutputSize(int i);

        void updateAAD(byte[] bArr, int i, int i2);

        int processByte(byte b, byte[] bArr, int i) throws DataLengthException;

        int processBytes(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws DataLengthException;

        int doFinal(byte[] bArr, int i) throws IllegalStateException, BadPaddingException;
    }

    protected BaseBlockCipher(BlockCipher engine) {
        this.availableSpecs = new Class[]{RC2ParameterSpec.class, RC5ParameterSpec.class, gcmSpecClass, GOST28147ParameterSpec.class, IvParameterSpec.class, PBEParameterSpec.class};
        this.scheme = -1;
        this.ivLength = 0;
        this.fixedIv = true;
        this.pbeSpec = null;
        this.pbeAlgorithm = null;
        this.modeName = null;
        this.baseEngine = engine;
        this.cipher = new BufferedGenericBlockCipher(engine);
    }

    protected BaseBlockCipher(BlockCipher engine, int scheme, int digest, int keySizeInBits, int ivLength) {
        this.availableSpecs = new Class[]{RC2ParameterSpec.class, RC5ParameterSpec.class, gcmSpecClass, GOST28147ParameterSpec.class, IvParameterSpec.class, PBEParameterSpec.class};
        this.scheme = -1;
        this.ivLength = 0;
        this.fixedIv = true;
        this.pbeSpec = null;
        this.pbeAlgorithm = null;
        this.modeName = null;
        this.baseEngine = engine;
        this.scheme = scheme;
        this.digest = digest;
        this.keySizeInBits = keySizeInBits;
        this.ivLength = ivLength;
        this.cipher = new BufferedGenericBlockCipher(engine);
    }

    protected BaseBlockCipher(BlockCipherProvider provider) {
        this.availableSpecs = new Class[]{RC2ParameterSpec.class, RC5ParameterSpec.class, gcmSpecClass, GOST28147ParameterSpec.class, IvParameterSpec.class, PBEParameterSpec.class};
        this.scheme = -1;
        this.ivLength = 0;
        this.fixedIv = true;
        this.pbeSpec = null;
        this.pbeAlgorithm = null;
        this.modeName = null;
        this.baseEngine = provider.get();
        this.engineProvider = provider;
        this.cipher = new BufferedGenericBlockCipher(provider.get());
    }

    protected BaseBlockCipher(AEADBlockCipher engine) {
        this.availableSpecs = new Class[]{RC2ParameterSpec.class, RC5ParameterSpec.class, gcmSpecClass, GOST28147ParameterSpec.class, IvParameterSpec.class, PBEParameterSpec.class};
        this.scheme = -1;
        this.ivLength = 0;
        this.fixedIv = true;
        this.pbeSpec = null;
        this.pbeAlgorithm = null;
        this.modeName = null;
        this.baseEngine = engine.getUnderlyingCipher();
        this.ivLength = this.baseEngine.getBlockSize();
        this.cipher = new AEADGenericBlockCipher(engine);
    }

    protected BaseBlockCipher(AEADBlockCipher engine, boolean fixedIv, int ivLength) {
        this.availableSpecs = new Class[]{RC2ParameterSpec.class, RC5ParameterSpec.class, gcmSpecClass, GOST28147ParameterSpec.class, IvParameterSpec.class, PBEParameterSpec.class};
        this.scheme = -1;
        this.ivLength = 0;
        this.fixedIv = true;
        this.pbeSpec = null;
        this.pbeAlgorithm = null;
        this.modeName = null;
        this.baseEngine = engine.getUnderlyingCipher();
        this.fixedIv = fixedIv;
        this.ivLength = ivLength;
        this.cipher = new AEADGenericBlockCipher(engine);
    }

    protected BaseBlockCipher(BlockCipher engine, int ivLength) {
        this.availableSpecs = new Class[]{RC2ParameterSpec.class, RC5ParameterSpec.class, gcmSpecClass, GOST28147ParameterSpec.class, IvParameterSpec.class, PBEParameterSpec.class};
        this.scheme = -1;
        this.ivLength = 0;
        this.fixedIv = true;
        this.pbeSpec = null;
        this.pbeAlgorithm = null;
        this.modeName = null;
        this.baseEngine = engine;
        this.cipher = new BufferedGenericBlockCipher(engine);
        this.ivLength = ivLength / 8;
    }

    protected BaseBlockCipher(BufferedBlockCipher engine, int ivLength) {
        this.availableSpecs = new Class[]{RC2ParameterSpec.class, RC5ParameterSpec.class, gcmSpecClass, GOST28147ParameterSpec.class, IvParameterSpec.class, PBEParameterSpec.class};
        this.scheme = -1;
        this.ivLength = 0;
        this.fixedIv = true;
        this.pbeSpec = null;
        this.pbeAlgorithm = null;
        this.modeName = null;
        this.baseEngine = engine.getUnderlyingCipher();
        this.cipher = new BufferedGenericBlockCipher(engine);
        this.ivLength = ivLength / 8;
    }

    @Override // org.spongycastle.jcajce.provider.symmetric.util.BaseWrapCipher, javax.crypto.CipherSpi
    protected int engineGetBlockSize() {
        return this.baseEngine.getBlockSize();
    }

    @Override // org.spongycastle.jcajce.provider.symmetric.util.BaseWrapCipher, javax.crypto.CipherSpi
    protected byte[] engineGetIV() {
        if (this.aeadParams != null) {
            return this.aeadParams.getNonce();
        }
        if (this.ivParam != null) {
            return this.ivParam.getIV();
        }
        return null;
    }

    @Override // org.spongycastle.jcajce.provider.symmetric.util.BaseWrapCipher, javax.crypto.CipherSpi
    protected int engineGetKeySize(Key key) {
        return key.getEncoded().length * 8;
    }

    @Override // org.spongycastle.jcajce.provider.symmetric.util.BaseWrapCipher, javax.crypto.CipherSpi
    protected int engineGetOutputSize(int inputLen) {
        return this.cipher.getOutputSize(inputLen);
    }

    @Override // org.spongycastle.jcajce.provider.symmetric.util.BaseWrapCipher, javax.crypto.CipherSpi
    protected AlgorithmParameters engineGetParameters() {
        if (this.engineParams == null) {
            if (this.pbeSpec != null) {
                try {
                    this.engineParams = createParametersInstance(this.pbeAlgorithm);
                    this.engineParams.init(this.pbeSpec);
                } catch (Exception e) {
                    return null;
                }
            } else if (this.aeadParams != null) {
                try {
                    this.engineParams = createParametersInstance("GCM");
                    this.engineParams.init(new GCMParameters(this.aeadParams.getNonce(), this.aeadParams.getMacSize() / 8).getEncoded());
                } catch (Exception e2) {
                    throw new RuntimeException(e2.toString());
                }
            } else if (this.ivParam != null) {
                String name = this.cipher.getUnderlyingCipher().getAlgorithmName();
                if (name.indexOf(47) >= 0) {
                    name = name.substring(0, name.indexOf(47));
                }
                try {
                    this.engineParams = createParametersInstance(name);
                    this.engineParams.init(new IvParameterSpec(this.ivParam.getIV()));
                } catch (Exception e3) {
                    throw new RuntimeException(e3.toString());
                }
            }
        }
        return this.engineParams;
    }

    @Override // org.spongycastle.jcajce.provider.symmetric.util.BaseWrapCipher, javax.crypto.CipherSpi
    protected void engineSetMode(String mode) throws NoSuchAlgorithmException {
        this.modeName = Strings.toUpperCase(mode);
        if (this.modeName.equals("ECB")) {
            this.ivLength = 0;
            this.cipher = new BufferedGenericBlockCipher(this.baseEngine);
            return;
        }
        if (this.modeName.equals("CBC")) {
            this.ivLength = this.baseEngine.getBlockSize();
            this.cipher = new BufferedGenericBlockCipher(new CBCBlockCipher(this.baseEngine));
            return;
        }
        if (this.modeName.startsWith("OFB")) {
            this.ivLength = this.baseEngine.getBlockSize();
            if (this.modeName.length() != 3) {
                int wordSize = Integer.parseInt(this.modeName.substring(3));
                this.cipher = new BufferedGenericBlockCipher(new OFBBlockCipher(this.baseEngine, wordSize));
                return;
            } else {
                this.cipher = new BufferedGenericBlockCipher(new OFBBlockCipher(this.baseEngine, 8 * this.baseEngine.getBlockSize()));
                return;
            }
        }
        if (this.modeName.startsWith("CFB")) {
            this.ivLength = this.baseEngine.getBlockSize();
            if (this.modeName.length() != 3) {
                int wordSize2 = Integer.parseInt(this.modeName.substring(3));
                this.cipher = new BufferedGenericBlockCipher(new CFBBlockCipher(this.baseEngine, wordSize2));
                return;
            } else {
                this.cipher = new BufferedGenericBlockCipher(new CFBBlockCipher(this.baseEngine, 8 * this.baseEngine.getBlockSize()));
                return;
            }
        }
        if (this.modeName.startsWith("PGP")) {
            boolean inlineIV = this.modeName.equalsIgnoreCase("PGPCFBwithIV");
            this.ivLength = this.baseEngine.getBlockSize();
            this.cipher = new BufferedGenericBlockCipher(new PGPCFBBlockCipher(this.baseEngine, inlineIV));
            return;
        }
        if (this.modeName.equalsIgnoreCase("OpenPGPCFB")) {
            this.ivLength = 0;
            this.cipher = new BufferedGenericBlockCipher(new OpenPGPCFBBlockCipher(this.baseEngine));
            return;
        }
        if (this.modeName.startsWith("SIC")) {
            this.ivLength = this.baseEngine.getBlockSize();
            if (this.ivLength < 16) {
                throw new IllegalArgumentException("Warning: SIC-Mode can become a twotime-pad if the blocksize of the cipher is too small. Use a cipher with a block size of at least 128 bits (e.g. AES)");
            }
            this.fixedIv = false;
            this.cipher = new BufferedGenericBlockCipher(new BufferedBlockCipher(new SICBlockCipher(this.baseEngine)));
            return;
        }
        if (this.modeName.startsWith("CTR")) {
            this.ivLength = this.baseEngine.getBlockSize();
            this.fixedIv = false;
            if (this.baseEngine instanceof DSTU7624Engine) {
                this.cipher = new BufferedGenericBlockCipher(new BufferedBlockCipher(new KCTRBlockCipher(this.baseEngine)));
                return;
            } else {
                this.cipher = new BufferedGenericBlockCipher(new BufferedBlockCipher(new SICBlockCipher(this.baseEngine)));
                return;
            }
        }
        if (this.modeName.startsWith("GOFB")) {
            this.ivLength = this.baseEngine.getBlockSize();
            this.cipher = new BufferedGenericBlockCipher(new BufferedBlockCipher(new GOFBBlockCipher(this.baseEngine)));
            return;
        }
        if (this.modeName.startsWith("GCFB")) {
            this.ivLength = this.baseEngine.getBlockSize();
            this.cipher = new BufferedGenericBlockCipher(new BufferedBlockCipher(new GCFBBlockCipher(this.baseEngine)));
            return;
        }
        if (this.modeName.startsWith("CTS")) {
            this.ivLength = this.baseEngine.getBlockSize();
            this.cipher = new BufferedGenericBlockCipher(new CTSBlockCipher(new CBCBlockCipher(this.baseEngine)));
            return;
        }
        if (this.modeName.startsWith("CCM")) {
            this.ivLength = 13;
            if (this.baseEngine instanceof DSTU7624Engine) {
                this.cipher = new AEADGenericBlockCipher(new KCCMBlockCipher(this.baseEngine));
                return;
            } else {
                this.cipher = new AEADGenericBlockCipher(new CCMBlockCipher(this.baseEngine));
                return;
            }
        }
        if (this.modeName.startsWith("OCB")) {
            if (this.engineProvider != null) {
                this.ivLength = 15;
                this.cipher = new AEADGenericBlockCipher(new OCBBlockCipher(this.baseEngine, this.engineProvider.get()));
                return;
            }
            throw new NoSuchAlgorithmException("can't support mode " + mode);
        }
        if (this.modeName.startsWith("EAX")) {
            this.ivLength = this.baseEngine.getBlockSize();
            this.cipher = new AEADGenericBlockCipher(new EAXBlockCipher(this.baseEngine));
        } else {
            if (this.modeName.startsWith("GCM")) {
                this.ivLength = this.baseEngine.getBlockSize();
                if (this.baseEngine instanceof DSTU7624Engine) {
                    this.cipher = new AEADGenericBlockCipher(new KGCMBlockCipher(this.baseEngine));
                    return;
                } else {
                    this.cipher = new AEADGenericBlockCipher(new GCMBlockCipher(this.baseEngine));
                    return;
                }
            }
            throw new NoSuchAlgorithmException("can't support mode " + mode);
        }
    }

    @Override // org.spongycastle.jcajce.provider.symmetric.util.BaseWrapCipher, javax.crypto.CipherSpi
    protected void engineSetPadding(String padding) throws NoSuchPaddingException {
        String paddingName = Strings.toUpperCase(padding);
        if (paddingName.equals("NOPADDING")) {
            if (this.cipher.wrapOnNoPadding()) {
                this.cipher = new BufferedGenericBlockCipher(new BufferedBlockCipher(this.cipher.getUnderlyingCipher()));
                return;
            }
            return;
        }
        if (paddingName.equals("WITHCTS")) {
            this.cipher = new BufferedGenericBlockCipher(new CTSBlockCipher(this.cipher.getUnderlyingCipher()));
            return;
        }
        this.padded = true;
        if (isAEADModeName(this.modeName)) {
            throw new NoSuchPaddingException("Only NoPadding can be used with AEAD modes.");
        }
        if (paddingName.equals("PKCS5PADDING") || paddingName.equals("PKCS7PADDING")) {
            this.cipher = new BufferedGenericBlockCipher(this.cipher.getUnderlyingCipher());
            return;
        }
        if (paddingName.equals("ZEROBYTEPADDING")) {
            this.cipher = new BufferedGenericBlockCipher(this.cipher.getUnderlyingCipher(), new ZeroBytePadding());
            return;
        }
        if (paddingName.equals("ISO10126PADDING") || paddingName.equals("ISO10126-2PADDING")) {
            this.cipher = new BufferedGenericBlockCipher(this.cipher.getUnderlyingCipher(), new ISO10126d2Padding());
            return;
        }
        if (paddingName.equals("X9.23PADDING") || paddingName.equals("X923PADDING")) {
            this.cipher = new BufferedGenericBlockCipher(this.cipher.getUnderlyingCipher(), new X923Padding());
            return;
        }
        if (paddingName.equals("ISO7816-4PADDING") || paddingName.equals("ISO9797-1PADDING")) {
            this.cipher = new BufferedGenericBlockCipher(this.cipher.getUnderlyingCipher(), new ISO7816d4Padding());
        } else {
            if (paddingName.equals("TBCPADDING")) {
                this.cipher = new BufferedGenericBlockCipher(this.cipher.getUnderlyingCipher(), new TBCPadding());
                return;
            }
            throw new NoSuchPaddingException("Padding " + padding + " unknown.");
        }
    }

    @Override // org.spongycastle.jcajce.provider.symmetric.util.BaseWrapCipher, javax.crypto.CipherSpi
    protected void engineInit(int opmode, Key key, AlgorithmParameterSpec params, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        CipherParameters param;
        KeyParameter keyParam;
        KeyParameter keyParam2;
        this.pbeSpec = null;
        this.pbeAlgorithm = null;
        this.engineParams = null;
        this.aeadParams = null;
        if (!(key instanceof SecretKey)) {
            throw new InvalidKeyException("Key for algorithm " + (key != null ? key.getAlgorithm() : null) + " not suitable for symmetric enryption.");
        }
        if (params == null && this.baseEngine.getAlgorithmName().startsWith("RC5-64")) {
            throw new InvalidAlgorithmParameterException("RC5 requires an RC5ParametersSpec to be passed in.");
        }
        if (this.scheme == 2 || (key instanceof PKCS12Key)) {
            try {
                SecretKey k = (SecretKey) key;
                if (params instanceof PBEParameterSpec) {
                    this.pbeSpec = (PBEParameterSpec) params;
                }
                if ((k instanceof PBEKey) && this.pbeSpec == null) {
                    PBEKey pbeKey = (PBEKey) k;
                    if (pbeKey.getSalt() == null) {
                        throw new InvalidAlgorithmParameterException("PBEKey requires parameters to specify salt");
                    }
                    this.pbeSpec = new PBEParameterSpec(pbeKey.getSalt(), pbeKey.getIterationCount());
                }
                if (this.pbeSpec == null && !(k instanceof PBEKey)) {
                    throw new InvalidKeyException("Algorithm requires a PBE key");
                }
                if (key instanceof BCPBEKey) {
                    CipherParameters pbeKeyParam = ((BCPBEKey) key).getParam();
                    if (pbeKeyParam instanceof ParametersWithIV) {
                        param = pbeKeyParam;
                    } else if (pbeKeyParam == null) {
                        param = PBE.Util.makePBEParameters(k.getEncoded(), 2, this.digest, this.keySizeInBits, this.ivLength * 8, this.pbeSpec, this.cipher.getAlgorithmName());
                    } else {
                        throw new InvalidKeyException("Algorithm requires a PBE key suitable for PKCS12");
                    }
                } else {
                    param = PBE.Util.makePBEParameters(k.getEncoded(), 2, this.digest, this.keySizeInBits, this.ivLength * 8, this.pbeSpec, this.cipher.getAlgorithmName());
                }
                if (param instanceof ParametersWithIV) {
                    this.ivParam = (ParametersWithIV) param;
                }
            } catch (Exception e) {
                throw new InvalidKeyException("PKCS12 requires a SecretKey/PBEKey");
            }
        } else if (key instanceof PBKDF1Key) {
            PBKDF1Key k2 = (PBKDF1Key) key;
            if (params instanceof PBEParameterSpec) {
                this.pbeSpec = (PBEParameterSpec) params;
            }
            if ((k2 instanceof PBKDF1KeyWithParameters) && this.pbeSpec == null) {
                this.pbeSpec = new PBEParameterSpec(((PBKDF1KeyWithParameters) k2).getSalt(), ((PBKDF1KeyWithParameters) k2).getIterationCount());
            }
            param = PBE.Util.makePBEParameters(k2.getEncoded(), 0, this.digest, this.keySizeInBits, this.ivLength * 8, this.pbeSpec, this.cipher.getAlgorithmName());
            if (param instanceof ParametersWithIV) {
                this.ivParam = (ParametersWithIV) param;
            }
        } else if (key instanceof BCPBEKey) {
            BCPBEKey k3 = (BCPBEKey) key;
            if (k3.getOID() != null) {
                this.pbeAlgorithm = k3.getOID().getId();
            } else {
                this.pbeAlgorithm = k3.getAlgorithm();
            }
            if (k3.getParam() != null) {
                param = adjustParameters(params, k3.getParam());
            } else if (params instanceof PBEParameterSpec) {
                this.pbeSpec = (PBEParameterSpec) params;
                param = PBE.Util.makePBEParameters(k3, params, this.cipher.getUnderlyingCipher().getAlgorithmName());
            } else {
                throw new InvalidAlgorithmParameterException("PBE requires PBE parameters to be set.");
            }
            if (param instanceof ParametersWithIV) {
                this.ivParam = (ParametersWithIV) param;
            }
        } else if (key instanceof PBEKey) {
            PBEKey k4 = (PBEKey) key;
            this.pbeSpec = (PBEParameterSpec) params;
            if ((k4 instanceof PKCS12KeyWithParameters) && this.pbeSpec == null) {
                this.pbeSpec = new PBEParameterSpec(k4.getSalt(), k4.getIterationCount());
            }
            param = PBE.Util.makePBEParameters(k4.getEncoded(), this.scheme, this.digest, this.keySizeInBits, this.ivLength * 8, this.pbeSpec, this.cipher.getAlgorithmName());
            if (param instanceof ParametersWithIV) {
                this.ivParam = (ParametersWithIV) param;
            }
        } else if (!(key instanceof RepeatedSecretKeySpec)) {
            if (this.scheme == 0 || this.scheme == 4 || this.scheme == 1 || this.scheme == 5) {
                throw new InvalidKeyException("Algorithm requires a PBE key");
            }
            param = new KeyParameter(key.getEncoded());
        } else {
            param = null;
        }
        if (params instanceof AEADParameterSpec) {
            if (!isAEADModeName(this.modeName) && !(this.cipher instanceof AEADGenericBlockCipher)) {
                throw new InvalidAlgorithmParameterException("AEADParameterSpec can only be used with AEAD modes.");
            }
            AEADParameterSpec aeadSpec = (AEADParameterSpec) params;
            if (param instanceof ParametersWithIV) {
                keyParam2 = (KeyParameter) ((ParametersWithIV) param).getParameters();
            } else {
                keyParam2 = (KeyParameter) param;
            }
            AEADParameters aEADParameters = new AEADParameters(keyParam2, aeadSpec.getMacSizeInBits(), aeadSpec.getNonce(), aeadSpec.getAssociatedData());
            this.aeadParams = aEADParameters;
            param = aEADParameters;
        } else if (params instanceof IvParameterSpec) {
            if (this.ivLength != 0) {
                IvParameterSpec p = (IvParameterSpec) params;
                if (p.getIV().length != this.ivLength && !(this.cipher instanceof AEADGenericBlockCipher) && this.fixedIv) {
                    throw new InvalidAlgorithmParameterException("IV must be " + this.ivLength + " bytes long.");
                }
                if (param instanceof ParametersWithIV) {
                    param = new ParametersWithIV(((ParametersWithIV) param).getParameters(), p.getIV());
                } else {
                    param = new ParametersWithIV(param, p.getIV());
                }
                this.ivParam = (ParametersWithIV) param;
            } else if (this.modeName != null && this.modeName.equals("ECB")) {
                throw new InvalidAlgorithmParameterException("ECB mode does not use an IV");
            }
        } else if (params instanceof GOST28147ParameterSpec) {
            GOST28147ParameterSpec gost28147Param = (GOST28147ParameterSpec) params;
            param = new ParametersWithSBox(new KeyParameter(key.getEncoded()), ((GOST28147ParameterSpec) params).getSbox());
            if (gost28147Param.getIV() != null && this.ivLength != 0) {
                if (param instanceof ParametersWithIV) {
                    param = new ParametersWithIV(((ParametersWithIV) param).getParameters(), gost28147Param.getIV());
                } else {
                    param = new ParametersWithIV(param, gost28147Param.getIV());
                }
                this.ivParam = (ParametersWithIV) param;
            }
        } else if (params instanceof RC2ParameterSpec) {
            RC2ParameterSpec rc2Param = (RC2ParameterSpec) params;
            param = new RC2Parameters(key.getEncoded(), ((RC2ParameterSpec) params).getEffectiveKeyBits());
            if (rc2Param.getIV() != null && this.ivLength != 0) {
                if (param instanceof ParametersWithIV) {
                    param = new ParametersWithIV(((ParametersWithIV) param).getParameters(), rc2Param.getIV());
                } else {
                    param = new ParametersWithIV(param, rc2Param.getIV());
                }
                this.ivParam = (ParametersWithIV) param;
            }
        } else if (params instanceof RC5ParameterSpec) {
            RC5ParameterSpec rc5Param = (RC5ParameterSpec) params;
            param = new RC5Parameters(key.getEncoded(), ((RC5ParameterSpec) params).getRounds());
            if (this.baseEngine.getAlgorithmName().startsWith("RC5")) {
                if (this.baseEngine.getAlgorithmName().equals("RC5-32")) {
                    if (rc5Param.getWordSize() != 32) {
                        throw new InvalidAlgorithmParameterException("RC5 already set up for a word size of 32 not " + rc5Param.getWordSize() + ".");
                    }
                } else if (this.baseEngine.getAlgorithmName().equals("RC5-64") && rc5Param.getWordSize() != 64) {
                    throw new InvalidAlgorithmParameterException("RC5 already set up for a word size of 64 not " + rc5Param.getWordSize() + ".");
                }
                if (rc5Param.getIV() != null && this.ivLength != 0) {
                    if (param instanceof ParametersWithIV) {
                        param = new ParametersWithIV(((ParametersWithIV) param).getParameters(), rc5Param.getIV());
                    } else {
                        param = new ParametersWithIV(param, rc5Param.getIV());
                    }
                    this.ivParam = (ParametersWithIV) param;
                }
            } else {
                throw new InvalidAlgorithmParameterException("RC5 parameters passed to a cipher that is not RC5.");
            }
        } else if (gcmSpecClass != null && gcmSpecClass.isInstance(params)) {
            if (!isAEADModeName(this.modeName) && !(this.cipher instanceof AEADGenericBlockCipher)) {
                throw new InvalidAlgorithmParameterException("GCMParameterSpec can only be used with AEAD modes.");
            }
            try {
                Method tLen = gcmSpecClass.getDeclaredMethod("getTLen", new Class[0]);
                Method iv = gcmSpecClass.getDeclaredMethod("getIV", new Class[0]);
                if (param instanceof ParametersWithIV) {
                    keyParam = (KeyParameter) ((ParametersWithIV) param).getParameters();
                } else {
                    keyParam = (KeyParameter) param;
                }
                AEADParameters aEADParameters2 = new AEADParameters(keyParam, ((Integer) tLen.invoke(params, new Object[0])).intValue(), (byte[]) iv.invoke(params, new Object[0]));
                this.aeadParams = aEADParameters2;
                param = aEADParameters2;
            } catch (Exception e2) {
                throw new InvalidAlgorithmParameterException("Cannot process GCMParameterSpec.");
            }
        } else if (params != null && !(params instanceof PBEParameterSpec)) {
            throw new InvalidAlgorithmParameterException("unknown parameter type.");
        }
        if (this.ivLength != 0 && !(param instanceof ParametersWithIV) && !(param instanceof AEADParameters)) {
            SecureRandom ivRandom = random;
            if (ivRandom == null) {
                ivRandom = new SecureRandom();
            }
            if (opmode == 1 || opmode == 3) {
                byte[] iv2 = new byte[this.ivLength];
                ivRandom.nextBytes(iv2);
                param = new ParametersWithIV(param, iv2);
                this.ivParam = (ParametersWithIV) param;
            } else if (this.cipher.getUnderlyingCipher().getAlgorithmName().indexOf("PGPCFB") < 0) {
                throw new InvalidAlgorithmParameterException("no IV set when one expected");
            }
        }
        if (random != null && this.padded) {
            param = new ParametersWithRandom(param, random);
        }
        try {
            switch (opmode) {
                case 1:
                case 3:
                    this.cipher.init(true, param);
                    break;
                case 2:
                case 4:
                    this.cipher.init(false, param);
                    break;
                default:
                    throw new InvalidParameterException("unknown opmode " + opmode + " passed");
            }
            if ((this.cipher instanceof AEADGenericBlockCipher) && this.aeadParams == null) {
                AEADBlockCipher aeadCipher = ((AEADGenericBlockCipher) this.cipher).cipher;
                this.aeadParams = new AEADParameters((KeyParameter) this.ivParam.getParameters(), aeadCipher.getMac().length * 8, this.ivParam.getIV());
            }
        } catch (Exception e3) {
            throw new InvalidKeyOrParametersException(e3.getMessage(), e3);
        }
    }

    private CipherParameters adjustParameters(AlgorithmParameterSpec params, CipherParameters param) {
        if (param instanceof ParametersWithIV) {
            CipherParameters key = ((ParametersWithIV) param).getParameters();
            if (params instanceof IvParameterSpec) {
                IvParameterSpec iv = (IvParameterSpec) params;
                this.ivParam = new ParametersWithIV(key, iv.getIV());
                param = this.ivParam;
            } else if (params instanceof GOST28147ParameterSpec) {
                GOST28147ParameterSpec gost28147Param = (GOST28147ParameterSpec) params;
                param = new ParametersWithSBox(param, gost28147Param.getSbox());
                if (gost28147Param.getIV() != null && this.ivLength != 0) {
                    this.ivParam = new ParametersWithIV(key, gost28147Param.getIV());
                    param = this.ivParam;
                }
            }
        } else if (params instanceof IvParameterSpec) {
            IvParameterSpec iv2 = (IvParameterSpec) params;
            this.ivParam = new ParametersWithIV(param, iv2.getIV());
            param = this.ivParam;
        } else if (params instanceof GOST28147ParameterSpec) {
            GOST28147ParameterSpec gost28147Param2 = (GOST28147ParameterSpec) params;
            param = new ParametersWithSBox(param, gost28147Param2.getSbox());
            if (gost28147Param2.getIV() != null && this.ivLength != 0) {
                param = new ParametersWithIV(param, gost28147Param2.getIV());
            }
        }
        return param;
    }

    @Override // org.spongycastle.jcajce.provider.symmetric.util.BaseWrapCipher, javax.crypto.CipherSpi
    protected void engineInit(int opmode, Key key, AlgorithmParameters params, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        AlgorithmParameterSpec paramSpec = null;
        if (params != null) {
            for (int i = 0; i != this.availableSpecs.length; i++) {
                if (this.availableSpecs[i] != null) {
                    try {
                        paramSpec = params.getParameterSpec(this.availableSpecs[i]);
                        break;
                    } catch (Exception e) {
                    }
                }
            }
            if (paramSpec == null) {
                throw new InvalidAlgorithmParameterException("can't handle parameter " + params.toString());
            }
        }
        engineInit(opmode, key, paramSpec, random);
        this.engineParams = params;
    }

    @Override // org.spongycastle.jcajce.provider.symmetric.util.BaseWrapCipher, javax.crypto.CipherSpi
    protected void engineInit(int opmode, Key key, SecureRandom random) throws InvalidKeyException {
        try {
            engineInit(opmode, key, (AlgorithmParameterSpec) null, random);
        } catch (InvalidAlgorithmParameterException e) {
            throw new InvalidKeyException(e.getMessage());
        }
    }

    @Override // javax.crypto.CipherSpi
    protected void engineUpdateAAD(byte[] input, int offset, int length) {
        this.cipher.updateAAD(input, offset, length);
    }

    @Override // javax.crypto.CipherSpi
    protected void engineUpdateAAD(ByteBuffer bytebuffer) {
        int offset = bytebuffer.arrayOffset() + bytebuffer.position();
        int length = bytebuffer.limit() - bytebuffer.position();
        engineUpdateAAD(bytebuffer.array(), offset, length);
    }

    @Override // org.spongycastle.jcajce.provider.symmetric.util.BaseWrapCipher, javax.crypto.CipherSpi
    protected byte[] engineUpdate(byte[] input, int inputOffset, int inputLen) {
        int length = this.cipher.getUpdateOutputSize(inputLen);
        if (length > 0) {
            byte[] out = new byte[length];
            int len = this.cipher.processBytes(input, inputOffset, inputLen, out, 0);
            if (len == 0) {
                return null;
            }
            if (len != out.length) {
                byte[] tmp = new byte[len];
                System.arraycopy(out, 0, tmp, 0, len);
                return tmp;
            }
            return out;
        }
        this.cipher.processBytes(input, inputOffset, inputLen, null, 0);
        return null;
    }

    @Override // org.spongycastle.jcajce.provider.symmetric.util.BaseWrapCipher, javax.crypto.CipherSpi
    protected int engineUpdate(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws ShortBufferException {
        if (outputOffset + this.cipher.getUpdateOutputSize(inputLen) > output.length) {
            throw new ShortBufferException("output buffer too short for input.");
        }
        try {
            return this.cipher.processBytes(input, inputOffset, inputLen, output, outputOffset);
        } catch (DataLengthException e) {
            throw new IllegalStateException(e.toString());
        }
    }

    @Override // org.spongycastle.jcajce.provider.symmetric.util.BaseWrapCipher, javax.crypto.CipherSpi
    protected byte[] engineDoFinal(byte[] input, int inputOffset, int inputLen) throws BadPaddingException, IllegalBlockSizeException {
        int len = 0;
        byte[] tmp = new byte[engineGetOutputSize(inputLen)];
        if (inputLen != 0) {
            len = this.cipher.processBytes(input, inputOffset, inputLen, tmp, 0);
        }
        try {
            int len2 = len + this.cipher.doFinal(tmp, len);
            if (len2 == tmp.length) {
                return tmp;
            }
            byte[] out = new byte[len2];
            System.arraycopy(tmp, 0, out, 0, len2);
            return out;
        } catch (DataLengthException e) {
            throw new IllegalBlockSizeException(e.getMessage());
        }
    }

    @Override // org.spongycastle.jcajce.provider.symmetric.util.BaseWrapCipher, javax.crypto.CipherSpi
    protected int engineDoFinal(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws BadPaddingException, IllegalBlockSizeException, ShortBufferException {
        int len = 0;
        if (outputOffset + engineGetOutputSize(inputLen) > output.length) {
            throw new ShortBufferException("output buffer too short for input.");
        }
        if (inputLen != 0) {
            try {
                len = this.cipher.processBytes(input, inputOffset, inputLen, output, outputOffset);
            } catch (OutputLengthException e) {
                throw new IllegalBlockSizeException(e.getMessage());
            } catch (DataLengthException e2) {
                throw new IllegalBlockSizeException(e2.getMessage());
            }
        }
        return len + this.cipher.doFinal(output, outputOffset + len);
    }

    private boolean isAEADModeName(String modeName) {
        return "CCM".equals(modeName) || "EAX".equals(modeName) || "GCM".equals(modeName) || "OCB".equals(modeName);
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/symmetric/util/BaseBlockCipher$BufferedGenericBlockCipher.class */
    private static class BufferedGenericBlockCipher implements GenericBlockCipher {
        private BufferedBlockCipher cipher;

        BufferedGenericBlockCipher(BufferedBlockCipher cipher) {
            this.cipher = cipher;
        }

        BufferedGenericBlockCipher(BlockCipher cipher) {
            this.cipher = new PaddedBufferedBlockCipher(cipher);
        }

        BufferedGenericBlockCipher(BlockCipher cipher, BlockCipherPadding padding) {
            this.cipher = new PaddedBufferedBlockCipher(cipher, padding);
        }

        @Override // org.spongycastle.jcajce.provider.symmetric.util.BaseBlockCipher.GenericBlockCipher
        public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
            this.cipher.init(forEncryption, params);
        }

        @Override // org.spongycastle.jcajce.provider.symmetric.util.BaseBlockCipher.GenericBlockCipher
        public boolean wrapOnNoPadding() {
            return !(this.cipher instanceof CTSBlockCipher);
        }

        @Override // org.spongycastle.jcajce.provider.symmetric.util.BaseBlockCipher.GenericBlockCipher
        public String getAlgorithmName() {
            return this.cipher.getUnderlyingCipher().getAlgorithmName();
        }

        @Override // org.spongycastle.jcajce.provider.symmetric.util.BaseBlockCipher.GenericBlockCipher
        public BlockCipher getUnderlyingCipher() {
            return this.cipher.getUnderlyingCipher();
        }

        @Override // org.spongycastle.jcajce.provider.symmetric.util.BaseBlockCipher.GenericBlockCipher
        public int getOutputSize(int len) {
            return this.cipher.getOutputSize(len);
        }

        @Override // org.spongycastle.jcajce.provider.symmetric.util.BaseBlockCipher.GenericBlockCipher
        public int getUpdateOutputSize(int len) {
            return this.cipher.getUpdateOutputSize(len);
        }

        @Override // org.spongycastle.jcajce.provider.symmetric.util.BaseBlockCipher.GenericBlockCipher
        public void updateAAD(byte[] input, int offset, int length) {
            throw new UnsupportedOperationException("AAD is not supported in the current mode.");
        }

        @Override // org.spongycastle.jcajce.provider.symmetric.util.BaseBlockCipher.GenericBlockCipher
        public int processByte(byte in, byte[] out, int outOff) throws DataLengthException {
            return this.cipher.processByte(in, out, outOff);
        }

        @Override // org.spongycastle.jcajce.provider.symmetric.util.BaseBlockCipher.GenericBlockCipher
        public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) throws DataLengthException {
            return this.cipher.processBytes(in, inOff, len, out, outOff);
        }

        @Override // org.spongycastle.jcajce.provider.symmetric.util.BaseBlockCipher.GenericBlockCipher
        public int doFinal(byte[] out, int outOff) throws IllegalStateException, BadPaddingException {
            try {
                return this.cipher.doFinal(out, outOff);
            } catch (InvalidCipherTextException e) {
                throw new BadPaddingException(e.getMessage());
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/symmetric/util/BaseBlockCipher$AEADGenericBlockCipher.class */
    private static class AEADGenericBlockCipher implements GenericBlockCipher {
        private static final Constructor aeadBadTagConstructor;
        private AEADBlockCipher cipher;

        static {
            Class aeadBadTagClass = ClassUtil.loadClass(BaseBlockCipher.class, "javax.crypto.AEADBadTagException");
            if (aeadBadTagClass != null) {
                aeadBadTagConstructor = findExceptionConstructor(aeadBadTagClass);
            } else {
                aeadBadTagConstructor = null;
            }
        }

        private static Constructor findExceptionConstructor(Class clazz) {
            try {
                return clazz.getConstructor(String.class);
            } catch (Exception e) {
                return null;
            }
        }

        AEADGenericBlockCipher(AEADBlockCipher cipher) {
            this.cipher = cipher;
        }

        @Override // org.spongycastle.jcajce.provider.symmetric.util.BaseBlockCipher.GenericBlockCipher
        public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
            this.cipher.init(forEncryption, params);
        }

        @Override // org.spongycastle.jcajce.provider.symmetric.util.BaseBlockCipher.GenericBlockCipher
        public String getAlgorithmName() {
            return this.cipher.getUnderlyingCipher().getAlgorithmName();
        }

        @Override // org.spongycastle.jcajce.provider.symmetric.util.BaseBlockCipher.GenericBlockCipher
        public boolean wrapOnNoPadding() {
            return false;
        }

        @Override // org.spongycastle.jcajce.provider.symmetric.util.BaseBlockCipher.GenericBlockCipher
        public BlockCipher getUnderlyingCipher() {
            return this.cipher.getUnderlyingCipher();
        }

        @Override // org.spongycastle.jcajce.provider.symmetric.util.BaseBlockCipher.GenericBlockCipher
        public int getOutputSize(int len) {
            return this.cipher.getOutputSize(len);
        }

        @Override // org.spongycastle.jcajce.provider.symmetric.util.BaseBlockCipher.GenericBlockCipher
        public int getUpdateOutputSize(int len) {
            return this.cipher.getUpdateOutputSize(len);
        }

        @Override // org.spongycastle.jcajce.provider.symmetric.util.BaseBlockCipher.GenericBlockCipher
        public void updateAAD(byte[] input, int offset, int length) {
            this.cipher.processAADBytes(input, offset, length);
        }

        @Override // org.spongycastle.jcajce.provider.symmetric.util.BaseBlockCipher.GenericBlockCipher
        public int processByte(byte in, byte[] out, int outOff) throws DataLengthException {
            return this.cipher.processByte(in, out, outOff);
        }

        @Override // org.spongycastle.jcajce.provider.symmetric.util.BaseBlockCipher.GenericBlockCipher
        public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) throws DataLengthException {
            return this.cipher.processBytes(in, inOff, len, out, outOff);
        }

        @Override // org.spongycastle.jcajce.provider.symmetric.util.BaseBlockCipher.GenericBlockCipher
        public int doFinal(byte[] out, int outOff) throws IllegalStateException, BadPaddingException {
            try {
                return this.cipher.doFinal(out, outOff);
            } catch (InvalidCipherTextException e) {
                if (aeadBadTagConstructor != null) {
                    BadPaddingException aeadBadTag = null;
                    try {
                        aeadBadTag = (BadPaddingException) aeadBadTagConstructor.newInstance(e.getMessage());
                    } catch (Exception e2) {
                    }
                    if (aeadBadTag != null) {
                        throw aeadBadTag;
                    }
                }
                throw new BadPaddingException(e.getMessage());
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/symmetric/util/BaseBlockCipher$InvalidKeyOrParametersException.class */
    private static class InvalidKeyOrParametersException extends InvalidKeyException {
        private final Throwable cause;

        InvalidKeyOrParametersException(String msg, Throwable cause) {
            super(msg);
            this.cause = cause;
        }

        @Override // java.lang.Throwable
        public Throwable getCause() {
            return this.cause;
        }
    }
}
