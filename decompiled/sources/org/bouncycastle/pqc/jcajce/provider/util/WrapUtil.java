package org.bouncycastle.pqc.jcajce.provider.util;

import java.security.InvalidKeyException;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.ARIAEngine;
import org.bouncycastle.crypto.engines.CamelliaEngine;
import org.bouncycastle.crypto.engines.RFC3394WrapEngine;
import org.bouncycastle.crypto.engines.RFC5649WrapEngine;
import org.bouncycastle.crypto.engines.SEEDEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jcajce.spec.KTSParameterSpec;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/util/WrapUtil.class */
public class WrapUtil {
    public static Wrapper getKeyWrapper(KTSParameterSpec kTSParameterSpec, byte[] bArr) throws InvalidKeyException {
        Wrapper wrapper = getWrapper(kTSParameterSpec.getKeyAlgorithmName());
        if (kTSParameterSpec.getKdfAlgorithm() == null) {
            wrapper.init(true, new KeyParameter(Arrays.copyOfRange(bArr, 0, (kTSParameterSpec.getKeySize() + 7) / 8)));
        } else {
            wrapper.init(true, new KeyParameter(makeKeyBytes(kTSParameterSpec, bArr)));
        }
        return wrapper;
    }

    public static Wrapper getKeyUnwrapper(KTSParameterSpec kTSParameterSpec, byte[] bArr) throws InvalidKeyException {
        Wrapper wrapper = getWrapper(kTSParameterSpec.getKeyAlgorithmName());
        if (kTSParameterSpec.getKdfAlgorithm() == null) {
            wrapper.init(false, new KeyParameter(bArr, 0, (kTSParameterSpec.getKeySize() + 7) / 8));
        } else {
            wrapper.init(false, new KeyParameter(makeKeyBytes(kTSParameterSpec, bArr)));
        }
        return wrapper;
    }

    public static Wrapper getWrapper(String str) {
        Wrapper rFC3394WrapEngine;
        if (str.equalsIgnoreCase("AESWRAP") || str.equalsIgnoreCase("AES")) {
            rFC3394WrapEngine = new RFC3394WrapEngine(new AESEngine());
        } else if (str.equalsIgnoreCase("ARIA")) {
            rFC3394WrapEngine = new RFC3394WrapEngine(new ARIAEngine());
        } else if (str.equalsIgnoreCase("Camellia")) {
            rFC3394WrapEngine = new RFC3394WrapEngine(new CamelliaEngine());
        } else if (str.equalsIgnoreCase("SEED")) {
            rFC3394WrapEngine = new RFC3394WrapEngine(new SEEDEngine());
        } else if (str.equalsIgnoreCase("AES-KWP")) {
            rFC3394WrapEngine = new RFC5649WrapEngine(new AESEngine());
        } else if (str.equalsIgnoreCase("Camellia-KWP")) {
            rFC3394WrapEngine = new RFC5649WrapEngine(new CamelliaEngine());
        } else {
            if (!str.equalsIgnoreCase("ARIA-KWP")) {
                throw new UnsupportedOperationException("unknown key algorithm: " + str);
            }
            rFC3394WrapEngine = new RFC5649WrapEngine(new ARIAEngine());
        }
        return rFC3394WrapEngine;
    }

    private static byte[] makeKeyBytes(KTSParameterSpec kTSParameterSpec, byte[] bArr) throws InvalidKeyException {
        try {
            return KdfUtil.makeKeyBytes(kTSParameterSpec.getKdfAlgorithm(), bArr, kTSParameterSpec.getOtherInfo(), kTSParameterSpec.getKeySize());
        } catch (IllegalArgumentException e) {
            throw new InvalidKeyException(e.getMessage());
        }
    }
}
