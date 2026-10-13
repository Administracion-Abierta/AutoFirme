package org.spongycastle.jcajce.provider.symmetric.util;

import java.lang.reflect.Method;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Hashtable;
import java.util.Map;
import javax.crypto.MacSpi;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.PBEKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.RC2ParameterSpec;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.Mac;
import org.spongycastle.crypto.macs.HMac;
import org.spongycastle.crypto.params.AEADParameters;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.spongycastle.crypto.params.RC2Parameters;
import org.spongycastle.crypto.params.SkeinParameters;
import org.spongycastle.jcajce.PKCS12Key;
import org.spongycastle.jcajce.spec.AEADParameterSpec;
import org.spongycastle.jcajce.spec.SkeinParameterSpec;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/symmetric/util/BaseMac.class */
public class BaseMac extends MacSpi implements PBE {
    private static final Class gcmSpecClass = ClassUtil.loadClass(BaseMac.class, "javax.crypto.spec.GCMParameterSpec");
    private Mac macEngine;
    private int scheme;
    private int pbeHash;
    private int keySize;

    protected BaseMac(Mac macEngine) {
        this.scheme = 2;
        this.pbeHash = 1;
        this.keySize = 160;
        this.macEngine = macEngine;
    }

    protected BaseMac(Mac macEngine, int scheme, int pbeHash, int keySize) {
        this.scheme = 2;
        this.pbeHash = 1;
        this.keySize = 160;
        this.macEngine = macEngine;
        this.scheme = scheme;
        this.pbeHash = pbeHash;
        this.keySize = keySize;
    }

    @Override // javax.crypto.MacSpi
    protected void engineInit(Key key, AlgorithmParameterSpec params) throws InvalidKeyException, InvalidAlgorithmParameterException {
        CipherParameters param;
        KeyParameter keyParam;
        if (key == null) {
            throw new InvalidKeyException("key is null");
        }
        if (key instanceof PKCS12Key) {
            try {
                SecretKey k = (SecretKey) key;
                try {
                    PBEParameterSpec pbeSpec = (PBEParameterSpec) params;
                    if ((k instanceof PBEKey) && pbeSpec == null) {
                        pbeSpec = new PBEParameterSpec(((PBEKey) k).getSalt(), ((PBEKey) k).getIterationCount());
                    }
                    int digest = 1;
                    int keySize = 160;
                    if (this.macEngine.getAlgorithmName().startsWith("GOST")) {
                        digest = 6;
                        keySize = 256;
                    } else if ((this.macEngine instanceof HMac) && !this.macEngine.getAlgorithmName().startsWith("SHA-1")) {
                        if (this.macEngine.getAlgorithmName().startsWith("SHA-224")) {
                            digest = 7;
                            keySize = 224;
                        } else if (this.macEngine.getAlgorithmName().startsWith("SHA-256")) {
                            digest = 4;
                            keySize = 256;
                        } else if (this.macEngine.getAlgorithmName().startsWith("SHA-384")) {
                            digest = 8;
                            keySize = 384;
                        } else if (this.macEngine.getAlgorithmName().startsWith("SHA-512")) {
                            digest = 9;
                            keySize = 512;
                        } else if (this.macEngine.getAlgorithmName().startsWith("RIPEMD160")) {
                            digest = 2;
                            keySize = 160;
                        } else {
                            throw new InvalidAlgorithmParameterException("no PKCS12 mapping for HMAC: " + this.macEngine.getAlgorithmName());
                        }
                    }
                    param = PBE.Util.makePBEMacParameters(k, 2, digest, keySize, pbeSpec);
                } catch (Exception e) {
                    throw new InvalidAlgorithmParameterException("PKCS12 requires a PBEParameterSpec");
                }
            } catch (Exception e2) {
                throw new InvalidKeyException("PKCS12 requires a SecretKey/PBEKey");
            }
        } else if (key instanceof BCPBEKey) {
            BCPBEKey k2 = (BCPBEKey) key;
            if (k2.getParam() != null) {
                param = k2.getParam();
            } else if (params instanceof PBEParameterSpec) {
                param = PBE.Util.makePBEMacParameters(k2, params);
            } else {
                throw new InvalidAlgorithmParameterException("PBE requires PBE parameters to be set.");
            }
        } else {
            if (params instanceof PBEParameterSpec) {
                throw new InvalidAlgorithmParameterException("inappropriate parameter type: " + params.getClass().getName());
            }
            param = new KeyParameter(key.getEncoded());
        }
        if (param instanceof ParametersWithIV) {
            keyParam = (KeyParameter) ((ParametersWithIV) param).getParameters();
        } else {
            keyParam = (KeyParameter) param;
        }
        if (params instanceof AEADParameterSpec) {
            AEADParameterSpec aeadSpec = (AEADParameterSpec) params;
            param = new AEADParameters(keyParam, aeadSpec.getMacSizeInBits(), aeadSpec.getNonce(), aeadSpec.getAssociatedData());
        } else if (params instanceof IvParameterSpec) {
            param = new ParametersWithIV(keyParam, ((IvParameterSpec) params).getIV());
        } else if (params instanceof RC2ParameterSpec) {
            param = new ParametersWithIV(new RC2Parameters(keyParam.getKey(), ((RC2ParameterSpec) params).getEffectiveKeyBits()), ((RC2ParameterSpec) params).getIV());
        } else if (params instanceof SkeinParameterSpec) {
            param = new SkeinParameters.Builder(copyMap(((SkeinParameterSpec) params).getParameters())).setKey(keyParam.getKey()).build();
        } else if (params == null) {
            param = new KeyParameter(key.getEncoded());
        } else if (gcmSpecClass != null && gcmSpecClass.isAssignableFrom(params.getClass())) {
            try {
                Method tLen = gcmSpecClass.getDeclaredMethod("getTLen", new Class[0]);
                Method iv = gcmSpecClass.getDeclaredMethod("getIV", new Class[0]);
                param = new AEADParameters(keyParam, ((Integer) tLen.invoke(params, new Object[0])).intValue(), (byte[]) iv.invoke(params, new Object[0]));
            } catch (Exception e3) {
                throw new InvalidAlgorithmParameterException("Cannot process GCMParameterSpec.");
            }
        } else if (!(params instanceof PBEParameterSpec)) {
            throw new InvalidAlgorithmParameterException("unknown parameter type: " + params.getClass().getName());
        }
        try {
            this.macEngine.init(param);
        } catch (Exception e4) {
            throw new InvalidAlgorithmParameterException("cannot initialize MAC: " + e4.getMessage());
        }
    }

    @Override // javax.crypto.MacSpi
    protected int engineGetMacLength() {
        return this.macEngine.getMacSize();
    }

    @Override // javax.crypto.MacSpi
    protected void engineReset() {
        this.macEngine.reset();
    }

    @Override // javax.crypto.MacSpi
    protected void engineUpdate(byte input) {
        this.macEngine.update(input);
    }

    @Override // javax.crypto.MacSpi
    protected void engineUpdate(byte[] input, int offset, int len) {
        this.macEngine.update(input, offset, len);
    }

    @Override // javax.crypto.MacSpi
    protected byte[] engineDoFinal() {
        byte[] out = new byte[engineGetMacLength()];
        this.macEngine.doFinal(out, 0);
        return out;
    }

    private static Hashtable copyMap(Map paramsMap) {
        Hashtable newTable = new Hashtable();
        for (Object key : paramsMap.keySet()) {
            newTable.put(key, paramsMap.get(key));
        }
        return newTable;
    }
}
